package ua.alexkras.hotel;

import ua.alexkras.hotel.model.mysql.ApartmentTableStrings;
import ua.alexkras.hotel.model.mysql.MySqlStrings;
import ua.alexkras.hotel.model.mysql.ReservationTableStrings;
import ua.alexkras.hotel.model.mysql.UserTableStrings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static ua.alexkras.hotel.model.mysql.MySqlStrings.databaseName;
import static ua.alexkras.hotel.model.mysql.PaymentTableStrings.*;
import static ua.alexkras.hotel.model.mysql.PaymentTableStrings.colReservationId;
import static ua.alexkras.hotel.model.mysql.PaymentTableStrings.colUserId;
import static ua.alexkras.hotel.model.mysql.ReservationTableStrings.*;
import static ua.alexkras.hotel.model.mysql.UserTableStrings.tableUser;

public class FirstLaunch {
    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(MySqlStrings.root, MySqlStrings.user, MySqlStrings.password)){
            createDatabase(connection);
        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void createDatabase(Connection conn){
        try (PreparedStatement createDB = conn.prepareStatement(MySqlStrings.sqlCreateDatabaseIfNotExists);
             PreparedStatement createUserTable = conn.prepareStatement(UserTableStrings.sqlCreateUserTableIfNotExists);
             PreparedStatement createApartmentTable = conn.prepareStatement(ApartmentTableStrings.sqlCreateApartmentTableIfNotExists);
             PreparedStatement createReservationTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "+
                     databaseName+"."+tableReservation+" ("+
                     ReservationTableStrings.colReservationId+" int unique primary key auto_increment, "+
                     ReservationTableStrings.colUserId+" int not null, FOREIGN KEY ("+ReservationTableStrings.colUserId+")"+
                     " REFERENCES "+databaseName+"."+tableUser+"("+UserTableStrings.colUserId+")" +
                     " ON DELETE CASCADE,"+
                     colApartmentId+" int, FOREIGN KEY ("+colApartmentId+")" +
                     "REFERENCES "+databaseName+"."+ApartmentTableStrings.tableApartment+"("+ApartmentTableStrings.colApartmentId+") ON DELETE NO ACTION, "+
                     colApartmentClass+" varchar(20) not null,"+
                     colApartmentPlaces+" int not null,"+
                     colApartmentPrice+" int,"+
                     colReservationStatus+" varchar(20) not null,"+
                     colFromDate+" DATE not null,"+
                     colToDate+" DATE not null,"+
                     colSubmitDate+" DATETIME not null,"+
                     colAdminConfirmationDate+" DATE,"+
                     colIsPaid+" boolean default 0,"+
                     colIsActive+" boolean default 1,"+
                     colIsExpired+" boolean default 0) ENGINE=INNODB;");
             PreparedStatement createPaymentsTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "+
                     databaseName+"."+tablePayments+" ("+
                     colPaymentId+" int unique primary key auto_increment, "+
                     colUserId+" int not null," +
                     "FOREIGN KEY ("+colUserId+") REFERENCES "+databaseName+"."+tableUser+"("+UserTableStrings.colUserId+") ON DELETE NO ACTION,"+
                     colReservationId+" int not null," +
                     "FOREIGN KEY ("+colReservationId+") REFERENCES  "+databaseName+"."+tableReservation+"("+ReservationTableStrings.colReservationId+") ON DELETE NO ACTION,"+
                     colValue+" int not null,"+
                     colPaymentDate+" DATETIME not null,"+
                     colCardNumber+" varchar(40) not null,"+
                     colCardExpirationDate+" DATE not null,"+
                     colCardCvv+" varchar(3) not null) ENGINE=INNODB;")
        ){
            createDB.execute();
            createUserTable.execute();
            createApartmentTable.execute();
            createReservationTable.execute();
            createPaymentsTable.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create database: "+ databaseName);
        }
    }



}
