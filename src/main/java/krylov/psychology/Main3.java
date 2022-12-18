package krylov.psychology;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Integer n = scanner.nextInt();
        int noc = 0;

        int a = 0;
        int b = 0;

        int k = n;
        if (n%2 == 1) {
            k = n - 1;
        }

        for (var i = 1; i <= k/2; i++) {
            int j = n - i;
            for(var l = i; l > 0; l--) {
                if(i%l == 0 && j%l == 0) {
                    if(l > noc) {
                        noc = l;
                        a = i;
                        b = j;
                    }
                }
            }
        }
        System.out.println(a + " " + b);
    }
}
