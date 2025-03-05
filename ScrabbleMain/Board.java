import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class Board implements Serializable {
    private Tile[][] board;
    private String[][] multipliers;

    /**
     * Constructor for the Board class.
     * Initializes a 15x15 board with all positions set to null (empty).
     */
    public Board() {
        multipliers = new String[15][15]; 
        board = new Tile[15][15];  // Initialize an empty 15x15 board
        setMultiplier("defaultLayout.xml");
    }
    public Tile[][] getBoard(){return board;}
    /**
     * Adds a tile to the board at the specified coordinates.
     *
     * @param x the x-coordinate of the tile to be placed
     * @param y the y-coordinate of the tile to be placed
     * @param tile the Tile object to place on the board
     * @throws IllegalArgumentException if the position is out of bounds or already occupied
     */
    public void addLetter(int x, int y, Tile tile) {
        // Check if the coordinates are within the valid range (0-14 for x, 0-14 for y)
        if (x <= 14 && x >= 0 && y <= 14 && y >= 0) {
            // Check if the specified board position is empty
            if (board[x][y] == null) {
                board[x][y] = tile;  // Place the tile on the board
            } else {
                // Throw an exception if the tile position is already occupied
                throw new IllegalArgumentException(
                        "Tile at " + x + ", " + y + " is already taken with a " + board[x][y].getTileChar());
            }
        } else {
            // Throw an exception if the coordinates are out of range
            throw new IllegalArgumentException("Index is out of range :(");
        }
    }

    /**
     * Retrieves the tile at the specified coordinates on the board.
     *
     * @param x the x-coordinate of the tile to retrieve
     * @param y the y-coordinate of the tile to retrieve
     * @return the Tile object at the specified position, or null if the position is empty
     */
    public Tile getTile(int x, int y) {
        if (x <= 14 && x >= 0 && y <= 14 && y >= 0) {
            return board[x][y];  // Return the tile at the given position
        }
        else {return null;}

    }

    /**
     * Checks if the position at the specified coordinates is empty.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is empty (null), false if occupied by a tile
     */
    public boolean isEmpty(int x, int y) {
        if (x <= 14 && x >= 0 && y <= 14 && y >= 0) {
            return board[x][y] == null;  // Return true if the position is empty
        }
        else {return true;}
    }

    /**
     * Sets up the multipliers for the Scrabble board, initializing positions for Double Letter (DL),
     * Triple Letter (TL), Double Word (DW), and Triple Word (TW) tiles.
     */
    public void setMultiplier(String fileName){
        try {
            File file = new File("src/boardLayouts/" + fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList types = doc.getElementsByTagName("type");

            multipliers = new String[15][15];

            for (int i = 0; i < types.getLength(); i++) {
                Element typeElement = (Element) types.item(i);
                String typeName = typeElement.getAttribute("name");

                NodeList positions = typeElement.getElementsByTagName("position");
                for (int j = 0; j < positions.getLength(); j++) {
                    Element position = (Element) positions.item(j);
                    int row = Integer.parseInt(position.getAttribute("row"));
                    int col = Integer.parseInt(position.getAttribute("col"));
                    multipliers[row][col] = typeName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the multiplier for a given board position, or "normal" if the position is empty or out of bounds.
     *
     * @param x the x-coordinate of the board position.
     * @param y the y-coordinate of the board position.
     * @return the multiplier at the specified position, or "normal" if none exists.
     */
    public String getMultiplier(int x, int y){
        if (x <= 14 && x >= 0 && y <= 14 && y >= 0) {
            String multiplier = multipliers[x][y]; // Get the multiplier at the given position
            if (multiplier == null) {
                // If no multiplier exists, it's a regular tile
                return "normal";
            }
            return multiplier;
        }
        else {return "normal";}
    }
}
