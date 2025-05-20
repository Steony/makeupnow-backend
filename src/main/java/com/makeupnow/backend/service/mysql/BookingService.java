package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Booking;
import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.Schedule;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.BookingRepository;
import com.makeupnow.backend.repository.mysql.CustomerRepository;
import com.makeupnow.backend.repository.mysql.MakeupServiceRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private MakeupServiceRepository makeupServiceRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserActionLogService userActionLogService; // injection du service log

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Booking createBooking(Long customerId, Long providerId, Long serviceId, Long scheduleId, double totalPrice) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer non trouvé avec l'id : " + customerId));

        Provider provider = providerRepository.findById(providerId)
            .orElseThrow(() -> new ResourceNotFoundException("Provider non trouvé avec l'id : " + providerId));

        MakeupService service = makeupServiceRepository.findById(serviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé avec l'id : " + serviceId));

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule non trouvé avec l'id : " + scheduleId));

        Booking booking = Booking.builder()
            .customer(customer)
            .provider(provider)
            .service(service)
            .schedule(schedule)
            .totalPrice(totalPrice)
            .status(BookingStatus.CONFIRMED)  // statut par défaut
            .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Log création réservation
        userActionLogService.logActionByUserId(customerId, "Création de réservation",
            "Réservation créée avec ID : " + savedBooking.getId() + " pour le client ID : " + customerId);

        return savedBooking;
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Transactional
    public void deleteBooking(Long bookingId, Long userId, Role userRole) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking non trouvé avec l'id : " + bookingId));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        if (userRole == Role.ADMIN) {
            // Log annulation par admin
            userActionLogService.logActionByUserId(userId, "Annulation de réservation par admin",
                "Réservation annulée avec ID : " + bookingId + " par l'admin ID : " + userId);
        } else {
            // Log annulation par client
            Long customerId = booking.getCustomer().getId();
            userActionLogService.logActionByUserId(customerId, "Annulation de réservation",
                "Réservation annulée avec ID : " + bookingId + " par le client ID : " + customerId);
        }
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<Booking> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public List<Booking> getBookingsByProvider(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
