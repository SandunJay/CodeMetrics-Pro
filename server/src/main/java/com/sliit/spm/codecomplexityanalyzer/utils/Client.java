package com.sliit.spm.codecomplexityanalyzer.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;


import java.io.IOException;

public class Client {

    public static final String FAILED_TO_SUBMIT_DATA_TO_THE_SERVER = "Failed to submit data to the server ";
    private static String accUrl = PropertyReader.getInstance().getProperty("accUrl");
    private static HttpClient httpClient = HttpClientBuilder.create().build();
    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private Client() {
    }

    public static void sendAnalysisData(Project project) {
        try {

            
            HttpPost request = new HttpPost(accUrl);
            request.addHeader("content-type", "application/json");

            String json = gson.toJson(project); // convert
            
            StringEntity params = new StringEntity(json);
            request.setEntity(params);

            if (project.getFiles().size() > 0) {
                HttpResponse response = httpClient.execute(request);

                if (response.getStatusLine().getStatusCode() == 200) {
                    } else {
                    }
            } else {
                }

        } catch (IOException e) {
            }
    }
}
