package pack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

import entities.Actor;
import entities.Entity;
import entities.Player;
import entities.Entity.Layer;

public class GraphicsHandler {

	private static SpriteBatch batch;
	private static final OrthographicCamera 
	skyCam = new OrthographicCamera(), 
	backCam = new OrthographicCamera(),
	midCam = new OrthographicCamera(), 
	frontCam = new OrthographicCamera();
	private static final List<OrthographicCamera> 
	cams = new ArrayList<OrthographicCamera>(Arrays.asList(skyCam, backCam, midCam, frontCam)),
	scrollCams = new ArrayList<OrthographicCamera>(Arrays.asList(skyCam, backCam, frontCam));
	private static final int camAdjustmentSpeedX = 8, camAdjustmentSpeedY = 8;
	private static final float ZOOM = 1/2f;
	private static OrthogonalTiledMapRenderer renderer;
	private static final float screenAdjust = 2f;
	private static ShaderProgram shader_flash, shader_darken;
	private static TextureRegion 
	textbox = new TextureRegion(new Texture(Gdx.files.internal("sprites/gui/textbox.png")));
	private static BitmapFont font = new BitmapFont();
	private static final ShapeRenderer debugRenderer = new ShapeRenderer();

	public static final int SCREENWIDTH  = (int) ((30 * GlobalRepo.TILE)/ZOOM);
	public static final int SCREENHEIGHT = (int) ((16 * GlobalRepo.TILE)/ZOOM);

	public static void begin() {
		batch = new SpriteBatch();
		for(OrthographicCamera cam : cams){
			cam.setToOrtho(false, SCREENWIDTH, SCREENHEIGHT);
			cam.zoom = ZOOM;
		}
		frontCam.zoom /= 2;

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/nes.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.color = Color.WHITE;
		parameter.size = 8;
		parameter.spaceY = 2;
		font = generator.generateFont(parameter);
		generator.dispose();

		ShaderProgram.pedantic = false;
		shader_flash = new ShaderProgram(Gdx.files.internal("shaders/vert.glsl"), Gdx.files.internal("shaders/flash.glsl"));
		if (!shader_flash.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader_flash.getLog());
		shader_darken = new ShaderProgram(Gdx.files.internal("shaders/vert.glsl"), Gdx.files.internal("shaders/darken.glsl"));
		if (!shader_darken.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader_darken.getLog());
	}

	static void updateCamera(){
		Player player = OutwardEngine.getPlayer();
		//int disp = (player.direct() * GlobalRepo.TILE * 1);
		midCam.position.x = (midCam.position.x*(camAdjustmentSpeedX-1) + player.getCenter().x)/camAdjustmentSpeedX;
		midCam.position.y = (midCam.position.y*(camAdjustmentSpeedY-1) + player.getCenter().y)/camAdjustmentSpeedY;

		for(OrthographicCamera cam : scrollCams){
			cam.position.y = midCam.position.y;
		}

		backCam.position.x = MapHandler.mapWidth/2;
		skyCam.position.x = midCam.position.x/4 - MapHandler.mapWidth;
		frontCam.position.x = midCam.position.x * 2 - MapHandler.mapWidth/3;
		for(OrthographicCamera cam : cams){
			cam.position.x = (int) cam.position.x;
			float minClamp = Math.max(screenBoundary(SCREENHEIGHT), GlobalRepo.TILE * 2);
			cam.position.y = (int) MathUtils.clamp(cam.position.y, minClamp, MapHandler.mapHeight - screenBoundary(SCREENHEIGHT));
			cam.update();
		}
	}

	private static float screenBoundary(float dimension){
		return dimension/(screenAdjust/ZOOM);
	}

	static float[] camDimensions(){
		return new float[]{
				midCam.position.x - SCREENWIDTH/(2/ZOOM), midCam.position.x + SCREENWIDTH/(2/ZOOM), 
				midCam.position.y - SCREENHEIGHT/(2/ZOOM), midCam.position.y + SCREENHEIGHT/(2/ZOOM)};
	}

	public static boolean inShot(Entity en, int mod){
		Vector2 pos = en.getPosition();
		float width = en.getCollisionBox(pos.x, pos.y).width + mod;
		float height = en.getCollisionBox(pos.x, pos.y).height + mod;
		boolean inShotX = pos.x + width > GraphicsHandler.camDimensions()[0] && pos.x - width < GraphicsHandler.camDimensions()[1];
		boolean inShotY = pos.y + height > GraphicsHandler.camDimensions()[2] && pos.y - height < GraphicsHandler.camDimensions()[3];
		return inShotX && inShotY;
	}

	static void updateGraphics(){
		Color skyColor = MapHandler.activeRoom.getSkyColor();
		Gdx.gl.glClearColor(skyColor.r, skyColor.g, skyColor.b, skyColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.setProjectionMatrix(midCam.combined);
		batch.setShader(getShader());
		renderer.getBatch().setShader(getShader());

		drawWeather(skyCam, 512, MapHandler.activeRoom.getAtmosphere());

		render(new int[]{0}, skyCam);  // far parallax

		render(new int[]{1}, backCam);  // near parallax

		List<Entity> wallList = new ArrayList<Entity>();
		List<Entity> groundList = new ArrayList<Entity>();
		for (Entity en: MapHandler.activeRoom.getEntityList()){
			switch(en.getLayer()){
			case WALL: wallList.add(en); break;
			case GROUND: groundList.add(en); break;
			}
		}

		render(new int[]{2}, midCam);  // wall

		batch.begin();
		for (Entity en: sortEntities(wallList)) drawEntity(en);
		batch.end();

		render(new int[]{3}, midCam);  // ground

		batch.begin();
		Player player = OutwardEngine.getPlayer();
		if (player.isInteract()){
			TextureRegion icon = player.getInteractor().getIcon();
			if (icon.isFlipX() ^ !player.getImage().isFlipX()) icon.flip(true, false);
			batch.draw(icon, player.getPosition().x, player.getPosition().y + player.getImage().getHeight());
		}
		batch.end();
		
		batch.begin();
		for (Entity en: sortEntities(groundList)) drawEntity(en);
		batch.end();

		render(new int[]{4}, midCam); // front
		drawGUI();
		if (OutwardEngine.debug) debugRender();
		Gdx.gl.glViewport(0, 0, SCREENWIDTH, SCREENHEIGHT);
	}

	private static ShaderProgram getShader(){
		return null;
	}
	
	private static void render(int[] arr, OrthographicCamera cam){
		renderer.setView(cam);
		renderer.render(arr);
		Matrix4 render = new Matrix4();
		render.set(cam.combined);
		render = render.translate(-MapHandler.mapWidth, 0, 0);
		renderer.setView(render, 0, 0, MapHandler.mapWidth, MapHandler.mapHeight);
		renderer.render(arr);
		render = render.translate(MapHandler.mapWidth * 2, 0, 0);
		renderer.setView(render, 0, 0, MapHandler.mapWidth, MapHandler.mapHeight);
		renderer.render(arr);
	}

	private static void debugRender(){
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		debugRenderer.setProjectionMatrix(midCam.combined);
		debugRenderer.begin(ShapeType.Line);
		for (Entity en: MapHandler.activeRoom.getEntityList()){
			Rectangle collisionBox = en.getCollisionBox(en.getPosition().x, en.getPosition().y);
			debugRenderer.setColor(Color.GREEN);
			debugRenderer.rect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
		}
		debugRenderer.end();
	}

	private static class ZComparator implements Comparator<Entity>{
		public int compare(Entity o1, Entity o2) {
			int z = 0;
			int pos1 = getLayerNum(o1.getLayer());
			int pos2 = getLayerNum(o2.getLayer());
			if (pos1 != pos2) z = pos1 - pos2;
			else z = (int) (o1.getPosition().y - o2.getPosition().y);
			return z;
		}
	}

	private static int getLayerNum(Layer layer){
		switch (layer){
		case GROUND: return -1;
		default: return 0;
		}
	}

	private static List<Entity> sortEntities(List<Entity> entityList){
		entityList.sort(new ZComparator());
		return entityList;
	}

	private static void drawEntity(Entity en){
		if (en instanceof Actor){
			if (((Actor)en).flashing()) batch.setShader(shader_flash);
		}
		batch.draw(en.getImage(), en.getPosition().x, en.getPosition().y);
		batch.setShader(getShader());
	}

	private static void drawWeather(OrthographicCamera cam, float tiling, Texture tex){
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		final float speed_mod = 1.0f/4.0f;
		final float pos_x = -(OutwardEngine.getDeltaTime() * speed_mod) % tiling;
		final int tiles = (int) ((MapHandler.mapWidth/tex.getWidth()) / speed_mod) + 1;
		for (int i = 0; i < tiles; ++i){
			batch.draw(tex, pos_x + (i * tiling), 0);
		}
		batch.end();
		batch.setProjectionMatrix(midCam.combined);
	}

	public static void updateRoomGraphics() {
		renderer = new OrthogonalTiledMapRenderer(MapHandler.activeMap, 1);
		midCam.position.x = OutwardEngine.getPlayer().getPosition().x;
		midCam.position.y = OutwardEngine.getPlayer().getPosition().y;
		updateCamera();
	}

	public static void drawDialogue() {
		Dialogue d = OutwardEngine.getDialogue();
		final float pos_x = midCam.position.x - textbox.getRegionWidth()/2;
		final float pos_y = midCam.position.y;
		batch.begin();
		batch.draw(textbox, pos_x, pos_y);
		font.draw(batch, d.getDisplayedString(), pos_x + 10, pos_y + textbox.getRegionHeight() - 6);
		batch.end();
	}

	private static void drawGUI(){
		batch.begin();
		if (OutwardEngine.isPaused()){
			font.draw(batch, "PAUSED", midCam.position.x - 24, midCam.position.y);
		}
		batch.end();
	}

	static void setCamera(Vector2 position) {
		midCam.position.x = position.x * GlobalRepo.TILE;
		midCam.position.y = position.y * GlobalRepo.TILE;
	}

	public static void snapCamera(float inc) {
		midCam.position.x += inc;
	}

}
