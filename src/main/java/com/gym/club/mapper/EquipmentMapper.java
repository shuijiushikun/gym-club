package com.gym.club.mapper;

import com.gym.club.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EquipmentMapper {
    int insert(Equipment equipment);

    Equipment selectById(Integer id);

    Equipment selectBySerialNumber(String serialNumber);

    List<Equipment> selectAll();

    List<Equipment> selectByType(String type);

    List<Equipment> selectByStatus(Integer status);

    int update(Equipment equipment);

    int deleteById(Integer id);

    int updateUsageHours(@Param("id") Integer id, @Param("hours") Integer hours);

    int updateMaintenanceDate(@Param("id") Integer id,
            @Param("lastDate") java.time.LocalDate lastDate,
            @Param("nextDate") java.time.LocalDate nextDate);
}