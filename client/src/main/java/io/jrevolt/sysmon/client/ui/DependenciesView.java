package io.jrevolt.sysmon.client.ui;

import io.jrevolt.sysmon.model.DomainDef;
import io.jrevolt.sysmon.model.EndpointStatus;
import io.jrevolt.sysmon.rest.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

//	Map<URI, Endpoint> endpointsByUri = new LinkedHashMap<>();


	@Override
	protected void initialize() {
		super.initialize();

		cluster.setCellValueFactory(new PropertyValueFactory<>("cluster"));
		server.setCellValueFactory(new PropertyValueFactory<>("server"));
		uri.setCellValueFactory(new PropertyValueFactory<>("uri"));
		status.setCellValueFactory(new PropertyValueFactory<>("status"));
		comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

//		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//		table.getSelectionModel().setCellSelectionEnabled(true);

		addCopyValueMenuItem(uri);
		addCopyValueMenuItem(comment);

//		setStatusCellFactory(status);

//		table.getSelectionModel().setCellSelectionEnabled(true);
//		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


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


//	<S, T> void setStatusCellFactory(TableColumn<S, T> col) {
//		col.setCellFactory(param -> {
//			TableCell<S, T> cell = new TableCell<S, T>() {
//				@Override
//				protected void updateItem(T item, boolean empty) {
//					super.updateItem(item, empty);
//					if (item == null || empty) {
//					} else {
//						setText(item.toString());
//						if (getText().equals("OK")) { setStyle("-fx-background-color: green"); }
//						else if (getText().equals("CONNECTED")) { setStyle("-fx-background-color: green"); }
//						else if (getText().equals("ERROR")) { getStyleClass().addAll("error", "center"); }
//						else { setStyle("-fx-background-color: aliceblue"); }
//					}
//				}
//			};
//			return cell;
//		});
//	}

	<S, T> void addCopyValueMenuItem(TableColumn<S, T> col) {
		Callback<TableColumn<S, T>, TableCell<S, T>> factory = col.getCellFactory();
		col.setCellFactory(param -> {
			TableCell<S, T> cell = factory.call(param);
			addCopyValueMenuItem(cell);
			return cell;
		});
	}

	<S,T> void addCopyValueMenuItem(TableCell<S, T> cell) {
		MenuItem item = new MenuItem("Copy");
		item.setOnAction(event -> {
			ClipboardContent content = new ClipboardContent();
			content.putString(Objects.toString(cell.getText(), ""));
			Clipboard.getSystemClipboard().setContent(content);
		});
		cell.setContextMenu(new ContextMenu(item));
	}

	void load() {
		final ObservableList<Endpoint> data = FXCollections.observableArrayList();
		final FilteredList<Endpoint> filtered = new FilteredList<>(data, this::filter);
		final SortedList<Endpoint> sorted = new SortedList<>(filtered);

		DomainDef domain = api.getDomainDef();

		fxasync(()->{
			domain.getClusters().stream()
					.flatMap(c -> c.getServers().stream())
					.flatMap(s -> s.getDependencies().stream())
					.forEach(e -> {
						Endpoint endpoint = new Endpoint();
						endpoint.setCluster(e.getCluster());
						endpoint.setServer(e.getServer());
						endpoint.setUri(e.getUri());
						endpoint.setStatus(e.getStatus());
						endpoint.setComment(e.getComment());
						data.add(endpoint);
					});

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
		Pattern filter = Pattern.compile(".*" + StringUtils.trimToEmpty(this.filter.getText()) + ".*",
													Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		String s = new StringBuilder()
				.append(Objects.toString(e.getCluster())).append("\t")
				.append(Objects.toString(e.getServer())).append("\t")
				.append(Objects.toString(e.getUri())).append("\t")
				.append(Objects.toString(e.getStatus(), "")).append("\t")
				.append(Objects.toString(e.getComment(), "")).append("\t")
				.toString();
		return filter.matcher(s).matches();
	}
}
