import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FlashCardGUI {

	private static Font buttonfont;
	private static Font cardfont;
	private static FlashCards cards;
	private static int currentCard;
	private static JFrame currentFrame;
	private static String currentFolder;
	private static String home;

	private static boolean onQuestionSide;
	private static boolean removalMode;

	public FlashCardGUI() {		
		Path currRelativePath = Paths.get("");
        String currAbsolutePathString = currRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is - " + currAbsolutePathString);
        currentFolder = currAbsolutePathString;
		
		SwingUtilities.invokeLater(() -> createFolderWindow(800, 800));
	}

	private void createFolderWindow(int width, int height) {
		// Setup the window
		currentFrame = new JFrame("FlashCards");
		currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = currentFrame.getContentPane();
		removalMode = false;

		// Create relevant panels
		JPanel fileview = new JPanel();
		fileview.setLayout(new GridLayout(6, 6));
		JPanel buttons = new JPanel();

		/////////////////////////////////////////////

		// Add folder and files buttons
		ArrayList<String> folders = readFolder(currentFolder);
		if (folders.size() != 0) {
			for (int i = 0; i < folders.size(); i++) {
				JButton button = createFolderButton(folders.get(i));
				button.setBackground(Color.ORANGE);
				fileview.add(button);
			}
		}

		ArrayList<String> files = readFiles(currentFolder);
		if (files.size() != 0) {
			for (int i = 0; i < files.size(); i++) {
				fileview.add(createFileButton(files.get(i)));
			}
		}

		// bottom buttons
		buttons.add(createBackButton());

		JButton newFolder = new JButton("New folder");
		newFolder.addActionListener(e -> {
			String input = JOptionPane.showInputDialog("Name of folder:");
			if (input == null) {
			} else {
				new File(currentFolder + "/" + input).mkdirs();
				currentFrame.dispose();
				createFolderWindow(800, 800);
			}
		});
		newFolder.setFont(buttonfont);

		JButton removeFolder = new JButton("Remove file");
		removeFolder.addActionListener(e -> {
			if (!removalMode) {
				removeFolder.setBackground(Color.RED);
				removalMode = true;
			} else {
				removeFolder.setBackground(Color.ORANGE);
				removalMode = false;
			}
		});
		removeFolder.setFont(buttonfont);

		JButton newCardSet = new JButton("New Cardset");
		newCardSet.addActionListener(e -> {
			String title = JOptionPane.showInputDialog("Enter new set title:");
			currentFrame.dispose();
			createNewSet(600, 600, title);
		});
		newCardSet.setFont(buttonfont);

		buttons.add(newFolder);
		buttons.add(removeFolder);
		buttons.add(newCardSet);

		////////////////////////////////////////////

		// Add all components
		pane.add(fileview, BorderLayout.NORTH);
		pane.add(buttons, BorderLayout.SOUTH);

		// Finishing touches
		currentFrame.pack();
		currentFrame.setSize(width, height);
		currentFrame.setVisible(true);
		currentFrame.setLocationRelativeTo(null);
	}

	private void createFlashCardWindow(String title, int i) {
		// Setup the window
		currentFrame = new JFrame(title);
		currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = currentFrame.getContentPane();

		// Create panes and fonts
		buttonfont = new Font("Verdana", Font.PLAIN, 20);
		cardfont = new Font("Verdana", Font.PLAIN, 25);
		onQuestionSide = true;

		// Create relevant panels
		JPanel cardview = new JPanel();
		cardview.setLayout(new BorderLayout());
		JPanel buttons = new JPanel();

		///////////////////////////////////////////

		// Create and add components
		cards = new FlashCards("");
		cards.load(currentFolder + "/" + title, currentFolder);

		if (cards.isCardSet()) {
			JTextArea card = createCardViewer(i);
			cardview.add(card, BorderLayout.CENTER);
			buttons.add(createFlipButton(card));
			buttons.add(createLastCardButton(card));
			buttons.add(createNextCardButton(card));
			JButton button = new JButton("Back");
			button.addActionListener(e -> {
				currentFrame.dispose();
				createFolderWindow(800, 800);
			});
			button.setFont(buttonfont);
			buttons.add(button);
		} else {
			JLabel label = new JLabel("Not a valid cardset");
			label.setFont(cardfont);
			cardview.add(label);
			buttons.add(createBackButton());
		}

		///////////////////////////////////////////

		// Add all components
		pane.add(cardview, BorderLayout.CENTER);
		pane.add(buttons, BorderLayout.SOUTH);

		// Finishing touches
		currentFrame.pack();
		currentFrame.setSize(600, 600);
		currentFrame.setVisible(true);
		currentFrame.setLocationRelativeTo(null);

	}

	private void createNewSet(int width, int height, String title) {		
		
		currentFrame = new JFrame(title);
		currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = currentFrame.getContentPane();

		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		JPanel buttons = new JPanel();
		cards = new FlashCards(title);
		panel.add(Box.createVerticalGlue());
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		ArrayList<newCard> newCards = new ArrayList<newCard>();

		// Buttons
		
		JButton newCardbutton = new JButton("Add new card");
		newCardbutton.addActionListener(e -> {
			newCards.add(new newCard(panel));
			currentFrame.revalidate();
			currentFrame.repaint();
		});
		
		JButton save = new JButton("Save Set");
		save.addActionListener(e -> {
			for (newCard card : newCards) {
				cards.add(card.getQuestion(), card.getAnswer());
			}
			cards.save(title, currentFolder);
			currentFrame.dispose();
			createFolderWindow(800, 800);
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> {
			if ((JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "ERROR",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
				currentFrame.dispose();
				createFolderWindow(800, 800);
			}	
		});
		
		buttons.add(newCardbutton);
		buttons.add(save);
		buttons.add(cancel);
		
		// Add panels
		
		pane.add(scrollPane, BorderLayout.NORTH);
		pane.add(buttons, BorderLayout.SOUTH);
		
		// Finishing touches
		currentFrame.pack();
		currentFrame.setSize(width, height);
		currentFrame.setVisible(true);
		currentFrame.setLocationRelativeTo(null);
	}
	

	private ArrayList<String> readFolder(String folder) {
		Set<String> folders = Stream.of(new File(folder).listFiles()).filter(file -> file.isDirectory())
				.map(File::getName).collect(Collectors.toSet());
		ArrayList<String> foldernames = new ArrayList<String>();
		if (folders.size() != 0) {
			for (Object s : folders.toArray()) {
				if (!s.equals(".settings") && !s.equals("bin") && !s.equals("src")) {
					foldernames.add(s.toString());
				}
			}
		}
		return foldernames;
	}

	private ArrayList<String> readFiles(String folder) {
		Set<String> folders = Stream.of(new File(folder).listFiles()).filter(file -> !file.isDirectory())
				.map(File::getName).collect(Collectors.toSet());
		ArrayList<String> foldernames = new ArrayList<String>();
		String[] temp;
		for (Object s : folders.toArray()) {
			if (!s.equals(".classpath") && !s.equals(".project")) {
				temp = ((String) s).split(".txt");
				foldernames.add(temp[0]);
			}
		}
		return foldernames;
	}

	private JTextArea createCardViewer(int i) {
		JTextArea text = new JTextArea(cards.getQuestion(i));
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setFont(cardfont);
		currentCard = i;
		return text;
	}

	private JButton createNextCardButton(JTextArea card) {
		JButton button = new JButton("Next Card");
		button.addActionListener(e -> {
			if (cards.getSize() <= currentCard + 1) {
				JOptionPane.showMessageDialog(null, "No cards left!", "", JOptionPane.PLAIN_MESSAGE);
			} else {
				currentCard++;
				card.setText(cards.getQuestion(currentCard));
				onQuestionSide = true;
			}
		});
		button.setFont(buttonfont);
		return button;
	}

	private JButton createLastCardButton(JTextArea card) {
		JButton button = new JButton("Last Card");
		button.addActionListener(e -> {
			if (currentCard - 1 >= 0) {
				currentCard--;
				card.setText(cards.getQuestion(currentCard));
				onQuestionSide = true;
			}
		});
		button.setFont(buttonfont);
		return button;
	}

	private JButton createFlipButton(JTextArea card) {
		JButton flip = new JButton("Flip");
		flip.addActionListener(e -> {
			if (onQuestionSide) {
				cards.getAnswer(currentCard);
				if (cards.getAnswer(currentCard).contains("#")) {
					String[] answer = cards.getAnswer(currentCard).split("#");
					card.setText("");
					for (int i = 0; i < answer.length; i++) {
						card.append("# " + answer[i] + "\n");
					}
				} else {
					card.setText(cards.getAnswer(currentCard));
				}
				onQuestionSide = false;
			} else {
				card.setText(cards.getQuestion(currentCard));
				onQuestionSide = true;
			}
		});
		flip.setFont(buttonfont);
		return flip;
	}

	private JButton createBackButton() {
		JButton back = new JButton("Go Back");
		back.addActionListener(e -> {
			String[] temp = currentFolder.split("/");
			temp[temp.length - 1] = "";
			String path = "";
			for (String s : temp) {
				path = path + "/" + s;
			}
			currentFolder = path;
			currentFrame.dispose();
			createFolderWindow(800, 800);
		});
		back.setFont(buttonfont);
		return back;
	}

	private JButton createFolderButton(String folder) {
		JButton folderbutton = new JButton(folder);
		folderbutton.addActionListener(e -> {
			if (removalMode) {
				File index = new File(currentFolder + "/" + folder);
				if (!index.delete()) {
					JOptionPane.showMessageDialog(null, "Directory is not empty!", "ERROR", JOptionPane.PLAIN_MESSAGE);
				}
			} else {
				currentFolder = currentFolder + "/" + folder;
			}
			currentFrame.dispose();
			createFolderWindow(800, 800);
		});
		return folderbutton;
	}

	private JButton createFileButton(String file) {
		JButton filebutton = new JButton(file);
		filebutton.addActionListener(e -> {
			if (removalMode) {
				if (JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this cardset?", "ERROR",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					File index = new File(currentFolder + "/" + file + ".txt");
					System.out.println(index.delete());
					System.out.println(currentFolder + "/" + file + ".txt");
					currentFrame.dispose();
					createFolderWindow(800, 800);
				}
			} else {
				currentFrame.dispose();
				createFlashCardWindow(file, 0);
			}
		});
		return filebutton;
	}

}
