package ch.akros.cc.elastic.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.akros.cc.elastic.connection.ClientConnectionFactory;
import ch.akros.cc.elastic.connection.IClientConnection;
import ch.akros.cc.elastic.util.DialogUtil;
import ch.akros.cc.elastic.util.JsonUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Patrick on 09.05.2017.
 */
public class SimpleElasticController implements Initializable, ISimpleElasticController {

	@FXML
	private TextField server;

	@FXML
	private Button connect;

	@FXML
	private TextField query;

	@FXML
	private TextField searchIndex;

	@FXML
	private TextField searchType;

	@FXML
	private Button search;

	@FXML
	private TextArea queryResult;

	@FXML
	private TextField insertIndex;

	@FXML
	private TextField insertType;

	@FXML
	private Button insert;

	@FXML
	private TextArea content;

	private static final Logger LOG = LoggerFactory.getLogger(SimpleElasticController.class);

	private boolean connected;
	private IClientConnection clientConnection;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		enableServerConnection();
		disableTooling();
	}

	@Override
	public void doConnect(final ActionEvent event) {
		if (connected) {
			CompletableFuture.runAsync(() -> {
				getScene().setCursor(Cursor.WAIT);
				clientConnection.disconnect();
			}).thenAccept(v -> {
				connected = false;
				getScene().setCursor(Cursor.DEFAULT);
				Platform.runLater(() -> {
					connect.setText("Connect");
					enableServerConnection();
					disableTooling();
				});
			});
		} else {
			CompletableFuture.supplyAsync(() -> {
				try {
					getScene().setCursor(Cursor.WAIT);
					ClientConnectionFactory.init(server.getText());
				} catch (MalformedURLException | UnknownHostException e) {
					throw new CompletionException(e);
				}
				return ClientConnectionFactory.getClientConnection();
			}).exceptionally(t -> {
				LOG.error("Cannot connect to Elasticsearch", t);
				getScene().setCursor(Cursor.DEFAULT);
				return null;
			}).thenAccept(cc -> {
				clientConnection = cc;
				connected = true;
				getScene().setCursor(Cursor.DEFAULT);
				Platform.runLater(() -> {
					connect.setText("Disconnect");
					disableServerConnection();
					enableTooling();
				});
			});
		}
	}

	@Override
	public void doSearch(final ActionEvent event) {
		if (query.getText().isEmpty()) {
			Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR, "Query is empty"));
		}
		CompletableFuture.supplyAsync(() -> {
			String[] indices = new String[0];
			String[] types = new String[0];
			if (!searchIndex.getText().isEmpty()) {
				indices = searchIndex.getText().split("\\s*,\\s*");
			}
			if (!searchType.getText().isEmpty()) {
				types = searchType.getText().split("\\s*,\\s*");
			}
			return clientConnection.search(indices, types, query.getText());
		}).exceptionally(t -> {
			Platform.runLater(() -> queryResult.setText(t.getMessage()));
			return null;
		}).thenAccept(r -> {
			if (r.getHits().getTotalHits() > 0L) {
				final StringBuilder sb = new StringBuilder();
				for (final SearchHit hit : r.getHits().getHits()) {
					sb.append(hit.getSourceAsString());
					sb.append(System.getProperty("line.separator"));
				}
				Platform.runLater(() -> queryResult.setText(sb.toString()));
			} else {
				Platform.runLater(() -> queryResult.setText("No results."));
			}
		});
	}

	@Override
	public void doInsert(final ActionEvent event) {
		if (insertIndex.getText().isEmpty() || insertType.getText().isEmpty() || content.getText().isEmpty()) {
			Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR,
					"Index, Type or Content is empty"));
		}
		if (JsonUtils.isJsonValid(content.getText())) {
			CompletableFuture
					.supplyAsync(() -> clientConnection.insertDocument(insertIndex.getText(), insertType.getText(),
							content.getText())) //
					.thenAccept(r -> {
						if (r.getResult().equals(DocWriteResponse.Result.CREATED)) {
							Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(),
									Alert.AlertType.INFORMATION, "Successfully added document to index"));
						} else {
							Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR,
									"Fialed to add docuemnt to index"));
						}
					});
		} else {
			Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR,
					"Document is not JSON Well-Formed"));
		}
	}

	@Override
	public void close() {
		if (clientConnection != null) {
			clientConnection.disconnect();
		}
	}

	private void disableServerConnection() {
		server.setEditable(false);
		server.setDisable(true);
	}

	private void enableServerConnection() {
		server.setEditable(true);
		server.setDisable(false);
	}

	private void enableTooling() {
		// Enable SearchTooling
		query.setEditable(true);
		query.setDisable(false);
		searchIndex.setEditable(true);
		searchIndex.setDisable(false);
		searchType.setEditable(true);
		searchType.setDisable(false);
		search.setDisable(false);
		queryResult.setDisable(false);

		// Enable InsertTooling
		insertIndex.setEditable(true);
		insertIndex.setDisable(false);
		insertType.setEditable(true);
		insertType.setDisable(false);
		insert.setDisable(false);
		content.setEditable(true);
		content.setDisable(false);
	}

	private void disableTooling() {
		// Disable SearchTooling
		query.setEditable(false);
		query.setDisable(true);
		search.setDisable(true);
		searchIndex.setEditable(false);
		searchIndex.setDisable(true);
		searchType.setEditable(false);
		searchType.setDisable(true);
		queryResult.setDisable(true);

		// Disable InsertTooling
		insertIndex.setEditable(false);
		insertIndex.setDisable(true);
		insertType.setEditable(false);
		insertType.setDisable(true);
		insert.setDisable(true);
		content.setEditable(false);
		content.setDisable(true);
	}

	private Stage getPrimaryStage() {
		return (Stage) server.getScene().getWindow();
	}

	private Scene getScene() {
		return server.getScene();
	}
}
