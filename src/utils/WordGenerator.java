package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WordGenerator {

	private final static String WORDS_FILE_NAME = "words.txt";
	private List<String> words = new ArrayList<String>();
	private Queue<String> usedWords = new ConcurrentLinkedQueue<String>();
	private Random rand = new Random(System.currentTimeMillis());

	public WordGenerator() {
		System.out.println("WordGenerator: reading and collecting words from file " + WORDS_FILE_NAME + "!");
		URL url = this.getClass().getClassLoader().getResource(WORDS_FILE_NAME);

		try {
			File file = new File(url.toURI());			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				String[] wordsLine = line.split(" ");
				for (String word : wordsLine) {
					System.out.println("adding word: "+word);
					words.add(word.toUpperCase());
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			// Handle this
		}
	}

	public String getNewWord() {
		if (usedWords.size() == words.size()) {
			usedWords.clear();
		}
		int r = rand.nextInt(words.size());
		System.out.println("random index of word: " + r);
		String randomWord = words.get(r);
		System.out.println("random word: " + randomWord);
		while (usedWords.contains(randomWord)) {
			r = rand.nextInt(words.size());
			System.out.println("random index of word: " + r);
			randomWord = words.get(r);
			System.out.println("random word: " + randomWord);
		}
		return randomWord;
	}

}
