@echo off
setlocal enabledelayedexpansion

:: ----------------------------
:: CONFIGURACIÓN
:: ----------------------------

set "JAVA17=C:\Program Files\Java\jdk-17"
set "JAVA_FX_SDK=E:\Descargas\javafx-sdk-17.0.15"
set "DIST=dist"
set "RUNTIME=runtime"
set "ZIPNAME=RoboRunner_FullRelease.zip"

:: ----------------------------
:: LIMPIEZA Y PREPARACIÓN
:: ----------------------------

echo 🔄 Limpiando carpetas...
rmdir /s /q %DIST% 2>nul
rmdir /s /q %RUNTIME% 2>nul
mkdir %DIST%\lib
mkdir %DIST%\assets

:: ----------------------------
:: COMPILACIÓN
:: ----------------------------

echo 🛠 Compilando con Java 17...
"%JAVA17%\bin\javac.exe" -encoding UTF-8 -d out -cp "lib/*" src\*.java

:: ----------------------------
:: GENERACIÓN JAR
:: ----------------------------

echo 📦 Empaquetando JAR principal...
"%JAVA17%\bin\jar.exe" --create --file %DIST%\RoboRunner.jar --main-class=Main -C out .

:: ----------------------------
:: COPIA DE RECURSOS Y LIBRERÍAS
:: ----------------------------

echo 📁 Copiando recursos...
xcopy /E /I /Y assets %DIST%\assets >nul
copy robot.ico %DIST%\robot.ico >nul

echo 📚 Copiando librerías JavaFX...
xcopy /Y "%JAVA_FX_SDK%\lib\*.jar" %DIST%\lib\ >nul

:: ----------------------------
:: GENERACIÓN RUNTIME CON JLINK
:: ----------------------------

echo 🚀 Generando runtime personalizado con jlink...
"%JAVA17%\bin\jlink.exe" --module-path "%JAVA17%\jmods;%JAVA_FX_SDK%\lib" --add-modules java.base,java.desktop,javafx.controls,javafx.fxml,javafx.graphics,javafx.base --output %RUNTIME% --strip-debug --compress 2 --no-header-files --no-man-pages

echo 📋 Copiando DLLs nativas a runtime\bin...
xcopy /Y "%JAVA_FX_SDK%\bin\*.dll" %RUNTIME%\bin\ >nul

:: ----------------------------
:: CREACIÓN ZIP FINAL
:: ----------------------------

echo 🗜 Empaquetando todo en %ZIPNAME%...
powershell -Command "Compress-Archive -Path '%DIST%\*','%RUNTIME%\*' -DestinationPath '%ZIPNAME%' -Force"

:: ----------------------------
:: FIN
:: ----------------------------

echo.
echo ✅ Build completado. Archivo listo: %ZIPNAME%
pause
