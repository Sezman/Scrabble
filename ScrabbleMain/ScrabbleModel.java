import javax.swing.*;
import java.io.*;
import java.util.*;

public class ScrabbleModel implements Serializable {
    private List<Player> players;
    private Board board;
    private Bag bag;
    private transient ScrabbleView view;
    private int currentPlayerIndex;
    private HashSet<String> wordSet;
    private Boolean firstMove, firstSave;;

    // Undo and Redo stacks
    private transient Stack<ScrabbleModel> undoStack = new Stack<>();
    private transient Stack<ScrabbleModel> redoStack = new Stack<>();

    /**
     * Constructor for ScrabbleModel.
     * Initializes the board, bag, players list, and loads valid words from a file.
     */
    public ScrabbleModel(ScrabbleView view) {
        board = new Board();
        bag = new Bag();
        players = new ArrayList<>();
        this.view = view;
        wordSet = new HashSet<>();
        this.loadWordsFromFile();
        firstMove = true;
        firstSave = true;
        currentPlayerIndex = 0;
    }

    /**
     * Constructor for ScrabbleModel with no view for test cases.
     * Initializes the board, bag, players list, and loads valid words from a file.
     */
    public ScrabbleModel() {
        board = new Board();
        bag = new Bag(0);
        players = new ArrayList<>();
        this.view = null;
        wordSet = new HashSet<>();
        this.loadWordsFromFile();
        firstMove = true;
        firstSave = true;
        currentPlayerIndex = 0;
    }

    /**
     * Saves the current state for undo functionality.
     */
    public void saveState() {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(this);
            out.close();
            undoStack.push((ScrabbleModel) deserialize(byteOut.toByteArray()));
            redoStack.clear(); // Clear redo stack after a new move
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Undo the last move.
     * @return True if undo was successful, false otherwise.
     */
    public boolean undo() {
        if (!undoStack.isEmpty()) {
            try {
                redoStack.push(cloneState());
                ScrabbleModel previousState = undoStack.pop();
                restoreState(previousState);
                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Redo the last undone move.
     * @return True if redo was successful, false otherwise.
     */
    public boolean redo() {
        if (!redoStack.isEmpty()) {
            try {
                undoStack.push(cloneState());
                ScrabbleModel nextState = redoStack.pop();
                restoreState(nextState);
                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Clones the current state.
     * @return A deep copy of the current ScrabbleModel state.
     */
    private ScrabbleModel cloneState() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(this);
        out.close();

        return (ScrabbleModel) deserialize(byteOut.toByteArray());
    }

    /**
     * Restores the state from a given ScrabbleModel.
     * @param state The state to restore.
     */
    private void restoreState(ScrabbleModel state) {
        this.players = state.players;
        this.board = state.board;
        this.bag = state.bag;
        if (this.view != null) {
            this.view = this.view; // Keep existing view
        } else {
            this.view = state.view;
        }
        this.currentPlayerIndex = state.currentPlayerIndex;
        this.wordSet = state.wordSet;
        this.firstMove = state.firstMove;
        this.firstSave = state.firstSave;
    }

    /**
     * Deserialize a ScrabbleModel from a byte array.
     * @param data The serialized state as a byte array.
     * @return The deserialized ScrabbleModel.
     * @throws IOException If deserialization fails.
     * @throws ClassNotFoundException If the class cannot be found.
     */
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        Object obj = in.readObject();
        in.close();
        return obj;
    }

    public void setView(ScrabbleView view){
        this.view = view;
    }

    /**
     * Loads valid words from a file into a HashSet for fast lookup.
     * The words are expected to be in "src/scrabble.txt".
     */
    private void loadWordsFromFile() {
        File file = new File("src/scrabble.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (scanner.hasNextLine()) {
            wordSet.add(scanner.nextLine().trim()); // Add each word to the set after trimming whitespace
        }

        scanner.close();
    }

    /**
     * Checks if a given word exists in the set of valid Scrabble words.
     * @param word the word to be checked.
     * @return true if the word exists, false otherwise.
     */
    public boolean isWord(List<Tile> word) {
        StringBuilder stringWord = new StringBuilder();
        for (Tile tile : word){
            stringWord.append(tile.getTileChar());
        }
        return wordSet.contains(stringWord.toString().toLowerCase());
    }

    /**
     * Adds a player to the game with the specified name.
     * @param name the name of the player to be added.
     */
    public void addPlayer(String name) {
        players.add(new Player(name, bag));
    }



    /**
     * Checks if a word can be placed on the board at the specified coordinates
     * in the given direction without violating any game rules.
     * @param x the starting x-coordinate of the word.
     * @param y the starting y-coordinate of the word.
     * @param direction the direction to place the word ('D' for down, 'R' for right).
     * @param word the word to be placed.
     * @return true if the word can be placed, false otherwise.
     */
    public boolean isPossible(int x, int y, char direction, List<Tile> word) {
        HashMap<Character, Integer> charMap = new HashMap<>();
        int xIndex;
        int yIndex;

        // If it's not a valid word, return false
        if (!(isWord(word))) {
            return false;
        }

        boolean isTouching = false; // Used to ensure that at least one tile is touching the existing placed tiles

        // Iterate over the characters in the word and check the placement on the board
        for (int i = 0; i < word.size(); i++) {
            Tile c = word.get(i);
            if (direction == 'D') { // Moving down
                yIndex = y + i;
                xIndex = x;
            } else { // Moving right
                yIndex = y;
                xIndex = x + i;
            }
            if (board.isEmpty(xIndex, yIndex)) {
                charMap.put(c.getTileChar(), charMap.getOrDefault(c.getTileChar(), 0) + 1);

                // Check adjacent tiles
                if (!board.isEmpty(xIndex + 1, yIndex) || !board.isEmpty(xIndex - 1, yIndex) ||
                        !board.isEmpty(xIndex, yIndex + 1)  || !board.isEmpty(xIndex, yIndex - 1) ){
                    isTouching = true;
                }

            } else if (!board.getTile(xIndex, yIndex).equals(c)) {
                return false;  // The board contains a different character at this position
            }
            else {isTouching = true;}
        }

        // Check if the player has enough of each character in their hand
        for (char c : charMap.keySet()) {
            if (charMap.get(c) > players.get(currentPlayerIndex).numInHand(c)) {
                return false;
            }
        }

        // Handle special case for the first move (must include the center tile)
        if (firstMove) {
            xIndex = x;
            yIndex = y;
            for (int i = 0; i < word.size(); i++) {
                if (direction == 'D') {
                    yIndex = y + i;
                } else {
                    xIndex = x + i;
                }
                if (xIndex == 7 && yIndex == 7) {
                    saveState();
                    firstMove = false;
                    return true;
                }
            }
            return false;
        }
        return isTouching;
    }

    /**
     * Checks if a word is valid for placement on the board, considering game rules.
     * @param x the starting x-coordinate of the word.
     * @param y the starting y-coordinate of the word.
     * @param direction the direction to place the word ('D' for down, 'R' for right).
     * @param word the word to be placed.
     * @return true if the word can be placed, false otherwise.
     */
    public boolean isValid(int x, int y, char direction, List<Tile> word) {
        // Check if the word can be placed
        if (!isPossible(x, y, direction, word)) {
            return false;
        }

        // Validate all affected words on the board
        List<List<Tile>> effectedWords = getEffectedWords(x, y, direction, word);
        for (List<Tile> effectedWord : effectedWords) {
            if (!isWord(effectedWord)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves all words affected by placing a word on the board.
     * @param x the starting x-coordinate.
     * @param y the starting y-coordinate.
     * @param direction the direction of the word.
     * @param word the word being placed.
     * @return a list of affected words.
     */
    private List<List<Tile>> getEffectedWords(int x, int y, char direction, List<Tile> word) {
        int xIndex;
        int yIndex;
        List<List<Tile>> wordList = new ArrayList<>();

        // For each character in the word, find new words formed by adjacent tiles
        for (int i = 0; i < word.size(); i++) {
            Tile c = word.get(i);
            if (direction == 'D') {
                yIndex = y + i;
                xIndex = x;
                if (board.isEmpty(xIndex, yIndex)) {
                    wordList.add(getWord(xIndex, yIndex, 'R', c));
                }
            } else {
                yIndex = y;
                xIndex = x + i;
                if (board.isEmpty(xIndex, yIndex)) {
                    wordList.add(getWord(xIndex, yIndex, 'D', c));
                }
            }
        }
        return wordList;
    }

    /**
     * Retrieves the complete word formed in a given direction from the board.
     * @param x the x-coordinate of the starting position.
     * @param y the y-coordinate of the starting position.
     * @param direction the direction to search ('D' for down, 'R' for right).
     * @param c the character being added to the board.
     * @return the complete word as a string.
     */
    private List<Tile> getWord(int x, int y, char direction, Tile c) {
        int xIndex;
        int yIndex;
        List<Tile> word = new ArrayList<>();
        word.add(c);

        // Search for adjacent tiles to complete the word in the specified direction
        if (direction == 'D') {
            yIndex = y + 1; // Search downward
            xIndex = x;
            while (!board.isEmpty(xIndex, yIndex)) {
                // Add tiles below the starting position to the word
                word.add(board.getTile(xIndex, yIndex));
                yIndex++;
            }
            yIndex = y - 1; // Search upward
            while (!board.isEmpty(xIndex, yIndex)) {
                // Add tiles above the starting position to the word
                word.addFirst(board.getTile(xIndex, yIndex));
                yIndex--;
            }
        } else {
            yIndex = y;
            xIndex = x + 1; // Search to the right
            while (!board.isEmpty(xIndex, yIndex)) {
                // Add tiles to the right of the starting position to the word
                word.add(board.getTile(xIndex, yIndex));
                xIndex++;
            }
            xIndex = x - 1; // Search to the left
            while (!board.isEmpty(xIndex, yIndex)) {
                // Add tiles to the left of the starting position to the word
                word.addFirst(board.getTile(xIndex, yIndex));
                xIndex--;
            }
        }

        return word;
    }

    /**
     * Scores a word and updates the current player's score.
     * The score is calculated based on the tiles in the word, an additional base score,
     * and a multiplier applied to the total score.
     *
     * @param word The word to score, represented as a list of tiles.
     * @param multiplier The multiplier to apply to the word's total score (e.g., for double/triple word scores).
     * @param additionalScore An additional base score to include in the calculation (e.g., bonus points).
     */
    private void scoreWord(List<Tile> word, int multiplier, int additionalScore) {
        int score = additionalScore;
        for (Tile c : word) {
            score += Tile.getTileScore(c);
        }
        if (word.size() > 1){
        getCurrentPlayer().updateScore(score*multiplier);}
    }

    /**
     * Updates the current player's score by adding a specified value.
     *
     * @param score The value to add to the current player's total score.
     */
    private void updatePlayerScore(int score){
        getCurrentPlayer().updateScore(score);
    }

    /**
     * Makes a move by placing a word on the board if it is valid.
     * @param x the starting x-coordinate of the word.
     * @param y the starting y-coordinate of the word.
     * @param direction the direction to place the word ('D' for down, 'R' for right).
     * @param word the word to be placed.
     * @return true if the move is successful, false otherwise.
     */
    public boolean makeMove(int x, int y, char direction, List<Tile> word) {

        int xIndex;
        int yIndex;

        // Determine the opposite direction for scoring perpendicular words
        char oppositeDirection = direction == 'D' ? 'R': 'D';

        int multiplier = 1;
        int wordScore = 0;

        // Check if the move is valid
        if (!isValid(x, y, direction, word)) {
            return false;
        }
        if(!firstSave){
            saveState();
        }
        else{
            firstSave = false;
        }

        // Place the word on the board and update the player's hand
        for (int i = 0; i < word.size(); i++) {
            Tile c = word.get(i); // Current tile to place
            if (direction == 'D') {
                yIndex = y + i; // Move down for vertical placement
                xIndex = x;
            } else {
                yIndex = y;
                xIndex = x + i; // Move right for horizontal placement
            }
            if (board.isEmpty(xIndex, yIndex)) {
                // if empty, place the tile
                Tile tile = getCurrentPlayer().popTile(c);
                board.addLetter(xIndex, yIndex, tile);
                getCurrentPlayer().refillHand();

                // Check for special score multipliers at the location
                String m = board.getMultiplier(xIndex, yIndex);
                if (!m.equals("normal")){
                    // Apply scoring rules based on multiplier type
                    switch (m){
                        // Apply scoring rules based on multiplier type
                        case "DL" -> {
                            wordScore += 2 * Tile.getTileScore(tile);
                            scoreWord(getWord(xIndex, yIndex, oppositeDirection, tile), 1, Tile.getTileScore(tile));
                        }
                        case "TL" -> {
                            wordScore += 3 * Tile.getTileScore(tile);
                            scoreWord(getWord(xIndex, yIndex, oppositeDirection, tile), 1, 2 * Tile.getTileScore(tile));
                        }
                        case "DW" -> {
                            multiplier = multiplier * 2;
                            wordScore += Tile.getTileScore(tile);
                            scoreWord(getWord(xIndex, yIndex, oppositeDirection, tile), 2, 0);
                        }
                        case "TW" -> {
                            multiplier = multiplier * 3;
                            wordScore += Tile.getTileScore(tile);
                            scoreWord(getWord(xIndex, yIndex, oppositeDirection, tile), 3, 0);
                        }
                    }
                } else {
                    // No special multiplier; add the tile's base score
                    wordScore += Tile.getTileScore(tile);
                    scoreWord(getWord(xIndex, yIndex, oppositeDirection, tile), 1, 0);
                }
            } else {
                // The board cell is already occupied; add the score of the existing tile
                wordScore += Tile.getTileScore(board.getTile(xIndex, yIndex));
            }
        }

        // Update the player's score and switch to the next player

        updatePlayerScore(wordScore*multiplier);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (view != null) {
            view.updateView();}
        return true;
    }

    /**
     * Updates currentPlayerIndex
     */
    public void skip(){
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (view != null) {
            view.updateView();
        }
    }

    /**
     * Retrieves the current player in the game.
     * @return the current player.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Retrieves the current board.
     * @return the current board.
     */
    public Board getBoard(){
        return board;
    }

    /**
     * Retrieves the player list.
     * @return the player list.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     *
     * @return true if first move has not happened yet.
     */
    public boolean isFirst(){
        return firstMove;
    }

    /**
     * Resets the game by reinitializing the board, bag, and players.
     */
    public void resetGame() {
        if (view != null) {
            this.view.showEnd();
        }
        bag = new Bag();
        firstMove = true;

        // Reset the players while retaining their names
        List<Player> holder = new ArrayList<>();
        for (Player player : players) {
            holder.add(new Player(player.getName(), bag));
        }
        players = holder;
        board = new Board();
        if (view != null) {
            view.updateView();
        }
    }

    /**
     * Saves the current state of the game to a specified file.
     *
     * @param fileName The name of the file where the game state should be saved.
     */
    public void saveState(String fileName) {
        try {
            String directoryPath = "src/saves";

            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(this);
            out.close();
            file.close();
        } catch (IOException e) {
            if (view != null) {
                JOptionPane.showMessageDialog(view.getFrame(), "Error saving game state!:" + e.getMessage());
            } else {
                System.out.println("Error saving game state:" + e.getMessage());
            }
        }
    }

    /**
     * Loads the game state from a specified file and updates the view if it exists.
     *
     * @param fileName The name of the file from which the game state should be loaded.
     */
    public void loadState (String fileName) {
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            ScrabbleModel model = (ScrabbleModel) in.readObject();
            if (view != null) {
                model.setView(view);
                view.setModel(model);
                view.updateView();
            }
            else {
                System.out.println("Should not be loading game state with no view...");
            }
        } catch (IOException | ClassNotFoundException e) {
            if (view != null) {
                JOptionPane.showMessageDialog(view.getFrame(), "Error loading game state!:" + e.getMessage());
            } else {
                System.out.println("Error loading game state:" + e.getMessage());
            }
        }
    }
}
