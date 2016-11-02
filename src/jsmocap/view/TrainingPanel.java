package jsmocap.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import jsmocap.io.AMCReader;
import jsmocap.io.ASFReader;
import jsmocap.skeleton.Classifier;
import jsmocap.skeleton.Motion;
import jsmocap.skeleton.Skeleton;
import jsmocap.skeleton.Training;

public class TrainingPanel 
{
	private ListView<Motion> motions_;
	private ObservableList<Motion> motions_list_;

	private FileChooser batch_;
	private Button batch_b_;
	
	private Button cr_class_;
	
	private Group root_;
	private SubScene scene_;
	
	private TextField class_name_;
	
	private Controller controller_;
	
	public TrainingPanel(Controller controller)
	{
		controller_=controller;
		
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
	                	if(new_val!=null)
	                		controller_.mocapscene_.setup(new_val);
	                }
	        });
		
		Label labelm=new Label("Motions");
		labelm.setAlignment(Pos.CENTER);
		vbox.getChildren().add(labelm);
		
		vbox.getChildren().add(motions_);
		
		batch_=new FileChooser();
		batch_.setInitialDirectory(new File("./data/"));
		batch_.getExtensionFilters().add(new ExtensionFilter("DAT", "*.dat"));
		
		batch_b_=new Button("Load batch");
		batch_b_.setPrefWidth(100);
		
		batch_b_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file = batch_.showOpenDialog(controller_.stage_);
	                    if (file != null) {
	                        try 
	                        {
	                        	motions_list_.clear();
								BufferedReader br=new BufferedReader(new FileReader(file));
								String line=br.readLine();
								while(line!=null)
								{
									String parts[]=line.split(" ");
									
									ASFReader r=new ASFReader();
		                			Skeleton skeleton=r.readSkeleton(new File(parts[0]));
		                			AMCReader ar=new AMCReader();
		                			Motion motion=ar.readMotion(new File(parts[1]), skeleton);
		                			motions_list_.add(motion);
		                			
		                			line=br.readLine();
								}
								br.close();
							} 
	                        catch (FileNotFoundException e1) 
	                        {
								e1.printStackTrace();
							} 
	                        catch (IOException e1) 
	                        {
								e1.printStackTrace();
							}
	                    }
	                }
	            });
		
		cr_class_=new Button("Create Class");
		
		cr_class_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) 
	                {
	                    Training training=new Training(new ArrayList<Motion>(motions_list_));
	                    double[][] cl=training.computeClass();
	                    Classifier classifier=new Classifier(cl);
	                    if(class_name_.getText().equals(" "))
	                    	classifier.name_=System.currentTimeMillis()+"";
	                    else
	                    	classifier.name_=class_name_.getText();
	                    controller_.cpanel_.classifiers_list_.add(classifier);
	                }
	            });
		
		HBox hbox1=new HBox(5);
		hbox1.getChildren().add(batch_b_);
		hbox1.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox1);
		
		class_name_=new TextField();
		
		HBox hbox3=new HBox(5);
		hbox3.getChildren().add(new Label("Class name"));
		hbox3.getChildren().add(class_name_);
		hbox3.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox3);
		
		HBox hbox2=new HBox(5);
		hbox2.getChildren().add(cr_class_);
		hbox2.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox2);
		
		scene_=new SubScene(root_, 224, 600, false, SceneAntialiasing.DISABLED);
		root_.getChildren().add(vbox);
		
	}
	
	public SubScene getSubScene()
	{
		return scene_;
	}
}
