import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShoppingListGUI extends JFrame {
    private JTextArea textArea;
    private JTextField inputField;
    private JTextField quantityField;
    private JTextField unitField;
    private JCheckBox integerCheckBox;
    private JComboBox<String> commandComboBox;
    private JComboBox<String> categoryComboBox;
    private JLabel statusLabel;
    private JLabel currentListLabel;
    private JTable productTable;
    private JTable categoryTable;
    private ShoppingTableModel productTableModel;
    private CategoryTableModel categoryTableModel;

    public ShoppingListGUI() {
        setTitle("Shopping List");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        currentListLabel = new JLabel("Current LIST: ");
        topPanel.add(currentListLabel);
        topPanel.add(new JScrollPane(textArea));
        mainPanel.add(topPanel, BorderLayout.CENTER);

        productTableModel = new ShoppingTableModel();
        productTable = new JTable(productTableModel);
        productTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        productTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), true));
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setBorder(BorderFactory.createTitledBorder("Products"));

        categoryTableModel = new CategoryTableModel();
        categoryTable = new JTable(categoryTableModel);
        categoryTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        categoryTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), false));
        JScrollPane categoryScrollPane = new JScrollPane(categoryTable);
        categoryScrollPane.setBorder(BorderFactory.createTitledBorder("Categories"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryScrollPane, productScrollPane);
        splitPane.setResizeWeight(0.5);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        inputField = new JTextField(10);
        quantityField = new JTextField(5);
        unitField = new JTextField(5);
        integerCheckBox = new JCheckBox("Integer");

        inputPanel.add(new JLabel("Product:"));
        inputPanel.add(inputField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Unit:"));
        inputPanel.add(unitField);
        inputPanel.add(integerCheckBox);

        String[] commands = {
                "Add Category", "Add Product", "Switch Shopping List File", "Save", "CLEAR WHOLE LIST"
        };
        commandComboBox = new JComboBox<>(commands);
        inputPanel.add(new JLabel("Command:"));
        inputPanel.add(commandComboBox);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.addActionListener(new CategoryComboBoxListener());
        inputPanel.add(new JLabel("Current Category:"));
        inputPanel.add(categoryComboBox);

        JButton executeButton = new JButton("Execute");
        executeButton.addActionListener(new ExecuteButtonListener());
        inputPanel.add(executeButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status: Ready");
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);

        loadInitialShoppingList();
    }

    private void loadInitialShoppingList() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            Main.loadShoppingList(fileChooser.getSelectedFile().getPath());
            updateTextArea();
            updateCategoryComboBox();
        }
    }

    private class ExecuteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = (String) commandComboBox.getSelectedItem();
            String input = inputField.getText();
            String quantityText = quantityField.getText();
            String unit = unitField.getText();
            boolean isInteger = integerCheckBox.isSelected();
            ShoppingList shoppingList = Main.getShoppingList();

            if (shoppingList == null) {
                statusLabel.setText("Status: No shopping list loaded.");
                return;
            }

            try {
                switch (command) {
                    case "Add Category":
                        if (input.isEmpty()) {
                            statusLabel.setText("Status: Category name cannot be empty.");
                        } else {
                            shoppingList.AddCategory(input);
                            statusLabel.setText("Status: Category added.");
                        }
                        break;
                    case "Add Product":
                        if (input.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
                            statusLabel.setText("Status: Product name, quantity, and unit cannot be empty.");
                        } else {
                            double quantity;
                            if (isInteger) {
                                quantity = Integer.parseInt(quantityText);
                                if (quantity != Math.floor(quantity)) {
                                    throw new NumberFormatException("Quantity must be an integer.");
                                }
                            } else {
                                quantity = Double.parseDouble(quantityText);
                            }
                            shoppingList.AddProduct(input, quantity, unit, isInteger);
                            statusLabel.setText("Status: Product added.");
                        }
                        break;
                    case "Switch Shopping List File":
                        JFileChooser fileChooser = new JFileChooser();
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            Main.loadShoppingList(fileChooser.getSelectedFile().getPath());
                            statusLabel.setText("Status: Shopping list switched.");
                        }
                        break;
                    case "Save":
                        shoppingList.SaveData();
                        statusLabel.setText("Status: Data saved.");
                        break;
                    case "CLEAR WHOLE LIST":
                        shoppingList.DeleteAll();
                        statusLabel.setText("Status: All categories deleted.");
                        break;
                    default:
                        statusLabel.setText("Status: Invalid command.");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Status: Invalid quantity format.");
            }

            updateTextArea();
            updateCategoryComboBox();
        }
    }

    private class CategoryComboBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            ShoppingList shoppingList = Main.getShoppingList();
            if (shoppingList != null) {
                shoppingList.SwitchCategoryByName(selectedCategory);
                statusLabel.setText("Status: Category switched to " + selectedCategory);
                updateTextArea();
            }
        }
    }

    private void updateTextArea() {
        ShoppingList shoppingList = Main.getShoppingList();
        StringBuilder builder = new StringBuilder();

        if (shoppingList != null) {
            currentListLabel.setText("Current LIST: " + Main.getCurrentFilePath());
            builder.append("Shopping List:\n");
            for (Category category : shoppingList.categories) {
                builder.append("\n- ").append(category.name).append(":\n");
                for (Product product : category.products) {
                    builder.append("  ").append(product.name).append(" (")
                            .append(product.quantity).append(" ")
                            .append(product.unit).append(", ")
                            .append(product.isInteger ? "Integer" : "Float").append(")\n");
                }
            }
        } else {
            currentListLabel.setText("Current LIST: None");
            builder.append("No shopping list loaded.");
        }

        textArea.setText(builder.toString());
        updateTables();
    }

    private void updateCategoryComboBox() {
        ShoppingList shoppingList = Main.getShoppingList();
        if (shoppingList != null) {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            categoryComboBox.removeAllItems();
            for (Category category : shoppingList.categories) {
                categoryComboBox.addItem(category.name);
            }
            if (selectedCategory != null) {
                categoryComboBox.setSelectedItem(selectedCategory);
            } else if (shoppingList.current != null) {
                categoryComboBox.setSelectedItem(shoppingList.current.name);
            }
        }
    }

    private void updateTables() {
        ShoppingList shoppingList = Main.getShoppingList();
        if (shoppingList != null) {
            productTableModel.setShoppingList(shoppingList);
            categoryTableModel.setShoppingList(shoppingList);
        }
    }

    private class ShoppingTableModel extends AbstractTableModel {
        private ShoppingList shoppingList;
        private final String[] columnNames = {"Category", "Product", "Quantity", "Unit", "Type", "Action"};

        public void setShoppingList(ShoppingList shoppingList) {
            this.shoppingList = shoppingList;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (shoppingList == null) return 0;
            int count = 0;
            for (Category category : shoppingList.categories) {
                count += category.products.size();
            }
            return count;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (shoppingList == null) return null;
            int count = 0;
            for (Category category : shoppingList.categories) {
                if (rowIndex < count + category.products.size()) {
                    Product product = category.products.get(rowIndex - count);
                    switch (columnIndex) {
                        case 0: return category.name;
                        case 1: return product.name;
                        case 2: return product.quantity;
                        case 3: return product.unit;
                        case 4: return product.isInteger ? "Integer" : "Float";
                        case 5: return "Delete";
                    }
                }
                count += category.products.size();
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5; // Only the "Action" column is editable
        }
    }

    private class CategoryTableModel extends AbstractTableModel {
        private ShoppingList shoppingList;
        private final String[] columnNames = {"Category", "Action"};

        public void setShoppingList(ShoppingList shoppingList) {
            this.shoppingList = shoppingList;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (shoppingList == null) return 0;
            return shoppingList.categories.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (shoppingList == null) return null;
            Category category = shoppingList.categories.get(rowIndex);
            switch (columnIndex) {
                case 0: return category.name;
                case 1: return "Delete";
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1; // Only the "Action" column is editable
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        private boolean isProductTable;

        public ButtonEditor(JCheckBox checkBox, boolean isProductTable) {
            super(checkBox);
            this.isProductTable = isProductTable;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                ShoppingList shoppingList = Main.getShoppingList();
                if (isProductTable) {
                    // Perform the deletion of the product
                    int count = 0;
                    for (Category category : shoppingList.categories) {
                        if (row < count + category.products.size()) {
                            category.products.remove(row - count);
                            break;
                        }
                        count += category.products.size();
                    }
                    statusLabel.setText("Status: Product deleted.");
                } else {
                    // Perform the deletion of the category
                    Category category = shoppingList.categories.get(row);
                    shoppingList.categories.remove(category);
                    statusLabel.setText("Status: Category deleted.");
                }
                updateTextArea();
                updateTables();
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
