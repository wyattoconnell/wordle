package edu.virginia.cs.gui;

import edu.virginia.cs.wordle.IllegalWordException;
import edu.virginia.cs.wordle.LetterResult;
import edu.virginia.cs.wordle.Wordle;
import edu.virginia.cs.wordle.WordleImplementation;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;


public class WordleController {
    Wordle wordle;

    private int row;
    private int col;

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private GridPane grid;
    @FXML
    private Label welcomeText;

    @FXML
    private Label wordleTitle;

    @FXML
    private Label error;
    static String gameMessage;
    static boolean won;
    static String prevword;
    @FXML
    private Label endText;
    @FXML
    private Label wordleAnswer;


    public WordleController() {
        wordle = new WordleImplementation();
        row = 0;
        col = 0;
        //System.out.println(wordle.getAnswer());
    }

    @FXML
    protected void initialize() {
        initGridPane();
        try{
            endText.setText(gameMessage);
            wordleAnswer.setText(prevword);
        }catch(Exception e){

        }
        prevword = wordle.getAnswer();
    }

    @FXML
    protected void onLetterOrDelPressed(KeyEvent event) {
        error.setText(" ");
        //System.out.println("" + event.getCode());
        if (event.getCode() == KeyCode.BACK_SPACE) {
            deleteTextOfNode(row,col);
            decreaseCol();
            giveFocusToNodeFromRowAndCol(row, col);
        }
        else if (event.getCode() == KeyCode.ENTER) {
            //handle enter logic
            try {
                LetterResult[] result = wordle.submitGuess(getInputWord(row, col));
                changeColors(result);
                row++;
                col = 0;
                if(wordle.isWin()){
                    gameMessage = "YOU WIN!!!";
                    endGame(event);
                }
                if(wordle.isLoss()){
                    gameMessage = "You Lost";
                    endGame(event);

                }

            }
            catch( IllegalWordException e){
                error.setText("INVALID WORD");
            }

        }
        else if (event.getCode().isLetterKey()) {
                setTextOfNode(row,col, event.getText());
                incrementRowAndCol();
                giveFocusToNodeFromRowAndCol(row, col);
        }
    }

    private String getInputWord(int row, int col){
        String input = "";
        for(Node node : grid.getChildren()) {
            if(grid.getRowIndex(node) == row && grid.getColumnIndex(node) <= col) {
                input = input.concat(((TextField)node).getText());
            }
        }
        return input;
    }

    private void changeColors(LetterResult[] result){
        for (int i = 0; i < result.length; i++){
            for(Node node : grid.getChildren()) {
                if(grid.getRowIndex(node) == row && grid.getColumnIndex(node) == i) {
                    if (result[i].equals(LetterResult.GRAY)){
                        node.setStyle("-fx-background-color:   gray; -fx-control-inner-background: black");}
                    else if (result[i].equals(LetterResult.GREEN)){
                        node.setStyle("-fx-background-color:   green; -fx-control-inner-background: black");}
                    else if (result[i].equals(LetterResult.YELLOW)){
                        node.setStyle("-fx-background-color:   #ECF000; -fx-control-inner-background: black");}
                }
            }
        }
    }

    private void deleteTextOfNode(int row, int col) {
        ObservableList<Node> children = grid.getChildren();
        for(Node node : children) {
            if(grid.getRowIndex(node) == row && grid.getColumnIndex(node) == col) {
                ((TextField)node).deleteText(0,((TextField)node).getText().length());
            }
        }
    }

    private void setTextOfNode(int row, int col, String letter) {
        ObservableList<Node> children = grid.getChildren();
        for(Node node : children) {
            if(grid.getRowIndex(node) == row && grid.getColumnIndex(node) == col) {
                ((TextField)node).setText(letter.toUpperCase());
            }
        }
    }

    private void giveFocusToNodeFromRowAndCol(int row, int col) {
        ObservableList<Node> children = grid.getChildren();
        for(Node node : children) {
            if(grid.getRowIndex(node) == row && grid.getColumnIndex(node) == col) {
                node.requestFocus();
                break;
            }
        }
    }

    @FXML
    protected void initGridPane() {
        //setting up wordle stuff
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                TextField box = new TextField();
                box.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
                    String newText = change.getControlNewText();
                    if (newText.length() > 1) {
                        return null;
                    } else {
                        return change;
                    }
                }));
                box.setAlignment(Pos.CENTER);
                box.setStyle("-fx-text-fill: white; -fx-control-inner-background: black");
                box.setFont(Font.font("Helvetica Neue", FontWeight.BOLD, FontPosture.REGULAR, 36));
                box.setOnKeyPressed(e -> onLetterOrDelPressed(e));
                box.setEditable(false);
                grid.add(box, i, j);
            }
        }
    }

    protected void incrementRowAndCol() {
        if (col != 4) {
            col++;
        }
    }

    protected void decreaseCol() {
        if (col != 0) {
            col--;
        }
    }

    private void endGame(Event event) {

        try {
            switchToEndScene(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    private void switchToWordleGame(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleController.class.getResource("wordle-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        row = 0;
        col = 0;
        wordle = new WordleImplementation();
        initGridPane();

    }
    @FXML
    private void switchToEndScene(Event event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApplication.class.getResource("wordle-end.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        grid.setVisible(false);
    }

    @FXML
    private void exitApplication(ActionEvent event){
        System.exit(0);
    }
    @FXML
    private void hideTable(ActionEvent event){
        grid.setVisible(false);
    }

}