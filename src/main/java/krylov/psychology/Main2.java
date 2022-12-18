package krylov.psychology;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("введите курсы валют: ");
        String course = scanner.nextLine();
        System.out.print("введите количество на счету: ");
        String amount = scanner.nextLine();

        String[] arrayCourse = course.split(" ");
        List<Integer> courseList = new ArrayList<>();
        for (var i = 0; i < 3; i++) {
            courseList.add(Integer.parseInt(arrayCourse[i]));
        }

        String[] arrayAmount = amount.split(" ");
        List<Integer> amountList = new ArrayList<>();
        for (var i = 0; i < 3; i++) {
            amountList.add(Integer.parseInt(arrayAmount[i]));
        }
        int aa = courseList.get(0);
        int bb = courseList.get(1);
        int cc = courseList.get(2);

        int xx = amountList.get(0);
        int yy = amountList.get(1);
        int zz = amountList.get(2);

        int total = aa * xx + bb* yy + cc * zz;

        System.out.println(aa + " " + bb + " " + cc);
        System.out.println(xx + " " + yy + " " + zz);
        int maxA = total / aa;
        int maxB = total / bb;
        int maxC = total / cc;

        int result = 0;

        for (var i = 0; i <= maxA; i++) {
            int localTotal = total - i * aa;
            if (localTotal >= 0) {
                for (var j = 0; j <= maxB; j++) {
                    int balance = localTotal - j * bb;
                    if (balance >= 0) {
                        if (balance == 0 || balance%cc == 0) {
                            if ((total - i * aa - j * bb) >= 0) {
                                result = result + 1;
                                System.out.println("==" + i + " " + j + " " + (total - i * aa - j * bb) / cc);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("количество вариантов: " + result);
    }
}
