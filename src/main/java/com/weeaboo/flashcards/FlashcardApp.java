package com.weeaboo.flashcards;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FlashcardApp extends Application {
    private FlashcardManager flashcardManager;
    private Timer timer;
    private boolean isRunning; // Tracks if the app is running (default is false)
    private boolean isFlashcardActive; // Tracks if a flashcard is currently active

    @Override
    public void start(Stage primaryStage) {
        flashcardManager = new FlashcardManager();
        timer = new Timer();
        isRunning = false; // Default to paused state
        isFlashcardActive = false;

        Label mainLabel = new Label("Flashcard App");
        mainLabel.setStyle("-fx-font-size: 16px;");

        Button startPauseButton = new Button("Start"); // Default to "Start" since the app is paused
        startPauseButton.setOnAction(event -> {
            if (isRunning) {
                pauseApp();
                startPauseButton.setText("Start");
            } else {
                startApp();
                startPauseButton.setText("Pause");
            }
        });

        Button addFlashcardButton = new Button("Add Flashcard");
        addFlashcardButton.setOnAction(event -> showAddFlashcardPopup());

        VBox root = new VBox(20, mainLabel, startPauseButton, addFlashcardButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Scene mainScene = new Scene(root, 400, 300);
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Flashcard App");
        primaryStage.show();

        // Prepare the flashcard pop-ups but do not start until "Start" is clicked
        prepareFlashcardPopups();
    }

    private void prepareFlashcardPopups() {
        Random random = new Random();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRunning && !isFlashcardActive) {
                    Platform.runLater(() -> showFlashcardPopup());
                }
            }
        }, 0, random.nextInt(10_000) + 5_000); // Random interval between 5 and 15 seconds
    }

    private void showFlashcardPopup() {
        isFlashcardActive = true; // Pause the timer logic while the flashcard is active

        Flashcard flashcard = flashcardManager.getRandomFlashcard();
        Image icon = new Image("icon.png");
        Stage flashcardStage = new Stage();

        flashcardStage.getIcons().add(icon);
        flashcardStage.initModality(Modality.APPLICATION_MODAL);
        flashcardStage.setTitle("Answer the Flashcard");
        flashcardStage.setWidth(400);
        flashcardStage.setHeight(200);
        flashcardStage.setOnCloseRequest(event -> event.consume()); // Prevent closing the flashcard

        Label questionLabel = new Label(flashcard.getQuestion());
        questionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        TextField answerField = new TextField();
        answerField.setPromptText("Enter your answer...");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            if (answerField.getText().trim().equalsIgnoreCase(flashcard.getAnswer())) {
                flashcardStage.close();
                isFlashcardActive = false; // Resume the timer after the flashcard is answered
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Incorrect Answer");
                alert.setHeaderText(null);
                alert.setContentText("Your answer is incorrect. Try again.");
                alert.showAndWait();
            }
        });

        VBox popupRoot = new VBox(10, questionLabel, answerField, submitButton);
        popupRoot.setAlignment(Pos.CENTER);
        popupRoot.setStyle("-fx-padding: 20; -fx-background-color: #f2f2f2;");

        Scene popupScene = new Scene(popupRoot);
        flashcardStage.setScene(popupScene);
        flashcardStage.show();
    }

    private void showAddFlashcardPopup() {
        Stage addFlashcardStage = new Stage();
        Image icon = new Image("icon.png");
        addFlashcardStage.getIcons().add(icon);
        addFlashcardStage.initModality(Modality.APPLICATION_MODAL);
        addFlashcardStage.setTitle("Add a Flashcard");

        Label questionLabel = new Label("Question:");
        TextField questionField = new TextField();
        questionField.setPromptText("Enter question");

        Label answerLabel = new Label("Answer:");
        TextField answerField = new TextField();
        answerField.setPromptText("Enter answer");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            String question = questionField.getText().trim();
            String answer = answerField.getText().trim();

            if (!question.isEmpty() && !answer.isEmpty()) {
                flashcardManager.addFlashcard(question, answer);
                addFlashcardStage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Flashcard Added");
                alert.setHeaderText(null);
                alert.setContentText("Flashcard added successfully!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Both fields must be filled out.");
                alert.showAndWait();
            }
        });

        VBox popupRoot = new VBox(10, questionLabel, questionField, answerLabel, answerField, saveButton);
        popupRoot.setAlignment(Pos.CENTER);
        popupRoot.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Scene popupScene = new Scene(popupRoot, 300, 250);
        addFlashcardStage.setScene(popupScene);
        addFlashcardStage.show();
    }

    private void startApp() {
        isRunning = true;
    }

    private void pauseApp() {
        isRunning = false;
    }

    @Override
    public void stop() {
        timer.cancel();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
