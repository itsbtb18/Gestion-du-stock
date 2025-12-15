package org.example.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.example.app.AppConfig;
import org.example.dao.CategorieDAO;
import org.example.model.entity.*;
import org.example.model.pattern.strategy.CardPayment;
import org.example.model.pattern.strategy.CashPayment;
import org.example.model.pattern.strategy.PaymentStrategy;
import org.example.model.service.*;
import org.example.util.CurrencyUtil;
import org.example.util.LoggerUtil;
import org.example.util.SessionManager;
import org.example.util.StoreContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CaisseController {

    private final ProduitService produitService = ProduitService.getInstance();
    private final ClientService clientService = ClientService.getInstance();
    private final VenteService venteService = VenteService.getInstance();
    private final PromotionService promotionService = PromotionService.getInstance();
    private final CategorieDAO categorieDAO = new CategorieDAO();

    @FXML private Label storeNameLabel;
    @FXML private Label storeAddressLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label cashierNameLabel;
    @FXML private Label cashierRoleLabel;
    @FXML private Button btnZReport;
    @FXML private Button btnNewTicket;
    @FXML private Button btnSignOut;

    @FXML private TextField barcodeField;
    @FXML private ToggleButton btnModeQty;
    @FXML private ToggleButton btnModePrice;
    @FXML private ToggleButton btnModeDiscount;
    @FXML private TextField keypadDisplay;
    @FXML private Button btnManualEntry;
    @FXML private Button btnHoldTicket;
    @FXML private Button btnRestoreTicket;
    @FXML private Button btnSearchProduct;
    @FXML private Button btnAssignClient;

    @FXML private Label ticketNumberLabel;
    @FXML private Label clientBadgeLabel;
    @FXML private Button btnRemoveLine;
    @FXML private Button btnClearCart;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colCode;
    @FXML private TableColumn<CartItem, String> colDesignation;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, Double> colPrixUnit;
    @FXML private TableColumn<CartItem, Double> colRemise;
    @FXML private TableColumn<CartItem, Double> colTotal;

    @FXML private Label subtotalHTLabel;
    @FXML private Label discountTotalLabel;
    @FXML private Label tvaLabel;
    @FXML private Label grandTotalLabel;
    @FXML private Label currencyLabel;

    @FXML private Button btnPayCash;
    @FXML private Button btnPayCard;
    @FXML private Button btnPayMixed;
    @FXML private Button btnCancelTicket;

    @FXML private FlowPane categoriesPane;
    @FXML private FlowPane productsPane;
    @FXML private Label productCountLabel;
    @FXML private TitledPane receiptPane;
    @FXML private TextArea receiptArea;

    @FXML private Label statusLabel;
    @FXML private Label heldTicketsLabel;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private Client currentClient;
    private String currentTicketNumber;
    private Timeline clockTimeline;
    private KeypadMode currentKeypadMode = KeypadMode.QTY;
    private StringBuilder keypadBuffer = new StringBuilder();
    private Categorie selectedCategory;
    
    private final List<HeldTicket> heldTickets = new ArrayList<>();
    private int ticketCounter = 1;

    private enum KeypadMode { QTY, PRICE, DISCOUNT }

    @FXML
    public void initialize() {
        setupHeader();
        setupClock();
        setupCartTable();
        setupKeypad();
        setupCategories();
        setupKeyboardShortcuts();
        newTicket();
        
        Platform.runLater(() -> barcodeField.requestFocus());
        
        LoggerUtil.logInfo(CaisseController.class, "POS Controller initialized");
    }

    private void setupHeader() {
        
        StoreContext ctx = StoreContext.getInstance();
        Store currentStore = ctx.getCurrentStore();
        String storeName = currentStore != null ? currentStore.getName() : "REB7A SUPERMARCHÉ";
        storeNameLabel.setText(storeName);
        storeAddressLabel.setText("Point de Vente");
        
        SessionManager session = SessionManager.getInstance();
        String userName = session.getCurrentUserDisplayName();
        cashierNameLabel.setText("Caissier: " + (userName != null ? userName : "Invité"));
        String roleName = session.getCurrentUserRoleName();
        cashierRoleLabel.setText(roleName != null ? roleName : "");
        
        currencyLabel.setText(AppConfig.CURRENCY_CODE);
    }

    private void setupClock() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime now = LocalDateTime.now();
            dateLabel.setText(now.format(dateFormatter));
            timeLabel.setText(now.format(timeFormatter));
        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
        
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText(now.format(dateFormatter));
        timeLabel.setText(now.format(timeFormatter));
    }

    private void setupCartTable() {
        colCode.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProduit().getCode()));
        colDesignation.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProduit().getNom()));
        colQty.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrixUnit.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getUnitPriceAfterPromotion()).asObject());
        colRemise.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getDiscountPercent()).asObject());
        colTotal.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        colPrixUnit.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });
        colRemise.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.0f%%", item));
            }
        });
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
                if (!empty && item != null) {
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff88;");
                }
            }
        });

        cartTable.setItems(cartItems);
        
        cartItems.addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            updateTotals();
        });
    }

    private void setupKeypad() {
        keypadDisplay.setText("0");
        btnModeQty.setSelected(true);
        currentKeypadMode = KeypadMode.QTY;
        
        ToggleGroup modeGroup = new ToggleGroup();
        btnModeQty.setToggleGroup(modeGroup);
        btnModePrice.setToggleGroup(modeGroup);
        btnModeDiscount.setToggleGroup(modeGroup);
    }

    private void setupCategories() {
        try {
            List<Categorie> categories = categorieDAO.findAll();
            categoriesPane.getChildren().clear();
            
            Button allBtn = createCategoryButton("📦 Tous", null);
            allBtn.getStyleClass().add("category-btn-selected");
            categoriesPane.getChildren().add(allBtn);
            
            for (Categorie cat : categories) {
                if (cat.isActif()) {
                    Button btn = createCategoryButton(cat.getNom(), cat);
                    categoriesPane.getChildren().add(btn);
                }
            }
            
            loadProducts(null);
            
        } catch (Exception e) {
            LoggerUtil.logError(CaisseController.class, "Error loading categories", e);
        }
    }

    private Button createCategoryButton(String name, Categorie category) {
        Button btn = new Button(name);
        btn.getStyleClass().add("category-btn");
        btn.setOnAction(e -> {
            
            categoriesPane.getChildren().forEach(node -> {
                node.getStyleClass().remove("category-btn-selected");
            });
            btn.getStyleClass().add("category-btn-selected");
            
            selectedCategory = category;
            loadProducts(category);
        });
        return btn;
    }

    private void loadProducts(Categorie category) {
        try {
            List<Produit> products;
            if (category == null) {
                products = produitService.getAllProduits();
            } else {
                final Long categoryId = category.getId();
                if (categoryId == null) {
                    products = produitService.getAllProduits();
                } else {
                    products = produitService.getAllProduits().stream()
                        .filter(p -> p.getCategorie() != null && 
                                     p.getCategorie().getId() != null &&
                                     p.getCategorie().getId().equals(categoryId))
                        .toList();
                }
            }
            
            productsPane.getChildren().clear();
            
            for (Produit p : products) {
                VBox tile = createProductTile(p);
                productsPane.getChildren().add(tile);
            }
            
            productCountLabel.setText(products.size() + " articles");
            
        } catch (Exception e) {
            LoggerUtil.logError(CaisseController.class, "Error loading products", e);
        }
    }

    private VBox createProductTile(Produit product) {
        VBox tile = new VBox(5);
        tile.getStyleClass().add("product-tile");
        tile.setAlignment(Pos.TOP_LEFT);
        
        Label nameLabel = new Label(product.getNom());
        nameLabel.getStyleClass().add("product-tile-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxHeight(35);
        
        Label priceLabel = new Label(CurrencyUtil.format(product.getPrix()));
        priceLabel.getStyleClass().add("product-tile-price");
        
        Label stockLabel = new Label("Stock: " + product.getQuantiteStock());
        stockLabel.getStyleClass().add("product-tile-stock");
        
        if (product.getQuantiteStock() <= 0) {
            stockLabel.getStyleClass().add("stock-out");
        } else if (product.getQuantiteStock() <= product.getSeuilAlerte()) {
            stockLabel.getStyleClass().add("stock-low");
        } else {
            stockLabel.getStyleClass().add("stock-ok");
        }
        
        tile.getChildren().addAll(nameLabel, priceLabel, stockLabel);
        
        tile.setOnMouseClicked(e -> addProductToCart(product, 1));
        
        return tile;
    }

    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            if (cartTable.getScene() != null) {
                cartTable.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.isConsumed()) return;
                    
                    Node focused = cartTable.getScene().getFocusOwner();
                    boolean isTextInput = focused instanceof TextField || focused instanceof TextArea;
                    
                    switch (event.getCode()) {
                        case F1 -> { handleNewTicket(); event.consume(); }
                        case F2 -> { barcodeField.requestFocus(); event.consume(); }
                        case F3 -> { handleSearchProduct(); event.consume(); }
                        case F4 -> { handleHoldTicket(); event.consume(); }
                        case F5 -> { handleRestoreTicket(); event.consume(); }
                        case DELETE -> { handleRemoveFromCart(); event.consume(); }
                        case ESCAPE -> { handleCancelSale(); event.consume(); }
                        case DIGIT0, DIGIT1, DIGIT2, DIGIT3, DIGIT4, DIGIT5, DIGIT6, DIGIT7, DIGIT8, DIGIT9,
                             NUMPAD0, NUMPAD1, NUMPAD2, NUMPAD3, NUMPAD4, NUMPAD5, NUMPAD6, NUMPAD7, NUMPAD8, NUMPAD9 -> {
                            if (!isTextInput) {
                                String digit = event.getText();
                                if (digit != null && !digit.isEmpty()) {
                                    handleNumericInput(digit);
                                    event.consume();
                                }
                            }
                        }
                        case PERIOD, DECIMAL -> {
                            if (!isTextInput) {
                                handleNumericInput(".");
                                event.consume();
                            }
                        }
                        case BACK_SPACE -> {
                            if (!isTextInput) {
                                handleKeypadBackspace();
                                event.consume();
                            }
                        }
                        case ENTER -> {
                            if (!isTextInput && !keypadBuffer.isEmpty()) {
                                handleKeypadEnter();
                                event.consume();
                            }
                        }
                        default -> {}
                    }
                });
            }
        });
    }
    
    private void handleNumericInput(String digit) {
        if (keypadBuffer.toString().equals("0") && !digit.equals(".")) {
            keypadBuffer = new StringBuilder();
        }
        if (digit.equals(".") && keypadBuffer.indexOf(".") >= 0) {
            return;
        }
        keypadBuffer.append(digit);
        keypadDisplay.setText(keypadBuffer.toString());
    }

    @FXML
    private void handleBarcodeEnter() {
        String code = barcodeField.getText().trim();
        if (code.isEmpty()) return;
        
        try {
            
            Optional<Produit> produitOpt = produitService.getProduitByCode(code);
            
            if (produitOpt.isEmpty()) {
                
                List<Produit> results = produitService.searchProduits(code);
                if (!results.isEmpty()) {
                    produitOpt = Optional.of(results.get(0));
                }
            }
            
            if (produitOpt.isPresent()) {
                Produit produit = produitOpt.get();
                int qty = 1;
                
                if (currentKeypadMode == KeypadMode.QTY && !keypadBuffer.isEmpty()) {
                    try {
                        qty = Integer.parseInt(keypadBuffer.toString());
                    } catch (NumberFormatException ignored) {}
                }
                
                addProductToCart(produit, qty);
                clearKeypad();
            } else {
                showProductNotFoundDialog(code);
            }
            
            barcodeField.clear();
            barcodeField.requestFocus();
            
        } catch (Exception e) {
            LoggerUtil.logError(CaisseController.class, "Error searching product", e);
            setStatus("Erreur de recherche", true);
        }
    }
    
    private void showProductNotFoundDialog(String code) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Produit non trouve");
        alert.setHeaderText("Le produit n'existe pas dans la base de donnees");
        alert.setContentText("Code recherche: " + code + "\n\nVoulez-vous ajouter ce produit manuellement?");
        
        ButtonType btnManual = new ButtonType("Saisie Manuelle");
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnManual, btnCancel);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnManual) {
            handleManualEntry();
        } else {
            setStatus("Produit non trouve: " + code, true);
        }
    }

    private void addProductToCart(Produit produit, int quantity) {
        
        if (produit.getQuantiteStock() < quantity) {
            showAlert("Stock insuffisant", 
                String.format("Stock disponible: %d", produit.getQuantiteStock()), 
                Alert.AlertType.WARNING);
            return;
        }
        
        Optional<CartItem> existing = cartItems.stream()
            .filter(item -> item.getProduit().getId().equals(produit.getId()))
            .findFirst();
        
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + quantity;
            
            if (newQty > produit.getQuantiteStock()) {
                showAlert("Stock insuffisant", 
                    String.format("Stock disponible: %d", produit.getQuantiteStock()), 
                    Alert.AlertType.WARNING);
                return;
            }
            
            item.setQuantity(newQty);
            cartTable.refresh();
        } else {
            CartItem newItem = new CartItem(produit, quantity);
            
            Long categoryId = produit.getCategorie() != null ? produit.getCategorie().getId() : null;
            Optional<Promotion> bestPromo = promotionService.getBestPromotion(
                produit.getId(), categoryId, produit.getPrix());
            
            if (bestPromo.isPresent()) {
                Promotion promo = bestPromo.get();
                double discount = promo.calculateDiscount(produit.getPrix());
                newItem.setAppliedPromotion(promo);
                newItem.setPromotionDiscount(discount);
            }
            
            cartItems.add(newItem);
        }
        
        updateTotals();
        setStatus("✅ " + produit.getNom() + " ajouté", false);
    }

    @FXML
    private void handleRemoveFromCart() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
            setStatus("🗑️ Article retiré", false);
        } else {
            setStatus("⚠️ Sélectionnez un article", true);
        }
    }

    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Vider le panier");
        confirm.setHeaderText("Confirmer la suppression");
        confirm.setContentText("Voulez-vous vraiment vider tout le panier?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cartItems.clear();
            currentClient = null;
            updateClientDisplay();
            updateTotals();
            setStatus("🗑️ Panier vidé", false);
        }
    }

    @FXML
    private void handleKeypad(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String digit = btn.getText();
        
        if (keypadBuffer.toString().equals("0") && !digit.equals(".")) {
            keypadBuffer = new StringBuilder();
        }
        
        if (digit.equals(".") && keypadBuffer.indexOf(".") >= 0) {
            return;
        }
        
        keypadBuffer.append(digit);
        keypadDisplay.setText(keypadBuffer.toString());
    }

    @FXML
    private void handleKeypadClear() {
        clearKeypad();
    }

    @FXML
    private void handleKeypadBackspace() {
        if (keypadBuffer.length() > 0) {
            keypadBuffer.deleteCharAt(keypadBuffer.length() - 1);
        }
        keypadDisplay.setText(keypadBuffer.length() == 0 ? "0" : keypadBuffer.toString());
    }

    @FXML
    private void handleKeypadEnter() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("⚠️ Sélectionnez un article", true);
            return;
        }
        
        try {
            double value = Double.parseDouble(keypadBuffer.toString());
            
            switch (currentKeypadMode) {
                case QTY -> {
                    int qty = (int) value;
                    if (qty > selected.getProduit().getQuantiteStock()) {
                        showAlert("Stock insuffisant", 
                            "Stock disponible: " + selected.getProduit().getQuantiteStock(), 
                            Alert.AlertType.WARNING);
                        return;
                    }
                    selected.setQuantity(qty);
                    setStatus("✅ Quantité modifiée: " + qty, false);
                }
                case PRICE -> {
                    selected.setCustomPrice(value);
                    setStatus("✅ Prix modifié: " + CurrencyUtil.format(value), false);
                }
                case DISCOUNT -> {
                    if (value > 100) value = 100;
                    selected.setDiscountPercent(value);
                    setStatus("✅ Remise appliquée: " + value + "%", false);
                }
            }
            
            cartTable.refresh();
            updateTotals();
            clearKeypad();
            
        } catch (NumberFormatException e) {
            setStatus("⚠️ Valeur invalide", true);
        }
    }

    @FXML
    private void handleModeQty() {
        currentKeypadMode = KeypadMode.QTY;
        clearKeypad();
    }

    @FXML
    private void handleModePrice() {
        currentKeypadMode = KeypadMode.PRICE;
        clearKeypad();
    }

    @FXML
    private void handleModeDiscount() {
        currentKeypadMode = KeypadMode.DISCOUNT;
        clearKeypad();
    }

    private void clearKeypad() {
        keypadBuffer = new StringBuilder();
        keypadDisplay.setText("0");
    }

    @FXML
    private void handleNewTicket() {
        if (!cartItems.isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Nouveau ticket");
            confirm.setHeaderText("Un ticket est en cours");
            confirm.setContentText("Voulez-vous mettre le ticket actuel en attente?");
            
            ButtonType holdBtn = new ButtonType("Mettre en attente");
            ButtonType discardBtn = new ButtonType("Abandonner");
            ButtonType cancelBtn = ButtonType.CANCEL;
            
            confirm.getButtonTypes().setAll(holdBtn, discardBtn, cancelBtn);
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent()) {
                if (result.get() == holdBtn) {
                    holdCurrentTicket();
                } else if (result.get() != discardBtn) {
                    return;
                }
            } else {
                return;
            }
        }
        
        newTicket();
    }

    private void newTicket() {
        cartItems.clear();
        currentClient = null;
        currentTicketNumber = String.format("TKT-%03d", ticketCounter++);
        ticketNumberLabel.setText("#" + currentTicketNumber);
        updateClientDisplay();
        updateTotals();
        receiptArea.clear();
        barcodeField.requestFocus();
        setStatus("🎫 Nouveau ticket créé", false);
    }

    @FXML
    private void handleHoldTicket() {
        if (cartItems.isEmpty()) {
            setStatus("⚠️ Panier vide", true);
            return;
        }
        
        holdCurrentTicket();
        newTicket();
    }

    private void holdCurrentTicket() {
        HeldTicket held = new HeldTicket();
        held.ticketNumber = currentTicketNumber;
        held.items = new ArrayList<>(cartItems);
        held.client = currentClient;
        held.heldAt = LocalDateTime.now();
        held.cashierName = cashierNameLabel.getText();
        
        heldTickets.add(held);
        updateHeldCount();
        setStatus("⏸️ Ticket " + currentTicketNumber + " mis en attente", false);
    }

    @FXML
    private void handleRestoreTicket() {
        if (heldTickets.isEmpty()) {
            setStatus("⚠️ Aucun ticket en attente", true);
            return;
        }
        
        HeldTicket toRestore;
        if (heldTickets.size() == 1) {
            toRestore = heldTickets.get(0);
        } else {
            toRestore = showHeldTicketSelectionDialog();
            if (toRestore == null) return;
        }
        
        if (!cartItems.isEmpty()) {
            holdCurrentTicket();
        }
        
        cartItems.clear();
        cartItems.addAll(toRestore.items);
        currentClient = toRestore.client;
        currentTicketNumber = toRestore.ticketNumber;
        ticketNumberLabel.setText("#" + currentTicketNumber);
        
        heldTickets.remove(toRestore);
        updateHeldCount();
        updateClientDisplay();
        updateTotals();
        
        setStatus("▶️ Ticket " + currentTicketNumber + " restauré", false);
    }

    private HeldTicket showHeldTicketSelectionDialog() {
        ChoiceDialog<HeldTicket> dialog = new ChoiceDialog<>(heldTickets.get(0), heldTickets);
        dialog.setTitle("Reprendre un ticket");
        dialog.setHeaderText("Sélectionnez un ticket");
        dialog.setContentText("Ticket:");
        
        return dialog.showAndWait().orElse(null);
    }

    private void updateHeldCount() {
        heldTicketsLabel.setText("🎫 " + heldTickets.size() + " ticket(s) en attente");
    }

    @FXML
    private void handlePayCash() {
        processPayment("Espèces");
    }

    @FXML
    private void handlePayCard() {
        processPayment("Carte Bancaire");
    }

    @FXML
    private void handlePayMixed() {
        showMixedPaymentDialog();
    }

    private void processPayment(String paymentMethod) {
        if (cartItems.isEmpty()) {
            setStatus("⚠️ Panier vide", true);
            return;
        }
        
        double total = calculateGrandTotal();
        
        if ("Espèces".equals(paymentMethod)) {
            showCashPaymentDialog(total);
        } else {
            completeSale(paymentMethod, total, 0);
        }
    }

    private void showCashPaymentDialog(double total) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Paiement Espèces");
        dialog.setHeaderText(String.format("Total à payer: %s", CurrencyUtil.format(total)));
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");
        
        Label totalLabel = new Label("TOTAL: " + CurrencyUtil.format(total));
        totalLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Montant reçu");
        amountField.setStyle("-fx-font-size: 20px; -fx-pref-height: 50px;");
        
        Label changeLabel = new Label("Rendu: 0.00 " + AppConfig.CURRENCY_CODE);
        changeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #f39c12;");
        
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double received = Double.parseDouble(newVal);
                double change = received - total;
                changeLabel.setText(String.format("Rendu: %s", CurrencyUtil.format(Math.max(0, change))));
                changeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + 
                    (change >= 0 ? "#00ff88" : "#ff4757") + ";");
            } catch (NumberFormatException e) {
                changeLabel.setText("Rendu: ---");
            }
        });
        
        HBox quickAmounts = new HBox(10);
        quickAmounts.setAlignment(Pos.CENTER);
        for (int amount : new int[]{500, 1000, 2000, 5000}) {
            Button btn = new Button(amount + "");
            btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-pref-width: 70;");
            btn.setOnAction(e -> amountField.setText(String.valueOf(amount)));
            quickAmounts.getChildren().add(btn);
        }
        
        Button exactBtn = new Button("Montant exact");
        exactBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        exactBtn.setOnAction(e -> amountField.setText(String.format("%.2f", total)));
        quickAmounts.getChildren().add(exactBtn);
        
        content.getChildren().addAll(totalLabel, amountField, quickAmounts, changeLabel);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return Double.parseDouble(amountField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        Optional<Double> result = dialog.showAndWait();
        if (result.isPresent() && result.get() >= total) {
            double change = result.get() - total;
            completeSale("Espèces", result.get(), change);
        } else if (result.isPresent()) {
            showAlert("Montant insuffisant", "Le montant reçu doit être supérieur ou égal au total.", Alert.AlertType.WARNING);
        }
    }

    private void showMixedPaymentDialog() {
        double total = calculateGrandTotal();
        
        Dialog<double[]> dialog = new Dialog<>();
        dialog.setTitle("Paiement Mixte");
        dialog.setHeaderText(String.format("Total: %s", CurrencyUtil.format(total)));
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        TextField cashField = new TextField("0");
        cashField.setPromptText("Espèces");
        
        TextField cardField = new TextField("0");
        cardField.setPromptText("Carte");
        
        Label remainingLabel = new Label("Reste: " + CurrencyUtil.format(total));
        
        Runnable updateRemaining = () -> {
            try {
                double cash = Double.parseDouble(cashField.getText());
                double card = Double.parseDouble(cardField.getText());
                double remaining = total - cash - card;
                remainingLabel.setText("Reste: " + CurrencyUtil.format(Math.max(0, remaining)));
                remainingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + 
                    (remaining <= 0 ? "#27ae60" : "#e74c3c") + ";");
            } catch (NumberFormatException e) {
                remainingLabel.setText("Reste: ---");
            }
        };
        
        cashField.textProperty().addListener((obs, o, n) -> updateRemaining.run());
        cardField.textProperty().addListener((obs, o, n) -> updateRemaining.run());
        
        content.getChildren().addAll(
            new Label("Espèces:"), cashField,
            new Label("Carte:"), cardField,
            remainingLabel
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new double[] {
                        Double.parseDouble(cashField.getText()),
                        Double.parseDouble(cardField.getText())
                    };
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        Optional<double[]> result = dialog.showAndWait();
        if (result.isPresent()) {
            double[] amounts = result.get();
            if (amounts[0] + amounts[1] >= total) {
                completeSale("Mixte (Espèces + Carte)", amounts[0] + amounts[1], 0);
            } else {
                showAlert("Montant insuffisant", "La somme des paiements doit être >= au total.", Alert.AlertType.WARNING);
            }
        }
    }

    private void completeSale(String paymentMethod, double amountPaid, double change) {
        try {
            
            Vente vente = new Vente();
            vente.setClient(currentClient);
            vente.setVendeurId(SessionManager.getInstance().getCurrentUserId());
            vente.setDateVente(LocalDateTime.now());
            vente.setModePaiement(paymentMethod);
            
            List<LigneVente> lignes = new ArrayList<>();
            double subtotal = 0;
            
            for (CartItem item : cartItems) {
                LigneVente ligne = new LigneVente();
                ligne.setProduit(item.getProduit());
                ligne.setQuantite(item.getQuantity());
                ligne.setPrixUnitaire(item.getUnitPriceAfterPromotion());
                ligne.setRemise(item.getDiscountPercent());
                ligne.setSousTotal(item.getSubtotal());
                lignes.add(ligne);
                subtotal += item.getSubtotal();
            }
            
            vente.setLignes(lignes);
            
            double vatRate = StoreContext.getInstance().getVatRate();
            double tva = subtotal * vatRate;
            
            vente.setMontantTotal(subtotal);
            vente.setMontantRemise(0); 
            vente.setMontantTVA(tva);
            vente.setMontantFinal(subtotal + tva);
            
            PaymentStrategy strategy = "Carte Bancaire".equals(paymentMethod) ?
                new CardPayment("0000", "Client", "12/25") :
                new CashPayment(amountPaid);
            
            Vente savedVente = venteService.processSale(vente, strategy);
            
            String receipt = generateReceipt(savedVente, change);
            receiptArea.setText(receipt);
            receiptPane.setExpanded(true);
            
            String message = String.format("Vente validée!\nTotal: %s", CurrencyUtil.format(savedVente.getMontantFinal()));
            if (change > 0) {
                message += String.format("\nRendu: %s", CurrencyUtil.format(change));
            }
            
            showAlert("Succès", message, Alert.AlertType.INFORMATION);
            
            newTicket();
            setStatus("✅ Vente #" + savedVente.getNumero() + " enregistrée", false);
            
        } catch (Exception e) {
            LoggerUtil.logError(CaisseController.class, "Error completing sale", e);
            showAlert("Erreur", "Erreur lors de la validation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelSale() {
        if (cartItems.isEmpty()) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Annuler la vente");
        confirm.setHeaderText("Confirmer l'annulation");
        confirm.setContentText("Voulez-vous vraiment annuler cette vente?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            newTicket();
            setStatus("❌ Vente annulée", false);
        }
    }

    @FXML
    private void handleAssignClient() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Assigner un client");
        dialog.setHeaderText("Rechercher un client");
        dialog.setContentText("Code, téléphone ou email:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                List<Client> clients = clientService.searchClients(result.get().trim());
                
                if (clients.isEmpty()) {
                    showAlert("Client non trouvé", "Aucun client trouvé avec ces critères.", Alert.AlertType.WARNING);
                } else if (clients.size() == 1) {
                    currentClient = clients.get(0);
                    updateClientDisplay();
                    setStatus("👤 Client: " + currentClient.getNom() + " " + currentClient.getPrenom(), false);
                } else {
                    ChoiceDialog<Client> choiceDialog = new ChoiceDialog<>(clients.get(0), clients);
                    choiceDialog.setTitle("Sélection client");
                    choiceDialog.setHeaderText("Plusieurs clients trouvés");
                    choiceDialog.setContentText("Choisissez:");
                    
                    choiceDialog.showAndWait().ifPresent(c -> {
                        currentClient = c;
                        updateClientDisplay();
                    });
                }
            } catch (Exception e) {
                LoggerUtil.logError(CaisseController.class, "Error searching client", e);
            }
        }
    }

    private void updateClientDisplay() {
        if (currentClient != null) {
            clientBadgeLabel.setText("👤 " + currentClient.getNom() + " " + currentClient.getPrenom() + 
                " | " + currentClient.getPointsFidelite() + " pts");
        } else {
            clientBadgeLabel.setText("Client: Passage");
        }
    }

    @FXML
    private void handleManualEntry() {
        Dialog<ManualProduct> dialog = new Dialog<>();
        dialog.setTitle("Saisie Manuelle");
        dialog.setHeaderText("Ajouter un article manuellement\n(Scanner non fonctionnel)");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");
        
        Label nameLabel = new Label("Désignation du produit:");
        nameLabel.setStyle("-fx-text-fill: #ffffff;");
        TextField nameField = new TextField();
        nameField.setPromptText("Ex: Produit divers");
        nameField.setStyle("-fx-font-size: 14px;");
        
        Label priceLabel = new Label("Prix unitaire (DZD):");
        priceLabel.setStyle("-fx-text-fill: #ffffff;");
        TextField priceField = new TextField();
        priceField.setPromptText("Ex: 150.00");
        priceField.setStyle("-fx-font-size: 14px;");
        
        Label qtyLabel = new Label("Quantité:");
        qtyLabel.setStyle("-fx-text-fill: #ffffff;");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 999, 1);
        qtySpinner.setEditable(true);
        qtySpinner.setStyle("-fx-font-size: 14px;");
        
        Label catLabel = new Label("Catégorie (optionnel):");
        catLabel.setStyle("-fx-text-fill: #ffffff;");
        ComboBox<String> catCombo = new ComboBox<>();
        catCombo.getItems().add("-- Aucune --");
        catCombo.getItems().add("Épicerie");
        catCombo.getItems().add("Boissons");
        catCombo.getItems().add("Produits Laitiers");
        catCombo.getItems().add("Fruits & Légumes");
        catCombo.getItems().add("Hygiène & Beauté");
        catCombo.getItems().add("Entretien");
        catCombo.getItems().add("Divers");
        catCombo.setValue("-- Aucune --");
        catCombo.setMaxWidth(Double.MAX_VALUE);
        
        Label quickPriceLabel = new Label("Prix rapides:");
        quickPriceLabel.setStyle("-fx-text-fill: #6c7a89;");
        HBox quickPrices = new HBox(8);
        for (int price : new int[]{50, 100, 150, 200, 300, 500, 1000}) {
            Button btn = new Button(price + " DA");
            btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px;");
            btn.setOnAction(e -> priceField.setText(String.valueOf(price)));
            quickPrices.getChildren().add(btn);
        }
        
        content.getChildren().addAll(
            nameLabel, nameField,
            priceLabel, priceField,
            quickPriceLabel, quickPrices,
            qtyLabel, qtySpinner,
            catLabel, catCombo
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: #1a1a2e;");
        
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        
        Runnable validateFields = () -> {
            boolean valid = !nameField.getText().trim().isEmpty();
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                valid = valid && price > 0;
            } catch (NumberFormatException e) {
                valid = false;
            }
            okButton.setDisable(!valid);
        };
        
        nameField.textProperty().addListener((obs, o, n) -> validateFields.run());
        priceField.textProperty().addListener((obs, o, n) -> validateFields.run());
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    ManualProduct mp = new ManualProduct();
                    mp.name = nameField.getText().trim();
                    mp.price = Double.parseDouble(priceField.getText().trim());
                    mp.quantity = qtySpinner.getValue();
                    mp.category = catCombo.getValue();
                    return mp;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });
        
        Optional<ManualProduct> result = dialog.showAndWait();
        if (result.isPresent()) {
            ManualProduct mp = result.get();
            
            Produit tempProduct = new Produit();
            tempProduct.setId(-System.currentTimeMillis()); 
            tempProduct.setCode("MANUAL-" + System.currentTimeMillis());
            tempProduct.setNom(mp.name);
            tempProduct.setPrix(mp.price);
            tempProduct.setQuantiteStock(9999); 
            tempProduct.setActif(true);
            
            addProductToCart(tempProduct, mp.quantity);
            setStatus("✅ Article manuel ajouté: " + mp.name, false);
        }
    }
    
    private static class ManualProduct {
        String name;
        double price;
        int quantity;
        String category;
    }

    @FXML
    private void handleSearchProduct() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rechercher un article");
        dialog.setHeaderText("Recherche par nom ou code");
        dialog.setContentText("Recherche:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            List<Produit> results = produitService.searchProduits(result.get().trim());
            
            if (results.isEmpty()) {
                showAlert("Aucun résultat", "Aucun produit trouvé.", Alert.AlertType.INFORMATION);
            } else if (results.size() == 1) {
                addProductToCart(results.get(0), 1);
            } else {
                ChoiceDialog<Produit> choiceDialog = new ChoiceDialog<>(results.get(0), results);
                choiceDialog.setTitle("Sélection produit");
                choiceDialog.setHeaderText(results.size() + " produits trouvés");
                choiceDialog.setContentText("Choisissez:");
                
                choiceDialog.showAndWait().ifPresent(p -> addProductToCart(p, 1));
            }
        }
    }

    @FXML
    private void handleZReport() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Rapport Z - Fermeture de Caisse");
        confirmAlert.setHeaderText("Générer le rapport Z du jour?");
        confirmAlert.setContentText("Ce rapport résume toutes les ventes de la journée.\nVoulez-vous continuer?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DailyClosingService closingService = DailyClosingService.getInstance();
                DailyClosingService.ZReport report = closingService.generateZReport(LocalDate.now());
                
                if (report.hasError()) {
                    showAlert("Erreur", report.getError(), Alert.AlertType.ERROR);
                    return;
                }
                
                String reportText = closingService.generateZReportText(report);
                
                receiptArea.setText(reportText);
                receiptPane.setExpanded(true);
                
                Alert summaryAlert = new Alert(Alert.AlertType.INFORMATION);
                summaryAlert.setTitle("Rapport Z Généré");
                summaryAlert.setHeaderText("Résumé de la journée");
                summaryAlert.setContentText(String.format(
                    "Transactions: %d\nVentes nettes: %s\nTicket moyen: %s",
                    report.getTotalTransactions(),
                    CurrencyUtil.format(report.getNetSales()),
                    CurrencyUtil.format(report.getAverageTicket())
                ));
                summaryAlert.showAndWait();
                
                setStatus("📊 Rapport Z généré", false);
            } catch (Exception e) {
                LoggerUtil.logError(CaisseController.class, "Error generating Z-Report", e);
                showAlert("Erreur", "Erreur lors de la génération du rapport: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleSignOut() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Confirmer la déconnexion");
        confirm.setContentText("Voulez-vous vraiment vous déconnecter?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            SessionManager.getInstance().endSession();
            
            setStatus("🚪 Déconnecté", false);
        }
    }

    private void updateTotals() {
        double subtotalHT = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double vatRate = StoreContext.getInstance().getVatRate();
        double tva = subtotalHT * vatRate;
        double total = subtotalHT + tva;
        
        subtotalHTLabel.setText(String.format("%.2f", subtotalHT));
        discountTotalLabel.setText("-0.00"); 
        tvaLabel.setText(String.format("%.2f", tva));
        grandTotalLabel.setText(String.format("%.2f", total));
    }

    private double calculateGrandTotal() {
        double subtotalHT = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double vatRate = StoreContext.getInstance().getVatRate();
        return subtotalHT * (1 + vatRate);
    }

    private String generateReceipt(Vente vente, double change) {
        StringBuilder sb = new StringBuilder();
        String line = "═".repeat(40);
        String thin = "─".repeat(40);
        
        sb.append(line).append("\n");
        sb.append(centerText(storeNameLabel.getText(), 40)).append("\n");
        sb.append(centerText("Ticket de Caisse", 40)).append("\n");
        sb.append(line).append("\n\n");
        
        sb.append(String.format("Date: %s\n", LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        sb.append(String.format("Ticket: %s\n", vente.getNumero()));
        sb.append(String.format("Caissier: %s\n", cashierNameLabel.getText()));
        
        if (currentClient != null) {
            sb.append(String.format("Client: %s %s\n", currentClient.getNom(), currentClient.getPrenom()));
        }
        
        sb.append("\n").append(thin).append("\n");
        sb.append("Articles:\n").append(thin).append("\n");
        
        for (CartItem item : cartItems) {
            String name = item.getProduit().getNom();
            if (name.length() > 20) name = name.substring(0, 17) + "...";
            sb.append(String.format("%-20s x%d\n", name, item.getQuantity()));
            sb.append(String.format("  %8.2f x %d = %10.2f\n",
                item.getUnitPriceAfterPromotion(),
                item.getQuantity(),
                item.getSubtotal()));
        }
        
        sb.append(thin).append("\n");
        sb.append(String.format("%-20s %18.2f\n", "Sous-total:", vente.getMontantTotal()));
        sb.append(String.format("%-20s %18.2f\n", "TVA:", vente.getMontantTVA()));
        sb.append(line).append("\n");
        sb.append(String.format("%-20s %14.2f %s\n", "TOTAL:", vente.getMontantFinal(), AppConfig.CURRENCY_CODE));
        sb.append(line).append("\n\n");
        
        sb.append(String.format("Paiement: %s\n", vente.getModePaiement()));
        if (change > 0) {
            sb.append(String.format("Rendu: %.2f %s\n", change, AppConfig.CURRENCY_CODE));
        }
        
        sb.append("\n").append(centerText("Merci de votre visite!", 40)).append("\n");
        sb.append(line).append("\n");
        
        return sb.toString();
    }

    private String centerText(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }

    private void setStatus(String message, boolean isWarning) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isWarning ? "#f39c12" : "#00ff88") + ";");
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class CartItem {
        private Produit produit;
        private int quantity;
        private Promotion appliedPromotion;
        private double promotionDiscount;
        private Double customPrice;
        private double discountPercent;

        public CartItem(Produit produit, int quantity) {
            this.produit = produit;
            this.quantity = quantity;
            this.discountPercent = 0;
        }

        public Produit getProduit() { return produit; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        public Promotion getAppliedPromotion() { return appliedPromotion; }
        public void setAppliedPromotion(Promotion promo) { this.appliedPromotion = promo; }
        
        public double getPromotionDiscount() { return promotionDiscount; }
        public void setPromotionDiscount(double discount) { this.promotionDiscount = discount; }
        
        public Double getCustomPrice() { return customPrice; }
        public void setCustomPrice(Double price) { this.customPrice = price; }
        
        public double getDiscountPercent() { return discountPercent; }
        public void setDiscountPercent(double percent) { this.discountPercent = percent; }

        public double getUnitPriceAfterPromotion() {
            if (customPrice != null) return customPrice;
            return produit.getPrix() - promotionDiscount;
        }

        public double getSubtotal() {
            double unitPrice = getUnitPriceAfterPromotion();
            double lineTotal = unitPrice * quantity;
            double discountAmount = lineTotal * (discountPercent / 100.0);
            return lineTotal - discountAmount;
        }
        
        @Override
        public String toString() {
            return produit.getNom() + " x" + quantity;
        }
    }

    private static class HeldTicket {
        String ticketNumber;
        List<CartItem> items;
        Client client;
        LocalDateTime heldAt;
        String cashierName;
        
        @Override
        public String toString() {
            return ticketNumber + " - " + items.size() + " articles - " + 
                   heldAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}
