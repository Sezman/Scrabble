import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.Serializable;
import java.util.*;
public class Tile implements Serializable {
    private char tileChar;
    private boolean isBlank = false;

    public Tile(char tile) {
        this.tileChar = Character.toLowerCase(tile);
        if (tile == ' '){
            isBlank = true;
        }
    }

    public void setTileChar(char tileChar){
        this.tileChar = Character.toLowerCase(tileChar);
    }

    public boolean isBlank() {
        return isBlank;
    }

    public char getTileChar() {
        return this.tileChar;
    }

    public static int getTileScore(Tile tile) {
        char c = tile.getTileChar();
        if (tile.isBlank()){
            return 0;
        }
        else if (c == 'a'||c == 'e'||c == 'i'||c == 'o'||c == 'u'||c == 'l'||c == 'n'||c == 's'||c == 't'||c == 'r') {
            return 1;
        }
        else if (c == 'd'||c == 'g') {
            return 2;
        }
        else if (c == 'b'||c == 'c'||c == 'm'||c == 'p') {
            return 3;
        }
        else if (c == 'f'||c == 'h'||c == 'v'||c == 'w'||c == 'y') {
            return 4;
        }
        else if (c == 'k') {
            return 5;
        }
        else if (c == 'j'||c == 'x') {
            return 8;
        }
        else if (c == 'q'||c == 'z') {
            return 10;
        }
        else {
            System.out.println(c);
            throw new IllegalArgumentException("That is not a valid tile");
        }
    }

    public boolean equals(char letter) {
        return Character.toUpperCase(letter) == Character.toUpperCase(this.tileChar);
    }
    public boolean equals(Tile tile) {
        return tileChar == tile.getTileChar() && isBlank == tile.isBlank();
    }

}
