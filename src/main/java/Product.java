import java.util.Date;

public class Product {

	int productId;
	String productName;
	String productCode;
	Date releaseDate;
	double price;
	String description;
	String imageUrl;

	public Product(int productId, String productName, String productCode, Date releaseDate, double price,
			String description, String imageUrl) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productCode = productCode;
		this.releaseDate = releaseDate;
		this.price = price;
		this.description = description;
		this.imageUrl = imageUrl;
	}

}
