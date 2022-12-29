package com.aerotravel.flightticketbooking.services.servicesimpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Passenger;
import com.aerotravel.flightticketbooking.repository.ApplicationRepository;
import com.aerotravel.flightticketbooking.services.ApplicationService;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public List<Application> getAllApplication() {
        return applicationRepository.findAll();
    }

    @Override
    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public void deleteApplicationById(long applicationId) {
        applicationRepository.deleteById(applicationId);
    }

    @Override
    public Page<Application> getAllApplicationsPaged(int pageNum) {
        return applicationRepository.findAll(PageRequest.of(pageNum, 5, Sort.by("status")));
    }

    @Override
    public Page<Application> getUserApplicationsPaged(int pageNum) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        System.out.println(currentPrincipalName);
        return applicationRepository.findAll(PageRequest.of(pageNum, 5, Sort.by("status")));
    }

    @Override
    public Application getApplicationByPassengerAndActionAndStatus(Passenger passenger, String action, String status) {
        return applicationRepository.findByPassengerAndActionAndStatus(passenger, action, status);
    }

    @Override
    public Application getApplicationByPassenger(Passenger passenger) {
        return null;
    }

    @Override
    public void insertApplicationData(String action, String status, long passengerId) {
        applicationRepository.insertApplicationData(action, status, passengerId);
    }
}
