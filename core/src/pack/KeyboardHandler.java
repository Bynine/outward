package pack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class KeyboardHandler extends InputHandler {
	
	@Override
	boolean begin(){
		Gdx.input.setInputProcessor(this);
		return true;
	}
	
	private final int
	jump_command = Keys.W,
	fire_command = Keys.M;
	@Override
	void update() {
		OutwardEngine.handleJumpHeld(Gdx.input.isKeyPressed(jump_command));
		OutwardEngine.handleFireHeld(Gdx.input.isKeyPressed(fire_command));
		if (Gdx.input.isKeyJustPressed(jump_command)) pressA();
		if (Gdx.input.isKeyJustPressed(fire_command)) pressB();
		if (Gdx.input.isKeyJustPressed(Keys.P)) pressStart();
		if (Gdx.input.isKeyJustPressed(Keys.LEFT_BRACKET)) pressSelect();
	}

	@Override
	public float getXInput() {
		if (Gdx.input.isKeyPressed(Keys.A)) return -1;
		if (Gdx.input.isKeyPressed(Keys.D)) return  1;
		return 0;
	}

	@Override
	public float getYInput() {
		if (Gdx.input.isKeyPressed(Keys.W)) return -1;
		if (Gdx.input.isKeyPressed(Keys.S)) return  1;
		return 0;
	}
	
	/* UNUSED */

	@Override
	public boolean keyDown(int keycode) {
		/**/
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		/**/
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		/**/
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		/**/
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		/**/
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		/**/
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		/**/
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		/**/
		return false;
	}

}
