package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import jdk.nashorn.internal.runtime.Debug;

import java.io.Console;
import java.util.Random;

/**
 * Created by manueltrambert on 21/11/2017.
 */
public class GoBoard extends Pane {
    public GoBoard() {
        surrounding = new int[3][3];
        can_reverse = new boolean[3][3];
        render = new GoPiece[8][8];
        in_play = true;
        initialiseRender();
        current_player = 2;
        opposing = 1;
        player2_score = 2;
        player1_score = 2;

        player1_gridpane = new GridPane();
        player1_label_score = new Label("SCORE: " + player1_score);
        player1_label_prisoner = new Label("PRISONERS: " + player1_score);
        player1_label_teritory = new Label("TERRITORY: " + player1_score);
        player2_gridpane = new GridPane();
        player2_label_score = new Label("SCORE: " + player2_score);
        player2_label_prisoner = new Label("PRISONERS: " + player2_score);
        player2_label_teritory = new Label("TERRITORY: " + player2_score);
        turn = new Label("TURN: BLACK");
        pass = new Button("PASS");
        pass.setOnAction(event -> { swapPlayers(); });

        player1_gridpane.add(player1_label_score, 0, 0);
        player1_gridpane.add(player1_label_prisoner, 0, 1);
        player1_gridpane.add(player1_label_teritory, 0, 2);
        player2_gridpane.add(player2_label_score, 0, 0);
        player2_gridpane.add(player2_label_prisoner, 0, 1);
        player2_gridpane.add(player2_label_teritory, 0, 2);


        initialiseLinesBackground();
        horizontalResizeRelocate(cell_width);
        verticalResizeRelocate(cell_height);
        horizontal_t = new Translate[7];
        horizontal_t[0] = new Translate(0, 0);
        horizontal_t[1] = new Translate(0, 0);
        horizontal_t[2] = new Translate(0, 0);
        horizontal_t[3] = new Translate(0, 0);
        horizontal_t[4] = new Translate(0, 0);
        horizontal_t[5] = new Translate(0, 0);
        horizontal_t[6] = new Translate(0, 0);

        horizontal[0].getTransforms().add(horizontal_t[0]);
        horizontal[1].getTransforms().add(horizontal_t[1]);
        horizontal[2].getTransforms().add(horizontal_t[2]);
        horizontal[3].getTransforms().add(horizontal_t[3]);
        horizontal[4].getTransforms().add(horizontal_t[4]);
        horizontal[5].getTransforms().add(horizontal_t[5]);
        horizontal[6].getTransforms().add(horizontal_t[6]);

        vertical_t = new Translate[8];
        vertical_t[0] = new Translate(0, 0);
        vertical_t[1] = new Translate(0, 0);
        vertical_t[2] = new Translate(0, 0);
        vertical_t[3] = new Translate(0, 0);
        vertical_t[4] = new Translate(0, 0);
        vertical_t[5] = new Translate(0, 0);
        vertical_t[6] = new Translate(0, 0);

        vertical[0].getTransforms().add(vertical_t[0]);
        vertical[1].getTransforms().add(vertical_t[1]);
        vertical[2].getTransforms().add(vertical_t[2]);
        vertical[3].getTransforms().add(vertical_t[3]);
        vertical[4].getTransforms().add(vertical_t[4]);
        vertical[5].getTransforms().add(vertical_t[5]);
        vertical[6].getTransforms().add(vertical_t[6]);

        getChildren().addAll(background,
                horizontal[0], horizontal[1], horizontal[2], horizontal[3], horizontal[4], horizontal[5], horizontal[6],
                vertical[0], vertical[1], vertical[2], vertical[3], vertical[4], vertical[5], vertical[6],
                player1_gridpane, player2_gridpane, pass, turn);

        resetGame();

    }

    // public method that will try to place a piece in the given x,y coordinate
    public void placePiece(final double x, final double y) {
        int indexx = (int) (x / cell_width);
        int indexy = (int) (y / cell_height);

        determineSurrounding(indexx, indexy);
        determineReverse(indexx, indexy);

       /* if (!in_play || getPiece(indexx, indexy) != 0
                || !adjacentOpposingPiece(indexx, indexy) || !determineReverse(indexx, indexy)) {
            return;
        }*/

        placeAndReverse(indexx, indexy);
        updateScores();
        swapPlayers();
        System.out.println("Player 1 score : " + player1_score + "\nPlayer 2 score : " + player2_score + "\nIt\'s the turn of the Player " + current_player);
        determineEndGame();
    }

    // overridden version of the resize method to give the board the correct size
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        cell_width = width / 10.0;
        cell_height = height / 10.0;

        background.setWidth(width);
        background.setHeight(height);
        player1_gridpane.setLayoutX(width / 4);
        player1_gridpane.setLayoutY(height / 20);
        player2_gridpane.setLayoutX(width / 4);
        player2_gridpane.setLayoutY(height / 1.2);
        pass.setLayoutX(10);
        pass.setLayoutY(height / 2);
        turn.setLayoutX(10);
        turn.setLayoutY(height / 2 + 30);
// set a new y on the horizontal lines and translate them into place
        horizontal_t[0].setY(2 * cell_height);
        horizontal_t[1].setY(3 * cell_height);
        horizontal_t[2].setY(4 * cell_height);
        horizontal_t[3].setY(5 * cell_height);
        horizontal_t[4].setY(6 * cell_height);
        horizontal_t[5].setY(7 * cell_height);
        horizontal_t[6].setY(8 * cell_height);

        horizontal[0].setEndX(width - (2 * cell_width));
        horizontal[1].setEndX(width - (2 * cell_width));
        horizontal[2].setEndX(width - (2 * cell_width));
        horizontal[3].setEndX(width - (2 * cell_width));
        horizontal[4].setEndX(width - (2 * cell_width));
        horizontal[5].setEndX(width - (2 * cell_width));
        horizontal[6].setEndX(width - (2 * cell_width));
        horizontal[0].setStartX(2 * cell_width);
        horizontal[1].setStartX(2 * cell_width);
        horizontal[2].setStartX(2 * cell_width);
        horizontal[3].setStartX(2 * cell_width);
        horizontal[4].setStartX(2 * cell_width);
        horizontal[5].setStartX(2 * cell_width);
        horizontal[6].setStartX(2 * cell_width);
// set a new x on the vertical lines and translate them into place

        vertical_t[0].setX(2 * cell_width);
        vertical_t[1].setX(3 * cell_width);
        vertical_t[2].setX(4 * cell_width);
        vertical_t[3].setX(5 * cell_width);
        vertical_t[4].setX(6 * cell_width);
        vertical_t[5].setX(7 * cell_width);
        vertical_t[6].setX(8 * cell_width);

        vertical[0].setEndY(height - (2 * cell_height));
        vertical[1].setEndY(height - (2 * cell_height));
        vertical[2].setEndY(height - (2 * cell_height));
        vertical[3].setEndY(height - (2 * cell_height));
        vertical[4].setEndY(height - (2 * cell_height));
        vertical[5].setEndY(height - (2 * cell_height));
        vertical[6].setEndY(height - (2 * cell_height));
        vertical[0].setStartY(2 * cell_height);
        vertical[1].setStartY(2 * cell_height);
        vertical[2].setStartY(2 * cell_height);
        vertical[3].setStartY(2 * cell_height);
        vertical[4].setStartY(2 * cell_height);
        vertical[5].setStartY(2 * cell_height);
        vertical[6].setStartY(2 * cell_height);

        pieceResizeRelocate();
    }

    // public method for resetting the game
    public void resetGame() {
        resetRenders();
        for (int i = 0; i < 3; i += 1) {
            for (int j = 0; j < 3; j += 1) {
                surrounding[i][j] = 0;
                can_reverse[i][j] = false;
            }
        }
        render[3][3].setPiece(1);
        getChildren().add(render[3][3]);
        render[4][4].setPiece(1);
        getChildren().add(render[4][4]);
        render[3][4].setPiece(2);
        getChildren().add(render[3][4]);
        render[4][3].setPiece(2);
        getChildren().add(render[4][3]);

        in_play = true;
        current_player = 2;
        opposing = 1;
        player1_score = 2;
        player2_score = 2;
    }

    // private method that will reset the renders
    private void resetRenders() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                getChildren().remove(render[i][j]);
                render[i][j].setPiece(0);
            }
        }
    }

    // private method that will initialise the background and the lines
    private void initialiseLinesBackground() {
        background = new Rectangle();
        background.setFill(Color.RED);
        horizontal = new Line[8];
        vertical = new Line[8];

        for (int i = 0; i < 8; i += 1) {
            horizontal[i] = new Line();
            vertical[i] = new Line();
            vertical[i].setStroke(Color.BLACK);
            horizontal[i].setStroke(Color.BLACK);
        }
    }

    // private method for resizing and relocating the horizontal lines
    private void horizontalResizeRelocate(final double width) {

        for (int i = 0; i < 8; i += 1) {
            horizontal[i].setStartX(cell_width / 2);
            horizontal[i].setStartY(cell_height / 2);
        }
    }

    // private method for resizing and relocating the vertical lines
    private void verticalResizeRelocate(final double height) {
        for (int i = 0; i < 8; i += 1) {
            vertical[i].setStartX(cell_width / 2);
            vertical[i].setStartY(cell_height / 2);
        }
    }

    // private method for swapping the players
    private void swapPlayers() {
        opposing = current_player;
        if (current_player == 1) {
            current_player = 2;
            turn.setText("TURN: BLACK");
        } else {
            current_player = 1;
            turn.setText("TURN: WHITE");
        }
        for (int i = 0; i < 3; i += 1) {
            for (int j = 0; j < 3; j += 1) {
                can_reverse[i][j] = false;
                surrounding[i][j] = 0;
            }
        }
    }

    // private method for updating the player scores
    private void updateScores() {
        player1_score = 0;
        player2_score = 0;
        for (int i = 0; i < 8; i += 1) {
            for (int j = 0; j < 8; j += 1) {
                if (getPiece(i, j) == 1) {
                    player1_score += 1;
                } else if (getPiece(i, j) == 2) {
                    player2_score += 1;
                }
            }
        }
        player1_label_score.setText("SCORE: " + player1_score);
        player2_label_score.setText("SCORE: " + player2_score);
        player1_label_prisoner.setText("PRISONERS: " + player1_score);
        player2_label_prisoner.setText("PRISONERS: " + player2_score);
    }

    // private method for resizing and relocating all the pieces
    private void pieceResizeRelocate() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getPiece(i, j) != 0) {
                    render[i][j].relocate(i * cell_width, j * cell_height);
                    render[i][j].resize(cell_width, cell_height);
                }
            }
        }
    }

    // private method for determining which pieces surround x,y will update the
    // surrounding array to reflect this
    private void determineSurrounding(final int x, final int y) {
        surrounding[0][0] = getPiece(x - 1, y - 1);
        surrounding[0][1] = getPiece(x - 1, y);
        surrounding[0][2] = getPiece(x - 1, y + 1);
        surrounding[1][0] = getPiece(x, y - 1);
        surrounding[1][1] = getPiece(x, y);
        surrounding[1][2] = getPiece(x, y + 1);
        surrounding[2][0] = getPiece(x + 1, y - 1);
        surrounding[2][1] = getPiece(x + 1, y);
        surrounding[2][2] = getPiece(x + 1, y + 1);
    }

    // private method for determining if a reverse can be made will update the can_reverse
    // array to reflect the answers will return true if a single reverse is found
    private boolean determineReverse(final int x, final int y) {
        if (surrounding[0][0] == opposing) {
            can_reverse[0][0] = isReverseChain(x, y, -1, -1, current_player);
        } else {
            can_reverse[0][0] = false;
        }
        if (surrounding[1][0] == opposing) {
            can_reverse[1][0] = isReverseChain(x, y, 0, -1, current_player);
        } else {
            can_reverse[1][0] = false;
        }
        if (surrounding[2][0] == opposing) {
            can_reverse[2][0] = isReverseChain(x, y, 1, -1, current_player);
        } else {
            can_reverse[2][0] = false;
        }
        if (surrounding[0][1] == opposing) {
            can_reverse[0][1] = isReverseChain(x, y, -1, 0, current_player);
        } else {
            can_reverse[0][1] = false;
        }
        if (surrounding[0][2] == opposing) {
            can_reverse[0][2] = isReverseChain(x, y, -1, 1, current_player);
        } else {
            can_reverse[0][2] = false;
        }
        if (surrounding[1][2] == opposing) {
            can_reverse[1][2] = isReverseChain(x, y, 0, 1, current_player);
        } else {
            can_reverse[1][2] = false;
        }
        if (surrounding[2][2] == opposing) {
            can_reverse[2][2] = isReverseChain(x, y, 1, 1, current_player);
        } else {
            can_reverse[2][2] = false;
        }
        if (surrounding[2][1] == opposing) {
            can_reverse[2][1] = isReverseChain(x, y, 1, 0, current_player);
        } else {
            can_reverse[2][1] = false;
        }
        for (int i = 0; i < 3; i += 1) {
            for (int j = 0; j < 3; j += 1) {
                if (can_reverse[i][j]) {
                    return true;
                }
            }
        }
        return false;
        // NOTE: this is to keep the compiler happy until you get to this part
    }

    // private method for determining if a reverse can be made from a position (x,y) for
    // a player piece in the given direction (dx,dy) returns true if possible
    // assumes that the first piece has already been checked
    private boolean isReverseChain(final int x, final int y, final int dx, final int dy, final int player) {
        // NOTE: this is to keep the compiler happy until you get to this part
        for (int i = x + dx, j = y + dy; i <= 7 && i > 0 && j > 0 && j <= 7; i += dx, j += dy) {
            if (getPiece(i, j) == 0) {
                return false;
            }
            if (getPiece(i, j) == player) {
                return true;
            }
        }
        return false;
    }

    // private method for determining if any of the surrounding pieces are an opposing
    // piece. if a single one exists then return true otherwise false
    private boolean adjacentOpposingPiece(final int x, final int y) {
        // NOTE: this is to keep the compiler happy until you get to this part
        for (int i = 0; i < 3; i += 1) {
            for (int j = 0; j < 3; j += 1) {
                if (surrounding[i][j] == opposing) {
                    return true;
                }
            }
        }
        return false;
    }

    // private method for placing a piece and reversing pieces
    private void placeAndReverse(final int x, final int y) {
        render[x][y].setPiece(current_player);
        render[x][y].resize(cell_width, cell_height);
        render[x][y].relocate(x * cell_width, y * cell_height);
        getChildren().add(render[x][y]);
        for (int i = 0; i < 3; i += 1) {
            for (int j = 0; j < 3; j += 1) {
                if (can_reverse[i][j]) {
                    reverseChain(x, y, i - 1, j - 1);
                }
            }
        }
    }

    // private method to reverse a chain
    private void reverseChain(final int x, final int y, final int dx, final int dy) {
        for (int i = x + dx, j = y + dy; i <= 7 && i > 0 && j <= 7 && j > 0 && getPiece(i, j) != current_player; i += dx, j += dy) {
            render[i][j].swapPiece();
        }
    }

    // private method for getting a piece on the board. this will return the board
    // value unless we access an index that doesnt exist. this is to make the code
    // for determing reverse chains much easier
    private int getPiece(final int x, final int y) {
        // NOTE: this is to keep the compiler happy until you get to this point
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            return -1;
        }
        return render[x][y].getPiece();
    }

    // private method that will determine if the end of the game has been reached
    private void determineEndGame() {
        if (player2_score == 0 || player1_score == 0 || !canMove()) {
            determineWinner();
            in_play = false;
        }
    }

    // private method to determine if a player has a move available
    private boolean canMove() {
        // NOTE: this is to keep the compiler happy until you get to this part
       /* int nbCheck = 0;
        while (nbCheck < 2) {*/
        for (int i = 0; i < 8; i += 1) {
            for (int j = 0; j < 8; j += 1) {
                if (getPiece(i, j) == 0) {
                    determineSurrounding(i, j);
                    if (in_play && adjacentOpposingPiece(i, j) && determineReverse(i, j)) {
                        return true;
                    }
                }
            }
        }
        swapPlayers();
         /*   nbCheck += 1;
        }*/
        return false;
    }

    // private method that determines who won the game
    private void determineWinner() {
        if (player2_score > player1_score) {
            System.out.println("Player 2 win. The game is finished press space to replay");
        } else if (player1_score > player2_score) {
            System.out.println("Player 1 win. The game is finished press space to replay");
        } else {
            Random winner = new Random();
            int winnerrand = winner.nextInt(2) + 1;
            System.out.println("Draw. The fate chose Player " + winnerrand + " as winner. The game is finished press space to replay");
        }
    }

    // private method that will initialise everything in the render array
    private void initialiseRender() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                render[i][j] = new GoPiece(0);
            }
        }
    }


    // private fields that make the Go board work

    // rectangle that makes the background of the board
    private Rectangle background;
    // arrays for the lines that makeup the horizontal and vertical grid lines
    private Line[] horizontal;
    private Line[] vertical;
    // arrays holding translate objects for the horizontal and vertical grid lines
    private Translate[] horizontal_t;
    private Translate[] vertical_t;
    // arrays for the internal representation of the board and the pieces that are
    // in place
    private GoPiece[][] render;
    // the current player who is playing and who is his opposition
    private int current_player;
    private int opposing;
    // is the game currently in play
    private boolean in_play;
    // current scores of player 1 and player 2
    private int player1_score;
    private int player2_score;
    // the width and height of a cell in the board
    private double cell_width;
    private double cell_height;
    // 3x3 array that holds the pieces that surround a given piece
    private int[][] surrounding;
    // 3x3 array that determines if a reverse can be made in any direction
    private boolean[][] can_reverse;
    private GridPane player1_gridpane;
    private Label player1_label_score;
    private Label player1_label_prisoner;
    private Label player1_label_teritory;
    private GridPane player2_gridpane;
    private Label player2_label_score;
    private Label player2_label_prisoner;
    private Label player2_label_teritory;
    private Label turn;
    private Button pass;
}
