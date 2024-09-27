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
     * Contains two fields: value (A, 2-10, J, Q, K), and type (C, D, H, S).
     * Each card object holds a value and a type.
     */
    private class Card {
        String value;   // value of the card (A, 2-10, J, Q, K)
        String type;    // type of the card (C = Clubs, D = Diamonds, H = Hearts, S = Spades)

        /**
         * Constructor: Card
         * Initializes a card with its value and type.
         * @param value The value of the card (A, 2-10, J, Q, K).
         * @param type The type of the card (C, D, H, S).
         */
        Card(String value, String type){
            this.value = value;
            this.type = type;
        }

        /**
         * Overriding toString() method to provide a meaningful string representation.
         * @return The card in the format "value-type" (e.g., "2-C", "A-D").
         */
        public String toString(){
            return value + "-" + type;
        }

        /**
         * Get the numerical value of the card.
         * @return 11 for Ace, 10 for face cards (J, Q, K), and the numerical value for number cards.
         */
        public int getValue(){
            if ("AJQK".contains(value)) {
                if (value.equals("A")) {
                    return 11;  // Ace can be 11 or 1, initially treated as 11
                }
                return 10;  // Face cards (J, Q, K) are worth 10
            }
            return Integer.parseInt(value);  // For numeric cards 2-10
        }

        /**
         * Checks if the card is an Ace.
         * @return True if the card is an Ace, otherwise false.
         */
        public boolean isAce() {
            return value.equals("A");
        }

        /**
         * Provides the path to the card's image file.
         * @return The image path in the format "./cards/value-type.jpg".
         */
        public String getImagePath() {
            return "./cards/" + toString() + ".jpg";
        }
    }

    // Fields for game state
    ArrayList<Card> deck;   // List to represent the deck of cards
    Random random = new Random();   // Random object for shuffling the deck

    // Dealer's information
    Card hiddenCard;    // The dealer's hidden card (face down)
    ArrayList<Card> dealerHand;  // List of the dealer's visible cards
    int dealerSum;  // The sum of the dealer's hand
    int dealerAceCount;  // Count of Aces in the dealer's hand (for ace reduction logic)

    // Player's information
    ArrayList<Card> playerHand;  // List of the player's cards
    int playerSum;  // The sum of the player's hand
    int playerAceCount;  // Count of Aces in the player's hand (for ace reduction logic)

    // Window and graphical settings
    int boardWidth = 550;  // Window width
    int boardHeight = boardWidth;  // Window height (square window)

    int cardWidth = 100;  // Width of the card images (ratio 1:1.4)
    int cardHeight = 150;  // Height of the card images

    JFrame frame = new JFrame("Black Jack");  // Main game window
    JPanel gamePanel = new JPanel() {  // Custom JPanel for rendering the game board
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // Draw hidden dealer card (back of the card)
                Image hiddenCardImage = new ImageIcon(getClass().getResource("./cards/back.jpg")).getImage();
                // Reveal hidden card if `Stay` button is disabled
                if (!stayButton.isEnabled()) {
                    hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }

                // Draw hidden dealer card and dealer's visible cards
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // Draw player's cards
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 300, cardWidth, cardHeight, null);
                }

                // Display result after Stay button is clicked (both hands revealed)
                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();  // Adjust dealer's sum if Aces present
                    playerSum = reducePlayerAce();  // Adjust player's sum if Aces present

                    String message = "";
                    if (playerSum > 21) {
                        message = "You Lose!";
                    } else if (dealerSum > 21 || playerSum > dealerSum) {
                        message = "You Win!";
                    } else if (playerSum < dealerSum) {
                        message = "You Lose!";
                    }

                    // Display the result message
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.WHITE);
                    g.drawString(message, 220, 250);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    JPanel buttonPanel = new JPanel();  // Panel for control buttons
    JButton hitButton = new JButton("Hit");  // Button for the "Hit" action
    JButton stayButton = new JButton("Stay");  // Button for the "Stay" action

    /**
     * Constructor: BlackJack
     * Initializes the game by setting up the frame, starting the game, and handling UI interactions.
     */
    BlackJack() {

        startGame();  // Begin a new game

        // Set up the frame (window)
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the game on window exit

        // Set up game panel (main board)
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));  // Set background color (green table-like)
        frame.add(gamePanel);

        // Set up buttons
        hitButton.setFocusable(false);  // Remove focus from the hit button
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);  // Remove focus from the stay button
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);  // Add buttons to the bottom of the window

        // Hit button action: add a card to the player's hand
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);  // Draw a card from the deck
                playerSum += card.getValue();  // Add the card value to player's sum
                playerAceCount += card.isAce() ? 1 : 0;  // Update Ace count if card is an Ace
                playerHand.add(card);  // Add card to player's hand
                if (reducePlayerAce() > 21) {  // Check if player busts (reduce Ace if necessary)
                    hitButton.setEnabled(false);  // Disable hit button if player is over 21
                }
                gamePanel.repaint();  // Repaint game panel to update UI
            }
        });

        // Stay button action: reveal dealer's hand and determine winner
        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);  // Disable hit button after staying
                stayButton.setEnabled(false);  // Disable stay button

                // Dealer's turn: draw cards until dealer's sum is at least 17
                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }

                gamePanel.repaint();  // Repaint to reveal dealer's cards and result
            }
        });

        gamePanel.repaint();  // Initial repaint to display the game board
    }

    /**
     * Initializes a new game by building and shuffling the deck and dealing cards to both player and dealer.
     */
    public void startGame() {
        buildDeck();  // Build a new deck of cards
        shuffleDeck();  // Shuffle the deck

        // Initialize dealer's hand
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        // Deal hidden card to dealer (face down)
        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        // Deal visible card to dealer (face up)
        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // Initialize player's hand
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        // Deal two cards to player
        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        // Enable hit and stay buttons at the start of the game
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
    }

    /**
     * Reduces player's sum by converting Ace from 11 to 1 if needed.
     * @return The adjusted player's sum.
     */
    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;  // Treat Ace as 1 instead of 11
            playerAceCount--;
        }
        return playerSum;
    }

    /**
     * Reduces dealer's sum by converting Ace from 11 to 1 if needed.
     * @return The adjusted dealer's sum.
     */
    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;  // Treat Ace as 1 instead of 11
            dealerAceCount--;
        }
        return dealerSum;
    }

    /**
     * Builds a deck of 52 cards (values A, 2-10, J, Q, K and types C, D, H, S).
     */
    public void buildDeck() {
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        deck = new ArrayList<>();

        for (String value : values) {
            for (String type : types) {
                deck.add(new Card(value, type));
            }
        }
    }

    /**
     * Shuffles the deck of cards randomly.
     */
    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int swapIndex = random.nextInt(deck.size());
            Card temp = deck.get(swapIndex);
            deck.set(swapIndex, deck.get(i));
            deck.set(i, temp);
        }
    }

    /**
     * Main method to start the game.
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new BlackJack();  // Create a new instance of BlackJack to start the game
    }
}