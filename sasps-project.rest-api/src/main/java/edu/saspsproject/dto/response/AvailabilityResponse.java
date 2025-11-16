package edu.saspsproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilityResponse {
    private Long institutionId;
    private List<LocalDateTime> availableSlots;
    
    // Default constructor for Jackson
    public AvailabilityResponse() {}
}