import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    public List<Category> categories;
    public Category current;
    private String filePath;

    public ShoppingList(String filePath) {
        this.filePath = filePath;
        this.categories = new ArrayList<>();
        load();
    }

    public void AddCategory(String name) {
        Category category = new Category(name);
        categories.add(category);
        current = category;
    }

    public void DeleteCategory() {
        if (current != null) {
            categories.remove(current);
            current = categories.isEmpty() ? null : categories.get(0);
        }
    }

    public void AddProduct(String name, double quantity, String unit, boolean isInteger) {
        if (current != null) {
            current.AddProduct(new Product(name, quantity, unit, isInteger));
        }
    }

    public void DeleteProduct(String name) {
        if (current != null) {
            current.DeleteProduct(name);
        }
    }

    public void DeleteAll() {
        categories.clear();
        current = null;
    }

    public void SwitchCategoryByName(String name) {
        for (Category category : categories) {
            if (category.name.equals(name)) {
                current = category;
                break;
            }
        }
    }

    public void SaveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Category category : categories) {
                writer.println(category.name + ":");
                for (Product product : category.products) {
                    writer.println(product.name + ";" + product.quantity + ";" + product.unit + ";" + product.isInteger);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Category category = null;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(":")) {
                    category = new Category(line.substring(0, line.length() - 1));
                    categories.add(category);
                } else if (category != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 1) {
                        // Handle old format: Product name only
                        category.AddProduct(new Product(parts[0]));
                    } else {
                        // Handle new format
                        String name = parts[0];
                        double quantity = Double.parseDouble(parts[1]);
                        String unit = parts[2];
                        boolean isInteger = Boolean.parseBoolean(parts[3]);
                        category.AddProduct(new Product(name, quantity, unit, isInteger));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
