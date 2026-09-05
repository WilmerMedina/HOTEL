# Hotel Reservation API

API REST para la gestión de reservas de un hotel, construida con **Spring Boot** y **Spring Security**. Permite el registro/login de usuarios mediante **JWT**, la administración de habitaciones y la creación/consulta de reservas, con control de acceso basado en roles (`ADMIN` / `CLIENT`).

Proyecto de portafolio orientado a demostrar buenas prácticas de arquitectura en capas, seguridad y validación en una API backend con Spring Boot.

---

## Características

- **Autenticación con JWT** (registro y login) usando `io.jsonwebtoken`, con validación fail-fast del secret al arrancar la app.
- **Autorización basada en roles** (`ADMIN`, `CLIENT`) con `@PreAuthorize` a nivel de endpoint.
- **Gestión de habitaciones**: alta, edición, baja y listado paginado.
- **Gestión de reservas**: creación con validación de fechas y detección de solapamientos (no se puede reservar una habitación ya ocupada en esas fechas), cálculo automático del precio total según noches.
- **Gestión de usuarios**: listado, consulta, edición y baja (solo `ADMIN`), expuesta mediante un `UserResponse` que nunca incluye la contraseña.
- **Cuentas activables/bloqueables**: el login rechaza cuentas inactivas o bloqueadas antes de validar la contraseña.
- **Usuario administrador por defecto**: un `DataInitializer` crea automáticamente una cuenta `ADMIN` al arrancar la aplicación, si todavía no existe.
- **Validación de datos** con Bean Validation (`jakarta.validation`) y mensajes de error personalizados.
- **Manejo de errores consistente**: respuestas JSON uniformes para `401 Unauthorized` y `403 Forbidden`, y excepciones de negocio propias (`ResourceNotFoundException`, `ConflictException`, `ReservationConflictException`, `AuthenticationException`).
- **CORS configurado** de forma explícita para restringir los orígenes permitidos.
- **Contraseñas cifradas** con BCrypt (strength 12).
- **Paginación** en los endpoints de listado (`Pageable`).

---

## Stack tecnológico

| Categoría | Tecnología |
|-------------------|----------------------------------------------|
| Lenguaje | Java |
| Framework | Spring Boot |
| Seguridad | Spring Security + JWT (`jjwt`) |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL |
| Validación | Jakarta Bean Validation |
| Testing | JUnit 5 / Spring Boot Test |
| Build | Maven / Gradle |

---

## Arquitectura del proyecto

El proyecto sigue una arquitectura en capas clásica:

```
com.example.hotel
├── config # Configuración de arranque (p.ej. DataInitializer)
├── controller # Endpoints REST
├── dto
│ ├── request # DTOs de entrada
│ └── response # DTOs de salida (p.ej. UserResponse)
├── entity # Entidades JPA (User, Room, Reservation)
├── enums # Role, RoomType, RoomStatus, ReservationStatus
├── mapper # Conversión entre entidades y DTOs
├── repository # Interfaces JpaRepository
├── security # JWT, filtro de autenticación, configuración de seguridad
├── service # Interfaces de servicio
└── service.impl # Implementación de la lógica de negocio
```

---

## Modelo de roles

| Rol | Permisos |
|----------|---------------------------------------------------------------------------|
| `CLIENT` | Crear reservas, ver sus propias reservas, ver habitaciones disponibles. |
| `ADMIN` | Todo lo anterior + gestionar habitaciones, ver/gestionar usuarios y ver todas las reservas. |

Todo usuario nuevo se registra por defecto con el rol `CLIENT`.

---

## Usuario administrador por defecto

Al arrancar la aplicación, `DataInitializer` (`com.example.hotel.config`) verifica si ya existe un usuario con el correo `admin@hotel.com`. Si no existe, crea automáticamente una cuenta con rol `ADMIN`, activa y sin bloquear:

| Campo | Valor |
|----------|------------------|
| Email | `admin@hotel.com` |
| Password | `admin123` |
| Rol | `ADMIN` |
| Activo | `true` |
| Bloqueado | `false` |

> **Advertencia:** esta cuenta es solo para desarrollo/pruebas. En un entorno productivo, cambia la contraseña inmediatamente después del primer despliegue o reemplaza este mecanismo por uno que use un secreto gestionado externamente (variable de entorno, vault, etc.).

---

## Endpoints principales

### Autenticación (públicos)
| Método | Endpoint | Descripción |
|--------|------------------|-------------------------------|
| POST | `/auth/register` | Registra un nuevo usuario |
| POST | `/auth/login` | Inicia sesión y devuelve un JWT |

### Habitaciones
| Método | Endpoint | Rol requerido | Descripción |
|--------|----------------|---------------------|---------------------------|
| GET | `/rooms` | CLIENT, ADMIN | Lista habitaciones (paginado) |
| POST | `/rooms` | ADMIN | Crea una habitación |
| PUT | `/rooms/{id}` | ADMIN | Actualiza una habitación |
| DELETE | `/rooms/{id}` | ADMIN | Elimina una habitación |

### Reservas
| Método | Endpoint | Rol requerido | Descripción |
|--------|--------------------------|----------------|---------------------------------------|
| POST | `/reservations/create` | CLIENT, ADMIN | Crea una reserva |
| GET | `/reservations/list` | CLIENT, ADMIN | Lista las reservas del usuario autenticado |
| GET | `/reservations/list/all` | ADMIN | Lista todas las reservas |

### Usuarios
| Método | Endpoint | Rol requerido | Descripción |
|--------|----------------|----------------|----------------------------|
| GET | `/users` | ADMIN | Lista usuarios (paginado), devuelve `UserResponse` (nombre, email, rol, activo, bloqueado) |
| GET | `/users/{id}` | ADMIN | Consulta un usuario |
| PUT | `/users/{id}` | ADMIN | Actualiza un usuario |
| DELETE | `/users/{id}` | ADMIN | Elimina un usuario |

Todos los endpoints protegidos requieren el header:
```
Authorization: Bearer <token>
```

---

## Configuración y variables de entorno

El proyecto lee la configuración desde un archivo `.env` (o variables de entorno del sistema):

```env
DB_URL=jdbc:mysql://localhost:3306/hotel
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
JWT_SECRET=una_clave_secreta_de_al_menos_32_bytes
JWT_EXPIRATION=3600000
```

> `JWT_SECRET` debe tener al menos 32 bytes (256 bits) para el algoritmo HS256. Puedes generar uno con:
> ```bash
> openssl rand -base64 32
> ```
> `JWT_EXPIRATION` se expresa en milisegundos (ej. `3600000` = 1 hora).

---

## Cómo ejecutar el proyecto

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/hotel-reservation-api.git
   cd hotel-reservation-api
   ```

2. **Crea la base de datos** en MySQL (el esquema se genera automáticamente vía `spring.jpa.hibernate.ddl-auto=update`).

3. **Configura las variables de entorno** creando un archivo `.env` en la raíz del proyecto (ver sección anterior).

4. **Ejecuta la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```
   o, si usas Gradle:
   ```bash
   ./gradlew bootRun
   ```

5. La API quedará disponible en `http://localhost:8080`. Al iniciar por primera vez, `DataInitializer` creará el usuario administrador por defecto descrito arriba.

---

## Ejecutar con Docker

El proyecto incluye un `Dockerfile` multi-stage (build con Maven, ejecución con JRE) y un `docker-compose.yml` que levanta la API junto con una base de datos MySQL.

### Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
services:

  mysql:
    image: mysql:8.0
    container_name: hotel-db
    environment:
      MYSQL_DATABASE: hotel
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-proot"]
      interval: 5s
      timeout: 5s
      retries: 10

  app:
    build: .
    container_name: hotel-api
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
    env_file:
      - .env

volumes:
  mysql_data:
```

El healthcheck en `mysql` evita que la aplicación arranque antes de que la base de datos esté lista para aceptar conexiones.

### Variables de entorno para Docker

Dentro de la red interna de docker-compose, la app se conecta al servicio `mysql` por su nombre, no por `localhost`. El puerto `3307` del host solo sirve para acceder a la base de datos desde tu máquina; internamente MySQL sigue escuchando en el `3306`. Ajusta el `.env` de la raíz del proyecto así:

```env
DB_URL=jdbc:mysql://mysql:3306/hotel
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=una_clave_secreta_de_al_menos_32_bytes
JWT_EXPIRATION=3600000
```

### Levantar los contenedores

```bash
docker compose up --build
```

Esto construye la imagen de la API, levanta MySQL, espera a que esté saludable y luego inicia la aplicación. La API queda disponible en `http://localhost:8080` y MySQL accesible desde el host en el puerto `3307`.

Para detener y limpiar:

```bash
docker compose down
```

Para eliminar también el volumen de datos de MySQL:

```bash
docker compose down -v
```

> Se recomienda agregar un archivo `.dockerignore` con al menos `target/`, `.git/` y `.env` para mantener el contexto de build limpio y evitar filtrar secretos en la imagen.

---

## Testing

```bash
./mvnw test
```

---

## Notas de diseño

- El `csrf` está deshabilitado de forma segura porque la API es 100% *stateless* (`SessionCreationPolicy.STATELESS`) y el JWT viaja siempre por el header `Authorization`, nunca por cookies.
- El login valida explícitamente que la cuenta esté activa y no bloqueada **antes** de comprobar la contraseña, ya que ese flujo no pasa por `AuthenticationManager` y por tanto no queda cubierto solo con `CustomUserDetailsService`.
- Los orígenes permitidos por CORS están centralizados en `SecurityConfig` y deben ajustarse a los dominios reales del frontend en producción.
- `UserResponse` solo expone `name`, `email`, `role`, `active` y `locked`; la contraseña nunca se serializa hacia el cliente.
- `DataInitializer` es idempotente: comprueba la existencia del admin por email antes de crearlo, por lo que puede ejecutarse en cada arranque sin duplicar la cuenta.

---
