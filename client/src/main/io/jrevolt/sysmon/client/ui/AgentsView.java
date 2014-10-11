package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.VersionInfo;
import io.jrevolt.sysmon.rest.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javax.ws.rs.client.WebTarget;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import static io.jrevolt.sysmon.client.ui.FxHelper.*;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
public class AgentsView extends Base<BorderPane> {

	@FXML TableView<UIAgentInfo> table;

	@FXML TableColumn<UIAgentInfo, String> cluster;
	@FXML TableColumn<UIAgentInfo, String> server;

	@FXML TableColumn<UIAgentInfo, String> status;
	@FXML TableColumn<UIAgentInfo, LocalDateTime> lastChecked;
	@FXML TableColumn<UIAgentInfo, LocalDateTime> lastUpdated;

	@FXML TableColumn<UIAgentInfo, Duration> checked;
	@FXML TableColumn<UIAgentInfo, Duration> ping;

	@FXML TableColumn<UIAgentInfo, VersionInfo> version;
	@FXML TableColumn<UIAgentInfo, LocalDateTime> built;

	@FXML TableColumn<UIAgentInfo, UIAgentInfo> actions;

	///

	@Autowired
	ApiService rest;

	@Autowired
	WebTarget arest;

	///

	Map<String, UIAgentInfo> uiagents = new HashMap<>();

	ScheduledFuture<?> updater;

	@Override
	protected void initialize() {
		async(this::refresh);
		updater = FxHelper.scheduler().scheduleAtFixedRate(this::update, 1, 5, TimeUnit.SECONDS);
	}

	@Override
	protected void close() {
		updater.cancel(true);
		super.close();
	}

	@FXML
	void refresh() {
		List<AgentInfo> agents = rest.getAgentInfo();
//		ObservableList<UIAgentInfo> uiagents = FXCollections.observableArrayList();
		uiagents.clear();
		agents.forEach(a -> uiagents.put(a.getServer(), new UIAgentInfo(a)));
		fxasync(()->{
			table.setItems(FXCollections.observableArrayList(uiagents.values()));
			cluster.setCellValueFactory(new PropertyValueFactory<>("cluster"));
			server.setCellValueFactory(new PropertyValueFactory<>("server"));
			status.setCellValueFactory(new PropertyValueFactory<>("status"));
			lastChecked.setCellValueFactory(new PropertyValueFactory<>("lastChecked"));
			lastUpdated.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));
			checked.setCellValueFactory(new PropertyValueFactory<>("checked"));
			ping.setCellValueFactory(new PropertyValueFactory<>("ping"));
			version.setCellValueFactory(new PropertyValueFactory<>("version"));
			built.setCellValueFactory(new PropertyValueFactory<>("built"));
			actions.setCellFactory(param -> new TableCell<UIAgentInfo, UIAgentInfo>() {
				@Override
				protected void updateItem(UIAgentInfo unusued, boolean empty) {
					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						UIAgentInfo item = table.getItems().get(getIndex());
						Button bRestart = new Button("restart");
						bRestart.setOnAction(e->restartSingle(item));

						Button bCheck  = new Button("ping");
						bCheck.setOnAction(e->pingAgent(item));

						HBox box = new HBox();
						box.getChildren().addAll(bRestart, bCheck);

						setGraphic(box);
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					}
				}
			});
			Preferences prefs = Preferences.userNodeForPackage(AgentsView.class);
			table.getColumns().forEach(c -> {
				c.prefWidthProperty().set(prefs.getDouble(c.getId(), 70));
				c.widthProperty().addListener((observable, oldValue, newValue) -> {
					prefs.putDouble(c.getId(), c.getWidth());
				});
			});
		});
	}

	@FXML
	void restartSelectedAgents() {
	}

	void restartSingle(UIAgentInfo info) {
		fxasync(()->info.statusProperty().set(AgentInfo.Status.REQUESTED));
		async(()->rest.restart(info.getCluster(), info.getServer()));
	}

	void pingAgent(UIAgentInfo item) {
		async(() -> {
			try {
				fxasync(()-> item.statusProperty().set(AgentInfo.Status.CHECKING));
//				Future<AgentInfo> f = arest.path("ping").path(item.getServer()).request().async().get(AgentInfo.class);
//				AgentInfo info = f.get(10, TimeUnit.SECONDS);
				AgentInfo info = rest.ping(item.getServer(), 10, null);
				fxupdate(() -> uiagents.get(item.getServer()).update(info));
//			} catch (TimeoutException e) {
//				fxupdate(() -> uiagents.get(item.getServer()).setStatus(AgentInfo.Status.UNKNOWN));
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		});
	}

	//scheduled
	void update() {
		if (!pane.getParent().isVisible()) { return; }

		rest.getAgentInfo().forEach(a -> uiagents.get(a.getServer()).update(a));
	}
}
