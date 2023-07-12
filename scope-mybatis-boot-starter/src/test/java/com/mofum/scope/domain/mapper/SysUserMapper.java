package com.mofum.scope.domain.mapper;

import com.mofum.scope.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    List<SysUser> selectList();

    List<SysUser> selectList2(@Param("id") String id);
}
