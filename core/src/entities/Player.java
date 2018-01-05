package entities;

import java.util.Arrays;
import java.util.List;

import timers.Timer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import effects.DustCloud;
import pack.OutwardEngine;
import pack.GlobalRepo;
import pack.MapHandler;

public class Player extends Actor{

	public static final int commandNone = -1;
	public static final int commandJump = 0;
	public static final int commandFire = 1;

	protected final float noZone = 0.2f;
	protected static Animation<TextureRegion> idle = 	GlobalRepo.makeAnimation("sprites/entities/player/idle.png", 2, 1, 25, PlayMode.LOOP);
	protected static Animation<TextureRegion> move = 	GlobalRepo.makeAnimation("sprites/entities/player/walk.png", 6, 1, 10, PlayMode.LOOP);
	protected static Animation<TextureRegion> jump = 	GlobalRepo.makeAnimation("sprites/entities/player/jump.png", 1, 1, 20, PlayMode.LOOP);
	protected static Animation<TextureRegion> roll = 	GlobalRepo.makeAnimation("sprites/entities/player/roll.png", 2, 1, 5, PlayMode.LOOP);
	protected static Animation<TextureRegion> slide = 	GlobalRepo.makeAnimation("sprites/entities/player/slide.png", 2, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> rolljump =GlobalRepo.makeAnimation("sprites/entities/player/rolljump.png", 2, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> dive	   =GlobalRepo.makeAnimation("sprites/entities/player/dive.png", 1, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> fall = 	GlobalRepo.makeAnimation("sprites/entities/player/fall.png", 1, 1, 20, PlayMode.LOOP);
	protected static Animation<TextureRegion> land = 	GlobalRepo.makeAnimation("sprites/entities/player/land.png", 1, 1, 20, PlayMode.LOOP);
	protected static Animation<TextureRegion> getup = 	GlobalRepo.makeAnimation("sprites/entities/player/getup.png", 2, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> crouch = 	GlobalRepo.makeAnimation("sprites/entities/player/crouch.png", 2, 1, 10, PlayMode.NORMAL);
	protected static Animation<TextureRegion> hit = 	GlobalRepo.makeAnimation("sprites/entities/player/hit.png", 2, 1, 10, PlayMode.NORMAL);
	protected static Animation<TextureRegion> climb = 	GlobalRepo.makeAnimation("sprites/entities/player/climb.png", 1, 1, 10, PlayMode.LOOP);
	protected static Animation<TextureRegion> latch = 	GlobalRepo.makeAnimation("sprites/entities/player/latch.png", 1, 1, 10, PlayMode.NORMAL);
	protected static Animation<TextureRegion> skid = 	GlobalRepo.makeAnimation("sprites/entities/player/skid.png", 1, 1, 10, PlayMode.NORMAL);
	protected static Animation<TextureRegion> flip = 	GlobalRepo.makeAnimation("sprites/entities/player/flip.png", 4, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> snap = 	GlobalRepo.makeAnimation("sprites/entities/player/rolljump.png", 2, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> wall = 	GlobalRepo.makeAnimation("sprites/entities/player/wall.png", 1, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> divejump =GlobalRepo.makeAnimation("sprites/entities/player/dive_jump.png", 4, 1, 5, PlayMode.NORMAL);
	protected static Animation<TextureRegion> fallen   =GlobalRepo.makeAnimation("sprites/entities/player/fallen.png", 1, 1, 5, PlayMode.NORMAL);

	private int queuedCommand = commandNone;
	protected final int landLag = 0;
	protected final Timer queueTimer = new Timer(12);
	protected final Timer landTimer = new Timer(landLag);
	protected final Timer crouchTimer = new Timer(0);
	protected final Timer getupTimer = new Timer(12);
	protected final Timer shadeTimer = new Timer(30);
	protected final Timer jumpSquatTimer = new Timer(5);
	protected final Timer jumpTimer = new Timer(8);
	protected final Timer invincibleTimer = new Timer(30);
	protected final Timer latchTimer = new Timer(10);
	protected final Timer skidTimer = new Timer(15);
	protected final Timer flipTimer = new Timer(20);
	protected final Timer snapTimer = new Timer(5);
	protected final Timer diveTimer = new Timer(10);
	protected final Timer diveJumpTimer = new Timer(20);
	protected final Timer fallenTimer = new Timer(120);
	protected boolean fireHeld = false, isInteract = false;
	protected final Rectangle hitbox = new Rectangle(0, 0, 16, 16);
	protected boolean isSnap = false;
	protected Interactable interactor = null;
	private State prevState = State.STAND;
	float prevStickX = 0, stickX = 0, prevStickY = 0, stickY = 0;

	public Player(float posX, float posY) {
		super(posX, posY);
		timerList.addAll(Arrays.asList(queueTimer, landTimer, crouchTimer, getupTimer, shadeTimer, 
				jumpSquatTimer, jumpTimer, invincibleTimer, latchTimer, skidTimer, flipTimer, snapTimer, diveTimer,
				diveJumpTimer, fallenTimer));
		image = new Sprite(idle.getKeyFrame(0));
		state = State.STAND;
	}

	@Override
	public void update(List<Rectangle> rectangleList, List<Entity> entityList){
		stickX = OutwardEngine.getInputHandler().getXInput();
		stickY = OutwardEngine.getInputHandler().getYInput();
		prevState = state;
		isInteract = false;
		super.update(rectangleList, entityList);

		if (isGrounded() && stunned()){
			stunTimer.end();
			fallenTimer.reset();
		}
		if (fallen() && fallenTimer.getCounter() >= 10 && Math.abs(stickX) > 0.5f){
			fallenTimer.end();
			state = State.WALK;
		}
		if (willSnapJump()) snapJump();
		final boolean pullBack = isGrounded() && state == State.DIVE && -direct() == Math.signum(stickX);
		if (pullBack){
			state = State.WALK;
			if (Math.abs(velocity.x) > 0.8) {
				MapHandler.addEntity(new DustCloud(this));
				skidTimer.reset();
			}
		}
		if (stickY < -0.5f && isInteract()) {
			interactor.interact();
		}
		if (queueTimer.timeUp()) queuedCommand = commandNone;
		if (!queueTimer.timeUp()) handlePress(queuedCommand);
		hitbox.setPosition(position.x + (direct() * 6), position.y);

		prevStickX = stickX;
		prevStickY = stickY;
	}

	@Override
	void updateState(){
		//System.out.println("State: " + getState());
		final boolean hold_down = stickY > 0.5f;
		final boolean hold_up = stickY < -0.5f;
		boolean isClimb = canClimb() && (hold_up || state == State.CLIMB) && !(isGrounded() && hold_down);
		if (state == State.DIVE) {
			state = State.DIVE;
		}
		else if (!canClimb() && state == State.CLIMB && hold_up){
			velocity.y = 0;
			jumpHelper(2);
		}
		else if (isClimb) {
			if (state != State.CLIMB) latch();
			state = State.CLIMB;
		}
		else if (isGrounded() && velocity.y <= 0) updateGroundedState(hold_down);
		else updateAerialState();
	}

	private void updateGroundedState(boolean hold_down){
		if (!inGroundedState()) {
			ground();
		}
		if (hold_down) {
			if (state != State.CROUCH) crouchTimer.reset();
			state = State.CROUCH;		
		}
		else if (Math.abs(stickX) > 0.5f){
			state = State.WALK;
		}
		else {
			if (prevState == State.CROUCH) getupTimer.reset();
			state = State.STAND;
		}
	}

	private void updateAerialState(){
		if (state == State.SPIN) {
			state = State.SPIN;
		}
		else if (state == State.JUMP && !jumpTimer.timeUp()){
			state = State.JUMP;
		}
		else {
			state = State.FALL;
		}
	}

	private boolean willSnapJump(){
		final int inc = direct() * 4;
		final boolean holdTowardWall = Math.abs(stickX) > 0.5;
		final boolean spring = canMove() && state != State.DIVE && holdTowardWall;
		final boolean forward = doesCollide(position.x + inc, position.y + 4) && !doesCollide(position.x + inc, position.y + 16);
		final boolean back = doesCollide(position.x - inc, position.y + 4) && !doesCollide(position.x - inc, position.y + 16);
		if (back) flip();
		return (forward || back) && spring;
	}

	private boolean canClimb(){
		if (!canMove() || (state != State.CLIMB && velocity.y > 0)) return false;
		boolean canClimb = false;
		for (Rectangle r: MapHandler.getClimbableRectangles()){
			if (getCollisionBox(position.x, position.y).overlaps(r)){
				canClimb = true;
			}
		}
		return canClimb;
	}

	@Override
	void handleDirection(){
		if (!canTurn()) return;
		if ((stickX < 0 && getDirection() == Direction.RIGHT) || (stickX > 0 && getDirection() == Direction.LEFT)) flip();
	}

	@Override
	protected boolean canTurn(){
		return Math.abs(stickX) > noZone && super.canTurn() && skidTimer.timeUp() && !fallen();
	}

	protected boolean canMove(){
		return super.canMove() && skidTimer.timeUp() && !fallen();
	}

	@Override
	void handleMovement(){
		if (!snapTimer.timeUp()) velocity.x = direct() * 0.5f;
		if (!canMove()) return;
		else{
			switch (getState()){
			case WALK: addSpeed(walkAcc); break;
			case JUMP: velocity.y += jumpAcc; break;
			case CLIMB: handleClimb(); break;
			default: break;
			}
			if (!isGrounded() && Math.abs(stickX) > noZone) addSpeed(airAcc);
		}
	}

	private void handleClimb(){
		if (latching()) {
			velocity.setZero();
			return;
		}
		final float speedMod = 1.25f;
		final float minSpeed = 0.2f;
		float speed_x = stickX * speedMod;
		float speed_y = -stickY * speedMod;
		if (Math.abs(stickX) < minSpeed) speed_x = 0;
		if (Math.abs(stickY) < minSpeed) speed_y = 0;
		velocity.set(speed_x, speed_y);
	}

	protected void addSpeed(float acc){
		float additive = Math.signum(stickX) * acc;
		if (Math.signum(stickX) == -direct()){
			if (state == State.DIVE || !diveJumpTimer.timeUp()) additive += direct()/2;
		}

		velocity.x += additive;
	}

	@Override
	void handleTouchHelper(Entity e){
		super.handleTouchHelper(e);
		if (isTouching(e, 0) && e instanceof Portal){
			Portal p = (Portal) e;
			OutwardEngine.changeRoom(p.getDestination());
		}
		if (e instanceof Interactable && atTalkingDistance((Interactable)e)){
			if (canInteract()) {
				isInteract = true;
				interactor = (Interactable)e;
			}
		} 
	}

	private boolean atTalkingDistance(Interactable inter) {
		Rectangle talkBox = getCollisionBox(getPosition().x, getPosition().y);
		int reduct = 16;
		talkBox.x += reduct/2;
		talkBox.width -= reduct;
		return Intersector.overlaps(talkBox, inter.getInteractionBox(inter.getPosition().x, inter.getPosition().y));
	}

	public void handlePress(int command){
		boolean wasCommandAccepted;

		switch (command){
		case commandJump: wasCommandAccepted = tryJump(); break;
		case commandFire: wasCommandAccepted = tryFire(); break;
		default: wasCommandAccepted = true; break;
		}

		if (!wasCommandAccepted && queueTimer.timeUp()) {
			queuedCommand = command;
			queueTimer.reset();
		}
		else if (wasCommandAccepted) queuedCommand = commandNone;
	}

	protected boolean tryJump(){
		if (skidTimer.getCounter() <= 5) return false;
		if (!skidTimer.timeUp()){
			flipJump();
			return true;
		}
		if (state == State.DIVE && !isGrounded()){
			if (!diveTimer.timeUp()) {
				return false;
			}
			else{
				diveCancel();
				return true;
			}
		}
		if (fallen() && fallenTimer.getCounter() >= 5){
			fallenTimer.end();
			diveCancel();
			return true;
		}
		else if (canJump()) {
			jump();
			return true;
		}
		else{
			return false;
		}
	}
	
	private void flipJump(){
		jumpHelper(jumpStrength * 1.3f);
		state = State.JUMP;
		flip();
		velocity.x = 3 * direct();
		flipTimer.reset();
	}
	
	protected void diveCancel(){
		jumpHelper(jumpStrength * 0.92f);
		skidTimer.end();
		state = State.SPIN;
		if (Math.signum(stickX) == -direct()) {
			flip();
			velocity.x = 1 * direct();
		}
		else{
			velocity.x /= 2;
		}
	}
	
	private void jump(){
		if (state == State.DIVE) diveJumpTimer.reset();
		jumpHelper(jumpStrength);
		state = State.JUMP;
	}

	private void snapJump(){
		state = State.JUMP;
		isSnap = true;
		snapTimer.reset();
		velocity.y = 2.5f;
	}
	
	protected void dive(){
		if (Math.signum(stickX) == -direct()) flip();
		state = State.DIVE;
		
		if (isGrounded()) {
			velocity.x = 4.8f * direct();
			velocity.y = 1.8f;
		}
		else {
			velocity.x = 4.2f * direct();
			velocity.y = 0.8f;
		}
		diveTimer.reset();
	}

	protected boolean canJump(){
		return !landed() && isGrounded() && !fallen();
	}

	public void handleJumpHeld(boolean button) {
		if (getState() == State.JUMP && button) {
			velocity.y += jumpAcc;
		}
	}

	public void handleFireHeld(boolean button) {
		fireHeld = button;
	}

	protected void jumpHelper(float strength){
		if (velocity.y < 0) {
			velocity.y = 0;
		}
		velocity.y += strength;
		jumpTimer.reset();
	}

	@Override
	protected void hitGround(){
		super.hitGround();
		landTimer.reset();
		isSnap = false;
		if (state == State.DIVE) velocity.x += (Math.abs(0.75f + velocity.y) * direct());
	}

	protected void latch(){
		latchTimer.reset();
	}

	protected boolean latching(){
		return !latchTimer.timeUp();
	}

	@Override
	public void dispose() {
		/* */
	}

	@Override
	public Rectangle getCollisionBox(float x, float y){
		Rectangle r = image.getBoundingRectangle();
		int width = 12;
		int height = 28;
		if (state == State.DIVE || state == State.SPIN) height = 18;
		r.setX(x + (r.getWidth() - width)/2); 
		r.setY(y);
		r.setWidth(width);
		r.setHeight(height);
		return r;
	}

	protected boolean landed(){
		return !landTimer.timeUp();
	}

	public boolean canInteract() {
		return canMove() && isGrounded();
	}

	public boolean isInteract(){
		return isInteract;
	}

	public void reset() {
		toRemove = false;
		stop();
	}
	
	protected boolean fallen(){
		return isGrounded() && !fallenTimer.timeUp();
	}

	public Interactable getInteractor(){
		return interactor;
	}

	public void stop() {
		queuedCommand = commandNone;
		velocity.setZero();
		for (Timer t: timerList){
			t.end();
		}
	}
	
	protected boolean tryFire(){
		if (!canMove()) {
			return false;
		}
		else{
			fire();
			return true;
		}
	}

	protected void fire() {
		if (state == State.DIVE) {
			diveCancel();
			return;
		}
		if (state == State.SPIN) {
			return;
		}
		dive();
	}

	@Override
	protected void changeImageHelper() {
		switch(state){
		case WALK: setImage(move.getKeyFrame(landTimer.getCounter())); break;
		case LAND: setImage(land.getKeyFrame(landTimer.getCounter())); break;
		case FALL:
		case JUMP: {
			if (velocity.y <= 0) setImage(fall.getKeyFrame(landTimer.getCounter())); 
			else if (isSnap) setImage(snap.getKeyFrame(snapTimer.getCounter())); 
			else setImage(jump.getKeyFrame(landTimer.getCounter())); 
		} break;
		case CROUCH: setImage(crouch.getKeyFrame(crouchTimer.getCounter())); break;
		case STAND: setImage(idle.getKeyFrame(landTimer.getCounter())); break;
		case CLIMB: setImage(climb.getKeyFrame(OutwardEngine.getDeltaTime())); break;
		case DIVE: {
			if (isGrounded()) {
				setImage(slide.getKeyFrame(landTimer.getCounter()));
			}
			else {
				setImage(dive.getKeyFrame(OutwardEngine.getDeltaTime()));
			}
		} break;
		case SPIN: setImage(roll.getKeyFrame(OutwardEngine.getDeltaTime())); break;
		case WALL: setImage(wall.getKeyFrame(OutwardEngine.getDeltaTime())); break;
		}

		if (!latchTimer.timeUp()) setImage(latch.getKeyFrame(latchTimer.getCounter()));
		else if (fallen()) setImage(fallen.getKeyFrame(OutwardEngine.getDeltaTime()));
		else if (!flipTimer.timeUp()) setImage(flip.getKeyFrame(jumpTimer.getCounter()));
		else if (stunned()) setImage(hit.getKeyFrame(stunTimer.getCounter()));
		else if (isGrounded() && !skidTimer.timeUp()) setImage(skid.getKeyFrame(OutwardEngine.getDeltaTime()));
		if (!diveJumpTimer.timeUp()) setImage(divejump.getKeyFrame(diveJumpTimer.getCounter()));
	}

}
