package pack;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;

import entities.Destination;
import timers.Timer;

public class Dialogue {

	private final List<String> sentences = new ArrayList<String>();
	private Destination destination = null;
	private final Timer time = new Timer(0);
	private int place = 0;
	private Voice voice;
	
	public static final Voice voice_text = new Voice(6, "sfx/voices/text.ogg");
	public static final Voice voice_deep = new Voice(6, "sfx/voices/deep.ogg");
	public static final Voice voice_mid = new Voice(6, "sfx/voices/mid.ogg");
	public static final Voice voice_high = new Voice(6, "sfx/voices/high.ogg");
	public static final Voice voice_obnoxious = new Voice(6, "sfx/voices/obnoxious.ogg");

	public Dialogue(Voice voice, Object ... objs){
		this.voice = voice;
		
		for (Object obj: objs){
			if (obj instanceof String) {
				sentences.add((String)obj);
			}
			else if (obj instanceof Destination) {
				destination = (Destination)obj;
			}
			else{
				sentences.add(obj.toString());
				System.out.println("ERROR: Tried to fill Dialogue with inadmissable object!");
			}
		}
	}
	
	public void begin(){
		time.reset();
	}
	
	public void update(){
		time.countUp();
	}
	
	public boolean advance(){
		place++;
		if (sentences.size() <= place) {
			place = 0;
			if (null != destination) OutwardEngine.changeRoom(destination);
			return false;
		}
		else {
			time.reset();
			return true;
		}
	}

	public String getDisplayedString(){
		String display = "";
		int num_letters = MathUtils.clamp(time.getCounter()/2, 0, getActiveSentence().length());
		if (num_letters % voice.timing == 0 && num_letters < getActiveSentence().length()) {
			float pitch = (float) (1.0 - (0.5 - Math.random())/10);
			AudioHandler.playSFX(voice.pickClip(), pitch);
		}
		display = getActiveSentence().substring(0, num_letters);
		return display;
	}
	
	private String getActiveSentence(){
		return sentences.get(place);
	}
	
	private static class Voice{
		private final ArrayList<String> voice_urls = new ArrayList<String>();
		private final int timing;
		private String prev_choice = "";
		
		Voice(int time, String ... urls){
			timing = time;
			for (String url: urls){
				voice_urls.add(url);
			}
		}
		
		String pickClip(){
			if (voice_urls.size() == 1) return voice_urls.get(0);
			// Otherwise, let's pick one we didn't use last time.
			ArrayList<String> usable_voice_urls = new ArrayList<String>();
			usable_voice_urls.addAll(voice_urls);
			usable_voice_urls.remove(prev_choice);
			
			String clip = usable_voice_urls.get((int) (Math.random() * usable_voice_urls.size()) );
			prev_choice = clip;
			return clip;
		}
	}

}
