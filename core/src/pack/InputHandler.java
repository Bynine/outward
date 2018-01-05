package pack;

import com.badlogic.gdx.InputProcessor;

public abstract class InputHandler implements InputProcessor {
	
	abstract boolean begin();
	abstract void update();
	
	public abstract float getXInput();
	public abstract float getYInput();
	
	protected final void pressA(){
		OutwardEngine.handleAPress();
	}
	
	protected final void pressB(){
		OutwardEngine.handleBPress();
	}
	
	protected final void pressStart(){
		OutwardEngine.handleStartPress();
	}
	
	protected final void pressSelect(){
		OutwardEngine.handleSelectPress();
	}

}
