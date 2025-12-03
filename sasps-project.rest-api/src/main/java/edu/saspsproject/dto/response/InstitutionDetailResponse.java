package edu.saspsproject.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record InstitutionDetailResponse(Long id, String name, LocalTime openingTime, LocalTime closingTime, int maxAppointmentsPerDay, List<PublicServiceDetailResponse> availableServices, List<LocalDateTime> availability) {
}
