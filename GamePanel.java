import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import java.util.concurrent.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 15;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    private static int DELAY;
    private static int scoreIncrement;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running =  false;
    Timer timer;
    Random random;
    int toGreenApple = 0;
    boolean isGreenApple = false;
    int greenAppleTimeLeft = 0;
    ScheduledExecutorService greenAppleTimer;
    int greenAppleScore;



    static void setDELAY(int difficulty){
        DELAY = difficulty;
    }
    static void setScoreIncrement(int increment){
        scoreIncrement = increment;
    }

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){

        if(running){
            for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++){
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            if(!isGreenApple){
                g.setColor(Color.red);
                g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            }
            else{
                g.setColor(Color.green);
                g.fillOval(appleX, appleY, UNIT_SIZE*2, UNIT_SIZE*2);
            }
            if(isGreenApple && greenAppleTimeLeft > 0){
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                FontMetrics metrics = getFontMetrics(g.getFont());
                String timeText = "Grab The Green Apple !!! " + greenAppleTimeLeft;
                g.drawString(timeText, (SCREEN_WIDTH - metrics.stringWidth(timeText)) / 2, SCREEN_HEIGHT - 50);
            }


            for(int i=0;i<bodyParts;i++){
                if(i == 0 && direction == 'R'){
                    g.setColor(Color.red);
                    g.fillArc(x[i]-UNIT_SIZE/2,y[i],  UNIT_SIZE, UNIT_SIZE, -90, 180);
                }
                else if(i == 0 && direction == 'U'){
                    g.setColor(Color.red);
                    g.fillArc(x[i],y[i]+UNIT_SIZE/2, UNIT_SIZE, UNIT_SIZE, 0, 180);
                }
                else if(i == 0 && direction == 'L'){
                    g.setColor(Color.red);
                    g.fillArc(x[i]+UNIT_SIZE/2,y[i],  UNIT_SIZE, UNIT_SIZE, 90, 180);
                }
                else if(i == 0 && direction == 'D'){
                    g.setColor(Color.red);
                    g.fillArc(x[i],y[i]-UNIT_SIZE/2,  UNIT_SIZE, UNIT_SIZE, 180, 180);
                }
                else{
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(new Color(128, 255, 128));
            g.setFont(new Font("Serif", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        }
        else{
            gameOver(g);
        }
    }


    public void newApple(){
        if(toGreenApple >= 5){
            isGreenApple = true;
            greenAppleTimeLeft = 5;
            greenAppleScore = scoreIncrement*5;

            if(greenAppleTimer != null && !greenAppleTimer.isShutdown()) {
                greenAppleTimer.shutdownNow();
            }

            greenAppleTimer = Executors.newSingleThreadScheduledExecutor();
            greenAppleTimer.scheduleAtFixedRate(() -> {
                greenAppleTimeLeft--;
                greenAppleScore--;
                if(greenAppleTimeLeft <= 0 && isGreenApple){
                    isGreenApple = false;
                    toGreenApple = 0;
                    newApple();
                    greenAppleTimer.shutdown();
                }
                repaint();
            }, 1, 1, TimeUnit.SECONDS);
        }
        else {
            isGreenApple = false;
        }

        appleX = random.nextInt(SCREEN_WIDTH /UNIT_SIZE-1)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_WIDTH /UNIT_SIZE-1)*UNIT_SIZE;
    }

    public void move(){
        for(int i = bodyParts;i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    public void checkApple(){
        if(!isGreenApple){
            if((x[0] == appleX) && (y[0] == appleY)){
                bodyParts++;
                toGreenApple++;
                applesEaten += scoreIncrement;
                newApple();
            }
        }
        else{
            if(((x[0] == appleX+UNIT_SIZE) && (y[0] == appleY+UNIT_SIZE || y[0] == appleY)) || (x[0] == appleX) && (y[0] == appleY || y[0] == appleY+UNIT_SIZE)){
                bodyParts++;
                applesEaten += greenAppleScore;
                isGreenApple = false;
                toGreenApple = 0;
                newApple();
            }
        }
    }
    public void checkCollisions(){
        //checks if head collides with body
        for(int i=bodyParts;i>0;i--){
            if ((x[0] == x[i] && (y[0] == y[i]))) {
                running = false;
                break;
            }
        }
        //check if head touches the left border
        if(x[0] < 0){
            x[0] = SCREEN_WIDTH;
            move();
        }
        //check if head touches the right border
        if(x[0] > SCREEN_WIDTH){
            x[0] = 0;
            move();
        }
        //check if head touches the top border
        if(y[0] < 0){
            y[0] = SCREEN_HEIGHT;
            move();
        }
        //check if head touches the bottom border
        if(y[0] > SCREEN_HEIGHT){
            y[0] = 0;
        }
        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){

        g.setColor(Color.red);
        g.setFont(new Font("Serif", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
//        game over text
        g.setColor(Color.red);
        g.setFont(new Font("Serif", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A, KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_D, KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_W, KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_S, KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;

            }
        }
    }
}