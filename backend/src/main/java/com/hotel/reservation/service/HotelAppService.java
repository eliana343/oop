package com.hotel.reservation.service;

import com.hotel.reservation.entity.*;
import com.hotel.reservation.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * One place for all app logic — keeps the school project easy to follow.
 */
@Service
public class HotelAppService implements ApplicationRunner {

    public record RegisterReq(String fullName, String email, String phoneNumber, String password) {
    }

    public record LoginReq(String email, String password) {
    }

    public record BookReq(Long guestId, List<Long> roomIds, List<Long> serviceIds, String checkIn, int nights) {
    }

    public record PayReq(String paymentMethod, BigDecimal amountPaid) {
    }

    public record ReviewReq(Long guestId, Integer rating, String comment) {
    }

    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final OfferedServiceRepository offeredServiceRepository;
    private final ReservationRepository reservationRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final CancellationRepository cancellationRepository;
    private final ReviewRepository reviewRepository;

    public HotelAppService(
            HotelRepository hotelRepository,
            GuestRepository guestRepository,
            RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository,
            OfferedServiceRepository offeredServiceRepository,
            ReservationRepository reservationRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            CancellationRepository cancellationRepository,
            ReviewRepository reviewRepository
    ) {
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.offeredServiceRepository = offeredServiceRepository;
        this.reservationRepository = reservationRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.cancellationRepository = cancellationRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (hotelRepository.count() > 0) {
            return;
        }
        hotelRepository.save(new Hotel(
                "Grand OOP Hotel",
                "123 Campus Road",
                "+1-555-0100",
                "hello@hotel.example",
                "Simple demo hotel for class."
        ));
        RoomType s = roomTypeRepository.save(new RoomType("Standard", new BigDecimal("99"), "Basic room"));
        RoomType d = roomTypeRepository.save(new RoomType("Deluxe", new BigDecimal("149"), "Bigger bed"));
        roomRepository.save(new Room("101", s, s.getRoomPrice(), "AVAILABLE"));
        roomRepository.save(new Room("102", s, s.getRoomPrice(), "AVAILABLE"));
        roomRepository.save(new Room("201", d, d.getRoomPrice(), "AVAILABLE"));
        offeredServiceRepository.save(new OfferedService("EAT", "Breakfast", new BigDecimal("15")));
        offeredServiceRepository.save(new OfferedService("SPA", "Spa", new BigDecimal("40")));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> loadPageData() {
        Hotel h = hotelRepository.findAll().stream().findFirst().orElse(null);
        Map<String, Object> out = new LinkedHashMap<>();
        if (h != null) {
            out.put("hotel", Map.of(
                    "hotelName", h.getHotelName(),
                    "location", h.getLocation(),
                    "phoneNumber", h.getPhoneNumber(),
                    "email", h.getEmail(),
                    "description", h.getDescription() != null ? h.getDescription() : ""
            ));
        }
        out.put("roomTypes", roomTypeRepository.findAll().stream().map(this::roomTypeMap).toList());
        out.put("rooms", roomRepository.findByAvailabilityStatusIgnoreCase("AVAILABLE").stream().map(this::roomMap).toList());
        out.put("services", offeredServiceRepository.findAll().stream().map(this::serviceMap).toList());
        return out;
    }

    private Map<String, Object> roomTypeMap(RoomType t) {
        return Map.of("id", t.getId(), "typeName", t.getTypeName(), "roomPrice", t.getRoomPrice(), "description", n(t.getDescription()));
    }

    private Map<String, Object> roomMap(Room r) {
        return Map.of(
                "id", r.getId(),
                "roomNumber", r.getRoomNumber(),
                "typeName", r.getRoomType() != null ? r.getRoomType().getTypeName() : "",
                "pricePerNight", r.getPricePerNight(),
                "availabilityStatus", r.getAvailabilityStatus()
        );
    }

    private Map<String, Object> serviceMap(OfferedService s) {
        return Map.of("id", s.getId(), "serviceCode", s.getServiceCode(), "serviceName", s.getServiceName(), "servicePrice", s.getServicePrice());
    }

    private static String n(String s) {
        return s == null ? "" : s;
    }

    @Transactional
    public Map<String, Object> register(RegisterReq r) {
        if (r.email() == null || r.email().isBlank() || r.password() == null || r.password().isBlank()) {
            throw new IllegalArgumentException("Email and password required");
        }
        String email = r.email().trim();
        guestRepository.findByEmail(email).ifPresent(x -> {
            throw new IllegalArgumentException("Email already used");
        });
        Guest g = new Guest(
                UUID.randomUUID().toString().substring(0, 12),
                r.fullName() != null ? r.fullName() : "Guest",
                email,
                r.phoneNumber() != null ? r.phoneNumber() : "",
                r.password()
        );
        g = guestRepository.save(g);
        return guestMap(g, "Registered.");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginReq r) {
        Guest g = guestRepository.findByEmail(r.email().trim())
                .orElseThrow(() -> new IllegalArgumentException("Wrong email or password"));
        if (!g.getPassword().equals(r.password())) {
            throw new IllegalArgumentException("Wrong email or password");
        }
        return guestMap(g, "OK");
    }

    private Map<String, Object> guestMap(Guest g, String msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", g.getId());
        m.put("guestId", g.getGuestId());
        m.put("fullName", g.getFullName());
        m.put("email", g.getEmail());
        m.put("phoneNumber", g.getPhoneNumber());
        m.put("message", msg);
        return m;
    }

    @Transactional
    public Map<String, Object> book(BookReq r) {
        if (r.guestId() == null) {
            throw new IllegalArgumentException("guestId missing");
        }
        if (r.roomIds() == null || r.roomIds().isEmpty()) {
            throw new IllegalArgumentException("Pick at least one room");
        }
        if (r.nights() <= 0) {
            throw new IllegalArgumentException("Nights must be > 0");
        }
        LocalDate checkIn = LocalDate.parse(r.checkIn().trim());
        Guest guest = guestRepository.findById(r.guestId()).orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        List<Room> rooms = roomRepository.findAllById(r.roomIds());
        if (rooms.size() != r.roomIds().size()) {
            throw new IllegalArgumentException("Bad room id");
        }
        for (Room room : rooms) {
            if (!room.isAvailable()) {
                throw new IllegalArgumentException("Room " + room.getRoomNumber() + " not free");
            }
        }
        List<Long> sids = r.serviceIds() != null ? r.serviceIds() : List.of();
        List<OfferedService> svcs = sids.isEmpty() ? List.of() : offeredServiceRepository.findByIdIn(sids);
        if (svcs.size() != sids.size()) {
            throw new IllegalArgumentException("Bad service id");
        }

        Reservation res = new Reservation();
        res.setReservationId("R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        res.setGuest(guest);
        res.setCheckInDate(checkIn);
        res.setNumberOfNights(r.nights());
        res.setReservationStatus("CONFIRMED");
        res.setReservedRooms(new LinkedHashSet<>(rooms));
        res.setSelectedServices(new LinkedHashSet<>(svcs));
        res = reservationRepository.save(res);

        for (Room room : rooms) {
            room.updateAvailability(false);
            roomRepository.save(room);
        }
        return reservationMap(res);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReservation(String reservationId) {
        return reservationMap(reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Not found")));
    }

    @Transactional
    public Map<String, Object> makeInvoice(String reservationId) {
        Reservation res = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
        if ("CANCELLED".equalsIgnoreCase(res.getReservationStatus())) {
            throw new IllegalArgumentException("Cancelled");
        }
        invoiceRepository.findByReservation(res).ifPresent(x -> {
            throw new IllegalArgumentException("Invoice already made");
        });
        int nights = res.getNumberOfNights();
        BigDecimal roomTotal = BigDecimal.ZERO;
        for (Room room : res.getReservedRooms()) {
            roomTotal = roomTotal.add(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        }
        BigDecimal svcTotal = res.getSelectedServices().stream().map(OfferedService::getServicePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        Invoice inv = new Invoice(res);
        inv.setTotalRoomCost(roomTotal.setScale(2, RoundingMode.HALF_UP));
        inv.setTotalServiceCost(svcTotal.setScale(2, RoundingMode.HALF_UP));
        inv.calculateTotal();
        inv = invoiceRepository.save(inv);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("invoiceId", inv.getId());
        m.put("reservationId", res.getReservationId());
        m.put("totalRoomCost", inv.getTotalRoomCost());
        m.put("totalServiceCost", inv.getTotalServiceCost());
        m.put("totalAmount", inv.getTotalAmount());
        m.put("text", inv.displayInvoice());
        return m;
    }

    @Transactional
    public Map<String, Object> pay(Long invoiceId, PayReq p) {
        if (p.amountPaid() == null || p.amountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Pay a positive amount");
        }
        String method = p.paymentMethod() == null ? "CASH" : p.paymentMethod().trim().toUpperCase(Locale.ROOT);
        Invoice inv = invoiceRepository.findById(invoiceId).orElseThrow(() -> new IllegalArgumentException("No invoice"));
        boolean paid = paymentRepository.findByInvoice(inv).stream().anyMatch(x -> "PAID".equalsIgnoreCase(x.getPaymentStatus()));
        if (paid) {
            throw new IllegalArgumentException("Already paid");
        }
        BigDecimal due = inv.getTotalAmount();
        BigDecimal amt = p.amountPaid().setScale(2, RoundingMode.HALF_UP);
        String status = amt.compareTo(due) >= 0 ? "PAID" : "PARTIAL";
        Payment pay = new Payment(inv, method, amt, status);
        pay = paymentRepository.save(pay);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("paymentId", pay.getId());
        m.put("invoiceId", inv.getId());
        m.put("paymentMethod", pay.getPaymentMethod());
        m.put("amountPaid", pay.getAmountPaid());
        m.put("paymentStatus", pay.getPaymentStatus());
        m.put("receipt", pay.displayReceipt());
        return m;
    }

    @Transactional
    public Map<String, Object> cancel(String reservationId, String reason) {
        Reservation res = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
        if ("CANCELLED".equalsIgnoreCase(res.getReservationStatus())) {
            throw new IllegalArgumentException("Already cancelled");
        }
        cancellationRepository.findByReservation(res).ifPresent(x -> {
            throw new IllegalArgumentException("Already cancelled");
        });

        BigDecimal paid = BigDecimal.ZERO;
        Invoice inv = invoiceRepository.findByReservation(res).orElse(null);
        if (inv != null) {
            paid = paymentRepository.findByInvoice(inv).stream()
                    .filter(x -> "PAID".equalsIgnoreCase(x.getPaymentStatus()))
                    .map(Payment::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // Very simple rule: $20 fee if they paid something, else $0
        BigDecimal fee = paid.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("20.00") : BigDecimal.ZERO;
        Cancellation c = new Cancellation(res, reason != null ? reason : "");
        c.setCancellationFee(fee);
        c.calculateRefund(paid);
        cancellationRepository.save(c);
        res.setReservationStatus("CANCELLED");
        reservationRepository.save(res);
        for (Room room : res.getReservedRooms()) {
            room.updateAvailability(true);
            roomRepository.save(room);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reservationId", reservationId);
        m.put("cancellationFee", fee);
        m.put("refundAmount", c.getRefundAmount());
        m.put("message", "Cancelled.");
        return m;
    }

    @Transactional
    public Map<String, Object> review(ReviewReq r) {
        if (r.guestId() == null || r.rating() == null || r.rating() < 1 || r.rating() > 5) {
            throw new IllegalArgumentException("Need guestId and rating 1–5");
        }
        Guest g = guestRepository.findById(r.guestId()).orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        Review rev = new Review(g, g.getFullName(), r.rating(), r.comment());
        rev = reviewRepository.save(rev);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rev.getId());
        m.put("guestName", rev.getGuestName());
        m.put("rating", rev.getRating());
        m.put("comment", n(rev.getComment()));
        m.put("message", "Thanks for the review!");
        return m;
    }

    private Map<String, Object> reservationMap(Reservation r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reservationId", r.getReservationId());
        m.put("guestId", r.getGuest().getId());
        m.put("guestName", r.getGuest().getFullName());
        m.put("roomNumbers", r.getReservedRooms().stream().map(Room::getRoomNumber).collect(Collectors.toList()));
        m.put("serviceNames", r.getSelectedServices().stream().map(OfferedService::getServiceName).collect(Collectors.toList()));
        m.put("checkInDate", r.getCheckInDate().toString());
        m.put("numberOfNights", r.getNumberOfNights());
        m.put("reservationStatus", r.getReservationStatus());
        return m;
    }
}
