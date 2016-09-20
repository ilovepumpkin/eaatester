package eaa.tester.ui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import eaa.tester.conf.Configuration;

public class PlayWindow extends Stage {
	private Configuration cfg;

	public PlayWindow(Configuration cfg) {
		this.cfg = cfg;

		initModality(Modality.APPLICATION_MODAL);

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(20));

		Node cfgInfo = buildCfgDisplay();
		Node buttons = buildButtons();
		Node table = buildTable();

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
		c.add(buildCfgLine("Data source type",
				MainGUI.getDataSourceTypeLabel(cfg.getDataSourceType())));
		c.add(buildCfgLine("Broker URL", cfg.getProxyUrl()));
		c.add(buildCfgLine("Data file", cfg.getDataFilePath()));
		c.add(buildCfgLine("Interval", String.valueOf(cfg.getInterval())));
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

	private Node buildTable() {
		final TableView table = new TableView();
		return table;
	}

	private Node buildButtons() {
		Button btnAutoPlay = new Button("Play");
		btnAutoPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("hellworld");
			}
		});
		Button btnNext = new Button("Next");
		Button btnPause = new Button("Pause");
		Button btnStop = new Button("Stop");
		HBox hbBtn = new HBox();
		hbBtn.setPrefHeight(50);
		hbBtn.setSpacing(30);
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().addAll(btnAutoPlay, btnStop, btnPause, btnNext);// 将按钮控件作为子节点
		hbBtn.setPadding(new Insets(20));
		return hbBtn;
	}
}
