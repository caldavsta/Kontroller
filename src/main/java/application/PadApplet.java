package application;

public abstract class PadApplet implements Runnable {
	
	
	private ApplicationManager appManager;
	
	
	public PadApplet(ApplicationManager appManager){
		this.appManager = appManager;
	}
	
	
	
	public abstract int[] getReservedButtons();
	public abstract String getName();
	
	@Override
	public void run() {
		System.out.println("App beginning: " + this + " on thread: " + Thread.currentThread().getId());
		
		onStart();

	}
	
	@Override
	public String toString(){
		String result = getName() + " (" + instanceId + ")";
		return result ;
		
	}
	

	public abstract void onStart();
	public abstract void onEnd();
	public abstract void onPause();
	public abstract void onResume();
	
	
	public Thread thread;
	public Integer instanceId;
	public abstract void ButtonPressed(int id);
	public abstract void ButtonReleased(int id);
	public abstract void PadPressed(int id, int velocity);
	public abstract void PadReleased(int id);
	public abstract void KnobChanged(int id, int newValue);
	public abstract void SelectorChanged(boolean up);
	public abstract void XYPadChanged(int x, int y);
	
	

}
