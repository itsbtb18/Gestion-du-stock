# 🛒 SuperMarket - Gestion de Stock & Vente

A modern, professional JavaFX desktop application for supermarket stock and sales management. This is a **frontend-only** application with a focus on beautiful UI/UX design, complete with professional application flow (splash screen → login → main interface).

## ✨ Features

### 🚀 Application Flow
- **Splash Screen**: Animated loading screen with logo and progress indicator
- **Login Screen**: Mock authentication interface with styled form
- **Main Application**: Complete management system with 8 functional views

### 📊 Dashboard (Tableau de bord)
- Beautiful statistics cards with gradients and animations
- Real-time sales charts (bar chart for weekly sales)
- Low stock alerts table
- Monthly revenue line chart
- Animated card entry effects

### 📦 Products Management (Gestion des Produits)
- Complete product table with sorting and filtering
- Modern add/edit product panel with blur overlay
- Search functionality
- Import/Export buttons (UI only)
- Category and sorting filters
- 10 mock products with realistic data

### 💰 Sales / Point of Sale (Point de Vente)
- Interactive POS interface for creating sales
- Quick-add buttons for common products
- Real-time total calculation with tax and discount
- Sales history table with 10 transactions
- Payment method selection (mock)

### 👥 Clients Management (Gestion des Clients)
- Customer list with detailed information (8 mock clients)
- Interactive detail panel showing:
  - Customer avatar with initials
  - Contact information
  - Purchase statistics
  - Visit history timeline
  - Notes section

### 🚚 Suppliers Management (Gestion des Fournisseurs) **NEW**
- Supplier table with 7 mock suppliers
- Detailed supplier information panel
- Contact information display
- Products supplied list
- Internal notes section
- Category badges (color-coded)

### 📋 Stock Movement (Mouvement de Stock) **NEW**
- Complete movement history (15 records)
- Filter by date range and movement type
- Three movement types:
  - 📥 Entrée (Deliveries from suppliers)
  - 📤 Sortie (Sales transactions)
  - ⚙️ Ajustement (Inventory adjustments)
- Quick statistics display
- User tracking for each movement

### 📈 Reports & Analytics (Rapports)
- Filter options (date range, report type, format)
- Summary cards with key metrics
- Sales comparison charts
- Category distribution pie chart
- Export functionality (UI only)

### ⚙️ Settings (Paramètres)
- General settings (language, currency, timezone)
- Appearance settings (theme, font size, animations)
- Notification preferences
- System settings and backup options

## 🎨 Design Highlights

- **Color Palette**: Modern gradient backgrounds with soft colors
  - Primary: #4E54C8 → #8F94FB
  - Success: #66BB6A → #A5D6A7
  - Warning: #FFB74D → #FFE082
  - Danger: #E57373 → #EF9A9A
- **Effects**: Soft drop shadows, rounded corners (15-20px radius), glow effects
- **Animations**: 
  - Splash screen with scale/fade
  - Hover effects on buttons
  - Fade transitions between views
  - Scale animations on menu items
- **Typography**: Clean sans-serif fonts with proper hierarchy
- **Layout**: Responsive BorderPane with animated sidebar navigation
- **Dynamic Headers**: View-specific titles that update on navigation

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- JavaFX 21.0.1

### Running the Application

#### Option 1: Using Maven
```bash
mvn clean javafx:run
```

#### Option 2: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Ensure JavaFX is properly configured
3. Run the `Main.java` class
4. Application will show:
   - Splash screen (3 seconds)
   - Login screen (credentials: admin/admin123)
   - Main dashboard

#### Option 3: Compile and Run
```bash
# Compile
mvn clean compile

# Run
mvn javafx:run
```

### Building Executable JAR
```bash
mvn clean package
```

## 📁 Project Structure

```
gestion/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── supermarket/
│       │           ├── Main.java                # Application entry + flow control
│       │           ├── SplashView.java          # Loading screen
│       │           ├── LoginView.java           # Authentication screen
│       │           ├── NavigationSidebar.java   # Sidebar navigation (8 items)
│       │           ├── HeaderBar.java           # Dynamic header component
│       │           ├── DashboardView.java       # Dashboard with charts
│       │           ├── ProductsView.java        # Products management
│       │           ├── SalesView.java           # POS interface
│       │           ├── ClientsView.java         # Customer management
│       │           ├── SuppliersView.java       # Supplier management (NEW)
│       │           ├── StockMovementView.java   # Stock tracking (NEW)
│       │           ├── ReportsView.java         # Reports & analytics
│       │           ├── SettingsView.java        # Settings panel
│       │           └── ViewTransitions.java     # Animation utilities
│       └── resources/
│           └── styles.css                       # Modern UI stylesheet (412 lines)
├── pom.xml                                      # Maven configuration
├── module-info.java                             # Java module descriptor
├── README.md                                    # This file
├── COMPLETE_FEATURES.md                         # Detailed feature documentation
├── DESIGN_GUIDE.md                              # Design system guide
├── PROJECT_SUMMARY.md                           # Project overview
├── TROUBLESHOOTING.md                           # Common issues & solutions
└── INDEX.md                                     # Documentation index
```

## 🎯 Key UI Components

### Navigation Sidebar
- Gradient background (deep blue theme)
- Animated menu items with hover effects
- Active state indicator
- Logo with glow effect

### Statistics Cards
- Gradient backgrounds with different color schemes
- Icon + Value + Label layout
- Entry animations with delay
- Hover scale effect

### Tables
- Alternating row colors
- Hover highlighting
- Custom cell styling
- Rounded containers with shadows

### Forms
- Rounded text fields with soft backgrounds
- Styled combo boxes and date pickers
- Gradient action buttons
- Validation-ready (UI structure)

## 🎨 Color Scheme

- **Background**: `#F5F7FB` (Very light grey)
- **Primary Gradient**: `#4E54C8` → `#8F94FB` (Indigo to soft blue)
- **Success**: `#66BB6A` (Green)
- **Warning**: `#FFB74D` (Warm orange)
- **Error**: `#E57373` (Red)
- **Text Primary**: `#222222`
- **Text Secondary**: `#555555`

## 📊 Mock Data

All views use mock/dummy data for demonstration:
- Products: 10 sample products (Lait Candia, Huile Elio, Pain Blanc, etc.)
- Sales: 10 recent sales transactions
- Clients: 8 sample customers with detailed profiles
- Suppliers: 7 Algerian suppliers (Cevital, Candia, Bimo, Ifri, etc.)
- Stock Movements: 15 movement records (entries, exits, adjustments)
- Dashboard: Statistics and charts with realistic sample data

## ⚠️ Important Notes

- **No Database**: This is a UI-only application with no backend
- **No Business Logic**: All data is static/mock data
- **Frontend Focus**: Emphasis on beautiful, modern UI/UX design
- **JavaFX Only**: No FXML files, pure Java code
- **Login**: Demo credentials (admin/admin123) - no real authentication

## 🎓 Learning Points

This project demonstrates:
- JavaFX application flow management (splash → login → main)
- Layout management (BorderPane, VBox, HBox, StackPane, GridPane)
- CSS styling for JavaFX components
- Custom components and views
- Animation and transitions (fade, scale, slide)
- TableView with custom styling and data binding
- Charts (LineChart, BarChart, PieChart)
- Modern UI/UX patterns (gradients, shadows, rounded corners)
- View state management
- Dynamic content updates

## 🔧 Customization

To customize the application:
1. **Colors**: Modify gradients in `styles.css`
2. **Mock Data**: Update data in view class constructors
3. **Animations**: Adjust durations in `ViewTransitions.java`
4. **Splash Duration**: Change delay in `SplashView.show()` (currently 3 seconds)
5. **Menu Items**: Update `MENU_ITEMS` array in `NavigationSidebar.java`
6. **New Views**: 
   - Create new view class following existing patterns
   - Add case to `switchView()` in `Main.java`
   - Add menu item to `NavigationSidebar.java`

## 📚 Documentation

Comprehensive documentation is available:
- **README.md**: This quick-start guide
- **COMPLETE_FEATURES.md**: Detailed feature documentation (recommended!)
- **DESIGN_GUIDE.md**: Design system and UI guidelines
- **PROJECT_SUMMARY.md**: Project overview and architecture
- **TROUBLESHOOTING.md**: Common issues and solutions
- **INDEX.md**: Documentation navigation

## 📈 Statistics

- **Total Lines of Code**: ~5,250 lines
- **Java Classes**: 14 files
- **CSS Lines**: 412 lines
- **Views**: 10 (8 main + splash + login)
- **Mock Data Records**: 57 entries
- **Documentation**: ~3,500 lines

## 📝 License

This is a demonstration project for educational purposes.

## 👨‍💻 Author

Created as a modern JavaFX UI/UX demonstration for supermarket management systems.

---

**Enjoy the beautiful UI! 🎉**

**Version 2.0 - Extended with professional application flow and additional management views**
