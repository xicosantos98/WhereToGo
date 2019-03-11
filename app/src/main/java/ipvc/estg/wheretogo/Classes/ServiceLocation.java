package ipvc.estg.wheretogo.Classes;

public class ServiceLocation {
    private double latitude;
    private double longitude;

    public ServiceLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ServiceLocation() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
