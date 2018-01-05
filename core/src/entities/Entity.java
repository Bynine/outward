package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timers.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import pack.OutwardEngine;
import pack.GlobalRepo;
import pack.MapHandler;

public abstract class Entity {
	protected final Vector2 position = new Vector2();
	protected final Vector2 velocity = new Vector2();
	protected State state;
	protected Direction direction = Direction.RIGHT;
	protected Sprite image;
	protected Collision collision;
	protected Layer layer = Layer.GROUND;

	int jumpSquatFrames = 4;
	protected float jumpStrength = 3.1f;
	protected float gravity = -0.25f;
	protected float walkAcc = 0.42f;
	protected float airAcc = 0.13f;
	protected float jumpAcc = 0.142f;
	protected float friction = 0.8f;
	protected float airFrictionX = 0.95f;
	protected float airFrictionY = 0.97f;
	protected float mass = 1f;

	protected boolean toRemove = false, hasShadow = false;

	private final int collisionCheck = 4;
	private final float softening = .8f;
	protected final int displacement;
	protected Vector2 lastVelocity = new Vector2();
	protected final List<Rectangle> tempRectangleList = new ArrayList<Rectangle>();

	protected final Timer stunTimer = new Timer(10);
	protected final Timer flipTimer = new Timer(4);
	protected final List<Timer> timerList = new ArrayList<Timer>(Arrays.asList(stunTimer, flipTimer));

	public Entity(float posX, float posY){
		displacement = (int) (Math.random() * 8);
		position.x = posX;
		position.y = posY;
	}

	public void update(List<Rectangle> rectangleList, List<Entity> entityList){
		changeImage();
		updateState();
		handleDirection();
		handleMovement();
		if (OutwardEngine.getDeltaTime() > 1) handleTouch(entityList);
		limitingForces(rectangleList, entityList);
		updateTimers();
		updatePosition();
		updateImage();
		lastVelocity.set(velocity);
	}

	protected final ArrayList<State> groundedStates = new ArrayList<State>(
			Arrays.asList(State.CROUCH, State.LAND, State.STAND, State.WALK));
	protected boolean inGroundedState() {
		return groundedStates.contains(getState());
	}

	void updateState() {
		if (isGrounded()) {
			if (!inGroundedState()) ground();
			state = State.STAND;
		}
		else state = State.FALL;
	}

	void handleDirection(){
		/* */
	}

	void handleMovement(){
		/* */
	}

	protected void changeImage() {
		/* */
	}

	private void handleTouch(List<Entity> entityList){
		for (Entity e: entityList) if (e != this) handleTouchHelper(e);
	}

	void handleTouchHelper(Entity e){
		/* */
	}

	protected void updatePosition(){
		position.x += velocity.x;
		position.y += velocity.y;
	}

	protected void updateImage(){
		image.setX(position.x);
		image.setY(position.y);
	}

	protected void updateTimers(){
		for (Timer t: timerList) t.countUp();
	}

	protected void limitingForces(List<Rectangle> mapRectangleList, List<Entity> entityList){
		velocity.add(MapHandler.getActiveRoom().getWind());
		handleGravity();
		handleFriction();
		setupRectangles(mapRectangleList, entityList);
		checkWalls();
		checkFloor();
		final float lowerLimit = 0.01f;
		if (Math.abs(velocity.x) < lowerLimit) velocity.x = 0;
		if (Math.abs(velocity.y) < lowerLimit) velocity.y = 0;
	}

	final float diveFrictionReduction = 0.1f;

	void handleFriction(){
		if (state == State.DIVE) {
			if (!isGrounded()) velocity.x *= Math.pow(airFrictionX, diveFrictionReduction * 6);
			else velocity.x *= Math.pow(friction, diveFrictionReduction);
		}
		else{
			if (!isGrounded()) velocity.x *= airFrictionX;
			else velocity.x *= friction;
		}
	}

	void handleGravity(){
		if (state == State.CLIMB) return;
		if (state == State.WALL){
			velocity.y = -0.5f;
			return;
		}
		velocity.y += gravity;
		if (velocity.y < 0) velocity.y *= airFrictionY;
	}

	private void setupRectangles(List<Rectangle> mapRectangleList, List<Entity> entityList){
		tempRectangleList.clear();
		tempRectangleList.addAll(mapRectangleList);
		for (Entity en: entityList){
			if (en.getCollision() == Collision.SOLID) tempRectangleList.add(en.getCollisionBox(en.position.x, en.position.y));
		}
	}

	void checkWalls(){
		for (int i = 0; i < collisionCheck; ++i)
			if (doesCollide(position.x + velocity.x, position.y)) {
				hitWall();
				velocity.x *= softening;
			}
		if (doesCollide(position.x + velocity.x, position.y)) {
			velocity.x = 0;
		}
	}

	protected void hitWall(){
		/**/
	}

	void checkFloor(){
		for (int i = 0; i < collisionCheck; ++i)
			if (doesCollide(position.x, position.y + velocity.y)) {
				velocity.y *= softening;
			}
		if (doesCollide(position.x, position.y + velocity.y)) velocity.y = 0;
		if (doesCollide(position.x + velocity.x, position.y + velocity.y)) velocity.y = 0; // checks for diagonal floor
	}

	public boolean doesCollide(float x, float y){
		if (collision == Collision.GHOST) return false;
		for (Rectangle r : tempRectangleList){
			if (doesCollideHelper(x, y, r)) return true;
		}
		return false;
	}

	protected boolean doesCollideHelper(float x, float y, Rectangle r){
		boolean ignoreRectangle = false;
		Rectangle thisR = getCollisionBox(x, y);
		if (r.getHeight() <= 1 && r.getY() - this.getPosition().y > 0) ignoreRectangle = true; // for platforms you can go through the bottom of
		if (!ignoreRectangle && Intersector.overlaps(thisR, r) && thisR != r) {
			hitSurface();
			return true;
		}
		return false;
	}

	protected void hitSurface(){
		/* */
	}

	public Rectangle getCollisionBox(float x, float y){
		Rectangle r = image.getBoundingRectangle();
		r.setX(x); r.setY(y);
		return r;
	}

	public void flip(){
		flipTimer.reset();
		if (direction == Direction.LEFT){
			setDirection(Direction.RIGHT);
			image.setFlip(false, false);
		}
		else if (direction == Direction.RIGHT){
			setDirection(Direction.LEFT);
			image.setFlip(true, false);
		}
	}

	public int direct(){
		if (direction == Direction.RIGHT) return 1;
		else return -1;
	}

	public boolean isOOB(int mapWidth, int mapHeight) {
		int OOBGrace = 2;
		if (position.y < (0 - image.getHeight()*OOBGrace) || (mapHeight + image.getHeight()*OOBGrace) < position.y) return true;
		return false;
	}

	public boolean isTouching(Entity en, int decrement){
		Rectangle hitboxRect = en.getCollisionBox(en.getPosition().x, en.getPosition().y);
		hitboxRect.setWidth(hitboxRect.getWidth() - decrement);
		hitboxRect.setHeight(hitboxRect.getHeight() - decrement);
		hitboxRect.setX(hitboxRect.getX() + decrement/2);
		hitboxRect.setY(hitboxRect.getY() + decrement/2);
		return Intersector.overlaps(getCollisionBox(getPosition().x, getPosition().y), hitboxRect);
	}

	public void setPosition(Vector2 startPosition) {
		position.x = GlobalRepo.TILE * startPosition.x;
		position.y = GlobalRepo.TILE * startPosition.y;
		velocity.x = 0;
		velocity.y = 0;
	}

	protected void setAnimation(Animation<TextureRegion> ani, int deltaTime){
		image.setRegion(ani.getKeyFrame(deltaTime));
		if (direction == Direction.LEFT) image.flip(true, false);
	}

	protected void setImage(TextureRegion tr){
		boolean flipped = image.isFlipX();
		float x = image.getX();
		float y = image.getY();
		image = new Sprite(tr);
		image.setFlip(flipped, false);
		image.setX(x);
		image.setY(y);
	}

	private final float aboveGround = 1f;
	public boolean isGrounded(){ 
		return doesCollide(position.x, position.y - aboveGround); 
	}

	public void ground(){ 
		if (velocity.y < 0){
			hitGround();
		}
	}

	protected void hitGround(){
		stunTimer.end();
	}

	public Vector2 getCenter() {
		Vector2 center = new Vector2();
		center.x = position.x + image.getWidth()/2;
		center.y = position.y + image.getHeight()/2;
		return center;
	}

	protected Particle makeParticle(float x, float y, float direct){
		float pos_x = ((position.x + x) + (getCenter().x))/ 2;
		float pos_y = ((position.y + y) + (getCenter().y))/ 2;
		return new Particle(pos_x, pos_y, direct);
	}

	protected final void createImage(String url){
		image = new Sprite(new TextureRegion(new Texture(Gdx.files.internal(url))));
	}

	public boolean hasShadow() {
		return hasShadow;
	}

	public abstract void dispose();
	public void setRemove() { toRemove = true; }
	public boolean toRemove() { return toRemove; } 

	public Vector2 getPosition() { return position; }
	public Vector2 getVelocity() { return velocity; }
	public Direction getDirection() { return direction; }
	public void setDirection (Direction d) { direction = d; }
	public Collision getCollision() { return collision; }
	public Sprite getImage() { return image; }
	public Layer getLayer() {
		return layer;
	}
	public State getState() {
		return state;
	}

	public static enum Direction{ LEFT, RIGHT }
	public static enum State{ STAND, WALK, JUMP, LAND, FALL, CROUCH, CLIMB, DIVE, SPIN, WALL }
	public static enum Collision{ SOLID, CREATURE, GHOST }
	public static enum Layer{ GROUND, WALL }

}
