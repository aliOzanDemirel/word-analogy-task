package wat.main;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Loader;

/**
 * Created by ali on 31.03.2017.
 */
public class coskun {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().totalMemory());
        System.out.println(Runtime.getRuntime().maxMemory());
        System.out.println(Runtime.getRuntime().freeMemory());
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

}
