import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private final String name;
    private List<Tile> hand;
    private int myScore;
    private Bag bag;

    /**
     * Constructor for the Player class.
     * Initializes the player's name, links the bag of tiles, and fills the player's hand.
     *
     * @param name the name of the player
     * @param bag the bag of tiles for the game
     */
    public Player(String name, Bag bag) {
        this.name = name;
        this.bag = bag;  // Set the bag for all Player instances (if shared)
        this.hand = new ArrayList<>();  // Initialize player's hand as an empty list
        this.myScore = 0;  // Initialize score to 0
        this.refillHand();  // Fill the player's hand with tiles
    }

    /**
     * Refills the player's hand to 7 tiles by drawing from the bag.
     * Ensures that the hand only contains 7 tiles, as long as there are enough tiles in the bag.
     */
    public void refillHand() {
        for (int value = 7 - hand.size(); value > 0 && bag.getTileCount() > 0; value--) {
            hand.add(bag.drawTile());  // Add a tile to the hand from the bag
        }
    }

    /**
     * Counts the occurrences of a specific character in the player's hand.
     *
     * @param c the character to count in the player's hand
     * @return the number of times the character appears in the hand
     */
    public int numInHand(char c) {
        int num = 0;
        for (Tile tile : this.hand) {
            if (tile.equals(c)) {  // Compare each tile's character with the input char
                ++num;  // Increment counter if a match is found
            }
        }
        return num;
    }

    /**
     * Removes a specific tile (represented by a character) from the player's hand.
     *
     * @param c the character of the tile to remove
     * @return the Tile object that was removed, or null if not found
     */
    public Tile popTile(Tile c) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).equals(c)) {  // Look for the tile matching the input character
                return hand.remove(i);  // Remove the tile and return it
            }
        }
        return null;  // Return null if no matching tile is found
    }

    /**
     * Updates the player's score based on the tile character
     *
     * @param score score to add
     */
    public void updateScore(int score) {
        myScore += score;  // Add the tile's score to the player's score
    }

    /**
     * Returns the current score of the player.
     *
     * @return the player's score
     */
    public int getScore() {
        return myScore;
    }

    /**
     * Returns the name of the player.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of tiles currently in the player's hand.
     *
     * @return the player's hand (a list of Tile objects)
     */
    public List<Tile> getHand() {
        return hand;
    }

    public boolean isWord(List<Tile> hand, int length){

        return false;
    }
    /**
     * Returns the current number of tiles in the player's hand.
     *
     * @return the size of the player's hand
     */
    public int handSize() {
        return this.hand.size();
    }

    public void setHandTest(String word){
        for (char c : word.toCharArray()){
            hand.add(new Tile(c));
        }
    }
}