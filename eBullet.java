import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.canvas.GraphicsContext;

/**
 * This class represents a bullet fired from the ship.
 * Supports upgrades based on score and temporary power-ups.
 * 
 * @author Kexin Wang
 * @version 2.0 (25-03-2025)
 */
public class eBullet {

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

    public eBullet(double x, double y, double angle, boolean penetration, Color color) {
        this.x = x;
        this.y = y;
        this.size = 7;
        this.speed = 7;
        this.damage = 10;
        this.penetration = penetration;
        this.color = color;
        
        this.dx = Math.cos(Math.toRadians(angle - 90));
        this.dy = Math.sin(Math.toRadians(angle - 90));
    }
    
    public boolean onScreen() {
        return x > 0 && x < Game.WIDTH && y > 0 && y < Game.HEIGHT;
    }

    /**
     * Update bullet position and check for expired buffs.
     */
    public void update() { // long currentTime
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

    
    public boolean getPenetration()
    {
        return this.penetration;
    }
    
    /**
     * Returns true if the given coordinates are within a certain
     * distance from the bullet.
     */
    public boolean inContact(double x, double y) {
        double x2 = Math.pow(x-this.x, 2);
        double y2 = Math.pow(y-this.y, 2);        
        double d = Math.sqrt(x2 + y2);
        return d <= size / 1.5; 
    } 
    
    public Color getColor() {
        return this.color;
    }
}