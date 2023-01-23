import java.io.*;
import java.util.Scanner;

public class Main {
    //    public static void main(String[] args) {
//        File file = new File("C:\\Users\\Господин\\IdeaProjects\\KeyWordGame\\src");
//        File file1 = new File(file,"historyStorage.csv");
//        File file2 = new File("src", "historyStorage.csv");
//        if (file.isDirectory()){
//            System.out.println("Каталог: " + file.getName());
//        }
    private static final String HOME = System.getProperty("user.home");

    public static void main(String[] args) {
        File file = new File("src\\historyStorage\\temp.txt");
        try (FileWriter fileWriter = new FileWriter(file,true)) {
            String text = "Разные умные слова\n";
            fileWriter.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fileWriter = new FileWriter(file,true)) {
            fileWriter.append("второй умопомрачительный текст\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileReader fileReader = new FileReader(file);) {
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}

