import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    public ShoppingList(String filePath) {
        this.categories = new ArrayList<>();
        this.filePath = filePath;
        FetchData();
        if(this.categories.isEmpty()) {
            this.current = null;
        } 
        else {
            this.current = this.categories.get(this.categories.size() - 1);
        }
    }

    public String filePath;
    public List<Category> categories;
    public Category current;

    public void AddCategory(String name) {
        Category category = new Category(name);
        categories.add(category);
        current = category;
    }

    public void DeleteCategory() {
        if(current == null) {
            return;
        }
        categories.remove(current);
    }

    public void DeleteProduct(String name) {
        if(current.products.isEmpty()) {   
            return;
        }
        current.DeleteProduct(name);
    }

    public void DeleteAll() {
        categories.clear();
    }

    public void AddProduct(String name) {
        current.AddProduct(name);
    }

    public void FetchData() {
        categories = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Category currentCategory = null;
    
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (line.endsWith(":")) {
                        String categoryName = line.substring(0, line.length() - 1).trim();
                        currentCategory = new Category(categoryName);
                        categories.add(currentCategory);
                    } else if (currentCategory != null) {
                        currentCategory.AddProduct(line.trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void SaveData() {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            for (Category category : categories) {
                writer.println(category.name + ":");
                for (Product product : category.products) {
                    writer.println(product.name);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Print() {
        for (Category category : categories) {
            System.out.println("\n- " + "\u001B[1m" + category.name + "\u001B[0m");
            int i = 1;
            for (Product product : category.products) {
                System.out.println("  " + i + ". " + "\u001B[1m" + product.name + "\u001B[0m");
                i++;
            }
        }
    }
}
