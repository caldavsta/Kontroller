package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Kontroller extends Application {
	
	//JAVAFX stuff
	public static final String MAIN_WINDOW_FXML = "/application/View/MainWindow.fxml";
	private Stage primaryStage;
	private AnchorPane rootLayout;
	private MainWindowController mainWindowController;
	
	//APPLICATION stuff
	private HardwareManager hwManager;
	private ApplicationManager appManager;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("PadKONTROLLer");
		
		initRootLayout();
		
		setupHardwareManager();
		hwManager.Initialize();
		
		appManager = new ApplicationManager(hwManager.getPk(), this);
		hwManager.getPk().Initialize(appManager);



	}
	
	@Override
	public void stop(){
		appManager.ShutDown();
		hwManager.ShutDown();
		try {
			super.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initRootLayout(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Kontroller.class.getResource(MAIN_WINDOW_FXML));
            rootLayout = (AnchorPane) loader.load();
            
            //get MainWindowController for access by other processes
            mainWindowController = (MainWindowController) loader.getController();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
        	System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
	public void setVolumeMeter(float volume){
		mainWindowController.prog_volume.setProgress(volume);
	}
    
    private void setupHardwareManager(){
		hwManager = new HardwareManager(this);
    }
    
    public MainWindowController getMainWindowController(){
    	return mainWindowController;
    }
    
    public HardwareManager GetHardwareManager(){
    	return hwManager;
    }
    
}
