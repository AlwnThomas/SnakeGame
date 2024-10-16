import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 30;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 120;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int applesEaten;
    int applex;
    int appley;
    char direction = 'R';
    boolean running = false;
    boolean startScreen = true; // Controls the start screen display
    Timer timer;
    Random random;
    Image appleImg;
    Image startImg;
    Image overImg;
    Image grassDark;
    Image grassLight;

    private HighScoreManager highScoreManager = new HighScoreManager(); // Manage high scores

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(178, 214, 96));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Load the images
        ImageIcon start = new ImageIcon("/Users/alwn/IdeaProjects/Snake/images/snake.jpg");
        startImg = start.getImage();
        ImageIcon apple = new ImageIcon("/Users/alwn/IdeaProjects/Snake/images/apple.png");
        appleImg = apple.getImage();
        ImageIcon over = new ImageIcon("/Users/alwn/IdeaProjects/Snake/images/gameover.png");
        overImg = over.getImage();
        ImageIcon grassD = new ImageIcon("/Users/alwn/IdeaProjects/Snake/images/grassDark.png");
        grassDark = grassD.getImage();
        ImageIcon grassL = new ImageIcon("/Users/alwn/IdeaProjects/Snake/images/GrassLight.png");
        grassLight = grassL.getImage();

        // Start the timer
        timer = new Timer(DELAY, this);
    }

    public void startGame() {
        newApple();
        bodyParts = 2; // Reset snake size
        applesEaten = 0; // Reset score
        direction = 'R'; // Reset direction
        running = true;
        startScreen = false; // Hide start screen
        timer.start(); // Start the game timer
    }

    public void restartGame() {
        newApple();
        bodyParts = 2;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // Reset snake position at the center
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        timer.restart(); // Restart the timer
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (startScreen) {
            // Draw the start screen
            g.drawImage(startImg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        } else {
            draw(g);
            score(g);
            if (!running) {
                gameOver(g); // Show game over screen
            }
        }
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw the grid (checkerboard pattern)
            for (int row = 0; row < SCREEN_HEIGHT / UNIT_SIZE; row++) {
                for (int col = 0; col < SCREEN_WIDTH / UNIT_SIZE; col++) {
                    if ((row + col) % 2 == 0) {
                        g.drawImage(grassDark, col * UNIT_SIZE, row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, this);
                    } else {
                        g.drawImage(grassLight, col * UNIT_SIZE, row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, this);
                    }
                }
            }

            // Draw apple
            g.drawImage(appleImg, applex, appley, UNIT_SIZE, UNIT_SIZE, this);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(52, 93, 207)); // Snake head
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    g.setColor(Color.white); // Snake eyes
                    g.fillOval(x[0] + UNIT_SIZE / 4, y[0] + UNIT_SIZE / 4, UNIT_SIZE / 5, UNIT_SIZE / 5);
                    g.fillOval(x[0] + UNIT_SIZE / 2, y[0] + UNIT_SIZE / 4, UNIT_SIZE / 5, UNIT_SIZE / 5);
                    g.setColor(new Color(173, 42, 57));
                    g.fillRect(x[0] + (UNIT_SIZE - 16), y[0] + (UNIT_SIZE - 10), UNIT_SIZE / 4, UNIT_SIZE / 5);
                } else {
                    g.setColor(new Color(81, 121, 230)); // Snake body
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    g.setColor(new Color(52, 93, 230)); // Snake body outline
                    g.drawRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
    }

    public void gameOver(Graphics g) {

        g.drawImage(overImg,0,0,SCREEN_WIDTH,SCREEN_HEIGHT,this);

        // Update and display high scores
        highScoreManager.addScore(applesEaten); // Update high scores with the current score
        g.setColor(new Color(232, 252, 236));
        g.setFont(new Font("ArcadeClassic", Font.PLAIN, 40));
        g.drawString("High Scores:", 190, 220);

        // Display the top 5 high scores
        ArrayList<Integer> highScores = highScoreManager.getHighScores();
        for (int i = 0; i < highScores.size(); i++) {
            g.drawString("•" + " " + highScores.get(i), 190, 220+ (i + 1) * 30);
        }
    }

    public void score(Graphics g) {
        g.setColor(new Color(214, 83, 48));
        g.setFont(new Font("ArcadeClassic", Font.PLAIN, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten) - 10, g.getFont().getSize());
    }

    public void newApple() {
        applex = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appley = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        // Shift body parts
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Move the head
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if (x[0] == applex && y[0] == appley) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollision() {
        // Check collision with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // Check boundary collision
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (startScreen) {
                // Start the game when Enter or Space is pressed
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startGame();
                }
            } else if (!running) {
                // Restart the game after game over when Enter is pressed
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    restartGame();
                }
            } else {
                // Handle direction changes when the game is running
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                }
            }
        }
    }

    // HighScoreManager class to manage high scores
    public class HighScoreManager {
        private ArrayList<Integer> highScores;

        public HighScoreManager() {
            highScores = new ArrayList<>(Collections.nCopies(5, 0)); // Initialize with 5 zero scores
        }

        public void addScore(int score) {
            highScores.add(score);
            Collections.sort(highScores, Collections.reverseOrder()); // Sort in descending order
            if (highScores.size() > 5) {
                highScores.remove(5); // Keep only top 5 scores
            }
        }

        public ArrayList<Integer> getHighScores() {
            return highScores;
        }
    }
}


