package edu.uw.ling570.fsaacceptor;

import java.io.BufferedReader;

public interface IFSAAcceptor {
	boolean recognize(String inputString) throws Exception; 
	String recognizePatch(BufferedReader inputBufferedReader) throws Exception ; 
}
