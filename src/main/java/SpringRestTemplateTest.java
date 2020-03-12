import org.apache.commons.io.FileUtils;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/* Request Capture using wireshark for "multipart/form-data" content-type header request, please note that content-type header have been added
charset=UTF-8 before boundary : Content-Type: multipart/form-data;charset=UTF-8;boundary=Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM
The same problem is present with "multipart/mixed"
**********************************
Hypertext Transfer Protocol
    POST /LATEST/resources/example HTTP/1.1\r\n
        [Expert Info (Chat/Sequence): POST /LATEST/resources/example HTTP/1.1\r\n]
        Request Method: POST
        Request URI: /LATEST/resources/example
        Request Version: HTTP/1.1
    Accept: text/plain, *\r\n
        Content-Type: multipart/form-data;charset=UTF-8;boundary=Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM\r\n
        Authorization: Basic YWRtaW46YWRtaW4=\r\n
        User-Agent: Java/1.8.0_231\r\n
        Host: ml10.eng.marklogic.com:8012\r\n
        Connection: keep-alive\r\n
        Content-Length: 1530920\r\n
        \r\n
        [Full request URI: http://ml10.eng.marklogic.com:8012/LATEST/resources/example]
        [HTTP request 1/3]
        [Response in frame: 2754]
        [Next request in frame: 4365]
        File Data: 1530920 bytes

        MIME Multipart Media Encapsulation, Type: multipart/form-data, Boundary: "Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM"
    [Type: multipart/form-data]
    First boundary: --Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM\r\n
    Encapsulated multipart part:  (text/plain)
        Content-Disposition: form-data; name="rs:docID"\r\n
        Content-Type: text/plain;charset=UTF-8\r\n
        Content-Length: 20\r\n\r\n
        Line-based text data: text/plain (1 lines)
            71895771486843648448
    Boundary: \r\n--Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM\r\n
    Encapsulated multipart part:  (text/plain)
        Content-Disposition: form-data; name="rs:versionID"\r\n
        Content-Type: text/plain;charset=UTF-8\r\n
        Content-Length: 1\r\n\r\n
        Line-based text data: text/plain (1 lines)
            1
    Boundary: \r\n--Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM\r\n
    Encapsulated multipart part:  (text/plain)
        Content-Disposition: form-data; name="rs:docStatus"\r\n
        Content-Type: text/plain;charset=UTF-8\r\n
        Content-Length: 5\r\n\r\n
        Line-based text data: text/plain (1 lines)
            STAGE
    Boundary: \r\n--Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM\r\n
    Encapsulated multipart part:  (text/plain)
        Content-Disposition: form-data; name="rs:pid"\r\n
        Content-Type: text/plain;charset=UTF-8\r\n
        Content-Length: 7\r\n\r\n
        Line-based text data: text/plain (1 lines)
            A123456
    Boundary: \r\n--Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM\r\n
    Encapsulated multipart part:  (application/octet-stream)
        Content-Disposition: form-data; name="rs:file"\r\n
        Content-Type: application/octet-stream\r\n
        Content-Length: 1530109\r\n\r\n
        Data (1530109 bytes)
            Data: 504b030414000000000056a7555000000000000000000000…
            [Length: 1530109]
    Last boundary: \r\n--Qnz2RJQSuZZCFzotxjXrT5q4mdMnMM--\r\n
***************************************************************************

 */
public class SpringRestTemplateTest {

    private static Map<String ,String> readProperties(String []args) throws Exception {
        Map<String,String> m=new HashMap<>();

        m.put("host",args[0]);
        m.put("port",args[1]);
        m.put("username",args[2]);
        m.put("password",args[3]);
        return  m;
    }
    private static void logRequest(HttpEntity request) throws IOException {

            System.out.println("===========================request entity begin================================================");
            System.out.println("Headers     : {}"+ request.getHeaders());
            System.out.println("Request body: {}"+ request.getBody());
            System.out.println("==========================request end================================================");

    }
    private static void logResponse(ResponseEntity response) throws IOException {

            System.out.println("============================response begin==========================================");
            System.out.println("Status code  : {}"+ response.getStatusCode());
            System.out.println("Headers      : {}"+ response.getHeaders());
            System.out.println("Response body: {}"+ response.getBody());
            System.out.println("=======================response end=================================================");

    }
    public static void invokePOST(String content_type_header,String[]args) throws Exception {
        Map<String,String> propertiesMap=readProperties(args);
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(propertiesMap.get("username"), propertiesMap.get("password")));

        headers.add(HttpHeaders.CONTENT_TYPE, content_type_header);
        System.out.println("************************invoke POST with header content-type="+content_type_header+"******************************");
        MultiValueMap body = new LinkedMultiValueMap();
        body.add("rs:docID", "71895771486843648448");
        body.add("rs:versionID", "1");
        body.add("rs:docStatus", "STAGE");
        body.add("rs:pid", "A123456");
        File someContent= new File(SpringRestTemplateTest.class.getClassLoader().getResource("myFile.zip").toURI());
        final HttpHeaders theFileHeader = new HttpHeaders();
        theFileHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        if (null!= someContent)
        {
            byte[] zip = FileUtils.readFileToByteArray(someContent);
            body.add("rs:file", new HttpEntity<>(zip,theFileHeader));
        }


        HttpEntity entity = new HttpEntity(body, headers);
        logRequest(entity);


        ResponseEntity response = restTemplate.exchange("http://"+propertiesMap.get("host")+":"+propertiesMap.get("port")+"/LATEST/resources/example", HttpMethod.POST, entity,String.class);
        logResponse(response);
    }
    public static void main(String[] args)  {
        try {
            invokePOST("multipart/form-data",args);
        }catch(Exception e){
            System.out.println("Exception occured while invoking POST with multipart/form-data content-type header");

        }
        try {
            invokePOST("multipart/mixed",args);
        }catch(Exception e){
            System.out.println("Exception occured while invoking POST with multipart/mixed content-type header");

        }

    }
}
