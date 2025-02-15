package application;

/**
 * The Answer class represents an answer to a question in the system.
 * It contains the answer's details such as the text, who created it, if its been resolved or read, and which question its in response to.
 */

public class Answer {
	private String answer;
	private String user;
	private boolean isResolved;
	private boolean isRead;
	private String qIndex;
	
	//Constructor to initialize a new answer with only the question, text, and who created it
	public Answer(String question, String ans, String user) {
		this.answer = ans;
		this.user = user;
		this.isResolved = false;
		this.isRead = false;
		this.qIndex = question;
	}
	
	//Constructor to initialize a new answer with the question, text, who created it, and its resolved/read status
	public Answer(String question, String ans, String user, boolean isResolved, boolean isRead) {
		this.answer = ans;
		this.user = user;
		this.isResolved = isResolved;
		this.isRead = isRead;
		this.qIndex = question;
	}
	
	//Constructor to initialize a new answer with the question in form of its id, text, who created it, and its resolved/read status
	public Answer(int question, String ans, String user, boolean isResolved, boolean isRead) {
		this.answer = ans;
		this.user = user;
		this.isResolved = isResolved;
		this.isRead = isRead;
		this.qIndex = question+"";
	}
	
	// Helper methods
	public String getAnswer() { return this.answer; }
	public String getUser() { return this.user; }
	public String getQInd() { return this.qIndex; }
}
