package edu.uw.ling570.tokenizer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.ling570.utils.Utils;

public class Tokenizer implements ITokenizer {

	private static boolean debug = false;
	private TreeSet<String> abbreviations;

	public static void main(String[] args) {
		ITokenizer tokenizer = new Tokenizer("src/main/webapp/WEB-INF/resources/tokenizer/abbreviations.txt");
		System.out.println(tokenizer.tokenizeLine("Mr. mr. MR. esalm a, good boy.!"));
	}

	public Tokenizer(String abbreviationsFilePath) {
		// read abbreviations file
		BufferedReader abbreviationsBufferedReader = Utils.getBufferedReader(abbreviationsFilePath);
		this.abbreviations = readAbbreviationsFile(abbreviationsBufferedReader);
	}

	public String tokenizeLine(String input) {
		String outputLine = "";
		String[] splittedLine = input.split("\\s");
		for (String candidateToken : splittedLine) {
			outputLine += tokenize(candidateToken) + " ";
		}
		return outputLine.trim();
	}

	private String tokenize(String candidateToken) {
		String processedToken = candidateToken;
		if (!isAbbreviation(processedToken) && !isInitials(processedToken)) {
			processedToken = handlePreceedingPunct(processedToken);
			processedToken = handleTrailingPunct(processedToken);
			processedToken = handleApostrophes(processedToken);
			processedToken = handleHyphens(processedToken);
			processedToken = handleSlashes(processedToken);
		}
		return processedToken;
	}

	private boolean isInitials(String processedToken) {
		String regex = "^([A-Z]\\.)+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(processedToken);
		return matcher.find();
	}

	private boolean isAbbreviation(String processedToken) {
		return this.abbreviations.contains(processedToken);
	}

	private String handleSlashes(String input) {
		String output = input;
		String hyphenRegex = "([a-zA-Z])(\\/)([a-zA-Z])";
		Pattern pattern = Pattern.compile(hyphenRegex);
		Matcher matcher = pattern.matcher(output);

		while (matcher.find()) {
			if (debug)
				System.out.println(
						String.format("I found the text" + " \"%s\" starting at " + "index %d and ending at index %d.",
								matcher.group(), matcher.start(), matcher.end()));
			output = matcher.replaceFirst("$1 $2 $3");
			matcher = pattern.matcher(output);

			if (debug)
				System.out.println(output);
		}

		return output;
	}

	private String handleHyphens(String input) {
		String output = input;
		String hyphenRegex = "(\\w)(\\-)(\\w)";
		Pattern pattern = Pattern.compile(hyphenRegex);
		Matcher matcher = pattern.matcher(output);

		while (matcher.find()) {
			if (debug)
				System.out.println(
						String.format("I found the text" + " \"%s\" starting at " + "index %d and ending at index %d.",
								matcher.group(), matcher.start(), matcher.end()));
			output = matcher.replaceFirst("$1 $2 $3");
			matcher = pattern.matcher(output);

			if (debug)
				System.out.println(output);
		}

		return output;
	}

	// handle 's n't 're 'll
	private String handleApostrophes(String input) {
		String output = input;
		String apostropheRegex = "(.+)('s|'re|n't|'ll|'ve|'m|'d)";
		Pattern pattern = Pattern.compile(apostropheRegex);
		Matcher matcher = pattern.matcher(output);

		if (matcher.find()) {
			if (debug)
				System.out.println(
						String.format("I found the text" + " \"%s\" starting at " + "index %d and ending at index %d.",
								matcher.group(), matcher.start(), matcher.end()));
			output = matcher.replaceFirst("$1 $2");
		}

		return output;
	}

	private String handlePreceedingPunct(String input) {
		String output = input;
		String preceedingPunct = "(\\s|^)(\\p{Punct})(\\p{Punct}*)(\\w+)(\\p{Punct}*)";
		Pattern pattern = Pattern.compile(preceedingPunct);
		Matcher matcher = pattern.matcher(output);

		while (matcher.find()) {
			if (debug)
				System.out.println(
						String.format("I found the text" + " \"%s\" starting at " + "index %d and ending at index %d.",
								matcher.group(), matcher.start(), matcher.end()));

			output = matcher.replaceFirst("$1$2 $3$4$5");
			matcher = pattern.matcher(output);

			if (debug)
				System.out.println(output);
		}

		return output;
	}

	private String handleTrailingPunct(String input) {
		String output = input;
		String trailingPunct = "(\\w+)(\\p{Punct}*)(\\p{Punct})(\\s|$)";
		Pattern pattern = Pattern.compile(trailingPunct);
		Matcher matcher = pattern.matcher(output);

		for (String s : output.split(" "))
			if (isAbbreviation(s))
				return output;

		while (matcher.find()) {
			if (debug)
				System.out.println(
						String.format("I found the text" + " \"%s\" starting at " + "index %d and ending at index %d.",
								matcher.group(), matcher.start(), matcher.end()));

			output = matcher.replaceFirst("$1$2 $3$4");

			String remainingPart = output.substring(matcher.start(1), matcher.end(2));
			if (isAbbreviation(remainingPart) || isInitials(remainingPart))
				return output;

			if (debug)
				System.out.println(output);

			matcher = pattern.matcher(output);
		}

		return output;
	}

	private TreeSet<String> readAbbreviationsFile(BufferedReader br) {
		TreeSet<String> abbreviations = new TreeSet<String>();

		try {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				abbreviations.add(currentLine);
			}

		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: Please provide full path to abbreviations.txt file");
			System.err.println(
					"Example: cat ex1 | ./eng_tokenizer.sh /Users/eslamelsawy/Desktop/CLMS/Projects/570/hw1/Abbreviations.txt > ex1.tok");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return abbreviations;
	}
}
