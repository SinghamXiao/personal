package infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FilesManager {
    private static final Logger logger = LoggerFactory.getLogger(FilesManager.class);
    private static final int BUFFER = 1024 * 10;

    /** Don't let anyone instantiate this class */
    private FilesManager() {
    }

    public static boolean mkDir(String dirPath) {
        if(!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }

        File dir = new File(dirPath);
/*        if(dir.exists()) {
            logger.info(dirPath + " exists!");
            return false;
        }

        if(!dir.mkdirs()) {
            logger.info("Mk dir " + dirPath + " failed!");
            return false;
        }
        logger.info("Mk dir " + dirPath + " success!");*/
        return !dir.exists() && dir.mkdirs();
    }

    public static boolean deleteDir(String dirPath){
        if(!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }

        File dir = new File(dirPath);
        if(!dir.exists() || !dir.isDirectory()) {
            logger.info("Dir '" + dirPath + "' is not exists or a dir!");
            return false;
        }

        boolean flag = true;
        File[] files = dir.listFiles();
        for (File file : files != null ? files : new File[0]) {
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDir(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

/*        if (!flag) {
            logger.info(dirPath + " delete failed");
            return false;
        }

        if (!dir.delete()) {
            logger.info(dirPath + " delete failed");
            return false;
        }

        logger.info(dirPath + " delete success");*/
        return flag && dir.delete();
    }

    public static boolean createFile(String filePath, String fileName) {
        String pathName = filePath + "/" + fileName;
        if(pathName.endsWith(File.separator)) {
            logger.info("File '" + pathName + "' should not be a folder!");
            return false;
        }

        File file = new File(pathName);
        if(file.exists()) {
            logger.info("File '" + pathName + "' already exists!");
            return false;
        }

        if(!file.getParentFile().exists()) {
            logger.info("Folder '" + filePath + "' not exists! Ready to create it!");
            if(!file.getParentFile().mkdirs()) {
                logger.info("Failed to create folder " + filePath + "!");
                return false;
            }
        }

        try {
            if(!file.createNewFile()) {
                logger.info("Failed to create file " + pathName + "!");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to create file " + pathName + "!" + " Error: " + e.getMessage());
            return false;
        }

        logger.info("Success to create file " + pathName + "!");
        return true;
    }

    public static boolean deleteFile(String pathName) {
        File file = new File(pathName);
/*        if(!file.exists()) {
            logger.info("Delete failed " + pathName + " not exists");
            return false;
        }

        if(!file.isFile()) {
            logger.info("Delete failed " + pathName + " is not a file");
            return false;
        }

        if(!file.delete()) {
            logger.info("Delete failed " + pathName);
            return false;
        }

        logger.info("Success to delete file " + pathName + "!");*/
        return file.exists() && file.isFile() && file.delete();
    }

    public static boolean packFiles(String srcFilePath, String destFilePath, String fileNameWithSuffix) {
        if(!fileNameWithSuffix.endsWith(".zip") && !fileNameWithSuffix.endsWith(".car")) {
            logger.warn("Packed File '" + fileNameWithSuffix + "' no suffix, use '.zip' as default");
            fileNameWithSuffix += ".zip";
        }

        String packPathName = destFilePath + "/" + fileNameWithSuffix;
        logger.info("Ready to pack files in '" + srcFilePath + "' to '" + packPathName + "'");

        File srcFile = new File(srcFilePath);
        if(!srcFile.exists()) {
            logger.info(srcFilePath + " Not Exists!");
            return false;
        }

        try {
            File zipFile = new File(packPathName);
            if(zipFile.exists()) {
                logger.info("Packed file '" + packPathName + "' already exists! Ready to delete it!");
                deleteFile(packPathName);
            }

            File[] srcFiles = srcFile.listFiles();
            if(null == srcFiles || srcFiles.length < 1) {
                logger.info(srcFilePath + " is empty! No need to pack!");
                return false;
            }

            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            for (File file : srcFiles) {
                compress(file, zos, "");
            }
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to pack file '" + packPathName + "'" + " Error: " + e.getMessage());
        }

        logger.info(packPathName + " pack success!");
        return true;
    }

    public static boolean unpackFiles(String packedFile, String destFilePath) {
        if(!packedFile.endsWith(".zip") && !packedFile.endsWith(".car")) {
            logger.warn("File '" + packedFile + "' no suffix, will not treat as packed file!");
            return false;
        }

        File pFile = new File(packedFile);
        if(!pFile.exists()) {
            logger.info("Packed ile '" + packedFile + "' not exists! Unpack failed!");
            return false;
        }

        if(!destFilePath.endsWith(File.separator)) {
            destFilePath = destFilePath + File.separator;
        }

        File dir = new File(destFilePath);
        if(!dir.exists()){
            if(!dir.mkdirs()) {
                logger.info("Cannot create dir '" + dir.getName() + "' unpack file '" + packedFile + "' failed!");
                return false;
            }
        }

        try {
            ZipFile zipFile = new ZipFile(pFile);

            for(Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream zis = zipFile.getInputStream(entry);
                String outPath = (destFilePath + zipEntryName).replaceAll("\\*", "/");

                File subDir = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if(!subDir.exists()) {
                    if(!subDir.mkdirs()) {
                        logger.info("Cannot create dir '" + subDir.getName() + "' unpack file '" + packedFile + "' failed!");
                        return false;
                    }
                }

                logger.debug("OutPath: " + outPath);

                if(new File(outPath).isDirectory()) {
                    continue;
                }

                OutputStream fos = new FileOutputStream(outPath);
                byte[] buf1 = new byte[BUFFER];
                int len;
                while((len = zis.read(buf1)) > 0){
                    fos.write(buf1, 0, len);
                }

                zis.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to unpack file '" + packedFile + "'" + " Error: " + e.getMessage());
        }

        logger.info(packedFile + " unpack success!");
        return true;
    }

    private static void compress(File file, ZipOutputStream zos, String absolutePath) {
        if (file.isDirectory()) {
            compressDir(file, zos, absolutePath);
            return;
        }

        compressFile(file, zos, absolutePath);
    }

    private static void compressDir(File dir, ZipOutputStream zos, String absolutePath) {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files != null ? files : new File[0]) {
            compress(file, zos, absolutePath + dir.getName() + File.separator);
        }
    }

    private static void compressFile(File file, ZipOutputStream zos, String absolutePath) {
        try {
            ZipEntry zipEntry = new ZipEntry(absolutePath + file.getName());
            zos.putNextEntry(zipEntry);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), BUFFER);
            byte[] buffer = new byte[BUFFER];
            int read;
            while ((read = bis.read(buffer, 0, BUFFER)) != -1) {
                zos.write(buffer, 0, read);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}