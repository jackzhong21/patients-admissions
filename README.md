# Patient Admissions Management System

A full-stack application for managing hospital patient admission records, supporting both regular and external (third-party system) admissions.

## Tech Stack

- **Backend**: Spring Boot 3 + Kotlin + PostgreSQL + Liquibase + SpringDoc (Swagger)
- **Frontend**: Next.js 14 (App Router) + TypeScript + Material UI v6 + Zustand
- **Testing**: JUnit 5 + Mockito (backend), Jest + RTL (frontend unit), Playwright (E2E)
- **Infrastructure**: Docker Compose (PostgreSQL)

## Prerequisites

- Java 21
- Node.js 18+
- Docker & Docker Compose
- Gradle 8.6

## Demo - Patient Admissions (Click to open)
[![Watch the video](https://i9.ytimg.com/vi_webp/CX79CegJ2MQ/maxresdefault.webp?v=69b8a423&sqp=CIzI4s0G&rs=AOn4CLCfSvEx5ADjHdT3HTFx5xxZUMswxQ)](https://youtu.be/CX79CegJ2MQ)

## Quick Start

### 1. Start PostgreSQL

```bash
docker compose up -d
```

### 2. Bootstrap Gradle wrapper (first time only)

The `gradlew` script requires `gradle/wrapper/gradle-wrapper.jar`. If you have Gradle installed:

```bash
cd backend
gradle wrapper --gradle-version 8.6
```

### 3. Run the backend

```bash
cd backend
./gradlew bootRun
```

Liquibase will automatically run the migration on first start. The API will be available at `http://localhost:8080`.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

### 4. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The app will be available at http://localhost:3000 and will redirect to `/admissions`.

## Features

- **Paginated list** of all admissions with sorting by date
- **Create** regular admissions (name, birthday, sex, category)
- **Create** external admissions (includes `externalSystemId` from third-party systems)
- **Edit** regular admissions (all 4 fields editable)
- **Edit** external admissions (only name, birthday, sex — category and externalSystemId are read-only)
- **Delete** admissions with confirmation dialog
- **Validation**: birthday cannot be in the future; all required fields enforced on both frontend and backend

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/admissions?page=0&size=20 | Paginated list |
| POST | /api/admissions | Create regular admission |
| PUT | /api/admissions/{id} | Update regular admission |
| DELETE | /api/admissions/{id} | Delete admission |
| POST | /api/admissions/external | Create external admission |
| PUT | /api/admissions/external/{id} | Update external admission |

## Testing

### Backend unit tests

```bash
cd backend
./gradlew test
```

### Frontend unit tests

```bash
cd frontend
npm test
```

### E2E tests (Playwright)

Requires the backend and frontend to be running:

```bash
cd frontend
npx playwright test
```

## Project Structure

```
├── backend/          Spring Boot application
├── frontend/         Next.js application
│   ├── e2e/          Playwright E2E tests
├── docker-compose.yml
└── README.md
```

## Environment Variables

### Backend (`backend/src/main/resources/application.yml`)

| Key | Default | Description |
|-----|---------|-------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/admissions` | PostgreSQL URL |
| `spring.datasource.username` | `admissions` | Database user |
| `spring.datasource.password` | `admissions` | Database password |

### Frontend (`.env.local`)

| Key | Default | Description |
|-----|---------|-------------|
| `NEXT_PUBLIC_API_BASE_URL` | `http://localhost:8080` | Backend API URL |


### SQL to genereate 1000 test data
```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.admissions
(
id,
"name",
birthday,
sex,
category,
date_of_admission,
external_system_id,
created_at,
updated_at
)
SELECT
gen_random_uuid(),
'Patient ' || gs,
CURRENT_DATE - (floor(random() * 36500))::int,
(ARRAY['FEMALE','MALE','INTERSEX','UNKNOWN'])[floor(random()*4 + 1)],
(ARRAY['NORMAL','INPATIENT','EMERGENCY','OUTPATIENT'])[floor(random()*4 + 1)],
CURRENT_TIMESTAMP,
NULL,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP
FROM generate_series(1,1000) gs;
```
