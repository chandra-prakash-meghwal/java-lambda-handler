package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class LambdaFunctionHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String MYSQL_URL = System.getenv("MYSQL_URL");
    private static final String MYSQL_USER = System.getenv("MYSQL_USER");
    private static final String MYSQL_PASSWORD = System.getenv("MYSQL_PASSWORD");
    private static final String REST_API_URL = System.getenv("REST_API_URL");
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String authToken = getAuthData().orElseThrow(() -> new RuntimeException("Unable to retrieve auth data"));

        String accessToken = "";
        // // Check if request body is not null
        // if (request.getBody() == null) {
        //     // Handle empty or null request body
        //     // For example, you can return an error response
        //     return new APIGatewayProxyResponseEvent()
        //             .withStatusCode(400)
        //             .withBody("Request body is empty or null");
        // }
        // try {
        //     JsonNode bodyJson = objectMapper.readTree(request.getBody());
        //     accessToken = bodyJson.get("access_token").asText();
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     return new APIGatewayProxyResponseEvent()
        //             .withStatusCode(400)
        //             .withBody("Error parsing request body: " + e.getMessage());
        // }
        
        String jsonPayload = "{\n" +
                "    \"authkey\": " + authToken + ",\n" +
                "    \"access-token\": \"" + accessToken + "\"\n" +
                "}";
        System.out.println("JSON Payload: " + jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(REST_API_URL);
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(jsonPayload);
            httpPost.setEntity(entity);
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(statusCode)
                        .withBody(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Error occurred: " + e.getMessage());
        }
    }

    private Optional<String> getAuthData() {
        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT JSON_EXTRACT(auth_data, '$.auth_key') as auth_token FROM user_auth_data WHERE user_id=2 LIMIT 1");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String authToken = rs.getString("auth_token");
                return Optional.of(authToken);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving auth data from MySQL", e);
        }

        return Optional.empty();
    }
}
