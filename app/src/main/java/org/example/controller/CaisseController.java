package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.*;
import org.example.model.pattern.strategy.PaymentStrategy;
import org.example.model.pattern.strategy.CardPayment;
import org.example.model.pattern.strategy.CashPayment;
import org.example.model.service.ClientService;
import org.example.model.service.ProduitService;
import org.example.model.service.VenteService;
import org.example.model.service.PromotionService;
import org.example.util.StoreContext;
import org.example.model.entity.Promotion;
import org.example.model.entity.LoyaltyProgramConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Caisse (POS/Checkout) view
 * Handles product search, cart management, payment processing, and receipt generation
 */
public class CaisseController {

    // Services
    private final ProduitService produitService = ProduitService.getInstance();
    private final ClientService clientService = ClientService.getInstance();
    private final VenteService venteService = VenteService.getInstance();
    private final PromotionService promotionService = PromotionService.getInstance();
    // FXML UI Components - Product Search
    @FXML private TextField productCodeField;
    @FXML private Button searchProductButton;
    @FXML private Button scanBarcodeButton;
    @FXML private Label productInfoLabel;

    // FXML UI Components - Cart Table
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> codeColumn;
    @FXML private TableColumn<CartItem, String> nameColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Double> unitPriceColumn;
    @FXML private TableColumn<CartItem, Double> subtotalColumn;
    @FXML private Button addToCartButton;
    @FXML private Button removeFromCartButton;
    @FXML private Button clearCartButton;
    @FXML private TextField quantityField;

    // FXML UI Components - Client Section
    @FXML private TextField clientSearchField;
    @FXML private Button searchClientButton;
    @FXML private Label clientNameLabel;
    @FXML private Label clientPointsLabel;
    @FXML private Label clientTypeLabel;
    @FXML private Button clearClientButton;

    // FXML UI Components - Payment Section
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextField discountField;
    @FXML private Label subtotalLabel;
    @FXML private Label tvaLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Button validateSaleButton;
    @FXML private Button cancelSaleButton;

    // FXML UI Components - Receipt
    @FXML private TextArea receiptArea;
    @FXML private Button printReceiptButton;

    // State
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private Produit currentProduct;
    private Client currentClient;

    /**
     * Initialize the controller and set up UI bindings
     */
    @FXML
    public void initialize() {
        setupCartTable();
        setupPaymentMethods();
        setupListeners();
        updateTotals();
        
        // Initialize quantity field
        quantityField.setText("1");
    }

    /**
     * Configure cart table columns
     */
    private void setupCartTable() {
        codeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProduit().getCode()));
        
        nameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        
        quantityColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        
        unitPriceColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getProduit().getPrix()).asObject());
        
        subtotalColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());

        cartTable.setItems(cartItems);
    }

    /**
     * Configure payment method ComboBox
     */
    private void setupPaymentMethods() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList(
            "Espèce", "Carte Bancaire", "Chèque"
        ));
        paymentMethodCombo.getSelectionModel().selectFirst();
    }



    /**
     * Set up event listeners
     */
    private void setupListeners() {
        // Update totals when cart changes
        cartItems.addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            updateTotals();
        });

        // Update totals when discount changes
        discountField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateTotals();
        });
    }

    /**
     * Search for a product by code or barcode
     */
    @FXML
    private void handleSearchProduct() {
        String code = productCodeField.getText().trim();
        if (code.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un code produit", Alert.AlertType.WARNING);
            return;
        }

        try {
            currentProduct = produitService.searchProduits(code).stream()
                .findFirst()
                .orElse(null);

            if (currentProduct == null) {
                showAlert("Produit introuvable", "Aucun produit avec le code: " + code, Alert.AlertType.WARNING);
                productInfoLabel.setText("Produit introuvable");
                return;
            }

            // Display product info
            productInfoLabel.setText(String.format("%s - %.2f DH (Stock: %d)", 
                currentProduct.getNom(), 
                currentProduct.getPrix(),
                currentProduct.getQuantiteStock()));

            // Check stock
            if (currentProduct.getQuantiteStock() <= 0) {
                showAlert("Stock insuffisant", "Ce produit n'est plus en stock", Alert.AlertType.WARNING);
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Add current product to cart
     */
    @FXML
    private void handleAddToCart() {
        if (currentProduct == null) {
            showAlert("Erreur", "Veuillez d'abord rechercher un produit", Alert.AlertType.WARNING);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                showAlert("Erreur", "La quantité doit être supérieure à 0", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Quantité invalide", Alert.AlertType.WARNING);
            return;
        }

        // Check stock availability
        if (quantity > currentProduct.getQuantiteStock()) {
            showAlert("Stock insuffisant", 
                String.format("Stock disponible: %d", currentProduct.getQuantiteStock()), 
                Alert.AlertType.WARNING);
            return;
        }

        // Check if product already in cart
        Optional<CartItem> existingItem = cartItems.stream()
            .filter(item -> item.getProduit().getId().equals(currentProduct.getId()))
            .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            if (newQuantity > currentProduct.getQuantiteStock()) {
                showAlert("Stock insuffisant", 
                    String.format("Stock disponible: %d", currentProduct.getQuantiteStock()), 
                    Alert.AlertType.WARNING);
                return;
            }
            
            item.setQuantity(newQuantity);
            cartTable.refresh();
        } else {
            // Add new item
            CartItem newItem = new CartItem(currentProduct, quantity);
            
            // Apply best promotion if available
            Long categoryId = currentProduct.getCategorie() != null ? currentProduct.getCategorie().getId() : null;
            Optional<Promotion> bestPromo = promotionService.getBestPromotion(
                currentProduct.getId(), 
                categoryId, 
                currentProduct.getPrix()
            );
            
            if (bestPromo.isPresent()) {
                Promotion promo = bestPromo.get();
                double discount = promo.calculateDiscount(currentProduct.getPrix());
                newItem.setAppliedPromotion(promo);
                newItem.setPromotionDiscount(discount);
                System.out.println("Applied promotion: " + promo.getName() + " - Discount: " + discount + " DH");
            }
            
            cartItems.add(newItem);
        }

        // Reset product selection and update totals
        currentProduct = null;
        productCodeField.clear();
        productInfoLabel.setText("");
        quantityField.setText("1");
        updateTotals();
    }

    /**
     * Remove selected item from cart
     */
    @FXML
    private void handleRemoveFromCart() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
        } else {
            showAlert("Erreur", "Veuillez sélectionner un article à supprimer", Alert.AlertType.WARNING);
        }
    }

    /**
     * Clear entire cart
     */
    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Vider le panier");
        confirmation.setContentText("Êtes-vous sûr de vouloir vider le panier ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cartItems.clear();
            currentClient = null;
            updateClientDisplay();
        }
    }

    /**
     * Search for a client
     */
    @FXML
    private void handleSearchClient() {
        String search = clientSearchField.getText().trim();
        if (search.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un code client, téléphone ou email", Alert.AlertType.WARNING);
            return;
        }

        try {
            List<Client> clients = clientService.searchClients(search);
            
            if (clients.isEmpty()) {
                showAlert("Client introuvable", "Aucun client trouvé avec: " + search, Alert.AlertType.WARNING);
                return;
            }

            if (clients.size() == 1) {
                currentClient = clients.get(0);
                updateClientDisplay();
            } else {
                // Multiple clients found - show selection dialog
                currentClient = showClientSelectionDialog(clients);
                if (currentClient != null) {
                    updateClientDisplay();
                }
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche client: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Clear current client
     */
    @FXML
    private void handleClearClient() {
        currentClient = null;
        clientSearchField.clear();
        updateClientDisplay();
    }

    /**
     * Update client display labels
     */
    private void updateClientDisplay() {
        if (currentClient != null) {
            clientNameLabel.setText(currentClient.getNom() + " " + currentClient.getPrenom());
            clientPointsLabel.setText(String.format("%d points", currentClient.getPointsFidelite()));
            clientTypeLabel.setText(currentClient.getTypeClient().toString());
        } else {
            clientNameLabel.setText("Aucun client");
            clientPointsLabel.setText("0 points");
            clientTypeLabel.setText("-");
        }
    }

    /**
     * Show client selection dialog when multiple clients found
     */
    private Client showClientSelectionDialog(List<Client> clients) {
        ChoiceDialog<Client> dialog = new ChoiceDialog<>(clients.get(0), clients);
        dialog.setTitle("Sélection Client");
        dialog.setHeaderText("Plusieurs clients trouvés");
        dialog.setContentText("Choisissez un client:");

        Optional<Client> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Update total labels
     */
    private void updateTotals() {
        double subtotal = cartItems.stream()
            .mapToDouble(CartItem::getSubtotal)
            .sum();

        double discountPercent = 0.0;
        try {
            String discountText = discountField.getText().trim();
            if (!discountText.isEmpty()) {
                discountPercent = Double.parseDouble(discountText);
                if (discountPercent < 0 || discountPercent > 15) {
                    discountPercent = 0.0;
                    discountField.setText("0");
                }
            }
        } catch (NumberFormatException e) {
            discountPercent = 0.0;
            discountField.setText("0");
        }

        double discountAmount = subtotal * (discountPercent / 100.0);
        double subtotalAfterDiscount = subtotal - discountAmount;
        
        // Get TVA rate from StoreContext
        double tvaRate = StoreContext.getInstance().getVatRate();
        double tvaAmount = subtotalAfterDiscount * tvaRate;
        double total = subtotalAfterDiscount + tvaAmount;

        subtotalLabel.setText(String.format("%.2f DH", subtotal));
        discountLabel.setText(String.format("-%.2f DH (%.1f%%)", discountAmount, discountPercent));
        tvaLabel.setText(String.format("%.2f DH (%.0f%%)", tvaAmount, tvaRate * 100));
        totalLabel.setText(String.format("%.2f DH", total));
    }

    /**
     * Process payment - alias for handleValidateSale
     */
    @FXML
    private void handleProcessPayment() {
        handleValidateSale();
    }

    /**
     * Validate and process the sale
     */
    @FXML
    private void handleValidateSale() {
        if (cartItems.isEmpty()) {
            showAlert("Erreur", "Le panier est vide", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Get payment method
            String paymentMethodStr = paymentMethodCombo.getValue();
            PaymentStrategy paymentStrategy = getPaymentStrategy(paymentMethodStr);

            // Get discount
            double discountPercent = 0.0;
            try {
                String discountText = discountField.getText().trim();
                if (!discountText.isEmpty()) {
                    discountPercent = Double.parseDouble(discountText);
                }
            } catch (NumberFormatException e) {
                discountPercent = 0.0;
            }

            // Create line items
            List<LigneVente> lignes = new ArrayList<>();
            double subtotal = 0.0;
            for (CartItem item : cartItems) {
                LigneVente ligne = new LigneVente();
                ligne.setProduit(item.getProduit());
                ligne.setQuantite(item.getQuantity());
                ligne.setPrixUnitaire(item.getProduit().getPrix());
                ligne.setSousTotal(item.getSubtotal());
                lignes.add(ligne);
                subtotal += item.getSubtotal();
            }

            // Create Vente object
            Vente vente = new Vente();
            if (currentClient != null) {
                vente.setClient(currentClient);
            }
            vente.setVendeurId(1L); // Default vendeur - in production, get from session
            vente.setLignes(lignes);
            
            // Calculate totals with configurable VAT rate
            double discountAmount = subtotal * (discountPercent / 100.0);
            double subtotalAfterDiscount = subtotal - discountAmount;
            double vatRate = StoreContext.getInstance().getVatRate();
            double tva = subtotalAfterDiscount * vatRate;
            vente.setMontantTotal(subtotal);
            vente.setMontantRemise(discountAmount);
            vente.setMontantFinal(subtotalAfterDiscount + tva);
            vente.setDateVente(LocalDateTime.now());

            // Apply loyalty points if enabled and client exists
            if (currentClient != null && StoreContext.getInstance().isLoyaltyProgramEnabled()) {
                LoyaltyProgramConfig loyaltyConfig = StoreContext.getInstance().getLoyaltyConfig();
                int pointsEarned = loyaltyConfig.calculatePointsEarned(vente.getMontantFinal());
                currentClient.setPointsFidelite(currentClient.getPointsFidelite() + pointsEarned);
                clientService.updateClient(currentClient);
                System.out.println("Client earned " + pointsEarned + " loyalty points");
            }

            // Process sale through service
            vente = venteService.processSale(vente, paymentStrategy);

            // Generate receipt
            String receipt = generateReceipt(vente, lignes);
            receiptArea.setText(receipt);

            // Show success message
            showAlert("Succès", 
                String.format("Vente enregistrée avec succès!\nMontant total: %.2f DH", vente.getMontantTotal()), 
                Alert.AlertType.INFORMATION);

            // Reset cart
            cartItems.clear();
            currentClient = null;
            currentProduct = null;
            productCodeField.clear();
            clientSearchField.clear();
            discountField.setText("0");
            updateClientDisplay();
            updateTotals();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la validation: " + e.getMessage(), Alert.AlertType.ERROR);
            org.example.util.LoggerUtil.logError(CaisseController.class, "Error validating sale", e);
        }
    }

    /**
     * Cancel current sale
     */
    @FXML
    private void handleCancelSale() {
        handleClearCart();
        receiptArea.clear();
    }

    /**
     * Print receipt (simulated - would integrate with printer)
     */
    @FXML
    private void handlePrintReceipt() {
        if (receiptArea.getText().isEmpty()) {
            showAlert("Erreur", "Aucun ticket à imprimer", Alert.AlertType.WARNING);
            return;
        }

        // Print simulation - in production, integrate with thermal printer driver
        showAlert("Impression", "Impression du ticket en cours...", Alert.AlertType.INFORMATION);
    }

    /**
     * Get payment strategy based on payment method
     */
    private PaymentStrategy getPaymentStrategy(String paymentMethod) {
        return switch (paymentMethod) {
            case "Carte Bancaire" -> new CardPayment("0000000000000000", "Client", "12/25");
            case "Chèque" -> new CashPayment(0); // Placeholder - should ask for amount
            default -> new CashPayment(0); // Placeholder - should ask for amount
        };
    }

    /**
     * Generate receipt text
     */
    private String generateReceipt(Vente vente, List<LigneVente> lignes) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====================================\n");
        receipt.append("         REB7A SUPERMARCHÉ\n");
        receipt.append("=====================================\n");
        receipt.append(String.format("Date: %s\n", LocalDateTime.now()));
        receipt.append(String.format("Ticket N°: %s\n", vente.getNumero()));
        receipt.append("-------------------------------------\n");

        if (currentClient != null) {
            receipt.append(String.format("Client: %s %s\n", currentClient.getNom(), currentClient.getPrenom()));
            receipt.append(String.format("Type: %s\n", currentClient.getTypeClient()));
            receipt.append("-------------------------------------\n");
        }

        receipt.append("Articles:\n");
        for (CartItem item : cartItems) {
            receipt.append(String.format("%-20s x%d\n", 
                item.getProduit().getNom(), 
                item.getQuantity()));
            receipt.append(String.format("  %.2f DH x %d = %.2f DH\n",
                item.getProduit().getPrix(),
                item.getQuantity(),
                item.getSubtotal()));
        }

        receipt.append("-------------------------------------\n");
        receipt.append(String.format("Sous-total:      %s\n", subtotalLabel.getText()));
        receipt.append(String.format("Remise:          %s\n", discountLabel.getText()));
        receipt.append(String.format("TVA (20%%):       %s\n", tvaLabel.getText()));
        receipt.append("=====================================\n");
        receipt.append(String.format("TOTAL:           %s\n", totalLabel.getText()));
        receipt.append("=====================================\n");
        receipt.append(String.format("Mode paiement: %s\n", paymentMethodCombo.getValue()));
        
        if (currentClient != null) {
            int pointsEarned = (int) (vente.getMontantTotal() * 10);
            receipt.append(String.format("Points gagnés: %d\n", pointsEarned));
            receipt.append(String.format("Total points: %d\n", currentClient.getPointsFidelite() + pointsEarned));
        }
        
        receipt.append("-------------------------------------\n");
        receipt.append("      Merci de votre visite!\n");
        receipt.append("=====================================\n");

        return receipt.toString();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Inner class to represent cart items
     */
    public static class CartItem {
        private Produit produit;
        private int quantity;
        private Promotion appliedPromotion;
        private double promotionDiscount;

        public CartItem(Produit produit, int quantity) {
            this.produit = produit;
            this.quantity = quantity;
            this.appliedPromotion = null;
            this.promotionDiscount = 0.0;
        }

        public Produit getProduit() {
            return produit;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public Promotion getAppliedPromotion() {
            return appliedPromotion;
        }
        
        public void setAppliedPromotion(Promotion promotion) {
            this.appliedPromotion = promotion;
        }
        
        public double getPromotionDiscount() {
            return promotionDiscount;
        }
        
        public void setPromotionDiscount(double discount) {
            this.promotionDiscount = discount;
        }

        public double getSubtotal() {
            double baseTotal = produit.getPrix() * quantity;
            return baseTotal - (promotionDiscount * quantity);
        }
        
        public double getUnitPriceAfterPromotion() {
            return produit.getPrix() - promotionDiscount;
        }
    }
}
