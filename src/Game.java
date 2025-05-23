package Mapa;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class Game extends Application {

    private final int filas = 12;
    private final int columnas = 16;
    private char[][] mapa;
    private boolean[][] visitado;
    private List<int[]> caminoSeguro = new ArrayList<>();
    private int robotX = 0, robotY = 0;
    private final int NUM_BOMBAS = 20;
    private GridPane gridpane = new GridPane();
    private Stage primaryStage;
    private String nombreJugador;
    private long tiempoInicio;

    public Game(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("La aventura del robot 🤖");

        initMapa();
        renderMap();

        Scene scene = new Scene(gridpane);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A: mover("izq"); break;
                case D: mover("der"); break;
                case S: mover("abj"); break;
                case W: mover("arr"); break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        tiempoInicio = System.currentTimeMillis(); // ⏱️ start timer
    }

    private void mover(String dir) {
        int nuevaX = robotX;
        int nuevaY = robotY;

        switch (dir) {
            case "izq": nuevaX--; break;
            case "der": nuevaX++; break;
            case "arr": nuevaY--; break;
            case "abj": nuevaY++; break;
        }

        if (nuevaY >= 0 && nuevaY < filas &&
                nuevaX >= 0 && nuevaX < columnas &&
                mapa[nuevaY][nuevaX] != '1') {

            if (mapa[nuevaY][nuevaX] == 'X') {
                long tiempoFinal = System.currentTimeMillis();
                long duracionMs = tiempoFinal - tiempoInicio;
                String tiempoFormateado = formatearTiempo(duracionMs);
                Leaderboard.guardarResultado(nombreJugador, tiempoFormateado);
                mostrarAlerta("¡Victoria!", "🏅 ¡Has llegado a la meta!\nTiempo: " + tiempoFormateado);
                return;
            } else if (mapa[nuevaY][nuevaX] == 'B') {
                mostrarAlerta("¡Perdiste!", "💥 Pisaste una bomba.");
                return;
            }

            mapa[robotY][robotX] = '0';
            mapa[nuevaY][nuevaX] = 'R';
            robotX = nuevaX;
            robotY = nuevaY;

            renderMap();
        }
    }

    private String formatearTiempo(long ms) {
        long seg = ms / 1000;
        long milis = ms % 1000;
        return String.format("%02d.%03d", seg, milis);
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(mensaje);
        alert.setContentText("¿Qué deseas hacer?");

        ButtonType btnMenu = new ButtonType("Menú principal");
        ButtonType btnReintentar = new ButtonType("Volver a jugar");
        ButtonType btnSalir = new ButtonType("Salir");

        alert.getButtonTypes().setAll(btnReintentar, btnMenu, btnSalir);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == btnReintentar) {
                try {
                    Game nuevo = new Game(nombreJugador);
                    nuevo.start(new Stage());
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (result.get() == btnMenu) {
                Stage menuStage = new Stage();
                menuStage.setTitle("Aventura del Robot - Menú");

                Label label = new Label("Introduce tu nombre:");
                TextField nombreField = new TextField();

                Button btnJugar = new Button("Jugar");
                Button btnLeaderboard = new Button("Leaderboard");

                btnJugar.setOnAction(e -> {
                    String nombre = nombreField.getText().trim();
                    if (!nombre.isEmpty()) {
                        Game nuevoJuego = new Game(nombre);
                        try {
                            nuevoJuego.start(new Stage());
                            menuStage.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                btnLeaderboard.setOnAction(e -> {
                    Leaderboard.mostrarLeaderboard();
                });

                VBox layout = new VBox(10, label, nombreField, btnJugar, btnLeaderboard);
                layout.setStyle("-fx-padding: 20;");
                menuStage.setScene(new Scene(layout, 300, 200));
                menuStage.show();

                primaryStage.close();

            } else {
                primaryStage.close();
            }
        }
    }


    private void initMapa() {
        generarMapaAleatorio();
    }

    private void generarMapaAleatorio() {
        mapa = new char[filas][columnas];
        visitado = new boolean[filas][columnas];
        caminoSeguro.clear();

        for (int i = 0; i < filas; i++) {
            Arrays.fill(mapa[i], '1');
        }

        dfsCrearCaminoUnico(0, 0);

        for (int[] paso : caminoSeguro) {
            mapa[paso[0]][paso[1]] = '0';
            visitado[paso[0]][paso[1]] = true;
        }

        Random rand = new Random();
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (mapa[i][j] == '1' && rand.nextDouble() < 0.25) {
                    mapa[i][j] = '0';
                }
            }
        }

        mapa[0][0] = 'R';
        robotX = 0;
        robotY = 0;
        mapa[filas - 1][columnas - 2] = 'X';

        colocarBombas();
    }

    private boolean dfsCrearCaminoUnico(int y, int x) {
        if (y < 0 || y >= filas || x < 0 || x >= columnas || visitado[y][x]) return false;

        visitado[y][x] = true;
        caminoSeguro.add(new int[]{y, x});

        if (y == filas - 1 && x == columnas - 2) return true;

        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        shuffleArray(dirs);

        for (int[] d : dirs) {
            if (dfsCrearCaminoUnico(y + d[0], x + d[1])) return true;
        }

        caminoSeguro.remove(caminoSeguro.size() - 1);
        return false;
    }

    private void shuffleArray(int[][] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void colocarBombas() {
        Random rand = new Random();
        int bombasPuestas = 0;

        Set<String> caminoSeguroSet = new HashSet<>();
        for (int[] paso : caminoSeguro) {
            caminoSeguroSet.add(paso[0] + "," + paso[1]);
        }

        while (bombasPuestas < NUM_BOMBAS) {
            int y = rand.nextInt(filas);
            int x = rand.nextInt(columnas);

            if (mapa[y][x] == '0' &&
                    !caminoSeguroSet.contains(y + "," + x) &&
                    !(y == 0 && x == 0) &&
                    !(y == filas - 1 && x == columnas - 2)) {
                mapa[y][x] = 'B';
                bombasPuestas++;
            }
        }
    }

    private void renderMap() {
        gridpane.getChildren().clear();

        for (int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                Label lblCasilla = new Label();
                lblCasilla.setPrefSize(40, 40);
                lblCasilla.setAlignment(Pos.CENTER);
                lblCasilla.setStyle("-fx-border-width: 1; -fx-border-color: black;");

                switch (mapa[i][j]) {
                    case '1':
                        lblCasilla.setStyle("-fx-background-color: black;");
                        break;
                    case '0':
                        lblCasilla.setText("");
                        lblCasilla.setStyle("-fx-background-color: white;");
                        break;
                    case 'R':
                        lblCasilla.setText("🤖");
                        lblCasilla.setStyle("-fx-background-color: white; -fx-font-size: 20px;");
                        break;
                    case 'X':
                        lblCasilla.setText("🏅");
                        lblCasilla.setStyle("-fx-background-color: white; -fx-font-size: 20px;");
                        break;
                    case 'B':
                        lblCasilla.setText("💣");
                        lblCasilla.setStyle("-fx-background-color: white; -fx-text-fill: red; -fx-font-size: 20px;");
                        break;
                }

                gridpane.add(lblCasilla, j, i);
            }
        }
    }
}
