package padKontrolApps;

import application.ApplicationManager;
import application.PadApplet;

public class AppLauncher extends PadApplet {
	
	ApplicationManager appManager;
	
	int[] ButtonsToReserve = {23, 24, 27, 30};

	public AppLauncher(ApplicationManager appManager) {
		super(appManager);
		
		this.appManager = appManager;
		
	}
	
	@Override
	public void onStart() {
	}
	
	@Override
	public void onEnd() {

	}
	
	@Override
	public void onPause() {
		
	}

	@Override
	public void onResume() {
		
	}

	@Override
	public void ButtonPressed(int id) {
		appManager.AppDebug(this, "Button Pressed " + id + ". This is thread: " + Thread.currentThread().getId());
	}

	@Override
	public void ButtonReleased(int id) {
		appManager.AppDebug(this, "Button Released " + id);
		
	}

	@Override
	public void PadPressed(int id, int velocity) {
		appManager.AppDebug(this, "Pad pressed " + id + " at " + velocity);
		appManager.setLight(this, id, true);
	}

	@Override
	public void PadReleased(int id) {
		appManager.AppDebug(this, "Pad pressed " + id);

		appManager.setLight(this, id, false);
	}

	@Override
	public void KnobChanged(int id, int newValue) {
		// TODO Auto-generated method stub
		appManager.AppDebug(this, "Knob " + id + " " + newValue);
		
	}

	@Override
	public void SelectorChanged(boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void XYPadChanged(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getReservedButtons() {
		return ButtonsToReserve;
	}




	@Override
	public String getName() {
		return "App Launcher";
	}


	

}
