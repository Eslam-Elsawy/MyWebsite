package edu.uw.ling570.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import sun.net.NetworkServer;

public class Utils {

	public static BufferedReader getBufferedReader(String filePath) {
		BufferedReader br = null;
		try { // try absolute path
			br = new BufferedReader(new FileReader(filePath));
			return br;
		} catch (Exception e1) {
			try { // try relative path
				String absoluteFilePath = new File("").getAbsolutePath();
				filePath = absoluteFilePath + "/" + filePath;
				br = new BufferedReader(new FileReader(filePath));
				return br;
			} catch (Exception e2) {
				System.out.println("Couldn't read file: " + filePath);
				System.exit(0);
			}
		}
		return br;
	}

	public static BufferedReader getBufferedReaderFromString(String input) {
		return new BufferedReader(new StringReader(input));
	}

	private static BufferedWriter getBufferedWriter(String filePath) {
		BufferedWriter bw = null;
		try { // try absolute path
			bw = new BufferedWriter(new FileWriter(filePath));
			return bw;
		} catch (Exception e1) {
			try { // try relative path
				String absoluteFilePath = new File("").getAbsolutePath();
				filePath = absoluteFilePath + "/../" + filePath;
				bw = new BufferedWriter(new FileWriter(filePath));
				return bw;
			} catch (Exception e2) {
				System.out.println("Couldn't read file: " + filePath);
				System.exit(0);
			}
		}
		return bw;
	}

	public static String readBufferedReader(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder("");
		String input = "";
		while ((input = br.readLine()) != null) {
			sb.append(input + "\n");
		}
		return sb.toString();
	}
}
