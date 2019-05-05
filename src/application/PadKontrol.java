package application;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

import org.apache.commons.lang3.ArrayUtils;

public class PadKontrol implements Receiver{
	//SYSEX STUFF
	private Receiver receiver; // this is where messages are sent TO
	public final byte[] SYSEX_PREFIX = {(byte) 0xf0, 0x42, 0x40, 0x6e, 0x08};
	public final byte[] SYSEX_SUFFIX = {(byte) 0xf7}; 
	public final byte LIGHT_OFF_HEX = 0x00;
	public final byte LIGHT_ON_HEX = 0x20;
	public final byte SYSEX_LIGHT_PREFIX = 0x01;
	public final byte SYSEX_LCD_PREFIX = 0x22;

	//APPLICATION STUFF
	private Kontroller application;
	private boolean allowPadAppCommunication;

	//HARDWARE STUFF
	ApplicationManager appManager;
	
	//LCD STUFF
	LongLcdText longLcdText;
	List<LongLcdText> shortMessageQueue = new ArrayList<LongLcdText>();
	

	
	public PadKontrol (Kontroller application){
		this.application = application;
	
		receiver=application.GetHardwareManager().getReceiver();
		application.getMainWindowController().pk =  this;

	}
	
	public void Initialize(ApplicationManager appManager){//only should be called once the device is opened while in native mode
		setAllowPadAppCommunication(true);
		this.appManager = appManager;


		//setLongLcdText("on ", false);
	}
	
	public void ShutDown(){
		setAllowPadAppCommunication(false);
		receiver=null;
		cancelLongLcdText();
		setShortLcdText("OFF");
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {// this is where messages come FROM
		System.out.println("Midi Received on thread " + Thread.currentThread().getId());
		String output = "";
		byte[] byteMessage = message.getMessage();
		for (int i = 5; i < byteMessage.length-1; i++){
			output += byteMessage[i] + " ";
		}
		boolean displayed = false;
		
		
		//Handle messages from device about buttons that may be related to apps
		if (appManager != null){
			if (byteMessage[5] == 69){//Message is about a drum pad
				if (byteMessage[6] >= 64 && byteMessage[6] <= 79){//Drum Pad was PRESSED
					appManager.PadPressed(byteMessage[6] - 64, byteMessage[7]);
					displayed = true;
				}
				if (byteMessage[6] >= 0 && byteMessage[6] <= 15){//Drum Pad was RELEASED
					appManager.PadReleased(byteMessage[6]);
					displayed = true;
				}
			}
			
			if (byteMessage[5] == 72){//Message is about a button OR the XY pad
				if (byteMessage[6] == 32){//XY pad
					
				} else {
					if (byteMessage[7] == 127){//Other button
						appManager.ButtonPressed(byteMessage[6]);
						displayed = true;
					} else {
						appManager.ButtonReleased(byteMessage[6]);
						displayed = true;
					}
				}

			}
			
			if (byteMessage[5] == 67){ //Message is about selector
				if (byteMessage[7] == 1){
					appManager.SelectorChanged(true);
					displayed = true;
				} else {
					appManager.SelectorChanged(false);
					displayed = true;
				}
			}
			
			if (byteMessage[5] == 73){//Message is about knob
				appManager.KnobChanged(byteMessage[6], byteMessage[7]);
				displayed = true;
			}
		}

		
		if (!displayed){
			System.out.println("Incoming Midi: " + output);
		}
		
	}
	
	public void setShortLcdText(String text){
		String messageToSet;
		if (text.length() > 3){
			StringBuilder sb = new StringBuilder();
			sb.append(text.substring(0, 2));
			messageToSet = sb.toString();
		} else {
			messageToSet = text;
		}
		setLcdText(messageToSet);
	}
	
	private void cancelLongLcdText(){
		   if (longLcdText != null){
			   System.out.println("Cancelling LcdText: " + longLcdText.text);
			   longLcdText.stop();
		   }
	}

   public void setLongLcdText(String text, boolean playOnce){
	   cancelLongLcdText();
	   
	   longLcdText = new LongLcdText(this, text, playOnce);
		Thread newThread = new Thread(longLcdText);
		newThread.start();
   }
   
   public void setLcdText(String text){
	   
	   byte[] prefix = {SYSEX_LCD_PREFIX, 0x04, 0x00};
	   
	   byte[] letters = text.getBytes();
	   byte[] byteMessage = ArrayUtils.addAll(prefix, letters);
	   SendMessage(byteMessage);
   }
   
	public void SetLight(int light, boolean status){
		byte lightStatus = (status? LIGHT_ON_HEX : LIGHT_OFF_HEX);
		
		byte[] lightMessage = {SYSEX_LIGHT_PREFIX, (byte) light, lightStatus};
		SendMessage(lightMessage);
	}

	public synchronized void SendMessage(byte[] input){
		if (receiver == null){
			System.out.println("PadKontrol can't send message. It has been deactivated.");
			return;
		}
		byte[] message1 = ArrayUtils.addAll(SYSEX_PREFIX, input);
		byte[] byteMessage = ArrayUtils.addAll(message1, SYSEX_SUFFIX);
		SysexMessage sysexMessage = null;
		try {
			sysexMessage = new SysexMessage(byteMessage, byteMessage.length);
			receiver.send(sysexMessage, -1);
			//System.out.println("Message sent: " + byteArrayToHexString(byteMessage));
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem creating message: " + e.getMessage());
		}
	}
	
	
	public void PutDeviceInNativeMode() {
		SendMessage(hexStringToByteArray("00 00 01"));
		SendMessage(hexStringToByteArray("3f 0a 01 7f 7f 7f 7f 7f 03 38 38 38"));
		SendMessage(hexStringToByteArray("3f 2a 00 00 05 05 05 7f 7e 7f 7f 03 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 0a 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10"));
	}
	
	public static byte[] hexStringToByteArray(String input) {
		
		String s = input.replace(" ", "");
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String byteArrayToHexString(byte[] input){
		StringBuilder sb = new StringBuilder();
		for (byte thisByte : input){
			sb.append(Byte.toString(thisByte) + " ");
		}
		
		return sb.toString();
	}

	public boolean isAllowPadAppCommunication() {
		return allowPadAppCommunication;
	}

	public void setAllowPadAppCommunication(boolean running) {
		this.allowPadAppCommunication = running;
	}
	

	
}
