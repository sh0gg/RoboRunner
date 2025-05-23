package Mapa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aventura del Robot - Menú");

        // Título estilizado
        Label titulo = new Label("🤖 Aventura del Robot");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label label = new Label("Introduce tu nombre:");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Tu nombre aquí...");

        Button btnJugar = new Button("🎮 Jugar");
        Button btnLeaderboard = new Button("🏆 Leaderboard");

        btnJugar.setPrefWidth(200);
        btnLeaderboard.setPrefWidth(200);

        btnJugar.setOnAction(e -> {
            String nombre = nombreField.getText().trim();
            if (!nombre.isEmpty()) {
                Game game = new Game(nombre);
                try {
                    game.start(new Stage());
                    primaryStage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, ingresa tu nombre.");
                alert.showAndWait();
            }
        });

        btnLeaderboard.setOnAction(e -> Leaderboard.mostrarLeaderboard());

        VBox layout = new VBox(15, titulo, label, nombreField, btnJugar, btnLeaderboard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #cce7ff);");

        Scene scene = new Scene(layout, 350, 280);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
