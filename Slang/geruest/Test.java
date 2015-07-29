//import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;

public class Test {

    public static void run(DirectoryStream<Path> files) throws IOException {
        for (Path f : files) {
            //String ext = FilenameUtils.getExtension(f.toString());
            String ext = f.toString().substring(f.toString().length() - 5);
            if (ext.equals("slang")) {               // if extension "slang" run program slang with slang test file as argument and dump result int a file with the same base name but .test extension
                System.out.println("Starting executes" + f.getFileName());
                String line;
                //String helper = f.toString().substring(0, f.toString().length() - 5) + "test";
                //System.out.println("java Slang < " + f.toString());
                Process p = Runtime.getRuntime().exec("java Slang < " + f.toString());
                System.out.println("1");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader bufferedErrorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                System.out.println("2");

                line = bufferedReader.readLine();
                System.out.println("2.5");

                while (line != null) {
                    System.out.println("Test");
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }

                System.out.println("3");

                while ((line = bufferedErrorReader.readLine()) != null) {
                    System.out.println(line);
                }

                bufferedReader.close();
                System.out.println("Finished executes" + f.getFileName());

                //Runtime.getRuntime().exec("java Slang < " + f.toString() + " > " + helper);
                //System.out.println("java Slang < " + f.toString() + " > " + helper);
            }
        }
    }

    public static void main(String[] args) {
        try {

            Path dir = Paths.get("C:\\Users\\Anton\\Documents\\TUWien\\PK\\SS2015\\Abschlussaufgabe\\progs");
            //createFiles(dir);
            //compile();

            DirectoryStream<Path> files = Files.newDirectoryStream(dir); //create list of all Files in test directory Files
            run(files);                                             //run all needed files

            files = Files.newDirectoryStream(dir);                  //iterator zählt jeweils nur einfach
            compare(files);                                         //compare all files

        } catch (IOException | DirectoryIteratorException x) {

            System.err.println(x);
            x.printStackTrace();
        }
    }

    private static void createFiles(Path dir) throws IOException {
        DirectoryStream<Path> files = Files.newDirectoryStream(dir);              //To create needed files
        for (Path f : files) {
            //String ext = FilenameUtils.getExtension(f.toString());
            String ext = "";
            if (ext.equals("slang")) {               // if extension "slang" run program slang with slang test file as argument and dump result int a file with the same base name but .test extension
                String helper = f.toString().substring(0, f.toString().length() - 5) + "test";
                System.out.println("Starting files" + helper);
                File helperF = new File(helper);
                helperF.createNewFile();
            }
        }
    }

    private static void compile() {
        try {
            String line;
            Process p = Runtime.getRuntime().exec("javac 'C:\\Users\\Anton\\Documents\\TUWien\\PK\\SS2015\\Abschlussaufgabe\\Slang\\geruest\\Slang.java'"); // compile slang

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void compare(DirectoryStream<Path> files) throws IOException {
        for (Path f : files) {
            System.out.println("Starting comparisons" + f.getFileName());
            //String ext = FilenameUtils.getExtension(f.toString());                  //is it a test file?
            String ext = f.toString().substring(f.toString().length() - 7);
            if (ext.equals("output")) {
                String testPath = f.toString().substring(0, f.toString().length() - 6) + "test";       //get path of output
                Path test = Paths.get(testPath);
                if (!fileEquals(f, test)) {                                   // compare them
                    System.err.println("Not Equal:" + f.getFileName());         // errormessage if fault
                } else {
                    System.out.println("Equal:" + f.getFileName());
                }
            }
            System.out.println("Finished comparisons" + f.getFileName());
        }
    }

    private static boolean fileEquals(Path p, Path q) throws IOException {
        String contentP = new String(Files.readAllBytes(p));                    //turn into string then compare, no idea whether best practice
        //System.out.println("Should be");
        //System.out.println(contentP);
        String contentQ = new String(Files.readAllBytes(q));
        //System.out.println("Output");
        //System.out.println(contentP);
        return contentP.equals(contentQ);
    }
}