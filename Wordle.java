import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Wordle extends JFrame implements KeyListener, MouseListener 
{
	private static final long serialVersionUID = 1L;
	
	// Variables to store game statistics
	private static int gamesPlayed = 0, winPercentage = 0, currentStreak = 0, maxStreak = 0;

	// File locations for word lists
	private final String fileLocation = "Common_5_Letter_Wordle_Word_List.txt"; // Frequently used words familiar to most users
	private final String fileLocation2 = "All_5_Letter_Wordle_Word_List.txt"; // Advanced vocabulary to account for various knowledge levels
	
	// Lists for storing common and all words
	private ArrayList<String> commonWordList = new ArrayList<>();
	private ArrayList<String> allWordList = new ArrayList<>();
	
	// Constants for grid dimensions, cell size, key size, and alphabet length
	private final int GRID_ROWS = 6, GRID_COLS = 5, CELL_SIZE = 70, KEY_HEIGHT = 80, KEY_WIDTH = 60, PADDING = 10, ALPHABET_LENGTH = 26;
	
	// Array to track win counts per guess (6 guesses max)
	private static int[] winCountPerGuess = new int[6];
	
	// Variables for tracking the current position in the grid
	private int currentRow = 0, currentCol = 0;
		
	// Boolean flags to track game state
	private boolean gameOver = false;
	private boolean evaluatingGuess = false;

	// Label for displaying messages
	private JLabel label;

	// String variables for the correct word and the message displayed to the user
	private String correctWord, message = "";

	// Array for different display messages based on performance
	private String[] displayMessage = {"Genius", "Magnificent", "Impressive", "Splendid", "Great", "Phew"};

	// 2D arrays for the game grid and colors assigned to the grid cells
	private String[][] grid = new String[GRID_ROWS][GRID_COLS];
	private Color[][] colors = new Color[GRID_ROWS][GRID_COLS];

	// Key layout for the on-screen keyboard
	private final String[][] keys = {
	    {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
	    {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
	    {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
	};

	// Array to store colors of each key on the keyboard
	private final Color[][] keyColors = new Color[keys.length][]; 
    
	private void hint() 
	{		
	    // Flatten your grid of guesses into a single list of letters
	    ArrayList<String> flatList = new ArrayList<>();
	    for (String[] row : grid) 
	        flatList.addAll(Arrays.asList(row));
	    

	    // Look for the first letter in the correct word that's not yet in flatList
	    for (int i = 0; i < correctWord.length(); i++) 
	    {
	        String letter = String.valueOf(correctWord.charAt(i)).toUpperCase();
	        if (!flatList.contains(letter)) 
	        {
	            // Show it on the GUI for 1.5 seconds
	            showMessage("Hint: the word contains '" + letter + "'", 1500);
	            Wordle.this.requestFocusInWindow();
	            return;
	        }
	    }

	    // If every letter’s already been guessed
	    showMessage("No more hints available!", 1200);
	    Wordle.this.requestFocusInWindow();
	}
	
	public Wordle() 
	{
	    super("Wordle"); // Set window title
	    setSize(900, 1100); // Set window size
	    setLocationRelativeTo(null); // Center window
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes application when the window is closed
	    getContentPane().setBackground(Color.BLACK); // Set background to black
	    setLayout(null); // Use absolute positioning

	    this.addKeyListener(this); // Key listener for keyboard input
	    this.addMouseListener(this); // Mouse listener for clicks on keyboard

	    label = new JLabel();
	    label.setBounds(50, 50, 400, 50);
	    label.setForeground(Color.WHITE);
	    this.add(label);
	    
	    // Initialize the grid and colors
	    for (int row = 0; row < 6; row++) 
	    {
	        for (int col = 0; col < 5; col++) 
	        {
	            grid[row][col] = ""; 
	            colors[row][col] = Color.BLACK;
	        }
	    }
	    
	    // Initialize key colors
	    for (int i = 0; i < keys.length; i++) 
	    {
	        keyColors[i] = new Color[keys[i].length];
	        for (int j = 0; j < keys[i].length; j++) 
	            keyColors[i][j] = Color.LIGHT_GRAY; 
	    }
	    
	    // Select a random word to guess
	    selectRandomWord();	    
	    setVisible(true);
	    this.setFocusable(true);
	    this.requestFocusInWindow();
	    repaint();         
	}

	private void selectRandomWord() 
    {
    	convertFileToList(fileLocation, commonWordList); // Load common words
    	convertFileToList(fileLocation2, allWordList); // Load all words
        
        correctWord = commonWordList.get(new Random().nextInt(commonWordList.size())); // Randomly pick the correct word
    }
    
    private void convertFileToList(String fileLocation, ArrayList<String> wordList) 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation))) 
        {
            String word;
            
            // Read each line from the file
            while ((word = reader.readLine()) != null) 
            {
                // If the word is not empty, add it to the list after trimming and converting to lowercase
                if (!word.trim().isEmpty()) 
                    wordList.add(word.toLowerCase().trim()); 
            }
        } 
        catch (IOException e) 
        {
            // Display error message if there is an issue reading the file
            JOptionPane.showMessageDialog(this, "Error reading word list: " + e.getMessage());
            
            // Exit the program if there's an error
            System.exit(1); 
        }
    }

    public void paint(Graphics g) 
    {
        super.paint(g); // Call parent method for proper rendering
        drawGrid(g); // Draw the grid
        drawTitle(g); // Draw the title
        drawMessage(g); // Draw the message
        drawKeyboard(g); // Draw the keyboard
        
        ImageIcon icon = new ImageIcon("hint.png");
        Image img = icon.getImage();
        if (img != null) 
        	g.drawImage(img, getWidth() - 175, 35, 150, 150, null);
        
    }
    
    private void drawGrid(Graphics g) 
    {
        g.setFont(new Font("Arial", Font.BOLD, 30)); 
        
        // Draw each grid cell
        for (int row = 0; row < GRID_ROWS; row++) 
        {
            for (int col = 0; col < GRID_COLS; col++) 
            {
            	// Calculates x and y coordinate based off row, column, padding and size
                int x = 250 + col * (CELL_SIZE + PADDING); 
                int y = 200 + row * (CELL_SIZE + PADDING); 
                
                // Sets color for cell based off user guess
                g.setColor(colors[row][col]); 
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE); 
                
                // Draws gray border for all boxes that haven't been guessed yet
                g.setColor(Color.DARK_GRAY); 
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                
                // Draws letter for each box that the user has guessed
                g.setColor(Color.WHITE);
                g.drawString(grid[row][col], x + 25, y + 45); 
            }
        }
    }

    private void drawTitle(Graphics g) 
    {
    	// Draws and centers the Wordle title based of title length 
        g.setFont(new Font("Arial", Font.BOLD, 50)); 
        String title = "Wordle"; 
        int titleX = (getWidth() - g.getFontMetrics().stringWidth(title)) / 2; 
        g.setColor(Color.WHITE); 
        g.drawString(title, titleX, 125); 
    }

    private void drawMessage(Graphics g) 
    {
    	// Only draws message if it is not empty
        if (!message.isEmpty()) 
        {
        	// Draws and centers the message based of message length 
            g.setFont(new Font("Arial", Font.BOLD, 25));
            int messageX = (getWidth() - g.getFontMetrics().stringWidth(message)) / 2;
            g.setColor(Color.WHITE); 
            g.drawString(message, messageX, 175);
        }
    }

    private void drawKeyboard(Graphics g) 
    {
        // Starting positions for the keys on the X-axis for each row
        int[] startX = {100, 130, 75};
        int startY = 750;

        // Set font for key labels
        g.setFont(new Font("Arial", Font.BOLD, 30));

        // Loop through each row of keys
        for (int row = 0; row < keys.length; row++) 
        {
            int x = startX[row];  // Start X position for the current row

            // Loop through each key in the current row
            for (int col = 0; col < keys[row].length; col++) 
            {
                // Adjust width for special keys (ENTER and DELETE)
                int width = keys[row][col].equals("ENTER") || keys[row][col].equals("DELETE") ? KEY_WIDTH + 75 : KEY_WIDTH;

                // Set the key's background color and draw filled rounded rectangle
                g.setColor(keyColors[row][col]);
                g.fillRoundRect(x, startY + row * (KEY_HEIGHT + PADDING), width, KEY_HEIGHT, 12, 12);

                // Draw the border of the key
                g.setColor(Color.DARK_GRAY);
                g.drawRoundRect(x, startY + row * (KEY_HEIGHT + PADDING), width, KEY_HEIGHT, 12, 12);

                // Calculate position to center the label within the key
                FontMetrics metrics = g.getFontMetrics(g.getFont());
                int labelX = x + (width - metrics.stringWidth(keys[row][col])) / 2;
                int labelY = startY + row * (KEY_HEIGHT + PADDING) + (KEY_HEIGHT + metrics.getAscent() - metrics.getDescent()) / 2;

                // Set label color and draw the key label
                g.setColor(Color.WHITE);
                g.drawString(keys[row][col], labelX, labelY);

                // Move to the next key position
                x += width + PADDING;
            }
        }
    }
    
    public void mouseClicked(MouseEvent e) 
    {
        // If the game is over or a guess is being evaluated, ignore the click
        if (gameOver || evaluatingGuess) return;

        // Get the point where the mouse was clicked
        Point clickPoint = e.getPoint(); 
        
        // Check if click is inside hint image bounds
        int imgX = getWidth() - 175, imgY = 35, imgWidth = 150, imgHeight = 150;

        if (clickPoint.x >= imgX && clickPoint.x <= imgX + imgWidth && clickPoint.y >= imgY && clickPoint.y <= imgY + imgHeight) 
        {
            hint();
            return;
        }
        
        // Starting positions for the keys on the X-axis for each row
        int[] startX = {100, 130, 75}; 
        int startY = 750; 

        // Loop through each row of keys and grid cells
        for (int row = 0; row < keys.length; row++) 
        {            
            int x = startX[row];  // Start X position for the current row
            int y = startY + row * (KEY_HEIGHT + PADDING);  // Y position for the current row

            // Loop through each key in the current row
            for (String key : keys[row]) 
            {
                // Adjust width for special keys (ENTER and DELETE)
                int width = key.equals("ENTER") || key.equals("DELETE") ? KEY_WIDTH + 75 : KEY_WIDTH;

                // Check if the click is within the bounds of the key
                if (clickPoint.x >= x && clickPoint.x <= x + width && clickPoint.y >= y && clickPoint.y <= y + KEY_HEIGHT) 
                {
                    handleKeyPress(key); // Handle key press if click is inside the key's bounds
                    return; // Exit after handling the key press
                }

                // Move to the next key's position
                x += width + PADDING; 
            }
        }
    }
    
	public void keyPressed(KeyEvent e)
    {
        // If the game is over or a guess is being evaluated, ignore the key press
        if (gameOver || evaluatingGuess) return;

        // Variable to store the key pressed
        String key;

        // Check if the pressed key is ENTER or DELETE
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            key = "ENTER"; 
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            key = "DELETE"; 
        else 
            key = String.valueOf(e.getKeyChar()).toUpperCase(); // Otherwise, get the character of the key and convert it to uppercase

        // If the key is a valid key (A-Z, ENTER, or DELETE), handle the key press
        if (key.matches("[A-Z]|ENTER|DELETE")) 
            handleKeyPress(key);
    }
    
    private void handleKeyPress(String key) 
    {				
        if (key.equals("DELETE") && currentCol > 0) 
        {
        	grid[currentRow][--currentCol] = ""; // Delete last entered character
        	repaint(); // Update display
        }

        if (key.equals("ENTER")) 
            validateGuess(); // Validate guess when "ENTER" is pressed

        if (key.matches("[A-Z]") && currentCol < GRID_COLS)
        {
        	grid[currentRow][currentCol++] = key;  // Add character to grid
        	repaint(); // Update display
        }
    }
    
    private void validateGuess() 
    {
    	String guess = "";
        
    	// Joins all letters from users guess into a single string
    	for (int i = 0; i < GRID_COLS; i++) 
            guess += grid[currentRow][i].toLowerCase();
            	
        if (guess.length() < GRID_COLS) // Ensures user guess is a 5 letter word
            showMessage("Not enough letters.", 1200); 
        else if (!allWordList.contains(guess)) // Ensures user guess is a valid word (checking all 5 letter words in case people know advanced words)
            showMessage("Not in word list.", 1200); 
        else // If all conditions are met the guess gets evaluated to assign colors for each letters
            evaluateGuess(guess); 
    }
    
    private void showMessage(String message, int duration) 
    {
        // Set the message to be displayed and trigger a repaint to update the display
        this.message = message;
        repaint();

        // Create a timer that will clear the message after the specified duration
        Timer timer = new Timer(duration, e -> {
            // Clear the message and repaint the display after the timer ends
            this.message = "";
            repaint(); 
        });

        timer.setRepeats(false); // Ensure the timer does not repeat
        timer.start(); // Start the timer        
    }
    
    private void evaluateGuess(String guess) 
    {
        // Start evaluating the guess
        evaluatingGuess = true;

        // Arrays to track the matched letters (green and yellow)
        boolean[] greenMatched = new boolean[GRID_COLS];
        boolean[] yellowMatched = new boolean[GRID_COLS];

        // Array to count the occurrences of each letter in the correct word
        int[] correctLetterCount = new int[ALPHABET_LENGTH]; 

        // Count occurrences of each letter in the correct word
        for (int i = 0; i < GRID_COLS; i++) 
            correctLetterCount[correctWord.charAt(i) - 'a']++;

        // First pass: Check for green matches (correct letter in correct position)
        for (int i = 0; i < GRID_COLS; i++) 
        {
            if (guess.charAt(i) == correctWord.charAt(i)) 
            {
                greenMatched[i] = true; // Mark as green if the letter matches in position
                correctLetterCount[guess.charAt(i) - 'a']--; // Decrease the count for that letter
            }
        }

        // Second pass: Check for yellow matches (correct letter, wrong position)
        for (int i = 0; i < GRID_COLS; i++) 
        {
            if (!greenMatched[i]) // Skip already matched (green) letters
            {                 
                if (correctLetterCount[guess.charAt(i) - 'a'] > 0) // Checks for leftover letters not accounted for in green (yellow)
                {
                    yellowMatched[i] = true; // Mark as yellow if the letter exists in the correct word
                    correctLetterCount[guess.charAt(i) - 'a']--; // Decrease the count for that letter
                }
            }
        }

        // Timer to handle the animation of color changes for each letter
        Timer timer = new Timer(500, null);
        ActionListener listener = new ActionListener() 
        {
            int index = 0;

            public void actionPerformed(ActionEvent e) 
            {
                if (index < GRID_COLS) 
                {
                    // Apply color based on the match type (green, yellow, or none)
                    if (greenMatched[index]) 
                    {
                        colors[currentRow][index] = new Color(144, 238, 144); // Light green for correct match
                        updateKeyColor(guess.charAt(index), new Color(144, 238, 144)); // Update key color to light green
                    } 
                    else if (yellowMatched[index]) 
                    {
                        colors[currentRow][index] = new Color(255, 225, 64); // Yellow for correct letter, wrong position
                        updateKeyColor(guess.charAt(index), new Color(255, 225, 64)); // Update key color to yellow
                    } 
                    else 
                    {
                        colors[currentRow][index] = Color.DARK_GRAY; // Dark gray for incorrect letters
                        updateKeyColor(guess.charAt(index), Color.DARK_GRAY); // Update key color to yellow
                    }

                    index++; // Move to the next letter
                    repaint(); // Update the display
                } 
                else 
                {
                    timer.stop(); // Stop the timer once all letters are processed
                    processGameEnding(guess); // Handle the game ending (win or lose)
                    evaluatingGuess = false; // End the evaluation
                    currentCol = 0; // Reset column position for the next guess
                }
            }
        };

        // Add listener to the timer and start the animation
        timer.addActionListener(listener);
        timer.start();
    }
    
    private void updateKeyColor(char keyChar, Color newColor) 
    {
        // Convert the char to uppercase string to match key names
        String key = String.valueOf(keyChar).toUpperCase(); 

        // Iterate through the rows of keys
        for (int row = 0; row < keys.length; row++) 
        {
            // Iterate through the columns of each row
            for (int col = 0; col < keys[row].length; col++) 
            {
                // Check if the current key matches the given key
                if (keys[row][col].equals(key)) 
                {
                    // Get the current color of the key
                    Color currentColor = keyColors[row][col];

                    // If the new color is green and the current color is not green, update to green
                    if (newColor.equals(new Color(144, 238, 144)) && !currentColor.equals(new Color(144, 238, 144))) 
                        keyColors[row][col] = newColor;

                    // If the new color is yellow and the current color is light gray, update to yellow
                    else if (newColor.equals(new Color(255, 225, 64)) && currentColor.equals(Color.LIGHT_GRAY)) 
                        keyColors[row][col] = newColor; 

                    // If the new color is gray and the current color is light gray, update to gray
                    else if (newColor.equals(Color.DARK_GRAY) && currentColor.equals(Color.LIGHT_GRAY)) 
                        keyColors[row][col] = newColor; 
                }
            }
        }
    }
    
    private void processGameEnding(String guess)
    {
        // Check if the guess is correct or if the maximum number of guesses is reached
        if (guess.equals(correctWord) || ++currentRow == GRID_ROWS) 
        {
            gamesPlayed++;  // Increment the number of games played
            gameOver = true; // Set the game over flag

            boolean isCorrect = guess.equals(correctWord); // Check if the guess was correct

            // If the guess is correct, increment the win count for that specific guess number
            if (isCorrect) winCountPerGuess[currentRow]++;

            // Update the game stats (win percentage, streaks, etc.)
            updateGameStats(isCorrect); 

            // Show a message based on whether the guess was correct or not
            showMessage(isCorrect ? displayMessage[currentRow] : correctWord.toUpperCase(), isCorrect ? 1500 : 1750);

            // Create a new GameStats object to display the statistics
            GameStats gameStats = new GameStats();
            gameStats.displayStats(this, gamesPlayed, winPercentage, currentStreak, maxStreak, isCorrect ? currentRow + 1 : currentRow, winCountPerGuess, this); 
        }
    }
    
    private void updateGameStats(boolean won) 
    {
        // If the game was won, increment the current streak
        if (won) 
        {
            currentStreak++;
            maxStreak = Math.max(maxStreak, currentStreak); // Update the maximum streak if the current streak is higher
        }
        else 
            currentStreak = 0; // Reset the current streak if the game was not won
        
        double totalGamesWon = 0;
        
        // Calculate the total number of games won by summing the win counts for each guess
        for (int i = 0; i < winCountPerGuess.length; i++)
            totalGamesWon += winCountPerGuess[i];
        
        // Calculate the win percentage based on the total wins and games played
        winPercentage = (int) (totalGamesWon / gamesPlayed * 100);
    }
    
    // Placeholder event handler methods for various user interactions (key and mouse events) that are currently not implemented.
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
