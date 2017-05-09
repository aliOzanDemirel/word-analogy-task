import org.junit.Assert;
import org.junit.Test;
import wat.file.FileActions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BasicTests {

    @Test
    public void testGetFileByCreatingIfNecessary() throws IOException {

        String folderName = "testFolder";
        Path file = FileActions.getUniquePathForGivenFileName(folderName, "shouldExist.txt");
        file = Files.createFile(file);
        Assert.assertTrue(file.toFile().exists());
    }

    @Test
    public void testScores() {

        double error;
        int base = 4;
        int wordSize = 5;
        double[] scores = new double[wordSize];
        double maxScore = 3 * Math.pow(base, wordSize) / 2;
//        double maxScore = Math.pow(wordSize, 2) * 2;
        System.out.println("Max score: " + maxScore);
        for (int i = 0; i < wordSize; i++) {
//            error = Math.pow(base, Math.log(i * 2000));
            error = Math.pow(base, i + 1);
//            error = Math.pow(i + 1, 2);
            scores[i] = maxScore - error;
            System.out.println(i + 1 + "- Error and score: " + error + " -- " + scores[i]);
        }

        for (int i = 0; i < scores.length; i++) {
            if (i + 1 == scores.length) {
                break;
            }
            System.out.println((i + 1) + "/" + (i + 2) + " Difference: " + (scores[i] - scores[i + 1]));
        }
    }

}