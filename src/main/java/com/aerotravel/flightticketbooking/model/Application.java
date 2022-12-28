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

    public int getApplicationId(){
        return applicationId;
    }

    public void setApplicationId(int applicationId){
        this.applicationId = applicationId;
    }

    public Passenger getPassenger(){
        return passenger;
    }

    public void setPassenger(Passenger passenger){
        this.passenger = passenger;
    }

    public String getAction(){
        return action;
    }

    public void setAction(String action){
        this.action = action;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

}
