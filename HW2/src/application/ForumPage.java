package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.beans.value.*;

public class ForumPage {
	private final DatabaseHelper databaseHelper;
	private Questions qL;
	private Answers aL;

    public ForumPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        qL = new Questions(databaseHelper);
        aL = new Answers(databaseHelper);
    }
    
    boolean showAll = true;
    boolean showRead = true;
    String resAns = "";
    
    public void show( Stage primaryStage, User user) {
    	// Left side of UI
    	VBox left = new VBox();
	    left.setStyle("-fx-alignment: center-left; -fx-padding: 20;");
	    
	    // Text Field for searching questions
	    TextField searchBar = new TextField();
	    searchBar.setPromptText("Search");
	    
	    // CheckBox for including resolved answers or not, on by default
	    CheckBox includeRes = new CheckBox("Include Resolved Answers?");
	    includeRes.setSelected(true);
	    
	    // CheckBox for including resolved answers or not, on by default
	    CheckBox onlyMine = new CheckBox("Show only your Answers?");
	    onlyMine.setSelected(false);
	    
	    // Button for searching with desired parameters
	    Button searchButton = new Button("Search");
	    searchButton.setMaxWidth(Double.MAX_VALUE);
	    
	    // Scroll-able list for seeing questions
	    ListView<String> list = new ListView<String>();
	    ObservableList<String> items = FXCollections.observableArrayList(qL.getList("",includeRes.isSelected(), onlyMine.isSelected(), user.getUserName()));
	    list.setItems(items);
	    
	    // Label for showing error messages
	    Label errorMessage = new Label();
	    errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    errorMessage.setVisible(false);
	    errorMessage.setManaged(false);
	    
	    left.getChildren().addAll(searchBar, includeRes, onlyMine, searchButton, list, errorMessage);
	    
	    
	    // Right side of UI
	    VBox right = new VBox();
	    right.setStyle("-fx-alignment: center-left; -fx-padding: 20;");
	    
	    // Right Row 1
	    HBox row1 = new HBox();
	    row1.setStyle("-fx-alignment: center;");
	    Region region = new Region();
	    HBox.setHgrow(region, Priority.ALWAYS);
	    
	    // Label for displaying question title
	    Label questionTitle = new Label();
	    questionTitle.setStyle("-fx-alignment: center; -fx-font-size: 18; -fx-font-weight: bold");
	    
	    // Button for adding questions
	    Button addQuestion = new Button("Add Question");
	    addQuestion.setOnAction(a -> {
	    	new AskQuestionPage(databaseHelper).show(primaryStage, user);
	    });
	    
	    row1.getChildren().addAll(questionTitle, region, addQuestion);
	    
	    // Right Row 2
	    HBox row2 = new HBox();
	    row2.setStyle("-fx-alignment: center;");
	    Region region2 = new Region();
	    HBox.setHgrow(region2, Priority.ALWAYS);
	    
	    // Label for displaying question asker
	    Label userTitle = new Label();
	    userTitle.setStyle("-fx-font-style: italic");
	    
	    // Button for marking question resolved
	    Button markResolved = new Button("Mark Resolved");
	    
	    row2.getChildren().addAll(userTitle, region2, markResolved);
	    
	    // Label for displaying question body
	    Text bodyText = new Text();
	    bodyText.setStyle(" -fx-font-size: 16;");
	    
	    // Right Row 3
	    HBox row3 = new HBox();
	    row3.setStyle("-fx-alignment: center;");
	    
	    // Text field for answers
	    TextField responseField = new TextField();
	    HBox.setHgrow(responseField, Priority.ALWAYS);
	    responseField.setPromptText("Reply Here");
	    
	    // Button for submitting answer
	    Button addAnswer = new Button("Submit");
	    
	    row3.getChildren().addAll(responseField, addAnswer);
	    
	    // Right row 4
	    HBox row4 = new HBox();
	    row4.setStyle("-fx-alignment: center;");
	    Region region4 = new Region();
	    HBox.setHgrow(region4, Priority.ALWAYS);
	    
	    // Label for stating question is resolved
	    Label resolvedTitle = new Label();
	    
	    // Button for displaying all answers again
	    Button reAns = new Button("Display/Hide All Answers");
	    
	    row4.getChildren().addAll(resolvedTitle, region4, reAns);
	    row4.setVisible(false);
	    row4.setManaged(false);
	    
	    // List for displaying answers
	    ListView<String> ansList = new ListView<String>();
	    
	    // Right row 5
	    HBox row5 = new HBox();
	    row5.setStyle("-fx-alignment: center;");
	    Region region5 = new Region();
	    HBox.setHgrow(region5, Priority.ALWAYS);
	    
	    // Button for displaying only unread answers
	    Button showUnread = new Button();
	    showUnread.setText("Show Unread Messages");
	    showUnread.setOnAction(a -> {
	    	showRead = false;
	    	ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead)));
	    });
	    // Button for showing all answers
	    Button showReadAns = new Button();
	    showReadAns.setText("Show All Messages");
	    showReadAns.setOnAction(a -> {
	    	showRead = true;
	    	ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead)));
	    });
	    // Button for marking all answers as read
	    Button markRead = new Button();
	    markRead.setText("Mark All Read");
	    markRead.setOnAction(a -> {
	    	for(int i = 0; i < aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead).length; i++){
	    		if(user.getUserName().equals(databaseHelper.getQuestion(list.getSelectionModel().getSelectedItem().toString()).getUser())) {
    	    		databaseHelper.setARead(aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead)[i]);
    	    	}
	    	}
	    });
	    
	    row5.getChildren().addAll(showUnread, showReadAns, region5, markRead);
	    row5.setVisible(false);
	    row5.setManaged(false);
	    
	    // Assembling the right v box
	    right.getChildren().addAll(row1, row2, bodyText, row3, row4, ansList, row5);
	    
	    // UI elements hidden until needed
	    row2.setVisible(false);
	    row3.setVisible(false);
	    
	    // Button Logic
	    // Search Logic
	    searchButton.setOnAction(a -> {
	    	String regex = searchBar.getText();
	    	boolean incResolved = includeRes.isSelected();
	    	boolean isOnlyMine = onlyMine.isSelected();
	    	list.setItems(FXCollections.observableArrayList(qL.getList(regex, incResolved, isOnlyMine, user.getUserName())));
	    });
	    
	    // Resolving question logic
	    markResolved.setOnAction(a -> {
	    	String errResolve = "Must select an answer first";
	    	
	    	// Retrieve answer
	    	if(ansList.getSelectionModel().getSelectedItem() != null) {
	    		// Clear error message, if any
	    		errorMessage.setText("");
	    		errorMessage.setVisible(false);
	    		errorMessage.setManaged(false);
	    		
	    		resAns = ansList.getSelectionModel().getSelectedItem().toString();
		    	
		    	qL.setResolved(list.getSelectionModel().getSelectedItem().toString());
		    	aL.setResolved(resAns);
		    	
		    	// Hide Mark Resolved Button
		    	markResolved.setVisible(false);
		    	markResolved.setManaged(false);
		    	
		    	// Hide responseField and responseButton
		    	row3.setVisible(false);
		    	row3.setManaged(false);
		    	
		    	// State user who resolved question
		    	resolvedTitle.setText("Resolved by "+ databaseHelper.getUser(resAns)+".");
		    	
		    	// Make resolved bar visible
		    	row4.setVisible(true);
		    	row4.setManaged(true);
		    	
		    	// Show only resolved answer
		    	showAll = false;
		    	ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),resAns, showRead)));
	    	}else {
	    		errorMessage.setText(errResolve);
	    		errorMessage.setVisible(true);
	    		errorMessage.setManaged(true);
	    	}
	    	
	    });
	    
	    // Display or hide other answers
	    reAns.setOnAction(a -> {
	    	showAll = !showAll;
	    	if(showAll) {
	    		ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead)));
	    	}else {
	    		ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),resAns, showRead)));
	    	}
	    });
	    
	    // Logic for displaying pages upon selecting question on the left
	    list.getSelectionModel().selectedItemProperty().addListener(
		    	new ChangeListener<String>() {
		    	    public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
		    	    	if(list.getSelectionModel().getSelectedItem() != null) {
		    	    		row2.setVisible(true);
		    	    	    row3.setVisible(true);
		    	    	    // Get selected question
		    	    		String q = list.getSelectionModel().getSelectedItem().toString();
				    		Question dispQ = databaseHelper.getQuestion(q);
					    	
				    		// Display question attributes
					    	questionTitle.setText(dispQ.getQuestion());
					    	userTitle.setText("Posted by "+dispQ.getUser());
					    	bodyText.setText(dispQ.getBody());
					    	responseField.setText("");
					    	
					    	// Check if the question has already been resolved
					    	isResolved(q, markResolved, row3, row4, list, ansList, resolvedTitle);
					    	
					    	// If the user posted the question they can choose to mark it resolved and filter their replies by read status
					    	if(user.getUserName().equals(dispQ.getUser())) {
					    		if(!qL.isResolved(q)) {
					    			markResolved.setVisible(true);
						    		markResolved.setManaged(true);
					    		}
					    		row5.setVisible(true);
					    		row5.setManaged(true);
					    	}else {
					    		row5.setVisible(false);
					    	    row5.setManaged(false);
					    		markResolved.setVisible(false);
					    		markResolved.setManaged(false);
					    	}
					    	
					    	// Get the list of answers
					    	String[] aList = aL.getList(q,"", showRead);
					    	
					    	ObservableList<String> ansItems =FXCollections.observableArrayList(aList);
					    	if(showAll) {
					    		ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead)));
					    	}else {
					    		ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),resAns, showRead)));
					    	}
		        	}
		        }
		   });
	   
	    // Answer Logic
	    addAnswer.setOnAction(a -> {
	    	String errEmptyR = "Answer must not be empty";
	    	String errLongR = "Answer must be less than 255 chars";
	    	if(responseField.getText() != "") {
	    		if(responseField.getText().length() < 255) {
	    			// Clear error message, if there is one
		    		errorMessage.setText("");
		    		errorMessage.setVisible(false);
		    		errorMessage.setManaged(false);
		    		
		    		// Create a new answer
		    		Answer newAnswer = new Answer(list.getSelectionModel().getSelectedItem().toString(), responseField.getText(), user.getUserName());
			    	try {
						aL.Add(newAnswer);
						responseField.setText("");
						ansList.setItems(FXCollections.observableArrayList(aL.getList(list.getSelectionModel().getSelectedItem().toString(),"", showRead)));
					} catch (SQLException e) {
						e.printStackTrace();
					}
	    		}else {
	    			errorMessage.setText(errLongR);
		    		errorMessage.setVisible(true);
		    		errorMessage.setManaged(true);
	    		}
	    		
	    	}else {
	    		errorMessage.setText(errEmptyR);
	    		errorMessage.setVisible(true);
	    		errorMessage.setManaged(true);
	    	}
	    	
	    	
	    });
	    
	    
	    // Putting the v boxes into gridPane columns
	    GridPane root = new GridPane();
	    root.add(left, 0, 0);
	    root.add(right, 1, 0);
	    
	    // Restrict Left column to 30%
	    ColumnConstraints cc1 = new ColumnConstraints();
	    cc1.setPercentWidth(30);
	    cc1.setHgrow(Priority.ALWAYS);
	    root.getColumnConstraints().add(cc1);
	    
	    // Restrict Right column to 70%
	    ColumnConstraints cc2 = new ColumnConstraints();
	    cc2.setPercentWidth(70);
	    cc2.setHgrow(Priority.ALWAYS);
	    root.getColumnConstraints().add(cc2);
	    
	    // Allow resizing vertically
	    RowConstraints rc = new RowConstraints();
	    rc.setVgrow(Priority.ALWAYS);
	    root.getRowConstraints().add(rc);
	    
	    Scene forumScene = new Scene(root, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(forumScene);
	    primaryStage.setTitle("Forum Page");
    }
    
    // Helper method to figure out if a question has been resolved
    public void isResolved(String question,Button mR, HBox r3, HBox r4, ListView<String> list, ListView<String> ansList, Label resolvedTitle) {
    	if(qL.isResolved(question)) {
    		// Retrieve answer
	    	resAns = aL.getResolved(question).getAnswer();
	    	
	    	// Hide Mark Resolved Button
	    	mR.setVisible(false);
	    	mR.setManaged(false);
	    	
	    	// Hide responseField and responseButton
	    	r3.setVisible(false);
	    	r3.setManaged(false);
	    	
	    	// State user who resolved question
	    	resolvedTitle.setText("Resolved by "+ databaseHelper.getUser(resAns)+".");
	    	
	    	// Make resolved bar visible
	    	r4.setVisible(true);
	    	r4.setManaged(true);
	    	 
	    	// Show only resolved answer
	    	showAll = false;
    	}else {
	    	// Show Mark Resolved Button
	    	mR.setVisible(true);
	    	mR.setManaged(true);
	    	
	    	// Show responseField and responseButton
	    	r3.setVisible(true);
	    	r3.setManaged(true);
	    	
	    	// State user who resolved question
	    	resolvedTitle.setText("Resolved by "+ databaseHelper.getUser(resAns)+".");
	    	
	    	// Make resolved bar visible
	    	r4.setVisible(false);
	    	r4.setManaged(false);
	    	 
	    	// Show only resolved answer
	    	showAll = true;
    	}
    }
}
