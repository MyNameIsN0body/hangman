import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        String intro = "logo";
        String gameover = "gameover";
        printResult(intro);
    }

    public static void printResult(String str) {
        String fileName = "C:\\Users\\a.gudkov\\Projects\\Free\\java\\gallows\\gallows\\src\\output.txt";
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
                    insideBlock = false;
                    break;
                }
                if (insideBlock) {
                    content.append(line).append("\n");
                }

            }

            System.out.println(content.toString());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

    }
}