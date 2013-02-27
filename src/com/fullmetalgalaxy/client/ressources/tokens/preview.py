import Blender
from Blender import *
from Blender.Scene import Render

from Blender.Mathutils import *


g_colors = { 'brown':'metal', 'pink':'metal', 'blue':'metal', 'white':'metal', 'cyan':'metal', 'grey':'metal', 'olive':'metal', 'orange':'metal', 'purple':'metal', 'yellow':'metal', 'green':'metal', 'red':'metal', 'camouflage':'camouflage', 'lightning':'lightning', 'pantera':'pantera', 'zebra':'zebra' }
g_cameraTactic = 'tactic'
g_cameraStrategy = 'strategy'
g_noshadowtokens = ('turret')

pi = 3.14

def renderColor(p_context, p_texture, p_color, p_path):
	footex = Texture.Get('color')             # get texture named 'color'
	footex.setType('Image')                 # make foo be an image texture
	img = Image.Load('textures/'+p_color+'.jpg')            # load an image
	footex.image = img                      # link the image to the texture
	footex = Texture.Get('Tex')             # get texture named 'Tex'
	footex.setType('Image')                 # make foo be an image texture
	img = Image.Load('textures/'+p_texture+'.jpg')            # load an image
	footex.image = img                      # link the image to the texture
	p_context.renderPath = p_path % (p_color)
	p_context.render()

	
def renderAll(p_context):
	for color in g_colors:
		renderColor( p_context, g_colors[color], color, '//render/%s/'+g_name+'-' )


scn = Blender.Scene.GetCurrent()
context = scn.getRenderingContext()

#get the root directory that the current file is in
#we'll write the muray files there.  
path = Blender.Get('filename')
tokens = path.split('\\')
g_name = tokens.pop();
file = g_name.split('.')
g_name = file[0]
# this isn't working for me
#g_name = Blender.Get('filename').replace('.blend','')

context.extensions = True
context.imageType = Render.JPEG
context.enableRGBAColor() 
context.enablePremultiply() 
context.enableOversampling(1) 
context.OSALevel = 8
context.enableRayTracing(1)
context.enableShadow(1)


# destroy all lamps
for obj in Blender.Scene.GetCurrent().objects:
	if obj.type == 'Lamp':
		scn.objects.unlink(obj)

context.enableRayTracing(1)
context.enableShadow(1)
# create new lamp
lamp = Lamp.New('Sun','sun')
lamp.energy = 2.5
lamp.R = 1
lamp.G = 0.9
lamp.B = 0.7
#lamp.setMode('Square', 'Shadow')
#lamp.setMode('RayShadow','NoSpecular')
#lamp.mode &= ~Lamp.Modes["RayShadow"] # Disable RayShadow.
#lamp.mode |= Lamp.Modes["Shadows"]    # Enable Shadowbuffer shadows
lamp.mode |= Lamp.Modes["RayShadow"]
lamp.mode |= Lamp.Modes["NoSpecular"]
objLamp = Object.New('Lamp')
objLamp.link(lamp)
scn.objects.link(objLamp)
# objLamp.LocX = 50
# objLamp.LocY = -30
# objLamp.LocZ = 24
objLamp.RotX = 54*pi/180  #62*pi/180
objLamp.RotY = 40*pi/180  #44*pi/180
objLamp.RotZ = 42*pi/180

# create new lamp
lamp = Lamp.New('Hemi','moon')
lamp.energy = 0.8
lamp.R = 0.6
lamp.G = 0.7
lamp.B = 1
lamp.mode |= Lamp.Modes["RayShadow"]
lamp.mode |= Lamp.Modes["NoSpecular"]
objLamp = Object.New('Lamp')
objLamp.link(lamp)
scn.objects.link(objLamp)
objLamp.RotX = 62*pi/180
objLamp.RotY = 44*pi/180
objLamp.RotZ = 222*pi/180

# create new lamp spot for shadow 
lamp = Lamp.New('Spot','shadow')
lamp.energy = 10
lamp.R = 1
lamp.G = 1
lamp.B = 1
lamp.mode |= Lamp.Modes["RayShadow"]
lamp.mode |= Lamp.Modes["OnlyShadow"]
objLamp = Object.New('Lamp')
objLamp.link(lamp)
scn.objects.link(objLamp)
objLamp.RotX = 0
objLamp.RotY = 0
objLamp.RotZ = 222*pi/180

if( g_name not in g_noshadowtokens ):
	#create ground material
	mat = Material.New('ground')          # create a new Material called 'newMat'
	#mat.rgbCol = [0.8, 0.2, 0.2]          # change its color
	#mat.setAlpha(0.2)                     # mat.alpha = 0.2 -- almost transparent
	#mat.emit = 0.7                        # equivalent to mat.setEmit(0.8)
	#mat.mode |= Material.Modes.ZTRANSP    # turn on Z-Buffer transparency
	#mat.setAdd(0.8)                       # make it glow
	mat.setMode('OnlyShadow')  

	# create ground to receive shadow
	plane = Mesh.Primitives.Plane(200.0)   # create a newplane of size 200
	ob = Object.New('Mesh','Ground')          # create a new mesh-type object
	ob.link(plane)                      # link mesh datablock with object
	scn.link(ob)                      # add object to the scene
	plane.materials += [mat]



# rendu 
# =====
renderAll(context)
