package com.petproject.hangman;

import java.io.*;
import java.util.*;

public class Main {
    private static final int MAX_MISTAKES = 7;
    private static final char HIDDEN_LETTER_SYMBOL = '□';

    public static void main(String[] args) {
        runStartMenu();
    }
    private static void runStartMenu(){
        List<String> gameStages = new ArrayList<>(Arrays.asList(
                "logo",
                "game_1", "game_2", "game_3", "game_4", "game_5", "game_6", "gameover",
                "win"));

        printResult(gameStages.getFirst());
        System.out.print(
                """
                        Игра 'Виселица'
                        Спасите человечка от виселицы, угадав загаданное слово по буквам \
                        быстрее, чем закончатся ваши попытки!!!
                        
                        """
        );

        boolean isPlay = userWantsToPlay();

        while (isPlay) {
            List<Character> wrongLetters = new ArrayList<>();
            List<String> dictionary = getDictionaryWords();
            String secretWord = getRandomWord(dictionary);
            int currentCountMistake = 0;
            char[] displayedWord = intiHiddenWord(secretWord, HIDDEN_LETTER_SYMBOL);
            char playerLitter;
            int hiddenCharCount;

            while (currentCountMistake < MAX_MISTAKES) {
                printHiddenWord(displayedWord);
                playerLitter = getPlayerLetter();

                if (secretWord.contains(String.valueOf(playerLitter))) {
                    openLetter(secretWord, displayedWord, playerLitter);
                } else {
                    if (wrongLetters.contains(playerLitter)) {
                        System.out.println("Такая буква уже была использована, попробуйте новую");
                        continue;
                    }
                    wrongLetters.add(playerLitter);
                    currentCountMistake++;
                    System.out.println("Ошибки: " + currentCountMistake + "/" + MAX_MISTAKES);
                    printWrongLetters(wrongLetters);
                    printResult(gameStages.get(currentCountMistake));
                }
                hiddenCharCount = getCountHiddenChar(displayedWord, HIDDEN_LETTER_SYMBOL);
                if (hiddenCharCount == 0) {
                    printResult(gameStages.getLast());
                    break;
                }
            }
            System.out.println("Загаданное слово: " + secretWord + "\nХотите сыграть снова?");
            isPlay = userWantsToPlay();
        }
    }

    public static void printWrongLetters(List<Character> wrongLetters) {
        System.out.print("Список неудачных букв: ");
        for (char letter : wrongLetters) {
            System.out.print(letter + " ");
        }
        System.out.println(" ");
    }

    public static int getCountHiddenChar(char[] hiddenWord, char hiddenChar) {
        int countHiddenChar = 0;
        for (char hW : hiddenWord) {
            if (hW == hiddenChar) {
                countHiddenChar++;
            }
        }
        return countHiddenChar;
    }

    public static void openLetter(String secretWord, char[] hiddenWord, char letter) {
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                hiddenWord[i] = letter;
            }
        }
    }

    public static void printHiddenWord(char[] hiddenWord) {
        for (char letter : hiddenWord) {
            System.out.print(letter + " ");
        }
        System.out.println(" ");
    }

    public static char[] intiHiddenWord(String gameWord, char hiddenChar) {
        char[] hiddenWord = new char[gameWord.length()];
        for (int i = 0; i < gameWord.length(); i++) {
            hiddenWord[i] = hiddenChar;
        }
        return hiddenWord;
    }

    public static boolean userWantsToPlay() {
        System.out.println("Нажмите Enter чтобы начать игру!");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = reader.readLine();

            if (input.isEmpty()) {
                return true;
            } else {
                System.out.println("Ждем вас снова! До свидания!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static char getPlayerLetter() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        try {
            do {
                System.out.println("Введите русскую букву:");
                input = reader.readLine();
            } while (!isValidInput(input));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input.toLowerCase().charAt(0);
    }

    public static boolean isValidInput(String letter) {
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

    public static String getRandomWord(List<String> dictWords) {
        int max = dictWords.size();
        Random r = new Random();
        int randomIndex = r.nextInt(max);
        return dictWords.get(randomIndex);
    }

    public static List<String> getDictionaryWords() {
        String fileName = "src/com/petproject/hangman/dictionary_words.txt";
        List<String> dictionaryWords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionaryWords.add(line);
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return dictionaryWords;
    }

    public static void printResult(String str) {
        String fileName = "src/com/petproject/hangman/output.txt";
        String start = str + "_start";
        String end = str + "_end";
        StringBuilder content = new StringBuilder();
        boolean insideBlock = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(start)) {
                    insideBlock = true;
                    continue;
                }
                if (line.contains(end)) {
                    break;
                }
                if (insideBlock) {
                    content.append(line).append("\n");
                }

            }
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

    }
}