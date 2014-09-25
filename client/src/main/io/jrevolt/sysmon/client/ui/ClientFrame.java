package io.jrevolt.sysmon.client.ui;

import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import io.jrevolt.sysmon.model.AppCfg;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.NodeDef;
import io.jrevolt.sysmon.rest.RestService;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.UriBuilder;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import static io.jrevolt.sysmon.client.ui.FxHelper.fxasync;
import static io.jrevolt.sysmon.client.ui.FxHelper.fxupdate;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 * @version $Id$
 */
@Component
public class ClientFrame extends Base<BorderPane> {

	@Autowired
	StandardEnvironment env;

	@Autowired
	AppCfg app;

	@Autowired
	RestService rest;

	@FXML
	TableView<Endpoint> table;

	@FXML TableColumn<Endpoint, ClusterDef> cluster;
	@FXML TableColumn<Endpoint, String> server;
	@FXML TableColumn<Endpoint, URI> endpoint;
	@FXML TableColumn<Endpoint, Endpoint.Type> type;
	@FXML TableColumn<Endpoint, Endpoint.Status> status;
	@FXML TextField filter;

	DomainDef domain;

	@Override
	protected void initialize() {
		super.initialize();
		fxasync(this::refresh);
	}

	@FXML
	void refresh() {
		domain = rest.getDomainDef();

		ObservableList<Endpoint> endpoints = FXCollections.observableArrayList();
		domain.getClusters().values().forEach(c-> c.getServers().forEach(s-> c.getProvides().forEach(u->{
			Endpoint endpoint = new Endpoint(
					UriBuilder.fromUri(u).host(s).build(),
					Endpoint.Type.PROVIDED, s, c);
			endpoints.add(endpoint);
			endpoint.status.set(Endpoint.Status.CHECKING);
			FxHelper.async(() -> check(endpoint));
		})));
		table.setItems(new FilteredList<>(endpoints, p->true));

		try {
			cluster.setCellValueFactory(new PropertyValueFactory<>("clusterName"));
			server.setCellValueFactory(new PropertyValueFactory<>("server"));
			endpoint.setCellValueFactory(new PropertyValueFactory<>("uri"));
			type.setCellValueFactory(new PropertyValueFactory<>("type"));
			status.setCellValueFactory(new PropertyValueFactory<>("status"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Preferences prefs = Preferences.userNodeForPackage(ClientFrame.class);
		table.getColumns().forEach(c-> {
			c.prefWidthProperty().set(prefs.getDouble(c.getId(), 70));
			c.widthProperty().addListener((observable, oldValue, newValue) -> {
				prefs.putDouble(c.getId(), c.getWidth());
			});
		});
	}

	void check(Endpoint endpoint) {
		ObjectProperty<Endpoint.Status> status = new SimpleObjectProperty<>(Endpoint.Status.UNDETERMINED);
		try {
			URL url = endpoint.getUri().toURL();
			URLConnection con = url.openConnection();
			int code = con instanceof HttpURLConnection ? ((HttpURLConnection) con).getResponseCode() : 0;

			switch (code) {
				case 200:
				case 302:
				case 401:
					status.set(Endpoint.Status.OK); break;
				case 404:
					status.set(Endpoint.Status.UNAVALABLE); break;
				case 500:
					status.set(Endpoint.Status.ERROR); break;
			}

		} catch (Exception e) {
			status.set(Endpoint.Status.ERROR);
		} finally {
			fxupdate(() -> {
				endpoint.status.set(status.get());
				//http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
				this.status.setVisible(false);
				this.status.setVisible(true);
			});
			System.out.printf("%-10s %s%n", status.get(), endpoint.getUri());
		}
	}

	@FXML
	void onFilterUpdated() {
		((FilteredList<Endpoint>) table.getItems()).setPredicate(p-> {
			Pattern pattern = Pattern.compile(".*" + filter.getText() + ".*");
			return pattern.matcher(p.getClusterName()).matches()
					|| pattern.matcher(p.getServer()).matches()
					|| pattern.matcher(p.getServer()).matches()
					|| pattern.matcher(p.getUri().toString()).matches()
					|| pattern.matcher(p.getStatus().name()).matches()
					|| pattern.matcher(p.getType().name()).matches();
		});
	}
}
