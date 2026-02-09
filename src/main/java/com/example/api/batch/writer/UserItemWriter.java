package com.example.api.batch.writer;

import com.example.api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserItemWriter implements ItemWriter<User> {

    @Override
    public void write(Chunk<? extends User> chunk) {
        log.info("Writing {} users", chunk.size());

        chunk.forEach(user -> {
            // Example: Write user data to database or file
            log.debug("Writing user: {}", user.getUsername());
        });
    }

}
