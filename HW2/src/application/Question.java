package application;

/**
 * The Question class represents a question in the system.
 * It contains the question's details such as the text, body, who created it, if its been resolved and its category (to be implemented later)
 */

public class Question {
	private String question;
	private String body;
	private String userID;
	private boolean isResolved;
	private enum Category{
		GENERAL,
		HOMEWORK,
		TEST
	}
	private Category category;
	
	// Constructor for initializing a question with its text, body, and who created it
	public Question(String question, String bodyText, String userID) {
		this.question = question;
		this.body = bodyText;
		this.userID = userID;
		this.isResolved = false;
		this.category = Category.GENERAL;
	}
	
	// Constructor for initializing a question with its text, body, who created it, and if its been resolved
	public Question(String question, String bodyText, String userID, boolean resolve) {
		this.question = question;
		this.body = bodyText;
		this.userID = userID;
		this.isResolved = resolve;
		this.category = Category.GENERAL;
	}
	
	// Constructor for initializing a question with its text, body, and who created it, and its category (To be implemented later)
	public Question(String question, String bodyText, String userID, Category cat) {
		this.question = question;
		this.body = bodyText;
		this.userID = userID;
		this.isResolved = false;
		this.category = cat;
	}
	
	// Helper methods
	public void setResolved() {
		this.isResolved = true;
	}
	
	public String getQuestion() { return question; };
	public String getBody() { return body; };
	public String getUser() { return userID; };
	public boolean getResolved() { return isResolved; };
	public String getCategory() { return category.toString(); };
}
