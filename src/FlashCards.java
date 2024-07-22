import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class FlashCards {

	private static ArrayList<String> Questions;
	private static ArrayList<String> Answers;
	private static String title;

	public FlashCards(String title) {
		Questions = new ArrayList<String>();
		Answers = new ArrayList<String>();
		this.title = title;
	}

	public static boolean add(String Question, String Answer) {
		Questions.add(Question);
		Answers.add(Answer);
		return true;
	}

	public static boolean remove(String question) {
		if (Questions.contains(question)) {
			int index = Questions.indexOf(question);
			Questions.remove(index);
			Answers.remove(index);
			return true;
		}
		return false;
	}

	public static void printCards() {
		if (Questions.size() == 0) {
			System.out.println("No cards left!");
		}
		for (int i = 0; i < Questions.size(); i++) {
			System.out.println(Questions.get(i));
			System.out.println(Answers.get(i));
			System.out.println("-----");
		}
	}

	public static int getSize() {
		return Questions.size();
	}

	public static int getIndex(String question) {
		if (Questions.contains(question)) {
			return Questions.indexOf(question);
		} else {
			return -1;
		}
	}
	
	public static String getQuestion(int i) {
		return Questions.get(i);
	}
	
	public static String getAnswer(int i) {
		return Answers.get(i);
	}

	public static void save(String filename, String path) {
		try {
			PrintStream ps = new PrintStream(new File(path + "/" + filename + ".txt"));
			ps.println("FlashCardSet");
			for (int i = 0; i < Questions.size(); i++) {
				ps.println(Questions.get(i) + "-" + Answers.get(i));
			}
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("File could not be created");
			e.printStackTrace();
		}
	}
	
	public static void load(String filename, String path) {
		try {
			Scanner scan = new Scanner(new File(filename + ".txt"));
			if (!scan.nextLine().equals("FlashCardSet")) {
				return;
			}
			if (Questions.size() != 0) {
				save(title + "_backup", path);
				Questions.clear();
				Answers.clear();
			}
			while (scan.hasNext()) {
				String[] input = scan.nextLine().split("-");
				if (input[1].contains("#")) {
					String[] temp = input[1].split("#");
					input[1] = "";
					for (String s : temp) {
						input[1] = input[1] + "# " + s + "\n";
					}
				}
				add(input[0], input[1]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File could not be created");
			e.printStackTrace();
		}
		title = filename;
	}
	
	public static boolean isCardSet() {
		if (Questions.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	

}
