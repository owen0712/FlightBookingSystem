package com.aerotravel.flightticketbooking.repository;

import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByPassengerAndActionAndStatus(Passenger passenger, String action, String status);

    // @Query("INSERT INTO Application (applicationId, action, status, passengerId) VALUES (?1, ?2, ?3, ?4)")
    // void insertApplicationData(int applicationId, String action, String status, long passengerId);
}