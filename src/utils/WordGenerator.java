package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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

		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("assets/" + WORDS_FILE_NAME);
		if (stream == null) {
			System.out.println("File " + WORDS_FILE_NAME + " not fund!");
			Arrays.asList("HAMAK", "WODA", "SAMOCH”D", "KOMPUTER").stream().forEach(words::add);
		} else {
			BufferedReader bf = new BufferedReader(new InputStreamReader(stream));
			String line = "";
			try {
				while ((line = bf.readLine()) != null) { // in debugging, (cb) contains the char data as char elements.
					String[] wordsLine = line.split(" ");
					for (String word : wordsLine) {
						System.out.println("adding word: " + word);
						words.add(word.toUpperCase());
					}
				}
			} catch (IOException e) {

			} finally {
				try {
					bf.close();
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader("Words.txt"));
//			String line = reader.readLine();
//			while (line != null) {
//				String[] wordsLine = line.split(" ");
//				for (String word : wordsLine) {
//					System.out.println("adding word: "+word);
//					words.add(word.toUpperCase());
//				}
//				line = reader.readLine();
//			}
//			reader.close();
//		} catch (Exception e) {
//			// Handle this
//		}
	}

	public String getNewWord() {
		System.out.println("WordGenerator: getNewWord()");
		return "•∆ £—”åØè";
		/*
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
		*/
	}

}
