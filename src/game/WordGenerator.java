package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WordGenerator {

	private static List<String> words = new ArrayList<String>();
	private static Queue<String> usedWords = new ConcurrentLinkedQueue<String>();

	static {
		System.out.println("WordGenerator: reading and collection words from file!");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("Words.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] wordsLine = line.split(" ");
				for (String word : wordsLine) {
					words.add(word);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			// Handle this
		}
	}

	public static String getNewWord() {
		System.out.println("WordGenerator: getNewWord()");
		if (usedWords.size() == words.size()) {
			usedWords.clear();
		}
		Random rand = new Random(System.currentTimeMillis());
		String randomWord = words.get(rand.nextInt(words.size()));
		while (usedWords.contains(randomWord)) {
			randomWord = words.get(rand.nextInt(words.size()));
		}
		System.out.println("WordGenerator: word="+randomWord);
		return randomWord;
	}

}
