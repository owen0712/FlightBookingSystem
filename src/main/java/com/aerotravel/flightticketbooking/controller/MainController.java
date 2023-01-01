package com.aerotravel.flightticketbooking.controller;

import com.aerotravel.flightticketbooking.constants.Constants;
import com.aerotravel.flightticketbooking.model.Aircraft;
import com.aerotravel.flightticketbooking.model.Airport;
import com.aerotravel.flightticketbooking.model.Application;
import com.aerotravel.flightticketbooking.model.Flight;
import com.aerotravel.flightticketbooking.model.Passenger;
import com.aerotravel.flightticketbooking.model.User;
import com.aerotravel.flightticketbooking.services.AircraftService;
import com.aerotravel.flightticketbooking.services.AirportService;
import com.aerotravel.flightticketbooking.services.ApplicationService;
import com.aerotravel.flightticketbooking.services.FlightService;
import com.aerotravel.flightticketbooking.services.PassengerService;
import com.aerotravel.flightticketbooking.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {

    @Autowired
    AirportService airportService;

    @Autowired
    AircraftService aircraftService;

    @Autowired
    FlightService flightService;

    @Autowired
    PassengerService passengerService;

    @Autowired
    UserService userService;

    @Autowired
    ApplicationService applicationService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/airport/new")
    public String showAddAirportPage(Model model) {
        model.addAttribute("airport", new Airport());
        return "newAirport";
    }

    @PostMapping("/airport/new")
    public String saveAirport(@Valid @ModelAttribute("airport") Airport airport, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("airport", new Airport());
            return "newAirport";
        }
        airportService.saveAirport(airport);
        model.addAttribute("airports", airportService.getAllAirportsPaged(0));
        model.addAttribute("currentPage", 0);
        return "airports";
    }

    @GetMapping("/airport/delete")
    public String deleteAirport(@PathParam("airportId") int airportId, Model model) {
        airportService.deleteAirport(airportId);
        model.addAttribute("airports", airportService.getAllAirportsPaged(0));
        model.addAttribute("currentPage", 0);
        return "airports";
    }

    @GetMapping("/airports")
    public String showAirportsList(@RequestParam(defaultValue = "0") int pageNo, Model model) {
        model.addAttribute("airports", airportService.getAllAirportsPaged(pageNo));
        model.addAttribute("currentPage", pageNo);
        return "airports";
    }

    @GetMapping("/aircraft/new")
    public String showAddAircraftPage(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        return "newAircraft";
    }

    @PostMapping("/aircraft/new")
    public String saveAircraft(@Valid @ModelAttribute("aircraft") Aircraft aircraft, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("aircraft", new Aircraft());
            return "newAircraft";
        }
        aircraftService.saveAircraft(aircraft);
        model.addAttribute("aircrafts", aircraftService.getAllAircraftsPaged(0));
        model.addAttribute("currentPage", 0);
        return "aircrafts";
    }

    @GetMapping("/aircraft/delete")
    public String deleteAircraft(@PathParam("aircraftId") long aircraftId, Model model) {
        aircraftService.deleteAircraftById(aircraftId);
        model.addAttribute("aircrafts", aircraftService.getAllAircraftsPaged(0));
        model.addAttribute("currentPage", 0);
        return "aircrafts";
    }

    @GetMapping("/aircrafts")
    public String showAircraftsList(@RequestParam(defaultValue = "0") int pageNo, Model model) {
        model.addAttribute("aircrafts", aircraftService.getAllAircraftsPaged(pageNo));
        model.addAttribute("currentPage", pageNo);
        return "aircrafts";
    }

    @GetMapping("/flight/new")
    public String showNewFlightPage(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
        model.addAttribute("airports", airportService.getAllAirports());
        return "newFlight";
    }

    @PostMapping("/flight/new")
    public String saveFlight(@Valid @ModelAttribute("flight") Flight flight, BindingResult bindingResult,
            @RequestParam("departureAirport") int departureAirport,
            @RequestParam("destinationAirport") int destinationAirport,
            @RequestParam("aircraft") long aircraftId,
            @RequestParam("arrivalTime") String arrivalTime,
            @RequestParam("departureTime") String departureTime,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("flight", new Flight());
            model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
            model.addAttribute("airports", airportService.getAllAirports());
            return "newFlight";
        }
        if (departureAirport == destinationAirport) {
            model.addAttribute("sameAirportError", "Departure and destination airport can't be same");
            model.addAttribute("flight", new Flight());
            model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
            model.addAttribute("airports", airportService.getAllAirports());
            return "newFlight";
        }

        flight.setAircraft(aircraftService.getAircraftById(aircraftId));
        flight.setDepartureAirport(airportService.getAirportById(departureAirport));
        flight.setDestinationAirport(airportService.getAirportById(destinationAirport));
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flightService.saveFlight(flight);

        model.addAttribute("flights", flightService.getAllFlightsPaged(0));
        model.addAttribute("currentPage", 0);
        return "flights";
    }

    @GetMapping("/flight/delete")
    public String deleteFlight(@PathParam("flightId") long flightId, Model model) {
        flightService.deleteFlightById(flightId);
        model.addAttribute("flights", flightService.getAllFlightsPaged(0));
        model.addAttribute("currentPage", 0);
        return "flights";
    }

    @GetMapping("/flights")
    public String showFlightsList(@RequestParam(defaultValue = "0") int pageNo, Model model) {
        model.addAttribute("flights", flightService.getAllFlightsPaged(pageNo));
        model.addAttribute("currentPage", pageNo);
        return "flights";
    }

    @GetMapping("/flight/search")
    public String showSearchFlightPage(Model model) {
        model.addAttribute("airports", airportService.getAllAirports());
        model.addAttribute("flights", null);
        return "searchFlight";
    }

    @PostMapping("/flight/search")
    public String searchFlight(@RequestParam("departureAirport") int departureAirport,
            @RequestParam("destinationAirport") int destinationAirport,
            @RequestParam("departureTime") String departureTime,
            Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate deptTime = LocalDate.parse(departureTime, dtf);
        Airport depAirport = airportService.getAirportById(departureAirport);
        Airport destAirport = airportService.getAirportById(destinationAirport);

        if (departureAirport == destinationAirport) {
            model.addAttribute("AirportError", "Departure and destination airport cant be same!");
            model.addAttribute("airports", airportService.getAllAirports());
            return "searchFlight";
        }
        List<Flight> flights = flightService.getAllFlightsByAirportAndDepartureTime(depAirport, destAirport, deptTime);
        if (flights.isEmpty()) {
            model.addAttribute("notFound", "No Record Found!");
        } else {
            model.addAttribute("flights", flights);
        }

        model.addAttribute("airports", airportService.getAllAirports());
        return "searchFlight";
    }

    @GetMapping("/flight/book")
    public String showBookFlightPage(Model model) {
        model.addAttribute("airports", airportService.getAllAirports());
        return "bookFlight";
    }

    @PostMapping("/flight/book")
    public String searchFlightToBook(@RequestParam("departureAirport") int departureAirport,
            @RequestParam("destinationAirport") int destinationAirport,
            @RequestParam("departureTime") String departureTime,
            Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate deptTime = LocalDate.parse(departureTime, dtf);
        Airport depAirport = airportService.getAirportById(departureAirport);
        Airport destAirport = airportService.getAirportById(destinationAirport);

        if (departureAirport == destinationAirport) {
            model.addAttribute("AirportError", "Departure and destination airport cant be same!");
            model.addAttribute("airports", airportService.getAllAirports());
            return "bookFlight";
        }
        List<Flight> flights = flightService.getAllFlightsByAirportAndDepartureTime(depAirport, destAirport, deptTime);
        if (flights.isEmpty()) {
            model.addAttribute("notFound", "No Record Found!");
        } else {
            model.addAttribute("flights", flights);
        }
        model.addAttribute("airports", airportService.getAllAirports());
        return "bookFlight";
    }

    @GetMapping("/flight/book/new")
    public String showCustomerInfoPage(@RequestParam long flightId, Model model) {
        model.addAttribute("flightId", flightId);
        model.addAttribute("passenger", new Passenger());
        return "newPassenger";
    }

    @PostMapping("/flight/book/new")
    public String bookFlight(@Valid @ModelAttribute("passenger") Passenger passenger, BindingResult bindingResult,
            @RequestParam("flightId") long flightId, Model model) {
        Flight flight = flightService.getFlightById(flightId);
        Passenger passenger1 = passenger;
        passenger1.setFlight(flight);
        passengerService.savePassenger(passenger1);
        model.addAttribute("passenger", passenger1);
        return "confirmationPage";
    }

    @GetMapping("/flight/book/verify")
    public String showVerifyBookingPage() {
        return "verifyBooking";
    }

    @PostMapping("/flight/book/verify")
    public String showVerifyBookingPageResult(@RequestParam("flightId") long flightId,
            @RequestParam("passengerId") long passengerId,
            Model model) {

        Flight flight = flightService.getFlightById(flightId);
        if (flight != null) {
            model.addAttribute("flight", flight);
            List<Passenger> passengers = flight.getPassengers();
            Passenger passenger = null;
            for (Passenger p : passengers) {
                if (p.getPassengerId() == passengerId) {
                    passenger = passengerService.getPassengerById(passengerId);
                    model.addAttribute("passenger", passenger);
                }
            }
            if (passenger != null) {
                return "verifyBooking";
            } else {
                model.addAttribute("notFound", "Not Found");
                return "verifyBooking";
            }
        } else {
            model.addAttribute("notFound", "Not Found");
            return "verifyBooking";
        }

    }

    @PostMapping("/flight/book/cancel")
    public String cancelTicket(@RequestParam("passengerId") long passengerId, Model model) {
        passengerService.deletePassengerById(passengerId);
        model.addAttribute("flights", flightService.getAllFlightsPaged(0));
        model.addAttribute("currentPage", 0);
        return "flights";
    }

    @GetMapping("passengers")
    public String showPassengerList(@RequestParam long flightId, Model model) {
        List<Passenger> passengers = flightService.getFlightById(flightId).getPassengers();
        model.addAttribute("passengers", passengers);
        model.addAttribute("flight", flightService.getFlightById(flightId));
        return "passengers";
    }

    @GetMapping("/userapplications")
    public String showApplicationsList(@RequestParam(defaultValue = "0") int pageNo, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Page<Application> applicationList = applicationService.getUserApplicationsPaged(pageNo);
        if (applicationList.isEmpty()) {
            model.addAttribute("notFound", "Not Found");
        } else {
            model.addAttribute("userapplications", applicationList);
            model.addAttribute("currentPage", pageNo);
        }
        return "userapplications";
    }

    @GetMapping("/userapplications/new")
    public String showNewApplicationPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");

        model.addAttribute("actions", action);
        model.addAttribute("passengers", passengerService.getAllPassengersByEmail(user.getEmail()));
        model.addAttribute("applications", new Application());
        return "newApplication";
    }

    @GetMapping("/userapplications/update")
    public String showUpdateApplicationPage(@PathParam("applicationId") Integer applicationId, Model model) {
        Application application = applicationService.getApplicationByApplicationId(applicationId);
        model.addAttribute("applications", application);

        String actionType = application.getAction();
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");

        model.addAttribute("actions", action);

        return "updateApplication";
    }

    @PostMapping("/userapplications/checkUpdate")
    public String updateApplication(@PathParam("applicationId") Integer applicationId,
            @RequestParam("actions") String chosenAction,
            Model model) {
        Application application = applicationService.getApplicationByApplicationId(applicationId);
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
            applicationService.saveApplication(application);
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

    @PostMapping("/userapplications/updateForm")
    public String updateApplicationForm(@Valid @ModelAttribute("passenger") Passenger passenger,
            BindingResult bindingResult,
            @PathParam("flightId") long flightId, @PathParam("applicationId") Integer applicationId, Model model) {
        Application application = applicationService.getApplicationByApplicationId(applicationId);

        application.setFlightId(flightId);

        String actionType = "Update";
        application.setAction(actionType);
        model.addAttribute("actionType", actionType);

        List<String> action = new ArrayList<>();
        action.add("Delete");
        action.add("Update");
        model.addAttribute("actions", action);

        applicationService.saveApplication(application);

        model.addAttribute("passenger", null);
        model.addAttribute("applications", application);
        model.addAttribute("successful", "Successfully");
        return "updateApplication";
    }

    @PostMapping("/flight/book/updateForm")
    public String searchFlightToBookForUpdate(@RequestParam("departureAirport") int departureAirport,
            @RequestParam("destinationAirport") int destinationAirport,
            @RequestParam("departureTime") String departureTime, @PathParam("applicationId") Integer applicationId,
            Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate deptTime = LocalDate.parse(departureTime, dtf);
        Airport depAirport = airportService.getAirportById(departureAirport);
        Airport destAirport = airportService.getAirportById(destinationAirport);

        Application application = applicationService.getApplicationByApplicationId(applicationId);
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

    @PostMapping("/application/new")
    public String createNewApplication(@RequestParam(defaultValue = "0") int pageNo,
            @RequestParam("actions") String action, @RequestParam("passengers") Long passengerId,
            Model model) {

        if (Objects.isNull(action)) {
            model.addAttribute("error", "action and booking id cannot be empty");
        } else {

            Passenger passenger = passengerService.getPassengerById(passengerId);

            if (applicationService.getApplicationByPassengerAndActionAndStatus(passenger, action,
                    Constants.PENDING) != null) {
                model.addAttribute("exist", "Same application exist ady. Please wait!");
            } else {
                Application application = new Application();
                application.setAction(action);
                application.setStatus(Constants.PENDING);
                application.setPassengerId(passenger.getPassengerId());

                applicationService.saveApplication(application);
                model.addAttribute("successful", "Successful");

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                model.addAttribute("passengers", passengerService.getAllPassengersByEmail(user.getEmail()));
            }
        }

        return "newApplication";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("fancy")
    public String showLoginPage1() {
        return "index";
    }

    @GetMapping("/view/booking")
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

    @GetMapping("/delete/application")
    public String saveDeleteApplication(@PathParam("passengerId") long passengerId, Model model) {
        Passenger passenger = passengerService.getPassengerById(passengerId);
        Application sameApplicationInDb = applicationService.getApplicationByPassengerAndActionAndStatus(passenger,
                Constants.DELETE, Constants.PENDING);
        if (!Objects.isNull(sameApplicationInDb)) {
            model.addAttribute("alreadyExist", "Already Exist");
        } else {
            Application application = new Application();
            application.setPassengerId(passenger.getPassengerId());
            application.setAction(Constants.DELETE);
            application.setStatus(Constants.PENDING);
            applicationService.saveApplication(application);
            model.addAttribute("successful", "Successful");
        }

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
}
