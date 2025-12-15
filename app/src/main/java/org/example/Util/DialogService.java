package org.example.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.app.AppConfig;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public final class DialogService {
    
    private static Stage ownerStage;
    
    private DialogService() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    public static void setOwnerStage(Stage stage) {
        ownerStage = stage;
    }
    
    public static void showInfo(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = createAlert(AlertType.INFORMATION, title, message);
            alert.showAndWait();
        });
    }
    
    public static void showInfo(String title, String header, String message) {
        runOnFxThread(() -> {
            Alert alert = createAlert(AlertType.INFORMATION, title, message);
            alert.setHeaderText(header);
            alert.showAndWait();
        });
    }
    
    public static void showSuccess(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = createAlert(AlertType.INFORMATION, title, message);
            alert.setHeaderText("Succès");
            
            alert.getDialogPane().setStyle("-fx-background-color: #f0fff0;");
            alert.showAndWait();
        });
    }
    
    public static void showWarning(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = createAlert(AlertType.WARNING, title, message);
            alert.showAndWait();
        });
    }
    
    public static void showError(String title, String message) {
        runOnFxThread(() -> {
            Alert alert = createAlert(AlertType.ERROR, title, message);
            alert.showAndWait();
        });
    }
    
    public static void showError(String title, String message, Throwable exception) {
        runOnFxThread(() -> {
            Alert alert = createAlert(AlertType.ERROR, title, message);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String exceptionText = sw.toString();
            
            Label label = new Label("Détails de l'erreur:");
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
            
            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
        });
    }
    
    public static boolean showConfirmation(String title, String message) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        
        runOnFxThreadAndWait(() -> {
            Alert alert = createAlert(AlertType.CONFIRMATION, title, message);
            Optional<ButtonType> response = alert.showAndWait();
            result.set(response.isPresent() && response.get() == ButtonType.OK);
        });
        
        return result.get();
    }
    
    public static Optional<ButtonType> showConfirmation(String title, String message, 
            ButtonType... buttons) {
        AtomicReference<Optional<ButtonType>> result = new AtomicReference<>(Optional.empty());
        
        runOnFxThreadAndWait(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(ownerStage);
            alert.getButtonTypes().setAll(buttons);
            styleDialog(alert);
            result.set(alert.showAndWait());
        });
        
        return result.get();
    }
    
    public static boolean showYesNo(String title, String message) {
        ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        
        Optional<ButtonType> result = showConfirmation(title, message, yesButton, noButton);
        return result.isPresent() && result.get() == yesButton;
    }
    
    public static Optional<ButtonType> showYesNoCancel(String title, String message) {
        ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        return showConfirmation(title, message, yesButton, noButton, cancelButton);
    }
    
    public static Optional<String> showTextInput(String title, String message) {
        return showTextInput(title, message, "");
    }
    
    public static Optional<String> showTextInput(String title, String message, String defaultValue) {
        AtomicReference<Optional<String>> result = new AtomicReference<>(Optional.empty());
        
        runOnFxThreadAndWait(() -> {
            TextInputDialog dialog = new TextInputDialog(defaultValue);
            dialog.setTitle(title);
            dialog.setHeaderText(null);
            dialog.setContentText(message);
            dialog.initOwner(ownerStage);
            styleDialog(dialog);
            result.set(dialog.showAndWait());
        });
        
        return result.get();
    }
    
    public static Optional<Double> showNumberInput(String title, String message, double defaultValue) {
        Optional<String> result = showTextInput(title, message, String.valueOf(defaultValue));
        
        if (result.isPresent()) {
            try {
                return Optional.of(Double.parseDouble(result.get()));
            } catch (NumberFormatException e) {
                showError("Erreur", "Veuillez entrer un nombre valide");
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
    @SafeVarargs
    public static <T> Optional<T> showChoice(String title, String message, T... choices) {
        AtomicReference<Optional<T>> result = new AtomicReference<>(Optional.empty());
        
        runOnFxThreadAndWait(() -> {
            ChoiceDialog<T> dialog = new ChoiceDialog<>(choices[0], choices);
            dialog.setTitle(title);
            dialog.setHeaderText(null);
            dialog.setContentText(message);
            dialog.initOwner(ownerStage);
            styleDialog(dialog);
            result.set(dialog.showAndWait());
        });
        
        return result.get();
    }
    
    public static Optional<File> showFileOpen(String title, FileChooser.ExtensionFilter... filters) {
        AtomicReference<File> result = new AtomicReference<>();
        
        runOnFxThreadAndWait(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(title);
            fileChooser.getExtensionFilters().addAll(filters);
            result.set(fileChooser.showOpenDialog(ownerStage));
        });
        
        return Optional.ofNullable(result.get());
    }
    
    public static Optional<File> showFileSave(String title, String defaultName, 
            FileChooser.ExtensionFilter... filters) {
        AtomicReference<File> result = new AtomicReference<>();
        
        runOnFxThreadAndWait(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(title);
            fileChooser.setInitialFileName(defaultName);
            fileChooser.getExtensionFilters().addAll(filters);
            result.set(fileChooser.showSaveDialog(ownerStage));
        });
        
        return Optional.ofNullable(result.get());
    }
    
    public static ProgressDialog showProgress(String title, String message) {
        return new ProgressDialog(title, message);
    }
    
    private static Alert createAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(ownerStage);
        styleDialog(alert);
        return alert;
    }
    
    private static void styleDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        try {
            String cssPath = DialogService.class.getResource(AppConfig.CSS_PATH + "styles.css").toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
        } catch (Exception e) {
            
        }
        dialogPane.getStyleClass().add("modern-dialog");
    }
    
    private static void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
    
    private static void runOnFxThreadAndWait(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            CompletableFuture<Void> future = new CompletableFuture<>();
            Platform.runLater(() -> {
                try {
                    action.run();
                } finally {
                    future.complete(null);
                }
            });
            try {
                future.get();
            } catch (Exception e) {
                LoggerUtil.logError(DialogService.class, "Error running on FX thread", e);
            }
        }
    }
    
    public static class ProgressDialog {
        private final Alert alert;
        private final ProgressBar progressBar;
        private final Label statusLabel;
        
        public ProgressDialog(String title, String message) {
            alert = new Alert(AlertType.NONE);
            alert.setTitle(title);
            alert.initOwner(ownerStage);
            alert.initStyle(StageStyle.UTILITY);
            
            progressBar = new ProgressBar();
            progressBar.setPrefWidth(300);
            progressBar.setProgress(-1); 
            
            statusLabel = new Label(message);
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(statusLabel, progressBar);
            
            alert.getDialogPane().setContent(content);
            alert.getButtonTypes().add(ButtonType.CANCEL);
            
            styleDialog(alert);
        }
        
        public void show() {
            runOnFxThread(() -> alert.show());
        }
        
        public void close() {
            runOnFxThread(() -> alert.close());
        }
        
        public void setProgress(double progress) {
            runOnFxThread(() -> progressBar.setProgress(progress));
        }
        
        public void setStatus(String status) {
            runOnFxThread(() -> statusLabel.setText(status));
        }
    }
}
