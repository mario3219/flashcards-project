import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class newCard {

	private JTextField Question;
	private JTextField Answer;
	
	public newCard(JPanel panel) {
		Question = new JTextField();
		Answer = new JTextField();
		panel.add(new JLabel("Question:"));
		panel.add(Question);
		panel.add(new JLabel("Answer:"));
		panel.add(Answer);
		
		JButton button = new JButton("Clear");
		button.addActionListener(e -> {
			Question.setText("");
			Answer.setText("");
		});
		panel.add(button);
	
	}
	
	public String getQuestion() {
		return Question.getText();
	}
	
	public String getAnswer() {
		return Answer.getText();
	}
	
}
