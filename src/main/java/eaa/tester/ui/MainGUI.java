package eaa.tester.ui;

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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

		// proxy url
		Label userName = new Label("Proxy URL:");
		grid.add(userName, 0, 1);
		TextField proxyUrlInput = new TextField();
		proxyUrlInput.setPrefWidth(300);
		proxyUrlInput.setText("http://localhost:8080/conn");
		Button testConnBtn=new Button("Test Connection");
		HBox proxyUrlHBox=new HBox();
		proxyUrlHBox.getChildren().addAll(proxyUrlInput,testConnBtn);
		grid.add(proxyUrlHBox, 1, 1);

		// file chooser
		grid.add(new Label("Data file:"), 0, 2);
		final FileChooser fileChooser = new FileChooser();
		final Label filePathLabel=new Label();
		final Button openButton = new Button("Select data file ...");
		openButton.setOnAction((final ActionEvent e) -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				filePathLabel.setText(file.getAbsolutePath());
			}
		});
		HBox hb=new HBox();
		hb.getChildren().addAll(openButton,filePathLabel);
		grid.add(hb, 1, 2);
		
		// interval 
		grid.add(new Label("Interval:"), 0, 3);
		final ChoiceBox unitInput=new ChoiceBox(FXCollections.observableArrayList("Seconds","Minutes","Hours"));
		unitInput.setValue("Seconds");
		final Spinner intervalSpinner=new Spinner(1,60,1);
		intervalSpinner.setEditable(true);
		HBox intervalHBox=new HBox();
		intervalHBox.setSpacing(20);
		intervalHBox.getChildren().addAll(intervalSpinner,unitInput);
		grid.add(intervalHBox, 1, 3);
		
		// loop
		grid.add(new Label("Loop"), 0, 4);
		CheckBox loopInput=new CheckBox();
		grid.add(loopInput, 1, 4);
		
		// automatic
		grid.add(new Label("Automatic"), 0, 5);
		CheckBox autoInput=new CheckBox();
		autoInput.disableProperty().set(true);
		grid.add(autoInput, 1, 5);
		
		// start button
		Button btn = new Button("Start");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("hellworld");
			}
		});
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);// 将按钮控件作为子节点
		grid.add(hbBtn, 1, 6);// 将HBox pane放到grid中的第1列，第4行

		
		// table view
		final TableView table=new TableView();
		
		VBox root=new VBox();
		root.getChildren().addAll(grid,table);
		
		Scene scene = new Scene(root, 600, 500);

		primaryStage.setTitle("EAA Tester");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
