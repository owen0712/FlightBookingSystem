package com.aerotravel.flightticketbooking.repository;

import com.aerotravel.flightticketbooking.model.Application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}