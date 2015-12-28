package edu.uw.ling570.fstacceptor;

import java.io.BufferedReader;

public interface IFSTAcceptor {
	String fstRecognize(String inputString) throws Exception;

	String fstRecognizePatch(BufferedReader inputBufferedReader) throws Exception;
}
