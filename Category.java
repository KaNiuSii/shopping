import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Category {
    public String name;
    public List<Product> products;

    public Category(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public void AddProduct(Product product) {
        products.add(product);
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
}
