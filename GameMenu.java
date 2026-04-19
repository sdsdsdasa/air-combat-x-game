import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
/**
 * This class represents game menu (start/end/pause).
 * 
 * @author Kexin Wang
 * @version 2.0 (25-03-2025)
 */
public class GameMenu{
    private Stage stage;
    private Game game;
    private Ship ship;

    public GameMenu(Game game) {
        this.game = game;
        this.ship = game.getShip();
        this.stage = new Stage();
    }

    public void showStartMenu() {
        createMenu("start");
    }

    public void showPauseMenu() {
        createMenu("pause");
    }

    public void showGameOverMenu() {
        createMenu("gameover");
    }
    
    public void showGameVictoryMenu() {
        createMenu("gamevictory");
    }

    private void createMenu(String state) {
        Stage stage = new Stage();
        stage.setTitle("Game Menu");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(game.getStage()); 
        Text titleText = new Text();
        titleText.setFont(Font.font("Arial Black", 48));
        titleText.setFill(Color.WHITE);
        titleText.setEffect(new DropShadow(5, Color.BLACK));
        
        Text scoreText = new Text();
        scoreText.setFont(Font.font("Arial Black", 36));
        scoreText.setFill(Color.WHITE);
        scoreText.setEffect(new DropShadow(5, Color.BLACK));
        
        if (state.equals("start")) {
            titleText.setText("Air Combat X");
            scoreText.setText("--to Invaders");
        } else if (state.equals("pause")) {
            titleText.setText("Game Paused");
            scoreText.setText("Your Score: " + Game.score);
        } else if (state.equals("gameover")) {
            titleText.setText("Game Over");
            scoreText.setText("Your Score: " + Game.score);
        } else {
            titleText.setText("Game Victory");
            scoreText.setText("Your Score: " + Game.score);
        }
        
        TextFlow titleFlow = new TextFlow(titleText, scoreText);;
        titleFlow.setLineSpacing(1);
        titleFlow.setMaxWidth(400);
        
        TextFlow scoreFlow = new TextFlow(scoreText);
        scoreFlow.setLineSpacing(1);
        scoreFlow.setMaxWidth(400); 
        
        VBox titleBox = new VBox(5, titleFlow, scoreFlow);
        titleBox.setAlignment(Pos.CENTER);

        // Button Container
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        // Button
        Button startBtn = new Button("Start");
        Button restartBtn = new Button("Restart");
        Button continueBtn = new Button("Continue");
        Button exitBtn = new Button("Exit");
        Button infBtn = new Button("Infinite Mode");
        Button hpBtn = new Button("HP+3");

        // Set button
        styleButton(startBtn);
        styleButton(restartBtn);
        styleButton(continueBtn);
        styleButton(exitBtn);
        styleButton(infBtn);
        styleButton(hpBtn);

        // Event
        startBtn.setOnAction(e -> {
            game.startGame();
            stage.close();
        });

        restartBtn.setOnAction(e -> {
            game.resetGame();
            stage.close();
        });

        continueBtn.setOnAction(e -> {
            game.resumeGame();
            stage.close();
        });
        
        infBtn.setOnAction(e -> {
            game.startInf();
            stage.close();
        });
        
        hpBtn.setOnAction(e -> {
            ship.addHP(3);
            game.hpText.setText("HP: " + ship.getHP());
        });

        exitBtn.setOnAction(e -> System.exit(0));

        // 4 different forms
        if (state.equals("start")) {
            buttonBox.getChildren().addAll(startBtn, exitBtn);
        } else if (state.equals("pause")) {
            buttonBox.getChildren().addAll(continueBtn, restartBtn, hpBtn, exitBtn);
        } else if (state.equals("gameover")) {
            buttonBox.getChildren().addAll(restartBtn, exitBtn);
        } else if (state.equals("gamevictory")) {
            buttonBox.getChildren().addAll(restartBtn, exitBtn, infBtn);
        }

        // Main layout
        VBox layout = new VBox(50);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleText, scoreText, buttonBox);
        layout.setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, null)));

        // Show scene
        Scene scene = new Scene(layout, 500, 300);
        stage.setScene(scene);
        // Ban keyboard input
        Pane blockLayer = new Pane();
        blockLayer.setPickOnBounds(true);
        blockLayer.setOnKeyPressed(event -> event.consume());
        blockLayer.setOnKeyReleased(event -> event.consume());
        
        StackPane root = new StackPane();
        root.getChildren().addAll(layout, blockLayer);
        
        Scene scene1 = new Scene(root, 500, 300);

        // Bam close by 'x'
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
    }

    private void styleButton(Button btn) {
        btn.setFont(Font.font("Comic Sans MS", 18));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: #556B2F; -fx-border-radius: 5px;");
        btn.setEffect(new DropShadow(5, Color.BLACK));

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #6B8E23; -fx-border-radius: 5px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #556B2F; -fx-border-radius: 5px;"));
    }

}

