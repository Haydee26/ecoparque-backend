-- Esquema de la base de datos del Ecoparque.
-- Entidades: zona, habitat, especie, itinerario, empleado, guia, cuidador, usuario.
-- empleado: todas las personas del parque (incluido el administrador). Guia y cuidador son
-- roles de un empleado (relacion 1 a 1 con empleado), sin datos personales propios.
-- Relaciones simples M:N (sin atributos propios): especie_habitat, itinerario_zona.
-- Entidades asociativas (M:N CON atributos propios, por eso tienen PK propia):
--   asignacion_guia (hora desde/hasta), responsabilidad_cuidador (fecha desde/hasta).
-- parque: un unico registro (id 1) con el area total; lo carga el administrador.

-- ATENCION: esta linea borra la base "ecoparque" completa y la vuelve a crear con datos de ejemplo.
DROP DATABASE IF EXISTS ecoparque;
CREATE DATABASE ecoparque CHARACTER SET utf8mb4;
USE ecoparque;

CREATE TABLE zona (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL UNIQUE,
    extension DOUBLE NOT NULL,
    -- Lugar en el mapa del parque (cuadricula de 10 columnas x 6 filas): esquina superior
    -- izquierda y tamano en casilleros. Son opcionales: una zona sin ubicar no se dibuja.
    columna   INT NULL,
    fila      INT NULL,
    ancho     INT NULL,
    alto      INT NULL
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

CREATE TABLE empleado (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    dni               VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento  DATE NOT NULL,
    direccion         VARCHAR(150) NOT NULL,
    telefono          VARCHAR(30) NOT NULL,
    correo            VARCHAR(150),
    fecha_ingreso     DATE NOT NULL
);

-- Roles: un empleado puede ser guia, cuidador, ambos o ninguno (UNIQUE = una sola vez cada rol).
CREATE TABLE guia (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    empleado_id  INT NOT NULL UNIQUE,
    FOREIGN KEY (empleado_id) REFERENCES empleado(id)
);

CREATE TABLE cuidador (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    empleado_id  INT NOT NULL UNIQUE,
    FOREIGN KEY (empleado_id) REFERENCES empleado(id)
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
    rol             ENUM('ADMINISTRADOR', 'GUIA', 'CUIDADOR', 'INVITADO') NOT NULL,
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
    ('admin', '1234', 'ADMINISTRADOR', 'Sofia Ramirez'),
    ('laura', '1111', 'GUIA', 'Laura Perez'),
    ('mario', '2222', 'CUIDADOR', 'Mario Gomez'),
    ('invitado', '3333', 'INVITADO', 'Visita');


-- PARQUE (area total en m2; el administrador puede cambiarla desde Registro del parque)
INSERT INTO parque (id, nombre, area_total) VALUES (1, 'EcoParque', 10000);

-- ZONAS
INSERT INTO zona (nombre, extension, columna, fila, ancho, alto) VALUES
('Sabana Africana', 1500, 1, 1, 4, 3),
('Selva Tropical', 1200, 5, 1, 3, 3),
('Bosque Templado', 900, 4, 4, 3, 3),
('Montaña', 1100, 8, 1, 3, 3),
('Desierto', 1000, 1, 4, 3, 3),
('Humedal', 800, 7, 4, 3, 3);

-- HABITATS
INSERT INTO habitat (nombre, clima, vegetacion_predominante) VALUES
('Sabana', 'Calido y seco', 'Pastizales'),
('Selva', 'Calido y humedo', 'Vegetacion densa'),
('Bosque', 'Templado', 'Arboles y arbustos'),
('Montaña', 'Frio y seco', 'Pastos de altura'),
('Desierto', 'Calido y arido', 'Cactus y arbustos bajos'),
('Humedal', 'Templado y humedo', 'Juncos y plantas acuaticas');

-- CONTINENTES DE LOS HABITATS
INSERT INTO habitat_continente (habitat_id, continente) VALUES
(1, 'África'),
(2, 'América del Sur'),
(3, 'Europa'),
(4, 'América del Sur'),
(5, 'África'),
(6, 'América del Sur');

-- ESPECIES
INSERT INTO especie
(nombre_espanol, nombre_cientifico, descripcion, cantidad_ejemplares, zona_id)
VALUES
('Leon', 'Panthera leo', 'Mamifero carnivoro', 4, 1),
('Jaguar', 'Panthera onca', 'Felino de selva', 2, 2),
('Oso pardo', 'Ursus arctos', 'Mamifero omnivoro', 3, 3),
('Condor andino', 'Vultur gryphus', 'Ave carroñera de gran tamaño', 2, 4),
('Camello', 'Camelus dromedarius', 'Mamifero herbivoro del desierto', 3, 5),
('Flamenco', 'Phoenicopterus chilensis', 'Ave zancuda de plumaje rosado', 6, 6);

-- ESPECIE - HABITAT
INSERT INTO especie_habitat (especie_id, habitat_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6);

-- ITINERARIOS
INSERT INTO itinerario
(codigo, duracion_minutos, longitud_metros, maximo_visitantes, cantidad_especies)
VALUES
('IT01', 60, 1200, 20, 2),
('IT02', 45, 800, 15, 1),
('IT03', 50, 1000, 18, 3);

-- ITINERARIO - ZONA
INSERT INTO itinerario_zona (itinerario_id, zona_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 4),
(3, 5),
(3, 6);

-- EMPLEADOS (todas las personas del parque)
INSERT INTO empleado
(nombre, apellido, dni, fecha_nacimiento, direccion, telefono, correo, fecha_ingreso)
VALUES
('Sofia', 'Ramirez', '28123456', '1985-04-12', 'Calle 5 800', '11110000', 'sofia.ramirez@ecoparque.com', '2021-02-01'),
('Laura', 'Perez', '33456789', '1990-07-21', 'Av. Rivadavia 1200', '11111111', 'laura.perez@ecoparque.com', '2024-03-10'),
('Carlos', 'Diaz', '31234567', '1988-11-02', 'Av. Corrientes 500', '22222222', NULL, '2023-08-15'),
('Mario', 'Gomez', '30987654', '1986-01-30', 'Calle 10 150', '33333333', 'mario.gomez@ecoparque.com', '2022-05-20'),
('Ana', 'Lopez', '35678901', '1993-09-14', 'Calle 20 300', '44444444', NULL, '2025-01-12'),
('Julia', 'Torres', '36123987', '1995-03-08', 'Calle 30 450', '55555555', NULL, '2025-06-01');

-- GUIAS (rol de guia: Laura Perez y Carlos Diaz)
INSERT INTO guia (empleado_id) VALUES (2), (3);

-- CUIDADORES (rol de cuidador: Mario Gomez y Ana Lopez)
INSERT INTO cuidador (empleado_id) VALUES (4), (5);

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
