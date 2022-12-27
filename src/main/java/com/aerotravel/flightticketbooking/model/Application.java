package com.aerotravel.flightticketbooking.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;

@Entity
public class Application {

    @Id
    @GeneratedValue
    private int applicationId;
    @ManyToOne
    private Passenger passenger;
    private String action;
    private String status;

}
