package jsmocap.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MocapTempl extends Application
{
	private Controller controller_;
	
	public static void main(String[] args) throws IOException 
	{
		launch(args);
	}

	@Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
		controller_=new Controller();
		controller_.stage_=primaryStage;
        
		primaryStage.setTitle("Motion Templates");
        
        VBox root = new VBox();
        HBox up = new HBox(10);
        
        root.getChildren().add(up);
        
        primaryStage.setScene(new Scene(root, 1024, 580, true));
        
        try
        {
        	MocapScene mocapScene=new MocapScene(controller_);
        	
        	ClassDisplay cdisplay=new ClassDisplay(controller_);
        	cdisplay.getRoot().setTranslateX(620);
        	cdisplay.getRoot().setTranslateY(20);
        	
        	Group group=new Group();
        	group.getChildren().add(cdisplay.getRoot());
        	group.getChildren().add(mocapScene.getSubScene());
        	up.getChildren().add(group);
        	
        	//up.getChildren().add(mocapScene.getSubScene());
        	
        	ToolboxPanel panel=new ToolboxPanel(controller_);
        	up.getChildren().add(panel.getScene());
        }
        catch(Exception e)
        {
        	
        }
       // root.getChildren().add(btn);
        
         
        
        
        
        primaryStage.show();
    }
}
