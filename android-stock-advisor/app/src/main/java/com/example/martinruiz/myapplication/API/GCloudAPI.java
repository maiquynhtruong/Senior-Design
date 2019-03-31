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

import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import java.io.File;

/*
 * Sample code for doing Cloud Machine Learning Engine online prediction in Java.
 */

public class GCloudAPI {

    static String projectId = "fluid-mote-232300-mlengine";
    static String sourceURL = "https://storage.googleapis.com/"+ projectId + "/Data/";
    // You should have already deployed a model and a version.
    // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
    static String modelId = "stock_advisor";
    static String versionId = "version1";

    public static String getTrend(String stockName) {
        String trendIndicator = "aapl"; // Default stock name
        try {
            trendIndicator = getResponse(stockName);
        } catch (Exception e) {
            Log.i("getTrend - ", e.getMessage());
        }
        return trendIndicator;
    }


    public static String getResponse(String stockName) throws Exception{
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Discovery discovery = new Discovery.Builder(httpTransport, jsonFactory, null).build();

        RestDescription api = discovery.apis().getRest("ml", "v1").execute();
        RestMethod method = api.getResources().get("projects").getMethods().get("predict");

        JsonSchema param = new JsonSchema();

        param.set("name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));

        GenericUrl url = new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
        Log.i("GCloudAPI-url: ", url.toString());

        String contentType = "application/json";
        String jsonInput = "{\"input_values\": [\"" + stockName + "\"]}";
        HttpContent content = new ByteArrayContent(contentType, jsonInput.getBytes());
        Log.i("GCloudAPI-content len:", "" + content.getLength());

        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
        HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);

        String response = request.execute().parseAsString();
        return response;
    }
}
