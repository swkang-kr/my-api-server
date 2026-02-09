package com.example.api.mapper;

import com.example.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

    User findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    List<User> findAll();

    List<User> findByStatus(@Param("status") String status);

    void insert(User user);

    void update(User user);

    void delete(@Param("id") Long id);

    int count();

}
