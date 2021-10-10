package ua.alexkras.hotel.model;

public enum ApartmentClass {
    ClassA("Class A"),
    ClassB("Class B"),
    ClassC("Class C"),
    ClassD("Class D");

    String apartmentName;

    ApartmentClass(String apartmentName){
        this.apartmentName=apartmentName;
    }

    public String getApartmentName() {
        return apartmentName;
    }
}
