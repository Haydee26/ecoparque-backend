-- Esquema de la base de datos del Ecoparque.
-- Entidades: zona, habitat, especie, itinerario, guia, cuidador, usuario.
-- Relaciones simples M:N (sin atributos propios): especie_habitat, itinerario_zona.
-- Entidades asociativas (M:N CON atributos propios, por eso tienen PK propia):
--   asignacion_guia (hora desde/hasta), responsabilidad_cuidador (fecha desde/hasta).
-- parque: un unico registro (id 1) con el area total; lo carga el administrador.

CREATE DATABASE IF NOT EXISTS ecoparque CHARACTER SET utf8mb4;
USE ecoparque;

CREATE TABLE zona (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL UNIQUE,
    extension DOUBLE NOT NULL
);

CREATE TABLE habitat (
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    nombre                   VARCHAR(100) NOT NULL UNIQUE,
    clima                    VARCHAR(100) NOT NULL,
    vegetacion_predominante  VARCHAR(100) NOT NULL
);

-- Atributo multivalor de habitat (continentes): tabla propia, sigue
-- siendo parte de la entidad habitat, no una relacion entre entidades.
CREATE TABLE habitat_continente (
    habitat_id  INT NOT NULL,
    continente  VARCHAR(50) NOT NULL,
    PRIMARY KEY (habitat_id, continente),
    FOREIGN KEY (habitat_id) REFERENCES habitat(id) ON DELETE CASCADE
);

CREATE TABLE especie (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre_espanol     VARCHAR(100) NOT NULL UNIQUE,
    nombre_cientifico  VARCHAR(150) NOT NULL,
    descripcion        VARCHAR(500),
    cantidad_ejemplares INT NOT NULL DEFAULT 0,
    zona_id            INT NOT NULL,
    FOREIGN KEY (zona_id) REFERENCES zona(id)
);

-- Relacion simple M:N (sin atributos propios): una especie vive en
-- varios habitats y un habitat aloja varias especies.
CREATE TABLE especie_habitat (
    especie_id  INT NOT NULL,
    habitat_id  INT NOT NULL,
    PRIMARY KEY (especie_id, habitat_id),
    FOREIGN KEY (especie_id) REFERENCES especie(id) ON DELETE CASCADE,
    FOREIGN KEY (habitat_id) REFERENCES habitat(id) ON DELETE CASCADE
);

CREATE TABLE itinerario (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    codigo             VARCHAR(20) NOT NULL UNIQUE,
    duracion_minutos   INT NOT NULL,
    longitud_metros    DOUBLE NOT NULL,
    maximo_visitantes  INT NOT NULL,
    cantidad_especies  INT NOT NULL
);

-- Relacion simple M:N (sin atributos propios): un itinerario recorre
-- varias zonas y una zona es recorrida por varios itinerarios.
CREATE TABLE itinerario_zona (
    itinerario_id  INT NOT NULL,
    zona_id        INT NOT NULL,
    PRIMARY KEY (itinerario_id, zona_id),
    FOREIGN KEY (itinerario_id) REFERENCES itinerario(id) ON DELETE CASCADE,
    FOREIGN KEY (zona_id) REFERENCES zona(id) ON DELETE CASCADE
);

CREATE TABLE guia (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL UNIQUE,
    direccion      VARCHAR(150),
    telefono       VARCHAR(30),
    fecha_ingreso  DATE NOT NULL
);

CREATE TABLE cuidador (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL UNIQUE,
    direccion      VARCHAR(150),
    telefono       VARCHAR(30),
    fecha_ingreso  DATE NOT NULL
);

CREATE TABLE parque (
    id          BIGINT PRIMARY KEY,
    nombre      VARCHAR(100),
    area_total  DOUBLE
);

CREATE TABLE usuario (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario  VARCHAR(50) NOT NULL UNIQUE,
    -- 100 caracteres: la clave se guarda encriptada con BCrypt (60 caracteres).
    clave           VARCHAR(100) NOT NULL,
    rol             ENUM('ADMINISTRADOR', 'GUIA', 'CUIDADOR') NOT NULL,
    nombre_persona  VARCHAR(100) NOT NULL DEFAULT ''
);

-- Entidad asociativa: Guia <-> Itinerario, con atributo propio "hora".
-- Al tener un atributo propio necesita clave primaria propia (no alcanza
-- con la clave compuesta guia_id+itinerario_id, porque un mismo guia
-- puede llevar el mismo itinerario en horas distintas).
CREATE TABLE asignacion_guia (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    guia_id        INT NOT NULL,
    itinerario_id  INT NOT NULL,
    hora           TIME NOT NULL,          -- hora desde
    hora_hasta     TIME NULL,              -- hora hasta
    FOREIGN KEY (guia_id) REFERENCES guia(id),
    FOREIGN KEY (itinerario_id) REFERENCES itinerario(id),
    UNIQUE (guia_id, itinerario_id, hora)
);

-- Entidad asociativa: Cuidador <-> Especie, con atributo propio "fecha_desde".
CREATE TABLE responsabilidad_cuidador (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    cuidador_id   INT NOT NULL,
    especie_id    INT NOT NULL,
    fecha_desde   DATE NOT NULL,
    fecha_hasta   DATE NULL,             -- vacia = sigue vigente
    FOREIGN KEY (cuidador_id) REFERENCES cuidador(id),
    FOREIGN KEY (especie_id) REFERENCES especie(id),
    UNIQUE (cuidador_id, especie_id, fecha_desde)
);

-- Estas claves de ejemplo estan en texto comun: el backend las encripta solo al arrancar.
INSERT INTO usuario (nombre_usuario, clave, rol, nombre_persona) VALUES
    ('admin', '1234', 'ADMINISTRADOR', ''),
    ('laura', '1111', 'GUIA', 'Laura Perez'),
    ('mario', '2222', 'CUIDADOR', 'Mario Gomez');


USE ecoparque;

-- PARQUE (area total en m2; el administrador puede cambiarla desde Registro del parque)
INSERT INTO parque (id, nombre, area_total) VALUES (1, 'EcoParque', 10000);

-- ZONAS
INSERT INTO zona (nombre, extension) VALUES
('Sabana Africana', 1500),
('Selva Tropical', 1200),
('Bosque Templado', 900);

-- HABITATS
INSERT INTO habitat (nombre, clima, vegetacion_predominante) VALUES
('Sabana', 'Calido y seco', 'Pastizales'),
('Selva', 'Calido y humedo', 'Vegetacion densa'),
('Bosque', 'Templado', 'Arboles y arbustos');

-- CONTINENTES DE LOS HABITATS
INSERT INTO habitat_continente (habitat_id, continente) VALUES
(1, 'Africa'),
(2, 'America del Sur'),
(3, 'Europa');

-- ESPECIES
INSERT INTO especie
(nombre_espanol, nombre_cientifico, descripcion, zona_id)
VALUES
('Leon', 'Panthera leo', 'Mamifero carnivoro', 1),
('Jaguar', 'Panthera onca', 'Felino de selva', 2),
('Oso pardo', 'Ursus arctos', 'Mamifero omnivoro', 3);

-- ESPECIE - HABITAT
INSERT INTO especie_habitat (especie_id, habitat_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- ITINERARIOS
INSERT INTO itinerario
(codigo, duracion_minutos, longitud_metros, maximo_visitantes, cantidad_especies)
VALUES
('IT01', 60, 1200, 20, 2),
('IT02', 45, 800, 15, 1);

-- ITINERARIO - ZONA
INSERT INTO itinerario_zona (itinerario_id, zona_id) VALUES
(1, 1),
(1, 2),
(2, 3);

-- GUIAS
INSERT INTO guia
(nombre, direccion, telefono, fecha_ingreso)
VALUES
('Laura Perez', 'Av. Rivadavia 1200', '11111111', '2024-03-10'),
('Carlos Diaz', 'Av. Corrientes 500', '22222222', '2023-08-15');

-- CUIDADORES
INSERT INTO cuidador
(nombre, direccion, telefono, fecha_ingreso)
VALUES
('Mario Gomez', 'Calle 10 150', '33333333', '2022-05-20'),
('Ana Lopez', 'Calle 20 300', '44444444', '2025-01-12');

-- ASIGNACION GUIA
INSERT INTO asignacion_guia
(guia_id, itinerario_id, hora)
VALUES
(1, 1, '10:00:00'),
(2, 2, '15:00:00');

-- RESPONSABILIDAD CUIDADOR
INSERT INTO responsabilidad_cuidador
(cuidador_id, especie_id, fecha_desde)
VALUES
(1, 1, '2025-01-01'),
(1, 2, '2025-02-01'),
(2, 3, '2025-03-01');
