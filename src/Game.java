import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private String seed;
    private Random random;

    public Game(String nombreJugador) {
        this(nombreJugador, null);
    }

    public Game(String nombreJugador, String seed) {
        this.nombreJugador = nombreJugador;
        this.seed = seed;
        this.random = (seed != null) ? new Random(seed.hashCode()) : new Random();
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
                case LEFT, A: mover("izq"); break;
                case RIGHT, D: mover("der"); break;
                case DOWN, S: mover("abj"); break;
                case UP, W: mover("arr"); break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        tiempoInicio = System.currentTimeMillis();
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

        if (nuevaY >= 0 && nuevaY < filas && nuevaX >= 0 && nuevaX < columnas && mapa[nuevaY][nuevaX] != '1') {
            if (mapa[nuevaY][nuevaX] == 'X') {
                long tiempoFinal = System.currentTimeMillis();
                long duracionMs = tiempoFinal - tiempoInicio;
                String tiempoFormateado = formatearTiempo(duracionMs);
                Leaderboard.guardarResultado(nombreJugador, tiempoFormateado, seed);
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
        alert.setHeaderText(mensaje + "\nSeed: " + (seed != null ? seed : "aleatoria"));
        alert.setContentText("¿Qué deseas hacer?");

        ButtonType btnMenu = new ButtonType("Menú principal");
        ButtonType btnReintentar = new ButtonType("Volver a jugar");
        ButtonType btnSalir = new ButtonType("Salir");
        ButtonType btnCopiarSeed = new ButtonType("Copiar seed");

        alert.getButtonTypes().setAll(btnReintentar, btnMenu, btnSalir, btnCopiarSeed);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == btnReintentar) {
                try {
                    Game nuevo = new Game(nombreJugador, seed);
                    nuevo.start(new Stage());
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (result.get() == btnMenu) {
                Main.mostrarMenuPrincipal();
            } else if (result.get() == btnCopiarSeed) {
                ClipboardContent content = new ClipboardContent();
                content.putString(seed != null ? seed : "aleatoria");
                Clipboard.getSystemClipboard().setContent(content);
                mostrarAlerta(titulo, mensaje); // volver a mostrar menú
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

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (mapa[i][j] == '1' && random.nextDouble() < 0.25) {
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
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void colocarBombas() {
        int bombasPuestas = 0;
        Set<String> caminoSeguroSet = new HashSet<>();
        for (int[] paso : caminoSeguro) {
            caminoSeguroSet.add(paso[0] + "," + paso[1]);
        }

        while (bombasPuestas < NUM_BOMBAS) {
            int y = random.nextInt(filas);
            int x = random.nextInt(columnas);

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
