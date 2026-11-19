-- Esquema de la base de datos del Ecoparque.
-- Entidades: zona, habitat, especie, especimen, itinerario, empleado, guia, cuidador, rol, usuario.
-- especie es solo la descripcion (nombre cientifico, descripcion, zona, habitats).
-- especimen es cada animal concreto: pertenece a una especie (relacion 1 a N) y tiene su
--   propio estado de salud (SANO, ENFERMO, EN_TRATAMIENTO) desde que ingresa al parque.
--   La cantidad de animales por especie no se guarda: se cuenta en la tabla especimen.
-- empleado: todas las personas del parque (incluido el administrador). Guia y cuidador son
-- roles de un empleado (relacion 1 a 1 con empleado), sin datos personales propios.
-- Relaciones simples M:N (sin atributos propios): especie_habitat, itinerario_zona.
-- Entidades asociativas (M:N CON atributos propios, por eso tienen PK propia):
--   asignacion_guia (hora desde/hasta), responsabilidad_cuidador (fecha desde/hasta).
-- rol: lo crea el administrador y le otorga permisos (tabla rol_permiso: cada permiso es
--   SECCION_VER o SECCION_EDITAR). Cada usuario tiene un rol.
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

-- La especie es la descripcion general. El nombre cientifico la identifica (UNIQUE):
-- el nombre comun cambia segun el pais, el cientifico es unico en el mundo.
CREATE TABLE especie (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cientifico  VARCHAR(150) NOT NULL UNIQUE,
    descripcion        VARCHAR(500),
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

-- Cada animal del parque. Relacion 1 a N con especie: la FK va del lado de "muchos".
-- Sin ON DELETE CASCADE: una especie con especimenes no se puede borrar.
CREATE TABLE especimen (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL,
    especie_id         INT NOT NULL,
    fecha_ingreso      DATE NOT NULL,
    estado_salud       VARCHAR(20) NOT NULL DEFAULT 'SANO',
    observacion_salud  VARCHAR(255),
    FOREIGN KEY (especie_id) REFERENCES especie(id)
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

-- Roles de usuario. Los cuatro "base" (es_base = 1) no se pueden borrar ni renombrar;
-- el administrador puede crear todos los que quiera desde la pantalla Roles.
CREATE TABLE rol (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(30) NOT NULL UNIQUE,
    descripcion  VARCHAR(200) NOT NULL DEFAULT '',
    es_base      TINYINT(1) NOT NULL DEFAULT 0
);

-- Permisos de cada rol (relacion M:N entre rol y el catalogo fijo de permisos del sistema).
CREATE TABLE rol_permiso (
    rol_id   INT NOT NULL,
    permiso  VARCHAR(40) NOT NULL,
    PRIMARY KEY (rol_id, permiso),
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE
);

CREATE TABLE usuario (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario  VARCHAR(50) NOT NULL UNIQUE,
    -- 100 caracteres: la clave se guarda encriptada con BCrypt (60 caracteres).
    clave           VARCHAR(100) NOT NULL,
    rol_id          INT NOT NULL,
    nombre_persona  VARCHAR(100) NOT NULL DEFAULT '',
    FOREIGN KEY (rol_id) REFERENCES rol(id)
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

-- ROLES base
INSERT INTO rol (nombre, descripcion, es_base) VALUES
    ('ADMINISTRADOR', 'Gestiona todo el sistema', 1),
    ('GUIA', 'Consulta el parque y sus propios itinerarios', 1),
    ('CUIDADOR', 'Consulta el parque y las especies a su cargo', 1),
    ('INVITADO', 'Visita: solo consulta el parque', 1);

-- ROL de ejemplo creado por el administrador (no es base): el veterinario ve los especimenes
-- que no estan sanos y puede cambiar su estado de salud.
INSERT INTO rol (nombre, descripcion, es_base) VALUES
    ('VETERINARIO', 'Atiende la salud de los especimenes', 0);

-- PERMISOS: el administrador tiene todos; guia, cuidador e invitado consultan el parque.
INSERT INTO rol_permiso (rol_id, permiso) VALUES
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ZONAS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ZONAS_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'HABITATS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'HABITATS_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ESPECIES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ESPECIES_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ITINERARIOS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ITINERARIOS_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ASIGNACIONES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ASIGNACIONES_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'RESPONSABILIDADES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'RESPONSABILIDADES_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'EMPLEADOS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'EMPLEADOS_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'GUIAS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'GUIAS_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'CUIDADORES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'CUIDADORES_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'USUARIOS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'USUARIOS_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ROLES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ROLES_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'PARQUE_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'PARQUE_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ESPECIMENES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'ESPECIMENES_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'SALUD_VER'),
    ((SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'SALUD_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'GUIA'), 'ZONAS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'GUIA'), 'HABITATS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'GUIA'), 'ESPECIES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'GUIA'), 'ITINERARIOS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'GUIA'), 'ASIGNACIONES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'GUIA'), 'RESPONSABILIDADES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'ZONAS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'HABITATS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'ESPECIES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'ITINERARIOS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'ASIGNACIONES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'RESPONSABILIDADES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'ESPECIMENES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'SALUD_EDITAR'),
    ((SELECT id FROM rol WHERE nombre = 'INVITADO'), 'ZONAS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'INVITADO'), 'HABITATS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'INVITADO'), 'ESPECIES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'INVITADO'), 'ITINERARIOS_VER'),
    ((SELECT id FROM rol WHERE nombre = 'INVITADO'), 'ASIGNACIONES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'INVITADO'), 'RESPONSABILIDADES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'VETERINARIO'), 'ESPECIES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'VETERINARIO'), 'CUIDADORES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'VETERINARIO'), 'ESPECIMENES_VER'),
    ((SELECT id FROM rol WHERE nombre = 'VETERINARIO'), 'SALUD_VER'),
    ((SELECT id FROM rol WHERE nombre = 'VETERINARIO'), 'SALUD_EDITAR');

-- Estas claves de ejemplo estan en texto comun: el backend las encripta solo al arrancar.
INSERT INTO usuario (nombre_usuario, clave, rol_id, nombre_persona) VALUES
    ('admin', '1234', (SELECT id FROM rol WHERE nombre = 'ADMINISTRADOR'), 'Sofia Ramirez'),
    ('laura', '1111', (SELECT id FROM rol WHERE nombre = 'GUIA'), 'Laura Perez'),
    ('mario', '2222', (SELECT id FROM rol WHERE nombre = 'CUIDADOR'), 'Mario Gomez'),
    ('invitado', '3333', (SELECT id FROM rol WHERE nombre = 'INVITADO'), 'Visita'),
    ('vet', '4444', (SELECT id FROM rol WHERE nombre = 'VETERINARIO'), 'Pablo Suarez');


-- PARQUE (area total en m2; el administrador puede cambiarla desde Registro del parque)
INSERT INTO parque (id, nombre, area_total) VALUES (1, 'EcoParque', 10000);

-- ZONAS (mapa de 10 columnas x 6 filas; el Reptilario usa el hueco de la columna 10)
INSERT INTO zona (nombre, extension, columna, fila, ancho, alto) VALUES
('Sabana Africana', 1500, 1, 1, 4, 3),
('Selva Tropical', 1200, 5, 1, 3, 3),
('Bosque Templado', 900, 4, 4, 3, 3),
('Montaña', 1100, 8, 1, 3, 3),
('Desierto', 1000, 1, 4, 3, 3),
('Humedal', 800, 7, 4, 3, 3),
('Reptilario', 300, 10, 4, 1, 3);

-- HABITATS
INSERT INTO habitat (nombre, clima, vegetacion_predominante) VALUES
('Sabana', 'Calido y seco', 'Pastizales'),
('Selva', 'Calido y humedo', 'Vegetacion densa'),
('Bosque', 'Templado', 'Arboles y arbustos'),
('Montaña', 'Frio y seco', 'Pastos de altura'),
('Desierto', 'Calido y arido', 'Cactus y arbustos bajos'),
('Humedal', 'Templado y humedo', 'Juncos y plantas acuaticas'),
('Terrario', 'Calido controlado', 'Arena, piedras y troncos');

-- CONTINENTES DE LOS HABITATS
INSERT INTO habitat_continente (habitat_id, continente) VALUES
(1, 'África'),
(2, 'América del Sur'),
(3, 'Europa'),
(4, 'América del Sur'),
(5, 'África'),
(6, 'América del Sur'),
(7, 'América del Sur');

-- ESPECIES (la descripcion empieza con el nombre comun, para saber de que animal se trata)
INSERT INTO especie (nombre_cientifico, descripcion, zona_id) VALUES
-- Sabana Africana (zona 1)
('Panthera leo', 'León: felino carnívoro que vive en manadas', 1),
('Giraffa camelopardalis', 'Jirafa: el animal terrestre más alto', 1),
('Loxodonta africana', 'Elefante africano: el mamífero terrestre más grande', 1),
('Equus quagga', 'Cebra: herbívoro de rayas blancas y negras', 1),
-- Selva Tropical (zona 2)
('Panthera onca', 'Yaguareté: el felino más grande de América', 2),
('Ramphastos toco', 'Tucán toco: ave de pico grande y anaranjado', 2),
('Alouatta caraya', 'Mono carayá: mono aullador del norte argentino', 2),
('Amazona aestiva', 'Loro hablador: loro verde que imita sonidos', 2),
-- Bosque Templado (zona 3)
('Ursus arctos', 'Oso pardo: mamífero omnívoro de gran tamaño', 3),
('Lycalopex griseus', 'Zorro gris: zorro pequeño de la Patagonia', 3),
('Cervus elaphus', 'Ciervo colorado: ciervo grande de cuernos ramificados', 3),
-- Montaña (zona 4)
('Vultur gryphus', 'Cóndor andino: ave carroñera, una de las voladoras más grandes del mundo', 4),
('Puma concolor', 'Puma: felino solitario de las montañas de América', 4),
('Lama guanicoe', 'Guanaco: camélido salvaje de la Patagonia y los Andes', 4),
-- Desierto (zona 5)
('Camelus dromedarius', 'Dromedario: herbívoro del desierto con una sola joroba', 5),
('Suricata suricatta', 'Suricata: pequeño mamífero que vive en colonias', 5),
-- Humedal (zona 6)
('Phoenicopterus chilensis', 'Flamenco austral: ave zancuda de plumaje rosado', 6),
('Hydrochoerus hydrochaeris', 'Carpincho: el roedor más grande del mundo', 6),
('Caiman latirostris', 'Yacaré overo: reptil de los esteros del Litoral', 6),
-- Reptilario (zona 7)
('Chelonoidis chilensis', 'Tortuga terrestre argentina: tortuga de tierra de vida muy larga', 7),
('Iguana iguana', 'Iguana verde: reptil herbívoro de cola larga', 7),
('Boa constrictor', 'Boa constrictora: serpiente que atrapa a sus presas enrollándose', 7),
('Grammostola rosea', 'Tarántula rosada: araña grande y peluda, de picadura leve', 7),
('Pandinus imperator', 'Escorpión emperador: escorpión negro de gran tamaño', 7);

-- ESPECIE - HABITAT (cada especie con el habitat de su zona)
INSERT INTO especie_habitat (especie_id, habitat_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1),
(5, 2), (6, 2), (7, 2), (8, 2),
(9, 3), (10, 3), (11, 3),
(12, 4), (13, 4), (14, 4),
(15, 5), (16, 5),
(17, 6), (18, 6), (19, 6),
(20, 7), (21, 7), (22, 7), (23, 7), (24, 7);

-- ESPECIMENES (cada animal; la cantidad por especie se cuenta, no se guarda)
INSERT INTO especimen (nombre, especie_id, fecha_ingreso, estado_salud, observacion_salud) VALUES
-- Leones
('Simba', 1, '2022-04-15', 'SANO', NULL),
('Nala', 1, '2022-04-15', 'SANO', NULL),
('Kiara', 1, '2026-08-02', 'ENFERMO', 'Llegó con una herida en la pata'),
-- Jirafas
('Estela', 2, '2023-05-25', 'SANO', NULL),
('Rafa', 2, '2024-08-16', 'SANO', NULL),
-- Elefantes
('Tembo', 3, '2020-12-01', 'SANO', NULL),
('Kali', 3, '2022-02-22', 'SANO', NULL),
-- Cebras
('Rayitas', 4, '2023-10-03', 'SANO', NULL),
('Zebi', 4, '2025-01-27', 'SANO', NULL),
-- Yaguaretes
('Tobuna', 5, '2023-06-20', 'SANO', NULL),
('Arami', 5, '2024-11-05', 'SANO', NULL),
-- Tucanes
('Pico', 6, '2024-06-10', 'SANO', NULL),
('Mango', 6, '2025-07-19', 'SANO', NULL),
-- Monos caraya
('Coco', 7, '2023-02-11', 'SANO', NULL),
('Tita', 7, '2023-02-11', 'SANO', NULL),
('Chango', 7, '2025-09-04', 'SANO', NULL),
-- Loros habladores
('Pepe', 8, '2022-11-30', 'SANO', NULL),
('Lorenza', 8, '2024-04-14', 'SANO', NULL),
-- Osos pardos
('Bruno', 9, '2021-05-10', 'SANO', NULL),
('Greta', 9, '2021-05-10', 'SANO', NULL),
-- Zorros grises
('Pampa', 10, '2024-03-21', 'SANO', NULL),
('Gris', 10, '2025-06-08', 'SANO', NULL),
-- Ciervos colorados
('Bambi', 11, '2023-08-19', 'SANO', NULL),
('Rojo', 11, '2023-08-19', 'SANO', NULL),
-- Condores
('Inti', 12, '2022-10-01', 'EN_TRATAMIENTO', 'Rescatado con intoxicación por plomo'),
('Wayra', 12, '2023-03-18', 'SANO', NULL),
-- Pumas
('Tayra', 13, '2023-12-04', 'SANO', NULL),
('Ñandi', 13, '2025-05-12', 'SANO', NULL),
-- Guanacos
('Chulengo', 14, '2024-01-09', 'SANO', NULL),
('Lana', 14, '2024-01-09', 'SANO', NULL),
('Andes', 14, '2025-11-02', 'SANO', NULL),
-- Dromedarios
('Omar', 15, '2022-07-07', 'SANO', NULL),
('Duna', 15, '2025-04-30', 'SANO', NULL),
-- Suricatas
('Zuri', 16, '2025-03-08', 'SANO', NULL),
('Kibo', 16, '2025-03-08', 'SANO', NULL),
('Nuri', 16, '2025-03-08', 'SANO', NULL),
-- Flamencos
('Rosa', 17, '2024-01-20', 'SANO', NULL),
('Coral', 17, '2024-01-20', 'SANO', NULL),
('Fresa', 17, '2024-03-02', 'SANO', NULL),
-- Carpinchos
('Pancho', 18, '2024-09-30', 'ENFERMO', 'Parásitos intestinales, en observación'),
('Lola', 18, '2024-09-30', 'SANO', NULL),
('Chiqui', 18, '2026-01-15', 'SANO', NULL),
-- Yacares
('Dientes', 19, '2023-07-14', 'SANO', NULL),
('Overo', 19, '2024-12-02', 'SANO', NULL),
-- Tortugas
('Manuelita', 20, '2015-03-01', 'SANO', NULL),
('Tortu', 20, '2021-09-17', 'SANO', NULL),
-- Iguanas
('Verdi', 21, '2024-05-23', 'SANO', NULL),
('Iggy', 21, '2025-08-11', 'EN_TRATAMIENTO', 'Muda de piel incompleta, en control'),
-- Boas
('Kaa', 22, '2022-06-06', 'SANO', NULL),
-- Tarantulas
('Rosita', 23, '2024-10-10', 'SANO', NULL),
('Peluda', 23, '2025-02-18', 'SANO', NULL),
-- Escorpiones
('Negro', 24, '2025-07-01', 'SANO', NULL);

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
(3, 6),
(3, 7);

-- EMPLEADOS (todas las personas del parque)
INSERT INTO empleado
(nombre, apellido, dni, fecha_nacimiento, direccion, telefono, correo, fecha_ingreso)
VALUES
('Sofia', 'Ramirez', '28123456', '1985-04-12', 'Calle 5 800', '11110000', 'sofia.ramirez@ecoparque.com', '2021-02-01'),
('Laura', 'Perez', '33456789', '1990-07-21', 'Av. Rivadavia 1200', '11111111', 'laura.perez@ecoparque.com', '2024-03-10'),
('Carlos', 'Diaz', '31234567', '1988-11-02', 'Av. Corrientes 500', '22222222', NULL, '2023-08-15'),
('Mario', 'Gomez', '30987654', '1986-01-30', 'Calle 10 150', '33333333', 'mario.gomez@ecoparque.com', '2022-05-20'),
('Ana', 'Lopez', '35678901', '1993-09-14', 'Calle 20 300', '44444444', NULL, '2025-01-12'),
('Julia', 'Torres', '36123987', '1995-03-08', 'Calle 30 450', '55555555', NULL, '2025-06-01'),
('Pablo', 'Suarez', '29876543', '1984-06-18', 'Calle 40 600', '66666666', 'pablo.suarez@ecoparque.com', '2023-11-05');

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
(1, 5, '2025-02-01'),
(2, 9, '2025-03-01'),
(2, 22, '2025-04-01');