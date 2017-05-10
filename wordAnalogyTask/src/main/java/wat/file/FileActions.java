package wat.file;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
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
import java.util.List;
import java.util.stream.Collectors;

public class FileActions {

    private static final Logger log = LoggerFactory.getLogger(FileActions.class);

    /**
     * get different paths till there is no file with given path.
     *
     * @param lines
     * @param folderName
     * @param fileName
     */
    public static void writeToFileByCreatingFile(final List<String> lines,
            final String folderName, final String fileName) throws IOException {

        Path file;
        do {
            file = FileActions.getUniquePathForGivenFileName(folderName, fileName);
        } while (Files.exists(file));

        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    /**
     * @param folderName
     * @param fileName
     */
    public static Path getUniquePathForGivenFileName(final String folderName,
            final String fileName) throws IOException {

        Path file = Paths.get(FileActions.createDirectoryWithFolderName(folderName)
                + File.separator + new LocalDate().toString() + "_"
                + new DateTime().get(DateTimeFieldType.secondOfDay()) + "_" + fileName);
        log.info("File path is prepared: " + file.toAbsolutePath());
        return file;
    }

    /**
     * @param folderName
     * @return a directory path with given name in home folder of user.
     */
    public static Path createDirectoryWithFolderName(final String folderName) throws IOException {

        Path directory = Files.createDirectories(Paths.get(System.getProperty("user.home")
                + File.separator + folderName + File.separator));
        if (log.isDebugEnabled()) {
            log.debug("Directory should exist: " + directory.toAbsolutePath());
        }
        return directory;
    }

    /**
     * not necessary to use since the folder name is hard coded.
     *
     * @param folderName
     */
    private void makeSurePathIsValidForOS(String folderName) {

        if (folderName.contains("\\")) {
            folderName = folderName.replace("\\", File.separator);
        } else if (folderName.contains("/")) {
            folderName = folderName.replace("/", File.separator);
        }
    }

    /**
     * @param filePath path to the file (can also be directory).
     * @return false if file does not exist for given path.
     */
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
