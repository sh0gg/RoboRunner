# RoboRunner

**RoboRunner** es un juego 2D tipo endless runner desarrollado en Java. El jugador controla a un robot que debe esquivar obstáculos y recolectar objetos para obtener la mayor puntuación posible.

## Características

- **Gráficos en 2D**: Estilo retro pixel-art.
- **Mecánicas de juego**: Esquivar obstáculos y recolectar objetos.
- **Puntuación**: Sistema de puntuación basado en la distancia recorrida y objetos recolectados.
- **Interfaz de usuario**: Menú de inicio, pausa y pantalla de fin de juego.
- **Persistencia**: Guardado de puntuaciones más altas.

## Estructura del proyecto

- `src/`: Código fuente del juego.
- `lib/`: Dependencias externas.
- `bin/`: Archivos compilados.
- `saves/`: Archivos de guardado de puntuaciones.
- `run.bat`: Script para ejecutar el juego en Windows.
- `RoboRunner.jar`: Archivo ejecutable del juego.

## Requisitos

- **Java**: JDK 23.
- **Sistema operativo**: Windows, macOS o Linux.

## Ejecución

### Desde el archivo JAR

1. Asegúrate de tener Java instalado.
2. Ejecuta el archivo `RoboRunner.jar` con el siguiente comando:

   ```bash
   java -jar RoboRunner.jar
   ```

### Desde el código fuente

1. Clona el repositorio:

   ```bash
   git clone https://github.com/sh0gg/RoboRunner.git
   ```

2. Navega al directorio del proyecto:

   ```bash
   cd RoboRunner
   ```

3. Compila el proyecto:

   ```bash
   javac -d bin src/*.java
   ```

4. Ejecuta el juego:

   ```bash
   java -cp bin Main
   ```

## Créditos

Desarrollado por [sh0gg](https://github.com/sh0gg).

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más información.
