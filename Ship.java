import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class represents a ship/player. A triagle (3-sided polygon).
 * 
 * @author Yingtao Zheng
 * @version 2.0 (25-03-2025)
 */
public class Ship extends Character {
    
    private double angle = 0;
    private Color color; 
    
    //buff time
    private long damageBuff = 0;
    private long speedBuff  = 0;
    
    public Ship() {
        super();
        
        hp = 5;
        fireSpeed = 1;
        moveSpeed = 3.5;
        x = Game.WIDTH / 2;
        y = Game.HEIGHT / 2;
        size = 40;
        
        color = Color.WHITE;
        update(0,0);
    }
    
    public void update(double deltaX, double deltaY) {
        this.setX(getX() + deltaX);
        this.setY(getY() + deltaY);
    }

    /**
     * Gets the angle of the ship
     */    
    public double getAngle() {
        return angle;
    }

    /**
     * Gets the color of the ship
     */     
    public Color getColor() {
        return color;
    }

    // for visualization of the hitbox
    public void draw(GraphicsContext gc) {
        double size = 40;
        double height = Math.sqrt(3) / 2 * size; // Height of an equilateral triangle
        double x = this.x;
        double y = this.y;
        
        // Define three vertices relative to the center
        double[] xPoints = {x, x - size / 2, x + size / 2};
        double[] yPoints = {y - height / 2, y + height / 2, y + height / 2};

        gc.setFill(Color.BLUE); // Set fill color
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Returns true if the given coordinates are within a certain
     * distance from the ship.
     */
    public boolean inContact(double x, double y) {
        
        double x2 = Math.pow(x-this.x, 2);
        double y2 = Math.pow(y-this.y, 2);
        double d = Math.sqrt(x2 + y2);
        return d <= size / 1.0; 
    }
    
    public void addHP(int x){
        hp = hp + x;
    }
    
}