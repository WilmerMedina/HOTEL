# Hotel Reservation API

API REST para la gestión de reservas de un hotel, construida con **Spring Boot** y **Spring Security**. Permite el registro/login de usuarios mediante **JWT**, la administración de habitaciones y la creación/consulta de reservas, con control de acceso basado en roles (`ADMIN` / `CLIENT`).

Proyecto de portafolio orientado a demostrar buenas prácticas de arquitectura en capas, seguridad y validación en una API backend con Spring Boot.

---

## Características

- **Autenticación con JWT** (registro y login) usando `io.jsonwebtoken`, con validación fail-fast del secret al arrancar la app.
- **Autorización basada en roles** (`ADMIN`, `CLIENT`) con `@PreAuthorize` a nivel de endpoint.
- **Gestión de habitaciones**: alta, edición, baja y listado paginado.
- **Gestión de reservas**: creación con validación de fechas y detección de solapamientos (no se puede reservar una habitación ya ocupada en esas fechas), cálculo automático del precio total según noches.
- **Gestión de usuarios**: listado, consulta, edición y baja (solo `ADMIN`).
- **Cuentas activables/bloqueables**: el login rechaza cuentas inactivas o bloqueadas antes de validar la contraseña.
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
├── controller # Endpoints REST
├── dto
│ ├── request # DTOs de entrada
│ └── response # DTOs de salida
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
| GET | `/users` | ADMIN | Lista usuarios (paginado) |
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

5. La API quedará disponible en `http://localhost:8080`.

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

---

## Licencia

Este proyecto se distribuye bajo la licencia MIT. Puedes usarlo libremente como referencia o base para tus propios proyectos.
