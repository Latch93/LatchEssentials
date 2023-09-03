package lmp.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.OutputStream;

public class MyHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue=null;
        if("GET".equals(httpExchange.getRequestMethod())) {
            requestParamValue = handleGetRequest(httpExchange);
        }
//        else if("POST".equals(httpExchange)) {
//            requestParamValue = handlePostRequest(httpExchange);
//        }
        handleResponse(httpExchange,requestParamValue);
    }

    public String handleGetRequest(HttpExchange httpExchange) {
        return httpExchange.getRequestURI()
            .toString()
            .split("\\?")[1]
            .split("=")[1];
    }
    public void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String htmlBuilder = "<html>" +
                "<body>" +
                "<h1>" +
                "Hello " +
                requestParamValue +
                "</h1>" +
                "</body>" +
                "</html>";

        // encode HTML content
        String htmlResponse = StringEscapeUtils.escapeHtml4(htmlBuilder);
        // this line is a must
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());

        outputStream.flush();
        outputStream.close();
    }
}