package sample;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.stage.Stage;

public class Reversi extends Application {
    // overridden init method
    public void init() {
        sp_mainlayout = new StackPane();
        rc_reversi = new ReversiControl();

        sp_mainlayout.getChildren().addAll(rc_reversi);
    }

    // overridden start method
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reversi");
        primaryStage.setScene(new Scene(sp_mainlayout, 800, 800));
        primaryStage.show();
    }

    // overridden stop method
    public void stop() {

    }

    public static void main(String[] args) {
        launch();
    }


    // private fields for a stack pane and a reversi control
    private StackPane sp_mainlayout;
    private ReversiControl rc_reversi;
}