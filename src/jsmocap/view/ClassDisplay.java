package jsmocap.view;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ClassDisplay 
{
	private VBox root_;
	
	public ClassDisplay(Controller controller)
	{
		controller.cdisplay_=this;
		root_=new VBox(10);
	}
	
	public VBox getRoot()
	{
		return root_;
	}
	
	public void setList(ArrayList<String> classes)
	{
		root_.getChildren().clear();
		for(int i=0; i<classes.size(); ++i)
		{
			Label label=new Label(classes.get(i));
			label.setPrefWidth(150);
			label.setAlignment(Pos.CENTER);
			label.setStyle("-fx-border-width: 2;-fx-border-color: black;-fx-border-radius:10px;-fx-padding: 5;-fx-background-radius: 10, 10;-fx-background-color: blue;-fx-font-size: 14pt;-fx-font-family: \"Segoe UI Semibold\";-fx-text-fill: white;");
			root_.getChildren().add(label);
		}
	}
}
