package databasePart1;
import java.sql.*;
import java.util.UUID;

import application.User;
import application.Question;
import application.Answer;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the questions table
	    String questTable = "CREATE TABLE IF NOT EXISTS cse360questions ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "question VARCHAR(255), "
				+ "body VARCHAR(255), "
				+ "userName VARCHAR(255), "
				+ "isResolved BOOLEAN DEFAULT FALSE, "
				+ "category VARCHAR(20))";
		statement.execute(questTable);
		
		// Create the questions table
	    String ansTable = "CREATE TABLE IF NOT EXISTS cse360answers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "answer VARCHAR(255), "
				+ "userName VARCHAR(255), "
				+ "isResolved BOOLEAN DEFAULT FALSE, "
				+ "isRead BOOLEAN DEFAULT FALSE, "
				+ "qInd INT)";
		statement.execute(ansTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	// QUESTION HELPER METHODS
	
	// Add question to the database
	public void addQuestion(Question question) throws SQLException {
		String insertQuest = "INSERT INTO cse360questions (question, body, userName, isResolved, category) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuest)) {
			pstmt.setString(1, question.getQuestion());
			pstmt.setString(2, question.getBody());
			pstmt.setString(3, question.getUser());
			pstmt.setBoolean(4, question.getResolved());
			pstmt.setString(5, question.getCategory());
			pstmt.executeUpdate();
		}
	}
	
	public void setQResolved(String question) {
		String query = "UPDATE cse360questions SET isResolved = ? WHERE question = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setBoolean(1, true);
			pstmt.setString(2, question);
			pstmt.executeUpdate();
		}	catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isQResolved(String question) {
		String query = "SELECT isResolved FROM cse360questions WHERE question = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, question);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getBoolean("isResolved");
			}
		}	catch (SQLException e) {
	        e.printStackTrace();
	    }
		
		return false;
	}
	
	// List number of users in the database
	public int numQuestions() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360questions";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count");
		}
		return 0;
	}

	// Return list of questions in the database
	public String[] listQuestions(String[] qList) throws SQLException {
		String query = "SELECT question FROM cse360questions";
		ResultSet rs = statement.executeQuery(query);
		System.out.println(rs.toString());
		//rs.first();
		int i = 0;
		while(rs.next()) {
			qList[i] = rs.getString(1);
			i++;
		}
		return qList;
	}
	
	public Question getQuestion(String question) {
	    String query = "SELECT * FROM cse360questions WHERE question = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, question);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	String body = rs.getString("body");
	        	String user = rs.getString("userName");
	        	boolean isResolved = rs.getBoolean("isResolved");
	        	//TODO IMPLEMENT CATEGORY
	        	String category = rs.getString("category");
	            Question q = new Question(question, body, user, isResolved);
	            return q;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no question exists or an error occurs
	}
	
	public int getNum(String question) {
	    String query = "SELECT id FROM cse360questions WHERE question = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, question);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	int val = rs.getInt("id");
	            return val;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; // If no user exists or an error occurs
	}
	
	//Answers helper methods
	
	// Add answer to the database
	public void addAnswer(Answer answer) throws SQLException {
		String insertQuest = "INSERT INTO cse360answers (answer, userName, isResolved, isRead, qInd) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuest)) {
			pstmt.setString(1, answer.getAnswer());
			System.out.println(answer.getAnswer());
			pstmt.setString(2, answer.getUser());
			pstmt.setBoolean(3, false);
			pstmt.setBoolean(4, false);
			pstmt.setInt(5, getNum(answer.getQInd()));
			pstmt.executeUpdate();
		}
	}
	
	public void setAResolved(String answer) {
		String query = "UPDATE cse360answers SET isResolved = ? WHERE answer = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setBoolean(1, true);
			pstmt.setString(2, answer);
			pstmt.executeUpdate();
		}	catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isAResolved(String answer) {
		String query = "SELECT isResolved FROM cse360answers WHERE answer = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getBoolean("isResolved");
			}
		}	catch (SQLException e) {
	        e.printStackTrace();
	    }
		
		return false;
	}
	
	public void setARead(String answer) {
		String query = "UPDATE cse360answers SET isRead = ? WHERE answer = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setBoolean(1, true);
			pstmt.setString(2, answer);
			pstmt.executeUpdate();
		}	catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isARead(String answer) {
		String query = "SELECT isRead FROM cse360answers WHERE answer = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getBoolean("isRead");
			}
		}	catch (SQLException e) {
	        e.printStackTrace();
	    }
		
		return false;
	}
	
	public Answer getResAns(String question) {
		String query = "SELECT * FROM cse360answers WHERE qInd = ? AND isResolved = true";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, getNum(question));
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String ans = rs.getString("answer");
				String user = rs.getString("userName");
				Answer a = new Answer(question, ans, user, true, true);
				return a;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// Return list of answers in the database
	public String[] listAnswers(String[] aList, String question) throws SQLException {
		String query = "SELECT answer FROM cse360answers WHERE qInd = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, getNum(question));
		       ResultSet rs = pstmt.executeQuery();
		        
		       int i = 0;
			while(rs.next()) {
				aList[i] = rs.getString(1);
				i++;
			}
		   } catch (SQLException e) {
	        e.printStackTrace();
	    }
		//rs.first();
		return aList;
	}
		
		// List number of users in the database
	public int numAnswers(String question) throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360answers WHERE qInd = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, getNum(question));
		       ResultSet rs = pstmt.executeQuery();
		        
		       if (rs.next()) {
					return rs.getInt("count");
		       }
		}
		return 0;
	}
	
	public String getUser(String answer) {
	    String query = "SELECT userName FROM cse360answers WHERE answer = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, answer);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	String val = rs.getString("userName");
	            return val;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
