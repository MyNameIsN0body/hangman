import java.io.*;
import java.util.*;

public class Main {
    private static final int MAX_MISTAKES = 7;
    private static final char HIDDEN_CHAR = '□';
    private static final String NAME_GAME = "'Виселица'";

    public static void main(String[] args) {

        List<String> gameStages = new ArrayList<>(Arrays.asList(
                "logo",
                "game_1", "game_2", "game_3","game_4", "game_5", "game_6", "gameover",
                "win")); // 7 попыток

        printResult(gameStages.getFirst());
        System.out.print(
                "Игра " + NAME_GAME + "\n" +
                        "Спасите человечка от виселицы, угадав загаданное слово по буквам " +
                        "быстрее, чем закончатся ваши попытки!!!\n\n"
        );

        boolean isPlay = isWantToPlay();

        while (isPlay) {
            List<String> wrongLetterList = new ArrayList<>();
            List<String> words = getDictionaryWords();
            String secretWord = getRandomWord(words);
            int currentCountMistake = 0;
            char[] hiddenWord = intiHiddenWord(secretWord, HIDDEN_CHAR);
            char playerLitter;
            int hiddenCharCount;

            while (currentCountMistake < MAX_MISTAKES) {
                printHiddenWord(hiddenWord);
                playerLitter = getPlayerLetter();

                if (secretWord.contains(String.valueOf(playerLitter))) {
                    hiddenWord = openLetter(secretWord, hiddenWord, playerLitter);
                } else {
                    if (wrongLetterList.contains(String.valueOf(playerLitter))) {
                        System.out.println("Такая буква уже была использована, попробуйте новую");
                        continue;
                    }
                    wrongLetterList.add(String.valueOf(playerLitter));
                    currentCountMistake++;
                    System.out.println("Ошибки: " + currentCountMistake + "/" + MAX_MISTAKES);
                    printWrongLetterList(wrongLetterList);
                    printResult(gameStages.get(currentCountMistake));
                }
                hiddenCharCount = getCountHiddenChar(hiddenWord, HIDDEN_CHAR);
                if (hiddenCharCount == 0) {
                    printResult(gameStages.getLast());
                    break;
                }
            }
            System.out.println("Загаданное слово: " + secretWord + "\nХотите сыграть снова?");
            isPlay = isWantToPlay();
        }
    }

    public static void printWrongLetterList(List<String> lists) {
        System.out.print("Список неудачных букв: ");
        for (String list: lists) {
            System.out.print(list + " ");
        }
        System.out.println(" ");
    }

    public static int getCountHiddenChar(char[] hiddenWord, char hiddenChar) {
        int countHiddenChar = 0;
        for (int i = 0; i < hiddenWord.length; i++) {
            if (hiddenWord[i] == hiddenChar) {
                countHiddenChar++;
            }
        }
        return countHiddenChar;
    }

    public static char[] openLetter(String wordResult, char[] hiddenWord, char litter) {
        for (int i = 0; i < wordResult.length(); i++) {
            if (wordResult.charAt(i) == litter) {
                hiddenWord[i] = litter;
            }
        }
        return hiddenWord;
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
    public static boolean isWantToPlay() {
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

            //reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input.charAt(0);
    }

    public static boolean isValidInput(String letter) {
        //проверка на null и пустая ли строка
        if (letter == null || letter.isEmpty()) {
            return false;
        }
        // проверка на длину строки
        if (letter.length() > 1) {
            return false;
        }
        // проверка на кириллицу
        char character = letter.charAt(0);
        if ((character < 1040 || character > 1103) && (character != 1025 && character != 1105)) {
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
        String fileName = "src/dictionary_words.txt";
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
        String fileName = "src/output.txt";
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