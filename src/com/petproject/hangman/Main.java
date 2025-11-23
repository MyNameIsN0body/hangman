package com.petproject.hangman;

import java.io.*;
import java.util.*;

public class Main {
    private static final int MAX_MISTAKES = 7;
    private static final char HIDDEN_LETTER_SYMBOL = '□';

    public static void main(String[] args) {
        try {
            runStartMenu();
        } catch (RuntimeException e) {
            System.out.println("Unhandled error: " + e.getClass().getSimpleName());
            System.out.println(e.getMessage());
            System.out.println("Program stopped.");
        }
    }

    private static void runStartMenu() {
        Pictures.printStartBanner();
        System.out.print(
                """
                        Игра 'Виселица'
                        Спасите человечка от виселицы, угадав загаданное слово по буквам \
                        быстрее, чем закончатся ваши попытки!!!
                        
                        """
        );
        while (userWantsToPlay()) {
            startGame();
        }
    }

    private static boolean isGameOver(int currentMistakeCount, int hiddenLetterCount) {
        return isLose(currentMistakeCount) || isWin(hiddenLetterCount);
    }

    private static boolean isWin(int hiddenLetterCount) {
        return hiddenLetterCount == 0;
    }

    private static boolean isLose(int currentMistakeCount) {
        return currentMistakeCount >= MAX_MISTAKES;
    }

    private static void startGame() {
        List<Character> wrongLetters = new ArrayList<>();
        List<String> dictionary = loadDictionary();
        String secretWord = getRandomWord(dictionary);
        char[] hiddenWordLetters = initHiddenWord(secretWord);
        int hiddenLetterCount = getHiddenLetterCount(hiddenWordLetters);
        int currentMistakeCount = 0;

        while (!isGameOver(currentMistakeCount, hiddenLetterCount)) {
            printGameState(hiddenWordLetters, currentMistakeCount, wrongLetters);
            currentMistakeCount = processGuess(secretWord, hiddenWordLetters, wrongLetters, currentMistakeCount);

            if (isWin(getHiddenLetterCount(hiddenWordLetters))) {
                System.out.println("\uD83C\uDFC6 Загаданное слово: " + secretWord);
                Pictures.printWinBanner();
                break;
            }
            if (isLose(currentMistakeCount)) {
                System.out.println("\uD83D\uDC80 Загаданное слово: " + secretWord);
                Pictures.printLoseBanner();
                break;
            }
        }
    }

    private static void printGameState(char[] hiddenWordLetters, int currentMistakeCount, List<Character> wrongLetters) {
        Pictures.printHangmanStage(currentMistakeCount);
        printWrongLetters(wrongLetters);
        printWordLetters(hiddenWordLetters);
    }

    private static int processGuess(String secretWord, char[] hiddenWordLetters, List<Character> wrongLetters, int currentMistakeCount) {
        char playerLitter;

        playerLitter = readUserLetter();
        if (secretWord.contains(String.valueOf(playerLitter))) {
            if ((containsChar(hiddenWordLetters, playerLitter))) {
                printLetterAlreadyUsed();
            } else {
                openLetter(secretWord, hiddenWordLetters, playerLitter);
            }

        } else {
            if ((containsChar(wrongLetters, playerLitter))) {
                printCurrentMistakeCount(currentMistakeCount);
                printLetterAlreadyUsed();
                return currentMistakeCount;
            }
            wrongLetters.add(playerLitter);
            ++currentMistakeCount;
        }
        printCurrentMistakeCount(currentMistakeCount);
        return currentMistakeCount;

    }

    private static boolean containsChar(char[] array, char playerLitter) {
        for (char litter : array) {
            if (litter == playerLitter) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsChar(List<Character> list, char playerLitter) {
        return list.contains(playerLitter);
    }

    private static void printLetterAlreadyUsed() {
        System.out.println("\uD83D\uDEAB Такая буква уже была использована, попробуйте новую");
    }

    private static void printCurrentMistakeCount(int currentMistakeCount) {
        System.out.println("Ошибки: " + currentMistakeCount + "/" + MAX_MISTAKES);
    }

    private static void printWrongLetters(List<Character> wrongLetters) {
        System.out.print("\uD83D\uDCDD Список неудачных букв: ");
        for (char letter : wrongLetters) {
            System.out.print(letter + " ");
        }
        System.out.println(" ");
    }

    private static int getHiddenLetterCount(char[] hiddenWord) {
        int hiddenLetterCount = 0;
        for (char letter : hiddenWord) {
            if (letter == HIDDEN_LETTER_SYMBOL) {
                hiddenLetterCount++;
            }
        }
        return hiddenLetterCount;
    }

    private static void openLetter(String secretWord, char[] hiddenWord, char letter) {
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                hiddenWord[i] = letter;
            }
        }
    }

    private static void printWordLetters(char[] hiddenWord) {
        for (char letter : hiddenWord) {
            System.out.print(letter + " ");
        }
        System.out.println(" ");
    }

    private static char[] initHiddenWord(String secretWord) {
        char[] hiddenWord = new char[secretWord.length()];
        for (int i = 0; i < secretWord.length(); i++) {
            hiddenWord[i] = HIDDEN_LETTER_SYMBOL;
        }
        return hiddenWord;
    }

    private static boolean userWantsToPlay() {
        System.out.println("\uD83C\uDFAE Для начала игры нажмите Enter. \uD83C\uDFC3 Для выхода - любую другую клавишу и Enter.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = reader.readLine();

            if (input.isEmpty()) {
                return true;
            } else {
                System.out.println("\uD83D\uDCAB Ждем вас снова! \uD83D\uDC4B До свидания!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private static char readUserLetter() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        try {
            do {
                System.out.println("✏️ Введите русскую букву:");
                input = reader.readLine();
            } while (!isValidInput(input));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input.toLowerCase().charAt(0);
    }

    private static boolean isValidInput(String letter) {
        if (letter == null || letter.isEmpty()) {
            return false;
        }
        if (letter.length() > 1) {
            return false;
        }
        char character = letter.toLowerCase().charAt(0);
        if ((character < 'а' || character > 'я') && character != 'ё') {
            return false;
        }

        return true;
    }

    private static String getRandomWord(List<String> dictionary) {
        Random random = new Random();
        int randomIndex = random.nextInt(dictionary.size());
        return dictionary.get(randomIndex);
    }

    private static List<String> loadDictionary() {
        String fileName = "src/com/petproject/hangman/dictionary_words.txt";
        List<String> dictionaryWords = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            throw new RuntimeException("Dictionary file not found: " + file.getAbsolutePath());
        }

        if (file.length() == 0) {
            throw new RuntimeException("Dictionary file is empty: " + file.getAbsolutePath());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                dictionaryWords.add(line.trim());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading dictionary file: " + file.getAbsolutePath() +
                    ". Details: " + e.getMessage(), e);
        }
        if (dictionaryWords.isEmpty()) {
            throw new RuntimeException("No valid words loaded from: " + file.getAbsolutePath() +
                    ". Check file content and format.");
        }
        return dictionaryWords;
    }
}