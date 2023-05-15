package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EntityIsNotFoundException;
import com.example.vse_back.exceptions.NotEnoughCoinsException;
import com.example.vse_back.infrastructure.event.EventCreationRequest;
import com.example.vse_back.model.entity.EventEntity;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.enums.EventStatusEnum;
import com.example.vse_back.model.enums.EventTypeEnum;
import com.example.vse_back.model.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.vse_back.model.service.utils.LocalUtil.getCurrentMoscowDate;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final ImageService imageService;

    public EventService(EventRepository eventRepository, UserService userService, ImageService imageService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    @Transactional
    public void createEvent(EventCreationRequest eventCreationRequest, UserEntity organizer) {
        EventEntity event = new EventEntity();
        EventTypeEnum type = eventCreationRequest.getType();
        event.setType(type.toString());
        event.setTitle(eventCreationRequest.getTitle());
        event.setDescription(eventCreationRequest.getDescription());
        LocalDateTime localDateTime = eventCreationRequest.getEventDate();
        // Do I need this check?
        // If I do, then what about date discrepancies between different time zones?
        if (localDateTime.isBefore(getCurrentMoscowDate())) {
            throw new RuntimeException(); // Create custom Exception
        }
        event.setEventDate(eventCreationRequest.getEventDate());
        ImageEntity image = imageService.createAndGetImage(eventCreationRequest.getFile());
        event.setImage(image);
        List<UserEntity> participants =
                eventCreationRequest
                        .getParticipantIds()
                        .stream()
                        .map(userService::getUserById)
                        .toList();
        event.setParticipants(participants);
        Integer participantsNumber = participants.size();
        event.setParticipantsNumber(participantsNumber);
        Integer total = participantsNumber * type.pricePerParticipant;
        Integer userBalance = organizer.getUserBalance();
        if (total > userBalance) {
            throw new NotEnoughCoinsException(userBalance);
        }
        event.setPrice(total);
        event.setOrganizer(organizer);
        event.setCreationDate(getCurrentMoscowDate());
        event.setStatus(EventStatusEnum.CREATED.toString());
        userService.changeUserBalance(organizer, organizer, userBalance - total, "Создание мероприятия");
        eventRepository.save(event);
    }

    /*public boolean changeOrderStatus(UUID id, String status) {
        if (orderRepository.existsById(id)) {
            OrderEntity order = orderRepository.getReferenceById(id);
            order.setStatus(OrderStatusEnum.valueOf(status).toString());
            if (OrderStatusEnum.CREATED.toString().equals(status)) {
                order.setCreationDate(getCurrentMoscowDate());
            } else if (OrderStatusEnum.PROCESSING.toString().equals(status)) {
                order.setProcessingDate(getCurrentMoscowDate());
            } else if (OrderStatusEnum.COMPLETED.toString().equals(status)) {
                order.setCompletionDate(getCurrentMoscowDate());
            }
            orderRepository.save(order);
            return true;
        } else {
            return false;
        }
    }

    public void editPost(PostEditRequest postEditRequest) {
        PostEntity post = getPostById(UUID.fromString(postEditRequest.getPostId()));
        post.setTitle(postEditRequest.getTitle());
        post.setText(postEditRequest.getText());
        post.setImage(setupImage(post, postEditRequest.getFile()));
        postRepository.save(post);
    }

    // Create interface with this method and make it implemented by PostService and UserService?
    private ImageEntity setupImage(PostEntity post, MultipartFile file) {
        if (file == null && post.getImage() != null) {
            imageService.deleteImage(post.getImage().getId());
        } else if (file != null) {
            if (post.getImage() != null) {
                imageService.deleteImage(post.getImage().getId());
            }
            return imageService.createAndGetImage(file);
        }
        return null;
    }*/

    public boolean deleteEventById(UUID id, UserEntity subjectUser) {
        if (eventRepository.existsById(id)) {
            EventEntity event = eventRepository.getReferenceById(id);
            UserEntity organizer = event.getOrganizer();
            Integer newBalance = organizer.getUserBalance() + event.getPrice();
            userService.changeUserBalance(organizer, subjectUser, newBalance, "Отмена мероприятия");
            ImageEntity image = getEventById(id).getImage();
            eventRepository.deleteById(id);
            if (image != null) {
                imageService.deleteImage(image.getId());
            }
            return true;
        }
        return false;
    }

    public EventEntity getEventById(UUID id) {
        EventEntity event = eventRepository.findByEventId(id);
        if (event == null) {
            throw new EntityIsNotFoundException("event", id);
        }
        return event;
    }

    /*public List<EventEntity> getEventsByUserId(UserEntity user) {

    }*/

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }
}
