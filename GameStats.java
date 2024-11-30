import javax.swing.*;
import java.awt.*;

public class GameStats 
{
    JFrame frame; 
    JLabel displayField; 
    ImageIcon image;

    // Displays the game statistics in a dialog
    public static void displayStats(JFrame parent, int gamesPlayed, int winPercentage, int currentStreak, int maxStreak, int guesses, int[] winCountPerGuess, Wordle wordle) 
    {
        JDialog statsDialog = new JDialog(parent, "Game Over", true); // Creates a modal dialog
        statsDialog.setSize(600, 600); // Sets the dialog size
        statsDialog.setLocationRelativeTo(null); // Centers the dialog on the screen
        statsDialog.setResizable(false); // Prevents resizing

        // Panel for displaying statistics
        JPanel statsPanel = new JPanel() 
        {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g); // Calls parent paintComponent method
                setBackground(Color.BLACK); // Sets the background color
                drawStats(g, gamesPlayed, winPercentage, currentStreak, maxStreak, guesses, winCountPerGuess);
            }
        };

        statsPanel.setBackground(Color.BLACK); // Sets panel background color
        statsPanel.setLayout(null); // Disables default layout for custom positioning
        
        // "Play Again" button configuration
        JButton playAgain = new JButton("Play Again!");
        playAgain.setFont(new Font("Arial", Font.BOLD, 24)); // Sets font style and size
        playAgain.setBackground(new Color(144, 238, 144)); // Sets background color to light green
        playAgain.setForeground(Color.WHITE); // Sets text color
        playAgain.setBounds(200, 480, 200, 50); 
        playAgain.setBorder(BorderFactory.createEmptyBorder()); 
        playAgain.setFocusPainted(false); 
        playAgain.addActionListener(e -> { 
            statsDialog.dispose(); // Closes the stats dialog
            wordle.dispose(); // Closes the current game
            wordle.resetGame(); // Resets the game state
            new Wordle(); // Starts a new game
        });

        statsPanel.add(playAgain); // Adds the button to the panel
        statsDialog.add(statsPanel); // Adds the panel to the dialog
        statsDialog.setVisible(true); // Displays the dialog
    }

    // Draws the game statistics on the panel
    public static void drawStats(Graphics g, int gamesPlayed, int winPercentage, int currentStreak, int maxStreak, int guesses, int[] winCountPerGuess) 
    {
        // Stats and positions for the main statistics
        int[] stats = {gamesPlayed, winPercentage, currentStreak, maxStreak};
        int[] statsX = {190, 245, 320, 380}; // X-coordinates for stats display
        
        // Labels for the stats
        String[] labels = {"Games", "Played", "Win %", "Current", "Streak", "Max", "Streak"};
        int[] labelX = {statsX[0] - 15, statsX[0] - 15, statsX[1], statsX[2] - 15, statsX[2] - 15, statsX[3] - 5, statsX[3] - 10};
        int[] labelY = {145, 165, 145, 145, 165, 145, 165};

        // Guess distribution labels
        String[] numGuessDisplay = {"1", "2", "3", "4", "5", "6"};
        int[] numGuessDisplayY = {280, 315, 350, 385, 420, 455}; // Y-coordinates for guess distribution

        // Adjust positions dynamically based on values
        if (winPercentage == 100) statsX[1] = 235;
        if (winPercentage == 0) statsX[1] = 255;
        if (gamesPlayed >= 10) statsX[0] = 175;
        if (currentStreak >= 10) statsX[2] = 305;
        if (maxStreak >= 10) statsX[3] = 365;

        g.setColor(Color.WHITE); // Sets text color to white
        g.setFont(new Font("Arial", Font.BOLD, 20)); // Sets font for the title

        // Draws the "STATISTICS" title
        String title = "STATISTICS";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int panelWidth = 600;
        int titleX = (panelWidth - titleWidth) / 2;
        g.drawString(title, titleX, 60);

        g.setFont(new Font("Arial", Font.PLAIN, 40)); // Sets font for the stats numbers
        for (int i = 0; i < stats.length; i++) 
            g.drawString(String.valueOf(stats[i]), statsX[i], 115); // Draws the stats numbers

        g.setFont(new Font("Arial", Font.PLAIN, 15)); // Sets font for the labels
        for (int i = 0; i < labels.length; i++) 
            g.drawString(labels[i], labelX[i], labelY[i]); // Draws the stats labels

        g.setFont(new Font("Arial", Font.BOLD, 20)); 
        String guessTitle = "GUESS DISTRIBUTION";
        int guessTitleWidth = g.getFontMetrics().stringWidth(title);
        int guessPanelWidth = 500;
        int guessTitleX = (guessPanelWidth - guessTitleWidth) / 2;
        g.drawString(guessTitle, guessTitleX, 225); 
                
        // Draws the guess distribution bars
        for (int i = 0; i < numGuessDisplay.length; i++) 
        {
        	int width = (winCountPerGuess[i] + 1) * 28;
            
        	if (winCountPerGuess[i] >= 17)
            	width = 504;
        	
            if (i == guesses - 1 && winCountPerGuess[i] > 0) 
                g.setColor(new Color(144, 238, 144)); // Highlights the bar for the current guess
            else 
                g.setColor(Color.DARK_GRAY); // Sets color for other bars

            g.fillRect(50, numGuessDisplayY[i] - 19, width, 22); // Draws the bar
            g.setColor(Color.WHITE); // Resets color to white for text
            g.drawString(numGuessDisplay[i], 25, numGuessDisplayY[i]); 
            g.drawString(String.valueOf(winCountPerGuess[i]), 58, numGuessDisplayY[i]);
        }
    }
}
