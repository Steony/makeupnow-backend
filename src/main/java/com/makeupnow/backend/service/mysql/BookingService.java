package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.booking.BookingCreateDTO;
import com.makeupnow.backend.dto.booking.BookingResponseDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;
import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.repository.mysql.*;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.security.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private MakeupServiceRepository makeupServiceRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private UserActionLogService userActionLogService;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ReviewRepository reviewRepository; // <--- AJOUT

    @PreAuthorize("hasRole('CLIENT')")
@Transactional
public BookingResponseDTO createBooking(BookingCreateDTO dto) {
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

    Booking saved = bookingRepository.save(booking);

    // 🔥 Création automatique du paiement
    Payment payment = new Payment();
    payment.setBooking(saved);
    payment.setAmount(saved.getTotalPrice());
    payment.setStatus(PaymentStatus.PENDING);
    payment.setProvider(provider);

    paymentRepository.save(payment);

    userActionLogService.logActionByUserId(dto.getCustomerId(), "Création de réservation",
            "Réservation créée avec ID : " + saved.getId());

    return mapToResponseDTO(saved);
}


    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking non trouvé avec l'id : " + bookingId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentUserRole();

        if (!"ROLE_ADMIN".equals(currentRole) && !booking.getCustomer().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez pas annuler cette réservation.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        String action = "ROLE_ADMIN".equals(currentRole) ? "Annulation de réservation par admin" : "Annulation de réservation";
        userActionLogService.logActionByUserId(currentUserId, action,
                "Réservation ID : " + bookingId + " annulée.");
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<BookingResponseDTO> getBookingsByCustomer(Long customerId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentUserRole();

        if (!"ROLE_ADMIN".equals(currentRole) && !currentUserId.equals(customerId)) {
            throw new AccessDeniedException("Accès interdit à ces réservations.");
        }

        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<BookingResponseDTO> getBookingsByProvider(Long providerId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentUserRole();

        if (!"ROLE_ADMIN".equals(currentRole) && !currentUserId.equals(providerId)) {
            throw new AccessDeniedException("Accès interdit à ces réservations.");
        }

        return bookingRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
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

    dto.setCustomerEmail(booking.getCustomer().getEmail());
    dto.setCustomerPhone(booking.getCustomer().getPhoneNumber());
    dto.setCustomerAddress(booking.getCustomer().getAddress());

    dto.setProviderEmail(booking.getProvider().getEmail());
    dto.setProviderPhone(booking.getProvider().getPhoneNumber());
    dto.setProviderAddress(booking.getProvider().getAddress());

    dto.setCustomerName(booking.getCustomer().getFirstname() + " " + booking.getCustomer().getLastname());
    dto.setProviderName(booking.getProvider().getFirstname() + " " + booking.getProvider().getLastname());

    dto.setServiceTitle(booking.getService().getTitle());
    dto.setServiceDuration(String.valueOf(booking.getService().getDuration()));

    if (booking.getSchedule() != null) {
        dto.setDateSchedule(booking.getSchedule().getStartTime().toLocalDate());
        dto.setTimeSchedule(booking.getSchedule().getStartTime().toLocalTime());
    }

    // ==== AJOUT DE LA REVIEW ====
    if (booking.getService() != null && booking.getCustomer() != null) {
        Review review = reviewRepository.findByMakeupServiceIdAndCustomerIdAndBookingId(
            booking.getService().getId(),
            booking.getCustomer().getId(),
            booking.getId()
        ).orElse(null);

        if (review != null) {
            dto.setReview(mapToReviewDTO(review));
        }
    }

    // ==== AJOUT DU PAYMENTID ====
    if (booking.getPayment() != null) {
        dto.setPaymentId(booking.getPayment().getId());
    }
// ==== AJOUT DU PAYMENT STATUS ====
if (booking.getPayment() != null && booking.getPayment().getStatus() != null) {
    dto.setPaymentStatus(booking.getPayment().getStatus().name());
}


    return dto;
}


    /*** AJOUT: Méthode utilitaire pour mapper Review => ReviewResponseDTO ***/
    private ReviewResponseDTO mapToReviewDTO(Review review) {
        return ReviewResponseDTO.builder()
            .id(review.getId())
            .customerId(review.getCustomerId())
            .customerName(review.getCustomerName())
            .providerId(review.getProviderId())
            .providerName(review.getProviderName())
            
            .makeupServiceId(review.getMakeupServiceId())
            .rating(review.getRating())
            .comment(review.getComment())
            .dateComment(review.getDateComment())
            .build();
    }
}
