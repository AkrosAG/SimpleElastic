package ch.akros.cc.elastic;

import ch.akros.cc.elastic.controller.ISimpleElasticController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Patrick on 09.05.2017.
 */
public class SimpleElasticApplication extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    public void start(final Stage primaryStage) throws Exception {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/elastic.fxml"));
        final Parent root = (Parent) fxmlLoader.load();

        final Scene scene = new Scene(root);

        primaryStage.setTitle("SimpleElastic");
        primaryStage.setScene(scene);
        primaryStage.show();

        final ISimpleElasticController controller =
                fxmlLoader.<ISimpleElasticController>getController();

        primaryStage.setOnCloseRequest(e -> {
            controller.close();
            primaryStage.close();
        });
    }
}
