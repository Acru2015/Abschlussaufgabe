import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

public class Slang {

    private static Deque<Value> stack = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        AtomScanner sc = null;
        try {
            sc = new AtomScanner(System.in);
        } catch (LexerException ex) {
            System.err.println(ex);
        }
        if (sc != null) {
            while (sc.hasNext()) {
                if (sc.hasNextString()) {
                    Value helper = new StringValue(sc.nextString());
                    //System.out.println("String: " + helper);
                    stack.push(helper);
                } else if (sc.hasNextInteger()) {
                    Value helper = new IntegerValue(sc.nextInteger());
                    //System.out.println("Int: " + helper);
                    stack.push(helper);
                } else if (sc.hasNextAtom()) {
                    execute(sc.nextAtom());
                } else {
                    System.err.println("UNKNOWN: " + sc.next());
                }
            }
        }
    }

    private static void execute(String atom) throws IOException {
        if (atom.equals(".s")) {
            for (Value v : stack) {
                System.out.println(v);
            }
            return;
        }
        if (atom.equals("skip-to")) {
            return;
        }
        if (atom.equals("skip-n")) {
            return;
        }
        if (atom.equals("copy-to")) {
            return;
        }
        //System.out.println("Atom: " + atom);
        Actions action = Actions.valueOf(atom);
        switch (action) {
            case prin:
                System.out.print(stack.pop());
                break;
            case print:
                System.out.println(stack.pop());
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
                Value erg = new IntegerValue(helper0 + helper1);
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
                Path file = Paths.get(helper);
                String content = null;
                if (helper.contains("http://") || helper.contains("https://")) {            //File oder Website?
                    content = getWebPageSource(helper);
                } else {
                    content = new String(Files.readAllBytes(file));
                }
                Value contVal = new StringValue(content);
                stack.push(contVal);
            }
            break;
            case write:
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
            case trunc:
                break;
            case dup: {
                Value helper = stack.pop();
                stack.push(helper);
                stack.push(helper);
            }
            break;
            case pop:
                stack.pop();
                break;
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
        prin, print, add, sub, div, mod, read, write, ask, askn, append, trunc, dup, pop;
    }
}
