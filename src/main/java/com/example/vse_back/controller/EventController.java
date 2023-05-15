package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.event.EventCreationRequest;
import com.example.vse_back.model.entity.EventEntity;
import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.EventService;
import com.example.vse_back.model.service.utils.LocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class EventController {
    private final EventService eventService;
    private final LocalUtil localUtil;

    public EventController(EventService eventService, LocalUtil localUtil) {
        this.eventService = eventService;
        this.localUtil = localUtil;
    }

    @Operation(summary = "Create the event")
    @PostMapping("/user/event/create")
    public ResponseEntity<Object> createEvent(@RequestHeader(name = "Authorization") String token,
                                              @ModelAttribute @Valid EventCreationRequest eventCreationRequest) {
        UserEntity user = localUtil.getUserFromToken(token);
        eventService.createEvent(eventCreationRequest, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*@Operation(summary = "Edit the event")
    @PostMapping("/user/event/edit")
    public ResponseEntity<Object> editEvent(@RequestHeader(name = "Authorization") String token,
                                            @ModelAttribute @Valid EventEditRequest eventEditRequest) {
        UserEntity user = localUtil.getUserFromToken(token);
        eventService.editEvent(eventEditRequest, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @Operation(summary = "Delete the event")
    @DeleteMapping("/user/event/{eventId}")
    public ResponseEntity<Object> deleteMyEvent(@RequestHeader(name = "Authorization") String token,
                                                @PathVariable(name = "eventId") UUID eventId) {
        UserEntity user = localUtil.getUserFromToken(token);
        final boolean isDeleted = eventService.deleteEventById(eventId, user);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @Operation(summary = "Delete user's event")
    @DeleteMapping("/admin/event/{eventId}")
    public ResponseEntity<Object> deleteEvent(@RequestHeader(name = "Authorization") String token,
                                              @PathVariable(name = "eventId") UUID eventId) {
        UserEntity user = localUtil.getUserFromToken(token);
        final boolean isDeleted = eventService.deleteEventById(eventId, user);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    /*@Operation(summary = "Get my events")
    @GetMapping("/user/events")
    public ResponseEntity<List<EventEntity>> getMyEvents(@RequestHeader(name = "Authorization") String token) {
        UserEntity user = localUtil.getUserFromToken(token);
        final List<EventEntity> events = eventService.getEventsByUserId(user);
        return events != null && !events.isEmpty()
                ? new ResponseEntity<>(events, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }*/

    @Operation(summary = "Get all events")
    @GetMapping("/events")
    public ResponseEntity<List<EventEntity>> getAllEvents() {
        final List<EventEntity> events = eventService.getAllEvents();
        return events != null && !events.isEmpty()
                ? new ResponseEntity<>(events, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    /*@Operation(summary = "Change the event's status")
    @GetMapping("/admin/event/{eventId}")
    public ResponseEntity<Object> changeEventStatus(@ModelAttribute @Valid EventStatusChangeRequest request) {
        final boolean isChanged = eventService.changeEventStatus(request);
        return isChanged
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }*/
}
