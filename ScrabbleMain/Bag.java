
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bag implements Serializable {
    private List<Tile> tiles;

    /**
     * Default constructor.
     * Initializes the bag with a standard set of tiles and shuffles them.
     */

    public Bag() {
        tiles = new ArrayList<>();
        this.initializeTiles();
        Collections.shuffle(tiles);
    }
    /**
     * Alternate constructor.
     * Initializes the bag with a custom set of tiles for testing purposes.
     *
     * @param a a dummy parameter used to differentiate this constructor.
     */

    public Bag(int a){
        tiles = new ArrayList<>();
        this.initializeTiles();
        tiles.add(new Tile('h'));
        tiles.add(new Tile('e'));
        tiles.add(new Tile('l'));
        tiles.add(new Tile('l'));
        tiles.add(new Tile('o'));
        tiles.add(new Tile('b'));
        tiles.add(new Tile('t'));
    }

    /**
     * Initializes the bag with the standard distribution of Scrabble tiles.
     */
    private void initializeTiles() {
        this.addTiles('a', 9);
        this.addTiles('b', 2);
        this.addTiles('c', 2);
        this.addTiles('d', 4);
        this.addTiles('e', 12);
        this.addTiles('f', 2);
        this.addTiles('g', 3);
        this.addTiles('h', 2);
        this.addTiles('i', 9);
        this.addTiles('j', 1);
        this.addTiles('k', 1);
        this.addTiles('l', 4);
        this.addTiles('m', 2);
        this.addTiles('n', 6);
        this.addTiles('o', 8);
        this.addTiles('p', 2);
        this.addTiles('q', 1);
        this.addTiles('r', 6);
        this.addTiles('s', 4);
        this.addTiles('t', 6);
        this.addTiles('u', 4);
        this.addTiles('v', 2);
        this.addTiles('w', 2);
        this.addTiles('x', 1);
        this.addTiles('y', 2);
        this.addTiles('z', 1);
        this.addTiles(' ', 2);
    }

    /**
     * Adds a specified number of tiles of a given letter to the bag.
     *
     * @param letter the letter to add.
     * @param count  the number of tiles to add for the letter.
     */

    private void addTiles(char letter, int count) {
        for(int i = 0; i < count; ++i) {
            tiles.add(new Tile(letter));
        }

    }

    /**
     * Draws a tile from the bag.
     * Removes and returns the last tile in the bag.
     *
     * @return the drawn tile.
     * @throws IllegalStateException if the bag is empty.
     */

    public Tile drawTile() {
        if (tiles.isEmpty()) {
            throw new IllegalStateException("No tiles left in the bag");
        } else {
            return (Tile)tiles.removeLast();
        }
    }

    /**
     * Gets the current count of tiles in the bag.
     *
     * @return the number of tiles left in the bag.
     */

    public int getTileCount() {
        return tiles.size();
    }

    /**
     * Checks if the bag contains all the characters of a given word.
     *
     * @param word the word to check.
     * @return true if all characters in the word are present in the bag; false otherwise.
     */

    public boolean ItContains(String word) {
        for(int i = 0; i < word.length(); ++i) {
            if (!tiles.contains(word.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
