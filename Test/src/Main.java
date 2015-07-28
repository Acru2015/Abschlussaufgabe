import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Anton on 16.06.2015.
 */

public class Main {

    public static void run(DirectoryStream<Path> files) throws IOException {
        for (Path f : files) {
            String ext = FilenameUtils.getExtension(f.toString());
            if (ext.equals("slang")) {               // if extension "slang" run program slang with slang test file as argument and dump result int a file with the same base name but .test extension
                //System.out.println("Starting executes" + f.getFileName()); //TODO: https://stackoverflow.com/questions/8149828/read-the-output-from-java-exec
                String helper = f.toString().substring(0, f.toString().length() - 5) + "test";
                Runtime.getRuntime().exec("java Slang < " + f.toString() + " > " + helper);
                //System.out.println("java Slang < " + f.toString() + " > " + helper);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("javac slang.java; ");         // compile slang
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path dir = Paths.get("C:\\Users\\Anton\\Documents\\TU Wien\\PK\\SS2015\\Abschlussaufgabe\\Slang\\progs");
        try {
            /*DirectoryStream<Path> files = Files.newDirectoryStream(dir);              To create needed files
            for (Path f : files) {
                String ext = FilenameUtils.getExtension(f.toString());
                if (ext.equals("slang")) {               // if extension "slang" run program slang with slang test file as argument and dump result int a file with the same base name but .test extension
                    String helper = f.toString().substring(0, f.toString().length() - 5) + "test";
                    System.out.println("Starting files" + helper);
                    File helperF = new File(helper);
                    helperF.createNewFile();
                }
            }*/
            DirectoryStream<Path> files = Files.newDirectoryStream(dir); //create list of all Files in test directory Files
            run(files);                                             //run all needed files
            files = Files.newDirectoryStream(dir);                  //iterator zählt jeweils nur einfach
            compare(files);                                         //compare all files
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
            x.printStackTrace();
        }
    }


    public static void compare(DirectoryStream<Path> files) throws IOException {
        for (Path f : files) {
            //System.out.println("Starting comparisons" + f.getFileName());
            String ext = FilenameUtils.getExtension(f.toString());                  //is it a test file?
            if (ext.equals("output")) {
                String testPath = f.toString().substring(0, f.toString().length() - 6) + "test";       //get path of output
                Path test = Paths.get(testPath);
                if (!fileEquals(f, test)) {                                   // compare them
                    System.err.println("Not Equal:" + f.getFileName());         // errormessage if fault, else just continue
                }
            }
        }
    }

    private static boolean fileEquals(Path p, Path q) throws IOException {
        String contentP = new String(Files.readAllBytes(p));                    //turn into string then compare, no idea whether best practice
        String contentQ = new String(Files.readAllBytes(q));
        return contentP.equals(contentQ);
    }
}