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

import com.google.gson.Gson;

import edu.uw.ling570.fsaacceptor.FSAAcceptor;
import edu.uw.ling570.utils.Utils;

@WebServlet(urlPatterns = { "/api/fsaacceptor", "/api/fsaacceptor/sample_fsa", "/api/fsaacceptor/sample_input" })
public class FSAAcceptorServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger("");
	private ServletContext context;

	@Override
	public void init() throws ServletException {
		super.init();
		this.context = getServletContext();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Gson gson = new Gson();
		BufferedReader reader = req.getReader();

		FSAAcceptorInput requestData = gson.fromJson(reader, FSAAcceptorInput.class);
		FSAAcceptor fsaAcceptor = new FSAAcceptor(Utils.getBufferedReaderFromString(requestData.fsa));
		String output = "";
		try {
			output = fsaAcceptor.recognizePatch(Utils.getBufferedReaderFromString(requestData.input));
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrintWriter responseWriter = resp.getWriter();
		responseWriter.write(output);
		responseWriter.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getRequestURI().endsWith("sample_fsa")) {
			String sampleInputFilePath = this.context.getRealPath("/WEB-INF/resources/fsaacceptor/input_fsa");
			BufferedReader br = Utils.getBufferedReader(sampleInputFilePath);
			String fileContents = Utils.readBufferedReader(br);

			PrintWriter responseWriter = resp.getWriter();
			responseWriter.write(fileContents.toString());
			responseWriter.flush();
		}
		if (req.getRequestURI().endsWith("sample_input")) {
			String sampleInputFilePath = this.context.getRealPath("/WEB-INF/resources/fsaacceptor/input_testsamples");
			BufferedReader br = Utils.getBufferedReader(sampleInputFilePath);
			String fileContents = Utils.readBufferedReader(br);

			PrintWriter responseWriter = resp.getWriter();
			responseWriter.write(fileContents.toString());
			responseWriter.flush();
		}
	}

	private class FSAAcceptorInput {
		String fsa;
		String input;

		public FSAAcceptorInput(String fsa, String input) {
			this.fsa = fsa;
			this.input = input;
		}
	}

}
