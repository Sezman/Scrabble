import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * A class representing a tile played by the user in Scrabble, with coordinates on the board and the letter it contains.
 */
class PlayedTile {
    public int x;         // x-coordinate on the board
    public int y;         // y-coordinate on the board
    public Tile tile;   // character representing the tile letter
    public int handIndex; // index of the tile in the player's hand
}

public class ScrabbleController implements ActionListener {
    private ScrabbleModel model;
    private final ScrabbleView view;
    private List<PlayedTile> playedTiles;
    private PlayedTile selectedTile;

    /**
     * Constructor for the ScrabbleController class.
     * Initializes the controller with the given model and view.
     *
     * @param model The model representing the game logic.
     * @param view  The view displaying the game interface.
     */
    public ScrabbleController(ScrabbleModel model, ScrabbleView view) {
        this.model = model;
        this.view = view;
        this.playedTiles = new ArrayList<>();
        this.selectedTile = null;
    }

    /**
     * Resets model
     *
     * @param model the model representing the game logic.
     */
    public void setModel(ScrabbleModel model){
        this.model = model;
    }

    /**
     * Handles user actions, routing based on the action command.
     * The command format determines whether a board, hand, play, or other button is activated.
     *
     * @param e ActionEvent that triggered the action.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String[] position = e.getActionCommand().split(" ");
        String command = position[0];

        switch (command) {
            case "B" -> handleBoardButton(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
            case "H" -> handleHandButton(Integer.parseInt(position[1]));
            case "P" -> handlePlayButton();
            case "HELP" -> view.showHelp();
            case "RGSP" -> {
                model.saveState(); //save state
                System.out.println("reset");
                this.playedTiles = new ArrayList<>();
                this.selectedTile = null;
                model.resetGame(); // Reset same players
            }
            case "RGNP" -> {
                this.playedTiles = new ArrayList<>();
                this.selectedTile = null;
                view.showEnd();
                view.resetGame(); // Reset new players
            }
            case "S" -> {
                this.playedTiles = new ArrayList<>();
                this.selectedTile = null;
                model.skip();
            }
            case "SAVE" -> {
                String input = JOptionPane.showInputDialog(view.getFrame(), "Title your save!");
                while(input.contains(" ")) {
                    input = JOptionPane.showInputDialog(view.getFrame(), "No spaces allowed! Title your save!");
                }
                model.saveState("src/saves/" + input);
                view.updateLoadMenu();
            }
            case "LOAD" -> model.loadState("src/saves/" + position[1]);
            case "XML" -> {
                model.getBoard().setMultiplier(position[1]);
                view.updateView();
            }
            case "UNDO" -> {
                if (!model.undo()) {
                    JOptionPane.showMessageDialog(view.getFrame(), "No moves to undo!");
                }
                view.updateView(); // Refresh view after undo
            }
            case "REDO" -> {
                if (!model.redo()) {
                    JOptionPane.showMessageDialog(view.getFrame(), "No moves to redo!");
                }
                view.updateView(); // Refresh view after redo
            }
        }
    }

    /**
     * Retrieves a PlayedTile at a specific x and y coordinate on the board.
     *
     * @param x The x-coordinate on the board.
     * @param y The y-coordinate on the board.
     * @return The PlayedTile at the specified coordinates, or null if none found.
     */
    private PlayedTile getPlayedTileAtXY(int x, int y) {
        for (PlayedTile tile : playedTiles) {
            if (x == tile.x && y == tile.y) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Handles the play action by validating the move, determining the word direction (down or right),
     * building the word, and checking for validity based on model rules.
     * If the move is illegal, calls handleIllegalMove().
     */
    private void handlePlayButton() {

        // If no tiles have been played, the move is illegal
        if (playedTiles.isEmpty()) {
            handleIllegalMove();
            return;
        }

        // Get the first tile placed by the player
        PlayedTile firstTile = playedTiles.getFirst();

        char direction;

        List<Tile> word = new ArrayList<>();
        word.add(firstTile.tile);

        Board board = model.getBoard();

        int xIndex;
        int yIndex;

        int xStartIndex;
        int yStartIndex;
        int xFinishIndex;
        int yFinishIndex;

        // Determine play direction and validates for single tile play
        if (playedTiles.size() == 1) {
            if (!board.isEmpty(firstTile.x - 1, firstTile.y) || !board.isEmpty(firstTile.x + 1, firstTile.y)) {
                direction = 'R';
            } else if (!board.isEmpty(firstTile.x, firstTile.y - 1) || !board.isEmpty(firstTile.x, firstTile.y + 1)) {
                direction = 'D';
            } else if (!model.isFirst()) {
                handleIllegalMove();
                return;
            }
            else direction = 'D';
        } else {
            // Check if the direction is down or right based on tile placements
            direction = (firstTile.x == playedTiles.get(1).x) ? 'D' : 'R';
        }

        // Construct the word and calculate start/end indexes based on direction
        if (direction == 'D') { // Vertical word construction
            yIndex = firstTile.y + 1;
            xIndex = firstTile.x;
            while (!board.isEmpty(xIndex, yIndex) || getPlayedTileAtXY(xIndex, yIndex) != null) {
                word.add(!board.isEmpty(xIndex, yIndex) ?
                        board.getTile(xIndex, yIndex) :
                        getPlayedTileAtXY(xIndex, yIndex).tile);
                yIndex++;
            }
            xFinishIndex = xIndex;
            yFinishIndex = yIndex - 1;
            yIndex = firstTile.y - 1;
            while (!board.isEmpty(xIndex, yIndex) || getPlayedTileAtXY(xIndex, yIndex) != null) {
                word.addFirst(!board.isEmpty(xIndex, yIndex) ?
                        board.getTile(xIndex, yIndex) :
                        getPlayedTileAtXY(xIndex, yIndex).tile);
                yIndex--;
            }
            xStartIndex = xIndex;
            yStartIndex = yIndex + 1;
        } else { // Horizontal word construction
            yIndex = firstTile.y;
            xIndex = firstTile.x + 1;
            while (!board.isEmpty(xIndex, yIndex) || getPlayedTileAtXY(xIndex, yIndex) != null) {
                word.add(!board.isEmpty(xIndex, yIndex) ?
                        board.getTile(xIndex, yIndex) :
                        getPlayedTileAtXY(xIndex, yIndex).tile);
                xIndex++;
            }
            xFinishIndex = xIndex - 1;
            yFinishIndex = yIndex;
            xIndex = firstTile.x - 1;
            while (!board.isEmpty(xIndex, yIndex) || getPlayedTileAtXY(xIndex, yIndex) != null) {
                word.addFirst(!board.isEmpty(xIndex, yIndex) ?
                        board.getTile(xIndex, yIndex) :
                        getPlayedTileAtXY(xIndex, yIndex).tile);
                xIndex--;
            }
            xStartIndex = xIndex + 1;
            yStartIndex = yIndex;
        }

        // Ensures all played tiles are part of constructed word
        for (PlayedTile tile : playedTiles) {
            if (direction == 'D' && (tile.x != xStartIndex || tile.y > yFinishIndex || tile.y < yStartIndex) ||
                    direction == 'R' && (tile.y != yStartIndex || tile.x > xFinishIndex || tile.x < xStartIndex)) {
                handleIllegalMove();
                return;
            }
        }

        // Make the move on the model, handle illegal move if unsuccessful
        if (!model.makeMove(xStartIndex, yStartIndex, direction, word)) {
            handleIllegalMove();
        }
        this.playedTiles = new ArrayList<>();
        this.selectedTile = null;
    }

    /**
     * Handles clicking a board button to place or remove tiles.
     * Adds or removes a tile at the specified coordinates, depending on if a tile is selected.
     *
     * @param x The x-coordinate on the board.
     * @param y The y-coordinate on the board.
     */
    private void handleBoardButton(int x, int y) {
        if (!model.getBoard().isEmpty(x, y)) {
            return; // Return if the board space is occupied
        }
        // If no tile is currently selected for placement
        if (selectedTile == null) {
            // Check if a tile was previously played at this position
            PlayedTile tile = getPlayedTileAtXY(x, y);
            if (tile != null) {
                // Remove the temporary tile from the board view and the list of played tiles
                view.removeTempTile(x, y, tile.handIndex);
                playedTiles.remove(tile);
            }
        } else { // If a tile is selected for placement
            PlayedTile tile = getPlayedTileAtXY(x, y);
            if (tile != null) {
                // Remove the tile from the board view and the list of played tiles
                view.removeTempTile(x, y, tile.handIndex);
                playedTiles.remove(tile);
                if (tile.tile.isBlank()){
                    tile.tile.setTileChar(' ');
                }
            }
            // If the selected tile is blank, prompt the user to input a character
            if (selectedTile.tile.isBlank()){
                String input = JOptionPane.showInputDialog(view.getFrame(), "Input desired char!");
                while (true) {
                    if (input == null){
                        return; // If the user cancels, exit without placing the tile
                    }
                    if (input.length() == 1){
                        char c = input.charAt(0);
                        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')){
                            selectedTile.tile.setTileChar(Character.toLowerCase(c));
                            break;
                        }
                    }
                    input = JOptionPane.showInputDialog(view.getFrame(), "Invalid input, try again!\nInput desired char!");
                }
            }
            // Add the tile to the board view and update its position
            view.addTempTile(selectedTile.tile, x, y, selectedTile.handIndex);
            selectedTile.x = x;
            selectedTile.y = y;
            playedTiles.add(selectedTile);
            selectedTile = null;
        }
    }

    /**
     * Handles selecting a tile from the player's hand to place on the board.
     * Prevents reselecting already played tiles.
     *
     * @param index The index of the tile in the player's hand.
     */
    private void handleHandButton(int index) {
        for (PlayedTile tile : playedTiles) {
            if (index == tile.handIndex) {
                return;
            }
        }
        selectedTile = new PlayedTile();
        selectedTile.handIndex = index;
        selectedTile.tile = model.getCurrentPlayer().getHand().get(index);
    }

    /**
     * Handles illegal moves by clearing played tiles and notifying the user.
     */
    private void handleIllegalMove() {
        this.playedTiles = new ArrayList<>();
        this.selectedTile = null;
        JOptionPane.showMessageDialog(view.getFrame(), "This move is illegal! \n Try again!");
        view.updateView();
    }
}
