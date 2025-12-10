# CoopCredit - Diagramas del Sistema

Este documento contiene los diagramas del sistema CoopCredit utilizando Mermaid.

## 📊 Índice

1. [Diagrama de Arquitectura General](#1-diagrama-de-arquitectura-general)
2. [Diagrama de Arquitectura Hexagonal](#2-diagrama-de-arquitectura-hexagonal)
3. [Diagrama de Casos de Uso](#3-diagrama-de-casos-de-uso)
4. [Diagrama de Relación entre Microservicios](#4-diagrama-de-relación-entre-microservicios)
5. [Diagrama de Flujo de Solicitud de Crédito](#5-diagrama-de-flujo-de-solicitud-de-crédito)
6. [Diagrama de Componentes del Stack de Observabilidad](#6-diagrama-de-componentes-del-stack-de-observabilidad)

---

## 1. Diagrama de Arquitectura General

```mermaid
flowchart TB
    subgraph Cliente["🖥️ Cliente"]
        Browser["Navegador Web"]
    end

    subgraph Frontend["📱 Frontend - Angular 17+"]
        Angular["Angular SPA<br/>Puerto: 4200"]
        NGINX["NGINX<br/>Reverse Proxy"]
    end

    subgraph Backend["⚙️ Backend - Spring Boot 3+"]
        API["Credit Application Service<br/>Puerto: 8080"]
        
        subgraph Hexagonal["Arquitectura Hexagonal"]
            Domain["🔷 Domain Layer<br/>(Entities, Rules)"]
            Application["🔶 Application Layer<br/>(Use Cases, Ports)"]
            Infrastructure["🔹 Infrastructure Layer<br/>(Adapters, Config)"]
        end
    end

    subgraph ExternalServices["🌐 Servicios Externos"]
        RiskService["Risk Central Mock Service<br/>Puerto: 8081"]
    end

    subgraph Database["🗄️ Base de Datos"]
        PostgreSQL[("PostgreSQL 15<br/>Puerto: 5433")]
    end

    subgraph Observability["📈 Stack de Observabilidad"]
        Grafana["Grafana<br/>Puerto: 3000"]
        Prometheus["Prometheus<br/>Puerto: 9090"]
        Loki["Loki<br/>Puerto: 3100"]
        Promtail["Promtail"]
    end

    Browser --> Angular
    Angular --> NGINX
    NGINX --> API
    API --> Domain
    Domain --> Application
    Application --> Infrastructure
    Infrastructure --> PostgreSQL
    Infrastructure --> RiskService
    
    API --> Prometheus
    Promtail --> Loki
    Prometheus --> Grafana
    Loki --> Grafana

    classDef frontend fill:#42a5f5,stroke:#1976d2,color:white
    classDef backend fill:#66bb6a,stroke:#388e3c,color:white
    classDef database fill:#ffa726,stroke:#f57c00,color:white
    classDef external fill:#ab47bc,stroke:#7b1fa2,color:white
    classDef monitoring fill:#26a69a,stroke:#00897b,color:white

    class Angular,NGINX frontend
    class API,Domain,Application,Infrastructure backend
    class PostgreSQL database
    class RiskService external
    class Grafana,Prometheus,Loki,Promtail monitoring
```

---

## 2. Diagrama de Arquitectura Hexagonal

```mermaid
flowchart TB
    subgraph External["🌐 Mundo Externo"]
        REST["REST API Clients"]
        DB[("PostgreSQL")]
        RiskAPI["Risk Service API"]
    end

    subgraph Infrastructure["🔹 INFRASTRUCTURE LAYER"]
        subgraph InputAdapters["Adaptadores de Entrada"]
            AuthController["AuthController"]
            AffiliateController["AffiliateController"]
            CreditAppController["CreditApplicationController"]
        end
        
        subgraph OutputAdapters["Adaptadores de Salida"]
            UserPersistence["UserPersistenceAdapter"]
            AffiliatePersistence["AffiliatePersistenceAdapter"]
            CreditPersistence["CreditApplicationPersistenceAdapter"]
            RiskServiceAdapter["RiskServiceAdapter"]
        end
        
        subgraph Config["Configuración"]
            Security["SecurityConfig<br/>+ JWT"]
            Swagger["OpenApiConfig"]
            GlobalHandler["GlobalExceptionHandler"]
        end
    end

    subgraph Application["🔶 APPLICATION LAYER"]
        subgraph InputPorts["Puertos de Entrada (Use Cases)"]
            AuthUseCase["AuthenticationUseCase"]
            RegisterAffiliateUC["RegisterAffiliateUseCase"]
            RegisterCreditUC["RegisterCreditApplicationUseCase"]
            EvaluateCreditUC["EvaluateCreditApplicationUseCase"]
        end
        
        subgraph Services["Servicios"]
            AuthService["AuthenticationService"]
            AffiliateService["RegisterAffiliateService"]
            CreditService["RegisterCreditApplicationService"]
            EvaluateService["EvaluateCreditApplicationService"]
        end
        
        subgraph OutputPorts["Puertos de Salida"]
            UserRepoPort["UserRepositoryPort"]
            AffiliateRepoPort["AffiliateRepositoryPort"]
            CreditRepoPort["CreditApplicationRepositoryPort"]
            RiskServicePort["RiskServicePort"]
        end
    end

    subgraph Domain["🔷 DOMAIN LAYER"]
        subgraph Models["Entidades de Dominio"]
            User["User"]
            Affiliate["Affiliate"]
            CreditApp["CreditApplication"]
            RiskEval["RiskEvaluation"]
        end
        
        subgraph Enums["Enumeraciones"]
            Role["Role<br/>(AFILIADO, ANALISTA, ADMIN)"]
            Status["CreditApplicationStatus<br/>(PENDIENTE, APROBADO, RECHAZADO)"]
            RiskLevel["RiskLevel<br/>(BAJO, MEDIO, ALTO)"]
        end
        
        subgraph Exceptions["Excepciones de Dominio"]
            DomainEx["DomainException"]
            NotFoundEx["AffiliateNotFoundException<br/>CreditApplicationNotFoundException<br/>UserNotFoundException"]
            InvalidOpEx["InvalidOperationException"]
        end
    end

    REST --> InputAdapters
    InputAdapters --> InputPorts
    InputPorts --> Services
    Services --> OutputPorts
    OutputPorts --> OutputAdapters
    OutputAdapters --> DB
    OutputAdapters --> RiskAPI
    
    Services --> Models
    Models --> Enums

    classDef infra fill:#90caf9,stroke:#1565c0,color:#0d47a1
    classDef app fill:#ffcc80,stroke:#ef6c00,color:#e65100
    classDef domain fill:#a5d6a7,stroke:#2e7d32,color:#1b5e20

    class AuthController,AffiliateController,CreditAppController,UserPersistence,AffiliatePersistence,CreditPersistence,RiskServiceAdapter,Security,Swagger,GlobalHandler infra
    class AuthUseCase,RegisterAffiliateUC,RegisterCreditUC,EvaluateCreditUC,AuthService,AffiliateService,CreditService,EvaluateService,UserRepoPort,AffiliateRepoPort,CreditRepoPort,RiskServicePort app
    class User,Affiliate,CreditApp,RiskEval,Role,Status,RiskLevel,DomainEx,NotFoundEx,InvalidOpEx domain
```

---

## 3. Diagrama de Casos de Uso

```mermaid
flowchart LR
    subgraph Actors["👥 Actores"]
        Public["🌐 Usuario Público"]
        Afiliado["👤 AFILIADO"]
        Analista["📊 ANALISTA"]
        Admin["👑 ADMIN"]
    end

    subgraph Auth["🔐 Autenticación"]
        UC1["Registrar Usuario"]
        UC2["Iniciar Sesión"]
        UC3["Cerrar Sesión"]
    end

    subgraph AffiliateUC["👥 Gestión de Afiliados"]
        UC4["Crear Perfil de Afiliado"]
        UC5["Consultar Perfil de Afiliado"]
        UC6["Actualizar Datos de Afiliado"]
    end

    subgraph CreditUC["💳 Solicitudes de Crédito"]
        UC7["Crear Solicitud de Crédito"]
        UC8["Consultar Solicitud de Crédito"]
        UC9["Listar Solicitudes de Crédito"]
        UC10["Evaluar Solicitud de Crédito"]
        UC11["Aprobar Solicitud de Crédito"]
        UC12["Rechazar Solicitud de Crédito"]
    end

    subgraph AdminUC["⚙️ Administración"]
        UC13["Listar Usuarios"]
        UC14["Cambiar Rol de Usuario"]
        UC15["Listar Afiliados"]
        UC16["Gestionar Sistema"]
    end

    subgraph RiskUC["🎯 Evaluación de Riesgo"]
        UC17["Consultar Evaluación de Riesgo"]
    end

    %% Relaciones Usuario Público
    Public --> UC1
    Public --> UC2

    %% Relaciones Afiliado
    Afiliado --> UC3
    Afiliado --> UC4
    Afiliado --> UC5
    Afiliado --> UC7
    Afiliado --> UC8

    %% Relaciones Analista
    Analista --> UC3
    Analista --> UC9
    Analista --> UC10
    Analista --> UC11
    Analista --> UC12
    Analista --> UC5

    %% Relaciones Admin
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
    Admin --> UC15
    Admin --> UC16

    %% Include/Extend
    UC10 -.->|include| UC17
    UC11 -.->|extend| UC10
    UC12 -.->|extend| UC10

    classDef actor fill:#fff9c4,stroke:#f9a825,color:#f57f17
    classDef auth fill:#e1bee7,stroke:#8e24aa,color:#4a148c
    classDef affiliate fill:#b2dfdb,stroke:#00897b,color:#004d40
    classDef credit fill:#bbdefb,stroke:#1976d2,color:#0d47a1
    classDef admin fill:#ffcdd2,stroke:#e53935,color:#b71c1c
    classDef risk fill:#ffe0b2,stroke:#fb8c00,color:#e65100

    class Public,Afiliado,Analista,Admin actor
    class UC1,UC2,UC3 auth
    class UC4,UC5,UC6 affiliate
    class UC7,UC8,UC9,UC10,UC11,UC12 credit
    class UC13,UC14,UC15,UC16 admin
    class UC17 risk
```

---

## 4. Diagrama de Relación entre Microservicios

```mermaid
flowchart TB
    subgraph Docker["🐳 Docker Compose Network: coopcredit-network"]
        
        subgraph FrontendContainer["📱 Frontend Container"]
            FE["coopcredit-frontend<br/>Angular + NGINX<br/>Puerto: 4200 → 80"]
        end
        
        subgraph MainServiceContainer["⚙️ Main Service Container"]
            MS["coopcredit-main-service<br/>Spring Boot 3+<br/>Puerto: 8080"]
            
            subgraph Endpoints["API Endpoints"]
                E1["/api/auth/**"]
                E2["/api/affiliates/**"]
                E3["/api/credit-applications/**"]
                E4["/actuator/**"]
            end
        end
        
        subgraph RiskServiceContainer["🎯 Risk Service Container"]
            RS["coopcredit-risk-service<br/>Spring Boot 3+<br/>Puerto: 8081"]
            
            subgraph RiskEndpoints["Risk API"]
                RE1["POST /api/risk/evaluate"]
            end
        end
        
        subgraph DatabaseContainer["🗄️ Database Container"]
            DB[("coopcredit-postgres<br/>PostgreSQL 15<br/>Puerto: 5433 → 5432")]
        end
        
        subgraph ObservabilityStack["📈 Observability Stack"]
            PROM["coopcredit-prometheus<br/>Puerto: 9090"]
            LOKI["coopcredit-loki<br/>Puerto: 3100"]
            PTAIL["coopcredit-promtail"]
            GRAF["coopcredit-grafana<br/>Puerto: 3000"]
        end
    end

    %% Flujo de datos principal
    FE -->|"HTTP/REST<br/>API Calls"| MS
    MS -->|"HTTP/REST<br/>Risk Evaluation"| RS
    MS -->|"JDBC<br/>Persistence"| DB
    
    %% Dependencias de inicio
    MS -.->|"depends_on<br/>service_healthy"| DB
    MS -.->|"depends_on<br/>service_healthy"| RS
    FE -.->|"depends_on<br/>service_healthy"| MS
    
    %% Observabilidad
    MS -->|"Metrics<br/>/actuator/prometheus"| PROM
    RS -->|"Metrics<br/>/actuator/prometheus"| PROM
    
    PTAIL -->|"Docker Logs"| LOKI
    
    PROM -->|"Datasource"| GRAF
    LOKI -->|"Datasource"| GRAF
    PTAIL -.->|"depends_on"| LOKI
    GRAF -.->|"depends_on"| PROM
    GRAF -.->|"depends_on"| LOKI

    classDef frontend fill:#42a5f5,stroke:#1976d2,color:white
    classDef backend fill:#66bb6a,stroke:#388e3c,color:white
    classDef risk fill:#ab47bc,stroke:#7b1fa2,color:white
    classDef database fill:#ffa726,stroke:#f57c00,color:white
    classDef monitoring fill:#26a69a,stroke:#00897b,color:white

    class FE frontend
    class MS,E1,E2,E3,E4 backend
    class RS,RE1 risk
    class DB database
    class PROM,LOKI,PTAIL,GRAF monitoring
```

---

## 5. Diagrama de Flujo de Solicitud de Crédito

```mermaid
sequenceDiagram
    autonumber
    
    participant U as 👤 Usuario (Afiliado)
    participant FE as 📱 Angular Frontend
    participant API as ⚙️ Credit Service<br/>(8080)
    participant RS as 🎯 Risk Service<br/>(8081)
    participant DB as 🗄️ PostgreSQL
    
    Note over U,DB: 🔐 FLUJO DE AUTENTICACIÓN
    
    U->>FE: Ingresa credenciales
    FE->>API: POST /api/auth/login
    API->>DB: Validar usuario
    DB-->>API: Usuario válido
    API-->>FE: JWT Token
    FE-->>U: Sesión iniciada
    
    Note over U,DB: 👥 CREAR PERFIL DE AFILIADO
    
    U->>FE: Completa formulario afiliado
    FE->>API: POST /api/affiliates<br/>[Authorization: Bearer JWT]
    API->>DB: Guardar afiliado
    DB-->>API: Afiliado creado
    API-->>FE: AffiliateResponse
    FE-->>U: Perfil creado exitosamente
    
    Note over U,DB: 💳 SOLICITAR CRÉDITO
    
    U->>FE: Completa solicitud de crédito
    FE->>API: POST /api/credit-applications<br/>[Authorization: Bearer JWT]
    API->>DB: Guardar solicitud (PENDIENTE)
    DB-->>API: Solicitud guardada
    API-->>FE: CreditApplicationResponse
    FE-->>U: Solicitud creada (Pendiente evaluación)
    
    Note over U,DB: 📊 EVALUACIÓN POR ANALISTA
    
    participant A as 📊 Analista
    
    A->>FE: Solicita evaluación
    FE->>API: POST /api/credit-applications/{id}/evaluate
    
    API->>RS: POST /api/risk/evaluate<br/>{identificacion, monto, plazo, ingresos}
    
    Note over RS: Evaluación de Riesgo:<br/>- Score crediticio<br/>- Nivel de riesgo<br/>- Recomendación
    
    RS-->>API: RiskEvaluationResponse<br/>{score, riskLevel, approved}
    
    API->>DB: Guardar evaluación de riesgo
    DB-->>API: Evaluación guardada
    
    alt Riesgo BAJO (Score >= 700)
        API->>DB: Actualizar estado → EVALUADO
        API-->>FE: Recomendación: APROBAR
        FE-->>A: Solicitud evaluada - Riesgo Bajo
        
        A->>FE: Aprobar crédito
        FE->>API: POST /api/credit-applications/{id}/approve
        API->>DB: Actualizar estado → APROBADO
        API-->>FE: Crédito aprobado
        FE-->>A: ✅ Crédito aprobado exitosamente
        
    else Riesgo ALTO (Score < 500)
        API->>DB: Actualizar estado → EVALUADO
        API-->>FE: Recomendación: RECHAZAR
        FE-->>A: Solicitud evaluada - Riesgo Alto
        
        A->>FE: Rechazar crédito
        FE->>API: POST /api/credit-applications/{id}/reject
        API->>DB: Actualizar estado → RECHAZADO
        API-->>FE: Crédito rechazado
        FE-->>A: ❌ Crédito rechazado
        
    else Riesgo MEDIO (500 <= Score < 700)
        API->>DB: Actualizar estado → EVALUADO
        API-->>FE: Recomendación: REVISAR MANUALMENTE
        FE-->>A: Solicitud evaluada - Requiere revisión
        
        Note over A: Decisión manual del analista
    end
```

---

## 6. Diagrama de Componentes del Stack de Observabilidad

```mermaid
flowchart TB
    subgraph Applications["🖥️ Aplicaciones"]
        MS["Credit Application Service<br/>:8080"]
        RS["Risk Central Service<br/>:8081"]
        FE["Angular Frontend<br/>:4200"]
    end

    subgraph DockerEngine["🐳 Docker Engine"]
        DL["Docker Logs<br/>/var/lib/docker/containers"]
        DS["Docker Socket<br/>/var/run/docker.sock"]
    end

    subgraph MetricsCollection["📊 Recolección de Métricas"]
        subgraph Prometheus["Prometheus :9090"]
            PM["Prometheus Metrics"]
            SC["Scrape Config"]
            TSDB["Time Series DB"]
        end
        
        Actuator1["/actuator/prometheus"]
        Actuator2["/actuator/prometheus"]
    end

    subgraph LogsCollection["📝 Recolección de Logs"]
        subgraph Promtail["Promtail"]
            PT["Log Collector"]
            LC["Label Config"]
        end
        
        subgraph Loki["Loki :3100"]
            LK["Log Aggregator"]
            LS["Log Storage"]
        end
    end

    subgraph Visualization["📈 Visualización"]
        subgraph Grafana["Grafana :3000"]
            DS1["Prometheus<br/>Datasource"]
            DS2["Loki<br/>Datasource"]
            
            subgraph Dashboards["Dashboards"]
                D1["📊 Métricas de Aplicación"]
                D2["📝 Logs en Tiempo Real"]
                D3["🎯 Tasa de Aprobación"]
                D4["⚡ Latencia de APIs"]
            end
        end
    end

    subgraph CustomMetrics["📐 Métricas Personalizadas"]
        M1["credit.evaluations.total"]
        M2["credit.approvals.total"]
        M3["credit.rejections.total"]
        M4["auth.login.success"]
        M5["auth.login.failure"]
        M6["risk.service.request.time"]
    end

    %% Flujo de métricas
    MS --> Actuator1
    RS --> Actuator2
    Actuator1 --> SC
    Actuator2 --> SC
    SC --> PM
    PM --> TSDB
    
    %% Flujo de logs
    MS --> DL
    RS --> DL
    FE --> DL
    DL --> PT
    DS --> PT
    PT --> LC
    LC --> LK
    LK --> LS
    
    %% Visualización
    TSDB --> DS1
    LS --> DS2
    DS1 --> D1
    DS1 --> D3
    DS1 --> D4
    DS2 --> D2
    
    %% Métricas personalizadas
    MS --> M1
    MS --> M2
    MS --> M3
    MS --> M4
    MS --> M5
    MS --> M6
    M1 --> Actuator1
    M2 --> Actuator1
    M3 --> Actuator1
    M4 --> Actuator1
    M5 --> Actuator1
    M6 --> Actuator1

    classDef app fill:#42a5f5,stroke:#1976d2,color:white
    classDef docker fill:#2196f3,stroke:#0d47a1,color:white
    classDef metrics fill:#66bb6a,stroke:#388e3c,color:white
    classDef logs fill:#ffa726,stroke:#f57c00,color:white
    classDef viz fill:#ab47bc,stroke:#7b1fa2,color:white
    classDef custom fill:#26a69a,stroke:#00897b,color:white

    class MS,RS,FE app
    class DL,DS docker
    class PM,SC,TSDB,Actuator1,Actuator2 metrics
    class PT,LC,LK,LS logs
    class DS1,DS2,D1,D2,D3,D4 viz
    class M1,M2,M3,M4,M5,M6 custom
```

---

## 7. Diagrama de Entidad-Relación (Base de Datos)

```mermaid
erDiagram
    USERS {
        bigint id PK
        varchar username UK
        varchar password
        varchar email UK
        varchar role
        timestamp created_at
        timestamp updated_at
    }
    
    AFFILIATES {
        bigint id PK
        bigint user_id FK
        varchar identification UK
        varchar first_name
        varchar last_name
        varchar phone
        varchar address
        decimal monthly_income
        varchar employment_type
        timestamp created_at
        timestamp updated_at
    }
    
    CREDIT_APPLICATIONS {
        bigint id PK
        bigint affiliate_id FK
        decimal requested_amount
        integer term_months
        decimal interest_rate
        varchar purpose
        varchar status
        timestamp application_date
        timestamp decision_date
        varchar decision_comments
        timestamp created_at
        timestamp updated_at
    }
    
    RISK_EVALUATIONS {
        bigint id PK
        bigint credit_application_id FK
        integer credit_score
        varchar risk_level
        boolean approved
        varchar evaluation_details
        timestamp evaluated_at
    }
    
    USERS ||--o| AFFILIATES : "has"
    AFFILIATES ||--o{ CREDIT_APPLICATIONS : "submits"
    CREDIT_APPLICATIONS ||--o| RISK_EVALUATIONS : "has"
```

---

## 8. Diagrama de Despliegue

```mermaid
flowchart TB
    subgraph Internet["☁️ Internet"]
        Client["🌐 Cliente Web"]
    end
    
    subgraph DockerHost["🖥️ Docker Host"]
        subgraph Network["🔗 coopcredit-network (bridge)"]
            
            subgraph FrontendTier["Frontend Tier"]
                NGINX["📱 NGINX Container<br/>coopcredit-frontend<br/>4200:80"]
            end
            
            subgraph ApplicationTier["Application Tier"]
                MainApp["⚙️ Spring Boot Container<br/>coopcredit-main-service<br/>8080:8080<br/>━━━━━━━━━━━━━━<br/>Java 17 + Maven<br/>Spring Security + JWT<br/>Spring Data JPA"]
                
                RiskApp["🎯 Spring Boot Container<br/>coopcredit-risk-service<br/>8081:8081<br/>━━━━━━━━━━━━━━<br/>Java 17 + Maven<br/>Mock Risk Evaluation"]
            end
            
            subgraph DataTier["Data Tier"]
                PG[("🗄️ PostgreSQL Container<br/>coopcredit-postgres<br/>5433:5432<br/>━━━━━━━━━━━━━━<br/>postgres_data volume")]
            end
            
            subgraph MonitoringTier["Monitoring Tier"]
                Prom["📊 Prometheus Container<br/>coopcredit-prometheus<br/>9090:9090"]
                Lok["📝 Loki Container<br/>coopcredit-loki<br/>3100:3100"]
                Ptail["📋 Promtail Container<br/>coopcredit-promtail"]
                Graf["📈 Grafana Container<br/>coopcredit-grafana<br/>3000:3000"]
            end
        end
        
        subgraph Volumes["💾 Docker Volumes"]
            V1["postgres_data"]
            V2["prometheus_data"]
            V3["loki_data"]
            V4["grafana_data"]
        end
    end
    
    Client --> NGINX
    NGINX --> MainApp
    MainApp --> RiskApp
    MainApp --> PG
    
    MainApp -.-> Prom
    RiskApp -.-> Prom
    
    Ptail -.-> Lok
    Prom --> Graf
    Lok --> Graf
    
    PG --- V1
    Prom --- V2
    Lok --- V3
    Graf --- V4

    classDef internet fill:#e3f2fd,stroke:#1976d2,color:#0d47a1
    classDef frontend fill:#42a5f5,stroke:#1976d2,color:white
    classDef app fill:#66bb6a,stroke:#388e3c,color:white
    classDef risk fill:#ab47bc,stroke:#7b1fa2,color:white
    classDef data fill:#ffa726,stroke:#f57c00,color:white
    classDef monitoring fill:#26a69a,stroke:#00897b,color:white
    classDef volume fill:#90a4ae,stroke:#546e7a,color:white

    class Client internet
    class NGINX frontend
    class MainApp app
    class RiskApp risk
    class PG data
    class Prom,Lok,Ptail,Graf monitoring
    class V1,V2,V3,V4 volume
```

---

## 📖 Cómo Visualizar estos Diagramas

### Opción 1: GitHub/GitLab
Simplemente sube este archivo a tu repositorio. GitHub y GitLab renderizan automáticamente los diagramas Mermaid.

### Opción 2: VS Code
Instala la extensión "Markdown Preview Mermaid Support" para visualizar los diagramas directamente en VS Code.

### Opción 3: Mermaid Live Editor
Visita [mermaid.live](https://mermaid.live) y pega el código de cualquier diagrama para editarlo y exportarlo.

### Opción 4: Documentación
Herramientas como Docusaurus, MkDocs o Confluence soportan Mermaid de forma nativa o mediante plugins.

---

**CoopCredit** - Sistema de Gestión de Créditos © 2025
