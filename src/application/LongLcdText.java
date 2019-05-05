package application;

public class LongLcdText implements Runnable{

	private PadKontrol pk;
	final long SCROLL_DELAY = 250;
	private String EMPTY_CHARACTER_LETTER = " ";
	
	//FOR MANAGING CURRENT TEXT
	String text; 
	String[] textArray;
	boolean playOnce;
	
	//FOR MANAGING STATE
	volatile boolean running;
	Thread currentThread;
	volatile boolean paused;
	
	
	public LongLcdText (PadKontrol pk, String message, boolean playOnce){
		this.pk = pk;
		text = message;
		running = true;
		this.playOnce = playOnce;
	}
	
	@Override
	public void run() {
		currentThread = Thread.currentThread();
		textArray = buildLoopingStringArray(text);
		System.out.println("LongLcdText beginning: " + text);
		
		int i = 0;
		while (running){
			if (!paused){//because other processes may display a message over this one
				pk.setLcdText(textArray[i]);
				i++;
				if (i >= textArray.length){
					i = 0;
					if (playOnce) running = false;
				}
			}

			try {
				Thread.sleep(SCROLL_DELAY);
			} catch (InterruptedException e) {
				System.out.println("Stopping message: " + text);
			}

		}
		pk.setShortLcdText("   ");
	}
	
	public void stop(){
		running = false;
		currentThread.interrupt();
	}
	
	public Thread getThread(){
		return Thread.currentThread();
	}
	
	private String[] buildLoopingStringArray(String input){
		int arrayLength = input.length()+1;
		String[] result = new String[arrayLength];
		
		StringBuilder sb;
		int s = 0;
		for (s = 0; s < input.length()-2; s++){
			sb = new StringBuilder();
			sb.append(input.charAt(s));
			sb.append(input.charAt(s+1));
			sb.append(input.charAt(s+2));
			result[s] = sb.toString();
			//System.out.println(s + " " + result[s]);
		}
		sb = new StringBuilder();
		sb.append(input.charAt(s));
		sb.append(input.charAt(s+1));
		sb.append(EMPTY_CHARACTER_LETTER);
		result[s] = sb.toString();
		//System.out.println(s + " " + result[s]);
		s++;
		sb = new StringBuilder();
		sb.append(input.charAt(s));
		sb.append(EMPTY_CHARACTER_LETTER);
		sb.append(EMPTY_CHARACTER_LETTER);
		result[s] = sb.toString();
		//System.out.println(s + " " + result[s]);
		s++;
		sb = new StringBuilder();
		sb.append(EMPTY_CHARACTER_LETTER);
		sb.append(EMPTY_CHARACTER_LETTER);
		sb.append(EMPTY_CHARACTER_LETTER);
		result[s] = sb.toString();
		//System.out.println(s + " " + result[s]);
		
		
		return result;
	}
	

}
