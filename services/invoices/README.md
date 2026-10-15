# invoices

Registers and queries invoices owed by debtors. First service of the platform (phase 1).

## API
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/invoices` | Register an invoice. Returns `201` with `Location`. |
| `GET` | `/invoices/{id}` | Get an invoice. `404` if it does not exist. |
| `GET` | `/actuator/health` | Health check |

Errors use RFC 9457 Problem Details (`application/problem+json`).

## Running
```bash
docker compose -f ../../platform/docker-compose.yml up -d
./mvnw spring-boot:run
```
Requests to try: [http/invoices.http](http/invoices.http).

## Tests
```bash
./mvnw verify
```
- `InvoiceTest` — domain rules, no Spring, milliseconds.
- `InvoiceControllerTest` — full application against a real PostgreSQL in Testcontainers.

## Docker image
```bash
docker build -t invoices .
docker run --rm -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/collections invoices
```

## Next steps
See the phase 1 items in [docs/backlog.md](../../docs/backlog.md).
