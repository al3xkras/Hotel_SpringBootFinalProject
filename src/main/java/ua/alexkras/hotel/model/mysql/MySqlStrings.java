package ua.alexkras.hotel.model.mysql;

import ua.alexkras.hotel.model.ApartmentStatus;

import static ua.alexkras.hotel.model.mysql.ReservationTableStrings.*;
import static ua.alexkras.hotel.model.mysql.ApartmentTableStrings.*;

public interface MySqlStrings {
    String root = "jdbc:mysql://localhost:3306/";
    String databaseName="hotel_db_test";
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
            databaseName+"."+tableReservation+" SET " +
            colReservationStatus+"=?,"+
            colIsExpired+"=true "+
            "WHERE not "+colIsExpired+" and not "+colIsPaid+" and " +
            colAdminConfirmationDate+" is not null and " +
            "DATEDIFF("+colAdminConfirmationDate+",?)>=?";

    String setExpiredReservationApartmentsAvailable = "UPDATE " +
            "hotel_db."+tableApartment+" SET "+
            colApartmentStatus+"=? "+
            "WHERE "+colApartmentStatus+"='"+ ApartmentStatus.RESERVED+"' and "+
            "id IN (SELECT "+ReservationTableStrings.colApartmentId+" FROM "+databaseName+"."+tableReservation+" WHERE "+
            colIsExpired+" and "+colIsActive+")";

    String updateActive = "UPDATE " +
            databaseName+"."+tableReservation+" SET " +
            colIsActive+"=false "+
            "WHERE "+colIsActive+" and "+colIsExpired;

}
