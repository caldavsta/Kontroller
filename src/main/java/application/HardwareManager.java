package application;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;


public class HardwareManager {
	
	//JAVAFX stuff
	private Kontroller application;
	
	//OTHER stuff
	
	private static final String PK_MIDI_OUT = "MIDIOUT2 (padKONTROL)";
	private static final String PK_MIDI_IN = "MIDIIN2 (padKONTROL)";
	
	
	private MidiDevice outDevice = null;
	private Transmitter transmitter = null;
	private Receiver receiver = null;
	private MidiDevice inDevice = null;
	private PadKontrol pk;
	
	private List<MidiDevice> devicesToClose;
	
	public HardwareManager(Kontroller application){
		devicesToClose = new ArrayList<MidiDevice>();
		
		//setupfxml
		this.application = application;
	}
	
	public void Initialize(){
		//setup devices
		
		System.out.println("Hardware Manager started on thread: " + Thread.currentThread().getId());
		
		try {
			setupDevices();
		} catch (MidiUnavailableException e) {
			System.out.println("Device(s) are unavailable: " + e.getMessage());
		}
		
		pk = new PadKontrol(application);
		transmitter.setReceiver(getPk());
		
		getPk().PutDeviceInNativeMode();//once device is in native mode, close devices and set them up again. must connect to device while it is already in native mode
		
		CloseAllMidiDevices();
		
		try {
			setupDevices();
		} catch (MidiUnavailableException e) {
			System.out.println("Device(s) are unavailable: " + e.getMessage());
		}

		pk = new PadKontrol(application);
		//pk.Initialize();
		transmitter.setReceiver(pk);
	}
	
	public void ShutDown(){
		pk.ShutDown();
		CloseAllMidiDevices();
	}
	
	private void setupDevices() throws MidiUnavailableException{
		
		outDevice = MidiSystem.getMidiDevice(GetMidiDeviceInfo(PK_MIDI_OUT));
		OpenMidiDevice(outDevice);
		
		inDevice = MidiSystem.getMidiDevice(GetMidiDeviceInfo(PK_MIDI_IN));
		OpenMidiDevice(inDevice);
		
		application.getMainWindowController().Debug("HardwareManager: Midi Devices Setup");
		
		receiver = outDevice.getReceiver();
		transmitter = inDevice.getTransmitter();

		application.getMainWindowController().Debug("HardwareManager: Midi Receiver and Transmitter setup");
		
	}
	
	private void OpenMidiDevice(MidiDevice device) throws MidiUnavailableException{
		device.open();
		devicesToClose.add(device);
	}
	
	public void CloseAllMidiDevices(){
		application.getMainWindowController().Debug("Closing " + devicesToClose.size() + " devices.");
		
		for (MidiDevice device : devicesToClose){
			device.close();
		}
		
		devicesToClose.clear();
		application.getMainWindowController().Debug("Devices Closed");
	}
	

	public static MidiDevice.Info GetMidiDeviceInfo(String deviceName){
		MidiDevice.Info result = null;
		MidiDevice.Info[] deviceInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < deviceInfos.length; i++){
			if (deviceInfos[i].getName().equals(deviceName)){
				result = deviceInfos[i];
			}
		}
		return result;

	}

	public Receiver getReceiver() {
		return receiver;
	}

	public PadKontrol getPk() {
		return pk;
	}


	

}
