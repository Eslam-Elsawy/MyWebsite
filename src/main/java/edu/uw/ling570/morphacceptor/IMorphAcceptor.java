package edu.uw.ling570.morphacceptor;

import java.io.BufferedReader;
import java.util.ArrayList;

public interface IMorphAcceptor {

	String fstRecognizePatch(BufferedReader inputBufferedReader) throws Exception;

	ArrayList<String> fstRecognize(String inputString) throws Exception;
}
