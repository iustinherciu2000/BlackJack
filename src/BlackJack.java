/**
 * All imports
 */
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;     // Storing the cards in the player's hands
import java.util.Random;        // Shuffling the deck of cards
import javax.swing.*;

public class BlackJack {

    /**
     * Representation of a card.
     * Contains two Strings: value, type
     * Contains constructor that passes in value and type.
     */
    private class Card{
        String value;
        String type;

        /**
         * Constructor: Card
         * @param value
         * @param type
         */
        Card(String value, String type){
            this.value = value;
            this.type = type;
        }

        /**
         * Method: ToString()
         *
         * @return: when we want to print a class or object, provide a string representation by overwriting the `toString` method
         */
        public String toString(){
            return value + "-" + type;      // we need the dash because the name of each Card is the `value - type`(i.e., 2-C, 2-D, 10-S)
        }

    }

    ArrayList<Card> deck;

    /**
     * Constructor: BlackJack
     */
    public BlackJack() {
        startGame();
    }

    /**
     * Create a deck of cards, shuffle it, and assign the player two cards
     */
    public void startGame() {
        // deck
        buildDeck();
    }

    /**
     * Class: buildDeck()
     *
     * Steps:
     * 1. create a new Array of type "Card".
     * 2. list out all the values and types of cards.
     *
     * Use `for loops` to iterate through each type and value and create a card based on each combination
     */
    public void buildDeck() {
        deck = new ArrayList<Card>();
        // Values of cards: ace, 2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King
        // Types of cards: Clubs, Diamonds, Hearts, Spades
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        // for loop to iterate through each type
        // through each type, iterate through each value and create a card using each combination
        for(int i = 0; i < types.length; i++){
            for(int j = 0; j < values.length; j++){
                Card card = new Card(values[j], types[i]);      // Card Object. Pass in values[j] and types[i] from the `for loops`
                deck.add(card);                                 // Take the deck and add the card
            }
        }

        // Create print statements
        System.out.println("Build Deck: ");
        System.out.println(deck);       // Print out the deck
    }
}
