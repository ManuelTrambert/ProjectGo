package sample;

import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Created by manueltrambert on 21/11/2017.
 */
public class GoControl extends Control {
    public GoControl() {
        setSkin(new GoControlSkin(this));
        setSkin(new GoControlSkin(this));
        rb_board = new GoBoard();
        getChildren().add(rb_board);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            // overridden handle method
            @Override
            public void handle(MouseEvent event) {
                rb_board.placePiece(event.getX(), event.getY());

            }
        });

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            // overridden handle method
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.SPACE)
                    rb_board.resetGame();

            }
        });
    }

    // overridden version of the resize method
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        rb_board.resize(width, height);
    }

    // private fields of a Go board
    GoBoard rb_board;
}
