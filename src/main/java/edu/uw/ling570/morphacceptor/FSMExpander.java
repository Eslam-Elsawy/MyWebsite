package edu.uw.ling570.morphacceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.ling570.utils.Utils;

/*- Make sure you understand ExpandFSM1 Class, as ExpandFSM2 follows the same approach with minor modifications, 
 *  the difference is that here I use TransitionInfo class which stores more information about each transition 
 *  (i.e: nextStateName, output & prob) however in ExpandFSM1 nextStateName was enough */
public class FSMExpander implements IFSMExpander {

	private HashMap<String, ArrayList<String>> lexicon;
	private Node startNode;
	private LexiconHelper lexiconHelper;
	private String acceptNode;

	public FSMExpander(BufferedReader lexiconBufferedReader, BufferedReader morphotacticsBufferedReader)
			throws IOException {
		this.lexicon = readLexicon(lexiconBufferedReader);
		this.startNode = readMorphotactics(morphotacticsBufferedReader);
		this.lexiconHelper = new LexiconHelper(lexicon);
	}

	public String expand() {
		StringBuilder expandedFSM = new StringBuilder(this.acceptNode+"\n");
		expandFSMRecursively(this.startNode, new HashSet<String>());
		dumpFSM(this.startNode, new HashSet<String>(), expandedFSM);
		return expandedFSM.toString();
	}

	public static void main(String[] args) throws IOException {

		// Input Arguments
		String lexiconFilePath = "src/main/webapp/WEB-INF/resources/morphacceptor/input_lexicon.txt";
		String morphotacticsFilePath = "src/main/webapp/WEB-INF/resources/morphacceptor/input_morphotactics.txt";
		BufferedReader lexiconBufferedReader = Utils.getBufferedReader(lexiconFilePath);
		BufferedReader morphotacticsBufferedReader = Utils.getBufferedReader(morphotacticsFilePath);

		FSMExpander fsmExpander = new FSMExpander(lexiconBufferedReader, morphotacticsBufferedReader);
		String expandedFSM = fsmExpander.expand();
		System.out.println(expandedFSM);

		lexiconBufferedReader.close();
		morphotacticsBufferedReader.close();
	}

	/*- same implementation as LexiconHelper in ExpandFSM1, see comments there */
	private static class LexiconHelper {

		private HashMap<String, ArrayList<String>> lexicon;
		private HashMap<String, ArrayList<Node>> startNodes;
		private HashMap<String, ArrayList<Node>> terminalNodes;
		private int stateNumber = 0;

		public LexiconHelper(HashMap<String, ArrayList<String>> lexicon) {
			this.lexicon = lexicon;
			startNodes = new HashMap<String, ArrayList<Node>>();
			terminalNodes = new HashMap<String, ArrayList<Node>>();
			parseLexicon();
		}

		private void parseLexicon() {
			for (Entry<String, ArrayList<String>> entry : lexicon.entrySet()) {
				String morphemeClassName = entry.getKey();
				ArrayList<Node> expandedStartNodes = new ArrayList<Node>();
				ArrayList<Node> expandedTerminalNodes = new ArrayList<Node>();
				for (String morpheme : entry.getValue()) {

					Node firstOnPathNode = new Node("_" + this.stateNumber++, false);
					Node previousOnPathNode = firstOnPathNode;
					Node lastOnPathNode = null;
					for (char c : morpheme.toCharArray()) {
						lastOnPathNode = new Node("_" + this.stateNumber++, false);
						previousOnPathNode.addTransition(c + "", lastOnPathNode, c + "", 1.0);
						previousOnPathNode = lastOnPathNode;
					}

					expandedStartNodes.add(firstOnPathNode);
					expandedTerminalNodes.add(lastOnPathNode);
				}

				startNodes.put(morphemeClassName, expandedStartNodes);
				terminalNodes.put(morphemeClassName, expandedTerminalNodes);
			}
		}

		public ArrayList<Node> getStartNodes(String transitionName) {
			return startNodes.get(transitionName);
		}

		public ArrayList<Node> getTerminalNodes(String transitionName) {
			return terminalNodes.get(transitionName);
		}

	}

	/*- same implementation as expandFSM in ExpandFSM1, see comments there 
	 * the difference is that TransitionInfo class stores more information about each transition (i.e: nextStateName, output & prob)*/
	private void expandFSMRecursively(Node currentNode, HashSet<String> visitedNodes) {

		if (visitedNodes.contains(currentNode.name))
			return;

		visitedNodes.add(currentNode.name);

		HashMap<String, ArrayList<TransitionInfo>> transitions = currentNode.transitions;
		ArrayList<String> toBeRemovedTransitions = new ArrayList<String>();
		for (Entry<String, ArrayList<TransitionInfo>> transition : transitions.entrySet()) {
			String transitionName = transition.getKey();
			if (!transitionName.equals("*e*"))
				toBeRemovedTransitions.add(transitionName);
		}

		for (String transitionName : toBeRemovedTransitions) {

			ArrayList<TransitionInfo> transitionInfoList = transitions.remove(transitionName);
			ArrayList<Node> replacementStartNodes = this.lexiconHelper.getStartNodes(transitionName);
			ArrayList<Node> replacementTerminalNodes = this.lexiconHelper.getTerminalNodes(transitionName);
			if (replacementStartNodes == null)
				continue;

			for (TransitionInfo transitionInfo : transitionInfoList) {
				for (int i = 0; i < replacementStartNodes.size(); i++) {
					Node replacementSource = replacementStartNodes.get(i);
					Node replacementTerminal = replacementTerminalNodes.get(i);

					currentNode.addTransition("*e*", replacementSource, "*e*", 1.0);
					replacementTerminal.addTransition("*e*", transitionInfo.nextState, "/" + transitionName + " ", 1.0);
				}
				expandFSMRecursively(transitionInfo.nextState, visitedNodes);
			}
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
		// tranitionInfo stores nextState, output & prob(if needed)
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

	private HashMap<String, ArrayList<String>> readLexicon(BufferedReader lexiconBufferedReader) throws IOException {
		HashMap<String, ArrayList<String>> lexicon = new HashMap<String, ArrayList<String>>();
		String input;
		while ((input = lexiconBufferedReader.readLine()) != null) {
			if (input.trim().isEmpty())
				continue;
			String[] morphemeInfo = input.split("\\s+");
			String morphemeName = morphemeInfo[0];
			String morphemeClass = morphemeInfo[1];
			if (!lexicon.containsKey(morphemeClass))
				lexicon.put(morphemeClass, new ArrayList<String>());
			lexicon.get(morphemeClass).add(morphemeName);
		}

		return lexicon;
	}

	private Node readMorphotactics(BufferedReader morphotacticsBufferedReader) throws IOException {
		HashMap<String, Node> nodes = new HashMap<String, Node>();
		Node startState = null;
		int lineNumber = 1;
		String space = "(\\s+)";
		String openBrace = "(\\()";
		String closedBrace = "(\\))";
		String word = "(\\w+)";
		String label = "(\\w+|\\*e\\*)";
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
		while ((input = morphotacticsBufferedReader.readLine()) != null) {
			if (input.trim().isEmpty()) {
				continue;
			}
			if (lineNumber == 1) {
				nodes.put(input, new Node(input, true));
				this.acceptNode = input;
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

				if (nodes.get(destName) == null)
					nodes.put(destName, new Node(destName, false));

				// morphatactics is FSA so no need to output
				nodes.get(srcName).addTransition(inputLabel, nodes.get(destName), "*e*", 1.0);
			}

			lineNumber++;
		}

		return startState;
	}

	private void dumpFSM(Node currentNode, HashSet<String> visitedNodes, StringBuilder sb) {
		if (visitedNodes.contains(currentNode.name))
			return;

		visitedNodes.add(currentNode.name);

		// transitions
		HashMap<String, ArrayList<TransitionInfo>> transitions = currentNode.transitions;
		for (Entry<String, ArrayList<TransitionInfo>> transition : transitions.entrySet()) {
			String transitionInputLabel = transition.getKey();
			ArrayList<TransitionInfo> transitionInfoList = transitions.get(transitionInputLabel);
			for (TransitionInfo transitionInfo : transitionInfoList) {
				// input label
				if (!transitionInputLabel.equals("*e*"))
					transitionInputLabel = "\"" + transitionInputLabel + "\"";

				// output label
				String transitionOutputLabel = transitionInfo.output;
				if (!transitionOutputLabel.equals("*e*"))
					transitionOutputLabel = "\"" + transitionOutputLabel + "\"";

				sb.append("(" + currentNode.name + " (" + transitionInfo.nextState.name + " " + transitionInputLabel
						+ " " + transitionOutputLabel + "))\n");
				dumpFSM(transitionInfo.nextState, visitedNodes, sb);
			}
		}
	}
}
