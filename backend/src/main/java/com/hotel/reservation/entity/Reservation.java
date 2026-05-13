package com.hotel.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String reservationId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "reservation_room",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private Set<Room> reservedRooms = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "reservation_service",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<OfferedService> selectedServices = new LinkedHashSet<>();

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private int numberOfNights;

    @Column(nullable = false, length = 32)
    private String reservationStatus = "CONFIRMED";

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;

    public Reservation() {
    }

    public String createReservation() {
        return "Reservation " + reservationId + " created for " + (guest != null ? guest.getFullName() : "?");
    }

    public int calculateStayDuration() {
        return numberOfNights;
    }

    public String displayReservationDetails() {
        String rooms = reservedRooms.stream().map(Room::getRoomNumber).collect(Collectors.joining(", "));
        String svcs = selectedServices.stream().map(OfferedService::getServiceName).collect(Collectors.joining(", "));
        return String.format(
                "ID: %s | Guest: %s | Rooms: [%s] | Services: [%s] | Check-in: %s | Nights: %d | Status: %s",
                reservationId,
                guest != null ? guest.getFullName() : "?",
                rooms,
                svcs,
                checkInDate,
                numberOfNights,
                reservationStatus
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Set<Room> getReservedRooms() {
        return reservedRooms;
    }

    public void setReservedRooms(Set<Room> reservedRooms) {
        this.reservedRooms = reservedRooms;
    }

    public Set<OfferedService> getSelectedServices() {
        return selectedServices;
    }

    public void setSelectedServices(Set<OfferedService> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
