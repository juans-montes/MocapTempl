package jsmocap.view;

import javafx.geometry.Pos;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

public class ToolboxPanel 
{
	private SubScene toolbox_;
	private Group root_;
	private TabPane panel_;
	
	private GeneralPanel gpanel_;
	private TrainingPanel tpanel_;
	private ClassifierPanel cpanel_;
	
	public ToolboxPanel(Controller controller)
	{
		root_=new Group();
		toolbox_=new SubScene(root_, 224, 600, false, SceneAntialiasing.DISABLED);
		
		panel_=new TabPane();
		
		gpanel_=new GeneralPanel(controller);
		Tab tab=new Tab();
		tab.setText("General");
		HBox hbox = new HBox();
        hbox.getChildren().add(gpanel_.getSubScene());
        hbox.setAlignment(Pos.CENTER);
        tab.setContent(hbox);
        tab.setClosable(false);
        panel_.getTabs().add(tab);
		
        tpanel_=new TrainingPanel(controller);
		Tab tab2=new Tab();
		tab2.setText("Training");
		HBox hbox2 = new HBox();
        hbox2.getChildren().add(tpanel_.getSubScene());
        hbox2.setAlignment(Pos.CENTER);
        tab2.setContent(hbox2);
        tab2.setClosable(false);
        panel_.getTabs().add(tab2);
        
        cpanel_=new ClassifierPanel(controller);
		Tab tab3=new Tab();
		tab3.setText("Classifier");
		HBox hbox3 = new HBox();
        hbox3.getChildren().add(cpanel_.getSubScene());
        hbox3.setAlignment(Pos.CENTER);
        tab3.setContent(hbox3);
        tab3.setClosable(false);
        panel_.getTabs().add(tab3);
        
		/*for (int i = 0; i < 5; i++) {
            Tab tab = new Tab();
            tab.setText("Tab" + i);
            HBox hbox = new HBox();
            hbox.getChildren().add(new Label("Tab" + i));
            hbox.setAlignment(Pos.CENTER);
            tab.setContent(hbox);
            panel_.getTabs().add(tab);
        }*/
		
		panel_.prefHeightProperty().bind(toolbox_.heightProperty());
        panel_.prefWidthProperty().bind(toolbox_.widthProperty());
		
		root_.getChildren().add(panel_);
	}
	
	public SubScene getScene()
	{
		return toolbox_;
	}
}
