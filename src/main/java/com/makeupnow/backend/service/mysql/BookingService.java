package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.booking.BookingCreateDTO;
import com.makeupnow.backend.dto.booking.BookingResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.*;

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
    private UserActionLogService userActionLogService;

    @PreAuthorize("hasRole('CUSTOMER')")
@Transactional
public BookingResponseDTO createBooking(BookingCreateDTO dto) {
    // Vérifier si le créneau est déjà réservé
    boolean isAlreadyBooked = bookingRepository.existsByScheduleIdAndStatusNot(
            dto.getScheduleId(), BookingStatus.CANCELLED);

    if (isAlreadyBooked) {
        throw new IllegalStateException("Ce créneau est déjà réservé.");
    }

    Customer customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer non trouvé avec l'id : " + dto.getCustomerId()));

    Provider provider = providerRepository.findById(dto.getProviderId())
            .orElseThrow(() -> new ResourceNotFoundException("Provider non trouvé avec l'id : " + dto.getProviderId()));

    MakeupService service = makeupServiceRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé avec l'id : " + dto.getServiceId()));

    Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Schedule non trouvé avec l'id : " + dto.getScheduleId()));

    Booking booking = Booking.builder()
            .customer(customer)
            .provider(provider)
            .service(service)
            .schedule(schedule)
            .totalPrice(dto.getTotalPrice())
            .status(BookingStatus.CONFIRMED)
            .build();

    Booking savedBooking = bookingRepository.save(booking);

    userActionLogService.logActionByUserId(dto.getCustomerId(), "Création de réservation",
            "Réservation créée avec ID : " + savedBooking.getId() + " pour le client ID : " + dto.getCustomerId());

    return mapToResponseDTO(savedBooking);
}





 @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Transactional
public void deleteBooking(Long bookingId, Long userId, Role userRole) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking non trouvé avec l'id : " + bookingId));

    booking.setStatus(BookingStatus.CANCELLED); 
    bookingRepository.save(booking);

    if (userRole == Role.ADMIN) {
        userActionLogService.logActionByUserId(userId, "Annulation de réservation par admin",
                "Réservation annulée avec ID : " + bookingId + " par l'admin ID : " + userId);
    } else {
        Long customerId = booking.getCustomer().getId();
        userActionLogService.logActionByUserId(customerId, "Annulation de réservation",
                "Réservation annulée avec ID : " + bookingId + " par le client ID : " + customerId);
    }
}



    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<BookingResponseDTO> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public List<BookingResponseDTO> getBookingsByProvider(Long providerId) {
        return bookingRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setDateBooking(booking.getDateBooking());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setProviderId(booking.getProvider().getId());
        dto.setServiceId(booking.getService().getId());
        dto.setScheduleId(booking.getSchedule().getId());
        return dto;
    }
}
