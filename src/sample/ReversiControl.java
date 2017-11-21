package sample;

import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


/**
 * Created by manueltrambert on 09/11/2017.
 */
public class ReversiControl extends Control {
    // constructor for the class
    public ReversiControl() {
        setSkin(new ReversiControlSkin(this));
        setSkin(new ReversiControlSkin(this));
        rb_board = new ReversiBoard();
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

    // private fields of a reversi board
    ReversiBoard rb_board;
}
