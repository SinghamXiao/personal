package test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PackageUtil {

    public static List<String> getClassName(String packageName) {
        List<String> fileNameList = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if ("file".equals(type)) {
                fileNameList = getClassNameByFile(packageName, url.getPath());
            }
        }
        return fileNameList;
    }

    private static List<String> getClassNameByFile(String packageName, String filePath) {
        List<String> fileNameList = new ArrayList<>();
        File[] files = new File(filePath.replace("test-", "")).listFiles();
        if (files == null) {
            return fileNameList;
        }

        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".class")) {
                fileNameList.add(packageName + "." + fileName.substring(0, fileName.indexOf(".")));
            }
        }

        return fileNameList;
    }

    public static void main(String[] args) {
        String packageName = "main.java";
        List<String> classNames = getClassName(packageName);
        if (classNames != null) {
            for (String className : classNames) {
                System.out.println(className);
            }
        }
    }

}
