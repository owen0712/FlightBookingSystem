package com.aerotravel.flightticketbooking.services;

import java.util.List;
import org.springframework.data.domain.Page;
import com.aerotravel.flightticketbooking.model.Application;

public interface ApplicationService {

    public abstract List<Application> getAllApplication();

    public abstract Application saveApplication(Application application);

    public abstract void deleteApplicationById(long applicationId);

    public abstract Page<Application> getAllApplicationsPaged(int pageNum);

    public abstract Page<Application> getUserApplicationsPaged(int pageNum);
}