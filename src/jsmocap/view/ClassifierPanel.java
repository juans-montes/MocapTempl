package jsmocap.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.vecmath.Point2i;

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
import jsmocap.skeleton.Annotation;
import jsmocap.skeleton.Classifier;
import jsmocap.skeleton.Motion;

public class ClassifierPanel 
{
	private ListView<Motion> motions_;

	private ListView<Classifier> classifiers_;
	public ObservableList<Classifier> classifiers_list_;
	
	private Group root_;
	private SubScene scene_;
	
	private Button classify_;
	
	private FileChooser save_class_c_;
	private Button save_class_;
	private Button load_class_;
	
	private Button clear_classes_;
	
	private Classifier selected_;
	private Motion selected_m_;
	
	private Controller controller_;
	
	public ClassifierPanel(Controller controller)
	{
		controller_=controller;
		
		controller_.cpanel_=this;
		
		VBox vbox = new VBox(5);
		root_=new Group();
		
		motions_=new ListView<Motion>();
		
		motions_.setPrefWidth(224);
		motions_.setPrefHeight(224);
		
		motions_.setItems(controller.gpanel_.motions_list_);
		
		motions_.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<Motion>() {
	                public void changed(ObservableValue<? extends Motion> ov, Motion old_val, Motion new_val) 
	                {
	                	controller_.mocapscene_.setup(new_val);
	                	selected_m_=new_val;
	                }
	        });
		
		classifiers_=new ListView<Classifier>();
		
		classifiers_.setPrefWidth(224);
		classifiers_.setPrefHeight(124);
		
		classifiers_list_=FXCollections.observableArrayList();
		classifiers_.setItems(classifiers_list_);
		
		classifiers_.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<Classifier>() {
	                public void changed(ObservableValue<? extends Classifier> ov, Classifier old_val, Classifier new_val) 
	                {
	                	selected_=new_val;
	                }
	        });
		
		Label labelm=new Label("Motions");
		labelm.setAlignment(Pos.CENTER);
		vbox.getChildren().add(labelm);
		
		vbox.getChildren().add(motions_);
		
		Label label=new Label("Classes");
		label.setAlignment(Pos.CENTER);
		vbox.getChildren().add(label);
		
		vbox.getChildren().add(classifiers_);
		
		classify_=new Button("Classify Motion");
		classify_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                	selected_m_.annotations_.clear();
	                	for(int i=0; i<classifiers_list_.size(); ++i)
	                	{
		                	ArrayList<Point2i> intervals=classifiers_list_.get(i).classify(selected_m_, 0.1, 0.03);
		                    Annotation annotation=new Annotation();
		                    annotation.name_=classifiers_list_.get(i).name_;
		                    annotation.windows_=intervals;
		                    selected_m_.annotations_.add(annotation);
	                	}
	                }
	            });
		
		HBox hbox1=new HBox();
		hbox1.getChildren().add(classify_);
		hbox1.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox1);
		
		save_class_=new Button("Save Class");
		save_class_c_=new FileChooser();
		save_class_c_.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT", "*.dat"));
		save_class_c_.setInitialDirectory(new File("./data/"));
		
		save_class_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file=save_class_c_.showSaveDialog(controller_.stage_);
	                    
	                    if(file!=null && selected_!=null)
	                    {
		                    try 
		                    {
								PrintWriter pw=new PrintWriter(file);
								if(selected_!=null)
								{
									double[][] matrix=selected_.template_;
									
									pw.println(selected_.name_);
									pw.println(matrix.length+" "+matrix[0].length);
									
									for(int i=0; i<matrix.length; ++i)
									{
										pw.print(matrix[i][0]);
										for(int j=1; j<matrix[0].length; ++j)
											pw.print(" "+matrix[i][j]);
										pw.println();
									}
								}
								pw.close();
							} 
		                    catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                    }
	                }
	            });
		
		load_class_=new Button("Load Class");
		
		load_class_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file=save_class_c_.showOpenDialog(controller_.stage_);
	                    if(file!=null)
	                    {
		                    try 
		                    {
								BufferedReader br=new BufferedReader(new FileReader(file));
								
								String name=br.readLine();
								String[] dimension=br.readLine().split(" ");
								int h=Integer.parseInt(dimension[0]);
								int w=Integer.parseInt(dimension[1]);
								
								double[][] matrix=new double[h][w];
								
								for(int i=0; i<matrix.length; ++i)
								{
									String[] parts=br.readLine().split(" ");
									for(int j=1; j<matrix[0].length; ++j)
										matrix[i][j]=Double.parseDouble(parts[j]);
								}
								
								br.close();
								
								Classifier classifier=new Classifier(matrix);
								classifier.name_=name;
								classifiers_list_.add(classifier);
							} 
		                    catch (IOException e1) 
		                    {
								e1.printStackTrace();
							}
	                    }
	                }
	            });
		
		clear_classes_=new Button("Clear Classes");
		
		clear_classes_.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    classifiers_list_.clear();
	                }
	            });
		
		HBox hbox2=new HBox();
		hbox2.getChildren().add(save_class_);
		hbox2.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox2);
		
		HBox hbox3=new HBox();
		hbox3.getChildren().add(load_class_);
		hbox3.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox3);
		
		HBox hbox4=new HBox();
		hbox4.getChildren().add(clear_classes_);
		hbox4.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox4);
		
		scene_=new SubScene(root_, 224, 600, false, SceneAntialiasing.DISABLED);
		root_.getChildren().add(vbox);
		
	}
	
	public SubScene getSubScene()
	{
		return scene_;
	}
}