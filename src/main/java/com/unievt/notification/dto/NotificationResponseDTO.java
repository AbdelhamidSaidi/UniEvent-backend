package com.unievt.notification.dto;

import com.unievt.enums.TypeNotifEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private Long id;
    private String titre;
    private String message;
    private TypeNotifEnum type;
    private Boolean lu;
    private LocalDateTime dateEnvoi;
    private Long destinataireId;
    private String destinataireNom;
    private String destinatairePrenom;
}
