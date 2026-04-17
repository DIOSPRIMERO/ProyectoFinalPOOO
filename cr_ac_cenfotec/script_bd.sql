-- ============================================================
-- Script de creacion de base de datos
-- Plataforma de Subastas Especializadas - UCENFOTEC
-- SOFT-04 / BISOFT-07
--
-- INSTRUCCIONES:
-- 1. Abre SQL Server Management Studio (SSMS)
-- 2. Conectate a tu servidor con usuario "sa"
-- 3. Abre este archivo: File > Open > File...
-- 4. Presiona F5 o el boton "Execute" para correr el script
-- 5. Luego edita db.properties con tu servidor y contraseña
-- ============================================================

-- Crear la base de datos
CREATE DATABASE SubastasDB;
GO
USE SubastasDB;
GO

-- ============================================================
-- Tabla Usuario
-- idRol: 1=Moderador, 2=Vendedor, 3=Coleccionista
-- ============================================================
CREATE TABLE Usuario (
    idUsuario         INT           PRIMARY KEY IDENTITY(1,1),
    nombreCompleto    NVARCHAR(150) NOT NULL,
    identificacion    NVARCHAR(50)  NOT NULL UNIQUE,
    fechaNacimiento   DATE          NOT NULL,  -- formato aaaa-MM-dd
    contrasena        NVARCHAR(100) NOT NULL,
    correoElectronico NVARCHAR(150) NOT NULL,
    activo            BIT           NOT NULL DEFAULT 1,
    idRol             INT           NOT NULL,  -- 1=Moderador 2=Vendedor 3=Coleccionista
    puntuacion        FLOAT                  DEFAULT 0,
    direccion         NVARCHAR(200),
    esModerador       BIT                    DEFAULT 0  -- moderador de subastas (coleccionista)
);
GO

-- ============================================================
-- Tabla Categoria
-- ============================================================
CREATE TABLE Categoria (
    idCategoria  INT           PRIMARY KEY IDENTITY(1,1),
    nombre       NVARCHAR(100) NOT NULL,
    descripcion  NVARCHAR(255)
);
GO

-- ============================================================
-- Tabla ObjetoSubasta
-- ============================================================
CREATE TABLE ObjetoSubasta (
    idObjeto     INT           PRIMARY KEY IDENTITY(1,1),
    nombre       NVARCHAR(150) NOT NULL,
    descripcion  NVARCHAR(255),
    estado       NVARCHAR(50)  NOT NULL,  -- 'Nuevo', 'Usado', 'Antiguo sin abrir'
    fechaCompra  DATE          NOT NULL  -- formato aaaa-MM-dd
);
GO

-- ============================================================
-- Tabla Subasta
-- ============================================================
CREATE TABLE Subasta (
    idSubasta             INT           PRIMARY KEY IDENTITY(1,1),
    idCreador             INT           NOT NULL REFERENCES Usuario(idUsuario),
    fechaVencimiento      NVARCHAR(20)  NOT NULL,
    precioMinimo          FLOAT         NOT NULL,
    estado                NVARCHAR(30)  NOT NULL DEFAULT 'Activa',
    idModeradorAsignado   INT           REFERENCES Usuario(idUsuario),
    adjudicacionAceptada  BIT           NOT NULL DEFAULT 0,
    entregaConfirmada     BIT           NOT NULL DEFAULT 0,
    calificacionGanador   INT                    DEFAULT 0,
    calificacionVendedor  INT                    DEFAULT 0
);
GO

-- ============================================================
-- Tabla SubastaObjeto (relacion N:N)
-- ============================================================
CREATE TABLE SubastaObjeto (
    idSubasta  INT NOT NULL REFERENCES Subasta(idSubasta),
    idObjeto   INT NOT NULL REFERENCES ObjetoSubasta(idObjeto),
    PRIMARY KEY (idSubasta, idObjeto)
);
GO

-- ============================================================
-- Tabla Oferta
-- ============================================================
CREATE TABLE Oferta (
    idOferta           INT           PRIMARY KEY IDENTITY(1,1),
    idSubasta          INT           NOT NULL REFERENCES Subasta(idSubasta),
    nombreOferente     NVARCHAR(150) NOT NULL,
    puntuacionOferente FLOAT,
    precioOfertado     FLOAT         NOT NULL,
    fechaOferta        DATE          NOT NULL DEFAULT GETDATE()
);
GO

-- ============================================================
-- Tabla OrdenAdjudicacion
-- ============================================================
CREATE TABLE OrdenAdjudicacion (
    idOrden       INT           PRIMARY KEY IDENTITY(1,1),
    idSubasta     INT           NOT NULL REFERENCES Subasta(idSubasta),
    nombreGanador NVARCHAR(150) NOT NULL,
    fechaOrden    DATE          NOT NULL DEFAULT GETDATE(),
    precioTotal   FLOAT         NOT NULL
);
GO

-- ============================================================
-- Verificar que las tablas se crearon correctamente
-- ============================================================
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';
GO
