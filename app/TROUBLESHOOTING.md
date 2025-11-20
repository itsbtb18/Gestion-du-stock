# 🔧 Troubleshooting Guide

## Common Issues and Solutions

---

## Issue: "Module javafx.controls not found"

**Cause**: JavaFX is not properly configured in your project.

**Solution**:
1. Ensure Java 11+ is installed
2. Run with Maven: `mvn javafx:run`
3. If using IDE, configure VM options:
   ```
   --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
   ```

---

## Issue: "Error loading CSS file"

**Cause**: The `styles.css` file is not in the resources folder or path is incorrect.

**Solution**:
1. Verify `styles.css` exists in `src/main/resources/`
2. Check the path in Main.java:
   ```java
   scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
   ```
3. Clean and rebuild: `mvn clean compile`

---

## Issue: Application window is too small

**Cause**: Display scaling or resolution issues.

**Solution**:
Adjust window size in `Main.java`:
```java
Scene scene = new Scene(mainLayout, 1600, 900); // Increase size
primaryStage.setMinWidth(1400);
primaryStage.setMinHeight(800);
```

---

## Issue: Charts not displaying properly

**Cause**: Missing JavaFX chart module or data issues.

**Solution**:
1. Ensure all JavaFX modules are loaded
2. Check mock data in view classes
3. Verify chart containers have proper size constraints

---

## Issue: Animations are jerky or slow

**Cause**: Hardware acceleration disabled or too many concurrent animations.

**Solution**:
1. Enable hardware acceleration (usually automatic)
2. Reduce animation duration in ViewTransitions.java
3. Limit the number of animated elements

---

## Issue: Table cells not displaying data

**Cause**: PropertyValueFactory property names don't match getter methods.

**Solution**:
Ensure your data class has proper getters:
```java
public class Product {
    private String name;
    
    public String getName() {  // Must be "get" + PropertyName
        return name;
    }
}
```

---

## Issue: Buttons not responding to clicks

**Cause**: Event handlers not properly set or overlapping nodes.

**Solution**:
1. Check `setOnAction()` or `setOnMouseClicked()` is called
2. Ensure no transparent overlays blocking clicks
3. Verify button is not disabled

---

## Issue: Maven build fails

**Cause**: Missing dependencies or incorrect POM configuration.

**Solution**:
1. Update Maven: `mvn clean install`
2. Check internet connection (Maven downloads dependencies)
3. Verify Java version: `java -version` (should be 17+)
4. Clear Maven cache: Delete `.m2/repository` folder and rebuild

---

## Issue: Application crashes on startup

**Cause**: Missing module exports or initialization errors.

**Solution**:
1. Check `module-info.java` exists and is correct
2. Review console error messages
3. Ensure all view classes are properly imported
4. Verify JavaFX runtime is available

---

## Issue: Sidebar not showing properly

**Cause**: CSS not loading or layout issues.

**Solution**:
1. Verify sidebar style class is set: `.sidebar`
2. Check if gradient is supported
3. Ensure NavigationSidebar is added to BorderPane left

---

## Issue: Unicode emojis not displaying

**Cause**: Font doesn't support emoji characters.

**Solution**:
1. Use system emoji font
2. Replace emojis with FontAwesome icons
3. Or use Unicode fallback characters

---

## Issue: Window doesn't maximize properly

**Cause**: Scene size constraints.

**Solution**:
```java
primaryStage.setMaximized(false); // Allow custom sizing
// Or set to true for full screen start
primaryStage.setMaximized(true);
```

---

## Running on Different Platforms

### Windows
```bash
run.bat
```
or
```bash
mvn javafx:run
```

### Mac/Linux
```bash
chmod +x run.sh  # If you create a shell script
./run.sh
```
or
```bash
mvn javafx:run
```

---

## Performance Tips

1. **Reduce animation complexity**:
   ```java
   // Use shorter durations
   Duration.millis(200) instead of Duration.millis(500)
   ```

2. **Limit table rows**:
   ```java
   // Paginate large datasets
   table.setItems(data.subList(0, 100));
   ```

3. **Disable effects in low-end systems**:
   ```java
   // Comment out DropShadow and blur effects
   // card.setEffect(null);
   ```

---

## Getting Help

If you encounter issues not listed here:

1. **Check Console Output**: Look for error messages and stack traces
2. **Verify Configuration**: Ensure all paths and dependencies are correct
3. **Test Incrementally**: Comment out sections to isolate the issue
4. **Check JavaFX Version**: Ensure compatibility between Java and JavaFX versions

---

## Useful Commands

```bash
# Clean build
mvn clean

# Compile only
mvn compile

# Run application
mvn javafx:run

# Package as JAR
mvn package

# Check dependencies
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates
```

---

## System Requirements

**Minimum**:
- Java 17
- 4GB RAM
- 1280x720 display
- Windows 10, macOS 10.14+, or Linux with X11

**Recommended**:
- Java 17 or 21
- 8GB RAM
- 1920x1080 display
- Modern GPU for smooth animations

---

**Still having issues?** Review the README.md and DESIGN_GUIDE.md for more details.
