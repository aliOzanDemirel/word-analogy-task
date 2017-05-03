import org.junit.Assert;
import org.junit.Test;
import wat.file.FileActions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileActionsTest {

    @Test
    public void testGetFileByCreatingIfNecessary() throws IOException {

        String folderName = "testFolder";
        Path file = FileActions.getUniquePathForGivenFileName(folderName, "shouldExist.txt");
        file = Files.createFile(file);
        Assert.assertTrue(file.toFile().exists());
    }

}
