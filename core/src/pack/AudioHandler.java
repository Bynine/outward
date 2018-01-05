package pack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import timers.DurationTimer;

public class AudioHandler {

	private static float MASTER_VOLUME = 0.4f;
	private static float PITCH = 1.0f;
	
	private static Music active_music = null;
	private static List<TimedSound> sounds = new ArrayList<TimedSound>();
	private static final int check_dispose = 60;
	private static final int time_dispose = 480;
	
	private static float getSFXVolume(){
		final float SFX_VOLUME = 1.0f;
		return SFX_VOLUME * MASTER_VOLUME;
	}
	
	private static float getMusicVolume(){
		final float MUSIC_VOLUME = 0.001f;
		return MUSIC_VOLUME * MASTER_VOLUME;
	}
	
	/**
	 * Checks every # of frames specified by check_dispose.
	 */
	public static void update(){
		if (OutwardEngine.getDeltaTime() % check_dispose == 0) handleTimedSounds();
	}
	
	/**
	 * Disposes of sounds after # frames specified in time_dispose.
	 */
	private static void handleTimedSounds(){
		Iterator<TimedSound> tsIter = sounds.iterator();
		while (tsIter.hasNext()){
			TimedSound timedSound = tsIter.next();
			timedSound.duration.countUp();
			if (timedSound.duration.timeUp()){
				timedSound.sfx.dispose();
				tsIter.remove();
			}
		}
	}

	/**
	 * Play sound effect with no specified origin
	 */
	public static void playSFX(String url, float ... optional_pitch){
		Sound sfx = Gdx.audio.newSound(Gdx.files.internal(url));
		playSFXHelper(sfx, getPitch(optional_pitch));
	}
	
	/**
	 * Play sound effect relative to player
	 */
	public static void playSFX(String url, float distance_x, float distance_y, float ... optional_pitch){
		Sound sfx = Gdx.audio.newSound(Gdx.files.internal(url));
		final long id = playSFXHelper(sfx, getPitch(optional_pitch));
		final float max_distance = 240f;
		
		float pan = -distance_x/160f;
		float agg_distance = MathUtils.clamp( ((Math.abs(distance_x) + Math.abs(distance_y))/2 - 60), 0, max_distance);
		float volume = getSFXVolume() * (1 - agg_distance/max_distance);
		sfx.setPan(id, pan, volume);
	}
	
	private static long playSFXHelper(Sound sfx, float pitch){
		final long id = sfx.play(getSFXVolume());
		sfx.setPitch(id, pitch * PITCH);
		sounds.add(new TimedSound(sfx));
		return id;
	}
	
	private static float getPitch(float ... optional_pitch){
		if (optional_pitch.length > 0) return optional_pitch[0];
		else return 1;
	}

	/**
	 * Ends current playing song, if applicable, then starts given one.
	 * Will do nothing if given song is same as currently playing one.
	 */
	public static void changeMusic(Music music){
		if (null != active_music){
			if (music.equals(active_music)){
				return;
			}
			active_music.stop();
			active_music.dispose();
		}
		active_music = music;
		active_music.setVolume(getMusicVolume());
		active_music.setLooping(true);
		active_music.play();
	}
	
	private static class TimedSound{
		private final Sound sfx;
		private final DurationTimer duration = new DurationTimer(time_dispose/check_dispose);
		TimedSound(Sound sfx){
			this.sfx = sfx;
		}
	}
	
}
