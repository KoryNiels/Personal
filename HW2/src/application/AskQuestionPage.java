package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AskQuestionPage {
		private final DatabaseHelper databaseHelper;
		private Questions qL;

	    public AskQuestionPage(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	        qL = new Questions(databaseHelper);
	    }
	    
	    public void show( Stage primaryStage, User user) {
	    	VBox layout = new VBox();
		    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		    
		    Label title = new Label("Ask a Question");
		    
		    TextField questionField = new TextField();
		    questionField.setPromptText("Title");
		    
		    TextField bodyField = new TextField();
		    bodyField.setPromptText("Explain a little bit more...");
		    
		    Button enterButton = new Button("Submit");
		    
		    Label errLabel = new Label("");
		    errLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		    String emptyErr = "Field must not be empty";
		    String longErr = "Field must be less than 255 characters";
		    
		    enterButton.setOnAction(a -> {
		    	String question = questionField.getText();
		    	String body = bodyField.getText();
		    	
		    	if(question != "") {
		    		if(question.length() < 255 && body.length() < 255) {
		    			Question q = new Question(question, body, user.getUserName());
				    	try {
							qL.Add(q);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	
				    	new ForumPage(databaseHelper).show(primaryStage, user);
		    		}
		    		else {
		    			errLabel.setText(longErr);
		    		}
		    	}else {
		    		errLabel.setText(emptyErr);
		    	}
		    	
		    	
		    });
		    
		    layout.getChildren().addAll(title, questionField, bodyField, errLabel, enterButton);
		    Scene askScene = new Scene(layout, 800, 400);

		    // Set the scene to primary stage
		    primaryStage.setScene(askScene);
		    primaryStage.setTitle("Ask a Question");
	    }
}

