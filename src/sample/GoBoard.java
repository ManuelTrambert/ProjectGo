package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
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
        render = new GoPiece[7][7];
        in_play = true;
        initialiseRender();
        current_player = 2;
        opposing = 1;
        player2_score = 2;
        player1_score = 2;

        player1_gridpane = new GridPane();
        player1_label_prisoner = new Label("PRISONERS: " + player1_score);
        player1_label_territory = new Label("TERRITORY: " + player1_score);
        player2_gridpane = new GridPane();
        player2_label_prisoner = new Label("PRISONERS: " + player2_score);
        player2_label_territory = new Label("TERRITORY: " + player2_score);
        turn = new Label("TURN: BLACK");
        pass = new Button("PASS");
        rules = new Button("RULES");

        pass.setOnAction(event -> {
            if (current_player == 1)
                pass_player1++;
            else if (current_player == 2)
                pass_player2++;
            swapPlayers();
            determineEndGame();
        });
        rules.setOnAction(event -> displayRules());

        player1_gridpane.add((new Label("PLAYER 1 (WHITE)")), 0, 0);
        player1_gridpane.add(player1_label_prisoner, 0, 1);
        player1_gridpane.add(player1_label_territory, 0, 2);
        player2_gridpane.add((new Label("PLAYER 2 (WHITE)")), 0, 0);
        player2_gridpane.add(player2_label_prisoner, 0, 1);
        player2_gridpane.add(player2_label_territory, 0, 2);


        initialiseLinesBackground();
        horizontalResizeRelocate(cell_width);
        verticalResizeRelocate(cell_height);
        horizontal_t = new Translate[8];
        horizontal_G = new Translate[7];
        vertical_t = new Translate[8];
        vertical_G = new Translate[7];
        for (int i = 0; i < 8; i += 1) {
            vertical_t[i] = new Translate(0, 0);
            horizontal_t[i] = new Translate(0, 0);

            horizontal[i].getTransforms().add(horizontal_t[i]);
            vertical[i].getTransforms().add(vertical_t[i]);
        }

        for (int i = 0; i < 7; i += 1) {
            horizontal_G[i] = new Translate(0, 0);
            vertical_G[i] = new Translate(0, 0);

            horizontalGo[i].getTransforms().add(horizontal_G[i]);
            verticalGo[i].getTransforms().add(vertical_G[i]);
        }

        getChildren().addAll(background,
                horizontal[0], horizontal[1], horizontal[2], horizontal[3], horizontal[4], horizontal[5], horizontal[6], horizontal[7],
                vertical[0], vertical[1], vertical[2], vertical[3], vertical[4], vertical[5], vertical[6], vertical[7],
                player1_gridpane, player2_gridpane, pass, turn, rules);
        for (int i = 0; i < 7; i += 1) {
            getChildren().addAll(horizontalGo[i], verticalGo[i]);
        }

        resetGame();

    }

    // public method that will try to place a piece in the given x,y coordinate
    public void placePiece(final double x, final double y) {
        int indexx = (int) (x / cell_width) - 1;
        int indexy = (int) (y / cell_height) - 1;


       /* if (!in_play || getPiece(indexx, indexy) != 0
                || !adjacentOpposingPiece(indexx, indexy) || !determineReverse(indexx, indexy)) {
            return;
        }*/

        placeAndReverse(indexx, indexy);
        updateScores();
        swapPlayers();
        deleteSurrounded();
        System.out.println("Player 1 score : " + player1_score + "\nPlayer 2 score : " + player2_score + "\nIt\'s the turn of the Player " + current_player);

    }

    // overridden version of the resize method to give the board the correct size
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        cell_width = width / 8.0;
        cell_height = height / 8.0;

        background.setWidth(width);
        background.setHeight(height);
        player1_gridpane.setLayoutX(width / 3);
        player1_gridpane.setLayoutY(10);
        player2_gridpane.setLayoutX(width / 1.5);
        player2_gridpane.setLayoutY(10);
        pass.setLayoutX(width / 15);
        pass.setLayoutY(40);
        rules.setLayoutX(width / 15 - 3);
        rules.setLayoutY(10);
        turn.setLayoutX(width / 15 - 15);
        turn.setLayoutY(70);
// set a new y on the horizontal lines and translate them into place
        for (int i = 0; i < 8; i += 1) {
            horizontal_t[i].setY((i + 1) * cell_height);
            horizontal[i].setEndX(width);
            vertical_t[i].setX((i + 1) * cell_width);
            vertical[i].setEndY(height);
            vertical[i].setStroke(Color.TRANSPARENT);
            horizontal[i].setStroke(Color.TRANSPARENT);
        }

        for (int i = 0; i < 7; i += 1) {
            horizontal_G[i].setY((i + 1.5) * cell_height);
            horizontalGo[i].setEndX(width);
            vertical_G[i].setX((i + 1.5) * cell_width);
            verticalGo[i].setEndY(height);
            verticalGo[i].setStroke(Color.BLACK);
            horizontalGo[i].setStroke(Color.BLACK);
            horizontalGo[i].setStartX(1.5 * cell_width);
            verticalGo[i].setStartY(1.5 * cell_height);
            horizontalGo[i].setEndX(7.5 * cell_width);
            verticalGo[i].setEndY(7.5 * cell_height);
        }
// set a new x on the vertical lines and translate them into place

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

        in_play = true;
        current_player = 2;
        opposing = 1;
        player1_score = 0;
        player2_score = 0;
    }

    // private method that will reset the renders
    private void resetRenders() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
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
        horizontalGo = new Line[7];
        verticalGo = new Line[7];

        for (int i = 0; i < 8; i += 1) {
            horizontal[i] = new Line();
            vertical[i] = new Line();
            vertical[i].setStroke(Color.BLACK);
            horizontal[i].setStroke(Color.BLACK);
        }
        for (int i = 0; i < 7; i += 1) {
            horizontalGo[i] = new Line();
            verticalGo[i] = new Line();
            verticalGo[i].setStroke(Color.BLACK);
            horizontalGo[i].setStroke(Color.BLACK);
        }
    }

    // private method for resizing and relocating the horizontal lines
    private void horizontalResizeRelocate(final double width) {

        for (int i = 0; i < 8; i += 1) {
            horizontal[i].setStartX(cell_width / 2);
            horizontal[i].setStartY(cell_height / 2);
        }
        for (int i = 0; i < 7; i += 1) {
            horizontalGo[i].setStartX(cell_width / 2 + cell_width);
            horizontalGo[i].setStartY(cell_height / 2 + cell_height);
        }
    }

    // private method for resizing and relocating the vertical lines
    private void verticalResizeRelocate(final double height) {
        for (int i = 0; i < 8; i += 1) {
            vertical[i].setStartX(cell_width / 2);
            vertical[i].setStartY(cell_height / 2);
        }
        for (int i = 0; i < 7; i += 1) {
            verticalGo[i].setStartX(cell_width / 2 + cell_width);
            verticalGo[i].setStartY(cell_height / 2 + cell_height);
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
        for (int i = 0; i < 7; i += 1) {
            for (int j = 0; j < 7; j += 1) {
                if (getPiece(i, j) == 1) {
                    player1_score += 1;
                } else if (getPiece(i, j) == 2) {
                    player2_score += 1;
                }
            }
        }
        player1_label_territory.setText("TERRITORY: " + player1_score);
        player2_label_territory.setText("TERRITORY: " + player2_score);
    }

    // private method for resizing and relocating all the pieces
    private void pieceResizeRelocate() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (getPiece(i, j) != 0) {
                    render[i][j].relocate((i + 1) * cell_width, (j + 1) * cell_height);
                    render[i][j].resize(cell_width, cell_height);
                }
            }
        }
    }

    // private method for determining which pieces surround x,y will update the
    // surrounding array to reflect this
    private boolean determineSurrounding(final int x, final int y) {
        boolean right = false;
        boolean up = false;
        boolean left = false;
        boolean down = false;
        if ((x < 6 && render[x + 1][y].getPiece() == opposing) || x >= 6) {
            right = true;
        }
        if ((x > 0 && render[x - 1][y].getPiece() == opposing) || x <= 0) {
            left = true;
        }
        if ((y < 6 && render[x][y + 1].getPiece() == opposing) || y >= 6) {
            up = true;
        }
        if ((y > 0 && render[x][y - 1].getPiece() == opposing) || y <= 0) {
            down = true;
        }
        return (right && left && up && down);
    }

    // private method for determining if a reverse can be made will update the can_reverse
    // array to reflect the answers will return true if a single reverse is found

    private boolean determineReverse(final int x, final int y) {
        return false;
        // NOTE: this is to keep the compiler happy until you get to this part
    }

    // private method for determining if a reverse can be made from a position (x,y) for
    // a player piece in the given direction (dx,dy) returns true if possible
    // assumes that the first piece has already been checked
    private boolean isReverseChain(final int x, final int y, final int dx, final int dy, final int player) {
        return false;
    }

    // private method for determining if any of the surrounding pieces are an opposing
    // piece. if a single one exists then return true otherwise false
    private boolean adjacentOpposingPiece(final int x, final int y) {
        return false;
    }

    // private method for placing a piece and reversing pieces
    private void placeAndReverse(final int x, final int y) {
        render[x][y].setPiece(current_player);
        render[x][y].resize(cell_width, cell_height);
        render[x][y].relocate((x + 1) * cell_width, (y + 1) * cell_height);
        getChildren().add(render[x][y]);

        if (current_player == 1)
            pass_player1 = 0;
        else if (current_player == 2)
            pass_player2 = 0;
    }

    // private method to reverse a chain
    private void reverseChain(final int x, final int y, final int dx, final int dy) {

    }

    // private method for getting a piece on the board. this will return the board
    // value unless we access an index that doesnt exist. this is to make the code
    // for determing reverse chains much easier
    private int getPiece(final int x, final int y) {
        // NOTE: this is to keep the compiler happy until you get to this point
        if (x > 6 || x < 0 || y > 6 || y < 0) {
            return -1;
        }
        return render[x][y].getPiece();
    }

    // private method that will determine if the end of the game has been reached
    private void determineEndGame() {

        if (pass_player1 == 2) {
            System.out.print("Player 1 wins!\nPress SPACE to replay!\n");
        } else if (pass_player2 == 2) {
            System.out.print("Player 2 wins!\nPress SPACE to replay!\n");
        }
    }

    // private method to determine if a player has a move available
    private boolean canMove() {
        return false;
    }

    private void deleteSurrounded() {
        for (int i = 0; i < 7; i += 1) {
            for (int j = 0; j < 7; j += 1) {
                if (determineSurrounding(j, i)) {
                    render[j][i].setPiece(0);
                }
            }
        }
    }

    // private method that determines who won the game
    private void determineWinner() {

    }

    // private method that will initialise everything in the render array
    private void initialiseRender() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                render[i][j] = new GoPiece(0);
            }
        }
    }

    private void displayRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Go rules");
        alert.setHeaderText(null);
        alert.setContentText("A game of Go starts with an empty board. Each player has an effectively unlimited" +
                "supply of pieces (called stones),one taking the black stones, the other taking white. The main" +
                " object of the game is to use your stones to form territories by surrounding vacant areas of the" +
                " board. It is also possible to capture your opponent's stones by completely surrounding them.\n" +
                "\n" +
                "Players take turns, placing one of their stones on a vacant point at each turn, with Black playing" +
                " first. Note that stones are placed on the intersections of the lines rather than in the squares" +
                " and once played stones are not moved. However they may be captured, in which case they are removed" +
                " from the board, and kept by the capturing player as prisoners.");

        alert.showAndWait();
    }


    // private fields that make the Go board work

    // rectangle that makes the background of the board
    private Rectangle background;
    // arrays for the lines that makeup the horizontal and vertical grid lines
    private Line[] horizontal;
    private Line[] vertical;
    private Line[] horizontalGo;
    private Line[] verticalGo;
    // arrays holding translate objects for the horizontal and vertical grid lines
    private Translate[] horizontal_t;
    private Translate[] vertical_t;
    private Translate[] horizontal_G;
    private Translate[] vertical_G;
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
    private Label player1_label_prisoner;
    private Label player1_label_territory;
    private GridPane player2_gridpane;
    private Label player2_label_prisoner;
    private Label player2_label_territory;
    private Label turn;
    private Button pass;
    private int pass_player1;
    private int pass_player2;
    private Button rules;
}
