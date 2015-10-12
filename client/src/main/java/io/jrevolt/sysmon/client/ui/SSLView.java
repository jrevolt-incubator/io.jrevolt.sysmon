package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.client.ui.model.SSLViewItem;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.Date;


/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
@EnableScheduling
public class SSLView extends Base<BorderPane> {

	@FXML TableView<SSLView> table;

	@FXML TableColumn<SSLViewItem, String> hostname;
	@FXML TableColumn<SSLViewItem, String> ip;
	@FXML TableColumn<SSLViewItem, Integer> port;
	@FXML TableColumn<SSLViewItem, String> commonName;
	@FXML TableColumn<SSLViewItem, String> subjectAlternativeName;
	@FXML TableColumn<SSLViewItem, String> issuer;
	@FXML TableColumn<SSLViewItem, Date> validFrom;
	@FXML TableColumn<SSLViewItem, Date> validTo;

	@FXML TextField filter;

	@Override
	protected void initialize() {
		super.initialize();

		hostname.setCellValueFactory(new PropertyValueFactory<>("hostname"));
		ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
		port.setCellValueFactory(new PropertyValueFactory<>("port"));
		commonName.setCellValueFactory(new PropertyValueFactory<>("commonName"));
		subjectAlternativeName.setCellValueFactory(new PropertyValueFactory<>("subjectAlternativeName"));
		issuer.setCellValueFactory(new PropertyValueFactory<>("issuer"));
		validFrom.setCellValueFactory(new PropertyValueFactory<>("validFrom"));
		validTo.setCellValueFactory(new PropertyValueFactory<>("validTo"));

		registerLayoutPersistor(table);

		FxHelper.async((Runnable) this::load);



	}

	void load() {}
}
