package com.hotel.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Represents a bookable hotel extra from the design's "Service" class (Java {@code Service} is reserved for Spring beans).
 */
@Entity
@Table(name = "offered_service")
public class OfferedService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String serviceCode;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal servicePrice;

    public OfferedService() {
    }

    public OfferedService(String serviceCode, String serviceName, BigDecimal servicePrice) {
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

    public String displayServices() {
        return String.format("[%s] %s — $%s", serviceCode, serviceName, servicePrice.toPlainString());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }
}
