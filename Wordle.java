import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Wordle extends JFrame implements KeyListener, MouseListener 
{
    private static final long serialVersionUID = 1L;

    // Constants defining the game grid and keyboard layout
    private final int GRID_ROWS = 6, GRID_COLS = 5, CELL_SIZE = 70, KEY_HEIGHT = 80, KEY_WIDTH = 60, PADDING = 10;

    // Variables to track game state
    private int currentRow = 0, currentCol = 0;
    private static int[] winCountPerGuess = new int[6];
    private static int gamesPlayed = 0, winPercentage = 0, currentStreak = 0, maxStreak = 0;
    private boolean gameOver = false;

    // UI elements and data for the game
    private JLabel label; // To display messages
    private String correctWord, message = ""; // Correct word and feedback message
    private String[] displayMessage = {"Genius", "Magnificent", "Impressive", "Splendid", "Great", "Phew"}; // Winning messages
    private String[][] grid = new String[6][5]; // Stores guessed letters
    private Color[][] colors = new Color[6][5]; // Tracks colors of grid cells
    private ArrayList<String> wordList = new ArrayList<>(); // List of possible words
    
    // Define the keyboard layout and colors
    private final String[][] keys = {
        {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
        {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
        {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
    };
    private final Color[][] keyColors = new Color[keys.length][];
    
    // Constructor initializes the game window and components
    public Wordle() 
    {
        super("Wordle"); // Set the title of the window
        setSize(900, 1100); // Set window size
        setLocationRelativeTo(null); // Center the window on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Add listeners for keyboard and mouse input
        this.addKeyListener(this);
        this.addMouseListener(this);

        // Initialize the label to display feedback messages
        label = new JLabel();
        label.setBounds(50, 50, 400, 50);
        label.setForeground(Color.WHITE); // Set label color
        this.getContentPane().setBackground(Color.black); // Set background color
        this.add(label);

        // Initialize grid cells and colors to default values
        for (int row = 0; row < 6; row++) 
        {
            for (int col = 0; col < 5; col++) 
            {
                grid[row][col] = ""; 
                colors[row][col] = Color.BLACK;
            }
        }
        
        // Initialize the keyboard colors
        for (int i = 0; i < keys.length; i++) 
        {
            keyColors[i] = new Color[keys[i].length];
            
            for (int j = 0; j < keys[i].length; j++) 
                keyColors[i][j] = Color.LIGHT_GRAY; // Default color
        }

        // Select a random word for the game
        selectRandomWord();

        // Draw the initial state of the game
        paint(getGraphics());
    }

    // Load a random word from a file containing valid words
    private void selectRandomWord() 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/Mustafa/Desktop/Wordle_Word_List.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) 
                wordList.add(word.toLowerCase().trim()); // Add words to the list in lowercase
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "Error reading word list: " + e.getMessage());
            System.exit(1); // Exit the program on error
        }

        // Select a random word from the list
        correctWord = wordList.get(new Random().nextInt(wordList.size()));
    }

    // Paint the game elements (grid, title, messages, keyboard)
    public void paint(Graphics g) 
    {
        super.paint(g); // Clear the screen
        drawGrid(g); // Draw the letter grid
        drawTitle(g); // Draw the game title
        drawMessage(g); // Draw feedback messages
        drawKeyboard(g); // Draw the on-screen keyboard
    }

    // Draw the 6x5 letter grid
    private void drawGrid(Graphics g) 
    {
        g.setFont(new Font("Arial", Font.BOLD, 30)); // Set font for letters

        for (int row = 0; row < GRID_ROWS; row++) 
        {
            for (int col = 0; col < GRID_COLS; col++) 
            {
                int x = 250 + col * (CELL_SIZE + PADDING); // Calculate x-position
                int y = 200 + row * (CELL_SIZE + PADDING); // Calculate y-position
                g.setColor(colors[row][col]); // Set cell color
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE); // Draw cell
                g.setColor(Color.DARK_GRAY); // Set border color
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE); // Draw cell border
                g.setColor(Color.WHITE); // Set text color
                g.drawString(grid[row][col], x + 25, y + 45); // Draw letter
            }
        }
    }

    // Draw the game title
    private void drawTitle(Graphics g) 
    {
        g.setFont(new Font("Arial", Font.BOLD, 50)); 
        String title = "Wordle"; 
        int titleX = (getWidth() - g.getFontMetrics().stringWidth(title)) / 2; // Centers the title
        g.setColor(Color.WHITE); 
        g.drawString(title, titleX, 125); 
    }

    // Draw the feedback message
    private void drawMessage(Graphics g) 
    {
        if (!message.isEmpty()) // Only draw if a message is set
        {
            g.setFont(new Font("Arial", Font.BOLD, 25));
            int messageX = (getWidth() - g.getFontMetrics().stringWidth(message)) / 2; // Centers the message
            g.setColor(Color.WHITE); 
            g.drawString(message, messageX, 175);
        }
    }

    // Draw the on-screen keyboard
    private void drawKeyboard(Graphics g) 
    {
        int[] startX = {100, 130, 75};
        int startY = 750;

        g.setFont(new Font("Arial", Font.BOLD, 30));

        for (int row = 0; row < keys.length; row++) 
        {
            int x = startX[row];
            for (int col = 0; col < keys[row].length; col++) 
            {
                int width = keys[row][col].equals("ENTER") || keys[row][col].equals("DELETE") ? KEY_WIDTH + 75 : KEY_WIDTH;

                // Use the color from keyColors
                g.setColor(keyColors[row][col]);
                g.fillRoundRect(x, startY + row * (KEY_HEIGHT + PADDING), width, KEY_HEIGHT, 12, 12);
                g.setColor(Color.DARK_GRAY);
                g.drawRoundRect(x, startY + row * (KEY_HEIGHT + PADDING), width, KEY_HEIGHT, 12, 12);

                // Draw the label
                FontMetrics metrics = g.getFontMetrics(g.getFont());
                int labelX = x + (width - metrics.stringWidth(keys[row][col])) / 2;
                int labelY = startY + row * (KEY_HEIGHT + PADDING) + (KEY_HEIGHT + metrics.getAscent() - metrics.getDescent()) / 2;
                g.setColor(Color.WHITE);
                g.drawString(keys[row][col], labelX, labelY);

                x += width + PADDING;
            }
        }
    }

    // Handles key presses from the keyboard
    private void handleKeyPress(String key) 
    {
        if (key.equals("DELETE") && currentCol > 0) // Handles the backspace key
            grid[currentRow][--currentCol] = ""; // Removes the last letter in the current row
        if (key.equals("ENTER")) // Handles the enter key
            validateGuess(); // Validates the guess
        if (key.matches("[A-Z]") && currentCol < GRID_COLS) // Checks for valid letter keys
            grid[currentRow][currentCol++] = key; // Adds the letter to the grid

        repaint(); // Updates the screen
    }

    // Validates the current guess
    private void validateGuess() 
    {
        String guess = String.join("", grid[currentRow]).toLowerCase(); // Combines the row into a single string
        
        if (guess.length() < GRID_COLS) // Ensures the guess is the correct length
            showMessage("Not enough letters.", 1500); // Displays an error message
        else if (!wordList.contains(guess)) // Checks if the word is valid
            showMessage("Not in word list.", 1500); // Displays an error message
        else 
            evaluateGuess(guess); // Proceeds to evaluate the guess
    }
    
    private void evaluateGuess(String guess) 
    {
        boolean[] correctUsed = new boolean[GRID_COLS];

        for (int i = 0; i < GRID_COLS; i++) 
        {
            if (guess.charAt(i) == correctWord.charAt(i)) 
            {
                colors[currentRow][i] = new Color(144, 238, 144); // Green
                correctUsed[i] = true;
                updateKeyColor(guess.charAt(i), new Color(144, 238, 144)); // Update keyboard color
            }
        }

        for (int i = 0; i < GRID_COLS; i++) 
        {
            if (colors[currentRow][i] == Color.BLACK) 
            {
                for (int j = 0; j < GRID_COLS; j++) 
                {
                    if (!correctUsed[j] && guess.charAt(i) == correctWord.charAt(j)) 
                    {
                        colors[currentRow][i] = Color.YELLOW; // Yellow
                        correctUsed[j] = true;
                        updateKeyColor(guess.charAt(i), Color.YELLOW); // Update keyboard color
                        break;
                    }
                }
                if (colors[currentRow][i] == Color.BLACK) 
                {
                    colors[currentRow][i] = Color.DARK_GRAY; // Dark gray
                    updateKeyColor(guess.charAt(i), Color.DARK_GRAY); // Update keyboard color
                }
            }
        }

        // Checks if the guess is correct
        if (guess.equals(correctWord)) 
        {
            gamesPlayed++; // Increments games played
            gameOver = true; // Marks the game as over
            winCountPerGuess[currentRow]++; // Updates win stats
            updateGameStats(true); // Updates stats for a win
            repaint(); // Updates the screen
            showMessage(displayMessage[currentRow], 1500); // Displays a win message
            GameStats.displayStats(this, gamesPlayed, winPercentage, currentStreak, maxStreak, currentRow + 1, winCountPerGuess, this); // Shows game stats
        } 
        // Checks if all attempts are used
        else if (++currentRow == GRID_ROWS)
        {
            gamesPlayed++; // Increments games played
            gameOver = true; // Marks the game as over
            updateGameStats(false); // Updates stats for a loss
            repaint(); // Updates the screen
            showMessage(correctWord.toUpperCase(), 1750); // Displays the correct word
            GameStats.displayStats(this, gamesPlayed, winPercentage, currentStreak, maxStreak, currentRow, winCountPerGuess, this); // Shows game stats
        }

        currentCol = 0; // Resets the column for the next guess
    }

    // Helper method to update the color of a key
    private void updateKeyColor(char keyChar, Color newColor) 
    {
        String key = String.valueOf(keyChar).toUpperCase();

        for (int row = 0; row < keys.length; row++) 
        {
            for (int col = 0; col < keys[row].length; col++) 
            {
                if (keys[row][col].equals(key)) 
                {
                	// Get the current color of the key to compare with the new color to see if the key needs to be updated
                    Color currentColor = keyColors[row][col];

                    // Update color based on priority
                    if (currentColor.equals(Color.LIGHT_GRAY) || (currentColor.equals(Color.YELLOW) && newColor.equals(new Color(144, 238, 144))) || (currentColor.equals(Color.DARK_GRAY) && (newColor.equals(Color.YELLOW) || newColor.equals(new Color(144, 238, 144)))))
                        keyColors[row][col] = newColor;
                }
            }
        }
    }

    
    // Updates game statistics
    private void updateGameStats(boolean won) 
    {
        if (won) 
            currentStreak++; // Increases the win streak
        else 
            currentStreak = 0; // Resets the streak for a loss

        maxStreak = Math.max(maxStreak, currentStreak); // Updates the maximum streak
        
        int totalGamesWon = 0;
        
        for (int i = 0; i < winCountPerGuess.length; i++) // Calculates total wins
            totalGamesWon += winCountPerGuess[i];
        
        winPercentage = (gamesPlayed == 0) ? 0 : (int) ((double) totalGamesWon / gamesPlayed * 100); // Calculates win percentage
    }
    
    // Resets the game state
    public void resetGame() 
    {
        currentRow = 0; // Resets the row
        currentCol = 0; // Resets the column
        gameOver = false; // Resets the game over state
        message = ""; // Clears any message
        
        // Clears the grid
        for (int row = 0; row < GRID_ROWS; row++) 
        {
            for (int col = 0; col < GRID_COLS; col++) 
            {
                grid[row][col] = "";
                colors[row][col] = Color.BLACK;
            }
        }
    }
    
    // Displays a temporary message on the screen
    private void showMessage(String msg, int timeLength) 
    {
        message = msg; // Sets the message

        Timer timer = new Timer(timeLength, new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                message = "";  // Clears the message after the timer ends
                repaint(); // Updates the screen
            }
        });
        
        timer.setRepeats(false); // Ensures the timer only runs once
        timer.start(); // Starts the timer
    }

    // Handles key presses
    public void keyPressed(KeyEvent e) 
    {
        if (gameOver) return; // Ignores user input if the game is over
        
        String key;

        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            key = "ENTER"; // Maps Enter key
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            key = "DELETE"; // Maps Backspace key
        else 
            key = String.valueOf(e.getKeyChar()).toUpperCase(); // Converts character input to uppercase
        
        if (key.matches("[A-Z]|ENTER|DELETE")) 
            handleKeyPress(key); // Handles valid inputs
    }

    // Handles mouse clicks for on-screen keyboard
    public void mouseClicked(MouseEvent e) 
    {
        if (gameOver) return; // Ignores input if the game is over
        
        Point clickPoint = e.getPoint(); // Gets the click position
        
        String[][] keys = { // Layout of the on-screen keyboard
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
        };

        int[] startX = {100, 130, 75}; // Starting X positions for each row
        int startY = 750; // Starting Y position for the keyboard

        // Loops through each key and checks if the click is within its bounds
        for (int row = 0; row < keys.length; row++) 
        {
            int x = startX[row];
            int y = startY + row * (KEY_HEIGHT + PADDING);

            for (String key : keys[row]) 
            {
                int width = key.equals("ENTER") || key.equals("DELETE") ? KEY_WIDTH + 75 : KEY_WIDTH; // Adjusts width for special keys

                // Checks if the click is within the key bounds
                if (clickPoint.x >= x && clickPoint.x <= x + width &&    
                    clickPoint.y >= y && clickPoint.y <= y + KEY_HEIGHT) {
                    handleKeyPress(key); // Handles the key press
                    return; 
                }

                x += width + PADDING; // Moves to the next key position
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public static void main(String[] args) { new Wordle(); }
}
