package edu.uw.ling570.fsaacceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.ling570.utils.Utils;

public class FSAAcceptor implements IFSAAcceptor {

	private Node startNode;

	public FSAAcceptor(BufferedReader fsaBufferedReader) throws IOException {
		this.startNode = readFSAFile(fsaBufferedReader);
	}

	public static void main(String[] args) throws Exception {

		String fsaFilePath = "src/main/webapp/WEB-INF/resources/fsaacceptor/input_fsa";
		String inputFilePath = "src/main/webapp/WEB-INF/resources/fsaacceptor/input_testsamples";
		BufferedReader fsaBufferedReader = Utils.getBufferedReader(fsaFilePath);
		BufferedReader inputBufferedReader = Utils.getBufferedReader(inputFilePath);

		IFSAAcceptor fsaAcceptor = new FSAAcceptor(fsaBufferedReader);
		String output = fsaAcceptor.recognizePatch(inputBufferedReader);
		System.out.println(output);
	}

	public String recognizePatch(BufferedReader br) throws Exception {
		// read inputfile line by line
		StringBuilder sb = new StringBuilder("");

		try {
			String input;
			while ((input = br.readLine()) != null) {
				boolean accepted = recognize(input);
				sb.append(input + " => " + (accepted ? "yes" : "no") + "\n");
			}
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return sb.toString();
	}

	public boolean recognize(String inputString) throws Exception {
		if (this.startNode == null)
			throw new Exception("No startNode");
		String[] input = cleanInput(inputString);
		Stack<SearchState> agenda = new Stack<SearchState>();
		HashMap<String, HashSet<Integer>> searchMemo = new HashMap<String, HashSet<Integer>>();

		agenda.push(new SearchState(0, this.startNode));
		SearchState currentState = agenda.pop();

		while (true) {
			if (isPreviouslyVisited(currentState, searchMemo)) {
				// System.out.println("PreviouslyVisited: " +
				// currentState.fsaState.name + "," + currentState.stringIndex);
				if (agenda.isEmpty())
					return false;
				currentState = agenda.pop();
				continue;
			}

			visitSearchState(currentState, searchMemo);
			// System.out.println("Visit: " + currentState.fsaState.name + "," +
			// currentState.stringIndex);

			if (isAcceptState(currentState, input))
				return true;
			else
				generateNewStates(currentState, agenda, input);
			if (agenda.isEmpty())
				return false;
			else
				currentState = agenda.pop();
		}
	}

	private String[] cleanInput(String input) {
		String tokenRegex = "(^)(\")(.+)(\")($)";
		Pattern pattern = Pattern.compile(tokenRegex);
		String[] inputArray = input.split("\\s+");

		for (int i = 0; i < inputArray.length; i++) {
			Matcher matcher = pattern.matcher(inputArray[i]);
			inputArray[i] = matcher.replaceFirst("$3");
		}
		return inputArray;
	}

	private void visitSearchState(SearchState currentState, HashMap<String, HashSet<Integer>> searchMemo) {
		String stateName = currentState.fsaState.name;
		Integer stringIndex = currentState.stringIndex;

		if (!searchMemo.containsKey(stateName))
			searchMemo.put(stateName, new HashSet<Integer>());
		searchMemo.get(stateName).add(stringIndex);
	}

	private boolean isPreviouslyVisited(SearchState currentState, HashMap<String, HashSet<Integer>> searchMemo) {
		String stateName = currentState.fsaState.name;
		Integer stringIndex = currentState.stringIndex;
		return searchMemo.get(stateName) == null ? false : searchMemo.get(stateName).contains(stringIndex);
	}

	private void generateNewStates(SearchState currentState, Stack<SearchState> agenda, String[] input) {
		Node currentNode = currentState.fsaState;
		int currentIndex = currentState.stringIndex;

		// Add empty transitions to agenda
		ArrayList<Node> destStatesWithEmptyInput = currentNode.transitions.get("*e*");
		if (destStatesWithEmptyInput != null)
			for (Node newState : destStatesWithEmptyInput)
				agenda.push(new SearchState(currentIndex, newState));

		if (currentIndex < input.length) {
			ArrayList<Node> destStatesWithNonEmptyInput = currentNode.transitions.get(input[currentIndex]);
			if (destStatesWithNonEmptyInput != null)
				for (Node newState : destStatesWithNonEmptyInput)
					agenda.push(new SearchState(currentIndex + 1, newState));
		}

		if (currentIndex < input.length && input[currentIndex].equals("*e*")) {
			agenda.push(new SearchState(currentIndex + 1, currentNode));
		}
	}

	private boolean isAcceptState(SearchState currentState, String[] input) {
		if (currentState.stringIndex == input.length && currentState.fsaState.isFinal)
			return true;
		return false;
	}

	private Node readFSAFile(BufferedReader fsaBufferedReader) throws IOException {
		HashMap<String, Node> nodes = new HashMap<String, Node>();
		Node startNode = null;
		int lineNumber = 1;
		String regex = "(\\()(\\w+)(\\s)(\\()(\\w+)(\\s)(\"?)(\\w+|\\*e\\*)(\"?)(\\))(\\))";

		// read fsa_file line by line
		String input;
		while ((input = fsaBufferedReader.readLine()) != null) {
			if (lineNumber == 1) {
				nodes.put(input, new Node(input, true));
				lineNumber++;
				continue;
			}
			// compare it against regex
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				String srcName = matcher.group(2);
				String destName = matcher.group(5);
				String label = matcher.group(8);

				if (nodes.get(srcName) == null)
					nodes.put(srcName, new Node(srcName, false));

				if (nodes.get(destName) == null)
					nodes.put(destName, new Node(destName, false));

				nodes.get(srcName).addTransition(label, nodes.get(destName));

				if (lineNumber == 2)
					startNode = nodes.get(srcName);

			} else {
				System.out.println("Couldn't read fsa line: " + input);
				return null;
			}

			lineNumber++;
		}

		return startNode;
	}

	private static class SearchState {
		private int stringIndex;
		private Node fsaState;

		private SearchState(int stringIndex, Node fsaState) {
			this.stringIndex = stringIndex;
			this.fsaState = fsaState;
		}
	}

	private static class Node {
		private String name;
		private boolean isFinal;
		private HashMap<String, ArrayList<Node>> transitions;

		public Node(String name, boolean isFinal) {
			this.name = name;
			this.isFinal = isFinal;
			this.transitions = new HashMap<String, ArrayList<Node>>();
		}

		public void addTransition(String label, Node destinationNode) {
			if (this.transitions.get(label) == null)
				this.transitions.put(label, new ArrayList<Node>());
			ArrayList<Node> destinations = this.transitions.get(label);
			destinations.add(destinationNode);
			this.transitions.put(label, destinations);
		}

		@Override
		public String toString() {
			String output = this.name + " " + this.isFinal;
			for (Map.Entry<String, ArrayList<Node>> entry : this.transitions.entrySet()) {
				String destLabel = entry.getKey();
				for (Node n : entry.getValue()) {
					String destName = n.name;
					output += "\n\t" + destName + " " + destLabel;
				}
			}
			return output;
		}
	}
}
