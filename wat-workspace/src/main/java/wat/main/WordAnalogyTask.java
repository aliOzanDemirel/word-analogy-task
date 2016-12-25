package wat.main;

import wat.file.FileUtil;

public class WordAnalogyTask {

    public static void main(String[] args) {

        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.readFile("/home/ali/ORTAM/wordAnalogyTask/logs/application.log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
