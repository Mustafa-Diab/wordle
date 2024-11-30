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
    private final int GRID_ROWS = 6, GRID_COLS = 5, CELL_SIZE = 70, KEY_HEIGHT = 80, KEY_WIDTH = 60, PADDING = 10;
    private int currentRow = 0, currentCol = 0;
    private static int[] winCountPerGuess = new int[6];
    private static int gamesPlayed = 0, winPercentage = 0, currentStreak = 0, maxStreak = 0;
    private boolean gameOver = false;
    private JLabel label;
    private String correctWord, message = "";
    private String[] displayMessage = {"Genius", "Magnificent", "Impressive", "Splendid", "Great", "Phew"};
    private String[][] grid = new String[6][5];
    private Color[][] colors = new Color[6][5];
    private ArrayList<String> wordList = new ArrayList<>();

    public Wordle() 
    {
        super("Wordle");
        setSize(900, 1100);
        setLocationRelativeTo(null);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.addKeyListener(this);
        this.addMouseListener(this);

        label = new JLabel();
        label.setBounds(50, 50, 400, 50);
        label.setForeground(Color.WHITE);
        this.getContentPane().setBackground(Color.black);
        this.add(label);

        for (int row = 0; row < 6; row++) 
        {
            for (int col = 0; col < 5; col++) 
            {
                grid[row][col] = "";
                colors[row][col] = Color.BLACK;
            }
        }

        selectRandomWord();
        paint(getGraphics());
    }

    private void selectRandomWord() 
    {
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/Mustafa/Desktop/Wordle_Word_List.txt"))) 
        {
            String word;
            while ((word = reader.readLine()) != null)
                wordList.add(word.toLowerCase());
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "Error reading word list: " + e.getMessage());
            System.exit(1);
        }

        correctWord = wordList.get(new Random().nextInt(wordList.size()));
        System.out.println(correctWord);
    }
    
    public void paint(Graphics g) 
    {
        super.paint(g);
        drawGrid(g);
        drawTitle(g);
        drawMessage(g);
        drawKeyboard(g);
    }

    private void drawGrid(Graphics g) 
    {
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        for (int row = 0; row < GRID_ROWS; row++) 
        {
            for (int col = 0; col < GRID_COLS; col++) 
            {
                int x = 250 + col * (CELL_SIZE + PADDING);
                int y = 200 + row * (CELL_SIZE + PADDING);
                g.setColor(colors[row][col]);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.WHITE);
                g.drawString(grid[row][col], x + 25, y + 45);
            }
        }
    }

    private void drawTitle(Graphics g) 
    {
        g.setFont(new Font("Arial", Font.BOLD, 50));
        
        String title = "Wordle";
        int titleX = (getWidth() - g.getFontMetrics().stringWidth(title)) / 2;
        
        g.setColor(Color.WHITE);
        g.drawString(title, titleX, 125);
    }

    private void drawMessage(Graphics g)
    {
        if (!message.isEmpty()) 
        {
            g.setFont(new Font("Arial", Font.BOLD, 25));
            int messageX = (getWidth() - g.getFontMetrics().stringWidth(message)) / 2;
            g.setColor(Color.WHITE);
            g.drawString(message, messageX, 175);
        }
    }

    private void drawKeyboard(Graphics g) 
    {
        String[][] keys = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
        };

        int[] startX = {100, 130, 75};
        int startY = 750;

        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        for (int row = 0; row < keys.length; row++) 
        {
            for (String key : keys[row]) 
            {	
                int width = key.equals("ENTER") || key.equals("DELETE") ? KEY_WIDTH + 75 : KEY_WIDTH;
                drawKey(g, key, startX[row], startY + row * (KEY_HEIGHT + PADDING), width, KEY_HEIGHT);
                startX[row] += width + PADDING;
            }
        }
    }

    private void drawKey(Graphics g, String label, int x, int y, int width, int height) 
    {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(x, y, width, height, 12, 12);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(x, y, width, height, 12, 12);

        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int labelX = x + (width - metrics.stringWidth(label)) / 2;
        int labelY = y + (height + metrics.getAscent() - metrics.getDescent()) / 2;
        g.setColor(Color.WHITE);
        g.drawString(label, labelX, labelY);
    }

    private void handleKeyPress(String key) 
    {    	
        if (key.equals("DELETE") && currentCol > 0) 
            grid[currentRow][--currentCol] = "";
        if (key.equals("ENTER")) 
            validateGuess();
        if (key.matches("[A-Z]") && currentCol < GRID_COLS) 
            grid[currentRow][currentCol++] = key;
        
        repaint();
    }

    private void validateGuess() 
    {
        String guess = String.join("", grid[currentRow]).toLowerCase();
        
        if (guess.length() < GRID_COLS) 
            showMessage("Not enough letters.", 1500);
        else if (!wordList.contains(guess))
            showMessage("Not in word list.", 1500);
        else 
            evaluateGuess(guess);
    }

    private void evaluateGuess(String guess) 
    {
        boolean[] correctUsed = new boolean[GRID_COLS];

        for (int i = 0; i < GRID_COLS; i++) 
        {
            if (guess.charAt(i) == correctWord.charAt(i)) 
            {
                colors[currentRow][i] = new Color(144, 238, 144);
                correctUsed[i] = true;
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
                        colors[currentRow][i] = Color.YELLOW;
                        correctUsed[j] = true;
                        break;
                    }
                }
                if (colors[currentRow][i] == Color.BLACK)
                    colors[currentRow][i] = Color.DARK_GRAY;
            }
        }

        if (guess.equals(correctWord)) 
        {
            gamesPlayed++;
            gameOver = true;
            winCountPerGuess[currentRow]++;
            updateGameStats(true);
            repaint();
            showMessage(displayMessage[currentRow], 1500);
            GameStats.displayStats(this, gamesPlayed, winPercentage, currentStreak, maxStreak, currentRow + 1, winCountPerGuess, this);
        } 
        else if (++currentRow == GRID_ROWS) 
        {
            gamesPlayed++;
            gameOver = true;
            updateGameStats(false); 
            repaint();
            showMessage(correctWord.toUpperCase(), 1750);
            GameStats.displayStats(this, gamesPlayed, winPercentage, currentStreak, maxStreak, currentRow, winCountPerGuess, this);
        }

        currentCol = 0;
    }
    
    private void updateGameStats(boolean won) 
    {
        if (won) 
            currentStreak++;
        else 
            currentStreak = 0;

        maxStreak = Math.max(maxStreak, currentStreak);
        
        int totalGamesWon = 0;
        
        for (int i = 0; i < winCountPerGuess.length; i++)
        	totalGamesWon += winCountPerGuess[i];
        
        winPercentage = (gamesPlayed == 0) ? 0 : (int) ((double) totalGamesWon / gamesPlayed * 100);
    }
    
    public void resetGame()
    {
        currentRow = 0;
        currentCol = 0;
        gameOver = false;
        message = "";

        for (int row = 0; row < GRID_ROWS; row++) 
        {
            for (int col = 0; col < GRID_COLS; col++) 
            {
                grid[row][col] = "";
                colors[row][col] = Color.BLACK;
            }
        }
    }
    
    private void showMessage(String msg, int timeLength) 
    {
        message = msg;

        Timer timer = new Timer(timeLength, new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                message = "";  
                repaint(); 
            }
        });
        
        timer.setRepeats(false);
        timer.start();
    }

    public void keyPressed(KeyEvent e) 
    {
    	if (gameOver) return;
    	
    	String key;

    	if (e.getKeyCode() == KeyEvent.VK_ENTER)
    	    key = "ENTER";
    	else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
    	    key = "DELETE";
    	else 
    	    key = String.valueOf(e.getKeyChar()).toUpperCase();
    	
    	if (key.matches("[A-Z]|ENTER|DELETE")) 
    		handleKeyPress(key);
    }

    public void mouseClicked(MouseEvent e) 
    {
    	if (gameOver) return;
    	
        Point clickPoint = e.getPoint();
        
        String[][] keys = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
        };

        int[] startX = {100, 130, 75};
        int startY = 750;

        for (int row = 0; row < keys.length; row++) 
        {
            int x = startX[row];
            int y = startY + row * (KEY_HEIGHT + PADDING);

            for (String key : keys[row]) 
            {
                int width = key.equals("ENTER") || key.equals("DELETE") ? KEY_WIDTH + 75 : KEY_WIDTH;

                if (clickPoint.x >= x && clickPoint.x <= x + width &&    
                    clickPoint.y >= y && clickPoint.y <= y + KEY_HEIGHT){
                    handleKeyPress(key);
                    return; 
                }

                x += width + PADDING;
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