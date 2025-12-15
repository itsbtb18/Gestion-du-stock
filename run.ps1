# Run the Gestion du Stock JavaFX Application
# This script sets up Java environment and runs Maven

Write-Host "Starting Gestion du Stock application..." -ForegroundColor Green
Write-Host "Setting up Java environment..." -ForegroundColor Cyan

# Set Java environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24.0.1"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host "Building and launching JavaFX application..." -ForegroundColor Cyan

# Change to app directory
Push-Location -Path "$PSScriptRoot\app"

try {
    # Run Maven with full path
    cmd /c "`"C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.11\bin\mvn.cmd`" javafx:run"
} finally {
    # Return to original directory
    Pop-Location
}
