package com.aerotravel.flightticketbooking.services.servicesimpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.aerotravel.flightticketbooking.model.Airport;
import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Flight;
import com.aerotravel.flightticketbooking.model.Passenger;
import com.aerotravel.flightticketbooking.model.User;
import com.aerotravel.flightticketbooking.repository.ApplicationRepository;
import com.aerotravel.flightticketbooking.services.AirportService;
import com.aerotravel.flightticketbooking.services.ApplicationService;
import com.aerotravel.flightticketbooking.services.FlightService;
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

    @Autowired
    private AirportService airportService;

    @Autowired
    private FlightService flightService;

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
        return applicationRepository.findByUserId(userId, PageRequest.of(pageNum, 5, Sort.by("status")));
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
    public Application getApplicationByApplicationId(Integer applicationId) {
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

    @Override
    public String updateApplication(Integer applicationId, String chosenAction, Model model) {

        Application application = getApplicationByApplicationId(applicationId);
        model.addAttribute("applications", application);

        String actionType = application.getAction();
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");

        model.addAttribute("actions", action);

        if (application.getAction().equals(chosenAction)) {
            model.addAttribute("sameAction", "same Action");
        } else if (chosenAction.equals("Delete")) {
            application.setAction(chosenAction);
            saveApplication(application);
            model.addAttribute("successful", "Successfully");
        } else if (chosenAction.equals("Update")) {
            Passenger passenger = passengerService.getPassengerById(application.getPassengerId());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            model.addAttribute("users", user);
            model.addAttribute("passenger", passenger);
            model.addAttribute("airports", airportService.getAllAirports());
        }

        return "updateApplication";
    }

    @Override
    public String updateApplicationForm(Passenger passenger, long flightId, Integer applicationId, Model model) {

        Application application = getApplicationByApplicationId(applicationId);

        application.setFlightId(flightId);

        String actionType = "Update";
        application.setAction(actionType);
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");
        model.addAttribute("actions", action);

        saveApplication(application);

        model.addAttribute("passenger", null);
        model.addAttribute("applications", application);
        model.addAttribute("successful", "Successfully");
        return "updateApplication";
    }

    @Override
    public String searchFlightToBookForUpdate(int departureAirport, int destinationAirport, String departureTime,
            Integer applicationId, Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate deptTime = LocalDate.parse(departureTime, dtf);
        Airport depAirport = airportService.getAirportById(departureAirport);
        Airport destAirport = airportService.getAirportById(destinationAirport);

        Application application = getApplicationByApplicationId(applicationId);
        model.addAttribute("applications", application);

        String actionType = "Update";
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");

        model.addAttribute("actions", action);

        Passenger passenger = passengerService.getPassengerById(application.getPassengerId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("users", user);
        model.addAttribute("passenger", passenger);
        model.addAttribute("airports", airportService.getAllAirports());

        if (departureAirport == destinationAirport) {
            model.addAttribute("AirportError", "Departure and destination airport cant be same!");
            model.addAttribute("airports", airportService.getAllAirports());
            return "updateApplication";
        }

        List<Flight> flights = flightService.getAllFlightsByAirportAndDepartureTime(depAirport, destAirport, deptTime);
        if (flights.isEmpty()) {
            model.addAttribute("notFound", "No Record Found!");
        } else {
            model.addAttribute("flights", flights);
        }
        model.addAttribute("airports", airportService.getAllAirports());
        return "updateApplication";
    }

    @Override
    public String createNewApplication(int pageNo, String action, Long passengerId, Model model) {
        List<String> actions = new ArrayList<>();
        actions.add("Delete");
        actions.add("Update");
        model.addAttribute("actions", actions);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (Objects.isNull(action)) {
            model.addAttribute("error", "action and booking id cannot be empty");
        } else {

            Application tempApplication = getApplicationByPassenger(passengerId);

            Passenger passenger = passengerService.getPassengerById(passengerId);

            if (tempApplication != null) {
                if (tempApplication.getStatus().equals("Approved")) {
                    model.addAttribute("existApproved", "Same application exist ady. Please wait for approvement!");
                    String actionType = null;
                    model.addAttribute("actionType", actionType);
                } else if ((tempApplication.getAction().equals(action))) {
                    model.addAttribute("exist", "Same application exist ady. Please wait for approvement!");
                    String actionType = null;
                    model.addAttribute("actionType", actionType);
                    model.addAttribute("applications", new Application());
                } else {
                    if (action.equals("Delete")) {
                        tempApplication.setAction(action);
                        saveApplication(tempApplication);
                        model.addAttribute("successful", "Successful");
                    } else {
                        if (action.equals("Update")) {
                            if (passenger.getFlight().getFlightId() == passengerId) {
                                model.addAttribute("error", "You cannot update to same flight!!!!");
                            }
                            model.addAttribute("applications", tempApplication);
                            model.addAttribute("updateForm", "updateForm");
                            String actionType = tempApplication.getAction();
                            model.addAttribute("actionType", actionType);
                            model.addAttribute("airports", airportService.getAllAirports());
                        }
                    }
                }
            } else {
                if (action.equals("Delete") && tempApplication == null) {
                    Application application = new Application();
                    application.setAction(action);
                    application.setStatus(Constants.PENDING);
                    application.setPassengerId(passenger.getPassengerId());

                    saveApplication(application);
                    model.addAttribute("successful", "Successful");
                } else {
                    Application application = new Application();
                    if (tempApplication == null) {
                        application.setAction(action);
                        application.setStatus(Constants.PENDING);
                        application.setPassengerId(passenger.getPassengerId());
                        model.addAttribute("applications", application);
                    } else {
                        model.addAttribute("applications", tempApplication);
                    }

                    model.addAttribute("updateForm", "updateForm");
                    String actionType = application.getAction();
                    model.addAttribute("actionType", actionType);
                    model.addAttribute("airports", airportService.getAllAirports());
                }
            }

        }
        model.addAttribute("passengers", passengerService.getAllPassengersByEmail(user.getEmail()));
        return "newApplication";
    }

    @Override
    public String searchFlightToBookForUpdateInNewApplication(int departureAirport, int destinationAirport,
            String departureTime, Integer applicationId, Model model) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate deptTime = LocalDate.parse(departureTime, dtf);
        Airport depAirport = airportService.getAirportById(departureAirport);
        Airport destAirport = airportService.getAirportById(destinationAirport);

        Application application = getApplicationByApplicationId(applicationId);
        model.addAttribute("applications", application);

        String actionType = "Update";
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");

        model.addAttribute("actions", action);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("users", user);
        model.addAttribute("passengers", passengerService.getAllPassengersByEmail(user.getEmail()));
        model.addAttribute("airports", airportService.getAllAirports());

        if (departureAirport == destinationAirport) {
            model.addAttribute("AirportError", "Departure and destination airport cant be same!");
            model.addAttribute("airports", airportService.getAllAirports());
            return "newApplication";
        }

        List<Flight> flights = flightService.getAllFlightsByAirportAndDepartureTime(depAirport, destAirport, deptTime);
        if (flights.isEmpty()) {
            model.addAttribute("notFound", "No Record Found!");
        } else {
            model.addAttribute("flights", flights);
            model.addAttribute("updateForm", "updateForm");
        }
        model.addAttribute("airports", airportService.getAllAirports());
        return "newApplication";
    }

    @Override
    public String updateApplicationFormInNewApplication(long flightId, Integer applicationId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Application application = getApplicationByApplicationId(applicationId);

        application.setFlightId(flightId);

        String actionType = "Update";
        application.setAction(actionType);
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");
        model.addAttribute("actions", action);

        Passenger passenger = passengerService.getPassengerById(application.getPassengerId());

        List<Passenger> passengerList = passengerService.getAllPassengersByEmail(user.getEmail());

        Passenger tempPassanger = passengerList.stream()
                .filter(customer -> passenger.getPassengerId() == (customer.getPassengerId()))
                .findAny()
                .orElse(null);

        if (tempPassanger != null) {
            if (tempPassanger.getFlight().getFlightId() == flightId) {
                model.addAttribute("error", "You had update to same flight, not allowed!!!!");
            } else {
                saveApplication(application);
                model.addAttribute("successful", "Successfully");
            }
        } else {
            saveApplication(application);
            model.addAttribute("successful", "Successfully");
        }

        model.addAttribute("passengers", passengerService.getAllPassengersByEmail(user.getEmail()));
        model.addAttribute("applications", application);

        return "newApplication";
    }

}
