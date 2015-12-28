package edu.uw.ling570.morphacceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.ling570.utils.Utils;

public class MorphAcceptor implements IMorphAcceptor {

	private Node startState;

	public MorphAcceptor(BufferedReader fstBufferedReader) throws IOException {
		this.startState = readFSTFile(fstBufferedReader);
	}

	public static void main(String[] args) throws Exception {

		// Step1: expandFSM
		String lexiconFilePath = "src/main/webapp/WEB-INF/resources/morphacceptor/input_lexicon.txt";
		String morphotacticsFilePath = "src/main/webapp/WEB-INF/resources/morphacceptor/input_morphotactics.txt";
		BufferedReader lexiconBufferedReader = Utils.getBufferedReader(lexiconFilePath);
		BufferedReader morphotacticsBufferedReader = Utils.getBufferedReader(morphotacticsFilePath);

		IFSMExpander fsmExpander = new FSMExpander(lexiconBufferedReader, morphotacticsBufferedReader);
		String expandedFSM = fsmExpander.expand();

		// Step2: acceptMorph
		String inputFilePath = "src/main/webapp/WEB-INF/resources/morphacceptor/input_testsamples.txt";
		BufferedReader expandedFSMBufferedReader = Utils.getBufferedReaderFromString(expandedFSM);
		BufferedReader inputBufferedReader = Utils.getBufferedReader(inputFilePath);

		IMorphAcceptor morphAcceptor = new MorphAcceptor(expandedFSMBufferedReader);
		String output = morphAcceptor.fstRecognizePatch(inputBufferedReader);
		System.out.println(output);

		lexiconBufferedReader.close();
		morphotacticsBufferedReader.close();
		expandedFSMBufferedReader.close();
		inputBufferedReader.close();
	}

	public String fstRecognizePatch(BufferedReader inputBufferedReader) throws Exception {
		StringBuilder sb = new StringBuilder("");
		String input;
		while ((input = inputBufferedReader.readLine()) != null) {
			if (input.trim().equals(""))
				continue;

			// Recognize input line using the FST starting from the
			// startState
			ArrayList<String> fstRecognizeOutput = fstRecognize(input);

			// Printing output
			sb.append(input + " => " + fstRecognizeOutput.get(0).trim() + "\n");
			for (int i = 1; i < fstRecognizeOutput.size(); i++) {
				sb.append(input.replaceAll(".", " ") + " => " + fstRecognizeOutput.get(i) + "\n");
			}
		}
		return sb.toString();
	}

	/*
	 * This method follows the algorithm described in fig.2.19 & section.2.2.6
	 * form J&M textbook
	 */
	public ArrayList<String> fstRecognize(String inputString) throws Exception {

		String[] input = cleanInput(inputString);
		if (this.startState == null)
			throw new Exception("No start state");

		Stack<SearchState> agenda = new Stack<SearchState>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<SearchState> searchMemo = new ArrayList<SearchState>();

		// push start state
		agenda.push(new SearchState(0, this.startState, "", 1.0));
		SearchState currentState = agenda.pop();

		while (true) {
			if (isPreviouslyVisited(currentState, searchMemo)) {
				if (agenda.isEmpty())
					break;
				currentState = agenda.pop();
				continue;
			}

			visitSearchState(currentState, searchMemo);

			if (isAcceptState(currentState, input)) {
				outputs.add(cleanOutput(currentState.accumulativeOutput));
			} else
				generateNewStates(currentState, agenda, input);

			if (agenda.isEmpty())
				break;
			else
				currentState = agenda.pop();
		}

		if (outputs.isEmpty()) {
			outputs.add("*NONE*");
		}
		return outputs;
	}

	private void generateNewStates(SearchState currentState, Stack<SearchState> agenda, String[] input) {
		Node currentNode = currentState.fsaState;
		int currentIndex = currentState.stringIndex;
		String currentOutput = currentState.accumulativeOutput;
		double currentProb = currentState.accumulativeProb;

		// Add empty transitions to agenda
		ArrayList<TransitionInfo> transitionInfosWithEmptyInput = currentNode.transitions.get("*e*");
		if (transitionInfosWithEmptyInput != null)
			for (TransitionInfo transitionInfo : transitionInfosWithEmptyInput) {
				String newOutput = transitionInfo.output.equals("*e*") ? "" : "\"" + transitionInfo.output + "\"";
				newOutput = currentOutput + newOutput.replace("\"", "");
				double newProb = currentProb * transitionInfo.prob;
				agenda.push(new SearchState(currentIndex, transitionInfo.nextState, newOutput, newProb));
			}

		if (currentIndex < input.length) {
			ArrayList<TransitionInfo> transitionInfos = currentNode.transitions.get(input[currentIndex]);
			if (transitionInfos != null)
				for (TransitionInfo transitionInfo : transitionInfos) {
					String newOutput = transitionInfo.output.equals("*e*") ? "" : "\"" + transitionInfo.output + "\"";
					newOutput = currentOutput + newOutput.replace("\"", "");
					double newProb = currentProb * transitionInfo.prob;
					agenda.push(new SearchState(currentIndex + 1, transitionInfo.nextState, newOutput, newProb));
				}
		}

		if (currentIndex < input.length && input[currentIndex].equals("*e*")) {
			agenda.push(new SearchState(currentIndex + 1, currentNode, currentOutput, currentProb));
		}
	}

	private boolean isAcceptState(SearchState currentState, String[] input) {
		if (currentState.stringIndex == input.length && currentState.fsaState.isFinal)
			return true;
		return false;
	}

	private void visitSearchState(SearchState currentState, ArrayList<SearchState> searchMemo) {
		searchMemo.add(currentState);
	}

	private boolean isPreviouslyVisited(SearchState currentState, ArrayList<SearchState> searchMemo) {
		boolean result = searchMemo.contains(currentState);
		return result;
	}

	private String cleanOutput(String output) {
		if (output.equals(""))
			return "*e*";
		return output;
	}

	private String[] cleanInput(String input) {
		if (input.equals("*e*")) {
			return new String[] { input };
		} else {
			char[] splitted = input.trim().toCharArray();
			String[] output = new String[splitted.length];
			for (int i = 0; i < splitted.length; i++) {
				output[i] = splitted[i] + "";
			}
			return output;
		}
	}

	private static class SearchState {
		private int stringIndex;
		private Node fsaState;
		private String accumulativeOutput;
		private double accumulativeProb;

		private SearchState(int stringIndex, Node fsaState, String accOutput, double accProb) {
			this.stringIndex = stringIndex;
			this.fsaState = fsaState;
			this.accumulativeOutput = accOutput;
			this.accumulativeProb = accProb;
		}

		@Override
		public boolean equals(Object obj) {
			SearchState other = (SearchState) obj;
			if (this.fsaState.name.equals(other.fsaState.name) && this.stringIndex == other.stringIndex
					&& this.accumulativeOutput.equals(other.accumulativeOutput)) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "name: " + this.fsaState.name + " stringIndex:" + this.stringIndex + " output:" + accumulativeOutput
					+ " prob:" + accumulativeProb;
		}
	}

	private static class TransitionInfo {
		private Node nextState;
		private String output;
		private double prob;

		public TransitionInfo(Node nextState, String output, double prob) {
			this.nextState = nextState;
			this.output = output;
			this.prob = prob;
		}

	}

	private static class Node {
		private String name;
		private boolean isFinal;
		private HashMap<String, ArrayList<TransitionInfo>> transitions;

		public Node(String name, boolean isFinal) {
			this.name = name;
			this.isFinal = isFinal;
			this.transitions = new HashMap<String, ArrayList<TransitionInfo>>();
		}

		public void addTransition(String label, Node destinationNode, String output, double prob) {
			if (this.transitions.get(label) == null)
				this.transitions.put(label, new ArrayList<TransitionInfo>());
			ArrayList<TransitionInfo> transitionInfoList = this.transitions.get(label);
			transitionInfoList.add(new TransitionInfo(destinationNode, output, prob));
			this.transitions.put(label, transitionInfoList);
		}

		@Override
		public String toString() {
			String result = this.name + " " + this.isFinal;
			for (Map.Entry<String, ArrayList<TransitionInfo>> entry : this.transitions.entrySet()) {
				String destLabel = entry.getKey();
				for (TransitionInfo transitionInfo : entry.getValue()) {
					String destName = transitionInfo.nextState.name;
					String output = transitionInfo.output;
					double prob = transitionInfo.prob;
					result += "\n\t" + destName + " " + destLabel + ":" + output + " prob:" + prob;
				}
			}
			return result;
		}
	}

	private Node readFSTFile(BufferedReader fstBufferedReader) throws IOException {
		HashMap<String, Node> nodes = new HashMap<String, Node>();
		Node startState = null;
		int lineNumber = 1;
		String space = "(\\s+)";
		String openBrace = "(\\()";
		String closedBrace = "(\\))";
		String word = "(\\w+)";
		String label = "(\".+?\"|\\*e\\*)";
		String prob = "((\\d|\\.)+)";
		String regex = openBrace // 1
				+ word // 2
				+ space + "?"// 3
				+ "(.*)" // 4
				+ closedBrace; // 5

		String transitionRegex = openBrace // 1
				+ word // 2
				+ space // 3
				+ label // 4
				+ space + "?" // 5
				+ label + "?" // 6
				+ space + "?" // 7
				+ prob + "?" // 8
				+ closedBrace; // 9

		// read fsa_file line by line
		String input;
		while ((input = fstBufferedReader.readLine()) != null) {
			if (lineNumber == 1) {
				nodes.put(input, new Node(input, true));
				lineNumber++;
				continue;
			}

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			matcher.find();

			// src
			String srcName = matcher.group(2);
			if (nodes.get(srcName) == null)
				nodes.put(srcName, new Node(srcName, false));
			if (lineNumber == 2)
				startState = nodes.get(srcName);

			// transitions
			String transitions = matcher.group(4);

			pattern = Pattern.compile(transitionRegex);
			matcher = pattern.matcher(transitions);

			while (matcher.find()) {

				String destName = matcher.group(2);
				String inputLabel = matcher.group(4);
				if (inputLabel != null)
					inputLabel = inputLabel.replaceAll("\"", "");
				String outputLabel = matcher.group(6);
				if (outputLabel != null)
					outputLabel = outputLabel.replaceAll("\"", "");
				String probability = matcher.group(8);

				if (nodes.get(destName) == null)
					nodes.put(destName, new Node(destName, false));

				double probalibityDouble = (probability == null) ? 1.0 : Double.parseDouble(probability);
				outputLabel = outputLabel == null ? inputLabel : outputLabel;
				nodes.get(srcName).addTransition(inputLabel, nodes.get(destName), outputLabel, probalibityDouble);
			}

			lineNumber++;
		}

		return startState;
	}
}
