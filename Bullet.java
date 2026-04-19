import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;

/**
 * This class represents a bullet fired from the ship.
 * Supports upgrades based on score and temporary power-ups.
 * 
 * @author Kexin Wang
 * @version 2.0 (25-03-2025)
 */
public class Bullet {

    private double x, y, dx, dy, size;
    private int speed, damage;
    private boolean penetration;
    private Color color;
    
    // Buff attributes
    private boolean speedBoost = false;
    private boolean damageBoost = false;
    private boolean penetrationBuff = false;
    
    private long speedBoostEndTime = 0;
    private long damageBoostEndTime = 0;
    private long penetrationEndTime = 0;

    public Bullet(double x, double y, double angle, boolean penetration, Color color) {
        this.x = x;
        this.y = y;
        this.size = 7;
        this.speed = 5;
        this.damage = 5;
        this.penetration = penetration;
        this.color = color;
        
        this.dx = Math.cos(Math.toRadians(angle - 90));
        this.dy = Math.sin(Math.toRadians(angle - 90));
    }

    /**
     * Update bullet position and check for expired buffs.
     */
    public void update() { 
        x += dx * speed;
        y += dy * speed;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
    
    /**
     * Permanent upgrades based on score.
     */
    public void upgradeBullet(int score) {
        int level = score / 500; // Every 500 points, upgrade bullet
        int damage = this.damage;
        int speed = this.speed;
        if(level >= 20){
            level = 20;
        }
        this.speed = Math.max(5, this.speed + (level / 2));
        this.damage = Math.max(5, this.damage + level);
    }

    /**
     * Temporary power-ups (speed, damage, penetration).
     */
    public void activateSpeedBoost(long duration) {
        if (!speedBoost) {
            speed = (int) (speed * 1.1);
            speedBoostEndTime = System.currentTimeMillis() + duration;
            speedBoost = true;
        }
    }

    public void activateDamageBoost(long duration) {
        if (!damageBoost) {
            damage = (int) (damage * 1.1);
            damageBoostEndTime = System.currentTimeMillis() + duration;
            damageBoost = true;
        }
    }

    public void activatePenetration(long duration) {
        if (!penetrationBuff) {
            penetration = true;
            penetrationEndTime = System.currentTimeMillis() + duration;
            penetrationBuff = true;
        }
    }

    /**
     * Draw the bullet on screen with glow effects for buffs.
     */
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        
        if (speedBoost) {
            gc.setEffect(new GaussianBlur(10));
            gc.setFill(Color.YELLOW);
        } else if (damageBoost) {
            gc.setEffect(new GaussianBlur(12));
            gc.setFill(Color.RED);
        } else if (penetrationBuff) {
            gc.setEffect(new GaussianBlur(8));
            gc.setFill(Color.GREEN);
        }
        
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.setEffect(null);
    }

    public boolean onScreen() {
        return x > 0 && x < Game.WIDTH && y > 0 && y < Game.HEIGHT;
    }
    
    public boolean getPenetration()
    {
        return this.penetration;
    }
    
    /**
     * Returns true if the given coordinates are within a certain
     * distance from the bullet.
     */
    public boolean inContact(double x, double y, Enemy enemy) {
        double x2 = Math.pow(x-this.x, 2);
        double y2 = Math.pow(y-this.y, 2);        
        double d = Math.sqrt(x2 + y2);
        return d <= enemy.getSize() / 1.5; 

    } 
    
    public Color getColor() {
        return this.color;
    }
    
    public int getDamage(){
        return this.damage;
    }

}
