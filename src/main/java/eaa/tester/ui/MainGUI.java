package eaa.tester.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainGUI extends Application {
	private final Desktop desktop = Desktop.getDesktop();

	@Override
	public void start(Stage primaryStage) throws Exception {
		// StackPane root = new StackPane();
		// root.getChildren().add(btn);
		
		

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Node proxyUrl=buildProxyUrl();
		Node fileChooser=buildFileChooser(primaryStage);
		Node interval=buildInterval();
		Node loop=buildLoop();
		Node automatic=buildAutomatic();
		Node buttons=buildButtons();
		
		BorderPane root=new BorderPane();
		VBox controlPanel=new VBox();
		controlPanel.setSpacing(10);
		controlPanel.setPadding(new Insets(20));
		controlPanel.getChildren().addAll(proxyUrl,fileChooser,interval,loop,automatic,buttons);
		
		root.setLeft(controlPanel);
		VBox displayPanel=new VBox();
		displayPanel.getChildren().add(buildTable());
		root.setCenter(displayPanel);
		
		Scene scene = new Scene(root, 600, 500);

		primaryStage.setTitle("EAA Tester");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private Node buildTable(){
		final TableView table=new TableView();
		return table;
	}
	
	private Node buildButtons(){
		Button btn = new Button("Start");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("hellworld");
			}
		});
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().add(btn);// 将按钮控件作为子节点
		return hbBtn;
	}
	
	private Node buildProxyUrl(){
		HBox box=new HBox();
		Label userName = new FieldLabel("Proxy URL:");
		TextField proxyUrlInput = new TextField();
		proxyUrlInput.setPrefWidth(300);
		proxyUrlInput.setText("http://localhost:8080/conn");
		Button testConnBtn=new Button("Test Connection");
		HBox proxyUrlHBox=new HBox();
		proxyUrlHBox.getChildren().addAll(proxyUrlInput,testConnBtn);
		box.getChildren().addAll(userName, proxyUrlHBox);
		return box;
	}
	
	private Node buildFileChooser(Window win){
		HBox box=new HBox();
		box.getChildren().add(new FieldLabel("Data file:"));
		final FileChooser fileChooser = new FileChooser();
		final Label filePathLabel=new Label();
		final Button openButton = new Button("Select data file ...");
		openButton.setOnAction((final ActionEvent e) -> {
			File file = fileChooser.showOpenDialog(win);
			if (file != null) {
				filePathLabel.setText(file.getAbsolutePath());
			}
		});
		HBox hb=new HBox();
		hb.getChildren().addAll(openButton,filePathLabel);
		box.getChildren().add(hb);
		return box;
	}
	
	private Node buildInterval(){
		HBox box=new HBox();
		box.getChildren().add(new FieldLabel("Interval:"));
		final ChoiceBox unitInput=new ChoiceBox(FXCollections.observableArrayList("Seconds","Minutes","Hours"));
		unitInput.setValue("Seconds");
		final Spinner intervalSpinner=new Spinner(1,60,1);
		intervalSpinner.setEditable(true);
		HBox intervalHBox=new HBox();
		intervalHBox.setSpacing(20);
		intervalHBox.getChildren().addAll(intervalSpinner,unitInput);
		box.getChildren().add(intervalHBox);
		return box;
	}
	
	private Node buildLoop(){
		HBox box=new HBox();
		box.getChildren().add(new FieldLabel("Loop"));
		CheckBox loopInput=new CheckBox();
		box.getChildren().add(loopInput);
		return box;
	}
	
	private Node buildAutomatic(){
		HBox box=new HBox();
		box.getChildren().add(new FieldLabel("Automatic"));
		CheckBox autoInput=new CheckBox();
		autoInput.disableProperty().set(true);
		box.getChildren().add(autoInput);
		return box;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
