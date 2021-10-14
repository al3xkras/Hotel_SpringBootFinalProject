package ua.alexkras.hotel.model;

public enum ApartmentStatus {
    AVAILABLE("apartment.available"),
    RESERVED("apartment.reserved"),
    OCCUPIED("apartment.occupied"),
    UNAVAILABLE("apartment.unavailable");

    String resName;
    ApartmentStatus(String resName){
        this.resName=resName;
    }

    public String getResName() {
        return resName;
    }
}
