// almost unused

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int sz) throws IOException {
        char[] body = new char[sz];
        br.read(body, 0, sz);
        return String.copyValueOf(body);
    }

    public static byte[] readResource(String path) throws IOException {
        return Files.readAllBytes(Paths.get("./webapp" + path));
    }
}
