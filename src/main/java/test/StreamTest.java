package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class StreamTest {
    public static void main(String[] args) throws IOException {
//        inputStreamMain();
//        InputStreamReaderMain();
        bufferedReaderMain();
//        scannerMain();
    }

    private static void scannerMain() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println(sc.next());
    }


    private static void bufferedReaderMain() throws IOException {
        InputStream in = System.in;
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(reader);

        String a = br.readLine();
        System.out.println(a);
    }


    private static void InputStreamReaderMain() throws IOException {
        InputStream in = System.in;
        InputStreamReader reader = new InputStreamReader(in);

        char[] a = new char[3];
        reader.read(a);

        System.out.println(a);
    }

    private static void inputStreamMain() throws IOException {
        InputStream in = System.in;

        byte[] a = new byte[3];
        in.read(a);
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);


//        int a;
//        a = in.read();
//
//        System.out.println(a);
    }
}
