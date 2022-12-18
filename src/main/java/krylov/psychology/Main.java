package krylov.psychology;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String n = scanner.nextLine();
        int nn = Integer.parseInt(n);

        String s = scanner.nextLine();

        String b = scanner.nextLine();

        String[] arrayOfWords = s.split(" ");

        List<Integer> listOfLength = new ArrayList<>();
        for (String word: arrayOfWords) {
            listOfLength.add(word.length());
        }

        List<String> listOfCodeString = new ArrayList<>();
        var j = 0;
        for (var i = 0; i < listOfLength.size(); i++) {
            listOfCodeString.add(b.substring(j, j + listOfLength.get(i)));
            j = listOfLength.get(i);
        }

        int result = 0;
        for (String str: listOfCodeString) {
            for (var i = 0; i < str.length() - 1; i ++) {
                if (str.substring(i, i + 1).equals(str.substring(i + 1, i + 2))) {
                    result = result + 1;
                    break;
                }
            }
        }
        System.out.println(result);
    }
}
