# EcoParque – Backend

Servidor (API REST) del **Sistema de gestión del Ecoparque**. Trabajo práctico final de **Programación II** – INSPT-UTN, docente Miguel Silva.

**Autora:** Haydee Cruz (trabajo individual).

## Repositorios del proyecto

| Parte | Repositorio |
|---|---|
| Backend (este) | https://github.com/Haydee26/ecoparque-backend |
| Frontend principal (Vaadin) | https://github.com/Haydee26/ecoparque-frontend-vaadin |
| Frontend secundario (JSP) | https://github.com/Haydee26/ecoparque-frontend-jsp |
| Versión por consola | https://github.com/Haydee26/ecoparque-consola |

## Qué hace

Administra el parque: zonas (con su lugar en el mapa), hábitats (con sus continentes), especies (con el aviso de salud), itinerarios, empleados, guías, cuidadores, asignaciones de guías, responsabilidades de cuidadores, usuarios y roles. Calcula el registro numérico del parque (animales, personal, área) y lista al personal por antigüedad.

Todas las personas del parque son filas de `empleado`; **Guía** y **Cuidador** son *puestos* que apuntan a un empleado.

## Usuarios y roles

Cada usuario tiene un **rol**, y cada rol tiene **permisos**. Hay cuatro roles base que vienen cargados y no se pueden borrar ni renombrar, y el administrador puede **crear roles nuevos** y elegir sus permisos.

| Rol base | Qué puede hacer |
|---|---|
| **Administrador** | Todo (los 26 permisos) |
| **Guía** | Consulta el parque, el mapa, la lista de guías y sus propios datos |
| **Cuidador** | Consulta el parque, el mapa y sus propios datos, y puede **avisar que una especie está enferma** |
| **Invitado** | Visitante que no es empleado: misma consulta, sin datos personales |

Los permisos son **Ver** y **Editar** en 13 secciones (Zonas, Hábitats, Especies, Itinerarios, Asignaciones, Responsabilidades, Empleados, Guías, Cuidadores, Usuarios, Roles, Parque y Salud de las especies), es decir 26 en total. Editar incluye Ver. El catálogo de permisos está fijo en el código (`Permiso`); lo que se guarda en la base es qué permisos tiene cada rol.

### Salud de las especies y el rol Veterinario

El cuidador avisa desde Especies que una especie está enferma y escribe qué le pasa. El administrador y el **veterinario** ven la lista de especies enfermas, pueden corregir el texto y marcarlas sanas.

- El aviso se guarda en la propia especie: `enferma`, `observacion_salud`, `fecha_aviso_salud` y `avisado_por`.
- La **fecha** y **quién avisó** los pone el servidor (la fecha del día y el usuario de la sesión), no la pantalla, así que no se pueden falsificar.
- Tiene su propia sección de permisos para no darle al cuidador el permiso de editar especies: **Salud: Editar** permite avisar y corregir; **Salud: Ver** permite ver la lista.
- **Veterinario** es un rol creado por el administrador (no es base): viene en el script de ejemplo con Especies (Ver), Cuidadores (Ver) y Salud (Ver y Editar).

Reglas principales:

- Un rol que tiene usuarios asignados no se puede eliminar, y todo rol necesita al menos un permiso.
- El nombre de la persona de un usuario tiene que ser un empleado ya cargado (nombre y apellido completos). El Invitado queda exento; en los roles nuevos es opcional.
- Guía y Cuidador exigen que la persona tenga ese puesto.
- No se puede eliminar al único administrador ni al propio usuario.
- Al arrancar, `InicializadorDeRoles` crea los roles base si faltan.

## Tecnologías

- Java, Spring Boot (Web, Data JPA, Security)
- MySQL
- Autenticación HTTP Basic con claves encriptadas con BCrypt
- Maven, despliegue como WAR en Apache Tomcat

## Arquitectura

Por capas, con responsabilidades separadas:

`controller` (recibe pedidos) → `service` (reglas de negocio y validaciones) → `repository` (acceso a datos) → base de datos. Los errores de negocio se devuelven con códigos claros (404 si no existe, 409 si rompe una regla, 400 si los datos no tienen el formato esperado).

La seguridad (`SecurityConfig`) decide el acceso a cada ruta según los **permisos** del rol del usuario, no según el nombre del rol.

## Cómo ponerlo en marcha

1. Crear la base de datos ejecutando `database/ecoparque.sql` en MySQL (crea las **16 tablas** y datos de ejemplo, incluidos los roles base, el rol Veterinario y sus permisos). El script borra y vuelve a crear la base `ecoparque`.
2. En `src/main/resources/application.properties` poner el usuario y la clave de MySQL de tu equipo (la base es `ecoparque` en `localhost:3306`).
3. Compilar con Maven (`mvn clean package`) y desplegar el WAR en Tomcat, en la raíz (`/`), puerto 8080.
4. Probar: `GET http://localhost:8080/api/zonas` con usuario y clave.

## Usuarios de prueba

| Usuario | Clave | Rol |
|---|---|---|
| admin | 1234 | Administrador |
| laura | 1111 | Guía |
| mario | 2222 | Cuidador |
| invitado | 3333 | Invitado |
| sofi | 4444 | Veterinario |

Las claves del script están en texto plano de ejemplo; al arrancar, el sistema las encripta solo.

## Algunos endpoints

- `GET /api/roles/mis-permisos` – permisos de quien inició sesión
- `GET /api/roles/permisos` – catálogo de permisos
- `GET/POST/PUT/DELETE /api/roles` – gestión de roles (necesita permiso de Roles)
- `GET /api/personal/antiguedad` – guías y cuidadores por antigüedad
- `GET /api/personal/yo` – datos de quien inició sesión
- `GET /api/especies/enfermas` – especies avisadas como enfermas (permiso Salud: Ver)
- `PUT /api/especies/{id}/enferma` – avisar que una especie está enferma, con `{"observacion": "..."}` (permiso Salud: Editar)
- `PUT /api/especies/{id}/sana` – marcarla como sana (permiso Salud: Editar)
- `GET /api/guias/publico` – guías sin datos personales
- `/api/zonas`, `/api/habitats`, `/api/especies`, `/api/itinerarios`, `/api/empleados`, `/api/guias`, `/api/cuidadores`, `/api/usuarios`, `/api/parque` – gestión (ver con permiso de Ver, modificar con permiso de Editar de la sección)
