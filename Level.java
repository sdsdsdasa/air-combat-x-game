/**
 * This class represents different levels of the game. 
 * 
 * @author Langyuan Huang
 * @version 2.0 (25-03-2025)
 */
public class Level {
    private int levelNumber;
    private int maxEnemyOnScreen;
    private double enemySpawnGap;
    private int totalEnemies;
    private int enemiesSpawned;
    private int enemiesKilled;
    private String bgmPath;
    private double bgmVolume;
    private double enemySpeed;
    private double bossSpeed;


    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        switch(levelNumber) {
            case 1:
                this.maxEnemyOnScreen = 3;
                this.enemySpawnGap = 1.5;
                this.totalEnemies = 5;
                this.enemySpeed = 1.5;
                bgmPath = "music/bgm/1.mp3";
                bgmVolume = 0.5;
                break;
            case 2:
                this.maxEnemyOnScreen = 5;
                this.enemySpawnGap = 1.2;
                this.totalEnemies = 7;
                this.enemySpeed = 2.5;
                this.bossSpeed = 1.0;
                bgmPath = "music/bgm/2.mp3";
                bgmVolume = 0.2;
                break;
            case 3:
                this.maxEnemyOnScreen = 8;
                this.enemySpawnGap = 0.7;
                this.totalEnemies = 10;
                this.enemySpeed = 3.0;
                bgmPath = "music/bgm/3.mp3";
                bgmVolume = 0.2;
                break;    
            case 10:
                this.maxEnemyOnScreen = 20;
                this.enemySpawnGap = 0.3;
                this.totalEnemies = Integer.MAX_VALUE;
                this.enemySpeed = 2.0;
                this.bossSpeed = 0.5;
                bgmPath = "music/bgm/inf.mp3";
                bgmVolume = 0.3;
                break;
            default:
                this.enemySpeed = 1.7;
        }
        this.enemiesSpawned = 0;
        this.enemiesKilled = 0;
    }

    public double getEnemySpeed() {
        return enemySpeed;
    }
    public double getBossSpeed() {
        return bossSpeed;
    }
    
    public boolean isLevelCompleted() {
        if (levelNumber == 10)return false;
        return enemiesKilled >= totalEnemies;
    }

    public void incrementSpawned() {
        enemiesSpawned++;
    }

    public void incrementKilled() {
        enemiesKilled++;
    }

    public int getMaxEnemyOnScreen() {
        return maxEnemyOnScreen;
    }

    public double getEnemySpawnGap() {
        return enemySpawnGap;
    }
    
    public int getTotalEnemy(){
        return totalEnemies;
    }

    public int getEnemiesSpawned() {
        return enemiesSpawned;
    }
    
    public int getRemainingEnemies() {
        return totalEnemies - enemiesKilled;
    }
    
    public int getEnemiesKilled(){
        return enemiesKilled;
    }

    public String getBgmPath() {
        return bgmPath;
    }
    
    public double getBgmVolume() {
        return bgmVolume;
    }
    
    public boolean isInfinite() {
         return levelNumber == 10;
    }
}