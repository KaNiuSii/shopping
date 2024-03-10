import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Category {

    public Category(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }
    
    public void AddProduct(String name) {
        products.add(new Product(name));
    }

    public void DeleteProduct(String name) {
        Iterator<Product> iterator = products.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.name.equals(name)) { 
                iterator.remove();
                break;
            }
        }
    }
    
    public List<Product> products;
    public String name;
}
