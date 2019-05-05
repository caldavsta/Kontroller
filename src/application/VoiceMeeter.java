package application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Native;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.win32.StdCallLibrary;

import padKontrolApps.VolumeControl;

public class VoiceMeeter {
	
	//Voicemeeter DLL stuff
	public static final String VOICEMEETER_DLL_LOCATION = "C:/Program Files (x86)/VB/Voicemeeter/VoicemeeterRemote64.dll";
	public boolean loggedIn = false;
	
	
	//Voicemeeter Parameters
	public float[] currentLevels;
	
	
	//Application stuff
	private VolumeControl volumeControl;
	private List<VolumeMeter> volumeMeters;
	
	public VoiceMeeter(VolumeControl volumeControl){
		this.volumeControl = volumeControl;
		
		//initialize vars
		currentLevels = new float[23];
		volumeMeters = new ArrayList<VolumeMeter>();
		
		
	    try {
	    	System.load(VOICEMEETER_DLL_LOCATION);
	    } catch (UnsatisfiedLinkError e) {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	}
	
	public void Initialize(){
		Login();
		
		
		//Show voicemeeter version
		LongByReference longRef = new LongByReference();
		VoicemeeterRemote64.INSTANCE.VBVMR_GetVoicemeeterVersion(longRef);
		Long value = longRef.getValue();
		System.out.println("Voicemeeter version: " + value);
		
		//Show volume

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		
		
		//UPDATE PARAMETERS
		try{
			exec.scheduleAtFixedRate(new Runnable() {
				  @Override
				  public void run() {
				    if (VoicemeeterRemote64.INSTANCE.VBVMR_IsParametersDirty() == 1){
				    	
				    }
				  }
				}, 0, 20, TimeUnit.MILLISECONDS);
		} catch (Exception e){
			e.printStackTrace();
		}

		
		//VOLUME CHECK
		try{
			exec.scheduleAtFixedRate(new Runnable() {
				  @Override
				  public void run() {
				    	refreshCurrentLevels();
				    	for(int i = 0; i < volumeMeters.size(); i++){
				    		volumeMeters.get(i).UpdateVolume(currentLevels);
				    	}
				  }
				}, 0, 100, TimeUnit.MILLISECONDS);
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public void setVolume(float newVolume){
		String parameterName = "Strip[0].Gain";
		VoicemeeterRemote64.INSTANCE.VBVMR_SetParameterFloat(parameterName, newVolume);
	}
	
	public float getVolume(){
		String parameterName = "Strip[0].Gain";
		FloatByReference floatRef = new FloatByReference();
		VoicemeeterRemote64.INSTANCE.VBVMR_GetParameterFloat(parameterName, floatRef);
		float parameterValue = floatRef.getValue();
		//System.out.println(parameterName + " : " + parameterValue);
		return parameterValue;
	}
	
	public void refreshCurrentLevels(){
		for (int vi = 0; vi < 22; vi++){
			FloatByReference floatRef = new FloatByReference();
			VoicemeeterRemote64.INSTANCE.VBVMR_GetLevel(0, vi, floatRef);
			currentLevels[vi] = floatRef.getValue();
		}
	}
	
	public void debugCurrentLevels(){
		StringBuilder sb = new StringBuilder();
		for (int vi = 0; vi < 22; vi++){
			sb.append(vi + ":" + currentLevels[vi] + "|");
		}
		System.out.println(sb.toString());
	}
	
	public void ShutDown(){
		VoicemeeterRemote64.INSTANCE.VBVMR_Logout();
		System.out.println("Voicemeeter Logged Out");
	}
	
	private void Login(){
		if (VoicemeeterRemote64.INSTANCE.VBVMR_Login() == 0){
			System.out.println("Voicemeeter Logged In");
		} else {
			System.out.println("Voicemeeter cannot login");
		}
	}
	
	public void registerVolumeMeter(VolumeMeter meter){
		this.volumeMeters.add(meter);
	}
	
	public void unregisterVolumeMeter(VolumeMeter meter){
		this.volumeMeters.remove(meter);
	}
	
	public interface VoicemeeterRemote64 extends StdCallLibrary {
		VoicemeeterRemote64 INSTANCE = (VoicemeeterRemote64)Native.loadLibrary("VoicemeeterRemote64", VoicemeeterRemote64.class);
		  long VBVMR_Login ();
		  long VBVMR_Logout ();
		  long VBVMR_GetVoicemeeterVersion(LongByReference pVersion);
		  long VBVMR_GetParameterFloat(String szParamName, FloatByReference pValue);
		  long VBVMR_SetParameterFloat(String szParamName, float Value);
		  long VBVMR_GetLevel(long nType, long nuChannel, FloatByReference pValue);
		  long VBVMR_IsParametersDirty();
	}
	

	
}
