# AGENTS.md - E-Commerce Application Developer Guide

## Project Overview
Spring Boot 4.0.6 REST API for e-commerce platform with user management and product catalog. Uses Java 21, Lombok, JPA/Hibernate, and H2 in-memory database.

**Key Tech Stack:**
- Spring Boot 4.0.6, Java 21, Maven
- Spring Data JPA with Hibernate ORM
- H2 in-memory database (development) with auto-DDL creation
- Lombok for boilerplate elimination
- RESTful API with DTO pattern

---

## Architecture Patterns

### Layered Three-Tier Architecture
- **Controllers** (`src/main/java/com/app/ecom/controller/`): REST endpoints, request routing
- **Service** (`src/main/java/com/app/ecom/service/`): Business logic, DTO↔Entity mapping, transactions
- **Repository** (`src/main/java/com/app/ecom/repository/`): Spring Data JPA interfaces, database queries
- **Models** (`src/main/java/com/app/ecom/model/`): JPA entities with custom table names
- **DTOs** (`src/main/java/com/app/ecom/dto/`): Request/Response objects

**Data Flow Example:** `POST /api/users` → `UserController.createUser()` → `UserService.addUser()` → `UserRepository.save()` → `User` entity

### Manual DTO Mapping Convention
The `UserService` demonstrates the project's mapping pattern (no MapStruct/ModelMapper used):
- Private methods: `mapToUserResponse()`, `updateUserFromRequest()`
- Stream-based collection mapping: `userRepository.findAll().stream().map(...).collect()`
- Null-safe nested mapping: Check `user.getAddress() != null` before mapping

**Why:** Educational codebase emphasizing manual mapping transparency. When adding services (e.g., `ProductService`), follow this pattern with explicit private mapping methods.

### Lombok Usage Conventions
- `@Data`: All models (User, Product, Address) - generates getters, setters, equals(), hashCode(), toString()
- `@NoArgsConstructor`: Required for JPA entity instantiation
- `@RequiredArgsConstructor`: Constructor injection in service (see `UserService` line 17) - prefer over `@Autowired`

---

## Data Model & Relationships

### User-Address Relationship (One-to-One)
```
User.java (line 24-26):
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "address_id", referencedColumnName = "id")
private Address address;
```
- **Cascade**: Address is automatically saved/updated with User
- **Orphan Removal**: Deleting User's address reference deletes the orphaned Address record
- **Table Names**: `user_table` (not `user`), `addresses` (not `address`)
- **Audit Fields**: Both entities have `@CreationTimestamp` and `@UpdateTimestamp` (not in Address yet)

### Entities & Entity Names
- `User`: JPA table name = `user_table` (line 13)
- `Product`: JPA table name = `products` (line 15)
- `Address`: JPA table name = `addresses` (line 12)

These custom names are intentional. When adding entities, use explicit `@Entity(name = "...")`.

---

## Common Development Workflows

### Build & Run
```bash
# Build with Maven
./mvnw clean install

# Run locally (starts on default http://localhost:8080)
./mvnw spring-boot:run

# H2 Console accessible at http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:test (from application.yml line 11)
```

### Database Management
- **Auto-DDL**: `ddl-auto: create` (application.yml line 15) recreates schema on startup
- **SQL Logging**: `show-sql: true` (line 13) prints executed queries to console for debugging
- Changes to entity fields require restart (schema recreation)

### Adding a New Endpoint
1. Create entity in `model/` with `@Entity`, `@Data`, `@NoArgsConstructor`
2. Create repository in `repository/` extending `JpaRepository<Entity, Long>`
3. Create request/response DTOs in `dto/` with `@Data`
4. Create service in `service/` with business logic and private mapping methods
5. Create controller in `controller/` extending mapping and calling service
6. Add manual `mapToDTO()` and `mapFromDTO()` methods in service (convention, not framework)

### Testing Entry Point
`src/test/java/com/app/ecom/EcomApplicationTests.java` - verify Spring context loads with `@SpringBootTest`

---

## API Conventions

### REST Endpoints Pattern (UserController example)
- `GET /api/users` → List all, returns `List<UserResponse>`
- `GET /api/users/{id}` → Fetch single, uses Optional pattern (line 26-28)
- `POST /api/users` → Create, accepts `UserRequest`, returns success message
- `PUT /api/users/{id}` → Update, accepts `UserRequest`, returns success or 404

**Response Wrapping:** Simple `ResponseEntity<String>` for operations, `ResponseEntity<Object>` for queries. No global wrapper object (e.g., `{ success: true, data: ... }`).

### Optional Handling Pattern
Controllers use `Optional.map().orElseGet()` (line 26-28 in UserController):
```java
return userService.fetchUser(id)
    .map(ResponseEntity::ok)
    .orElseGet(() -> ResponseEntity.notFound().build());
```
When adding new controllers, adopt this pattern for consistency.

---

## Key Files Reference

| File | Purpose | Key Details |
|------|---------|-------------|
| `pom.xml` | Maven build config | Spring Boot parent 4.0.6, Lombok annotation processing (lines 93-98) |
| `application.yml` | App config | H2 config, auto-DDL, SQL logging enabled |
| `model/User.java` | User entity | OneToOne Address, audit timestamps, default role=CUSTOMER |
| `model/Product.java` | Product entity | Audit timestamps, soft-delete via `isActive` Boolean |
| `service/UserService.java` | Business logic | Template for mapping pattern and service structure |
| `controller/UserController.java` | REST endpoints | Template for controller structure and Optional handling |
| `dto/UserRequest.java` | Request DTO | Nested AddressDTO (line 11) |

---

## Patterns to Maintain

✅ **DO:**
- Use Lombok `@Data`, `@NoArgsConstructor` in all entities
- Use constructor injection with `@RequiredArgsConstructor` in services
- Write private mapping methods in services (not auto-mapping frameworks)
- Explicitly set JPA table names with `@Entity(name = "...")`
- Use Optional for nullable repository results
- Test entity creation with `EcomApplicationTests` context loader

❌ **DON'T:**
- Use `@Autowired` field injection (use constructor injection)
- Skip null checks for nested object mapping (see `mapToUserResponse` line 54)
- Change entity table names without updating repository queries
- Use validation annotations without corresponding service layer validation

---

## Expansion Opportunities
- No validation framework present (add `jakarta.validation.constraints` or Spring Validation)
- No error/exception handling (no `@ExceptionHandler`, `@ControllerAdvice`)
- No authentication/security (consider Spring Security later)
- ProductController is empty - mirror UserController pattern to implement
- No pagination support - add `Pageable` parameter to `findAll()` methods

