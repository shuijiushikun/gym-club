package com.gym.club.service;

import com.gym.club.entity.Equipment;
import com.gym.club.mapper.EquipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    public Equipment addEquipment(Equipment equipment) {
        equipmentMapper.insert(equipment);
        return equipmentMapper.selectById(equipment.getId());
    }

    public Equipment getById(Integer id) {
        return equipmentMapper.selectById(id);
    }

    public List<Equipment> getAll() {
        return equipmentMapper.selectAll();
    }

    public List<Equipment> getByType(String type) {
        return equipmentMapper.selectByType(type);
    }

    public List<Equipment> getByStatus(Integer status) {
        return equipmentMapper.selectByStatus(status);
    }

    public Equipment updateEquipment(Equipment equipment) {
        equipmentMapper.update(equipment);
        return equipmentMapper.selectById(equipment.getId());
    }

    public boolean deleteById(Integer id) {
        return equipmentMapper.deleteById(id) > 0;
    }

    public boolean recordUsage(Integer id, Integer hours) {
        return equipmentMapper.updateUsageHours(id, hours) > 0;
    }

    public boolean updateMaintenance(Integer id, java.time.LocalDate lastDate, java.time.LocalDate nextDate) {
        return equipmentMapper.updateMaintenanceDate(id, lastDate, nextDate) > 0;
    }
}