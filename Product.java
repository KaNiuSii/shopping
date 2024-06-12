public class Product {
    public String name;
    public double quantity;
    public String unit;
    public boolean isInteger;

    public Product(String name) {
        this.name = name;
        this.quantity = 1;
        this.unit = "pcs";
        this.isInteger = true;
    }

    public Product(String name, double quantity, String unit, boolean isInteger) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.isInteger = isInteger;
    }
}
