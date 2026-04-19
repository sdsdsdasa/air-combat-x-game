import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Boss extends Enemy {
    private int shootInterval = 1500; 
    private long lastShootTime = 0;
    private int bossHP = 300;
    public boolean isDead = false;
    private double moveSpeedX = 0.2;
    private double moveSpeedY = 1.2;
    private Image bossImage;

    public Boss(double x, double y, Level level, boolean isBoss) {
        super(x, y, level, true);
        this.moveSpeed = level.getBossSpeed();
        this.size = 80;
        bossImage = new Image(getClass().getResourceAsStream("image/boss.png"));
    }

    public void update() {
        if(isDead) {
            return;
        }
        if (getY() < 200) {
            setY(getY() + moveSpeedY);
        } else {
            setX(getX() + moveSpeedX);
            double leftBound = 300;
            double rightBound = 500;
            if (getX() < leftBound) {
                setX(leftBound); 
                moveSpeedX = -moveSpeedX;
            } else if (getX() > rightBound) {
                setX(rightBound);
                moveSpeedX = -moveSpeedX;
            }
        }

        long now = System.currentTimeMillis();
        if (now - lastShootTime > shootInterval && !isDead) {
            lastShootTime = now;
            Game.getInstance().bossShoot(getX(), getY());
        }
    }
    
    public void upgradeBOSS(int killed){
        int level = killed/40;
        int bossHP = this.bossHP;
        int shootInterval = this.shootInterval;
        double moveSpeedX = this.moveSpeedX;
        this.bossHP = bossHP + (level*100);
        this.shootInterval = shootInterval - (level*100);
        if(shootInterval < 500){
            shootInterval = 500;
        }
        this.moveSpeedX = moveSpeedX + (level * 0.05);
    }
    

    public void takeDamage(int dmg) {
        bossHP -= dmg;
        if (bossHP <= 0) {
            isDead = true;
        }
    }
    
    public int getBossHP(){
        return this.bossHP;
    }

    public boolean isBossDead() {
        return isDead;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.PURPLE);
        gc.fillOval(x - size/2, y - size/2, size, size);
    }
    
}
