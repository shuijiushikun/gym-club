package com.gym.club.mapper;

import com.gym.club.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MemberMapper {
    // 插入会员
    int insert(Member member);

    // 根据ID查询
    Member selectById(Integer id);

    // 根据用户名查询
    Member selectByUsername(String username);

    // 查询所有会员
    List<Member> selectAll();

    // 更新会员信息
    int update(Member member);

    // 根据ID删除
    int deleteById(Integer id);

    // 查询教练的学员（通过预约关联）
    List<Member> selectByCoachId(Integer coachId);
}