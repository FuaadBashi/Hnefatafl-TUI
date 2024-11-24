package ws.aperture.hnefatafl.model;

import java.util.List;

public record DestResultDTO(String destLocation, List<String> attackeeLocations) {}