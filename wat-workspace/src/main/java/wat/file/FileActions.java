package wat.file;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileActions {

    private static final Logger log = LoggerFactory.getLogger(FileActions.class);

    public static void writeToFile(final String folderNameWithModel, final List<String> lines)
            throws IOException {

        Path file = Paths.get(System.getProperty("user.home") +
                "/" + folderNameWithModel + "_saved/" + new LocalDate().toString() + "_SCORES.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    public static File getFolderToSaveModel(final String folderNameWithModel) {

        String path = System.getProperty("user.home") + "/" + folderNameWithModel + "_saved";
        return new File(path);
    }

    public static boolean isPathValid(String filePath) {

        return Files.exists(Paths.get(filePath));
    }

    private Path getPathIfValid(String filePath) {

        Path path = null;
        try {
            path = Paths.get(filePath);
        } catch (InvalidPathException e) {
            log.error("File path is invalid: " + filePath);
        }
        return path;
    }

    public void readFile(String filePath) throws Exception {

        BufferedReader bufferedReader = null;
        Path path = this.getPathIfValid(filePath);
        try {
            bufferedReader = Files.newBufferedReader(path);
        } catch (IOException e) {
            throw new Exception("Error while creating buffered reader!", e);
        }

        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
        lines.forEach(System.out::println);
    }


}
