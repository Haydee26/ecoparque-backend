# EcoParque – Backend

Servidor (API REST) del **Sistema de gestión del Ecoparque**. Trabajo práctico final de **Programación II** – INSPT-UTN, docente Miguel Silva.

**Autora:** Haydee (trabajo individual).

## Repositorios del proyecto

| Parte | Repositorio |
|---|---|
| Backend (este) | https://github.com/Haydee26/ecoparque-backend |
| Frontend principal (Vaadin) | https://github.com/Haydee26/ecoparque-frontend-vaadin |
| Frontend secundario (JSP) | https://github.com/Haydee26/ecoparque-frontend-jsp |

## Qué hace

Administra el parque: zonas, hábitats (con sus continentes), especies, itinerarios, guías, cuidadores, asignaciones de guías, responsabilidades de cuidadores y usuarios. Calcula el registro numérico del parque (animales, personal, área) y lista al personal por antigüedad.

Hay tres actores, con permisos distintos:

| Actor | Qué puede hacer |
|---|---|
| **Administrador** | Gestiona todo (alta, baja y modificación), ve el registro del parque, los datos completos del personal y la lista por antigüedad |
| **Guía** | Consulta el parque y sus propios datos (itinerarios y horarios) |
| **Cuidador** | Consulta el parque y sus propios datos (especies a su cargo) |

## Tecnologías

- Java, Spring Boot (Web, Data JPA, Security)
- MySQL
- Autenticación HTTP Basic con claves encriptadas con BCrypt
- Maven, despliegue como WAR en Apache Tomcat

## Arquitectura

Por capas, con responsabilidades separadas:

`controller` (recibe pedidos) → `service` (reglas de negocio y validaciones) → `repository` (acceso a datos) → base de datos. Los errores de negocio se devuelven con códigos claros (404 si no existe, 409 si rompe una regla).

## Cómo ponerlo en marcha

1. Crear la base de datos ejecutando `database/ecoparque.sql` en MySQL (crea las 13 tablas y datos de ejemplo).
2. En `src/main/resources/application.properties` poner el usuario y la clave de MySQL de tu equipo (la base es `ecoparque` en `localhost:3306`).
3. Compilar con Maven (`mvn clean package`) y desplegar el WAR en Tomcat, en la raíz (`/`), puerto 8080.
4. Probar: `GET http://localhost:8080/api/zonas` con usuario y clave.

## Usuarios de prueba

| Usuario | Clave | Rol |
|---|---|---|
| admin | 1234 | Administrador |
| laura | 1111 | Guía |
| mario | 2222 | Cuidador |

Las claves del script están en texto plano de ejemplo; al arrancar, el sistema las encripta solo.

## Algunos endpoints

- `GET /api/personal/antiguedad` – guías y cuidadores por antigüedad (solo administrador)
- `GET /api/personal/yo` – datos de quien inició sesión
- `GET /api/guias/publico` – guías sin datos personales
- `/api/zonas`, `/api/habitats`, `/api/especies`, `/api/itinerarios`, `/api/guias`, `/api/cuidadores`, `/api/usuarios` – gestión (altas, bajas y modificaciones solo para administrador)
