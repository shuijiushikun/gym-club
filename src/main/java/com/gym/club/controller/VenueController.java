package com.gym.club.controller;

import com.gym.club.entity.Venue;
import com.gym.club.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venue")
public class VenueController {
    @Autowired
    private VenueService venueService;

    @PostMapping("/add")
    public Venue addVenue(@RequestBody Venue venue) {
        return venueService.addVenue(venue);
    }

    @GetMapping("/{id}")
    public Venue getById(@PathVariable Integer id) {
        return venueService.getById(id);
    }

    @GetMapping("/all")
    public List<Venue> getAll() {
        return venueService.getAll();
    }

    @GetMapping("/type/{type}")
    public List<Venue> getByType(@PathVariable Integer type) {
        return venueService.getByType(type);
    }

    @GetMapping("/status/{status}")
    public List<Venue> getByStatus(@PathVariable Integer status) {
        return venueService.getByStatus(status);
    }

    @PutMapping("/update")
    public Venue updateVenue(@RequestBody Venue venue) {
        return venueService.updateVenue(venue);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        return venueService.deleteById(id);
    }
}