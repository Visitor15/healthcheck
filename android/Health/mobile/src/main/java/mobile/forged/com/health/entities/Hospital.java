package mobile.forged.com.health.entities;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Hospital {
    public String name;
    public String street;
    public String city;
    public String state;
    public String zip;
    public String phone;
    public double lat;
    public double lng;

    public Hospital(String name, String street, String city, String state,
                    String zip, String phone, double lat, double lng) {
        this.name = name != null ? name : "";
        this.street = street != null ? street : "";
        this.city = city != null ? city : "";
        this.state = state != null ? state : "";
        this.zip = zip != null ? zip : "";
        this.phone = phone != null ? phone : "";
        this.lat = lat;
        this.lng = lng;
    }
}