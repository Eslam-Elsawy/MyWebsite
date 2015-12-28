import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

@WebServlet(urlPatterns = { "/api/products", "/api/products/*" })
public class SimpleServlet extends HttpServlet {

	private static final Logger logger = LogManager.getLogger("");
	private static HashMap<Integer, Product> products = new HashMap<Integer, Product>();

	@Override
	public void init() throws ServletException {

		products.put(1,
				new Product(1, "Leaf Rake", "GDN-0011", new Date(0), 19.95, "Leaf rake with 48-inch wooden handle.",
						"http://openclipart.org/image/300px/svg_to_png/26215/Anonymous_Leaf_Rake.png"));

		products.put(2,
				new Product(2, "Garden Cart", "GDN-0023", new Date(0), 26.95, "15 gallon capacity rolling garden cart",
						"http://openclipart.org/image/300px/svg_to_png/58471/garden_cart.png"));

		products.put(3, new Product(3, "Saw", "TBX-002", new Date(0), 16.95, "15-inch steel blade hand saw",
				"http://openclipart.org/image/300px/svg_to_png/27070/egore911_saw.png"));

		products.put(4, new Product(4, "Hammer", "TBX-0048", new Date(0), 8.99, "Curved claw steel hammer",
				"http://openclipart.org/image/300px/svg_to_png/73/rejon_Hammer.png"));

		products.put(5,
				new Product(5, "Video Game Controller", "GMG-0042", new Date(0), 35.95,
						"Standard five-button video game controller",
						"http://openclipart.org/image/300px/svg_to_png/120337/xbox-controller_01.png"));

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * Helper methods req.getHeader , req.getContentType(),
		 * req.getParameter(), req.getReader res.sednRedirect("/login"),
		 * res.setContentType(""), resp.setHeader(), resp.getWriter(),
		 * resp.sendError()
		 */

		Gson gson = new Gson();
		resp.setContentType("application/json");

		int lastParameterIndex = req.getRequestURI().lastIndexOf("/");
		String idStr = req.getRequestURI().substring(lastParameterIndex + 1);
		try {
			int id = Integer.parseInt(idStr);
			resp.getWriter().printf(gson.toJson(products.get(id)));
		} catch (Exception e) {
			Product[] productsArray = new Product[products.size()];
			for (Entry<Integer, Product> entry : products.entrySet()) {
				productsArray[entry.getKey() - 1] = entry.getValue();
			}

			resp.getWriter().printf(gson.toJson(productsArray));
		}

	}
}
