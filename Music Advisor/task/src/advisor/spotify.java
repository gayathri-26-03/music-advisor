package advisor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class spotify {
    static final String clientId = "f674a9a4caaf4529bd4ec978d5a0dd5c";
    static final String clientSecret = "ecb412e3114747aa9ae8dd4f656da541";

    public static final String redirect_url = "http://localhost:8085";
    static HttpClient client = HttpClient.newBuilder().build();
    public static final String authorize_url = String.format("https://accounts.spotify.com/authorize?client_id=%s&redirect_uri=%s&response_type=code",
            clientId, redirect_url);
    String accessPoint;
    public static String accessToken;
    public static String resourceArg;
    public static int page;

    public void authorize(String[] args) {
        System.out.println("Use this link to authorize Spotify");

        try {
            accessPoint = args[1];
            resourceArg = args[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            accessPoint = "https://accounts.spotify.com";
            resourceArg = "https://api.spotify.com";
        }
        if ("-page".equals(args[4])) {
            page = Integer.parseInt(args[5]);
        } else {
            page = 5;
        }
        System.out.println(authorize_url);
    }
    public void getAccessToken() {
        String plainCredentials = clientId + ":" + clientSecret;
        String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
        String authorizationHeader = "Basic " + base64Credentials;

        String access_token_url = accessPoint + "/api/token";
        String bodyData = String.format("code=%s&redirect_uri=%s&grant_type=authorization_code",
                AppHttpServer.authCode, redirect_url);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", authorizationHeader)
                .uri(URI.create(access_token_url))
                .POST(HttpRequest.BodyPublishers.ofString(bodyData))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            accessToken = response.body().toString().substring(17,23);

        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public static String featured() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourceArg + "/v1/browse/featured-playlists"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        List<Info> infos = new ArrayList<>();

        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject categories = jo.getAsJsonObject("playlists");

        for (JsonElement item : categories.getAsJsonArray("items")) {
            Info element = new Info();
            element.setAlbum(item.getAsJsonObject().get("name").toString().replaceAll("\"", ""));

            element.setLink(item.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify")
                    .toString().replaceAll("\"", ""));

            infos.add(element);
        }
        StringBuilder result = new StringBuilder();
        for (Info each : infos) {
            result.append(each.album).append("\n")
                    .append(each.link).append("\n")
                    .append("\n");
        }
        return result.toString();
    }

    public static String new1() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourceArg + "/v1/browse/new-releases"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        //System.out.println(response.body());
        List<Info> infos = new ArrayList<>();

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject categories = jsonObject.getAsJsonObject("albums");


        for (JsonElement item : categories.getAsJsonArray("items")) {
            Info element = new Info();
            element.setAlbum(item.getAsJsonObject().get("name").toString().replaceAll("\"", ""));

            StringBuilder artists = new StringBuilder("[");

            for (JsonElement name : item.getAsJsonObject().getAsJsonArray("artists")) {
                if (!artists.toString().endsWith("[")) {
                    artists.append(", ");
                }
                artists.append(name.getAsJsonObject().get("name"));
            }

            element.setName(artists.append("]").toString().replaceAll("\"", ""));

            element.setLink(item.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify")
                    .toString().replaceAll("\"", ""));

            infos.add(element);

        }

        StringBuilder result = new StringBuilder();
        for (Info each : infos) {
            result.append(each.album).append("\n")
                    .append(each.name).append("\n")
                    .append(each.link).append("\n")
                    .append("\n");
        }
        return result.toString();

    }

    public static String categories(){

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
//                .header("limit","20")
                .uri(URI.create(resourceArg + "/v1/browse/categories"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            List<Info> infos = new ArrayList<>();

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jsonObject.getAsJsonObject("categories");
            for (JsonElement item : categories.getAsJsonArray("items")) {
                Info element = new Info();
                element.setCategories(item.getAsJsonObject().get("name").toString().replaceAll("\"", ""));
                infos.add(element);
            }

            StringBuilder result = new StringBuilder();
            for (Info each : infos) {
                result.append(each.categories).append("\n");
            }
            return result.toString();

        } catch (Exception e) {
            return "Error response";
        }
    }

    public static String playlist(String name) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourceArg+"/v1/browse/categories"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        List<Info> infos = new ArrayList<>();

        String id_categories = "Unknown category name.";

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject categories = jsonObject.getAsJsonObject("categories");
        for (JsonElement item : categories.getAsJsonArray("items")) {

            if (item.getAsJsonObject().get("name").toString().replaceAll("\"", "").equals(name.trim())){
                id_categories = item.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                break;
            }
        }
        if (id_categories.equals("Unknown category name.")) {
            return id_categories;
        }

        HttpRequest httpRequest1 = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(resourceArg+"/v1/browse/categories/" + id_categories + "/playlists"))
                .GET()
                .build();
        HttpResponse<String> response1 = client.send(httpRequest1, HttpResponse.BodyHandlers.ofString());

        if(response1.body().contains("Test unpredictable error message")) {
            return "Test unpredictable error message";
        }
        jsonObject = JsonParser.parseString(response1.body()).getAsJsonObject();
        categories = jsonObject.getAsJsonObject("playlists");

        for (JsonElement item : categories.getAsJsonArray("items")) {
            Info element = new Info();
            element.setAlbum(item.getAsJsonObject().get("name").toString().replaceAll("\"", ""));

            element.setLink(item.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify")
                    .toString().replaceAll("\"", ""));

            infos.add(element);
        }

        StringBuilder result = new StringBuilder();
        for (Info each : infos) {
            result.append(each.album).append("\n")
                    .append(each.link).append("\n")
                    .append("\n");
        }
        return result.toString();

    }
}
