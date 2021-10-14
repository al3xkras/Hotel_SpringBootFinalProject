package ua.alexkras.hotel.model;

public enum ApartmentClass {
    ClassA("apartment.class_a"),
    ClassB("apartment.class_b"),
    ClassC("apartment.class_c"),
    ClassD("apartment.class_d");

    String resName;

    ApartmentClass(String resName){
        this.resName=resName;
    }

    public String getResName() {
        return resName;
    }
}
