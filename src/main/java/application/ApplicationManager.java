package application;

import java.util.Hashtable;
import java.util.Random;

import padKontrolApps.AppLauncher;
import padKontrolApps.HiddenLights;
import padKontrolApps.VolumeControl;


public class ApplicationManager {
	
	private static final int BUTTON_NOT_IN_USE = -1;
	
	private Hashtable<Integer, PadApplet> RunningApps;
	private int[] buttonsMap; //-1 means the button is not used
	private volatile PadKontrol pk;
	private Random random;
	
	private Kontroller kontroller;
	
	
	public ApplicationManager(PadKontrol pk, Kontroller kontroller){
		this.pk = pk;
		this.kontroller = kontroller;
		
		//setup local vars
		random = new Random();
		RunningApps = new Hashtable<Integer, PadApplet>();
		
		
		//setup buttonsMap
		buttonsMap = new int[41];
		for (int i = 0; i < buttonsMap.length; i++){
			buttonsMap[i] = BUTTON_NOT_IN_USE;
		}
		
		//make it dark
		turnOffAllLights();
		
		//the default app is called AppLauncher. Launch it now.
		AppLauncher myAppLauncher = new AppLauncher(this);
		try{
			beginRunningApplet(myAppLauncher);
			beginRunningApplet(new VolumeControl(this));
			//BeginRunningApp(new HiddenLights(this));
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("Application Manager Running on  thread: " + Thread.currentThread().getId());
	}
	
	
	
	public synchronized void ShutDown(){
		for (PadApplet thisApp : RunningApps.values()){
			stopRunningApplet(thisApp);
		}
		turnOffAllLights();
	}
	
	public synchronized boolean beginRunningApplet(PadApplet applet) throws PadAppException{
		
		//make sure app has an ID. Might as well get this out of the way
		applet.instanceId = generateAppInstanceId();
		
		//make sure buttons arent already taken
		System.out.println("Launching applet " + applet.getName() + " .....");
		if (AreButtonsAlreadyReserved(applet.getReservedButtons())){
			throw new PadAppException("Applet " + applet + "Launch Failed. Buttons already reserved.");
		}
		
		//reserve the buttons
		reserveButtonsForApp(applet);
		

		//add app to running
		RunningApps.put(applet.instanceId, applet);
		
		//start the app
		Thread thisThread = new Thread(applet);
		applet.thread = thisThread;
		thisThread.start();
		
		
		
		return true;
	}
	
	public synchronized boolean stopRunningApplet(PadApplet applet){
		releaseButtonsForApp(applet);
		
		
		//remove app from running
		if (RunningApps.contains(applet)){
			System.out.println(applet + " shutting down.");
			applet.onPause();
			applet.onEnd();
			RunningApps.remove(applet);
			
			return true;
		}
		
		return false;
	}
	

	
	public synchronized boolean AreButtonsAlreadyReserved(int[] buttons){
		for (int i = 0; i < buttonsMap.length; i++){
			for (int j = 0; j < buttons.length; j++){
				if (buttonsMap[i] == buttons[j]){
					return true;
				}
			}
		}
		return false;
	}
	
	public synchronized void setLight(PadApplet source, int id, boolean power){
		if (buttonsMap[id] == source.instanceId){
			if (id <= 15){
				pk.SetLight(id, power);
			} else {
				if (id <=34){
					pk.SetLight(BUTTON_MAP[id], power);
				}
			}
		} else {
			throw new PadAppException("App " + source + " is trying to access lights that belong to app " + getAppByInstanceId(buttonsMap[id]));
		}

	}
	
	public void turnOffAllLights(){
		for (int i = 0; i <= 34; i++){
			pk.SetLight(i, false);
		}
	}
	
	private synchronized int generateAppInstanceId(){
		boolean intUsed;
		int intToTry;
		do {
			intToTry = anyRandomIntRange(random, 10, 99);
			if (RunningApps.containsKey(intToTry)){
				intUsed = true;
			} else {
				intUsed = false;
			}
		} while (intUsed);
		
		return intToTry;
	}
	
	private synchronized void reserveButtonsForApp(PadApplet app){
		//reserve buttons
		for (int i=0; i< app.getReservedButtons().length; i++){
			buttonsMap[app.getReservedButtons()[i]] = app.instanceId;
		}
	}
	
	private synchronized void releaseButtonsForApp(PadApplet app){
		//reserve buttons
		for (int i=0; i< app.getReservedButtons().length; i++){
			buttonsMap[app.getReservedButtons()[i]] = -1;
		}
	}
	
	
	private synchronized PadApplet getAppByInstanceId(int instanceId){
		if (RunningApps.containsKey(instanceId)){
			return RunningApps.get(instanceId);
		} else {
			throw new PadAppException("Cannot get app instance #" + instanceId);
		}
	}
	
	public void AppDebug(PadApplet app, String message){
		System.out.println("APP: " + app.getName() + " :: " + message);
	}
	
	// ----------------------------------------------------------------------------------
	// Redirecting hardware input to the applet
	// ----------------------------------------------------------------------------------
	//
	

	private static final int[] BUTTON_MAP = {16, 17, 23, 31, 28, 25, 32, 29, 26, 18, 19, 30, 27, 33, 34, 24, 22, 21, 20};
	private static final int[] KNOB_MAP = {36, 37};
	
	public void ButtonPressed(int id){
		System.out.println("Button " + id + " Pressed. " + BUTTON_MAP[id]);
		if (buttonsMap[BUTTON_MAP[id]] != BUTTON_NOT_IN_USE){
			getAppByInstanceId(buttonsMap[BUTTON_MAP[id]]).ButtonPressed(BUTTON_MAP[id]);
		}
	}
	
	public void ButtonReleased(int id){
		System.out.println("Button " + id + " Released. " + BUTTON_MAP[id]);
		if (buttonsMap[BUTTON_MAP[id]] != BUTTON_NOT_IN_USE){
			getAppByInstanceId(buttonsMap[BUTTON_MAP[id]]).ButtonReleased(BUTTON_MAP[id]);
		}
	}
	
	public void PadPressed(int id, int velocity){
		System.out.println("Pad " + id + " Pressed at " + velocity);
		if (buttonsMap[id] != BUTTON_NOT_IN_USE){
			getAppByInstanceId(buttonsMap[id]).PadPressed(id, velocity);
		}
	}
	
	public void PadReleased(int id){
		System.out.println("Pad " + id + " Released");
		if (buttonsMap[id] != BUTTON_NOT_IN_USE){
			getAppByInstanceId(buttonsMap[id]).PadReleased(id);
		}
	}
	
	public void KnobChanged(int id, int newValue){
		System.out.println("Knob " + id + " : " + newValue);
		if (buttonsMap[KNOB_MAP[id]] != BUTTON_NOT_IN_USE){
			getAppByInstanceId(buttonsMap[KNOB_MAP[id]]).KnobChanged(KNOB_MAP[id], newValue);
		}
	}
	
	public void SelectorChanged(boolean up){
		int id = 35;
		System.out.println("Selector " + up);
		if (buttonsMap[id] != BUTTON_NOT_IN_USE){
			getAppByInstanceId(buttonsMap[id]).SelectorChanged(up);;
		}
	}
	
	public void XYPadChanged(int x, int y){
		System.out.println("X: " + x + " Y: " + y);
	}
	
	public static int anyRandomIntRange(Random random, int low, int high) {
		int randomInt = random.nextInt(high) + low;
		return randomInt;
	}
	
	public Kontroller getKontroller(){
		return kontroller;
	}
	

/*
PADS
0 
1 
2 
3 
4 
5 
6 
7 
8 
9 
10 
11 
12 
13 
14 
15 

BUTTONS
16 0
17 1
18 9
19 10
20 18
21 17
22 16
23 2
24 15
25 5
26 8
27 12
28 4
29 7
30 11
31 3
32 6
33 13
34 14

SELECTOR
35 

KNOBS
36 
37 

LCD
38 

XY PAD
39 

PEDAL
40 
*/
}
