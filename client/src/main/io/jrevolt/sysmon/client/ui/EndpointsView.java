package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.EndpointStatus;
import io.jrevolt.sysmon.model.EndpointType;
import io.jrevolt.sysmon.rest.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static io.jrevolt.sysmon.client.ui.FxHelper.*;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
@EnableScheduling
public class EndpointsView extends Base<BorderPane> {

	@FXML TableView<Endpoint> table;

	@FXML TableColumn<Endpoint, String> cluster;
	@FXML TableColumn<Endpoint, String> server;
	@FXML TableColumn<Endpoint, String> artifact;
	@FXML TableColumn<Endpoint, URI> uri;
	@FXML TableColumn<Endpoint, EndpointType> type;
	@FXML TableColumn<Endpoint, EndpointStatus> status;
	@FXML TableColumn<Endpoint, String> comment;

	@FXML TextField filter;

	@Autowired
	ApiService api;

	Map<URI, Endpoint> endpointsByUri = new LinkedHashMap<>();

	ScheduledFuture<?> updater;

	@Override
	protected void initialize() {
		super.initialize();

		cluster.setCellValueFactory(new PropertyValueFactory<>("cluster"));
		server.setCellValueFactory(new PropertyValueFactory<>("server"));
		artifact.setCellValueFactory(new PropertyValueFactory<>("artifact"));
		uri.setCellValueFactory(new PropertyValueFactory<>("uri"));
		type.setCellValueFactory(new PropertyValueFactory<>("type"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

		registerLayoutPersistor(EndpointsView.class, table);

		async(() ->{
			load();
			updater = FxHelper.scheduler().scheduleAtFixedRate(this::update, 1, 1, TimeUnit.SECONDS);
		});
	}

	@Override
	protected void close() {
		updater.cancel(true);
		super.close(); // todo implement this
	}

	void load() {
		ObservableList<Endpoint> endpoints = FXCollections.observableArrayList();
		DomainDef domain = api.getDomainDef();
		domain.getClusters().forEach(c -> c.getProvides().forEach(e -> {
			Endpoint endpoint = new Endpoint();
			endpoint.setCluster(c.getName());
//			endpoint.setServer(e.);
			endpoint.setUri(e.getUri());
			endpoint.setStatus(e.getStatus());
			endpoint.setComment(e.getComment());
			endpoints.add(endpoint);
			endpointsByUri.put(endpoint.getUri(), endpoint);
		}));
		fxasync(() -> table.setItems(new FilteredList<>(endpoints, this::filter)));
	}

	void update() {
		DomainDef domain = api.getDomainDef();
		fxasync(() -> {
			domain.getClusters().forEach(c -> c.getServers().forEach(s -> c.getProvides().forEach(e -> {
				Endpoint endpoint = endpointsByUri.get(e.getUri());
				endpoint.setStatus(e.getStatus());
				endpoint.setComment(e.getComment());
			})));
			//http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
			status.setVisible(false);
			status.setVisible(true);
		});
	}

	@FXML
	void onFilterUpdate() {
		((FilteredList<Endpoint>) table.getItems()).setPredicate(this::filter);
	}

	boolean filter(Endpoint e) {
		Pattern filter = Pattern.compile(".*" + StringUtils.trimToEmpty(this.filter.getText()) + ".*");
		return e.getUri() != null && filter.matcher(e.getUri().toString()).matches()
				|| e.getStatus() != null && filter.matcher(e.getStatus().name()).matches()
				|| e.getComment() != null && filter.matcher(e.getComment()).matches()
				;

	}
}
