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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import io.jrevolt.sysmon.rest.ApiService;

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

	@FXML TabPane tabs;
	@FXML Tab groups;
	@FXML Tab clusters;
	@FXML Tab servers;
	@FXML Tab artifacts;
	@FXML Tab endpoints;
	@FXML Tab dependencies;
	@FXML Tab proxies;
	@FXML Tab ssl;
	@FXML Tab dns;
	@FXML Tab time;
	@FXML Tab management;
	@FXML Tab agents;

	@FXML TextField statusbar;

	///

	@Autowired ApiService api;

	@Override
	protected void initialize() {
		super.initialize();
		statusbar.textProperty().bindBidirectional(FxHelper.status());
	}
	
	@FXML
	void refresh() {
		fxasync(() -> {
			endpoints.setContent(FxHelper.load(EndpointsView.class).pane);
			dependencies.setContent(FxHelper.load(DependenciesView.class).pane);
			agents.setContent(FxHelper.load(AgentsView.class).pane);
		});
	}

	@FXML
	void restartAll() {
		api.restart("all", "all");
	}

}
