package eaa.tester.ui;

import java.awt.Desktop;
import java.awt.Paint;
import java.io.File;

import eaa.tester.conf.Configuration;
import eaa.tester.data.provider.DataProviderFactory;
import eaa.tester.exception.EAATesterException;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainGUI extends Application {
	private final Desktop desktop = Desktop.getDesktop();
	private BooleanProperty fileChooserVisibleProperty;
	private BooleanProperty intervalVisibleProperty;
	private BooleanProperty loopVisibleProperty;
	private int dataSourceType = 0;
	private StringProperty proxyUrlProperty;
	private String filePath;
	private ReadOnlyObjectProperty intervalProperty;
	private ReadOnlyObjectProperty loopProperty;
	private ObjectProperty unitProperty;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// StackPane root = new StackPane();
		// root.getChildren().add(btn);

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Node dataSourceType = buildDataSourceType();
		Node brokerUrl = buildBrokerUrl();
		Node fileChooser = buildFileChooser(primaryStage);
		Node interval = buildInterval();
		Node loop = buildLoop();
		Node buttons = buildButtons();

		BorderPane root = new BorderPane();
		VBox controlPanel = new VBox();
		controlPanel.setPadding(new Insets(20));
		controlPanel.setAlignment(Pos.TOP_CENTER);

		VBox fieldsVBox = new VBox();
		fieldsVBox.setSpacing(10);
		fieldsVBox.setPadding(new Insets(20));
		fieldsVBox.getChildren().addAll(dataSourceType, brokerUrl, fileChooser,
				interval, loop);
		// fieldsVBox.setStyle("-fx-border-color:red; -fx-background-color: blue;");

		controlPanel.getChildren().addAll(fieldsVBox, buttons);

		root.setTop(controlPanel);

		Scene scene = new Scene(root, 600, 500);

		primaryStage.setTitle("EAA Tester");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Configuration buildCfg() {
		Configuration cfg = new Configuration();

		cfg.setDataSourceType(dataSourceType);
		cfg.setProxyUrl(proxyUrlProperty.get());
		cfg.setDataFilePath(filePath);
		
		final int uiInterval=Integer
				.parseInt(intervalProperty.getValue().toString());
		final String unit=unitProperty.getValue().toString();
		
		int interval=0;
		switch(unit){
		case "Seconds":interval=uiInterval*1000;break;
		case "Minutes":interval=uiInterval*60*1000;break;
		case "Hours":interval=uiInterval*60*24*1000;break;
		default:
			throw new EAATesterException("Unknown unit: "+unit);
		}
		
		cfg.setInterval(interval);
		cfg.setLoopCount(Integer.parseInt(loopProperty.getValue().toString()));

		return cfg;
	}

	public static String getDataSourceTypeLabel(int type) {
		switch (type) {
		case 0:
			return "Simple file";
		case 1:
			return "Timeseries file";
		case 2:
			return "Manual input";
		}
		return null;
	}

	private Node buildButtons() {
		Button btnLaunch = new Button("Launch");
		btnLaunch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new PlayWindow(buildCfg()).show();
			}
		});
		
		Button btnClose = new Button("Close");
		btnClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		HBox hbBtn = new HBox();
		hbBtn.setPrefHeight(50);
		hbBtn.setSpacing(30);
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().addAll(btnLaunch,btnClose);// 将按钮控件作为子节点
		return hbBtn;
	}

	private Node buildBrokerUrl() {
		HBox box = new HBox();
		Label userName = new FieldLabel("Broker URL:");
		TextField proxyUrlInput = new TextField();
		proxyUrlInput.setPrefWidth(300);
		proxyUrlInput.setText("http://localhost:8080/conn");
		proxyUrlProperty = proxyUrlInput.textProperty();
		Button testConnBtn = new Button("Test Connection");
		HBox proxyUrlHBox = new HBox();
		proxyUrlHBox.getChildren().addAll(proxyUrlInput, testConnBtn);
		box.getChildren().addAll(userName, proxyUrlHBox);
		return box;
	}

	private Node buildFileChooser(Window win) {
		HBox box = new HBox();
		box.getChildren().add(new FieldLabel("Data file:"));
		final FileChooser fileChooser = new FileChooser();
		final Label filePathLabel = new Label();
		final Button openButton = new Button("Select data file ...");
		openButton.setOnAction((final ActionEvent e) -> {
			File file = fileChooser.showOpenDialog(win);
			if (file != null) {
				filePathLabel.setText(file.getAbsolutePath());
				filePath = file.getAbsolutePath();
			}
		});
		VBox hb = new VBox();
		hb.getChildren().addAll(openButton, filePathLabel);
		box.getChildren().add(hb);

		fileChooserVisibleProperty = box.visibleProperty();
		box.managedProperty().bind(fileChooserVisibleProperty);

		return box;
	}

	private Node buildInterval() {
		HBox box = new HBox();
		box.getChildren().add(new FieldLabel("Interval:"));
		final ChoiceBox unitInput = new ChoiceBox(
				FXCollections
						.observableArrayList("Seconds", "Minutes", "Hours"));
		unitInput.setValue("Seconds");
		final Spinner intervalSpinner = new Spinner(1, 60, 1);
		intervalSpinner.setEditable(true);
		HBox intervalHBox = new HBox();
		intervalHBox.setSpacing(20);
		intervalHBox.getChildren().addAll(intervalSpinner, unitInput);
		box.getChildren().add(intervalHBox);

		intervalVisibleProperty = box.visibleProperty();
		box.managedProperty().bind(intervalVisibleProperty);

		intervalProperty = intervalSpinner.valueProperty();
		unitProperty=unitInput.valueProperty();

		return box;
	}

	private Node buildLoop() {
		HBox box = new HBox();
		box.getChildren().add(new FieldLabel("Loop count:"));
		final Spinner loopSpinner = new Spinner(1, 60, 1);
		loopSpinner.setEditable(true);
		box.getChildren().add(loopSpinner);

		loopVisibleProperty = box.visibleProperty();
		box.managedProperty().bind(loopVisibleProperty);

		loopProperty = loopSpinner.valueProperty();

		return box;
	}

	private Node buildDataSourceType() {
		HBox box = new HBox();
		box.getChildren().add(new FieldLabel("Data source type:"));

		final ToggleGroup group = new ToggleGroup();

		// simple file
		RadioButton rbSimpleFile = new RadioButton("Simple file");
		rbSimpleFile.setToggleGroup(group);
		rbSimpleFile.setSelected(true);
		rbSimpleFile.setPadding(new Insets(5));
		rbSimpleFile.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						if (newValue) {
							dataSourceType = DataProviderFactory.TYPE_SIMPLE_FILE;
						}
					}

				});

		// timeseries file
		RadioButton rbTSFile = new RadioButton("Timeseries file");
		rbTSFile.setPadding(new Insets(5));
		rbTSFile.setToggleGroup(group);
		rbTSFile.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				intervalVisibleProperty.set(!newValue.booleanValue());
				if (newValue) {
					dataSourceType = DataProviderFactory.TYPE_TIMESERIES_FILE;
				}
			}

		});

		// manual input
		RadioButton rbManual = new RadioButton("Manual input");
		rbManual.setPadding(new Insets(5));
		rbManual.setToggleGroup(group);
		rbManual.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				intervalVisibleProperty.set(!newValue.booleanValue());
				fileChooserVisibleProperty.set(!newValue.booleanValue());
				loopVisibleProperty.set(!newValue.booleanValue());
				if (newValue) {
					dataSourceType = DataProviderFactory.TYPE_MANUAL;
				}
			}

		});

		HBox radioHBox = new HBox();
		radioHBox.getChildren().addAll(rbSimpleFile, rbTSFile, rbManual);
		box.getChildren().add(radioHBox);

		return box;
	}
	
	@Override
	public void stop(){
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
