package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.EndpointStatus;
import io.jrevolt.sysmon.rest.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;

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

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.jrevolt.sysmon.client.ui.FxHelper.async;
import static io.jrevolt.sysmon.client.ui.FxHelper.fxasync;

/**
 * @author <a href="mailto:patrikbeno@gmail.com">Patrik Beno</a>
 */
@Component
public class DependenciesView extends Base<BorderPane> {

	@FXML TableView<Endpoint> table;

	@FXML TableColumn<Endpoint, String> cluster;
	@FXML TableColumn<Endpoint, String> server;
	@FXML TableColumn<Endpoint, URI> uri;
	@FXML TableColumn<Endpoint, EndpointStatus> status;
	@FXML TableColumn<Endpoint, String> comment;

	@FXML TextField filter;

	@Autowired
	ApiService api;

	Map<URI, Endpoint> endpointsByUri = new LinkedHashMap<>();


	@Override
	protected void initialize() {
		super.initialize();

		cluster.setCellValueFactory(new PropertyValueFactory<>("cluster"));
		server.setCellValueFactory(new PropertyValueFactory<>("server"));
		uri.setCellValueFactory(new PropertyValueFactory<>("uri"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

		// http://stackoverflow.com/questions/22732013/javafx-tablecolumn-text-wrapping
//		comment.setCellFactory(param -> {
//			TableCell<Endpoint, String> cell = new TableCell<>();
//			Text text = new Text();
//			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//			text.wrappingWidthProperty().bind(cell.widthProperty());
//			text.textProperty().bind(cell.textProperty());
//			return cell;
//		});

// todo: disabled, as it interferes with sorting/filtering
//		comment.setCellFactory(new Callback<TableColumn<Endpoint, String>, TableCell<Endpoint, String>>() {
//			@Override
//			public TableCell<Endpoint, String> call(TableColumn<Endpoint, String> param) {
//				return new TableCell<Endpoint, String>() {
//					@Override
//					protected void updateItem(String item, boolean empty) {
//						super.updateItem(item, empty);
//						if (!empty) {
//							setText(item);
//							setTooltip(new Tooltip(item));
//						}
//					}
//				};
//			}
//		});

		registerLayoutPersistor(DependenciesView.class, table);

		async((Runnable) this::load);
	}

	void load() {
		final ObservableList<Endpoint> data = FXCollections.observableArrayList();
		final FilteredList<Endpoint> filtered = new FilteredList<>(data, this::filter);
		final SortedList<Endpoint> sorted = new SortedList<>(filtered);

		DomainDef domain = api.getDomainDef();

		fxasync(()->{
			domain.getClusters().forEach(c -> c.getDependencies().forEach(e -> {
				Endpoint endpoint = new Endpoint();
				endpoint.setCluster(c.getClusterName());
//			endpoint.setServer(e.get);
				endpoint.setUri(e.getUri());
				endpoint.setStatus(e.getStatus());
				endpoint.setComment(e.getComment());
				data.add(endpoint);
				endpointsByUri.put(endpoint.getUri(), endpoint);
			}));

			sorted.comparatorProperty().bind(table.comparatorProperty());
			filter.textProperty().addListener((observable, oldvalue, newvalue) -> {
				filtered.setPredicate(this::filter);
			});

			table.setItems(sorted);
		});
	}

	@FXML
	void onFilterUpdate() {
		((FilteredList<Endpoint>) table.getItems()).setPredicate(this::filter);
	}

	boolean filter(Endpoint e) {
		Pattern filter = Pattern.compile(".*" + StringUtils.trimToEmpty(this.filter.getText()) + ".*");
		return
				e.getCluster() != null && filter.matcher(e.getCluster()).matches()
				|| e.getServer() != null && filter.matcher(e.getServer()).matches()
				|| e.getUri() != null && filter.matcher(e.getUri().toString()).matches()
				|| e.getStatus() != null && filter.matcher(e.getStatus().name()).matches()
				|| e.getComment() != null && filter.matcher(e.getComment()).matches()
				;

	}
}
