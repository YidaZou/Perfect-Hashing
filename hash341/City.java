package hash341;

import java.io.Serializable;

public class City implements Serializable {
    public String name;
    public float latitude;
    public float longitude;

    public City(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Found " + name + " (" + latitude + ", " + longitude + " )"
                + "\n" + "http://www.google.com/maps?z=10&q=" + latitude + "," + longitude;
    }
}
