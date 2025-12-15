package org.example.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.Client;
import org.example.model.entity.TypeClient;
import org.example.model.service.ClientService;

import java.util.List;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> codeColumn;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> prenomColumn;
    @FXML private TableColumn<Client, String> telephoneColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> typeColumn;
    @FXML private TableColumn<Client, Integer> pointsColumn;
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button showAllButton;
    @FXML private Button showVIPButton;
    
    @FXML private TextField codeField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<TypeClient> typeCombo;
    @FXML private TextField pointsField;
    @FXML private Label totalAchatsLabel;
    
    @FXML private Button newClientButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearFormButton;
    @FXML private Button addPointsButton;
    @FXML private Button usePointsButton;
    
    private final ClientService clientService = ClientService.getInstance();
    
    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private Client selectedClient = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupClientTable();
        setupTypeCombo();
        loadAllClients();
        setupListeners();
        updateFormState();
    }

    private void setupClientTable() {
        codeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCode()));
        
        nomColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom()));
        
        prenomColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPrenom()));
        
        telephoneColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelephone()));
        
        emailColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail()));
        
        typeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTypeClient().toString()));
        
        pointsColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getPointsFidelite()).asObject());

        clientTable.setItems(clientList);
    }

    private void setupTypeCombo() {
        typeCombo.setItems(FXCollections.observableArrayList(TypeClient.values()));
        typeCombo.getSelectionModel().select(TypeClient.NORMAL);
    }

    private void setupListeners() {
        
        clientTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectClient(newValue);
                }
            }
        );
    }

    private void loadAllClients() {
        try {
            List<Client> clients = clientService.searchClients("");
            clientList.clear();
            clientList.addAll(clients);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des clients: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllClients();
            return;
        }

        try {
            List<Client> clients = clientService.searchClients(query);
            clientList.clear();
            clientList.addAll(clients);
            
            if (clients.isEmpty()) {
                showAlert("Recherche", "Aucun client trouvé", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefresh() {
        loadAllClients();
        clearForm();
        searchField.clear();
    }

    @FXML
    private void handleShowAll() {
        loadAllClients();
    }

    @FXML
    private void handleShowVIP() {
        try {
            List<Client> vipClients = clientService.getVIPClients();
            clientList.clear();
            clientList.addAll(vipClients);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des clients VIP: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }

    private void selectClient(Client client) {
        selectedClient = client;
        isEditMode = true;
        
        codeField.setText(client.getCode());
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        telephoneField.setText(client.getTelephone());
        emailField.setText(client.getEmail());
        adresseField.setText(client.getAdresse());
        typeCombo.setValue(client.getTypeClient());
        pointsField.setText(String.valueOf(client.getPointsFidelite()));
        totalAchatsLabel.setText(String.format("%.2f %s", client.getTotalAchats(), org.example.app.AppConfig.CURRENCY_CODE));
        
        updateFormState();
    }

    @FXML
    private void handleNewClient() {
        clearForm();
        isEditMode = false;
        selectedClient = null;
        
        codeField.setText("AUTO");
        
        updateFormState();
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            Client client = new Client();
            
            client.setNom(nomField.getText().trim());
            client.setPrenom(prenomField.getText().trim());
            client.setTelephone(telephoneField.getText().trim());
            client.setEmail(emailField.getText().trim());
            client.setAdresse(adresseField.getText().trim());
            client.setTypeClient(typeCombo.getValue());
            client.setPointsFidelite(0);
            client.setTotalAchats(0.0);

            clientService.registerClient(client);
            
            showAlert("Succès", "Client créé avec succès!", Alert.AlertType.INFORMATION);
            
            loadAllClients();
            clearForm();
            isEditMode = false;
            updateFormState();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la création: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedClient == null) {
            showAlert("Erreur", "Veuillez sélectionner un client à modifier", 
                     Alert.AlertType.WARNING);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            selectedClient.setNom(nomField.getText().trim());
            selectedClient.setPrenom(prenomField.getText().trim());
            selectedClient.setTelephone(telephoneField.getText().trim());
            selectedClient.setEmail(emailField.getText().trim());
            selectedClient.setAdresse(adresseField.getText().trim());
            selectedClient.setTypeClient(typeCombo.getValue());

            clientService.updateClient(selectedClient);
            
            showAlert("Succès", "Client modifié avec succès!", Alert.AlertType.INFORMATION);
            
            loadAllClients();
            clearForm();
            isEditMode = false;
            selectedClient = null;
            updateFormState();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedClient == null) {
            showAlert("Erreur", "Veuillez sélectionner un client à supprimer", 
                     Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le client");
        confirmation.setContentText(String.format(
            "Êtes-vous sûr de vouloir supprimer le client %s %s ?",
            selectedClient.getNom(), selectedClient.getPrenom()
        ));

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                clientService.deleteClient(selectedClient.getId());
                
                showAlert("Succès", "Client supprimé avec succès!", Alert.AlertType.INFORMATION);
                
                loadAllClients();
                clearForm();
                isEditMode = false;
                selectedClient = null;
                updateFormState();
                
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
        isEditMode = false;
        selectedClient = null;
        updateFormState();
    }

    @FXML
    private void handleAddPoints() {
        if (selectedClient == null) {
            showAlert("Erreur", "Veuillez sélectionner un client", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Ajouter des points");
        dialog.setHeaderText("Ajouter des points de fidélité");
        dialog.setContentText("Nombre de points à ajouter:");

        dialog.showAndWait().ifPresent(pointsStr -> {
            try {
                int points = Integer.parseInt(pointsStr);
                if (points <= 0) {
                    showAlert("Erreur", "Le nombre de points doit être positif", Alert.AlertType.WARNING);
                    return;
                }

                clientService.addLoyaltyPoints(selectedClient.getId(), points);
                
                showAlert("Succès", 
                    String.format("%d points ajoutés avec succès!", points), 
                    Alert.AlertType.INFORMATION);
                
                loadAllClients();
                
                selectClient(clientService.searchClients(selectedClient.getCode()).get(0));
                
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez saisir un nombre valide", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ajout des points: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleUsePoints() {
        if (selectedClient == null) {
            showAlert("Erreur", "Veuillez sélectionner un client", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Utiliser des points");
        dialog.setHeaderText(String.format("Points disponibles: %d", selectedClient.getPointsFidelite()));
        dialog.setContentText("Nombre de points à utiliser:");

        dialog.showAndWait().ifPresent(pointsStr -> {
            try {
                int points = Integer.parseInt(pointsStr);
                if (points <= 0) {
                    showAlert("Erreur", "Le nombre de points doit être positif", Alert.AlertType.WARNING);
                    return;
                }

                if (points > selectedClient.getPointsFidelite()) {
                    showAlert("Erreur", "Points insuffisants", Alert.AlertType.WARNING);
                    return;
                }

                clientService.useLoyaltyPoints(selectedClient.getId(), points);
                
                showAlert("Succès", 
                    String.format("%d points utilisés avec succès!", points), 
                    Alert.AlertType.INFORMATION);
                
                loadAllClients();
                
                selectClient(clientService.searchClients(selectedClient.getCode()).get(0));
                
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez saisir un nombre valide", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'utilisation des points: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
            }
        });
    }

    private void clearForm() {
        codeField.clear();
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        emailField.clear();
        adresseField.clear();
        typeCombo.getSelectionModel().select(TypeClient.NORMAL);
        pointsField.setText("0");
        totalAchatsLabel.setText(String.format("0.00 %s", org.example.app.AppConfig.CURRENCY_CODE));
        clientTable.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            showAlert("Validation", "Le nom est obligatoire", Alert.AlertType.WARNING);
            return false;
        }

        if (prenomField.getText().trim().isEmpty()) {
            showAlert("Validation", "Le prénom est obligatoire", Alert.AlertType.WARNING);
            return false;
        }

        if (telephoneField.getText().trim().isEmpty()) {
            showAlert("Validation", "Le téléphone est obligatoire", Alert.AlertType.WARNING);
            return false;
        }

        String telephone = telephoneField.getText().trim();
        if (!telephone.matches("^(\\+212|0)[5-7][0-9]{8}$")) {
            showAlert("Validation", 
                "Format de téléphone invalide (ex: 0612345678 ou +212612345678)", 
                Alert.AlertType.WARNING);
            return false;
        }

        if (!emailField.getText().trim().isEmpty()) {
            String email = emailField.getText().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showAlert("Validation", "Format d'email invalide", Alert.AlertType.WARNING);
                return false;
            }
        }

        return true;
    }

    private void updateFormState() {
        boolean isNew = !isEditMode && selectedClient == null;
        boolean isEdit = isEditMode && selectedClient != null;

        saveButton.setDisable(!isNew);
        updateButton.setDisable(!isEdit);
        deleteButton.setDisable(!isEdit);
        addPointsButton.setDisable(!isEdit);
        usePointsButton.setDisable(!isEdit);
        
        codeField.setEditable(false); 
        pointsField.setEditable(false); 
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
