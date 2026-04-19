/**
 * The parent class of "living" objects (ship/player, enemies).
 * 
 * @author Yingtao Zheng
 * @version 2.0 (25-03-2025)
 */
public class Character
{
    // default values
    int hp;
    int fireSpeed; // per second
    double moveSpeed; 
    double size;
    boolean death = false;
    double x; // position x
    double y; // position y
    

    /**
     * Constructor for objects of class Character
     */
    public Character()
    {
    }

    protected int getHP(){
        return hp;
    }
    
    protected int getFireSpeed(){
        return fireSpeed;
    }
    
    protected double getMoveSpeed(){
        return moveSpeed;
    }
    
    protected double getSize(){
        return size;
    }

    protected boolean isDead(){
        return death;
    }
    
    
    protected void setHP(int newHP){
        hp = newHP;
    }
    
    protected void changeHP(int deltaHP){
        hp += deltaHP;
    }
    
    protected void setFireSpeed(int theFireSpeed){
        fireSpeed = theFireSpeed;
    }
    
    protected void setMoveSpeed(int theMoveSpeed){
        moveSpeed = theMoveSpeed;
    }
    
    protected void setSize(int theSize){
        moveSpeed = theSize;
    }
    
    protected void setDead(){
        death = true;
    }
    
    
    // Coordinates

    /**
     * Gets the x,y coordinate of the Enemy
     */    
    public double[] getPos() {
        return new double[]{x,y};
    }    
    
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
    
    public void setX(double newX) {
        this.x = newX;
    }
    
    public void setY(double newY) {
        this.y = newY;
    }

}
