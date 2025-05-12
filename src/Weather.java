public class Weather {
    public WeatherCondition[] weather;
    public String name;

    public class WeatherCondition{
        public String main;
        public String description;
    }

    public String toString(){
        return "\nWeather: " + weather[0].main + " (" + weather[0].description + ") ";
    }
}
