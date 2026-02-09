package com.example.api.batch.reader;

import com.example.api.entity.User;
import com.example.api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserItemReader implements ItemReader<User> {

    private final UserMapper userMapper;
    private Iterator<User> userIterator;
    private boolean initialized = false;

    @Override
    public User read() {
        if (!initialized) {
            List<User> users = userMapper.findAll();
            userIterator = users.iterator();
            initialized = true;
            log.info("UserItemReader initialized with {} users", users.size());
        }

        if (userIterator != null && userIterator.hasNext()) {
            return userIterator.next();
        }

        return null; // End of data
    }

}
