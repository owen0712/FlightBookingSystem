package com.aerotravel.flightticketbooking.services.servicesimpl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.aerotravel.flightticketbooking.constants.Constants;
import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Passenger;
import com.aerotravel.flightticketbooking.model.User;
import com.aerotravel.flightticketbooking.repository.ApplicationRepository;
import com.aerotravel.flightticketbooking.services.ApplicationService;
import com.aerotravel.flightticketbooking.services.PassengerService;
import com.aerotravel.flightticketbooking.services.UserService;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PassengerService passengerService;

    @Override
    public List<Application> getAllApplication() {
        return applicationRepository.findAll();
    }

    @Override
    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public void deleteApplicationById(Integer applicationId) {
        applicationRepository.deleteById(applicationId);
    }

    @Override
    public Page<Application> getAllApplicationsPaged(int pageNum) {
        return applicationRepository.findAll(PageRequest.of(pageNum, 5, Sort.by("status")));
    }

    @Override
    public Page<Application> getUserApplicationsPaged(int pageNum, Integer userId) {
        return applicationRepository.findByUserId(userId,PageRequest.of(pageNum, 5, Sort.by("status")));
    }

    @Override
    public Application getApplicationByPassengerAndActionAndStatus(Passenger passenger, String action, String status) {
        return applicationRepository.findByActionAndStatusAndPassengerId(action, status, passenger.getPassengerId());
    }

    @Override
    public Application getApplicationByPassenger(Long passengerId) {
        return applicationRepository.findByPassengerId(passengerId);
    }

    @Override
    public void insertApplicationData(String action, String status, long passengerId) {
        applicationRepository.insertApplicationData(action, status, passengerId);
    }

    @Override
    public Application getApplicationByApplicationId(Integer applicationId){
        return applicationRepository.findByApplicationId(applicationId);
    }

    @Override
    public String showBookingsList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);

        List<Passenger> passengerList = passengerService.getAllPassengersByEmail(user.getEmail());
        if (CollectionUtils.isEmpty(passengerList)) {
            model.addAttribute("notFound", "Not Found");
        } else {
            model.addAttribute("passengerList", passengerList);
        }
        return "viewBooking";
    }

    @Override
    public String saveDeleteApplication(long passengerId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Passenger passenger = passengerService.getPassengerById(passengerId);
        Application sameApplicationInDb = getApplicationByPassengerAndActionAndStatus(passenger,
                Constants.DELETE, Constants.PENDING);
        if (!Objects.isNull(sameApplicationInDb)) {
            model.addAttribute("alreadyExist", "Already Exist");
        } else {
            Application application = new Application();
            application.setPassengerId(passenger.getPassengerId());
            application.setAction(Constants.DELETE);
            application.setStatus(Constants.PENDING);
            application.setUserId(user.getId());
            saveApplication(application);
            model.addAttribute("successful", "Successful");
        }

        model.addAttribute("user", user);

        List<Passenger> passengerList = passengerService.getAllPassengersByEmail(user.getEmail());
        if (CollectionUtils.isEmpty(passengerList)) {
            model.addAttribute("notFound", "Not Found");
        } else {
            model.addAttribute("passengerList", passengerList);
        }

        return "viewBooking";
    }
}
