package pack.concrete.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import pack.OutwardEngine;
import pack.GraphicsHandler;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GraphicsHandler.SCREENWIDTH;
		config.height = GraphicsHandler.SCREENHEIGHT;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.vSyncEnabled = false;
		config.title = "Outward";
		new LwjglApplication(new OutwardEngine(), config);
	}
}