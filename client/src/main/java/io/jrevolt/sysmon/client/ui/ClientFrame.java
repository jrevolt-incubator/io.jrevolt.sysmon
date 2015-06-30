package io.jrevolt.sysmon.client.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jrevolt.sysmon.rest.ApiService;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static io.jrevolt.sysmon.client.ui.FxHelper.*;

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
	@FXML Tab dns;
	@FXML Tab network;
	@FXML Tab artifacts;
	@FXML Tab endpoints;
	@FXML Tab dependencies;
	@FXML Tab proxies;
	@FXML Tab ssl;
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
			network.setContent(FxHelper.load(NetworkView.class).pane);
			endpoints.setContent(FxHelper.load(EndpointsView.class).pane);
			dependencies.setContent(FxHelper.load(DependenciesView.class).pane);
			agents.setContent(FxHelper.load(AgentsView.class).pane);
			ssl.setContent(FxHelper.load(SSLView.class).pane);
		});
	}

	@FXML
	void restartServer() {
		api.restart();
	}

	@FXML
	void restartAgents() {
		api.restartAgent("all", "all");
	}

	@FXML
	void checkAllClusters() {
		async(() -> {
			api.checkAll();
			fxasync(()->status().setValue("Sent request for checking all clusters..."));
		});
	}

	@FXML
	void copyImage() throws IOException {
		Tab tab = tabs.getTabs().stream().filter(Tab::isSelected).findFirst().get();
		Node content = tab.getContent();
		if (content instanceof BorderPane) {
			content = ((BorderPane) content).getCenter();
		}


		WritableImage snapshot = content.snapshot(new SnapshotParameters(), null);
		ClipboardContent clip = new ClipboardContent();
		clip.putImage(snapshot);
		Clipboard.getSystemClipboard().setContent(clip);
//		BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
//		ImageIO.write(image, "png", new File("c:/users/patrik/out.png"));
	}
}
