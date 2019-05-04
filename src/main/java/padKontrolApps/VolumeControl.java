package padKontrolApps;

import application.ApplicationManager;
import application.PadApplet;
import application.VoiceMeeter;
import application.VolumeMeter;

public class VolumeControl extends PadApplet implements VolumeMeter{
	
	VoiceMeeter vm;
	ApplicationManager appManager;
	
	public ApplicationManager getAppManager() {
		return appManager;
	}

	final float DEAD_ZONE = 0.01f;
	final float VOLUME_INCREMENT = 5.0f;
	
	
	int[] ButtonsToReserve = {0, 4, 8, 12, 1, 5, 9, 13};
	int[][] LightSets = new int[][]{
		  {0, 4, 8, 12},
		  {1, 5, 9, 13}
		};

	public VolumeControl(ApplicationManager appManager) {
		super(appManager);
		this.appManager = appManager;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int[] getReservedButtons() {
		return ButtonsToReserve;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		vm = new VoiceMeeter(this);
		vm.Initialize();
		vm.registerVolumeMeter(this);
	}

	@Override
	public void onEnd() {
		vm.ShutDown();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	public void updateLevelDisplay(float value, int lightSet){
		
		boolean[] lights = whichLights(4, value);
		for (int i = 0; i < LightSets[lightSet].length; i++){
			appManager.setLight(this, LightSets[lightSet][3-i], lights[i]);
		}
	}
	
	private boolean[] whichLights(int numberOfSegments, float value){
		boolean[] result = new boolean[numberOfSegments];
		final float max = 1.0f;
		float eachSegmentRange = max / numberOfSegments; 
		
		
		result[0] = (value > DEAD_ZONE)? true : false;
		for (int i = 1; i < numberOfSegments; i++){
			if (value > (eachSegmentRange * i)){
				result [i] = true;
			} else {
				result [i] = false;
			}
		}
		
		return result;
	}
	
	
	@Override
	public void ButtonPressed(int id) {
		
	}

	@Override
	public void ButtonReleased(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PadPressed(int id, int velocity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PadReleased(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void KnobChanged(int id, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SelectorChanged(boolean up) {
		String message;
		if (up){
			message = "Volume Up";
			vm.setVolume(vm.getVolume() + VOLUME_INCREMENT);
		} else {

			message = "Volume Down";
			vm.setVolume(vm.getVolume() - VOLUME_INCREMENT);
		}

		appManager.AppDebug(this, message);
		
	}

	@Override
	public void XYPadChanged(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Volume Control";
	}

	@Override
	public void UpdateVolume(float[] values) {
		updateLevelDisplay((values[0] + values[1])/2.0f, 0);
		updateLevelDisplay((values[2] + values[3])/2.0f, 1);
		
	}



}
