import javax.swing.*;
import java.awt.*;

public class Rules extends JFrame
{
    JPanel rulesPanel; // Panel for displaying rules
    JButton button; // Button to return to the main menu

    private static final long serialVersionUID = 1L; // Ensures serialization compatibility
    
    public Rules() 
    {
        setTitle("Rules"); // Sets the title of the window
        setSize(750, 950); // Sets the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        setLocationRelativeTo(null); // Centers the window on the screen

        // Panel with custom painting logic
        rulesPanel = new JPanel()
        {
            private static final long serialVersionUID = 1L; // Serialization compatibility
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g); // Call parent method for proper rendering
                paintScreen(g); // Custom screen painting
            }
        };
        
        rulesPanel.setBackground(Color.BLACK); // Set panel background color
        rulesPanel.setLayout(null); // Disable layout manager for custom positioning

        // Instructions to display in the panel
        String[] instructions = {
            "You have 6 attempts to guess a 5-letter word.",
            "",
            "Each guess must be a valid word.",
            "Press or click enter to submit your guess.",
            "",
            "After your submission, the color of the tiles", 
            "will change as shown in the examples below.",
        };
        
        String[] labels = {"1.", "2.", "3."}; // Labels for the instructions
        
        int yPosition = 85; // Starting y-coordinate for instructions
        Font instructionFont = new Font("Arial", Font.PLAIN, 20); // Font for instructions
        Font labelFont = new Font("Arial", Font.BOLD, 20); // Font for labels
        
        // Add instruction text to the panel
        for (String instruction : instructions) 
        {
            JLabel instructionLabel = new JLabel(instruction);
            instructionLabel.setFont(instructionFont); // Sets the text font
            instructionLabel.setForeground(Color.WHITE); // Set text color to white
            instructionLabel.setBounds(175, yPosition, 650, 30); // Set position and size
            rulesPanel.add(instructionLabel);
            yPosition += 20; // Increment y-coordinate for next set of instructions
        }
        
        yPosition = 85; // Reset y-coordinate for labels

        // Add labels to the panel
        for (int i = 0; i < labels.length; i++) 
        {
            if (i == 2) // Add extra spacing for the third label
                yPosition += 20;
            
            JLabel instructionLabel = new JLabel(labels[i]);
            instructionLabel.setFont(labelFont);
            instructionLabel.setForeground(Color.WHITE); // Set text color to white
            instructionLabel.setBounds(140, yPosition, 650, 30); // Set position and size
            rulesPanel.add(instructionLabel);
            
            yPosition += 40; // Increment y-coordinate
        }

        // Add a button to return to the main menu
        Color lightGreen = new Color(144, 238, 144); // Button background color
        Font font = new Font("Arial", Font.BOLD, 24); // Button font

        button = new JButton("Return to Main Menu"); // Button title
        button.setFont(font); // Sets font of the button text
        button.setBackground(lightGreen); // Sets background of button
        button.setForeground(Color.WHITE); // Set text color to white
        button.setBounds(235, 835, 275, 50); // Set position and size
        button.setBorder(BorderFactory.createEmptyBorder()); // Remove button border
        button.setFocusPainted(false); // Remove focus highlight
        button.addActionListener(e -> 
        {
            dispose(); // Close the current window
            new Main_Menu(); // Open the main menu window
        });
        rulesPanel.add(button); // Add button to the panel
        
        add(rulesPanel); // Add the panel to the frame
        setVisible(true); // Make the window visible
    }
    
    // Custom painting for the screen
    private void paintScreen(Graphics g) 
    {   
        Font font = new Font("Arial", Font.BOLD, 25); // Font for the title
        Color yellow = new Color(255, 225, 64); // Yellow color
        Color lightGreen = new Color(144, 238, 144); // Light green color
        
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("How To Play", 300, 50); // Draw the title
        
        // Colors, text, and descriptions for Wordle examples
        Color[][] color = {
            {lightGreen, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY},
            {Color.DARK_GRAY, Color.DARK_GRAY, yellow, Color.DARK_GRAY, Color.DARK_GRAY},
            {Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY}
        };
        String[][] wordle = {{"A", "R", "I", "S", "E"}, {"V", "E", "N", "O", "M"}, {"G", "R", "E", "E", "K"}};
        String[] information = {
            "The letter    is in the word and in the correct spot.", 
            "The letter    is in the word but in the wrong spot.", 
            "       The letter    is not in the word in any spot."
        };
        String[] letterBold = {"A", "N", "E"}; // Letters emphasized in examples
        
        int[] letterBoldCoord = {224, 224, 266}; // Coordinates for emphasized letters
        int[] boxToFill = {0, 2, 3}; // Indices of filled boxes
        int[] x = {130, 230, 330, 430, 530, 630, 730}; // X-coordinates for boxes

        font = new Font("Arial", Font.BOLD, 40);
        drawBoxes(g, color, wordle, font, x, boxToFill, information, letterBold, letterBoldCoord);

        // Draw horizontal divider lines
        g.setColor(Color.DARK_GRAY);
        g.fillRect(100, 270, 560, 5);
        g.fillRect(100, 800, 560, 5);

        font = new Font("Arial", Font.BOLD, 25);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Examples", 310, 320); // Draw "Examples" title
    }
    
    // Draw Wordle example boxes with explanations
    private void drawBoxes(Graphics g, Color[][] color, String[][] text, Font font, int[] xCoord, int[] boxToFill, String[] information, String[] letterBold, int[] letterBoldCoord)
    {
        int y = 0; // Starting y-coordinate
        int boxSize = 70; // Size of each box
        int textOffsetX = 20; // X-offset for text inside boxes
        int textOffsetY = 50; // Y-offset for text inside boxes

        for (int i = 0; i < 3; i++) 
        {
            int boxY = y + 350; // Y-coordinate for the current row
            for (int j = 0; j < color[i].length; j++) 
            {
                g.setColor(color[i][j]);
                int boxX = xCoord[j]; // X-coordinate for the current box

                if (j == boxToFill[i])
                    g.fillRect(boxX, boxY, boxSize, boxSize); // Draw filled box
                else 
                    g.drawRect(boxX, boxY, boxSize, boxSize); // Draw outlined box

                g.setFont(font);
                g.setColor(Color.WHITE);
                g.drawString(text[i][j], boxX + textOffsetX, boxY + textOffsetY); // Draw letter
            }
            
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            g.drawString(information[i], 130, boxY + 120); // Draw description
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString(letterBold[i], letterBoldCoord[i], boxY + 120); // Draw emphasized letter
            
            y += 150; // Move to the next row
        }
    }
}