package com.aerotravel.flightticketbooking.repository;

import com.aerotravel.flightticketbooking.model.Passenger;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findAllByEmailAndStatusEquals(String email, String status);

    Passenger findByPassengerId(Long passengerId);
}
