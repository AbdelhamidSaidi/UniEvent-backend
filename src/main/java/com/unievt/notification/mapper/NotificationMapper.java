package com.unievt.notification.mapper;

import com.unievt.notification.dto.NotificationDTO;
import com.unievt.notification.dto.NotificationResponseDTO;
import com.unievt.notification.entity.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "destinataire", ignore = true)
    Notification toEntity(NotificationDTO dto);

    @Mapping(source = "destinataire.id", target = "destinataireId")
    @Mapping(source = "destinataire.nom", target = "destinataireNom")
    @Mapping(source = "destinataire.prenom", target = "destinatairePrenom")
    NotificationResponseDTO toResponseDTO(Notification entity);
}
