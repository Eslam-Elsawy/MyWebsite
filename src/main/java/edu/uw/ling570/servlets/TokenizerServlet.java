package edu.uw.ling570.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.uw.ling570.tokenizer.ITokenizer;
import edu.uw.ling570.tokenizer.Tokenizer;
import edu.uw.ling570.utils.Utils;

@WebServlet(urlPatterns = { "/api/tokenizer", "/api/tokenizer/sample" })
public class TokenizerServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger("");
	private ITokenizer tokenizer;
	private ServletContext context;

	@Override
	public void init() throws ServletException {
		super.init();
		this.context = getServletContext();
		String abbreviationsFilePath = this.context.getRealPath("/WEB-INF/resources/tokenizer/abbreviations.txt");
		tokenizer = new Tokenizer(abbreviationsFilePath);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuilder output = new StringBuilder();
		BufferedReader reader = req.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			output.append(tokenizer.tokenizeLine(line) + "\n");
		}

		PrintWriter responseWriter = resp.getWriter();
		responseWriter.write(output.toString());
		responseWriter.flush();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(req.getRequestURI().endsWith("sample")){
			String sampleInputFilePath = this.context.getRealPath("/WEB-INF/resources/tokenizer/sample_input.txt");
			BufferedReader br = Utils.getBufferedReader(sampleInputFilePath);
			String fileContents = Utils.readBufferedReader(br);
			
			PrintWriter responseWriter = resp.getWriter();
			responseWriter.write(fileContents.toString());
			responseWriter.flush();
		}
	}

}
