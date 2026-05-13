package com.hotel.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typeName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal roomPrice;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Room> rooms = new ArrayList<>();

    public RoomType() {
    }

    public RoomType(String typeName, BigDecimal roomPrice, String description) {
        this.typeName = typeName;
        this.roomPrice = roomPrice;
        this.description = description;
    }

    public String displayRoomType() {
        return String.format("%s — $%s/night — %s", typeName, roomPrice.toPlainString(),
                description != null ? description : "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(BigDecimal roomPrice) {
        this.roomPrice = roomPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}
