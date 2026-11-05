package com.handbook.structural.composite.filesystem;

import com.handbook.structural.composite.filesystem.Node.File;
import com.handbook.structural.composite.filesystem.Node.Folder;

public class FileSystemDemo {

    public static void main(String[] args) {
        Folder root = new Folder("project",
                new File("pom.xml", 2_048),
                new Folder("src",
                        new File("App.java", 4_096),
                        new Folder("util", new File("Strings.java", 1_024))),
                new File("README.md", 512));
        StringBuilder out = new StringBuilder();
        root.print("", out);
        System.out.print(out);
    }
}
