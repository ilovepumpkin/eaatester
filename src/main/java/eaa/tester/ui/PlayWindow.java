package eaa.tester.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import eaa.tester.conf.Configuration;
import eaa.tester.data.DataLine;
import eaa.tester.data.provider.DataProvider;
import eaa.tester.data.provider.DataProviderFactory;
import eaa.tester.event.DataLineChangeEvent;
import eaa.tester.event.EAAEventBus;
import eaa.tester.player.DataPlayer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PlayWindow extends Stage {
	private Configuration cfg;
	private DataPlayer player;
	private TableView table;
	private List<String> fieldNames;
	private Label infoLabel;
	
	private static final String COLUMN_TIME="[time]";

	public PlayWindow(Configuration cfg) {
		this.cfg = cfg;

		EAAEventBus.getInstance().register(this);
		DataProvider dp = DataProviderFactory.getDataProvider(this.cfg);
		player = new DataPlayer(dp);
		fieldNames = dp.getFieldNames();

		initModality(Modality.APPLICATION_MODAL);

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(20));

		Node cfgInfo = buildCfgDisplay();
		Node buttons = buildButtons();
		Node table = buildTable(fieldNames);

		root.setTop(cfgInfo);
		root.setCenter(table);
		root.setBottom(buttons);

		Scene scene = new Scene(root, 300, 275);
		setTitle("Player Window");
		setScene(scene);
	}

	private Node buildCfgDisplay() {
		VBox box = new VBox();
		ObservableList c = box.getChildren();
		c.add(buildCfgLine("Data source type", MainGUI.getDataSourceTypeLabel(cfg.getDataSourceType())));
		c.add(buildCfgLine("Broker URL", cfg.getProxyUrl()));
		c.add(buildCfgLine("Data file", cfg.getDataFilePath()));
		c.add(buildCfgLine("Interval(ms)", String.valueOf(cfg.getInterval())));
		c.add(buildCfgLine("Loop count", String.valueOf(cfg.getLoopCount())));
		return box;
	}

	private Node buildCfgLine(String label, String value) {
		HBox box = new HBox();
		box.setPadding(new Insets(5));
		FieldLabel cLabel = new FieldLabel(label + ":");
		Label cValue = new Label(value);
		box.getChildren().addAll(cLabel, cValue);
		return box;
	}

	private Node buildTable(List<String> columnNames) {
		table = new TableView();
		//table.getColumns().add(new TableColumn("Time"));
		columnNames.add(0,COLUMN_TIME);
		columnNames.stream().forEach(name -> {
			TableColumn col = new TableColumn(name);
			col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataLine,String>,ObservableValue<String>>(){                   
			       public ObservableValue<String> call(TableColumn.CellDataFeatures<DataLine, String> param) {                                                                                             
			            return new SimpleStringProperty(param.getValue().get(name));                       
			        }                   
			    });
			table.getColumns().add(col);
		});

		return table;
	}

	private Node buildButtons() {
		Button btnAutoPlay = new Button("Play");
		btnAutoPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				player.play();
			}
		});
		btnAutoPlay.disableProperty().bind(player.getIsStoppedProperty().not());
		
		Button btnNext = new Button("Next");
		btnNext.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				player.next();
			}
		});
		btnNext.disableProperty().bind(player.getIsStoppedProperty().not());
		
		Button btnPause = new Button("Pause");
		btnPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				player.pause();
			}
		});
		btnPause.disableProperty().bind(player.getIsStoppedProperty());
		
		Button btnStop = new Button("Stop");
		btnStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				player.stop();
			}
		});
		btnStop.disableProperty().bind(player.getIsStoppedProperty());
		
		Button btnClear = new Button("Clear");
		btnClear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				table.getItems().clear();
			}
		});
		btnClear.disableProperty().bind(player.getIsStoppedProperty().not());
		
		infoLabel=new Label();
		infoLabel.setPrefWidth(300);
		updateInfo();
		
		HBox hbBtn = new HBox();
		hbBtn.setPrefHeight(50);
		hbBtn.setSpacing(30);
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().addAll(infoLabel,btnAutoPlay, btnStop, btnPause, btnNext,btnClear);// 将按钮控件作为子节点
		hbBtn.setPadding(new Insets(20));
		return hbBtn;
	}

	private void updateInfo() {
		StringBuilder sb=new StringBuilder();
		sb.append("Current item: "+player.current()+"/"+player.total());
		sb.append("         ");
		sb.append("Current loop: "+player.currentLoop()+"/"+player.loopCount());
		infoLabel.setText(sb.toString());
	}

	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(Calendar.getInstance().getTime());
	}

	@Subscribe
	public void handlePublishedData(DataLineChangeEvent dataEvent) {
		final DataLine dl = dataEvent.getDataLine();
		dl.put(COLUMN_TIME, getCurrentTime());		
		table.getItems().add(0,dl);
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				updateInfo();		
			}
		});
	}
}
