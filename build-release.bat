@echo off
setlocal enabledelayedexpansion

:: ⚙️ CONFIGURACIÓN

set "JAVA17=C:\Program Files\Java\jdk-17"
set "LAUNCH4J=C:\Program Files (x86)\Launch4j\launch4j.exe"
set "CONFIG=launch4j.xml"
set "DISTFOLDER=dist"
set "ZIPNAME=RoboRunner_v0.81_sh0gg.zip"

:: 🧹 LIMPIEZA
echo 🔄 Limpiando carpeta %DISTFOLDER%...
rmdir /s /q %DISTFOLDER% 2>nul
mkdir %DISTFOLDER%\lib
mkdir %DISTFOLDER%\assets

:: 🛠 COMPILACIÓN
echo 🛠 Compilando código con Java 17...
"%JAVA17%\bin\javac.exe" -encoding UTF-8 -d out -cp "lib/*" src\*.java

:: 📦 CREACIÓN DEL JAR
echo 📦 Generando JAR con clase principal...
"%JAVA17%\bin\jar.exe" --create --file %DISTFOLDER%\RoboRunner.jar --main-class=Main -C out .

:: 🧳 COPIA DE RECURSOS
echo 📁 Copiando recursos y librerías...
xcopy /E /I /Y assets %DISTFOLDER%\assets >nul
copy robot.ico %DISTFOLDER%\robot.ico >nul
copy lib\javafx.base.jar %DISTFOLDER%\lib\
copy lib\javafx.controls.jar %DISTFOLDER%\lib\
copy lib\javafx.graphics.jar %DISTFOLDER%\lib\

:: 📝 CREAR README
echo 📝 Generando README...
(
echo RoboRunner v0.81 - por sh0gg
echo ---------------------------
echo Este juego requiere tener instalado Java 17 para funcionar correctamente.
echo Puedes descargarlo desde:
echo https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
echo.
echo Para jugar, simplemente ejecuta el archivo: RobotRunner.exe
echo.
echo ¡Gracias por jugar!
) > %DISTFOLDER%\README.txt

:: 🚀 EJECUTANDO LAUNCH4J
echo 🚀 Ejecutando Launch4j...
"%LAUNCH4J%" %CONFIG%

:: 🗜️ CREAR ZIP
echo 🗜️ Empaquetando build en %ZIPNAME%...
powershell -Command "Compress-Archive -Path '%DISTFOLDER%\*' -DestinationPath '%ZIPNAME%'"

:: ✅ FIN
echo.
echo ✅ Build completado: %ZIPNAME% listo para distribuir.
pause
