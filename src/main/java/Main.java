

import org.apache.commons.io.FileUtils;


import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;


import org.springframework.http.*;

import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;



public class Main {
    private static CredentialsProvider provider() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials =
                new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);
        return provider;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor("admin", "admin"));
        //headers.add(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE); //Content-Type  = multipart/form-data
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MultiValueMap body = new LinkedMultiValueMap();

        body.add("rs:docID", "71895771486843648448");

        body.add("rs:revisionID", "1");

        body.add("rs:versionID", "1");

        body.add("rs:docStatus", "STAGE");
        body.add("rs:pid", "A123456");
        File someContent= new File(Main.class.getClassLoader().getResource("myFile.zip").toURI());
        final HttpHeaders theFileHeader = new HttpHeaders();
        theFileHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        if (null!= someContent)
        {
            byte[] zip = FileUtils.readFileToByteArray(someContent);
            body.add("rs:file", new HttpEntity<>(zip,theFileHeader));
        }


        HttpEntity entity = new HttpEntity(body, headers);

        ResponseEntity response = restTemplate.exchange("http://ml10.eng.marklogic.com:8012/LATEST/resources/example", HttpMethod.POST, entity,String.class); //with marklogic rest end point the POST method sending
        System.out.println(response.toString());
    }
}
