package pack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class ThreeDimensionalGraphicsHandler {

	private static PerspectiveCamera pCam;
	private static Model model;
	private static ModelInstance instance;
	private static Environment environment;
	private static Shader shader;
	private static Renderable renderable;
	private static ModelBatch modelBatch;
	private static Texture texture = new Texture(Gdx.files.internal("sprites/entities/misc/coin.png"));

	static void begin() {
		pCam = new PerspectiveCamera(90, GraphicsHandler.SCREENWIDTH, GraphicsHandler.SCREENHEIGHT);
		pCam.position.set(0f, 0f, 5f);
		pCam.lookAt(0,0,0);
		pCam.near = 1f;
		pCam.far = 300f;
		pCam.update();
		modelBatch = new ModelBatch();
		ModelBuilder modelBuilder = new ModelBuilder();

		final float dim = 2.0f;
		Material material = new Material();
		model = modelBuilder.createBox(dim, dim, dim, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		
		material.set(TextureAttribute.createDiffuse(texture));
		NodePart blockPart = model.nodes.get(0).parts.get(0);
		instance = new ModelInstance(model);
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.2f, 0.2f, 1.0f));
		environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -0.2f, -1.0f, -0.4f));

//		sp = new ShaderProgram(Gdx.files.internal("shaders/vert.glsl"), Gdx.files.internal("shaders/default.glsl"));
//		float vertices[] = {
//				-1.0f, -1.0f, -1.0f, 0.0f, 0.0f, // A
//				1.0f, -1.0f, -1.0f, 1.0f, 0.0f,  // B
//				1.0f, 1.0f, -1.0f, 1.0f, 1.0f,   // C
//				-1.0f, 1.0f, -1.0f, 0.0f, 1.0f,  // D
//				-1.0f, -1.0f, 1.0f, 0.0f, 0.0f,  // E
//				1.0f, -1.0f, 1.0f, 1.0f, 0.0f,   // F
//				1.0f, 1.0f, 1.0f, 1.0f, 1.0f,    // G
//				-1.0f, 1.0f, 1.0f, 0.0f, 1.0f    // H
//		};
//		short indices[] = {
//
//				0, 4, 5,    0, 5, 1,
//				1, 5, 6,    1, 6, 2,
//				2, 6, 7,    2, 7, 3,
//				3, 7, 4,    3, 4, 0,
//				4, 7, 6,    4, 6, 5,
//				3, 0, 1,    3, 1, 2
//		};
//		mesh = new Mesh(true, 36, indices.length, 
//				new VertexAttribute(Usage.Position, 3, "a_position"),
//				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
//
//		mesh.setVertices(vertices);
//		mesh.setIndices(indices);


		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = environment;
		renderable.worldTransform.idt();

		shader = new TestShader();
		shader.init();
	}

	private static float posX = 0, posY = 0, posZ = 0;
	static void update(){
		pCam.update();
				modelBatch.begin(pCam);
				modelBatch.setCamera(pCam);
				instance.transform.setFromEulerAngles(OutwardEngine.getDeltaTime() % 360, 0, 90).trn(posX, posY, posZ);
				texture.bind();
				modelBatch.render(instance, environment, shader);
				modelBatch.end();
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		        Gdx.graphics.getGL20().glFrontFace(GL20.GL_CW);
	}
}
