package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LoadDictionary {

	static Dictionary initializeDictionary() throws IOException {

		// Variables
		String inputLine = null;
		String oldWordKey = "";

		// Reference name for the file dictionary
		BufferedReader reader = new BufferedReader(new FileReader(new File("WebstersUnabridgedDictionary.txt")));

		// Instantiate class WordDetail
		Dictionary wordDetail = new Dictionary();

		// load the file dictionary into a HashMap an Arralist
		while ((inputLine = reader.readLine()) != null) {
			String[] wordsKey = inputLine.split("\\s+");

			// Ignore empty lines.
			if (inputLine.equals("")) {
				continue;
			}

			// Select the first word in line
			if (wordsKey.length == 1) {
				oldWordKey = wordsKey[0];
			}
			// if not, is just definition
			else {
				wordDetail.addDicWord(oldWordKey.toUpperCase(), inputLine);

			}
		}

		return wordDetail;
	}

}
