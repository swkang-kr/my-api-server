package com.example.api.batch.processor;

import com.example.api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) {
        log.debug("Processing user: {}", user.getUsername());

        // Example: Process user data
        // You can add business logic here

        return user;
    }

}
