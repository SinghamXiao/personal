
import infrastructure.FilesManager;
import org.junit.*;

import java.lang.reflect.Constructor;

public class FilesManagerTest {
    private String path = "src/test/resources/tmp";
    private String fileName0 = "test0.xml";
    private String fileName1 = "test1.xml";
    private String folder = "test";
    private String packToCar = "test.car";
    private String packToZip = "test.zip";

    @BeforeClass
    public static void testBeforeClass() {
        System.out.println("@BeforeClass");
    }

    @Before
    public void testBefore(){
        System.out.println("@Before");
    }

    @Test
    public void testInstanceOfClassFilesManager() {
        Constructor[] constructors = FilesManager.class.getDeclaredConstructors();
        for(Constructor constructor: constructors) {
            System.out.println("constructor: " + constructor.toString());
            constructor.setAccessible(true);
            try {
                FilesManager filesManager = (FilesManager)constructor.newInstance();
                assert !filesManager.getClass().isLocalClass();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMkDirSuccess() {
        assert FilesManager.mkDir(path + "/" + folder);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testMkDirFailedWithExists() {
        assert FilesManager.mkDir(path + "/" + folder);
        assert !FilesManager.mkDir(path + "/" + folder);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testDeleteEmptyDirSuccess() {
        assert FilesManager.mkDir(path + "/" + folder);
        assert FilesManager.deleteDir(path + "/" + folder);
    }

    @Test
    public void testDeleteDirWithSubdirAndFilesSuccess() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.mkDir(path + "/" + folder);
        assert FilesManager.createFile(path + "/" + folder, fileName1);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testDeleteDirFailedWithNotExists() {
        assert !FilesManager.deleteDir(path + "/" + folder);
    }

    @Test
    public void testDeleteDirFailedWithIsNotDir() {
        assert !FilesManager.deleteDir(path + "/" + fileName1);
    }

    @Test
    public void testCreateFileSuccess() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.deleteFile(path + "/" + fileName0);
    }

    @Test
    public void testCreateFileFailedWithExists() {
        assert FilesManager.createFile(path, fileName0);
        assert !FilesManager.createFile(path, fileName0);
        assert FilesManager.deleteFile(path + "/" + fileName0);
    }

    @Test
    public void testCreateFileFailedWithFolder() {
        assert !FilesManager.createFile(path, folder + "/");
    }

    @Test
    public void testDeleteFileSuccess() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.deleteFile(path + "/" + fileName0);
    }

    @Test
    public void testDeleteFileFailedWithNotExists() {
        assert !FilesManager.deleteFile("src/test/resources/tmpTest/test1234.xml");
    }

    @Test
    public void testDeleteFileFailedWithFolder() {
        assert !FilesManager.deleteFile(path);
    }

    @Test
    public void testPackFilesToCarSuccess() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.createFile(path + "/" + folder, fileName1);
        assert FilesManager.packFiles(path, path, packToCar);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testPackFilesToZipWithNoSuffixSuccess() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.createFile(path + "/" + folder, fileName1);
        assert FilesManager.packFiles(path, path, folder);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testPackFilesToZipSuccess() {
        assert FilesManager.createFile(path, fileName1);
        assert FilesManager.createFile(path + "/" + folder, fileName0);
        assert FilesManager.packFiles(path, path, packToZip);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testPackFilesFailedWithExists() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.createFile(path + "/" + folder, fileName1);
        assert FilesManager.packFiles(path, path, packToCar);
        assert FilesManager.packFiles(path, path, packToCar);
        assert FilesManager.deleteDir(path);
    }

    @Test
    public void testPackFilesFailedWithNotExists() {
        assert !FilesManager.packFiles(path + "0", path, packToCar);
    }

    @Test
    public void testUnpackCarFilesSuccess() {
        assert FilesManager.createFile(path, fileName0);
        assert FilesManager.createFile(path + "/" + folder, fileName1);
        assert FilesManager.packFiles(path, path, packToCar);
        assert FilesManager.mkDir(path + "0");
        assert FilesManager.unpackFiles(path + "/" + packToCar, path + "0");
        assert FilesManager.deleteDir(path);
        assert FilesManager.deleteDir(path + "0");
    }

    @Test
    public void testUnpackZipFilesSuccess() {
        assert FilesManager.createFile(path, fileName1);
        assert FilesManager.createFile(path + "/" + folder, fileName0);
        assert FilesManager.packFiles(path, path, packToZip);
        assert FilesManager.mkDir(path + "1");
        assert FilesManager.unpackFiles(path + "/" + packToZip, path + "1");
        assert FilesManager.deleteDir(path);
        assert FilesManager.deleteDir(path + "1");
    }

    @Test
    public void testUnpackCarFilesFailedWithNotPackedFile() {
        assert !FilesManager.unpackFiles(path, path + "0");
    }

    @Test
    public void testUnpackCarFilesFailedWithNotExists() {
        assert !FilesManager.unpackFiles(path + "/test/" + packToCar, path + "0");
    }

    @After
    public void testAfter(){
        System.out.println("@After");
    }

    @AfterClass
    public static void testAfterClass() {
        System.out.println("@AfterClass");
    }

    @Ignore
    public void testIgnore() {
        System.out.println("@Ignore");
    }
}
