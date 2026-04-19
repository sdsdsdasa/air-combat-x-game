import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * This class represents enemies.
 * 
 * @author Yingtao Zheng
 * @version 2.0 (25-03-2025)
 */
public class Enemy extends Character {

    public static final Color COLOR = Color.GREY;
    //public static final int SIZE = 40;

    private Random random = new Random();
    
    public Enemy(double x, double y, Level level, boolean isBoss) {
        super();
        this.x = x;
        this.y = y;
        if (isBoss) {
            this.moveSpeed = level.getBossSpeed();
        } else {
            this.moveSpeed = level.getEnemySpeed();
        }
        size = 40;
    }

    

    /**
     * Modifies the position of the Enemy.  Enemy travel at an
     * angle and must remain in the screen (hence the modulo math)
     */    
    public void update() {
        y = (y + moveSpeed + Game.HEIGHT) % Game.HEIGHT;
    }

    public double getSize() {
        return size;
    }
    
    /**
     * Draws the Enemy on the canvas via the GraphicsContext object
     */     
    public void draw(GraphicsContext gc) {
        gc.setFill(COLOR);
        gc.fillOval(x - size / 2, y - size / 2, size, size);
    }

}