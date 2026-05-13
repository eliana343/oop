package com.hotel.reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    private String guestName;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String comment;

    public Review() {
    }

    public Review(Guest guest, String guestName, Integer rating, String comment) {
        this.guest = guest;
        this.guestName = guestName;
        this.rating = rating;
        this.comment = comment;
    }

    public String submitReview() {
        return "Review submitted by " + (guestName != null ? guestName : "?") + " — " + rating + "/5 stars";
    }

    public String displayReview() {
        return String.format("%s — Rating: %d/5%n%s", guestName, rating, comment != null ? comment : "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
