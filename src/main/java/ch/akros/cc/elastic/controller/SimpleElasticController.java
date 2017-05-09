package ch.akros.cc.elastic.controller;

import ch.akros.cc.elastic.connection.ClientConnection;
import ch.akros.cc.elastic.connection.ClientConnectionFactory;
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
import org.elasticsearch.action.DocWriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
    private Button search;

    @FXML
    private TextArea queryResult;

    @FXML
    private TextField index;

    @FXML
    private TextField type;

    @FXML
    private Button insert;

    @FXML
    private TextArea content;

    private static final Logger LOG = LoggerFactory.getLogger(SimpleElasticController.class);

    private boolean connected;
    private ClientConnection clientConnection;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        enableServerConnection();
        disableTooling();
    }

    @Override
    public void doConnect(final ActionEvent event) {
        if (connected) {
            clientConnection.disconnect();
            connected = false;
            connect.setText("Connect");
            enableServerConnection();
            disableTooling();
        } else {
            CompletableFuture.supplyAsync(() -> {
                        try {
                            getScene().setCursor(Cursor.WAIT);
                            ClientConnectionFactory.init(server.getText());
                        } catch (MalformedURLException | UnknownHostException e) {
                            throw new CompletionException(e);
                        }
                        return ClientConnectionFactory.getClientConnection();
                    }
            ).exceptionally(t -> {
                LOG.error("Cannot connect to Elasticsearch", t);
                getScene().setCursor(Cursor.DEFAULT);
                return null;
            }).thenAccept(cc -> {
                clientConnection = cc;
                getScene().setCursor(Cursor.DEFAULT);
                Platform.runLater(() -> {
                    connected = true;
                    connect.setText("Disconnect");
                    disableServerConnection();
                    enableTooling();
                });
            });
        }
    }

    @Override
    public void doSearch(final ActionEvent event) {
        System.out.println("doSearch");
    }

    @Override
    public void doInsert(final ActionEvent event) {
        if (index.getText().isEmpty() || type.getText().isEmpty() || content.getText().isEmpty()) {
            Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR, "Index, Type or Content is empty"));
        }
        if (JsonUtils.isJsonValid(content.getText())) {
            CompletableFuture.supplyAsync(() -> clientConnection.insertDocument(index.getText(), type.getText(), content.getText())
            ).thenAccept(r -> {
                if (r.getResult().equals(DocWriteResponse.Result.CREATED)) {
                    Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.INFORMATION, "Successfully added document to index"));
                } else {
                    Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR, "Fialed to add docuemnt to index"));
                }
            });
        } else {
            Platform.runLater(() -> DialogUtil.showDialog(getPrimaryStage(), Alert.AlertType.ERROR, "Document is not JSON Well-Formed"));
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
        search.setDisable(false);
        queryResult.setDisable(false);

        // Enable InsertTooling
        index.setEditable(true);
        index.setDisable(false);
        type.setEditable(true);
        type.setDisable(false);
        insert.setDisable(false);
        content.setEditable(true);
        content.setDisable(false);
    }

    private void disableTooling() {
        // Disable SearchTooling
        query.setEditable(false);
        query.setDisable(true);
        search.setDisable(true);
        queryResult.setDisable(true);

        // Disable InsertTooling
        index.setEditable(false);
        index.setDisable(true);
        type.setEditable(false);
        type.setDisable(true);
        insert.setDisable(true);
        content.setEditable(false);
        content.setDisable(true);
    }

    private Stage getPrimaryStage() {
        return (Stage) server.getScene().getWindow();
    }

    private Scene getScene(){
        return server.getScene();
    }
}
