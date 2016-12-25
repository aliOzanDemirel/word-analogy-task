package wat.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileUtil {

    protected final static Logger logger = Logger.getLogger("FileUtil");

    public FileUtil() {

    }

    public boolean fileExists() {
        return true;
    }

    public void readFile(String filePath) throws Exception {
        Path path = null;
        BufferedReader bufferedReader = null;
        try {
            path = Paths.get(filePath);
        } catch (InvalidPathException e) {
            logger.info("Path of the file is invalid!");
            throw new Exception("Path of the file is invalid!", e);
        }
        try {
            bufferedReader = Files.newBufferedReader(path);
        } catch (IOException e) {
            logger.log(Level.INFO, "Error while creating buffered reader!", e);
            throw new Exception("Error while creating buffered reader!", e);
        }

        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
        lines.forEach(System.out::println);
    }


}
