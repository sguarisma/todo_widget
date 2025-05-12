import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {
    private static String API_KEY = KEY;

    public static Weather getWeather(String city) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String encodedCity = java.net.URLEncoder.encode(city, java.nio.charset.StandardCharsets.UTF_8);
        String endpoint = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity +
                          "&appid=" + API_KEY + "&units=metric";

         HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), Weather.class);

    }
}
