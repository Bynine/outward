package pack;

public class SaveFile {

	public final Flag 
	/* AUGUST */
	flag_mansion_entrance = new Flag(false),
	flag_mansion_trapA =	new Flag(false),
	flag_mansion_trapB =	new Flag(false),
	flag_mansion_key   =	new Flag(false),
	flag_mansion_coin  =	new Flag(false),
	flag_mansion_book  = 	new Flag(false),
	flag_mansion_wall  = 	new Flag(false),
	/* GLOBAL */
	flag_august_torch  = 	new Flag(false),
	flag_win 		   = 	new Flag(false);

	public class Flag{

		private boolean on = false;
		
		private Flag(boolean on){
			this.on = on;
		}

		public void activate(){ 
			on = true; 
			// Save game
		}

		public boolean is_on() { 
			return on; 
		}

	}

}
