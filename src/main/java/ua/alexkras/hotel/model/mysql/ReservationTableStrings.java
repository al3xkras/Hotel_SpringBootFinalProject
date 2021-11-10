package ua.alexkras.hotel.model.mysql;

import static ua.alexkras.hotel.model.mysql.MySqlStrings.databaseName;

public interface ReservationTableStrings {
    String tableReservation="reservations";

    String colReservationId = "id";
    String colReservationUserId = "user_id";
    String colApartmentId = "apartment_id";
    String colApartmentClass = "apartment_class";
    String colApartmentPlaces = "places";
    String colApartmentPrice = "price";
    String colReservationStatus="reservation_status";
    String colFromDate = "from_date";
    String colToDate = "to_date";
    String colSubmitDate = "submit_date";
    String colAdminConfirmationDate = "confirmation_date";
    String colIsPaid = "id_paid";
    String colIsActive = "is_active";
    String colIsExpired = "is_expired";

    String updateAllExpiredReservations = "UPDATE " +
            databaseName+'.'+tableReservation+" SET " +
            colReservationStatus+"=?,"+
            colIsExpired+"=true "+
            "WHERE not "+colIsExpired+" and not "+colIsPaid+" and " +
            colAdminConfirmationDate+" is not null and " +
            "DATEDIFF("+colAdminConfirmationDate+",?)>=?";

    String updateActiveReservations = "UPDATE " +
            databaseName+'.'+tableReservation+" SET " +
            colIsActive+"=false "+
            "WHERE "+colIsActive+" and "+colIsExpired;

}
