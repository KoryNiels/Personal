package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * The Answers class represents a set of helper methods for the answer class
 * It contains the methods for adding an answer to the system, getting a list of all answers for a question in the system, etc.
 */

public class Answers {
	private final DatabaseHelper databaseHelper;

    public Answers(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void Add(Answer answer) throws SQLException{
		databaseHelper.addAnswer(answer);
	}
    
    // Returns a list of all answers to a single question
    public String[] getList(String question, String regex, boolean readOnly) {
		String[] subset = new String[1];
		String[] searched = new String[1];
		try {  
			// Creates an array of the perfect size
			subset = new String[databaseHelper.numAnswers(question)];
			databaseHelper.listAnswers(subset, question);
			
			// Filling the array
			if(regex != "" || readOnly == false) {
				int iter = 0;
				// Creating the empty array first
				for(int i = 0; i < subset.length; i++) {
					// If a user is searching for a particular phrase return only those answers containing it
					if(subset[i].contains(regex)) {
						// If a user wants to see only messages they have not read, return only those
						if(readOnly == false) {
							if(!databaseHelper.isARead(subset[i])) {
								iter++;
							}
						}else {
							iter++;
						}
						
					}
				}
				searched = new String[iter];
				iter = 0;
				
				for(int i = 0; i < subset.length; i++) {
					if(subset[i].contains(regex)) {
						// If a user wants to see only messages they have not read, return only those
						if(readOnly == false) {
							if(!databaseHelper.isARead(subset[i])) {
								searched[iter]=subset[i];
								iter++;
							}
						}else {
							searched[iter]=subset[i];
							iter++;
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
    
    public void setResolved(String answer) {
		databaseHelper.setAResolved(answer);
	}
    
    public boolean isResolved(String answer) {
		return databaseHelper.isAResolved(answer);
	}
    
    public Answer getResolved(String question) {
    	return databaseHelper.getResAns(question);
    }

}
