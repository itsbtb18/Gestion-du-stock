# How to Install Maven

## Quick Installation Steps

### Method 1: Using Chocolatey (Easiest)
If you have Chocolatey package manager:
```powershell
choco install maven
```

### Method 2: Manual Installation

1. **Download Maven**
   - Go to: https://maven.apache.org/download.cgi
   - Download: `apache-maven-3.9.6-bin.zip` (or latest version)

2. **Extract**
   - Extract to: `C:\Program Files\Apache\maven`

3. **Set Environment Variables**
   - Open "System Properties" → "Environment Variables"
   - Add to System Variables:
     - Variable: `MAVEN_HOME`
     - Value: `C:\Program Files\Apache\maven`
   
   - Edit `Path` variable:
     - Add: `%MAVEN_HOME%\bin`

4. **Verify Installation**
   ```powershell
   mvn -version
   ```

### Method 3: Using Scoop
If you have Scoop package manager:
```powershell
scoop install maven
```

## After Installation

Once Maven is installed, run the application with:
```bash
mvn clean javafx:run
```

Or use the provided script:
```bash
.\run.bat
```
