package br.edu.atitus.apisample.services;

import br.edu.atitus.apisample.entities.PointEntity;
import br.edu.atitus.apisample.entities.User;
import br.edu.atitus.apisample.repositories.PointRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PointService {

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "Música", "Cultura", "Festa", "Entretenimento", "Congresso",
            "Seminário", "Esporte", "Show", "Feira", "Rodeio", "Inauguração",
            "Exposição", "Workshop", "Automóvel", "Festival", "Outros"
    );

    private static final Set<String> VALID_ACCESSIBILITY = Set.of(
            "Físico", "Visual", "Auditivo", "Todas"
    );

    private static final Set<String> VALID_REGISTRATION_TYPES = Set.of(
            "Pago", "Gratuito", "Sem Inscrição"
    );

    private final PointRepository repository;

    public PointService(PointRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PointEntity save(PointEntity point) throws Exception {
        validateFields(point);
        User user = getUsuarioAutenticado();
        point.setUser(user);
        return repository.save(point);
    }

    @Transactional
    public PointEntity update(UUID id, PointEntity updatedData) throws Exception {
        PointEntity existing = repository.findById(id)
                .orElseThrow(() -> new Exception("Ponto não encontrado!"));

        User user = getUsuarioAutenticado();
        if (!existing.getUser().getId().equals(user.getId()))
            throw new Exception("Este ponto não pertence ao usuário autenticado!");

        validateFields(updatedData);

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setLatitude(updatedData.getLatitude());
        existing.setLongitude(updatedData.getLongitude());
        existing.setEventDate(updatedData.getEventDate());
        existing.setEventTime(updatedData.getEventTime());
        existing.setLocationName(updatedData.getLocationName());
        existing.setWebsite(updatedData.getWebsite());
        existing.getCategories().clear();
        if (updatedData.getCategories() != null)
            existing.getCategories().addAll(updatedData.getCategories());
        existing.getAccessibility().clear();
        if (updatedData.getAccessibility() != null)
            existing.getAccessibility().addAll(updatedData.getAccessibility());
        existing.setRegistrationType(updatedData.getRegistrationType());
        existing.setImageBase64(updatedData.getImageBase64());

        return repository.save(existing);
    }

    @Transactional
    public void deleteById(UUID id) throws Exception {
        if (id == null)
            throw new Exception("Id informado inválido!");

        PointEntity point = repository.findById(id)
                .orElseThrow(() -> new Exception("Ponto não encontrado!"));

        User user = getUsuarioAutenticado();
        if (!point.getUser().getId().equals(user.getId()))
            throw new Exception("Este ponto não pertence ao usuário autenticado!");

        repository.delete(point);
    }

    public List<PointEntity> findAll() {
        return repository.findAll();
    }

    private void validateFields(PointEntity point) throws Exception {
        if (point == null)
            throw new Exception("Objeto nulo!");

        if (point.getName() == null || point.getName().isBlank())
            throw new Exception("Nome do evento informado inválido!");
        point.setName(point.getName().trim());

        if (point.getCategories() == null || point.getCategories().isEmpty())
            throw new Exception("Informe ao menos uma categoria!");

        for (String cat : point.getCategories()) {
            if (!VALID_CATEGORIES.contains(cat))
                throw new Exception("Categoria inválida: " + cat +
                        ". Valores aceitos: Música, Cultura, Festa, Entretenimento, Congresso, Seminário, Esporte, Show, Feira, Rodeio, Inauguração, Exposição, Workshop, Automóvel, Festival, Outros");
        }

        if (point.getAccessibility() != null) {
            for (String acc : point.getAccessibility()) {
                if (!VALID_ACCESSIBILITY.contains(acc))
                    throw new Exception("Acessibilidade inválida: " + acc +
                            ". Valores aceitos: Físico, Visual, Auditivo, Todas");
            }
        }

        if (point.getRegistrationType() != null && !point.getRegistrationType().isBlank()) {
            if (!VALID_REGISTRATION_TYPES.contains(point.getRegistrationType()))
                throw new Exception("Tipo de inscrição inválido: " + point.getRegistrationType() +
                        ". Valores aceitos: Pago, Gratuito, Sem Inscrição");
        }

        if (point.getLatitude() == null)
            throw new Exception("Latitude informada inválida!");

        if (point.getLongitude() == null)
            throw new Exception("Longitude informada inválida!");

        BigDecimal latMin = new BigDecimal("-90");
        BigDecimal latMax = new BigDecimal("90");
        if (point.getLatitude().compareTo(latMin) < 0 || point.getLatitude().compareTo(latMax) > 0)
            throw new Exception("Latitude deve estar entre -90 e 90!");

        BigDecimal lngMin = new BigDecimal("-180");
        BigDecimal lngMax = new BigDecimal("180");
        if (point.getLongitude().compareTo(lngMin) < 0 || point.getLongitude().compareTo(lngMax) > 0)
            throw new Exception("Longitude deve estar entre -180 e 180!");
    }

    private User getUsuarioAutenticado() throws Exception {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User))
            throw new Exception("Usuário autenticado inválido!");

        return (User) principal;
    }
}
