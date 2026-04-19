import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * This class represents items.
 * 
 * @author Langyuan Huang
 * @version 2.0 (25-03-2025)
 */
public class Item {

    private Shape shape;
    private Color color; 
    private double speedY = 1.8;
    private double shakeLength = 50;
    private double shakeSpeed = 0.05;
    private double time = 0;        

    private boolean collected = false; 
    private boolean active = true;

    //FPS
    private long lastUpdate = 0;
    
    private AnimationTimer timer;
    
    public Item(double startX, double startY, Color color) {
        this.color = color;

        shape = new Circle(10);
        shape.setFill(color);
        shape.setTranslateX(startX);
        shape.setTranslateY(startY);

        startFalling();
    }

    private void startFalling() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= Game.NANOSECONDS_PER_FRAME) {
                    if (collected || !active) {
                        return;
                    }
                    time += shakeSpeed;
                    double shakeX = Math.sin(time) * shakeLength;
                    shape.setTranslateX(shape.getTranslateX() + shakeX * 0.01);
                    shape.setTranslateY(shape.getTranslateY() + speedY);
    
                    if (shape.getTranslateY() >= Game.HEIGHT) {
                        active = false;
                        stopFalling();
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    public void stopFalling() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void collect() {
        if (!collected) {
            collected = true;
            active = false;
            stopFalling();
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isActive() {
        return active;
    }

    public double getX() {
        return shape.getTranslateX();
    }

    public double getY() {
        return shape.getTranslateY();
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }
    
    public void pause() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void resume() {
        if (timer != null && !collected && active) {
            timer.start();
        }   
    }   

    public boolean inContact(double px, double py) {
        double dx = px - getX();
        double dy = py - getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist <= 50; 
    }
    
    public boolean onScreen() {
        double x = shape.getTranslateX();
        double y = shape.getTranslateY();
        return x > 0 && x < Game.WIDTH && y > 0 && y < Game.HEIGHT;
    }
}
