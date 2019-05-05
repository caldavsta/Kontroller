package padKontrolApps;

import application.ApplicationManager;
import application.PadApplet;

public class HiddenLights extends PadApplet {

	private int[] buttons = {10, 5, 0, 13};
	private ApplicationManager appManager;
	
	public HiddenLights(ApplicationManager appManager) {
		super(appManager);
		this.appManager = appManager;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int[] getReservedButtons() {
		// TODO Auto-generated method stub
		return buttons;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ButtonPressed(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ButtonReleased(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PadPressed(int id, int velocity) {
		appManager.setLight(this, id, true);
		
	}

	@Override
	public void PadReleased(int id) {
		appManager.setLight(this, id, false);
		
	}

	@Override
	public void KnobChanged(int id, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SelectorChanged(boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void XYPadChanged(int x, int y) {
		// TODO Auto-generated method stub
		
	}



}
