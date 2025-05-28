@echo off
setlocal

REM Ruta corregida al JDK 17
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "JAVA_COMPILER=%JAVA_HOME%\bin\javac.exe"
set "JAR_TOOL=%JAVA_HOME%\bin\jar.exe"

echo Compilando archivos .java con JDK 17...

REM Crear carpeta bin de salida si no existe
if not exist dist\bin mkdir dist\bin

"%JAVA_COMPILER%" ^
  --module-path dist\lib ^
  --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml ^
  -cp "dist\lib\json-20250517.jar" ^
  -encoding UTF-8 ^
  -d dist\bin ^
  src\*.java

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ ERROR: Falló la compilación.
    pause
    exit /b 1
)

echo Empaquetando JAR final...

"%JAR_TOOL%" --create --file dist\RoboRunner.jar -C dist\bin .

echo ✅ Compilación y empaquetado completados.
pause
