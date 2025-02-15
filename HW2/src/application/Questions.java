package application;
import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * The Question class represents a set of helper methods for the question class
 * It contains the methods for adding a question to the system, getting a list of all questions in the system, etc.
 */

public class Questions {

	private final DatabaseHelper databaseHelper;

    public Questions(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
   
    // Adds a question to the system
	public void Add(Question question) throws SQLException {
		databaseHelper.addQuestion(question);
	}
	
	// Returns the number of questions in the system
	public int getNum(Question question) {
		return databaseHelper.getNum(question.getQuestion());
	}
	
	// Returns a list of the question titles
	public String[] getList(String regex, boolean incResolved, boolean onlyMine, String user) {
		String[] subset = new String[1];
		String[] searched = new String[1];
		try {
			// Creates a new array the exact size of how many questions there are
			subset = new String[databaseHelper.numQuestions()];
			databaseHelper.listQuestions(subset);
			
			// Filling the array
			if(regex != "" || incResolved == false || onlyMine == true) {
				int iter = 0;
				// The first loop creates the properly sized array regardless of filters
				for(int i = 0; i < subset.length; i++) {
					// If a user is searching for a phrase, return only the questions containing that phrase
					if(subset[i].toLowerCase().contains(regex.toLowerCase())) {
						// If the user only wants to see resolved questions, return only those
						if(incResolved) {
							// If the user only wants to see their questions, return only those
							if(onlyMine == false) {
								iter++;
							}else {
								if(databaseHelper.getQuestion(subset[i]).getUser().equals(user)) {
									iter++;
								}
							}
							
						}else {
							if(isResolved(subset[i])==false) {
								if(onlyMine == false) {
									iter++;
								}else {
									if(databaseHelper.getQuestion(subset[i]).getUser().equals(user)) {
										iter++;
									}
								}
							}
						}
						
					}
				}
				searched = new String[iter];
				iter = 0;
				
				for(int i = 0; i < subset.length; i++) {
					// If a user is searching for a phrase, return only the questions containing that phrase
					if(subset[i].toLowerCase().contains(regex.toLowerCase())) {
						// If the user only wants to see resolved questions, return only those
						if(incResolved) {
							// If the user only wants to see their questions return only those
							if(onlyMine == false) {
								searched[iter]=subset[i];
								iter++;
							}else {
								if(databaseHelper.getQuestion(subset[i]).getUser().equals(user)) {
									searched[iter]=subset[i];
									iter++;
								}
							}
						}
						else {
							if(isResolved(subset[i])==false) {
								if(onlyMine == false) {
									searched[iter]=subset[i];
									iter++;
								}else {
									if(databaseHelper.getQuestion(subset[i]).getUser().equals(user)) {
										searched[iter]=subset[i];
										iter++;
									}
								}
							}
						}
						
					}
				}
				return searched;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return subset;
	}
	
	public void setResolved(String question) {
		databaseHelper.setQResolved(question);
	}
	
	public boolean isResolved(String question) {
		return databaseHelper.isQResolved(question);
	}
}
