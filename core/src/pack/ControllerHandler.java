package pack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

import entities.Player;

public class ControllerHandler extends InputHandler implements ControllerListener {

	private Controller controller;
	
	@Override
	boolean begin(){
		int numControllers = Controllers.getControllers().size;
		if (numControllers < 1) return false;
		Controllers.addListener(this);
		controller = Controllers.getControllers().first();
		if (!(controller.getName().toLowerCase().contains("xbox") 
				&& controller.getName().contains("360")) ) return false;
		Gdx.input.setInputProcessor(this);
		return true;
	}
	
	final float minInput = 0.2f;

	@Override
	public float getXInput() {
		float xInput = controller.getAxis(1);
		if (Math.abs(xInput) < minInput) return 0;
		else return xInput;
	}

	@Override
	public float getYInput() {
		float yInput = controller.getAxis(0);
		if (Math.abs(yInput) < minInput) return 0;
		else return yInput;
	}

	@Override
	void update() {
		OutwardEngine.handleJumpHeld(controller.getButton(Player.commandJump));
		OutwardEngine.handleFireHeld(controller.getButton(Player.commandFire));
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if (buttonCode == 0) pressA();
		if (buttonCode == 1) pressB();
		if (buttonCode == 6) pressSelect();
		if (buttonCode == 7) pressStart();
		return false;
	}
	
	@Override
	public void connected(Controller controller) {
		/**/
	}

	@Override
	public void disconnected(Controller controller) {
		/**/
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		/**/
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		/**/
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		/**/
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		/**/
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		/**/
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		/**/
		return false;
	}

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
