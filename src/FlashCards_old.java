import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FlashCards_old {
	
	private static Map<String, String> cards;
	
	public FlashCards_old() {
		cards = new TreeMap<String, String>();
	}
	
	public static void add(String Question, String Answer) {
		cards.put(Question, Answer);
	}
	
	public static void remove(String Question) {
		cards.remove(Question);
	}
	
	public static void printCards() {
		Set<String> keys = cards.keySet();
		for (String s : keys) {
			System.out.println("Question:");
			System.out.println(s);
			System.out.println("Answer:");
			System.out.println(cards.get(s));
		}
	}
}
