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

import edu.uw.ling570.morphacceptor.FSMExpander;
import edu.uw.ling570.morphacceptor.IFSMExpander;
import edu.uw.ling570.morphacceptor.IMorphAcceptor;
import edu.uw.ling570.morphacceptor.MorphAcceptor;
import edu.uw.ling570.utils.Utils;

@WebServlet(urlPatterns = { "/api/morphacceptor", "/api/morphacceptor/sample_lexicon",
		"/api/morphacceptor/sample_morphotactics", "/api/morphacceptor/sample_input" })
public class MorphAcceptorServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger("");
	private ServletContext context;

	@Override
	public void init() throws ServletException {
		super.init();
		this.context = getServletContext();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Gson gson = new Gson();

			// request
			BufferedReader reader = req.getReader();
			MorphAcceptorInput requestData = gson.fromJson(reader, MorphAcceptorInput.class);

			// Step 1: Expand
			BufferedReader lexiconBufferedReader = Utils.getBufferedReaderFromString(requestData.lexicon);
			BufferedReader morphotacticsBufferedReader = Utils.getBufferedReaderFromString(requestData.morphotactics);
			IFSMExpander fsmExpander = new FSMExpander(lexiconBufferedReader, morphotacticsBufferedReader);
			String expandedFSM = fsmExpander.expand();

			// Step 2: Recognize
			BufferedReader expandedFSMBufferedReader = Utils.getBufferedReaderFromString(expandedFSM);
			BufferedReader inputBufferedReader = Utils.getBufferedReaderFromString(requestData.input);
			IMorphAcceptor morphAcceptor = new MorphAcceptor(expandedFSMBufferedReader);
			String output;
			output = morphAcceptor.fstRecognizePatch(inputBufferedReader);

			// response
			MorphAcceptorOutput morhAcceptorOutput = new MorphAcceptorOutput(expandedFSM, output);
			PrintWriter responseWriter = resp.getWriter();
			responseWriter.write(gson.toJson(morhAcceptorOutput));
			responseWriter.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sampleFilePath = "";
		if (req.getRequestURI().endsWith("sample_lexicon"))
			sampleFilePath = this.context.getRealPath("/WEB-INF/resources/morphacceptor/input_lexicon.txt");
		else if (req.getRequestURI().endsWith("sample_morphotactics"))
			sampleFilePath = this.context.getRealPath("/WEB-INF/resources/morphacceptor/input_morphotactics.txt");
		else if (req.getRequestURI().endsWith("sample_input"))
			sampleFilePath = this.context.getRealPath("/WEB-INF/resources/morphacceptor/input_testsamples.txt");
		else
			return;

		BufferedReader br = Utils.getBufferedReader(sampleFilePath);
		String fileContents = Utils.readBufferedReader(br);

		PrintWriter responseWriter = resp.getWriter();
		responseWriter.write(fileContents.toString());
		responseWriter.flush();
	}

	private class MorphAcceptorInput {
		String lexicon;
		String morphotactics;
		String input;

		public MorphAcceptorInput(String lexiocn, String morphotactics, String input) {
			this.lexicon = lexicon;
			this.morphotactics = morphotactics;
			this.input = input;
		}
	}

	private class MorphAcceptorOutput {
		String expandedMorphotactics;
		String output;

		public MorphAcceptorOutput(String expandedMorphotactics, String output) {
			this.expandedMorphotactics = expandedMorphotactics;
			this.output = output;
		}
	}

}
