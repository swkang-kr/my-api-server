package com.example.api.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailMessage implements Serializable {

    private String recipient;
    private String subject;
    private String content;
    private boolean isHtml;

}
