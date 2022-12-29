package com.aerotravel.flightticketbooking.repository;

import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByPassengerAndActionAndStatus(Passenger passenger, String action, String status);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Application (action, status, passengerId) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void insertApplicationData(String action, String status, long passengerId);

    // @Query("FROM Application WHERE ")
    // Application findByPassengerId(long passengerId);
}