import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	public static void main(String[] args) throws IOException {
	
//		String s = "3\n(0 (1 "a"))\n(0 (2 "b"))\n(0 (3 *e*))\n(1 (1 "a"))\n(1 (3 *e*))\n(1 (2 "b"))\n(2 (2 "b"))\n(2 (3 *e*))\n";
	}
	
	private static String[] cleanInput(String input) {
		String tokenRegex = "(^)(\")(.+)(\")($)";
		Pattern pattern = Pattern.compile(tokenRegex);
		String[] inputArray = input.split("\\s+");

		for (int i = 0; i < inputArray.length; i++) {
			Matcher matcher = pattern.matcher(inputArray[i]);
			inputArray[i] = matcher.replaceFirst("$3");
		}
		return inputArray;
	}

}
