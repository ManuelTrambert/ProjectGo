package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Translate;

/**
 * Created by manueltrambert on 21/11/2017.
 */
public class GoPiece extends Group {
    // default constructor for the class
    public GoPiece(int player) {
        t = new Translate();
        this.player = player;

        if (player == 1) {
            piece = new Ellipse();
            getChildren().addAll(piece);
            piece.getTransforms().add(t);
            piece.setStroke(Color.WHITE);
            piece.setFill(Color.WHITE);
        } else if (player == 2) {
            piece = new Ellipse();
            getChildren().addAll(piece);
            piece.getTransforms().add(t);
            piece.setStroke(Color.BLACK);
            piece.setFill(Color.BLACK);
        } else {
            piece = new Ellipse();
            getChildren().addAll(piece);
            piece.getTransforms().add(t);
            piece.setStroke(Color.TRANSPARENT);
        }
    }

    // overridden version of the resize method to give the piece the correct size
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        piece.setCenterX(width / 2);
        piece.setCenterY(height / 2);
        piece.setRadiusX(width / 2);
        piece.setRadiusY(height / 2);
    }

    // overridden version of the relocate method to position the piece correctly
    @Override
    public void relocate(double x, double y) {
        super.relocate(x, y);
        t.setX(x);
        t.setY(y);
    }

    // public method that will swap the colour and type of this piece
    public void swapPiece() {
        if (player == 1) {
            piece.setFill(Color.BLACK);
            piece.setStroke(Color.BLACK);
            player = 2;
        } else {
            piece.setFill(Color.WHITE);
            piece.setStroke(Color.WHITE);
            player = 1;
        }
    }

    // method that will set the piece type
    public void setPiece(final int type) {
        this.player = type;
        if (player == 1) {
            piece.setFill(Color.WHITE);
            piece.setStroke(Color.WHITE);
        } else if (player == 2) {
            piece.setFill(Color.BLACK);
            piece.setStroke(Color.BLACK);
        } else {
            piece.setFill(Color.TRANSPARENT);
            piece.setStroke(Color.TRANSPARENT);
        }
    }

    // returns the type of this piece
    public int getPiece() {
        // NOTE: this is to keep the compiler happy until you get to this point
        return player;
    }

    // private fields
    private int player;        // the player that this piece belongs to
    private Ellipse piece;    // ellipse representing the player's piece
    private Translate t;    // translation for the player piece
}
