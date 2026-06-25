package br.edu.atitus.apisample.controllers;

import br.edu.atitus.apisample.entities.PointEntity;
import br.edu.atitus.apisample.services.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ws/point")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    public record PointDTO(
            String name,
            String description,
            double latitude,
            double longitude,
            String eventDate,
            String eventTime,
            String locationName,
            String website,
            List<String> categories,
            List<String> accessibility,
            String registrationType,
            String imageBase64
    ) {}

    @GetMapping
    public ResponseEntity<List<PointEntity>> getPoints() {
        return ResponseEntity.ok(pointService.findAll());
    }

    @PostMapping
    public ResponseEntity<PointEntity> postPoint(@RequestBody PointDTO dto) throws Exception {
        PointEntity point = buildFromDTO(dto);
        return ResponseEntity.status(201).body(pointService.save(point));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointEntity> putPoint(@PathVariable UUID id, @RequestBody PointDTO dto) throws Exception {
        PointEntity point = buildFromDTO(dto);
        return ResponseEntity.ok(pointService.update(id, point));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePoint(@PathVariable UUID id) throws Exception {
        pointService.deleteById(id);
        return ResponseEntity.ok("Ponto removido com sucesso!");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        String message = ex.getMessage()
                .replace("\r", "")
                .replace("\n", "");
        return ResponseEntity.badRequest().body(message);
    }

    private PointEntity buildFromDTO(PointDTO dto) {
        PointEntity point = new PointEntity();
        point.setName(dto.name());
        point.setDescription(dto.description());
        point.setLatitude(BigDecimal.valueOf(dto.latitude()));
        point.setLongitude(BigDecimal.valueOf(dto.longitude()));
        point.setEventDate(dto.eventDate());
        point.setEventTime(dto.eventTime());
        point.setLocationName(dto.locationName());
        point.setWebsite(dto.website());
        if (dto.categories() != null) point.setCategories(new HashSet<>(dto.categories()));
        if (dto.accessibility() != null) point.setAccessibility(new HashSet<>(dto.accessibility()));
        point.setRegistrationType(dto.registrationType());
        point.setImageBase64(dto.imageBase64());
        return point;
    }
}
