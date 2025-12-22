package com.gym.club.service;

import com.gym.club.entity.Venue;
import com.gym.club.mapper.VenueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VenueService {
    @Autowired
    private VenueMapper venueMapper;

    public Venue addVenue(Venue venue) {
        venueMapper.insert(venue);
        return venueMapper.selectById(venue.getId());
    }

    public Venue getById(Integer id) {
        return venueMapper.selectById(id);
    }

    public List<Venue> getAll() {
        return venueMapper.selectAll();
    }

    public List<Venue> getByType(Integer type) {
        return venueMapper.selectByType(type);
    }

    public List<Venue> getByStatus(Integer status) {
        return venueMapper.selectByStatus(status);
    }

    public Venue updateVenue(Venue venue) {
        venueMapper.update(venue);
        return venueMapper.selectById(venue.getId());
    }

    public boolean deleteById(Integer id) {
        return venueMapper.deleteById(id) > 0;
    }
}