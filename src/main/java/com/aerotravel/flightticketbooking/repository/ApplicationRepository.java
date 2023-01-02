package com.aerotravel.flightticketbooking.repository;

import com.aerotravel.flightticketbooking.model.Application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    Application findByActionAndStatusAndPassengerId(String action, String status, Long passengerId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Application (action, status, passengerId) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void insertApplicationData(String action, String status, long passengerId);

    @Query("FROM Application WHERE passengerId = ?1 ")
    Application findByPassengerId(long passengerId);

    Application findByApplicationId(Integer applicationId);

    Page<Application> findByUserId(Integer userId, Pageable pageable);
}