@echo off
..\runtime\bin\java.exe ^
--module-path .\lib ^
--add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml ^
-cp ".\RoboRunner.jar;.\lib\json-20250517.jar" ^
Main
