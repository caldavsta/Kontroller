package application;

import java.net.URL;
import java.util.ResourceBundle;

import application.VoiceMeeter.VoicemeeterRemote64;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MainWindowController implements Initializable {

	public PadKontrol pk;
	
	
	//PadKontrol
	@FXML Slider slider_lightselect;
	@FXML TextArea txtarea_output;
	@FXML Button btn_lightcycle;
	@FXML Button btn_allon;
	@FXML Button btn_alloff;
	@FXML TextField txt_Lcd;
	@FXML Button btn_setLcd;
	@FXML ProgressBar prog_volume;
	@FXML CheckBox chk_playOnce;
	
	//Voicemeeter
	@FXML Button btn_getVolume;
	@FXML Label lbl_volume;
	
	@FXML
	void updatetxtarea(MouseEvent event){
		Debug("Hello!!");
	}
	
	@FXML
	void cyclelights(ActionEvent event){

	}
	
	@FXML
	void setLcd(ActionEvent event){
		pk.setLongLcdText(txt_Lcd.getText(), chk_playOnce.isSelected());
	}
	
	@FXML
	void allLightsOn(ActionEvent event){
	}
	
	@FXML
	void allLightsOff(ActionEvent event){
	}
	
	@FXML
	void getVolume(ActionEvent event){
		
	}
	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//SLIDER EVENT LISTENER
		slider_lightselect.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number old_val, Number new_val) {
				int oldValue = old_val.intValue();
				int newValue = new_val.intValue();
				pk.SetLight(newValue, true);
				pk.SetLight(oldValue, false);
			}
		});
		
	}
	
	public void Debug(String text){
		System.out.println(text);
		txtarea_output.appendText("\n" + text);
	}

}
