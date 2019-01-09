package utils;

public class WordCodeDecode {
	
	private final static String specPolishChars = "¥ÆÊ£ÑÓŒ¯";	
	
	private static String charToSpec(char c) {
		for(int i=0; i < specPolishChars.length(); i++) {
			if (c == specPolishChars.charAt(i)) {
				return "#"+i;
			}
		}
		return String.valueOf(c);
	}
	
	public static String codePolishWordToWordWithSpecs(String word) {
		StringBuilder result = new StringBuilder();
		for(int i=0; i < word.length(); i++ ) {
			result.append(charToSpec(word.charAt(i)));
		}
		return result.toString();
	}
	
	public static String decodeWordWithSpecsToPolishWord(String word) {
		for(int i=0; i < specPolishChars.length(); i++) {
			word = word.replace("#"+i, specPolishChars.substring(i,i+1));				
			}
		return word;
	}
	  

}
