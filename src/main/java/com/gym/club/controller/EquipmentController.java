package com.gym.club.controller;

import com.gym.club.entity.Equipment;
import com.gym.club.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    @PostMapping("/add")
    public Equipment addEquipment(@RequestBody Equipment equipment) {
        return equipmentService.addEquipment(equipment);
    }

    @GetMapping("/{id}")
    public Equipment getById(@PathVariable Integer id) {
        return equipmentService.getById(id);
    }

    @GetMapping("/all")
    public List<Equipment> getAll() {
        return equipmentService.getAll();
    }

    @GetMapping("/type/{type}")
    public List<Equipment> getByType(@PathVariable String type) {
        return equipmentService.getByType(type);
    }

    @GetMapping("/status/{status}")
    public List<Equipment> getByStatus(@PathVariable Integer status) {
        return equipmentService.getByStatus(status);
    }

    @PutMapping("/update")
    public Equipment updateEquipment(@RequestBody Equipment equipment) {
        return equipmentService.updateEquipment(equipment);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        return equipmentService.deleteById(id);
    }

    @PostMapping("/record-usage/{id}")
    public boolean recordUsage(@PathVariable Integer id, @RequestParam Integer hours) {
        return equipmentService.recordUsage(id, hours);
    }
}