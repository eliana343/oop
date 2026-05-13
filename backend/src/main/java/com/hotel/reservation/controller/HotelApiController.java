package com.hotel.reservation.controller;

import com.hotel.reservation.service.HotelAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Single controller — fewer files for a school project.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HotelApiController {

    private final HotelAppService app;

    public HotelApiController(HotelAppService app) {
        this.app = app;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> bad(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    /** One call: hotel + rooms + services for the web page */
    @GetMapping("/data")
    public Map<String, Object> data() {
        return app.loadPageData();
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody HotelAppService.RegisterReq body) {
        return app.register(body);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody HotelAppService.LoginReq body) {
        return app.login(body);
    }

    @PostMapping("/book")
    public Map<String, Object> book(@RequestBody HotelAppService.BookReq body) {
        return app.book(body);
    }

    @GetMapping("/reservation/{id}")
    public Map<String, Object> reservation(@PathVariable String id) {
        return app.getReservation(id);
    }

    @PostMapping("/reservation/{id}/invoice")
    public Map<String, Object> invoice(@PathVariable String id) {
        return app.makeInvoice(id);
    }

    @PostMapping("/invoice/{invoiceId}/pay")
    public Map<String, Object> pay(@PathVariable Long invoiceId, @RequestBody HotelAppService.PayReq body) {
        return app.pay(invoiceId, body);
    }

    @PostMapping("/reservation/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null && body.get("reason") != null ? body.get("reason") : "";
        return app.cancel(id, reason);
    }

    @PostMapping("/review")
    public Map<String, Object> review(@RequestBody HotelAppService.ReviewReq body) {
        return app.review(body);
    }
}
