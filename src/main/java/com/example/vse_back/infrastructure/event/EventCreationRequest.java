package com.example.vse_back.infrastructure.event;

import com.example.vse_back.model.enums.EventTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class EventCreationRequest {
    @NotNull
    private EventTypeEnum type;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime eventDate;

    private MultipartFile file;

    //@Size(min = 3)
    @NotNull
    @NotEmpty
    private List<UUID> participantIds;
}
