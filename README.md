# Servicio de Autenticación Crediya

Servicio de registro de usuarios implementado con Clean Architecture y Spring WebFlux.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 21
- Docker & Docker Compose
- Gradle 8.14.3+

### Ejecutar Localmente
```bash
# Iniciar base de datos
docker-compose up -d

# Construir y ejecutar
./gradlew build
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`

## 📋 API

### Registrar Usuario
```http
POST /api/v1/usuarios
Content-Type: application/json

{
  "name": "Juan Pérez",
  "lastName": "García",
  "email": "juan.perez@email.com",
  "birthDate": "1990-01-15",
  "address": "Calle 123 #45-67",
  "idDocument": "12345678",
  "baseSalary": 2500000.00,
  "phoneNumber": "+57 300 123 4567"
}
```

## 🏗️ Arquitectura

Implementa Clean Architecture/Hexagonal con capas claramente separadas:

### Domain
- **Model**: Entidades de negocio (`User`, `RoleId`)
- **UseCase**: Lógica de aplicación (`UserUseCase`)

### Infrastructure
- **Driven Adapters**: Persistencia con R2DBC PostgreSQL
- **Entry Points**: API REST con Spring WebFlux

### Application
- Configuración y ensamblaje de dependencias

## 🛠️ Tecnologías

- **Java 21** + **Spring Boot 3.5.4**
- **Spring WebFlux** (reactivo)
- **PostgreSQL** + **R2DBC**
- **Gradle** (build tool)
- **JUnit 5**, **Mockito**, **JaCoCo** (testing)

## 🔧 Comandos Útiles

```bash
# Build completo
./gradlew build

# Ejecutar tests
./gradlew test

# Cobertura de código
./gradlew jacocoTestReport

# Análisis SonarQube
./gradlew sonarqube
```

## 📖 Referencias

- [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
- [Repository Pattern](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)
