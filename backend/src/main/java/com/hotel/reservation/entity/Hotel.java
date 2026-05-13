package com.hotel.reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hotelName;

    private String location;
    private String phoneNumber;
    private String email;

    @Column(length = 2000)
    private String description;

    public Hotel() {
    }

    public Hotel(String hotelName, String location, String phoneNumber, String email, String description) {
        this.hotelName = hotelName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.description = description;
    }

    public String displayHotelInfo() {
        return String.format(
                "Hotel: %s | Location: %s | Phone: %s | Email: %s%n%s",
                hotelName, location, phoneNumber, email, description != null ? description : "");
    }

    public String showWelcomeMessage() {
        return "Welcome to " + hotelName + " — we are glad you chose us.";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
