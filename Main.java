import javax.swing.SwingUtilities;

public class Main {
    private static ShoppingList shoppingList;
    private static String currentFilePath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShoppingListGUI().setVisible(true));
    }

    public static void loadShoppingList(String filePath) {
        currentFilePath = filePath;
        try {
            shoppingList = new ShoppingList(currentFilePath);
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    public static ShoppingList getShoppingList() {
        return shoppingList;
    }

    public static String getCurrentFilePath() {
        return currentFilePath;
    }
}
