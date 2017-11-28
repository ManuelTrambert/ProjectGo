package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public void init() {
        sp_mainlayout = new StackPane();
        rc_Go = new GoControl();

        sp_mainlayout.getChildren().addAll(rc_Go);
    }

    // overridden start method
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Go game");
        primaryStage.setScene(new Scene(sp_mainlayout, 800, 800));
        primaryStage.show();
    }

    // overridden stop method
    public void stop() {

    }

    // private fields for a stack pane and a Go control
    private StackPane sp_mainlayout;
    private GoControl rc_Go;
}
