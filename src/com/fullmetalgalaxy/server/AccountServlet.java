/**
 * 
 */
package com.fullmetalgalaxy.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.fullmetalgalaxy.model.AuthProvider;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;
import com.fullmetalgalaxy.server.datastore.PersistAccount;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author Vincent
 *
 */
public class AccountServlet extends HttpServlet
{
  private static final long serialVersionUID = -4916146982326069190L;
  private final static FmpLogger log = FmpLogger.getLogger( AccountServlet.class.getName() );



  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    if( p_request.getParameter( "logout" ) != null )
    {
      if( Auth.isUserLogged( p_request, p_response ) )
      {
        Auth.disconnectFmgUser( p_request );
      }
      String continueUrl = p_request.getParameter( "continue" );
      if( continueUrl == null )
      {
        continueUrl = "/";
      }
      String redirectUrl = UserServiceFactory.getUserService().createLogoutURL( continueUrl );
      p_response.sendRedirect( redirectUrl );
    }
    else
    {
      p_response.sendRedirect( "/" );
    }
  }


  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest p_request, HttpServletResponse p_response)
      throws ServletException, IOException
  {
    ServletFileUpload upload = new ServletFileUpload();
    Map<String, String> params = new HashMap<String, String>();
    boolean isConnexion = false;

    try
    {
      // Parse the request
      FileItemIterator iter = upload.getItemIterator( p_request );
      while( iter.hasNext() )
      {
        FileItemStream item = iter.next();
        if( item.isFormField() )
        {
          if( item.getFieldName().equalsIgnoreCase( "connexion" ) )
          {
            isConnexion = true;
          }
          params.put( item.getFieldName(), Streams.asString( item.openStream() ) );
        }
      }
    } catch( FileUploadException e )
    {
      log.error( e );
    }

    if( isConnexion )
    {
      if( !Auth.isUserLogged( p_request, p_response ) )
      {
        connectUser( p_request, p_response, params );
      }

    }
    else
    {
      String msg = checkParams( params );
      if( msg != null )
      {
        p_response.sendRedirect( "/account.jsp?msg=" + msg );
      }
      msg = saveAccount( p_request, p_response, params );
      if( msg != null )
      {
        p_response.sendRedirect( "/account.jsp?msg=" + msg );
      }
      else
      {
        if( !Auth.isUserLogged( p_request, p_response ) )
        {
          Auth.connectUser( p_request, params.get( "login" ) );
        }
        if( "0".equalsIgnoreCase( params.get( "accountid" ) ) )
        {
          p_response.sendRedirect( "/gamelist.jsp" );
        }
        else
        {
          p_response.sendRedirect( "/profile.jsp?id=" + params.get( "accountid" ) );
        }
      }
    }


  }


  private void connectUser(HttpServletRequest p_request, HttpServletResponse p_response,
      Map<String, String> params) throws IOException
  {
    String login = params.get( "login" );
    if( login == null || login.isEmpty() )
    {
      p_response.sendRedirect( "/auth.jsp" );
      return;
    }
    PersistAccount account = FmgDataStore.getPersistAccount( login );
    if( account == null )
    {
      p_response.sendRedirect( "/auth.jsp" );
      return;
    }
    if( account.getAuthProvider() != AuthProvider.Fmg )
    {
      p_response.sendRedirect( Auth.getGoogleLoginURL( p_request, p_response ) );
      return;
    }
    String password = params.get( "password" );
    if( password == null )
    {
      p_response.sendRedirect( "/auth.jsp" );
      return;
    }
    if( account != null && account.getPassword() != null && account.getPassword().equals( password ) )
    {
      Auth.connectUser( p_request, login );
      String continueUrl = params.get( "continue" );
      if( continueUrl == null )
      {
        continueUrl = "/";
      }
      p_response.sendRedirect( continueUrl );
      return;
    }
    p_response.sendRedirect( "/auth.jsp" );
  }


  /**
   * 
   * @param params
   * @return null if all ok, an error message otherwise
   */
  private String checkParams(Map<String, String> params)
  {
    if( params.get( "authprovider" ).equalsIgnoreCase( "Fmg" ) )
    {
      String pass1 = params.get( "password1" );
      String pass2 = params.get( "password2" );
      if( pass1 == null || pass2 == null || !pass1.equals( pass2 ) )
      {
        return "vous devez tapper le meme mot de passe";
      }
      if( !pass1.isEmpty() )
      {
        params.put( "password", pass1 );
      }
    }
    if( params.get( "accountid" ) == null )
      return "pas de champs accountid";
    if( params.get( "login" ) == null || params.get( "login" ).length() < 4 )
      return "votre login doit faire plus de 3 caracteres";
    if( params.get( "pseudo" ) != null && params.get( "pseudo" ).length() < 4 )
      return "votre pseudo doit faire plus de 3 caracteres";
    return null;
  }

  /**
   * 
   * @param params
   * @return null if saved successfully, an error message otherwise
   */
  private String saveAccount(HttpServletRequest p_request, HttpServletResponse p_response,
      Map<String, String> params)
  {
    String strid = params.get( "accountid" );
    assert strid != null;
    long id = Long.parseLong( strid );
    FmgDataStore store = new FmgDataStore();
    PersistAccount pAccount = null;
    EbAccount account = null;
    if( id == 0 )
    {
      // we are creating a new account
      assert pAccount == null;
      account = new EbAccount();
      // lets check that login ins't took already
      if( FmgDataStore.isLoginExist( params.get( "login" ) )
          || FmgDataStore.isPseudoExist( params.get( "login" ) ) )
      {
        store.rollback();
        return "Ce login existe deja";
      }
    }
    else
    {
      if( id != Auth.getUserAccount( p_request, p_response ).getId()
          && !Auth.isUserAdmin( p_request, p_response ) )
      {
        store.rollback();
        return "Vous n'avez pas le droit de faire ces modifs";
      }
      // just update an account
      pAccount = store.getPersistAccount( id );
      account = pAccount.getAccount();
      if( params.get( "pseudo" ) != null
          && (account.getPseudo() == null || !account.getPseudo().equalsIgnoreCase(
              params.get( "pseudo" ) )) )
      {
        // lets check that pseudo ins't took already
        if( FmgDataStore.isPseudoExist( params.get( "pseudo" ) ) )
        {
          store.rollback();
          return "Ce pseudo existe deja";
        }
        // check that user is allowed to change his pseudo
        if( !account.canChangePseudo() && !Auth.isUserAdmin( p_request, p_response ) )
        {
          store.rollback();
          return "Vous ne pouvez pas modifier votre pseudo";
        }
        account.setPseudo( params.get( "pseudo" ) );
      }
    }

    account.setEmail( params.get( "email" ) );
    account.setDescription( params.get( "description" ) );
    account.setAllowMailFromGame( params.get( "AllowMailFromGame" ) != null );
    account.setAllowMailFromNewsLetter( params.get( "AllowMailFromNewsLetter" ) != null );
    account.setAllowPrivateMsg( params.get( "AllowPrivateMsg" ) != null );
    if( pAccount == null )
    {
      account.setLogin( params.get( "login" ) );
      account.setAuthProvider( AuthProvider.Fmg );
      store.save( account );
      pAccount = store.getPersistAccount( account.getId() );
    }
    if( params.get( "password" ) != null )
    {
      pAccount.setPassword( params.get( "password" ) );
    }
    if( pAccount.getAuthProvider() == AuthProvider.Fmg && pAccount.getPassword().isEmpty() )
    {
      store.rollback();
      return "Vous devez definir un mot de passe";
    }
    pAccount.setEb( account );
    store.close();
    // to reload account data from datastore
    p_request.getSession().setAttribute( "account", null );
    return null;
  }

}
