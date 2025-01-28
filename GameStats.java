import javax.swing.*;
import java.awt.*;

public class GameStats extends JFrame 
{
    public static final long serialVersionUID = 1L; 
    
    JPanel statsPanel;
    JButton button; 

    // Method to display statistics in a JFrame
    public void displayStats(JFrame parent, int gamesPlayed, int winPercentage, int currentStreak, int maxStreak, int guesses, int[] winCountPerGuess, Wordle wordle) 
    {
        setSize(600, 600); // Set frame size
        setLocationRelativeTo(null); // Center the frame on the screen
        setResizable(false); // Disable resizing

        // Create a panel to display statistics
        statsPanel = new JPanel() 
        {
            private static final long serialVersionUID = 1L;

            // Override paintComponent to draw custom graphics
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g); // Call parent method to ensure proper rendering
                setBackground(Color.BLACK); // Set background color
                drawStats(g, gamesPlayed, winPercentage, currentStreak, maxStreak, guesses, winCountPerGuess); // Draw stats
            }
        };

        statsPanel.setBackground(Color.BLACK); // Set panel background color
        statsPanel.setLayout(null); // Use no layout manager

        // Define font and color for buttons
        Font font = new Font("Arial", Font.BOLD, 24);
        Color lightGreen = new Color(144, 238, 144);

        // Add "Play Again" and "Main Menu" buttons
        addButton("Play Again!", lightGreen, font, 50, 490, 180, 50, wordle);
        addButton("Main Menu", lightGreen, font, 350, 490, 180, 50, wordle);

        add(statsPanel); // Add panel to the frame
        setVisible(true); // Make the frame visible
    }

    // Method to add a button to the panel
    public void addButton(String title, Color color, Font font, int x, int y, int width, int height, Wordle wordle) 
    {
        button = new JButton(title); // Create a button with the given title
        button.setFont(font); // Set button font
        button.setBackground(color); // Set button background color
        button.setForeground(Color.WHITE); // Set button text color
        button.setBounds(x, y, width, height); // Set button size and position
        button.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        button.setFocusPainted(false); // Disable focus painting

        // Add action listener for button click
        button.addActionListener(e -> 
        {
            dispose(); // Close the current frame
            wordle.dispose(); // Close the Wordle game frame

            // Open appropriate frame based on button title
            if (title.equals("Play Again!"))
                new Wordle(); // Start a new game
            else
                new Main_Menu(); // Return to main menu
        });

        statsPanel.add(button); // Add button to the panel
    }

    // Method to draw game statistics on the panel
    public void drawStats(Graphics g, int gamesPlayed, int winPercentage, int currentStreak, int maxStreak, int guesses, int[] winCountPerGuess) 
    {
        int[] stats = {gamesPlayed, winPercentage, currentStreak, maxStreak}; // Array of stats
        int[] statsX = {190, 245, 320, 380}; // X-coordinates for stats

        // Labels and their positions for statistics
        String[] labels = {"Games", "Played", "Win %", "Current", "Streak", "Max", "Streak"};
        int[] labelX = {statsX[0] - 15, statsX[0] - 15, statsX[1], statsX[2] - 15, statsX[2] - 15, statsX[3] - 5, statsX[3] - 10};
        int[] labelY = {145, 165, 145, 145, 165, 145, 165};

        // Guess distribution labels and positions
        String[] numGuessDisplay = {"1", "2", "3", "4", "5", "6"};
        int[] numGuessDisplayY = {280, 315, 350, 385, 420, 455};

        // Adjust positions based on specific values
        if (winPercentage == 100) statsX[1] = 235;
        if (winPercentage == 0) statsX[1] = 255;
        if (gamesPlayed >= 10) statsX[0] = 175;
        if (currentStreak >= 10) statsX[2] = 305;
        if (maxStreak >= 10) statsX[3] = 365;

        // Draw title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String title = "STATISTICS";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int panelWidth = 600;
        int titleX = (panelWidth - titleWidth) / 2;
        g.drawString(title, titleX, 50);

        // Draw stats values
        g.setFont(new Font("Arial", Font.PLAIN, 40));
        for (int i = 0; i < stats.length; i++)
            g.drawString(String.valueOf(stats[i]), statsX[i], 115);

        // Draw labels for stats
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        for (int i = 0; i < labels.length; i++)
            g.drawString(labels[i], labelX[i], labelY[i]);

        // Draw "Guess Distribution" title
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String guessTitle = "GUESS DISTRIBUTION";
        int guessTitleWidth = g.getFontMetrics().stringWidth(title);
        int guessPanelWidth = 500;
        int guessTitleX = (guessPanelWidth - guessTitleWidth) / 2;
        g.drawString(guessTitle, guessTitleX, 225);

        // Draw guess distribution bars
        for (int i = 0; i < numGuessDisplay.length; i++) 
        {
            int width = (winCountPerGuess[i] + 1) * 28;

            if (winCountPerGuess[i] >= 17) // Sets cap on distribution length;
                width = 504;

            // Highlight the current guess bar
            if (i == guesses - 1 && winCountPerGuess[i] > 0)
                g.setColor(new Color(144, 238, 144)); // Shows game won in n number of tries as light green
            else
                g.setColor(Color.DARK_GRAY); // Shows other game won in n number of tries as dark gray

            g.fillRect(50, numGuessDisplayY[i] - 19, width, 22); // Draw bar
            g.setColor(Color.WHITE);
            g.drawString(numGuessDisplay[i], 25, numGuessDisplayY[i]); // Guess number
            g.drawString(String.valueOf(winCountPerGuess[i]), 58, numGuessDisplayY[i]); // Win count
        }
    }
}
