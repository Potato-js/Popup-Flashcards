package com.weeaboo.flashcards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardManager {
    private List<Flashcard> flashcards;

    public FlashcardManager() {
        flashcards = new ArrayList<>();
//        loadFlashcards();
    }

//    private void loadFlashcards() {
//        flashcards.add(new Flashcard("What is the capital of France?", "Paris"));
//        flashcards.add(new Flashcard("What is 2 + 2?", "4"));
//        flashcards.add(new Flashcard("Who wrote 'Hamlet'?", "Shakespeare"));
//    }

    public Flashcard getRandomFlashcard() {
        Collections.shuffle(flashcards);
        return flashcards.get(0);
    }

    public void addFlashcard(String question, String answer) {
        flashcards.add(new Flashcard(question, answer));
    }

    public List<Flashcard> getAllFlashcards() {
        return flashcards;
    }
}
