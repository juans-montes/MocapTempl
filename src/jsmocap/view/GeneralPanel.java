package jsmocap.view;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import jsmocap.io.AMCReader;
import jsmocap.io.ASFReader;
import jsmocap.skeleton.Motion;
import jsmocap.skeleton.Skeleton;

public class GeneralPanel 
{
	private ListView<Motion> motions_;
	public ObservableList<Motion> motions_list_;
	
	private FileChooser asf_;
	private FileChooser amc_;
	private Button asf_b_;
	private Button amc_b_;
	private Label asf_l_;
	private Label amc_l_;
	private File asf_f_;
	private File amc_f_;
	private Button load_motion_;
	
	private Button start_pause_;
	
	private Group root_;
	private SubScene scene_;
	
	private Controller controller_;
	
	public GeneralPanel(Controller controller)
	{
		controller_=controller;
		controller_.gpanel_=this;
		
		VBox vbox = new VBox(5);
		root_=new Group();
		
		motions_=new ListView<Motion>();
		
		motions_.setPrefWidth(224);
		motions_.setPrefHeight(300);
		
		motions_list_=FXCollections.observableArrayList();
		
		motions_.setItems(motions_list_);
		
		motions_.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<Motion>() {
	                public void changed(ObservableValue<? extends Motion> ov, Motion old_val, Motion new_val) 
	                {
	                	controller_.mocapscene_.setup(new_val);
	                }
	        });
		
		Label labelm=new Label("Motions");
		labelm.setAlignment(Pos.CENTER);
		vbox.getChildren().add(labelm);
		
		vbox.getChildren().add(motions_);
		
		asf_=new FileChooser();
		asf_.setInitialDirectory(new File("./data/"));
		asf_.getExtensionFilters().add(new ExtensionFilter("ASF", "*.asf"));
		amc_=new FileChooser();
		amc_.setInitialDirectory(new File("./data/"));
		amc_.getExtensionFilters().add(new ExtensionFilter("AMC", "*.amc"));
		
		asf_l_=new Label();
		asf_l_.setAlignment(Pos.CENTER);
		asf_l_.setPrefWidth(119);
		amc_l_=new Label();
		amc_l_.setAlignment(Pos.CENTER);
		amc_l_.setPrefWidth(119);
		
		asf_b_=new Button("Choose ASF");
		asf_b_.setPrefWidth(100);
		amc_b_=new Button("Choose AMC");
		amc_b_.setPrefWidth(100);
		
		asf_b_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file = asf_.showOpenDialog(controller_.stage_);
	                    if (file != null) {
	                        asf_f_=file;
	                        asf_l_.setText(file.getName());
	                    }
	                }
	            });
		
		amc_b_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file = amc_.showOpenDialog(controller_.stage_);
	                    if (file != null) {
	                        amc_f_=file;
	                        amc_l_.setText(file.getName());
	                    }
	                }
	            });
		
		load_motion_=new Button("Load motion");
		load_motion_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    if(asf_f_!=null && amc_f_!=null)
	                    {
	                    	try 
	                    	{
	                    		ASFReader r=new ASFReader();
	                			Skeleton skeleton=r.readSkeleton(asf_f_);
	                			AMCReader ar=new AMCReader();
	                			Motion motion=ar.readMotion(amc_f_, skeleton);
	                			motions_list_.add(motion);

	                			amc_f_=null;
	                			amc_l_.setText("");
	                		} 
	                    	catch (IOException ex) 
	                    	{
	                			ex.printStackTrace();
	                		}
	                    }
	                }
	            });
		
		start_pause_=new Button("Resume/Pause animation");
		start_pause_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    controller.mocapscene_.resumePause();
	                }
	            });
		
		HBox hbox1=new HBox(5);
		hbox1.getChildren().add(asf_l_);
		hbox1.getChildren().add(asf_b_);
		hbox1.setPrefWidth(224);
		vbox.getChildren().add(hbox1);
		
		HBox hbox2=new HBox(5);
		hbox2.getChildren().add(amc_l_);
		hbox2.getChildren().add(amc_b_);
		hbox2.setPrefWidth(224);
		vbox.getChildren().add(hbox2);
		
		HBox hbox3=new HBox();
		hbox3.getChildren().add(load_motion_);
		hbox3.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox3);
		
		HBox hbox4=new HBox();
		hbox4.getChildren().add(start_pause_);
		hbox4.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox4);
		
		/*ASFReader r=new ASFReader();
		try {
			Skeleton skeleton=r.readSkeleton(new File("./data/HDM_bd.asf"));
			AMCReader ar=new AMCReader();
			Motion motion=ar.readMotion(new File("./data/HDM_bd_cartwheelLHandStart1Reps_001_120.amc"), skeleton);
			Motion motion2=ar.readMotion(new File("./data/HDM_bd_clapAboveHead5Reps_001_120.amc"), skeleton);
			motions_list_.add(motion);
			motions_list_.add(motion2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		scene_=new SubScene(root_, 224, 600, false, SceneAntialiasing.DISABLED);
		root_.getChildren().add(vbox);
		
	}
	
	public SubScene getSubScene()
	{
		return scene_;
	}
}
