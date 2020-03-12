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
    private static Map<String ,String> readProperties(String []args) throws Exception {
        Map<String,String> m=new HashMap<>();

        m.put("host",args[0]);
        m.put("port",args[1]);
        m.put("username",args[2]);
        m.put("password",args[3]);
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
    public static void main(String[] args) throws Exception {

        Map<String,String> propertiesMap=readProperties(args);
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
