@echo off
REM Gestion du Stock - Launch Script
REM Run this script to start the application

echo Starting REB7A - Gestion de Stock...

set M2_REPO=%USERPROFILE%\.m2\repository\org\openjfx
set FX_VERSION=17.0.12
set MODULE_PATH=%M2_REPO%\javafx-controls\%FX_VERSION%;%M2_REPO%\javafx-fxml\%FX_VERSION%;%M2_REPO%\javafx-graphics\%FX_VERSION%;%M2_REPO%\javafx-base\%FX_VERSION%

cd /d "%~dp0"
java -cp "target/classes;lib/*" --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml org.example.app.MainApp

pause
