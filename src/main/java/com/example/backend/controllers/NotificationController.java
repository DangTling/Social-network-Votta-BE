package com.example.backend.controllers;

import com.example.backend.dto.request.NotificationRequest;
import com.example.backend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("")
    public ResponseEntity create(@RequestBody NotificationRequest request) {
        return notificationService.save(request);
    }

    @GetMapping("")
    public ResponseEntity get(@RequestParam String id) {
        return notificationService.get(id);
    }

    @DeleteMapping("")
    public ResponseEntity delete(@RequestParam String id) {
        return notificationService.delete(id);
    }

    @GetMapping("/get-all")
    public ResponseEntity getAll(@RequestParam String ownerId, @RequestParam int page, @RequestParam int size) {
        return notificationService.getAll(ownerId, page, size);
    }
}
