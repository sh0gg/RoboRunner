import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Leaderboard {

    private static final String JSON_PATH;

    static {
        File dir = new File("saves");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        JSON_PATH = "saves" + File.separator + "leaderboard.json";
    }

    public static void guardarResultado(String nombre, String tiempo, String seed) {
        List<JSONObject> datos = new ArrayList<>();

        // Leer archivo existente
        try {
            if (Files.exists(Paths.get(JSON_PATH))) {
                String contenido = new String(Files.readAllBytes(Paths.get(JSON_PATH)));
                JSONArray jsonArray = new JSONArray(contenido);
                for (int i = 0; i < jsonArray.length(); i++) {
                    datos.add(jsonArray.getJSONObject(i));
                }
            }
        } catch (IOException | JSONException ignored) {
        }

        // Añadir nuevo resultado
        JSONObject entrada = new JSONObject();
        entrada.put("nombre", nombre);
        entrada.put("fecha", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        entrada.put("tiempo", tiempo);
        entrada.put("seed", seed);
        datos.add(entrada);

        // Guardar
        try (FileWriter file = new FileWriter(JSON_PATH)) {
            file.write(new JSONArray(datos).toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mostrarLeaderboard() {
        try {
            if (!Files.exists(Paths.get(JSON_PATH))) {
                throw new FileNotFoundException("Archivo leaderboard.json no encontrado.");
            }

            String contenido = new String(Files.readAllBytes(Paths.get(JSON_PATH)));
            JSONArray jsonArray = new JSONArray(contenido);

            List<JSONObject> lista = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                lista.add(jsonArray.getJSONObject(i));
            }

            lista.sort(Comparator.comparingDouble(obj -> Double.parseDouble(obj.getString("tiempo"))));

            ListView<String> listView = new ListView<>();
            Map<String, String> seedMap = new HashMap<>();

            for (JSONObject obj : lista) {
                String line = String.format("%s - %s (%s) - %s",
                        obj.getString("seed"),
                        obj.getString("nombre"),
                        obj.getString("fecha"),
                        obj.getString("tiempo"));
                listView.getItems().add(line);
                seedMap.put(line, obj.getString("seed"));
            }

            Button btnJugarSeed = new Button("Jugar este mapa");
            btnJugarSeed.setOnAction(event -> {
                String seleccion = listView.getSelectionModel().getSelectedItem();
                if (seleccion != null) {
                    String seed = seedMap.get(seleccion);
                    Game game = new Game("Jugador", seed);
                    try {
                        game.start(new Stage());
                        ((Stage) btnJugarSeed.getScene().getWindow()).close(); // ⬅️ CIERRA EL LEADERBOARD
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            VBox content = new VBox(10,
                    new Label("🏆 Mejores partidas (haz clic en una y luego en 'Jugar este mapa'):"), listView,
                    btnJugarSeed);
            content.setPrefSize(500, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Leaderboard");
            alert.setHeaderText("Seed - Nombre (Fecha) - Tiempo");
            alert.getDialogPane().setContent(content);
            alert.showAndWait();

        } catch (IOException | JSONException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "No se pudo leer el leaderboard.");
            error.show();
        }
    }

}
