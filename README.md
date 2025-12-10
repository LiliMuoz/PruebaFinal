# CoopCredit - Sistema de Gestión de Créditos

Sistema completo para la gestión de solicitudes de crédito de una cooperativa, implementado con arquitectura de microservicios, arquitectura hexagonal y tecnologías modernas.

## Índice

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Ejecución](#ejecución)
- [API Documentation](#api-documentation)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Seguridad](#seguridad)
- [Observabilidad](#observabilidad)
- [Panel de Administración](#panel-de-administración)
- [Pruebas](#pruebas)

##  Arquitectura

### Diagrama de Microservicios

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                 │
│                    Angular 17+ (SPA)                            │
│                    Puerto: 4200                                  │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│              CREDIT-APPLICATION-SERVICE                          │
│                  Spring Boot 3+                                  │
│                  Puerto: 8080                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                 ARQUITECTURA HEXAGONAL                    │   │
│  │  ┌─────────┐  ┌─────────────┐  ┌───────────────────┐   │   │
│  │  │ Domain  │◄─│ Application │◄─│  Infrastructure   │   │   │
│  │  │ (POJO)  │  │ (Use Cases) │  │  (Adapters)       │   │   │
│  │  └─────────┘  └─────────────┘  └───────────────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────┘
                          │
          ┌───────────────┴───────────────┐
          ▼                               ▼
┌─────────────────────┐       ┌─────────────────────┐
│    PostgreSQL       │       │ RISK-CENTRAL-MOCK   │
│    Puerto: 5432     │       │    Puerto: 8081     │
│    (Persistencia)   │       │  (Servicio Riesgo)  │
└─────────────────────┘       └─────────────────────┘
```

### Arquitectura Hexagonal

```
┌──────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                       │
│  ┌─────────────────┐           ┌─────────────────────────┐   │
│  │  REST Adapters  │           │  Persistence Adapters   │   │
│  │  (Controllers)  │           │  (JPA Repositories)     │   │
│  └────────┬────────┘           └───────────┬─────────────┘   │
│           │                                 │                 │
│           ▼                                 ▼                 │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                   APPLICATION LAYER                      │ │
│  │  ┌──────────────────┐     ┌──────────────────────────┐  │ │
│  │  │   Input Ports    │     │     Output Ports         │  │ │
│  │  │   (Use Cases)    │     │  (Repository Interfaces) │  │ │
│  │  └────────┬─────────┘     └────────────┬─────────────┘  │ │
│  │           │                            │                 │ │
│  │           ▼                            ▼                 │ │
│  │  ┌─────────────────────────────────────────────────────┐│ │
│  │  │                  DOMAIN LAYER                        ││ │
│  │  │  ┌──────────────┐  ┌────────────┐  ┌─────────────┐  ││ │
│  │  │  │   Entities   │  │   Rules    │  │ Exceptions  │  ││ │
│  │  │  │   (POJOs)    │  │ (Business) │  │  (Domain)   │  ││ │
│  │  │  └──────────────┘  └────────────┘  └─────────────┘  ││ │
│  │  └─────────────────────────────────────────────────────┘│ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

##  Tecnologías

### Backend
- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security** con JWT
- **Spring Data JPA** + Hibernate
- **PostgreSQL** 15
- **Flyway** para migraciones
- **MapStruct** para mapeo de DTOs
- **SpringDoc OpenAPI** (Swagger)
- **Micrometer** + Prometheus
- **WebFlux** (WebClient)

### Frontend
- **Angular 17+**
- **TypeScript**
- **RxJS**
- **NGINX** (producción)

### DevOps & Observabilidad
- **Docker** + Docker Compose
- **Testcontainers**
- **Grafana** 10.2 (Visualización)
- **Prometheus** 2.47 (Métricas)
- **Loki** 2.9 (Logs)
- **Promtail** (Recolector de logs)

##  Requisitos

- Java 17+
- Maven 3.8+
- Node.js 20+
- Docker y Docker Compose
- PostgreSQL 15 (o usar Docker)

##  Instalación

### Clonar el repositorio

```bash
git clone <repository-url>
cd CoopCredit
```

### Configurar variables de entorno

```bash
# Crear archivo .env basado en el ejemplo
cp .env.example .env

# Editar según sea necesario
nano .env
```

Variables principales:
- `DB_NAME`: Nombre de la base de datos (default: coopcredit)
- `DB_USER`: Usuario de PostgreSQL (default: lili)
- `DB_PASS`: Contraseña de PostgreSQL (default: lili123*)
- `JWT_SECRET`: Clave secreta para JWT
- `FRONTEND_PORT`: Puerto del frontend (default: 4200)

##  Ejecución

### Con Docker Compose (Recomendado)

```bash
# Construir y ejecutar todos los servicios
docker-compose up --build

# En segundo plano
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### Ejecución Local (Desarrollo)

#### 1. Base de datos PostgreSQL

```bash
# Usando Docker
docker run -d \
  --name coopcredit-postgres \
  -e POSTGRES_DB=coopcredit \
  -e POSTGRES_USER=lili \
  -e POSTGRES_PASSWORD=lili123* \
  -p 5432:5432 \
  postgres:15-alpine
```

#### 2. Servicio de Riesgo

```bash
cd risk-central-mock-service
mvn spring-boot:run
```

#### 3. Servicio Principal

```bash
cd credit-application-service
mvn spring-boot:run
```

#### 4. Frontend Angular

```bash
cd angular-frontend
npm install
npm start
```

## API Documentation

Una vez ejecutado el servicio principal, acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Endpoints Principales

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| POST | `/api/auth/register` | Registro de usuario | Público |
| POST | `/api/auth/login` | Login y obtención de JWT | Público |
| POST | `/api/affiliates` | Crear afiliado | AFILIADO, ADMIN |
| GET | `/api/affiliates/{id}` | Obtener afiliado | Autenticado |
| POST | `/api/credit-applications` | Crear solicitud | AFILIADO, ADMIN |
| GET | `/api/credit-applications` | Listar solicitudes | ANALISTA, ADMIN |
| POST | `/api/credit-applications/{id}/evaluate` | Evaluar solicitud | ANALISTA, ADMIN |
| POST | `/api/credit-applications/{id}/approve` | Aprobar solicitud | ANALISTA, ADMIN |
| POST | `/api/credit-applications/{id}/reject` | Rechazar solicitud | ANALISTA, ADMIN |

## Estructura del Proyecto

```
CoopCredit/
├── credit-application-service/      # Microservicio principal
│   ├── src/main/java/com/coopcredit/
│   │   ├── domain/                  # Capa de dominio (POJOs)
│   │   │   ├── model/               # Entidades de dominio
│   │   │   └── exception/           # Excepciones de dominio
│   │   ├── application/             # Capa de aplicación
│   │   │   ├── ports/input/         # Puertos de entrada (Use Cases)
│   │   │   ├── ports/output/        # Puertos de salida (Repositorios)
│   │   │   └── usecases/            # Implementaciones
│   │   └── infrastructure/          # Capa de infraestructura
│   │       ├── adapters/input/rest/ # Controladores REST
│   │       ├── adapters/output/     # Adaptadores de persistencia
│   │       └── config/              # Configuraciones
│   └── src/main/resources/
│       └── db/migration/            # Migraciones Flyway
├── risk-central-mock-service/       # Microservicio de riesgo
├── angular-frontend/                # Frontend Angular
├── monitoring/                      # Stack de observabilidad
│   ├── prometheus/                  # Configuración Prometheus
│   ├── loki/                        # Configuración Loki
│   ├── promtail/                    # Configuración Promtail
│   └── grafana/                     # Dashboards y datasources
├── docker-compose.yml               # Orquestación Docker
└── README.md
```

## Seguridad

### Autenticación JWT

1. **Registro**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` → Obtiene token JWT
3. **Uso**: Incluir header `Authorization: Bearer <token>`

### Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| AFILIADO | Usuario estándar | Crear solicitudes propias, ver perfil |
| ANALISTA | Evaluador de créditos | Evaluar, aprobar, rechazar solicitudes |
| ADMIN | Administrador | Acceso completo |

### Ejemplo de uso con cURL

```bash
# 1. Registro
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "password": "password123",
    "email": "usuario@example.com",
    "role": "AFILIADO"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "password": "password123"
  }'

# 3. Usar token en peticiones
curl -X GET http://localhost:8080/api/affiliates/1 \
  -H "Authorization: Bearer <TOKEN>"
```

## Observabilidad

### Stack de Monitoring

El proyecto incluye un stack completo de observabilidad con **Grafana**, **Prometheus** y **Loki**.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        OBSERVABILITY STACK                               │
│                                                                          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                  │
│  │  Prometheus │    │    Loki     │    │  Promtail   │                  │
│  │   :9090     │    │   :3100     │    │  (collector)│                  │
│  │  (Métricas) │    │   (Logs)    │    │             │                  │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘                  │
│         │                  │                   │                         │
│         └─────────────┬────┴───────────────────┘                         │
│                       ▼                                                  │
│              ┌─────────────────┐                                         │
│              │     Grafana     │                                         │
│              │      :3000      │                                         │
│              │ (Visualización) │                                         │
│              └─────────────────┘                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### URLs de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Grafana** | http://localhost:3000 | Dashboards y visualización |
| **Prometheus** | http://localhost:9090 | Métricas y consultas |
| **Loki** | http://localhost:3100 | API de logs |

### Credenciales de Grafana

- **Usuario**: `admin`
- **Contraseña**: `admin123`

### Dashboard Pre-configurado

El proyecto incluye un dashboard de Grafana llamado **"CoopCredit - Observability Dashboard"** que incluye:

#### Métricas en tiempo real:
-  Requests por segundo
-  Latencia p95
-  Evaluaciones de crédito (24h)
-  Tasa de aprobación

#### Gráficos:
-  Requests por endpoint
-  Uso de memoria JVM

#### Logs:
-  Logs de todos los contenedores en tiempo real
-  Volumen de logs por servicio

**Acceso directo al dashboard**: http://localhost:3000/d/coopcredit-main

### Endpoints de Actuator

- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus

### Métricas Personalizadas

| Métrica | Descripción |
|---------|-------------|
| `credit.evaluations.total` | Total de evaluaciones realizadas |
| `credit.approvals.total` | Total de créditos aprobados |
| `credit.rejections.total` | Total de créditos rechazados |
| `auth.login.success` | Logins exitosos |
| `auth.login.failure` | Logins fallidos |
| `risk.service.request.time` | Tiempo de respuesta servicio riesgo |

### Estructura de Archivos de Monitoring

```
monitoring/
├── prometheus/
│   └── prometheus.yml          # Configuración de scraping
├── loki/
│   └── loki-config.yml         # Configuración de Loki
├── promtail/
│   └── promtail-config.yml     # Recolector de logs Docker
└── grafana/
    └── provisioning/
        ├── datasources/
        │   └── datasources.yml # Prometheus + Loki como fuentes
        └── dashboards/
            ├── dashboards.yml
            └── json/
                └── coopcredit-dashboard.json
```

## Panel de Administración

El sistema incluye un panel de administración completo para gestionar usuarios y afiliados.

### Acceso

- **URL**: http://localhost:4200/admin
- **Rol requerido**: ADMIN

### Funcionalidades

| Función | Descripción |
|---------|-------------|
| **Listar Usuarios** | Ver todos los usuarios registrados |
| **Cambiar Roles** | Asignar roles (AFILIADO, ANALISTA, ADMIN) |
| **Listar Afiliados** | Ver perfiles de afiliados registrados |

### Usuarios por Defecto

El sistema crea automáticamente los siguientes usuarios de prueba:

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| `admin` | `admin123` | ADMIN | Administrador del sistema |
| `analista` | `analista123` | ANALISTA | Evaluador de créditos |
| `afiliado` | `afiliado123` | AFILIADO | Usuario estándar |

##  Pruebas

### Ejecutar pruebas unitarias

```bash
cd credit-application-service
mvn test
```

### Ejecutar pruebas de integración

```bash
mvn verify -P integration-tests
```

## Frontend

### URLs de acceso

| URL | Descripción | Rol |
|-----|-------------|-----|
| http://localhost:4200/login | Inicio de sesión | Público |
| http://localhost:4200/register | Registro de usuario | Público |
| http://localhost:4200/credit/list | Listado de solicitudes | Autenticado |
| http://localhost:4200/credit/new | Nueva solicitud | AFILIADO |
| http://localhost:4200/affiliate/new | Perfil de afiliado | AFILIADO |
| http://localhost:4200/admin | Panel de administración | ADMIN |

## Servicios Docker

El proyecto utiliza Docker Compose para orquestar **8 contenedores**:

### Servicios de Aplicación

| Contenedor | Puerto | Descripción |
|------------|--------|-------------|
| `coopcredit-frontend` | 4200 | Frontend Angular con NGINX |
| `coopcredit-main-service` | 8080 | API principal (Spring Boot) |
| `coopcredit-risk-service` | 8081 | Servicio de evaluación de riesgo |
| `coopcredit-postgres` | 5433 | Base de datos PostgreSQL |

### Servicios de Observabilidad

| Contenedor | Puerto | Descripción |
|------------|--------|-------------|
| `coopcredit-grafana` | 3000 | Visualización de métricas y logs |
| `coopcredit-prometheus` | 9090 | Recolección de métricas |
| `coopcredit-loki` | 3100 | Agregación de logs |
| `coopcredit-promtail` | - | Recolector de logs de Docker |

### Comandos Útiles

```bash
# Ver estado de todos los servicios
docker-compose ps

# Ver logs de un servicio específico
docker-compose logs -f credit-application-service

# Reiniciar un servicio
docker-compose restart credit-application-service

# Reconstruir y reiniciar
docker-compose up -d --build credit-application-service

# Detener todo
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

## Placeholders Personalizables

| Placeholder | Descripción | Valor por defecto |
|-------------|-------------|-------------------|
| `DB_NAME` | Nombre de la base de datos | coopcredit |
| `DB_USER` | Usuario de BD | lili |
| `DB_PASS` | Contraseña de BD | lili123* |
| `JWT_SECRET` | Clave secreta JWT | (ver .env) |
| `SERVICE_PORT` | Puerto del backend | 8080 |
| `FRONTEND_PORT` | Puerto del frontend | 4200 |

##  Checklist de Producción

- [ ] Cambiar JWT_SECRET por una clave segura
- [ ] Configurar HTTPS/TLS
- [ ] Ajustar CORS para dominios específicos
- [ ] Configurar rate limiting
- [x] ~~Habilitar logging centralizado~~ Loki + Promtail configurado
- [ ] Configurar backups de base de datos
- [ ] Ajustar límites de memoria JVM
- [x] ~~Configurar monitoreo (Prometheus/Grafana)~~ Stack completo configurado
- [ ] Cambiar credenciales de Grafana en producción

## Licencia

Este proyecto es de uso educativo y demostrativo.

---

**CoopCredit** - Sistema de Gestión de Créditos © 2025

