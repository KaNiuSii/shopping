import java.io.IOException;
import java.util.*;

public class Main {
    private static ShoppingList shoppingList;
    private static String currentFilePath;
    private static Boolean validOS;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadShoppingList(scanner);
        validOS = true;
        while (true) {
            if (validOS){
                try 
                {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("windows")) {
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    } else {
                        new ProcessBuilder("clear").inheritIO().start().waitFor();
                    }
                } catch (IOException | InterruptedException e) {
                    validOS = false;
                }
            }
            System.out.println();
            shoppingList.Print();
            System.out.println("\nAvailable commands: ");
            System.out.println("1. Add Category");
            System.out.println("2. Delete Category");
            System.out.println("3. Add Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Switch Shopping List File");
            System.out.println("6. Save and Exit\n");
            System.out.println("\u001B[1m" + "Current LIST ->" + currentFilePath + "\u001B[0m" + "\n");
            System.out.println("\u001B[1m" + "Current CATEGORY ->" + shoppingList.current.name == null ? "" : shoppingList.current.name + "\u001B[0m" + "\n");
            System.out.println("Choose an option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("Enter category name:");
                    shoppingList.AddCategory(scanner.nextLine());
                    break;
                case "2":
                    shoppingList.DeleteCategory();
                    break;
                case "3":
                    System.out.println("Enter product name:");
                    shoppingList.AddProduct(scanner.nextLine());
                    break;
                case "4":
                    System.out.println("Enter product name to delete:");
                    shoppingList.DeleteProduct(scanner.nextLine());
                    break;
                case "5":
                    shoppingList.SaveData();
                    System.out.println("Current list saved.");
                    loadShoppingList(scanner);
                    break;
                case "6":
                    shoppingList.SaveData();
                    System.out.println("Data saved. Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void loadShoppingList(Scanner scanner) {
        boolean validFile = false;
        while (!validFile) {
            System.out.println("Enter the path to the shopping list file:");
            currentFilePath = scanner.nextLine();
            try {
                shoppingList = new ShoppingList(currentFilePath);
                System.out.println("Shopping list loaded from " + "\u001B[1m" + currentFilePath + "\u001B[0m" );
                validFile = true;
            } catch (Exception e) {
                System.out.println("Error loading file: " + e.getMessage());
                System.out.println("Please enter a valid file path.");
            }
        }
        
    }

}
