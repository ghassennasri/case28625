import okhttp3.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OkHttpMultipartTest {
    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }
    private static Map<String ,String> readProperties() throws IOException {
        Map<String,String> m=new HashMap<>();
        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream = SpringRestTemplateTest.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        m.put("host",prop.getProperty("host"));
        m.put("port",prop.getProperty("port"));
        m.put("username",prop.getProperty("username"));
        m.put("password",prop.getProperty("password"));
        return  m;
    }
    private static void logRequest(Request request) throws IOException {

        System.out.println("===========================request begin================================================");
        System.out.println("request : "+ request);
        System.out.println("==========================request end================================================");

    }
    private static void logResponse(Response response) throws IOException {

        System.out.println("============================response begin==========================================");
        System.out.println("Response: "+ response);
        System.out.println("=======================response end=================================================");

    }
    public static void main(String[] args) throws IOException, URISyntaxException {

        Map<String,String> propertiesMap=readProperties();
        OkHttpClient client = createAuthenticatedClient(propertiesMap.get("username"), propertiesMap.get("password"));

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("rs:docID", "71895771486843648448")
                .addFormDataPart("rs:pid", "A123456")
                .addFormDataPart("rs:docStatus", "STAGE")
                .addFormDataPart("rs:file","myFile.zip",
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(SpringRestTemplateTest.class.getClassLoader().getResource("myFile.zip").toURI())))
                .build();
        Request request = new Request.Builder()
                .url("http://"+propertiesMap.get("host")+":"+propertiesMap.get("port")+"/LATEST/resources/example")
                .method("POST", body)
                .addHeader("Content-Type", "multipart/from-data;boundary=5jg6RlFkCZ1vZ6Cnz38HyhuURGMQnuHYgdPPjcRy")
                .build();
        logRequest(request);
        Response response = client.newCall(request).execute();
        logResponse(response);
    }
}
