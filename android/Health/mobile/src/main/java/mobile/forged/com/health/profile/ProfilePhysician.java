package mobile.forged.com.health.profile;

/**
 * Created by visitor15 on 11/9/14.
 */
public class ProfilePhysician
{
    public String name;
    public String specialty;
    public String address;
    public String city;
    public String state;
    public String zip;
    public String phone;
    public String email;

    public ProfilePhysician(final String name, final String specialty,
                            final String address, final String city, final String state,
                            final String zip, final String phone, final String email) {
        this.name = name;
        this.specialty = specialty;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

    public ProfilePhysician() {
    }

    public String getFirstName() {
        final String[] substrings = name.split(" ");
        if (substrings.length > 0) {
            return substrings[0];
        }
        return "";
    }

    public String getLastName() {
        final String[] substrings = name.split(" ");
        if (substrings.length > 1) {
            final String[] substrings2 = substrings[1].split(",");
            if (substrings2.length > 0) {
                return substrings2[0];
            }
        }
        return "";
    }

    @Override
    public ProfilePhysician clone()
    {
        ProfilePhysician clone = new ProfilePhysician();
        clone.name = this.name;
        clone.specialty = this.specialty;
        clone.address = this.address;
        clone.city = this.city;
        clone.state = this.state;
        clone.zip = this.zip;
        clone.phone = this.phone;
        clone.email = this.email;
        return clone;
    }
}