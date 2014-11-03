package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.AgentInfo;
import io.jrevolt.sysmon.model.VersionInfo;
import io.jrevolt.sysmon.rest.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.ws.rs.client.WebTarget;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

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
	@FXML TableColumn<UIAgentInfo, Duration> ping;
	@FXML TableColumn<UIAgentInfo, Duration> lastModified;

	@FXML TableColumn<UIAgentInfo, VersionInfo> version;
	@FXML TableColumn<UIAgentInfo, LocalDateTime> built;

	@FXML TableColumn<UIAgentInfo, UIAgentInfo> actions;

	@FXML TextField filter;

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
		if (updater != null) {
			updater.cancel(true);
			updater = null;
		}

		super.initialize();

		pane.setCenter(new Text("Loading..."));
		async(()-> {
			refresh();
			updater = FxHelper.scheduler().scheduleAtFixedRate(this::update, 1, 5, TimeUnit.SECONDS);
			fxasync(() -> pane.setCenter(table));
		});
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
			cluster.setCellValueFactory(new PropertyValueFactory<>("cluster"));
			server.setCellValueFactory(new PropertyValueFactory<>("server"));
			status.setCellValueFactory(new PropertyValueFactory<>("status"));
			lastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
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

			registerLayoutPersistor(AgentsView.class, table);


			final ObservableList<UIAgentInfo> data = FXCollections.observableArrayList(uiagents.values());
			final FilteredList<UIAgentInfo> filtered = new FilteredList<>(data, this::filter);
			final SortedList<UIAgentInfo> sorted = new SortedList<>(filtered);
			
			sorted.comparatorProperty().bind(table.comparatorProperty());
			
			table.setItems(sorted);

			filter.textProperty().addListener((observable, oldvalue, newvalue) -> {
				filtered.setPredicate(this::filter);
			});

//			filter.getScene().addMnemonic(new Mnemonic(filter, new KeyCodeCombination(KeyCode.ESCAPE)));

		});
	}

	@FXML
	void restartSelectedAgents() {
	}

	void restartSingle(UIAgentInfo info) {
		fxasync(()->info.statusProperty().set(AgentInfo.Status.REQUESTED));
		async(()->rest.restartAgent(info.getCluster(), info.getServer()));
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
		if (!isVisible(pane)) { return; }

		Instant now = Instant.now();
		fxasync(() -> FxHelper.status().set("Updating..."));

		try {
			rest.getAgentInfo().forEach(a -> uiagents.get(a.getServer()).update(a));

			fxasync(()->FxHelper.status().set("Ready. Last update in "+Duration.between(now, Instant.now())));
		} catch (Exception e) {
			e.printStackTrace();
			fxasync(() -> FxHelper.status().set("Error: " + e.toString()));
		}
	}

	boolean filter(UIAgentInfo e) {
		Pattern filter = Pattern.compile("(?i).*" + StringUtils.trimToEmpty(this.filter.getText()) + ".*");
		return filter.matcher(e.getCluster()).matches()
				|| filter.matcher(e.getServer()).matches()
				|| e.getStatus() != null && filter.matcher(e.getStatus().name()).matches()
				;

	}

}
