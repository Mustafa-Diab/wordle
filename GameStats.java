import javax.swing.*;
import java.awt.*;

public class GameStats 
{
	JFrame frame;
	JLabel displayField;
    ImageIcon image;
	
    public static void displayStats(JFrame parent, int gamesPlayed, int winPercentage, int currentStreak, int maxStreak, int guesses, int[] winCountPerGuess, Wordle wordle) 
    {
        JDialog statsDialog = new JDialog(parent, "Game Over", true);
        statsDialog.setSize(600, 600);
        statsDialog.setLocationRelativeTo(null);
        statsDialog.setResizable(false);

        JPanel statsPanel = new JPanel() 
        {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                setBackground(Color.BLACK);
                drawStats(g, gamesPlayed, winPercentage, currentStreak, maxStreak, guesses, winCountPerGuess);
            }
        };

        statsPanel.setBackground(Color.BLACK);
        statsPanel.setLayout(null);
        
        JButton playAgain = new JButton("Play Again!");
        playAgain.setFont(new Font("Arial", Font.BOLD, 24));
        playAgain.setBackground(new Color(144, 238, 144));
        playAgain.setForeground(Color.WHITE);
        playAgain.setBounds(200, 480, 200, 50);
        playAgain.setBorder(BorderFactory.createEmptyBorder());
        playAgain.setFocusPainted(false);
        playAgain.addActionListener(e -> {
            statsDialog.dispose();
            wordle.dispose();
            wordle.resetGame();
            new Wordle();
        });
        
        statsPanel.add(playAgain);
        statsDialog.add(statsPanel);
        statsDialog.setVisible(true);
    }

    public static void drawStats(Graphics g, int gamesPlayed, int winPercentage, int currentStreak, int maxStreak, int guesses, int[] winCountPerGuess) 
    {
    	int[] stats = {gamesPlayed, winPercentage, currentStreak, maxStreak};
        int[] statsX = {190, 245, 320, 380};
        
        String[] labels = {"Games", "Played", "Win %", "Current", "Streak", "Max", "Streak"};
        int[] labelX = {statsX[0] - 15, statsX[0] - 15, statsX[1], statsX[2] - 15, statsX[2] - 15, statsX[3] - 5, statsX[3] - 10};
        int[] labelY = {145, 165, 145, 145, 165, 145, 165};
        
        String[] numGuessDisplay = {"1", "2", "3", "4", "5", "6"};
        int[] numGuessDisplayY = {280, 315, 350, 385, 420, 455};
        
        if (winPercentage == 100) statsX[1] = 235;
        if (winPercentage == 0) statsX[1] = 255;
        
        if (gamesPlayed >= 10) statsX[0] = 175;
        if (currentStreak >= 10) statsX[2] = 305;
        if (maxStreak >= 10) statsX[3] = 365;
                
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String title = "STATISTICS";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int panelWidth = 600;
        int titleX = (panelWidth - titleWidth) / 2;
        g.drawString(title, titleX, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 40));

        for (int i = 0; i < stats.length; i++)
            g.drawString(String.valueOf(stats[i]), statsX[i], 115);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        
        for (int i = 0; i < labels.length; i++)
            g.drawString(labels[i], labelX[i], labelY[i]);

        g.setFont(new Font("Arial", Font.BOLD, 20));

        String guessTitle = "GUESS DISTRIBUTION";
        int guessTitleWidth = g.getFontMetrics().stringWidth(title);
        int guessPanelWidth = 500;
        int guessTitleX = (guessPanelWidth - guessTitleWidth) / 2;
        g.drawString(guessTitle, guessTitleX, 225);
                
        for (int i = 0; i < numGuessDisplay.length; i++)
        {
        	if (winCountPerGuess[i] <= 17)
        	{
        		if (i == guesses - 1) 
                    g.setColor(new Color(144, 238, 144)); 
                else 
                    g.setColor(Color.DARK_GRAY); 
                
                g.fillRect(50, numGuessDisplayY[i] - 19, (winCountPerGuess[i] + 1) * 28, 22);
            	g.setColor(Color.WHITE);
                g.drawString(numGuessDisplay[i], 25, numGuessDisplayY[i]);
                g.drawString(String.valueOf(winCountPerGuess[i]), 58, numGuessDisplayY[i]);
        	}
        }
    }
}