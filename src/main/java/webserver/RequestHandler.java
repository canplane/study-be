package webserver;

import java.io.*;
import java.net.Socket;
/* form 태그를 이용한 데이터 전송 시,
모두 utf-8 기반의 urlencoded (ascii 아닌 걸 모두 %..로 바꾸는 것)를 이용함 (POST의 경우에도 body가 Content-Type=x-www-form-urlencoded가 default)
근데 POST의 경우에는 html의 charset으로 문자를 읽은 걸 urlencoded 돌려버림.
즉, GET 방식은 URI 표준에 따라 UTF-8이 강제지만, POST 방식은 meta charset에 따름. -> 근데 어떤 charset이든 GET이나 POST나 기본은 그걸 urlencode해서 전송.

아무튼 html/서버 모두 utf-8로 맞추고, form이나 url 해석 시에는 utf-8이 urlencoded된 후 쿼리 스트링 형태로 되어 있는 게 기준이 되는 게 편함.
form이나 url을 받을 때는 urldecode를 먼저 하고 받기! (POST form의 경우에도 Content-Type을 따로 지정한 적이 없댜면 urlencoded되어서 온다.)
일반 파일이나 html,js,css 등 리소스 전송될 때는 urlencoded되는 건 아님.

+ 만약 js, css가 인코딩이 html meta와 다르다면? -> CSS는 @charset="UTF-8"으로 css 파일 내에 선언, JS는 <script charset="UTF-8" ...>로 사용

Q. 서버 소켓 getBytes 시 utf-8인지 어떻게 알고 그에 맞춰 스트링으로 변환하는 건지?
-> JVM의 Charset.defaultCharset()이 기반 시스템의 charset이라서. 그리고 내 기반 시스템의 charset이 utf-8이다. (우분투 wsl이 내 환경에선 $LANG=C.UTF-8로 되어 있다.)
-> inputstreamreader와 같은 게 인코딩 설정 안하면 디폴트 charset 따라가는것
 */

import model.*;
import controller.*;

import org.slf4j.*;


public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    public RequestHandler(Socket connectionSocket) { this.connection = connectionSocket; }

    public void run() {
        //log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest req = new HttpRequest(in);
            HttpResponse res = new HttpResponse(out);

            String path = req.getPath();

            Controller controller = RequestMapping.getController(path);
            if (controller == null) {
                System.out.println(path);
                res.forward(resolvePath(path));
            } else {
                controller.service(req, res);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String resolvePath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
