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

import java.util.UUID;

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

        Label seedLabel = new Label("Seed (opcional):");
        TextField seedField = new TextField();
        seedField.setPromptText("Ej: mapa123");

        Button btnJugar = new Button("🎮 Jugar");
        Button btnLeaderboard = new Button("🏆 Leaderboard");

        btnJugar.setPrefWidth(200);
        btnLeaderboard.setPrefWidth(200);

        btnJugar.setOnAction(e -> {
            String nombre = nombreField.getText().trim();
            String seed = seedField.getText().trim();
            if (!nombre.isEmpty()) {
                if (seed.isEmpty()) {
                    seed = generarSeedAleatoria();
                }
                Game game = new Game(nombre, seed);
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

        VBox layout = new VBox(15, titulo, label, nombreField, seedLabel, seedField, btnJugar, btnLeaderboard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #cce7ff);");

        Scene scene = new Scene(layout, 350, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String generarSeedAleatoria() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
