import javax.swing.*;
import java.awt.*;

public class Main_Menu extends JFrame 
{
    JPanel mainMenuPanel; // Panel for displaying main menu
    JLabel titleLabel; // Label for the title
    JButton button; // Button to go to rules, playing, or exiting the program

    private static final long serialVersionUID = 1L; // Ensures version compatibility during serialization

    public Main_Menu() 
    {
        setTitle("Wordle"); // Sets the title of the window
        setSize(750, 850); // Sets the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes application when the window is closed
        setLocationRelativeTo(null); // Centers the window on the screen
        
        // Create the main panel with custom painting logic
        mainMenuPanel = new JPanel() 
        {
            private static final long serialVersionUID = 1L;
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g); // Call the parent method to ensure proper rendering
                paintScreen(g); // Custom painting for the screen
            }
        };

        mainMenuPanel.setBackground(Color.BLACK); // Set background color of the panel
        mainMenuPanel.setLayout(null); // Disable layout manager for custom positioning

        // Button styling properties
        Color lightGreen = new Color(144, 238, 144);
        Font font = new Font("Arial", Font.BOLD, 24);

        // Add buttons to the panel
        addButton("Start Game", lightGreen, font, 272, 275, 200, 50);
        addButton("Rules", lightGreen, font, 272, 425, 200, 50);
        addButton("Exit", lightGreen, font, 272, 575, 200, 50);

        add(mainMenuPanel); // Add the panel to the frame
        setVisible(true); // Make the window visible
    }

    // Custom painting for the screen
    private void paintScreen(Graphics g) 
    {
        Font font = new Font("Arial", Font.BOLD, 40); // Font for the "WORDLE" text

        Color yellow = new Color(255, 225, 64); // Define yellow color
        Color lightGreen = new Color(144, 238, 144); // Define light green color

        // Coordinates, colors, and text for the "WORDLE" blocks
        int[] x = {85, 185, 285, 385, 485, 585};
        Color[] color = {lightGreen, Color.DARK_GRAY, yellow, lightGreen, yellow, Color.DARK_GRAY};
        String[] wordle = {"W", "O", "R", "D", "L", "E"};

        // Draw blocks and letters for "WORDLE"
        for (int i = 0; i < color.length; i++)
        {
            g.setColor(color[i]);
            g.fillRect(x[i], 50, 70, 70); // Draw colored blocks
            g.setColor(Color.WHITE); // Set text color to white
            g.setFont(font); // Set font
            g.drawString(wordle[i], x[i] + 17, 100); // Draw letters
        }

        // Draw horizontal divider lines
        g.fillRect(117, 175, 500, 10);
        g.fillRect(127, 200, 480, 10);
        g.fillRect(117, 715, 500, 10);
        g.fillRect(127, 690, 480, 10);
    }

    // Add a button to the panel
    public void addButton(String title, Color color, Font font, int x, int y, int width, int height)
    {
        button = new JButton(title); // Create a button with the given title
        button.setFont(font); // Set the font of the button text
        button.setBackground(color); // Set the button background color
        button.setForeground(Color.WHITE); // Set the button text color
        button.setBounds(x, y, width, height); // Set button size and position
        button.setBorder(BorderFactory.createEmptyBorder()); // Remove button border
        button.setFocusPainted(false); // Remove focus highlight
        button.addActionListener(e -> 
        {
            dispose(); // Close the current window
            
            // Open a new window or exit based on the button pressed
            if (title.equals("Start Game"))
                new Wordle(); // Open the Wordle game window
            else if (title.equals("Rules"))
                new Rules(); // Open the rules window
            else
                System.exit(0); // Exit the application
        });
        
        mainMenuPanel.add(button); // Add the button to the main panel
    }
    
    // Create and display the main menu
    public static void main(String[] args) {new Main_Menu();}
}