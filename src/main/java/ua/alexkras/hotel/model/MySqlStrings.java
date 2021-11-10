package ua.alexkras.hotel.model;

public interface MySqlStrings {
    String root = "jdbc:mysql://localhost:3306/";
    String databaseName="hotel_db";
    String tableUser = "user";

    String colUserId = "ID";
    String colUserName = "NAME";
    String colUserSurname = "SURNAME";
    String colUserUsername = "USERNAME";
    String colUserPassword = "PASSWORD";
    String colUserPhoneNumber = "PHONE_NUMBER";
    String colUserBirthday = "BIRTHDAY";
    String colUserGender = "GENDER";
    String colUserUserType = "USER_TYPE";

    String[] tableUserColumns = new String[]{
        colUserId,colUserName, colUserSurname, colUserUsername,
                colUserPassword, colUserPhoneNumber, colUserBirthday,
                colUserGender, colUserUserType};

    String connectionUrl = root+databaseName+"?serverTimezone=UTC";

    String user = "root";
    String password = "root";


    String sqlCreateDatabaseIfNotExists = String.format("CREATE DATABASE IF NOT EXISTS %s;",
            databaseName);

    String sqlSelectColumnsFromUserDB = "SELECT %s FROM " + databaseName + "." + tableUser;

    String updateExpired = "UPDATE " +
            "hotel_db.reservations SET " +
            "status=?,"+
            "expired=true "+
            "WHERE not expired and not is_paid and " +
            "admin_confirmation_date is not null and " +
            "DATEDIFF(admin_confirmation_date,?)>=?";
    String setExpiredReservationApartmentsAvailable = "UPDATE " +
            "hotel_db.apartments SET "+
            "apartment_status=? "+
            "WHERE apartment_status='"+ApartmentStatus.RESERVED+"' and "+
            "id IN (SELECT apartment_id FROM hotel_db.reservations WHERE expired and is_active)";
    String updateActive = "UPDATE " +
            "hotel_db.reservations SET " +
            "is_active=false "+
            "WHERE is_active and expired ";

}
