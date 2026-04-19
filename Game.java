import javafx.application.Application;
import javafx.application.Platform;

import javafx.stage.Stage;
import javafx.stage.Modality;

import javafx.event.*;

import javafx.scene.input.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;

import java.util.*;
import javafx.util.Duration;


/**
 * This class is the main entry of the game. 
 * It builds up the GUI and set up the game. 
 *  
 * @author Yingtao Zheng, Kexin Wang, Langyuan Huang, Junhao Zhou
 * @version 2.0 (25-03-2025)
 */
public class Game extends Application
{
    private Stage stage;
    private GameMenu gameMenu;
    
    private BorderPane root;
    private Pane gamePane;
    private ImageView playerView;
    public Text hpText;
    private Text scoreText;
    private Text levelText;
    private Text bossHPText;
    private Text tutorialText;
    private Text tutorialText2;
    public static int score = 0;
    private Scene scene;
    private Random random = new Random();
    
    private boolean isPaused = false;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    
    private AnimationTimer animationTimer;
    private Timeline enemySpawner;
    
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    
    private double menuBarHeight;
    private double enemySpawnGap = 1.5;
    private int maxEnemyOnScreen = 10;
    
    private Ship ship;
    
    private double playerSpeed;
    private boolean keepHP = false;
    
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<ImageView> bulletViewList = new ArrayList<>();
    private ArrayList<Enemy> enemys = new ArrayList<>();   
    private ArrayList<ImageView> enemyViewList = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<ImageView> itemViewList = new ArrayList<>();
    private ArrayList<eBullet> enemyBullets = new ArrayList<>();
    private ArrayList<ImageView> enemyBulletsViewList = new ArrayList<>();
    private static Game instance;
    
    //probability of dropping items
    private double dropItem = 0.4;
    
    private double dRed   = 0.2;
    private double dGreen = 0.2;
    private double dBlue  = 0.1; 
    private double dYellow = 0.5;
    
    //buff
    private boolean bulletDamageBuffActive = false;
    private long bulletDamageBuffEnd = 0;
    private boolean bulletSpeedBuffActive = false;
    private long bulletSpeedBuffEnd = 0;
    private boolean bulletPenetrationBuffActive = false;
    private long bulletPenetrationBuffEnd = 0;
     
    //level
    private Level currentLevel;
    private int currentLevelNumber = 1;
    
    private ImageView bossView;
    private Boss boss;
    private boolean bossSpawned = false;
    
    //fireGap
    private long lastFiredTime = 0; // track the last fired time
    private final long fireInterval = 50; // minimum interval in milliseconds (now is 0.1s)
    
    //bgm
    private SoundPlayer bgm = createMedia("music/bgm/1.mp3", MediaPlayer.INDEFINITE, 0.5);
    
    //FPS
    public static final double FRAME_RATE = 120.0; // Desired FPS
    public static final double NANOSECONDS_PER_FRAME = 1_000_000_000.0 / FRAME_RATE;
    
    private long lastUpdate = 0;
    

    // Constructor
    
    public Game(){
        ship = new Ship();
        ship.setX(400);
        ship.setY(700);
        playerSpeed = ship.getMoveSpeed();
        instance =  this;
    }

    public static Game getInstance() {
        return instance;
    }
    
    // Main entry
    
    /**
     * The start method is the main entry point for every JavaFX application. 
     * It is called after the init() method has returned and after 
     * the system is ready for the application to begin running.
     *
     * @param  stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage)
    {
        this.stage = stage;
        root = new BorderPane();
        Scene scene = new Scene(root,WIDTH,HEIGHT);
        
        gamePane = new Pane();
        root.setCenter(gamePane);
        
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gamePane.getChildren().add(canvas);

        // background image
        Image background = new Image(getClass().getResourceAsStream("image/images.jpeg"));
        ImageView backgroundView = new ImageView(background);
        backgroundView.setFitWidth(WIDTH); 
        backgroundView.setFitHeight(HEIGHT); 
        gamePane.getChildren().add(backgroundView);
        
        //create menuBar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem quitItem = new MenuItem("Quit");
        fileMenu.getItems().addAll(quitItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().addAll(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        root.setTop(menuBar);
        
        quitItem.setOnAction(this::Quit);
        
        aboutItem.setOnAction(this::About);
        
        //create hp
        hpText = new Text("HP: " + ship.getHP());
        hpText.setX(10);
        hpText.setY(50);
        hpText.setFill(Color.WHITE);
        gamePane.getChildren().add(hpText);
        
        //create score
        scoreText = new Text("SCORE: " + score);
        scoreText.setX(700);
        scoreText.setY(50);
        scoreText.setFill(Color.WHITE);
        gamePane.getChildren().add(scoreText);
        
        //create level
        levelText = new Text();
        levelText.setX(350);
        levelText.setY(30);
        levelText.setFill(Color.WHITE);
        levelText.setText("LEVEL: " + currentLevelNumber + "   REMAINING: 0"); 
        gamePane.getChildren().add(levelText);
        
        this.currentLevelNumber = 1;
        this.currentLevel = new Level(this.currentLevelNumber);
        
        //create tutorial
        tutorialText = new Text("Move: ↑↓←→   Fire: SPACE   Pause: SHIFT");
        tutorialText.setX(50);
        tutorialText.setY(750);
        tutorialText.setFill(Color.WHITE);
        gamePane.getChildren().add(tutorialText);
        tutorialText2 = new Text("Add HP: Pause & Click the HP Button");
        tutorialText2.setX(520);
        tutorialText2.setY(750);
        tutorialText2.setFill(Color.WHITE);
        gamePane.getChildren().add(tutorialText2);
        
        
        bossHPText = new Text();
        bossHPText.setX(200);
        bossHPText.setY(30);
        bossHPText.setFill(Color.WHITE);
        bossHPText.setText("BOSS: NULL"); 
        gamePane.getChildren().add(bossHPText);
        
        //create image for player plane
        Image playerImage = new Image(getClass().getResourceAsStream("image/playerPlane.jpg"));
        playerView = new ImageView(playerImage);
        playerView.setFitWidth(100); 
        playerView.setFitHeight(100);
        gamePane.getChildren().add(playerView);
        
        //key listener
        scene.setOnKeyPressed(this::keyPressed);
        scene.setOnKeyReleased(this::keyReleased);
        
        stage.setTitle("Air Combat X");
        stage.setScene(scene);
        
        // Show the Stage (window)
        stage.show();
        
        animationTimer= new AnimationTimer()  {
            @Override
            public void handle(long now) {
                // adjust fps
                if (now - lastUpdate >= NANOSECONDS_PER_FRAME) {
                    update();
                    checkCollisions();
                    draw(gc);
                    lastUpdate = now;
                }
            }
        };
        
        animationTimer.start();
        
        bgm.play();
        
        startEnemySpawner(enemySpawnGap); 
        
        imageFollowObject(playerView, ship);
        
        gameMenu = new GameMenu(this);
        // gameMenu.showStartMenu();
        // Pause();
        startGame();
    }
    
    // Pause
    
    private void Pause() {
        left = false;
        right = false;
        up = false;
        down = false;
        
        if (isPaused) {
            animationTimer.start();
            enemySpawner.play();
             for (Item item : items) {
                  item.resume();
             }
            isPaused = false;
        } else {
            animationTimer.stop();
            enemySpawner.stop();
             for (Item item : items) {
                  item.pause();
             }
            isPaused = true;
        }
    }
      
    // Methods in the main loop (draw and update)
    
    /** 
     * Draw current frame. 
     */
    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        
        ship.draw(gc);
        
        for (Bullet bullet : bullets)
            bullet.draw(gc);
        
        gamePane.getChildren().removeAll(bulletViewList);
        gamePane.getChildren().addAll(bulletViewList);
            
        for (Enemy enemy : enemys)
            enemy.draw(gc);
            
        for (eBullet eb : enemyBullets)
             eb.draw(gc);
        
        if (boss != null && !boss.isBossDead()) {
            boss.draw(gc);
        }
    }

    /** 
     * update the game per frame. 
     */
    private void update() {
        if (ship.getHP() <= 0) {
            return;
        }

        Iterator<Enemy> enemyIt = enemys.iterator();
        Iterator<ImageView> enemyViewIt = enemyViewList.iterator();
        while (enemyIt.hasNext() && enemyViewIt.hasNext()) {
               Enemy e = enemyIt.next();
               ImageView ev = enemyViewIt.next();
               e.update();
               imageFollowObject(ev, e);
               if (e.getY() >= HEIGHT - 50) {
                   currentLevel.incrementKilled();
                   updateLevelText();
                   enemyIt.remove();
                   enemyViewIt.remove();
                   gamePane.getChildren().remove(ev);
                   decreaseScore();
               }
        }

        Iterator<Bullet> bulletIt = bullets.iterator();
        Iterator<ImageView> bulletViewIt = bulletViewList.iterator();
        while (bulletIt.hasNext() && bulletViewIt.hasNext()) {
               Bullet b = bulletIt.next();
               ImageView bv = bulletViewIt.next();
               b.update();
               imageFollowObject(bv, b);

               if (!b.onScreen()) {
                   bulletIt.remove();
                   bulletViewIt.remove();
                   gamePane.getChildren().remove(bv);
               }
        }

        Iterator<eBullet> enemyBulletIt = enemyBullets.iterator();
        Iterator<ImageView> enemyBulletViewIt = enemyBulletsViewList.iterator();
        while (enemyBulletIt.hasNext() && enemyBulletViewIt.hasNext()) {
               eBullet eb = enemyBulletIt.next();
               ImageView ebv = enemyBulletViewIt.next();
               eb.update();
               imageFollowObject(ebv, eb);
               if (!eb.onScreen()) {
                   enemyBulletIt.remove();
                   enemyBulletViewIt.remove();
                   gamePane.getChildren().remove(ebv);
               }
        }
        
        Iterator<ImageView> viewIt = enemyBulletsViewList.iterator();
        while (bulletIt.hasNext() && viewIt.hasNext()) {
               eBullet eb = enemyBulletIt.next();
               ImageView ebv = viewIt.next();
               eb.update();
               imageFollowObject(ebv, eb);
               if (!eb.onScreen()) {
                   bulletIt.remove();
                   viewIt.remove();
                   gamePane.getChildren().remove(ebv);
               }
         }
        checkCollisions(); 

        movePlayer();
        imageFollowObject(playerView, ship);
        
        if (currentLevel.getEnemiesKilled() % 40 == 0  && currentLevelNumber == 10 
            && !bossSpawned && currentLevel.getEnemiesKilled() != 0){
            spawnBoss();
            boss.upgradeBOSS(currentLevel.getEnemiesKilled());
            updateBossHP();
        }  
        
        if (boss != null) {
            if (!boss.isBossDead()) {
                boss.update();
                imageFollowObject(bossView, boss); 
            } else {
                removeBoss();
                if(currentLevelNumber == 10){
                   currentLevel.incrementKilled();
                }    
                if(currentLevelNumber != 10){
                   nextLevel();
                }
                return;
            }
        }
        
        if (boss != null && boss.isBossDead()) {
            while (enemyBulletIt.hasNext() && enemyBulletViewIt.hasNext()) {
                   eBullet eb = enemyBulletIt.next();
                   ImageView ebv = enemyBulletViewIt.next();
                   enemyBulletIt.remove();
                   enemyBulletViewIt.remove();
                   gamePane.getChildren().remove(ebv);
            }
        }
    }

    // Menu buttons
    
    /** 
     * Quit the game.
     */
    private void Quit(ActionEvent event) {
        System.exit(0);
    }
    
    /** 
     * About the game.
     */
    private void About(ActionEvent event) {
        boolean alreadyPaused = true; //if the game is already paused before running this method
        if (!isPaused) {
            alreadyPaused = false;
            Pause();
        }
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("About");
        alert.setHeaderText("Air Combat X");
        alert.setContentText("Authors: \nKexin Wang, Yingtao Zheng, Junhao Zhou, Langyuan Huang");
        
        alert.showAndWait();
        
        if (isPaused && !alreadyPaused) {
            Pause();
        }
    }
    
    // Scores
    
    /** 
     * Increase the score of the player. 
     */
    public void increaseScore(int x) {
        score = score + x;
        updateScoreLabel();
    }
    
    /** 
     * Decrease the score of the player. 
     */
    public void decreaseScore() {
        score -= 500;
        updateScoreLabel();
    }
    
    /** 
     * get current score of the player. 
     */
    public int getScore()
    {
        this.score = score;
        return score;
    }
    
    // Images
    
    /** 
     * Create image of an enemy ship. 
     */
    private ImageView createEnemyImage(){
        Image enemyImage = new Image(getClass().getResourceAsStream("image/enemyPlane.jpg"));
        ImageView enemyView = new ImageView(enemyImage);
        enemyView.setFitWidth(50); 
        enemyView.setFitHeight(50);
        return enemyView;
    }
    
    /** 
     * Create image of a bullet. 
     */
    private ImageView createBulletImage(){
        if(bulletPenetrationBuffActive){
           Image bulletImage = new Image(getClass().getResourceAsStream("image/green.png"));
           ImageView bulletView = new ImageView(bulletImage);
           bulletView.setFitWidth(10); 
           bulletView.setFitHeight(10);
           return bulletView;
        }else if(bulletDamageBuffActive){
            Image bulletImage = new Image(getClass().getResourceAsStream("image/red.png"));
            ImageView bulletView = new ImageView(bulletImage);
            bulletView.setFitWidth(10); 
            bulletView.setFitHeight(10);
            return bulletView;
        }else if(bulletSpeedBuffActive){
            Image bulletImage = new Image(getClass().getResourceAsStream("image/purple.png"));
            ImageView bulletView = new ImageView(bulletImage);
            bulletView.setFitWidth(10); 
            bulletView.setFitHeight(10);
            return bulletView;
        }else{
            Image bulletImage = new Image(getClass().getResourceAsStream("image/blue.png"));
            ImageView bulletView = new ImageView(bulletImage);
            bulletView.setFitWidth(10); 
            bulletView.setFitHeight(10);
            return bulletView;
        }
    }
    
    /**
     * Object A will keep updating its position to object B.
     * Used for images tracking with its responding object (e.g. shipView to ship)
     */
    private void imageFollowObject(ImageView image, Character object) {
        image.setX(object.getX() - image.getFitWidth()/2);
        image.setY(object.getY() - image.getFitHeight()/2);
    }
    
    /**
     * @method overloading
     * Object A will keep updating its position to object B.f
     * Used for images tracking with its responding object (e.g. bulletView to bullet)
     */
    private void imageFollowObject(ImageView image, Bullet bullet) {
        image.setX(bullet.getX() - image.getFitWidth()/2);
        image.setY(bullet.getY() - image.getFitHeight()/2);
    }
    
    private void imageFollowObject(ImageView image, eBullet ebullet) {
         image.setX(ebullet.getX() - image.getFitWidth() / 2);
         image.setY(ebullet.getY() - image.getFitHeight() / 2);
    }
    
    // Player movement
    
    /**
     * player movement
     */
    private void movePlayer() {
        if (left && ship.getX() > playerView.getFitWidth()/2) {
            ship.update(-1 * playerSpeed, 0);
        }
        if (right && playerView.getX() < WIDTH - playerView.getFitWidth()) {
            ship.update(playerSpeed, 0);
        }
        if (up && ship.getY() > playerView.getFitHeight()) {
            ship.update(0, -1 * playerSpeed);
        }
        if (down && playerView.getY() < HEIGHT - playerView.getFitHeight()) {
            ship.update(0, playerSpeed);
        }
    }
    
    private void keyPressed(KeyEvent k) {
        if (k.getCode() == KeyCode.LEFT) 
            left = true;
            
        if (k.getCode() == KeyCode.RIGHT)
            right = true;
        
        if (k.getCode() == KeyCode.UP)
            up = true;
            
        if (k.getCode() == KeyCode.DOWN)
            down = true;
            
        if (k.getCode() == KeyCode.SPACE) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFiredTime >= fireInterval) { // Enforce fire rate limit
                fireBullet(); // Call the bullet creation method
                lastFiredTime = currentTime; // Update last fired time
            }
        }
        
        if (k.getCode() == KeyCode.SHIFT) {
            gameMenu.showPauseMenu();
            Pause();
        }
        
    }

    public void resumeGame() {
        if (isPaused) {
            Pause();
        }
    }
    
    public void startGame() {
        if (isPaused) {
            Pause();
        }
    }
    
    private void fireBullet() {
        long now = System.currentTimeMillis();
        
        if(!bulletDamageBuffActive && !bulletSpeedBuffActive && !bulletPenetrationBuffActive){
            Bullet newBullet = new Bullet(ship.getX(), ship.getY(), 0, false, Color.GRAY);
            newBullet.upgradeBullet(score);
            bullets.add(newBullet);
            bulletViewList.add(createBulletImage());
        }
        if (bulletDamageBuffActive && now < bulletDamageBuffEnd) {
            Bullet newBullet = new Bullet(ship.getX(), ship.getY(), 0, false, Color.GRAY);
            Bullet newBullet1 = new Bullet(ship.getX(), ship.getY(), 30, false, Color.GRAY);
            Bullet newBullet2 = new Bullet(ship.getX(), ship.getY(), -30, false, Color.GRAY);
            newBullet.activateDamageBoost(bulletDamageBuffEnd - now);
            bullets.add(newBullet);
            bulletViewList.add(createBulletImage());
            bullets.add(newBullet1);
            bulletViewList.add(createBulletImage());
            bullets.add(newBullet2);
            bulletViewList.add(createBulletImage());
            newBullet.upgradeBullet(score);
            newBullet2.upgradeBullet(score);
            newBullet1.upgradeBullet(score);
        } else {
            bulletDamageBuffActive = false;
        }
    
        if (bulletSpeedBuffActive && now < bulletSpeedBuffEnd) {
            Bullet newBullet = new Bullet(ship.getX()-10.0,ship.getY(), 0, false, Color.GRAY);
            Bullet newBullet1 = new Bullet(ship.getX()+10.0, ship.getY(), 0, false, Color.GRAY);
            newBullet.activateSpeedBoost(bulletSpeedBuffEnd - now);
            bullets.add(newBullet);
            bulletViewList.add(createBulletImage());
            bullets.add(newBullet1);
            bulletViewList.add(createBulletImage());
            newBullet1.upgradeBullet(score);
            newBullet.upgradeBullet(score);
        } else {
            bulletSpeedBuffActive = false;
        }
    
        if (bulletPenetrationBuffActive && now < bulletPenetrationBuffEnd) {
            Bullet newBullet = new Bullet(ship.getX(), ship.getY(), 0, false, Color.GRAY);
            newBullet.activatePenetration(bulletPenetrationBuffEnd - now);
            bullets.add(newBullet);
            bulletViewList.add(createBulletImage());
            newBullet.upgradeBullet(score);
        } else {
            bulletPenetrationBuffActive = false;
        }
    }
    
    //boss shoot
    public void bossShoot(double x, double y) {
        int[] angles = {150, 165, 180, 195,210};

        for (int angle : angles) {
             eBullet bullet = new eBullet(x, y, angle, false, Color.PURPLE);
             enemyBullets.add(bullet);
             ImageView bulletView = new ImageView(new Image(getClass().getResourceAsStream("image/bossBullet.png")));
             bulletView.setFitWidth(10);
             bulletView.setFitHeight(10);
             enemyBulletsViewList.add(bulletView);
             gamePane.getChildren().add(bulletView);
        }
    }

    public List<eBullet> getEnemyBullets() {
        return enemyBullets;
    }
        
    private void keyReleased(KeyEvent k) {
        if (k.getCode() == KeyCode.LEFT) 
            left = false;
            
        if (k.getCode() == KeyCode.RIGHT)
            right = false;
        
        if (k.getCode() == KeyCode.UP)
            up = false;
            
        if (k.getCode() == KeyCode.DOWN)
            down = false;
    }
    
    // Spawner
    
    /**
     * Continuously spawn enemy witha 
     */
    private void startEnemySpawner(double timeGap) {
        enemySpawner = new Timeline(new KeyFrame(Duration.seconds(timeGap), e -> spawnEnemy()));
        enemySpawner.setCycleCount(Timeline.INDEFINITE);  
        enemySpawner.play();
    }
    
    private void spawnEnemy() {
        if (enemys.size() >= currentLevel.getMaxEnemyOnScreen()) {
            return;
        }
        
        if (currentLevel.getEnemiesSpawned() >= currentLevel.getTotalEnemy()) {
            return;
        }
        
        Enemy enemy = new Enemy(random.nextInt(50, WIDTH-50), 50, currentLevel, false);
        enemys.add(enemy);
        ImageView enemyView = createEnemyImage();
        enemyViewList.add(enemyView);
        gamePane.getChildren().add(enemyView);

        currentLevel.incrementSpawned();
        updateLevelText();
    }

    // Collision
        
    /** check collision */
    private void checkCollisions() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        Iterator<ImageView> bulletViewIterator = bulletViewList.iterator();
        
        Iterator<Enemy> enemyIterator = enemys.iterator();
        Iterator<ImageView> enemyViewIterator = enemyViewList.iterator();
        
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            ImageView bulletView = bulletViewIterator.next();
            
            enemyIterator = enemys.iterator();
            enemyViewIterator = enemyViewList.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                ImageView enemyView = enemyViewIterator.next();
                
                double[] pos = enemy.getPos();
                
                if (bullet.inContact(pos[0], pos[1], enemy)) {
                    currentLevel.incrementKilled();
                    updateLevelText();
                    if(!bulletPenetrationBuffActive){
                       bulletIterator.remove();
                       gamePane.getChildren().remove(bulletView);
                       bulletViewIterator.remove();
                    }
                    
                    enemyIterator.remove();
                    gamePane.getChildren().remove(enemyView);
                    enemyViewIterator.remove();

                    increaseScore(300);
                    double r1 = Math.random();
                    if (r1 < dropItem) {
                        double max = dRed + dGreen + dBlue + dYellow;
                        double min = 0;
                        //random number between min to max
                        double r2 = Math.random() * (max - min + 1) + min; 
                        Color finalColor;
                        if (r2 < dRed) {
                            finalColor = Color.RED;
                        } else if (r2 < dRed + dGreen) {
                            finalColor = Color.GREEN;
                        } else if(r2 < dRed + dGreen + dBlue) {
                            finalColor = Color.BLUE;
                        }else{
                            finalColor = Color.YELLOW;
                        }
                        // create items
                        Item newItem = new Item(enemy.getX(), enemy.getY(), finalColor);
                        items.add(newItem);
                        gamePane.getChildren().add(newItem.getShape());
                    }
                    SoundPlayer soundEffect = createMedia("music/se/boom.wav", 1, 0.8);
                    break;
                }
                
                else if (!bullet.onScreen()) {
                    bulletIterator.remove();
                    gamePane.getChildren().remove(bulletView);
                    bulletViewIterator.remove();
                    break;
                }
            }


            if (ship.getHP() <= 0) {
                 animationTimer.stop();
                 enemySpawner.stop();
                 for (Item item : items) {
                      item.pause();
                 }
                 isPaused = true;
                 gameMenu.showGameOverMenu();
                 return;
            }

        }  
        
        enemyIterator = enemys.iterator();
        enemyViewIterator = enemyViewList.iterator();
        while (enemyIterator.hasNext()){
            Enemy enemy = enemyIterator.next();
            ImageView enemyView = enemyViewIterator.next();
            
            double[] pos = enemy.getPos();
            if (ship.inContact(pos[0], pos[1]) && ship.getHP() > 0 && !keepHP) {
                currentLevel.incrementKilled();
                updateLevelText();
                
                SoundPlayer soundEffect = createMedia("music/se/explosion2.mp3", 1, 0.7);
                
                ship.addHP(-1);
                hpText.setText("HP: " + ship.getHP());
                keepHP = true;
                
                enemyIterator.remove(); 
                gamePane.getChildren().remove(enemyView);
                enemyViewIterator.remove(); 
                
                
                new Timer().schedule(new TimerTask()
                {
                    @Override
                    public void run(){
                        keepHP = false;
                    }
                }, 1000);
                

                if (ship.getHP() <= 0) {
                    animationTimer.stop();
                    enemySpawner.stop();
                    for (Item item : items) {
                         item.pause();
                    }
                    isPaused = true;
                    gameMenu.showGameOverMenu();
                    return;
                }

            }
        }
        
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
               Item item = itemIterator.next();
               if (!item.isActive()) {
                   itemIterator.remove();
                   gamePane.getChildren().remove(item.getShape());
                   continue;
               }
               if (item.inContact(ship.getX(), ship.getY())) {
                   SoundPlayer soundEffect = createMedia("music/se/pick-up-item.mp3", 1, 0.8);
                   
                   item.collect();
                   gamePane.getChildren().remove(item.getShape());
                   itemIterator.remove();
                   Color c = item.getColor();
                   if (c == Color.RED) {
                       bulletDamageBuffActive = true;
                       bulletDamageBuffEnd = System.currentTimeMillis() + 5000;
                   } else if (c == Color.GREEN) {
                       bulletPenetrationBuffActive = true;
                       bulletPenetrationBuffEnd = System.currentTimeMillis() + 5000;
                   } else if (c == Color.YELLOW) {
                       bulletSpeedBuffActive = true;
                       bulletSpeedBuffEnd = System.currentTimeMillis() + 5000;
                   } else if(c == Color.BLUE){
                       ship.addHP(1);
                       hpText.setText("HP: " + ship.getHP());
                   }    
               }
               else if (item.getY() >= HEIGHT-50) {
                   itemIterator.remove();
                   gamePane.getChildren().remove(item.getShape());
                   break;
               }
        }
        
        Iterator<eBullet> ebIt = enemyBullets.iterator();
        Iterator<ImageView> ebvIt = enemyBulletsViewList.iterator();

        while (ebIt.hasNext() && ebvIt.hasNext()) {
            eBullet ebul = ebIt.next();
            ImageView ebv = ebvIt.next();
            
            if (ship.inContact(ebul.getX(), ebul.getY()) && ship.getHP() > 0 && !keepHP) {
               SoundPlayer soundEffect = createMedia("music/se/explosion2.mp3", 1, 0.7);
               ship.addHP(-1);
               hpText.setText("HP: " + ship.getHP());
               keepHP = true;
               new Timer().schedule(new TimerTask() {
                   public void run() {
                       keepHP = false;
                   }
               }, 1000);
            
               ebIt.remove();
               ebvIt.remove();
               gamePane.getChildren().remove(ebv);
            
               if (ship.getHP() <= 0) {
                   animationTimer.stop();
                   enemySpawner.stop();
                   for (Item item : items) item.pause();
                      isPaused = true;
                      gameMenu.showGameOverMenu();
                      return;
               }
            }
            else if (!ebul.onScreen()) {
                ebIt.remove();
                ebvIt.remove();
                gamePane.getChildren().remove(ebv);
                break;
            }
         }
        
        if (boss != null && !boss.isBossDead()) {
            Iterator<Bullet> bulletIt = bullets.iterator();
            Iterator<ImageView> bulletViewIt = bulletViewList.iterator();
            while (bulletIt.hasNext() && bulletViewIt.hasNext()) {
                   Bullet b = bulletIt.next();
                   ImageView bv = bulletViewIt.next();
                   if (b.inContact(boss.getX(), boss.getY(), boss)) {
                       int D = b.getDamage();
                       if(bulletPenetrationBuffActive){
                           D = D * 2 ;
                       }
                       boss.takeDamage(D);
                       bulletIt.remove();
                       bulletViewIt.remove();
                       gamePane.getChildren().remove(bv);
                       updateBossHP();

                       SoundPlayer soundEffect = createMedia("music/se/boom.wav", 1, 0.8);
                       break; 
                   } else if (!b.onScreen()) {
                       bulletIt.remove();
                       bulletViewIt.remove();
                       gamePane.getChildren().remove(bv);
                       break;
                   }
            }
            if (ship.inContact(boss.getX(), boss.getY()) && ship.getHP() > 0 && !keepHP) {
                SoundPlayer soundEffect = createMedia("music/se/explosion2.mp3", 1, 0.7);

                ship.addHP(-1);
                hpText.setText("HP: " + ship.getHP());
                keepHP = true;

                new Timer().schedule(new TimerTask() {
                 public void run() {
                    keepHP = false;
                 }
                }, 1000);


                if (ship.getHP() <= 0) {
                    animationTimer.stop();
                    enemySpawner.stop();
                    for (Item item : items) item.pause();
                    isPaused = true;
                    gameMenu.showGameOverMenu();
                    return;
                }

            }
        }
        
        if(currentLevelNumber == 2
        && currentLevel.getEnemiesKilled() >= currentLevel.getTotalEnemy()
        && !bossSpawned) {
           spawnBoss();
        }
                    
        if(currentLevelNumber != 2 
        && currentLevel.getEnemiesKilled() >= currentLevel.getTotalEnemy()){
           nextLevel();
        }

        if (ship.getHP() <= 0) {

            animationTimer.stop();
            enemySpawner.stop();
            for (Item item : items) {
                 item.pause();
            }
            isPaused = true;
            gameMenu.showGameOverMenu();
            return;
        }
    }  

    // Media

    private SoundPlayer createMedia(String relativePath, int cycleCount, double volume){
        SoundPlayer media = new SoundPlayer(relativePath, cycleCount, volume);
        media.play(); //play media straight away
        return media;
    }
    
    // GUI related
    
    private void updateLifeLabel() {
        hpText.setText("HP: " + ship.getHP());
    }
    
    private void updateScoreLabel() {
        scoreText.setText("SCORE: " + score);
    }
    
    /** reset the game */
    public void resetGame() {
        if (enemySpawner != null) {
            enemySpawner.stop();
        }
        if (boss != null) {
            boss.isDead = true;
            boss = null;
        }   
        if (bossView != null) {
            gamePane.getChildren().remove(bossView);
             bossView = null;
        }
        bossSpawned = false;
        enemyBullets.clear();
        for (ImageView bulletView : enemyBulletsViewList) {
             gamePane.getChildren().remove(bulletView);
        }
        enemyBullets.clear();
        enemyBulletsViewList.clear();
        enemyBulletsViewList.clear(); 
    
        ship.setHP(5);
        score = 0; 
        hpText.setText("HP: " + ship.getHP());
        scoreText.setText("Score: " + score);
        levelText.setText("LEVEL: " + currentLevelNumber + "   REMAINING: 0");
        tutorialText.setText("Move: ↑↓←→   Fire: SPACE   Pause: SHIFT");
        tutorialText2.setText("Add HP: Pause & Click the HP Button");
        
        currentLevelNumber = 1; 
        currentLevel = new Level(currentLevelNumber);
        enemySpawnGap = currentLevel.getEnemySpawnGap();
        maxEnemyOnScreen = currentLevel.getMaxEnemyOnScreen();
        updateLifeLabel();
        updateScoreLabel();
        updateLevelText();
        updateBossHP();
        
        ship.setX(400);
        ship.setY(700);
        
        left = false;
        right = false;
        up = false;
        down = false;
        
        isPaused = false;
        resetBuffs();
        
        bullets.clear();
        bulletViewList.clear();
        enemys.clear();
        enemyViewList.clear();
        items.clear();
        itemViewList.clear();
        gamePane.getChildren().clear();
        
        Image background = new Image(getClass().getResourceAsStream("image/images.jpeg"));
        ImageView backgroundView = new ImageView(background);
        backgroundView.setFitWidth(WIDTH); 
        backgroundView.setFitHeight(HEIGHT); 
        gamePane.getChildren().add(backgroundView);
        gamePane.getChildren().addAll(playerView, hpText, scoreText, levelText, tutorialText, bossHPText,tutorialText2);
        
        if (bgm != null) {
            bgm.dispose();
        }
        
        bgm = createMedia(currentLevel.getBgmPath(), MediaPlayer.INDEFINITE, currentLevel.getBgmVolume());
    
        animationTimer.start();
        startEnemySpawner(enemySpawnGap); 
    }
    
    private void nextLevel() {
        resetBuffs();
        enemySpawner.stop();
        enemys.clear();
        enemyViewList.clear();
        if (boss != null) {
            boss.isDead = true ; 
            gamePane.getChildren().remove(bossView);
            boss = null;
            bossView = null;
            bossSpawned = false;
        }
        
        enemyBullets.clear();
        for (ImageView bulletView : enemyBulletsViewList) {
             gamePane.getChildren().remove(bulletView);
        }
        
        if(currentLevelNumber >= 3){
            return;
        }
        currentLevelNumber++;
        currentLevel = new Level(currentLevelNumber);
        bgm.dispose();
        String newBgmPath = currentLevel.getBgmPath();
        bgm = createMedia(newBgmPath, MediaPlayer.INDEFINITE, 0.3);
        startEnemySpawner(enemySpawnGap);
    }
    
    private void updateLevelText() {
        int remaining = currentLevel.getTotalEnemy() - currentLevel.getEnemiesKilled();
        if (currentLevel.isInfinite()) {
            levelText.setText("LEVEL: INFINITE");
        } else {
            levelText.setText("LEVEL: " + currentLevelNumber + "   REMAINING: " + remaining);
        }
        
        if (currentLevelNumber == 3 && remaining == 0 &&  ship.getHP() > 0) {
            Platform.runLater(() -> {
                Pause();
                for (Item item : items) {
                     item.pause();
                }
                gameMenu.showGameVictoryMenu();
            });
        }
    }
    
    private void updateBossHP(){
         if (boss != null && !boss.isBossDead()) {
        bossHPText.setText("BOSS HP: " + boss.getBossHP());
        } else {
        bossHPText.setText("BOSS: NULL");
    } 
    }
    
    public void startInf(){
        isPaused = false;
        resetBuffs();
        ship.setHP(10);
        hpText.setText("HP: " + ship.getHP());
        this.currentLevelNumber = 10;
        this.currentLevel = new Level(10);
        this.enemySpawnGap = currentLevel.getEnemySpawnGap();
        this.maxEnemyOnScreen = currentLevel.getMaxEnemyOnScreen();
        for (ImageView enemyView : enemyViewList) {
            gamePane.getChildren().remove(enemyView);
        }
        enemys.clear();
        enemyViewList.clear();
        for (ImageView bulletView : bulletViewList) {
            gamePane.getChildren().remove(bulletView);
        }
        bullets.clear();
        bulletViewList.clear();
        for (Item item : items) {
            gamePane.getChildren().remove(item.getShape());
        }
        items.clear();
        updateLevelText();
        bgm.dispose();
        String newBgmPath = currentLevel.getBgmPath();
        bgm = createMedia(newBgmPath, MediaPlayer.INDEFINITE, 0.3);
        startEnemySpawner(enemySpawnGap);
        animationTimer.start();
        
    }
    
    private void resetBuffs() {
        bulletDamageBuffActive = false;
        bulletSpeedBuffActive = false;
        bulletPenetrationBuffActive = false;
        bulletDamageBuffEnd = 0;
        bulletSpeedBuffEnd = 0;
        bulletPenetrationBuffEnd = 0;
    }
    
    private void spawnBoss() {
        bossSpawned = true;
        boss = new Boss(WIDTH / 2, 0, currentLevel, true);
        updateBossHP();
        Image bossImage = new Image(getClass().getResourceAsStream("image/boss.png"));
        bossView = new ImageView(bossImage);
        bossView.setFitWidth(300);
        bossView.setFitHeight(100);
        gamePane.getChildren().add(bossView);
    }
    
    public void removeBoss() {
        if (boss != null) {
            gamePane.getChildren().remove(bossView);
            boss = null;
            bossView = null;
            bossSpawned = false;
            if(currentLevelNumber == 10){
                increaseScore(currentLevel.getEnemiesKilled()*10);
            }else{
                increaseScore(1000);
            }
            Iterator<eBullet> enemyBulletIt = enemyBullets.iterator();
            Iterator<ImageView> enemyBulletViewIt = enemyBulletsViewList.iterator();
            while (enemyBulletIt.hasNext() && enemyBulletViewIt.hasNext()) {
                   eBullet eb = enemyBulletIt.next();
                   ImageView ebv = enemyBulletViewIt.next();
                   enemyBulletIt.remove();
                   enemyBulletViewIt.remove();
                   gamePane.getChildren().remove(ebv);
            }
        }
    }
    
    public Ship getShip() {
        return ship;
    }

    public Stage getStage() {
        return stage;
    }
}
