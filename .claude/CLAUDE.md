# indexing-app

@../../../conventions/CLAUDE.md

Spring Boot application that synchronises data from content-app to Elasticsearch via RabbitMQ events.

## Key components

- **ContentEventListener** — listens on RabbitMQ and receives events from content-app
- **Synchronizer** — coordinates synchronisation based on event type and aggregate type
- **\*SynchronizationService** — one per entity (Artist, Label, Media, MediaGroup, MediaArtistRole)
- **ConfigurationService** — reads configuration and app settings

## Local environment

Requires running infrastructure from `churchmedia-infra` (RabbitMQ, Elasticsearch, Keycloak).

| Service       | URL                        |
|---------------|----------------------------|
| indexing-app  | http://localhost:8086      |
| content-app   | http://localhost:8081      |
| Keycloak      | http://localhost:8080      |
| Elasticsearch | http://localhost:9200      |

## Security

Uses OAuth2 client credentials against Keycloak (realm `Alive`, client `indexing-app`) to call content-app's API.
