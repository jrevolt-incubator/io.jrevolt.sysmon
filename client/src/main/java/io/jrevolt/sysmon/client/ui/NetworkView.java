package io.jrevolt.sysmon.client.ui;

import static io.jrevolt.sysmon.client.ui.FxHelper.*;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import io.jrevolt.sysmon.client.ui.model.NetworkItem;
import io.jrevolt.sysmon.model.NetworkInfo;
import io.jrevolt.sysmon.rest.ApiService;

import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
@EnableScheduling
public class NetworkView extends Base<BorderPane> {

	@FXML TableView<NetworkItem> table;

	@FXML TableColumn<NetworkItem, String> cluster;
	@FXML TableColumn<NetworkItem, String> server;
	@FXML TableColumn<NetworkItem, String> destination;
	@FXML TableColumn<NetworkItem, Integer> port;
	@FXML TableColumn<NetworkItem, String> sourceIP;
	@FXML TableColumn<NetworkItem, String> destinationIP;

	@FXML TableColumn<NetworkItem, NetworkInfo.Status> status;
	@FXML TableColumn<NetworkItem, String> comment;

	@FXML TextField filter;

	@Autowired
	ApiService api;

	ScheduledFuture<?> updater;

	@Override
	protected void initialize() {

		if (updater != null) {
			updater.cancel(true);
			updater = null;
		}

		super.initialize();

		cluster.setCellValueFactory(new PropertyValueFactory<>("cluster"));
		server.setCellValueFactory(new PropertyValueFactory<>("server"));
		destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
		port.setCellValueFactory(new PropertyValueFactory<>("port"));
		sourceIP.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
		destinationIP.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

		registerLayoutPersistor(NetworkView.class, table);

		async(() ->{
			load();
			updater = FxHelper.scheduler().scheduleAtFixedRate(this::update, 1, 1, TimeUnit.SECONDS);
		});
	}

	@Override
	protected void close() {
		updater.cancel(true);
		super.close();
	}

	void load() {
		fxasync(()->pane.setCenter(new Text("Loading...")));

		final ObservableList<NetworkItem> data = FXCollections.observableArrayList();
		final FilteredList<NetworkItem> filtered = new FilteredList<>(data);
		final SortedList<NetworkItem> sorted = new SortedList<>(filtered);

		sorted.comparatorProperty().bind(table.comparatorProperty());
		filter.textProperty().addListener((observable, oldValue, newValue) -> {
			filtered.setPredicate(e->filter(e));
		});

		List<NetworkInfo> network = api.getNetworkInfo();
		network.forEach(n->{
			NetworkItem item = new NetworkItem();
			item.setCluster(n.getCluster());
			item.setServer(n.getServer());
			item.setDestination(n.getDestination());
			item.setPort(n.getPort());
			item.setSourceIP(n.getSrcAddress());
			item.setDestinationIP(n.getDstAddress());
			item.setStatus(n.getStatus());
			item.setComment(n.getComment());
			data.add(item);
		});

		fxasync(() -> {
			table.setItems(sorted);
			pane.setCenter(table);
		});
	}

	void update() {
		if (!isVisible(pane)) { return; }
	}

	@FXML
	void onFilterUpdate() {
		((FilteredList<NetworkItem>) table.getItems()).setPredicate(this::filter);
	}

	boolean filter(NetworkItem e) {
		Pattern filter = Pattern.compile("(?i).*" + StringUtils.trimToEmpty(this.filter.getText()) + ".*",
													Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		return filter.matcher(e.getCluster()).matches()
				|| filter.matcher(e.getServer()).matches()
				|| (e.getDestination() == null || filter.matcher(e.getDestination()).matches())
				|| e.getStatus() != null && filter.matcher(e.getStatus().name()).matches()
				|| e.getComment() != null && filter.matcher(e.getComment()).matches()
				;
	}
}
