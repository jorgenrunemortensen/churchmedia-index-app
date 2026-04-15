package dk.runerne.indexingserver.contenteventprocessing;

import java.util.UUID;

public record ContentEvent (
    UUID id,
    AggregateType aggregateType,
    EventType eventType,
    Object subPayload
) {}