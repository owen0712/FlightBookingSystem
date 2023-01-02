package com.aerotravel.flightticketbooking.services;

import java.util.List;
import org.springframework.data.domain.Page;
import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Passenger;

public interface ApplicationService {

    public abstract List<Application> getAllApplication();

    public abstract Application getApplicationByApplicationId(Integer application);

    public abstract Application saveApplication(Application application);

    public abstract void deleteApplicationById(Integer applicationId);

    public abstract Page<Application> getAllApplicationsPaged(int pageNum);

    public abstract Page<Application> getUserApplicationsPaged(int pageNum);

    public abstract Application getApplicationByPassengerAndActionAndStatus(Passenger passenger, String action,
            String status);

    public abstract Application getApplicationByPassenger(Long passengerId);

    public abstract void insertApplicationData(String action, String status, long passengerId);
}