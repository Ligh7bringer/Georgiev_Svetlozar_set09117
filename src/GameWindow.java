import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;

public class GameWindow extends JPanel implements Runnable, MouseListener, MouseMotionListener {
    //window dimensions
    public static final int WIDTH = 481;
    public static final int HEIGHT = Board.TILE_HEIGHT * Board.SIZE;

    //instance of board
    private static Board board;

    //mouse coordinates
    private static int mouseX, mouseY;
    //private boolean isClicked = false;
    private int clicks = 0;

    //game loop variables
    private Thread thread;
    private int targetFPS = 30;
    private long targetTime = 1000 / targetFPS;
    private boolean isRunning = false;

    //graphics
    private Graphics2D g2d;

    //JComponents
    private static JLabel currentPlayer;
    private static JButton button;

    //constructor
    private GameWindow() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); //set window width and height
        setFocusable(true); // this should help with mouse events?
        //add mouse listeners
        addMouseListener(this);
        addMouseMotionListener(this);

        currentPlayer = new JLabel("", SwingConstants.CENTER);
        currentPlayer.setFont(new Font("Serif", Font.PLAIN, 16));

        button = new JButton("CLICK ME");

        board = new Board(); // initialise the board
        start(); //start main game loop
    }

    // main game loop
    public void run() {
        long startTime, deltaTime, wait; //some time tracking
        while(isRunning) {
            startTime = System.currentTimeMillis();

            update();
            repaint();

            deltaTime = System.currentTimeMillis() - startTime;
            wait = targetTime - deltaTime;
            if(wait < 5)
                wait = 5;
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    //this method starts the thread
    private void start() {
        isRunning = true; //we need to know the game loop has started
        thread = new Thread(this, "Game loop"); //initialise the thread
        thread.start(); //start the thread
    }

    //stops the thread and the game
    private void stop() {
        isRunning = false; //we need to know the game loop has ended
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //some variables to track clicking
    private int sourceX, sourceY, destX, destY;
    //update, called every "frame", TODO: FIX CLICKING DETECTION!
    public void update() {
        currentPlayer.setText("     It's Player " + board.getCurrentPlayer() + "'s turn.       ");

        if(clicks == 1) {
            sourceX = mouseX;
            sourceY = mouseY;
            board.highlightTile(sourceX, sourceY);
        }
        if(clicks == 2) {
            destX = mouseX;
            destY = mouseY;
            board.update(sourceX, sourceY, destX, destY);
            clicks = 0;
        }
    }

    // this will draw everything hopefully
    public void paintComponent(Graphics g) {
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //turn antialising on for nicer graphics

        board.paintComponent(g2d); //the board paints itself, pieces paintComponent themselves in the board

        g2d.dispose(); //is this needed?
    }

    //get mouse coords when event we are listening to occurs
    private void setMousePosition(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    //main method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Checkers"); //create frame
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        //set properties of the frame
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(gridBag);
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(new GameWindow());

        c.gridx = 1;
        c.gridy = 0;

        c.weighty = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 5, 5);
        gridBag.setConstraints(button, c);
        frame.add(currentPlayer);

        c.weighty = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(150, 5, 5, 5);
        gridBag.setConstraints(button, c);
        frame.add(button);

        frame.pack();

        frame.setLocationRelativeTo(null); // this should display the window in the middle of the screen ?
        frame.setVisible(true);
        frame.setBackground(Color.lightGray);

    }

    //event listeners
    public void mouseClicked(MouseEvent e) {
        setMousePosition(e);
        clicks++;

        if(clicks > 2)
            clicks = 0;

        System.out.println("CLICKS " + clicks);
    }

    //might use these eventually
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    //need these although I am not going to use them
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
}