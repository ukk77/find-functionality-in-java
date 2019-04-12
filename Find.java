/*
 * Find.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  A class which copies the functionality of UNIX find command.
 *
 * @author      Aamir Jamal
 * @author      Uddesh Karda
 */

public class Find {

    private static String[] arguments;

    /**
     * The Main program.
     * @param args  -name for specifying a pattern, -type to choose from f/d
     *              -length for getting the length of the file, -date to get the
     *              last modified date.
     */
    public static void main(String[] args) {
        if(args.length < 1)
            throw new NoBaseDirectoryException("You need to pass a start directory as a parameter");
        arguments = args.clone();
        String startDirectory = args[0];
        File dir = new File(startDirectory);
        if(!dir.isDirectory())
            throw  new InvalidBaseDirectoryException("There is no directory having name as " + startDirectory);
        findFilesInDir(dir);
    }

    /**
     * Finds all the files present in the directory recursively on the basis of
     * the arguments passed.
     * @param dir   start directory
     */
    private static void findFilesInDir(File dir){
        File[] files = dir.listFiles();
        String regex;
        if(findArg("-name") != -1)
            regex = "\\b" + arguments[findArg("-name") + 1] + "\\b";
        else
            regex = ".*";
        Pattern pattern = Pattern.compile(regex);
        for(File file : files){
            if(file.isDirectory()){
                findFilesInDir(file);
            }
            if(findArg("-type") == -1)
                printFileDetails(file, pattern);
            else {
                if(arguments[findArg("-type") + 1].equals("d")){
                    if (file.isDirectory()){
                        printFileDetails(file, pattern);
                    }
                }
                else if (arguments[findArg("-type") + 1].equals("f")){
                    if (file.isFile()){
                        printFileDetails(file, pattern);
                    }
                }
                else
                    throw new InvalidArgumentException("-type can have only f or d as option.");
            }
        }
    }

    /**
     * Prints out the found file's details as per the passed arguments.
     * @param file  File whose details need to be shown.
     * @param regex regex required to match the file name.
     */
    private static void printFileDetails(File file, Pattern regex){
        Matcher matcher = regex.matcher(file.getName());
        if(matcher.find())
            System.out.println(file.getPath() + getLengthIfNeeded(file) + getLastModifiedDateIfNeeded(file));
    }

    /**
     * Returns the lelngth of the File passed.
     * @param file  file whose length is needed.
     * @return  length of the file.
     */
    private static String getLengthIfNeeded(File file){
        int indexLength = findArg("-length");
        if(indexLength != -1)
            return "\t\t" + file.length();
        else
            return "";
    }

    /**
     * returns the last modified date of a file if asked in arguments.
     * @param file  file whose last modified date is needed.
     * @return  last modified date of the file.
     */
    private static String getLastModifiedDateIfNeeded(File file){
        int indexDate = findArg("-date");
        if(indexDate != -1)
            return "\t\t" + new Date(file.lastModified()).toString();
        else
            return "";
    }

    /**
     * Looks for an value present in the present in the arguments array.
     * @param val   value to look for.
     * @return  index of the value in arguments array.
     */
    private static int findArg(String val){
        for (int index = 0; index < arguments.length; index++){
            if(arguments[index].equals(val))
                return index;
        }
        return -1;
    }
}

/**
 * Exception for wrong argument passed.
 */
class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String s){ super(s); }
}

/**
 * Exception for invalid base directory passed.
 */
class InvalidBaseDirectoryException extends RuntimeException {
    public InvalidBaseDirectoryException(String s) { super(s); }
}

/**
 * Exception for no arguments passed.
 */
class NoBaseDirectoryException extends RuntimeException {
    public NoBaseDirectoryException (String s) { super(s); }
}
