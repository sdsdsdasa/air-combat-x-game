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

public class GameView extends Application {
    private Stage stage;
    private Game game;

    @Override
    public void start(Stage primaryStage) { 
        this.stage = primaryStage;
        this.game = game;
        showStartMenu();
    }

    public void showStartMenu() {
        stage.setTitle("Game Menu");
        stage.setAlwaysOnTop(true);
        Text titleText = new Text();
        titleText.setFont(Font.font("Arial Black", 48));
        titleText.setFill(Color.WHITE);
        titleText.setEffect(new DropShadow(5, Color.BLACK));
        
        Text scoreText = new Text();
        scoreText.setFont(Font.font("Arial Black", 36));
        scoreText.setFill(Color.WHITE);
        scoreText.setEffect(new DropShadow(5, Color.BLACK));
        
        titleText.setText("Air Combat X");
        scoreText.setText("--to Invaders");

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
        Button exitBtn = new Button("Exit");

        // Set button
        styleButton(startBtn);
        styleButton(exitBtn);

        startBtn.setOnAction(e -> {
            stage.close();
            try {
                new Game().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        exitBtn.setOnAction(e -> System.exit(0));
        
        buttonBox.getChildren().addAll(startBtn, exitBtn);
        
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
