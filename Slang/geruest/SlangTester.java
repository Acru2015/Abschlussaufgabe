import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

public class SlangTester {

    private static Deque<Value> stack = new LinkedList<>();
    private static PrintWriter printWriter = null;
    private static Path currentPath;


    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("progs");
        DirectoryStream<Path> files = Files.newDirectoryStream(dir);
        //System.out.println(dir.toAbsolutePath().toString());
        for (Path f : files) {
            String ext = f.toString().substring(f.toString().length() - 5);
            if (ext.equals("slang")) {
                System.out.println(f.getFileName());
                test(f);
                stack.clear();
                printWriter = null;
            }
            //compare(f);
        }
    }

    private static void test(Path f) {
        currentPath = f;
        String testPath = f.toString().substring(0, f.toString().length() - 5) + "test";
        AtomScanner sc = null;

        try {
            printWriter = new PrintWriter(testPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sc = new AtomScanner(new FileInputStream(f.toFile()));
        } catch (LexerException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (sc != null && printWriter != null) {
            while (sc.hasNext()) {
                if (sc.hasNextString()) {
                    Value helper = new StringValue(sc.nextString());
                    stack.push(helper);
                } else if (sc.hasNextInteger()) {
                    Value helper = new IntegerValue(sc.nextInteger());
                    stack.push(helper);
                } else if (sc.hasNextAtom()) {
                    try {
                        execute(sc.nextAtom());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    printWriter.println("UNKNOWN: " + sc.next());
                    System.out.println();
                }
            }
        }
        printWriter.close();
    }

    public static void compare(Path f) throws IOException {
        //System.out.println("Starting comparisons" + f.getFileName());
        String ext = f.toString().substring(f.toString().length() - 6);
        //System.out.println(ext);

        if (ext.equals("output")) {
            String testPath = f.toString().substring(0, f.toString().length() - 6) + "test";       //get path of output
            Path test = Paths.get(testPath);
            fileEquals(f, test);
        }
        //System.out.println("Finished comparisons" + f.getFileName());
    }

    private static boolean fileEquals(Path p, Path q) throws IOException {
        String contentP = new String(Files.readAllBytes(p));                    //turn into string then compare, no idea whether best practice

        String contentQ = new String(Files.readAllBytes(q));


        boolean equal = contentP.equals(contentQ);
        if (!equal) {
            System.out.println("Not Equal:" + p.getFileName());
            System.out.println("Should be:");
            System.out.println(contentP);
            System.out.println("Output:");
            System.out.println(contentQ);
            System.out.println("Difference:");
            System.out.println(StringUtils.difference(contentP, contentQ));
        } else {
            System.out.println("Equal:" + p.getFileName());
        }
        return equal;
    }

    private static void execute(String atom) throws IOException {
        try {
            Value helperValue;

            if (atom.equals(".s")) {
                printWriter.println("--Stack:");
                for (Value v : stack) {
                    printWriter.println(v.toS());
                }
                printWriter.println("--bottom");
                return;
            }

            if (atom.equals("skip-to")) {

                String helper1 = stack.pop().toS();
                String helper0 = stack.pop().toS();

                //System.out.println(helper0);
                //System.out.println(helper1);

                if (helper0.toLowerCase().contains(helper1.toLowerCase())) {
                    Value result = new StringValue(helper0.substring(0, helper0.length() - helper1.length()));
                    stack.push(result);
                } else {
                    stack.push(new StringValue(""));
                }
                return;
            }

            if (atom.equals("skip-n")) {
                int n = stack.pop().toI();
                String helper0 = stack.pop().toS();

                System.out.println(n);
                System.out.println(helper0.length());

                Value result = new StringValue(helper0.substring(n, helper0.length()));
                stack.push(result);
                return;
            }

            if (atom.equals("copy-to")) {

                String helper1 = stack.pop().toS();
                String helper0 = stack.pop().toS();

                if (helper0.toLowerCase().contains(helper1.toLowerCase())) {
                    Value result = new StringValue(helper0.substring(0, helper0.indexOf(helper1)));
                    stack.push(result);
                } else {
                    stack.push(new StringValue(""));
                }
                return;
            }
            Actions action = Actions.valueOf(atom);


            switch (action) {
                case prin:
                    helperValue = stack.pop();
                    printWriter.print(helperValue.toS());
                    break;
                case print:
                    helperValue = stack.pop();
                    printWriter.println(helperValue.toS());
                    break;
                case add: {
                    int helper0 = stack.pop().toI();
                    int helper1 = stack.pop().toI();
                    Value erg = new IntegerValue(helper0 + helper1);
                    stack.push(erg);
                }
                break;
                case sub: {
                    int helper0 = stack.pop().toI();
                    int helper1 = stack.pop().toI();
                    Value erg = new IntegerValue(helper0 - helper1);
                    stack.push(erg);
                }
                break;
                case mul: {
                    int helper0 = stack.pop().toI();
                    int helper1 = stack.pop().toI();
                    Value erg = new IntegerValue(helper0 * helper1);
                    stack.push(erg);
                }
                break;
                case div: {
                    int helper0 = stack.pop().toI();
                    int helper1 = stack.pop().toI();
                    Value erg = new IntegerValue(helper0 / helper1);
                    stack.push(erg);
                }
                break;
                case mod: {
                    int helper0 = stack.pop().toI();
                    int helper1 = stack.pop().toI();
                    Value erg = new IntegerValue(helper0 % helper1);
                    stack.push(erg);
                }
                break;
                case read: {
                    String helper = stack.pop().toS();
                    String content;
                    if (helper.contains("http://") || helper.contains("https://")) {            //File oder Website?
                        content = getWebPageSource(helper);
                    } else {
                        Path file = Paths.get(".\\progs\\" + helper);
                        content = new String(Files.readAllBytes(file));
                    }
                    Value contVal = new StringValue(content);
                    stack.push(contVal);
                }
                break;
                case write: {
                    String string = stack.pop().toS();
                    String dest = ".\\progs\\" + stack.pop().toS();

                    //Path file = Paths.get(dest);
                    try {
                        BufferedWriter out = null;
                        try {
                            out = new BufferedWriter(new FileWriter(dest));
                            out.write(string);
                        } finally {
                            if (out != null) {
                                out.close();
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println("I/O Error: " + ex.getMessage());
                    }
                }
                break;
                case ask: {
                    String entered = JOptionPane.showInputDialog("Yo, gimme a String to crunch!");
                    stack.push(new StringValue(entered));
                }
                break;
                case askn: {
                    String entered = JOptionPane.showInputDialog("Yo, gimme a Numba to crunch!");
                    stack.push(new IntegerValue(Integer.parseInt(entered)));
                }
                break;
                case append: {
                    String helper0 = stack.pop().toS();
                    String helper1 = stack.pop().toS();
                    Value result = new StringValue(helper0 + helper1);
                    stack.push(result);
                }
                break;
                case trunc: {
                    int n = stack.pop().toI();
                    String helper = stack.pop().toS();
                    Value result = new StringValue(helper.substring(0, n));
                    stack.push(result);
                }
                break;
                case dup: {
                    helperValue = stack.pop();
                    stack.push(helperValue);
                    stack.push(helperValue);
                }
                break;
                case pop:
                    stack.pop();
                    break;
            }
        } catch (IndexOutOfBoundsException | NullPointerException | NoSuchElementException | IllegalArgumentException | LexerError | NoSuchFileException e) {
            System.out.println(currentPath.getFileName().toString() + ": " + e);
        }
    }

    private static String getWebPageSource(String sURL) throws IOException {
        URL url = new URL(sURL);
        URLConnection urlCon = url.openConnection();
        BufferedReader in;

        if (urlCon.getHeaderField("Content-Encoding") != null && urlCon.getHeaderField("Content-Encoding").equals("gzip")) {            //Wenn gzip mit gzip input stream
            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlCon.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
        }

        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();
        return sb.toString();
    }

    private enum Actions {
        prin, print, add, sub, div, mul, mod, read, write, ask, askn, append, trunc, dup, pop;
    }
}