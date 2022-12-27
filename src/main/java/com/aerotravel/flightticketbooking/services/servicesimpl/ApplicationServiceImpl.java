package com.aerotravel.flightticketbooking.services.servicesimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aerotravel.flightticketbooking.model.Application;
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
}
