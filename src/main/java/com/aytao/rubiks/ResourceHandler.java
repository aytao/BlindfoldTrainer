package com.aytao.rubiks;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

public class ResourceHandler {
    public static File getFile(String fileName) throws Exception {
        ClassLoader classLoader = ResourceHandler.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null)
            throw new Exception("Error retrieving resource " + fileName);
        String filePath = resource.toURI().getPath();
        File file = new File(filePath);
        return file;
    }

    public static void main(String[] args) {
        String fileName = "TestFile.txt";
        if (args.length == 1) {
            fileName = args[0];
        }
        try (Scanner in = new Scanner(ResourceHandler.getFile(fileName), "utf-8")) {
            while (in.hasNextLine()) {
                System.out.println(in.nextLine());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
