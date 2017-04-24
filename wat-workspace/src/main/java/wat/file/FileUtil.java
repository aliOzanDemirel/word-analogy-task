package wat.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.wordnet.WordNetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(WordNetUtil.class);
    private static boolean debugEnabled = log.isDebugEnabled();

    public static boolean isPathValid(String filePath) {
//        File f = new File(filePath);
//        if (f.exists()) {
//            return f.getAbsolutePath();
//        }
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
