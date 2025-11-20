# 🎨 SuperMarket UI/UX Design Guide

## Overview
This document provides a comprehensive guide to the UI/UX design patterns and implementation details of the SuperMarket Management System.

---

## 🏗️ Architecture

### Application Structure
```
Main (Application Entry)
├── BorderPane Layout
│   ├── LEFT: NavigationSidebar
│   ├── TOP: HeaderBar
│   └── CENTER: Dynamic View Container (StackPane)
│       ├── DashboardView
│       ├── ProductsView
│       ├── SalesView
│       ├── ClientsView
│       ├── ReportsView
│       └── SettingsView
```

### View Switching Mechanism
- Uses `StackPane` as the content container
- `ViewTransitions.fadeTransition()` provides smooth transitions
- Each view is a separate `VBox` with its own layout

---

## 🎨 Design System

### Color Palette

#### Primary Colors
```css
Background:     #F5F7FB  /* Very light grey */
Primary Dark:   #4E54C8  /* Indigo */
Primary Light:  #8F94FB  /* Soft blue */
```

#### Accent Colors
```css
Success:        #66BB6A  /* Green */
Success Light:  #A5D6A7
Warning:        #FFB74D  /* Orange */
Warning Light:  #FFE082
Error:          #E57373  /* Red */
Info:           #FF6E7F  /* Pink-red */
Info Light:     #BFE9FF
```

#### Text Colors
```css
Primary Text:   #222222  /* Almost black */
Secondary Text: #555555  /* Dark grey */
Tertiary Text:  #999999  /* Medium grey */
```

#### Sidebar Colors
```css
Gradient From:  #1E3C72  /* Deep blue */
Gradient To:    #2A5298  /* Blue */
```

### Typography

#### Font Hierarchy
```
App Title:       24-28pt, Bold
Section Title:   18-20pt, Bold
Subsection:      16pt, Semi-bold
Body Text:       13-14pt, Normal
Small Text:      11-12pt, Normal
```

#### Font Family
- Primary: "Segoe UI" (Windows)
- Fallback: "Helvetica", "Arial", sans-serif

### Spacing System
```
Extra Small:  5px
Small:        10px
Medium:       15px
Large:        20px
Extra Large:  25px
XXL:          30px
```

### Border Radius
```
Small:   8px   (TextFields, small buttons)
Medium:  10-12px (Standard buttons, cards)
Large:   15px  (Containers, panels)
Circle:  50%   (Avatars, circular elements)
```

### Shadows
```css
Soft Shadow:
  dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3)

Medium Shadow:
  dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5)

Strong Shadow:
  dropshadow(gaussian, rgba(0,0,0,0.2), 25, 0, 0, 8)

Colored Glow:
  dropshadow(gaussian, rgba(78,84,200,0.3), 10, 0, 0, 0)
```

---

## 🧩 Component Patterns

### 1. Gradient Buttons

```java
Button button = new Button("Text");
button.setStyle(
    "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB);" +
    "-fx-background-radius: 10;" +
    "-fx-padding: 10 20;" +
    "-fx-text-fill: white;" +
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
);
```

**Usage**: Action buttons, primary CTAs
**Variants**: 
- Success: `#66BB6A` → `#A5D6A7`
- Warning: `#FFB74D` → `#FFE082`
- Error: `#E57373` → `#EF9A9A`

### 2. Statistics Cards

```java
VBox card = new VBox(15);
card.setPadding(new Insets(25));
card.setStyle(
    "-fx-background-color: linear-gradient(to bottom right, colorFrom, colorTo);" +
    "-fx-background-radius: 15;" +
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);"
);
```

**Components**:
- Icon (emoji or FontAwesome)
- Large number (28-36pt)
- Description label

**Animation**:
- Entry: FadeTransition + ScaleTransition
- Hover: Scale up to 1.05

### 3. Form TextFields

```java
TextField field = new TextField();
field.setStyle(
    "-fx-background-color: #F5F7FB;" +
    "-fx-background-radius: 10;" +
    "-fx-padding: 10 15;" +
    "-fx-font-size: 13;"
);
```

**Focus State**: Add border and shadow
```css
-fx-border-color: #4E54C8;
-fx-border-width: 2;
-fx-effect: dropshadow(gaussian, rgba(78,84,200,0.3), 10, 0, 0, 0);
```

### 4. Data Tables

```java
TableView<T> table = new TableView<>();
table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
```

**Styling via CSS**:
- Alternating row colors (white / #FAFBFC)
- Hover effect (#F0F4FF)
- Selected state (#E3F2FD)
- Rounded container with shadow

### 5. Modal Overlays

```java
// Blur background
GaussianBlur blur = new GaussianBlur(10);
mainView.setEffect(blur);

// Dark overlay
Pane overlay = new Pane();
overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

// White panel
VBox panel = new VBox(20);
panel.setStyle(
    "-fx-background-color: white;" +
    "-fx-background-radius: 20;" +
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);"
);
```

---

## 🎬 Animation Guidelines

### Transition Durations
```
Fast:    150-200ms  (Hover effects, small changes)
Normal:  300ms      (View transitions, fades)
Slow:    500ms      (Complex animations, entries)
```

### Common Animations

#### 1. Fade Transition
```java
FadeTransition ft = new FadeTransition(Duration.millis(300), node);
ft.setFromValue(0);
ft.setToValue(1);
ft.play();
```

**Use for**: View switching, content loading

#### 2. Scale Transition (Hover)
```java
ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
st.setToX(1.05);
st.setToY(1.05);
st.play();
```

**Use for**: Button hover, card hover

#### 3. Card Entry Animation
```java
// Combine fade + scale with delay
FadeTransition ft = new FadeTransition(Duration.millis(500), card);
ft.setDelay(Duration.millis(delay));

ScaleTransition st = new ScaleTransition(Duration.millis(500), card);
st.setFromX(0.8);
st.setToX(1.0);
st.setDelay(Duration.millis(delay));
```

**Use for**: Dashboard cards, initial load

---

## 📐 Layout Patterns

### Dashboard Grid Layout
```
┌─────────────────────────────────────┐
│  [Card 1]  [Card 2]  [Card 3]  [Card 4]  │  Statistics Row
├─────────────────────────────────────┤
│  [Bar Chart]        │ [Low Stock]   │  Middle Row
├─────────────────────────────────────┤
│  [Line Chart - Full Width]          │  Bottom Row
└─────────────────────────────────────┘
```

### Products View Layout
```
┌─────────────────────────────────────┐
│  Title  [Search] [Filters] [Actions]│  Toolbar
├─────────────────────────────────────┤
│                                     │
│    Product Table (TableView)       │  Main Content
│                                     │
└─────────────────────────────────────┘
```

### Sales POS Layout
```
┌──────────────────┬──────────────┐
│  [Search Field]  │              │
├──────────────────┤  Recent      │
│                  │  Sales       │
│  Sale Items      │  History     │
│  Table           │              │
├──────────────────┤              │
│  Quick Add       │              │
├──────────────────┼──────────────┤
│  [TOTAL CARD]    │              │
├──────────────────┤              │
│  [Action Buttons]│              │
└──────────────────┴──────────────┘
```

### Clients View Layout
```
┌─────────────────────┬──────────┐
│                     │          │
│   Client Table      │  Detail  │
│   (TableView)       │  Panel   │
│                     │          │
└─────────────────────┴──────────┘
```

---

## 🎯 Interaction Patterns

### Navigation Flow
1. Click sidebar item
2. Sidebar updates active state
3. Content area fades out
4. New view fades in
5. Scroll position resets

### Form Submission Flow
1. User fills form
2. Validate on focus lost (visual only)
3. Submit button click
4. Show loading state (if needed)
5. Display success message
6. Clear form or close modal

### Table Interaction
1. Hover row → Highlight
2. Click row → Select (blue highlight)
3. Double-click → Open detail
4. Action buttons → Context-specific

---

## 🔧 Best Practices

### Performance
- ✅ Reuse view instances (don't recreate)
- ✅ Use CSS for static styles
- ✅ Limit concurrent animations
- ✅ Virtualize large tables (TableView does this)

### Accessibility
- ✅ Sufficient color contrast (meets WCAG AA)
- ✅ Clear button labels
- ✅ Tooltips for icon buttons
- ✅ Keyboard navigation support (built-in)

### Code Organization
- ✅ One view class per file
- ✅ Separate data classes (inner classes)
- ✅ Utility classes for common operations
- ✅ CSS for reusable styles

---

## 🎨 Chart Styling

### Bar Chart
```css
.chart-bar {
    -fx-bar-fill: linear-gradient(to bottom, #4E54C8, #8F94FB);
}
```

### Line Chart
```css
.chart-series-line {
    -fx-stroke: #4E54C8;
    -fx-stroke-width: 3px;
}
.chart-line-symbol {
    -fx-background-color: #4E54C8, white;
}
```

### Pie Chart
```css
.chart-pie {
    -fx-pie-color: #4E54C8;  /* Per slice */
}
```

---

## 📱 Responsive Considerations

### Minimum Size
- Width: 1200px
- Height: 700px

### Resizable Elements
- Use `HBox.setHgrow(node, Priority.ALWAYS)`
- Use `VBox.setVgrow(node, Priority.ALWAYS)`
- Tables auto-resize with `CONSTRAINED_RESIZE_POLICY`

### Fixed Elements
- Sidebar width: 250px
- Header height: 80px
- Client detail panel: 350px

---

## 🚀 Future Enhancements

### UI Improvements
- [ ] Dark mode support
- [ ] More animation variants
- [ ] Custom chart themes
- [ ] Advanced filters with animations
- [ ] Data visualization improvements

### Component Library
- [ ] Reusable button component
- [ ] Card component system
- [ ] Form validation visual feedback
- [ ] Custom date picker styling
- [ ] Loading spinners

---

## 📚 Resources

### JavaFX Documentation
- [Official JavaFX Docs](https://openjfx.io/)
- [CSS Reference Guide](https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/doc-files/cssref.html)

### Design Inspiration
- Material Design
- Fluent Design System
- Modern dashboard patterns

---

**Last Updated**: November 18, 2025
