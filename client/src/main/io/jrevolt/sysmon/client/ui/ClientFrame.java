package io.jrevolt.sysmon.client.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import io.jrevolt.sysmon.model.AppCfg;
import io.jrevolt.sysmon.model.ClusterDef;
import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.rest.RestService;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.UriBuilder;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import static io.jrevolt.sysmon.client.ui.FxHelper.*;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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
	Tab servers;

	@FXML
	Tab agents;

	@FXML
	TableView<Endpoint> table;

	@FXML TableColumn<Endpoint, ClusterDef> cluster;
	@FXML TableColumn<Endpoint, String> server;
	@FXML TableColumn<Endpoint, URI> endpoint;
	@FXML TableColumn<Endpoint, Endpoint.Type> type;
	@FXML TableColumn<Endpoint, Endpoint.Status> status;
	@FXML TableColumn<Endpoint, String> comment;
	@FXML TextField filter;

	DomainDef domain;

	@Override
	protected void initialize() {
		super.initialize();
		fxasync(this::refresh);
	}

	@Override
	protected void close() {
		super.close(); // todo implement this
	}

	@FXML
	void refresh() {

		agents.setContent(FxHelper.load(AgentsView.class).pane);

		async(()->{
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
			domain.getClusters().values().forEach(c->c.getDependencies().forEach(d->{
				Endpoint endpoint = new Endpoint(d, Endpoint.Type.DEPENDENCY, null, c);
				endpoints.add(endpoint);
				endpoint.status.set(Endpoint.Status.CHECKING);
				FxHelper.async(() -> check(endpoint));
			}));
			domain.getClusters().values().forEach(c->c.getProxies().forEach(p->{
				Endpoint endpoint = new Endpoint(p, Endpoint.Type.PROXY, null, c);
				endpoints.add(endpoint);
				endpoint.status.set(Endpoint.Status.CHECKING);
				FxHelper.async(() -> check(endpoint));
			}));
			fxasync(() -> {
				table.setItems(new FilteredList<>(endpoints, this::filter));
				cluster.setCellValueFactory(new PropertyValueFactory<>("clusterName"));
				server.setCellValueFactory(new PropertyValueFactory<>("server"));
				endpoint.setCellValueFactory(new PropertyValueFactory<>("uri"));
				type.setCellValueFactory(new PropertyValueFactory<>("type"));
				status.setCellValueFactory(new PropertyValueFactory<>("status"));
				comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
			});

			Preferences prefs = Preferences.userNodeForPackage(ClientFrame.class);
			table.getColumns().forEach(c-> {
				c.prefWidthProperty().set(prefs.getDouble(c.getId(), 70));
				c.widthProperty().addListener((observable, oldValue, newValue) -> {
					prefs.putDouble(c.getId(), c.getWidth());
				});
			});
		});
	}

	void check(Endpoint endpoint) {
		if (System.getProperty("enableClientCheck", "false").equals("false")) {
			fxupdate(()->endpoint.status().set(Endpoint.Status.UNDETERMINED));
			return;
		}

		ObjectProperty<Endpoint.Status> status = new SimpleObjectProperty<>(Endpoint.Status.UNDETERMINED);
		StringProperty comment = new SimpleStringProperty();
		try {
			if (endpoint.getUri().getScheme().equals("jdbc")) {
				Socket socket = new Socket();
				URL url = URI.create(endpoint.getUri().getSchemeSpecificPart()
						.replaceFirst(".*//(.*):([0-9]+).*", "http://$1:$2"))
						.toURL();
				socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), 1000);
				status.set(Endpoint.Status.OK);
				comment.set("Connected");
			} else {
				URL url = endpoint.getUri().toURL();
				String test = trimToEmpty(endpoint.getUri().getFragment()).replaceFirst(".*test=([^;]+).*", "$1");
				url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath() + test
						+ (url.getQuery() != null ? "?" + url.getQuery() : ""));
//				System.out.println(url);
				URLConnection con = url.openConnection();
				if (con instanceof HttpURLConnection) {
					if (endpoint.getUri().getFragment() != null) {
						if (endpoint.getUri().getFragment().contains("post")) {
							((HttpURLConnection) con).setRequestMethod("POST");
						}
						if (endpoint.getUri().getFragment().contains("method=")) {
							((HttpURLConnection) con).setRequestMethod(
									endpoint.getUri().getFragment().replaceFirst(".*method=([^;]+).*", "$1").toUpperCase());
						}
						if (endpoint.getUri().getFragment().contains("type=")) {
							con.setRequestProperty("Content-Type",
														  endpoint.getUri().getFragment().replaceFirst(".*type=([^;]+).*", "$1"));
						}
					}
				}
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

				comment.set(String.format("HTTP %d", code));
			}

		} catch (Exception e) {
			status.set(Endpoint.Status.ERROR);
			comment.set(e.toString());
		} finally {
			fxupdate(() -> {
				endpoint.status.set(status.get());
				endpoint.comment.set(comment.get());
				//http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
				this.status.setVisible(false);
				this.status.setVisible(true);

			});
//			System.out.printf("%-10s %s%n", status.get(), endpoint.getUri());
		}
	}

	@FXML
	void checkAll() {
		async(rest::checkAll);
	}


	@FXML
	void onFilterUpdated() {
		((FilteredList<Endpoint>) table.getItems()).setPredicate(this::filter);
	}

	boolean filter(Endpoint p) {
		if (StringUtils.isEmpty(filter.getText())) { return true; }
		Pattern pattern = Pattern.compile(".*" + filter.getText() + ".*");
		return (cluster.isVisible() && pattern.matcher(p.getClusterName()).matches())
				|| (server.isVisible() && p.getServer() != null && pattern.matcher(p.getServer()).matches())
				|| (endpoint.isVisible() && pattern.matcher(p.getUri().toString()).matches())
				|| (status.isVisible() && pattern.matcher(p.getStatus().name()).matches())
				|| (comment.isVisible() && p.getComment() != null && pattern.matcher(p.getComment()).matches())
				|| (type.isVisible() && pattern.matcher(p.getType().name()).matches())
				;
	}

	@FXML void restartServer() {
		async(() -> rest.restart("all", "all"));
	}

	@FXML
	void copyCellValue(Event e) {
		URI uri = table.getSelectionModel().getSelectedItem().getUri();
		ClipboardContent content = new ClipboardContent();
		content.putString(uri.toString());
		Clipboard.getSystemClipboard().setContent(content);
	}



}
