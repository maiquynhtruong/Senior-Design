package com.example.martinruiz.myapplication.API;
/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.example.martinruiz.myapplication.R;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.gax.paging.Page;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;

/*
 * Sample code for doing Cloud Machine Learning Engine online prediction in Java.
 */

public class GCloudAPI extends AsyncTask<String, Integer, String> {

    static String projectId = "fluid-mote-232300"; //-mlengine";
    static String sourceURL = "https://storage.googleapis.com/"+ projectId + "/Data/";
    // You should have already deployed a model and a version.
    // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
    static String modelId = "stock_advisor";
    static String versionId = "version1";
    public static String trendIndicator = "-1"; // -1 is error

    public static String getTrend(String stockName) {
        new GCloudAPI().execute(stockName);

        return trendIndicator;
    }


    protected String doInBackground(String... stockNames) {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//        HttpTransport httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleCredentials credentials = ComputeEngineCredentials.create();
        String stockName = stockNames[0];

        try {
//            RestDescription api = discovery.apis().getRest("ml", "v1").execute();
//            RestMethod method = api.getResources().get("projects").getMethods().get("predict");
//
//            JsonSchema param = new JsonSchema();

//            param.set("name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));
//
//            GenericUrl url = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
//            Log.i("GCloudAPI-url: ", url.toString());
//
            String contentType = "application/json";
            String jsonInput = "{\"input_values\": [\"" + stockName + "\"]}";
            HttpContent content = new ByteArrayContent(contentType, jsonInput.getBytes());
            Log.i("GCloudAPI-content:", "" + ((ByteArrayContent) content).getInputStream().toString());
//
//            GoogleCredential credential = GoogleCredential.getApplicationDefault();

//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("GCP_Credentials.json"));
//        InputStream credentialsJSON = GCloudAPI.class.getClassLoader().getResourceAsStream("GCP_Credentials.json");
//            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsJSON);
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport).setJsonFactory(jsonFactory)
//                    .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
//                    .setServiceAccountScopes(Collections.singleton(STORAGE_SCOPE))
                    // .setServiceAccountUser(SERVICE_ACCOUNT_EMAIL)
                    // .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build();
            System.out.println(credential.getServiceAccountId());

            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
//            HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);
//            String response = request.execute().parseAsString();
            String response = "hi";
            return response;
        } catch (Exception e) {
            Log.e("doInBackground", e.getMessage());
            return "There was an error getting trend prediction. Please try again...";
        }
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
        Log.i("ProgressUpdate", String.valueOf(progress[0]));
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(String result) {
        trendIndicator = result;
    }
}
