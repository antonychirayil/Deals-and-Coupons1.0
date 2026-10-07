# Deals & Coupons

[![CI](https://github.com/antonychirayil/Deals-and-Coupons1.0/actions/workflows/ci.yml/badge.svg)](https://github.com/antonychirayil/Deals-and-Coupons1.0/actions/workflows/ci.yml)

A coupon website built as **Java Spring Boot microservices** with an **Angular** frontend.
Anyone can browse coupons from popular Indian online stores; **signing up (free) unlocks the coupon codes**, and
logged-in users can save coupons for later. Admins manage the coupons from a dashboard.

> This is a complete rebuild of a 2021 Capgemini training case study. The original code is kept in
> [`legacy/`](legacy/) and under the git tag `legacy-v1`.

---

## Features

| Who | Can do |
|---|---|
| **Guest** (not logged in) | Browse and search all coupons, filter by category, sort, page through results. Sees store, discount, description and expiry, but **not the code**. |
| **User** (signed up / logged in) | Everything a guest can, plus **reveal and copy coupon codes**, save coupons for later, keep a profile. |
| **Admin** | Everything a user can, plus create, edit and delete coupons from the admin dashboard. |

Coupon codes are hidden **by the backend**, not just by the website: a guest's API responses contain
`"code": null`, so the codes cannot be read from the browser's network tab either.

---

## Architecture

```
                    Browser  (Angular app, http://localhost:4200)
                       │
                       ▼   every request goes through ONE address
         ┌──────────────────────────────┐
         │   gateway-service   :8080    │  routing · login-token check · admin-only rules · CORS · gzip
         └──────┬──────────┬────────┬───┘
   /api/auth/** │ /api/coupons/**   │ /api/users/**
                ▼          ▼        ▼
       auth-service   coupon-service   user-service
          :8082           :8081    ◄──── :8083   (asks coupon-service for coupon details)
            │               │               │
            └───────────────┴───────────────┘
                            ▼
                 MongoDB  (Docker, :27017, database "deals_db")
```

| Service | Port | Responsibility |
|---|---|---|
| `gateway-service` | 8080 | Single entry point. Forwards requests, checks login tokens, allows coupon changes for admins only, CORS for the Angular app. |
| `auth-service` | 8082 | Sign up, log in, issues **JWT** login tokens. Passwords stored as **BCrypt** hashes. Creates the first admin at startup. |
| `coupon-service` | 8081 | Coupons: search, filter, sort, paging, create/edit/delete. Leaves codes out for guests. |
| `user-service` | 8083 | Profiles and saved coupons for the logged-in user. |
| `frontend/deals-web` | 4200 (dev) / 80 (Docker) | The website (Angular + Angular Material). In Docker, **nginx** serves it and forwards `/api/*` to the gateway. |

In Docker, **only the website (port 80) is reachable from your PC**. The gateway and services sit on Docker's
private network, so nobody can call a service directly and skip the gateway's rules.

---

## Tech stack

| Area | Technology |
|---|---|
| Backend | Java 25, Spring Boot 4.1, Spring Security (JWT), Spring Data MongoDB, Spring Cloud Gateway |
| Frontend | Angular 22 (standalone components, signals), Angular Material 22 |
| Database | MongoDB 8 (runs in Docker) |
| Tests | JUnit 5, Mockito, MockMvc, Spring Boot test slices |
| Tools | Maven Wrapper (`mvnw`), Angular CLI, Docker + Docker Compose, nginx, VS Code |

---

## Project structure

```
Deals-and-Coupons1.0/
├── backend/
│   ├── gateway-service/       routes + security rules (config/SecurityConfig.java, application.yml)
│   ├── auth-service/          register / login / JWT
│   ├── coupon-service/        coupons (the most complete example of the layout below)
│   └── user-service/          profiles + saved coupons (client/CouponClient calls coupon-service)
│       Each service:
│       ├── pom.xml            dependencies
│       ├── Dockerfile         how to build this service's Docker image
│       ├── requests.http      ready-made API calls (VS Code REST Client extension)
│       └── src/main/java/com/deals/<service>/
│           ├── controller/    HTTP endpoints only
│           ├── service/       business rules
│           ├── repository/    database access
│           ├── entity/        MongoDB documents
│           ├── dto/           what the API receives and returns (Java records)
│           ├── exception/     custom errors + one global error handler
│           └── config/        security, clock, ...
├── frontend/deals-web/
│   ├── Dockerfile             builds the app with Node, serves it with nginx
│   └── nginx.conf             serves the website, forwards /api/* to the gateway
├── frontend/deals-web/src/
│   ├── app/
│   │   ├── core/              models, services (HTTP calls), interceptor, guards, utils
│   │   ├── shared/            navbar, footer, coupon card, confirm dialog, pipes
│   │   ├── features/          one folder per page: home, coupons, auth, saved-coupons, admin, not-found
│   │   ├── app.routes.ts      URL -> page (most pages lazy-loaded)
│   │   └── app.config.ts      app-wide setup (router, HTTP, Material defaults)
│   ├── environments/          API address
│   ├── material-theme.scss    colours, fonts (Material 3 theme)
│   └── styles.css             global styles
├── scripts/seed-coupons.js    fills MongoDB with ~1000 test coupons
├── .github/workflows/ci.yml   CI pipeline: builds and tests everything on every push (GitHub Actions)
├── docker-compose.yml         MongoDB, or the whole system with --profile app
├── .env.example               template for secrets (copy to .env)
├── .vscode/                   recommended extensions, launch configs
└── legacy/                    the original 2021 code (reference only)
```

---

## Getting started

There are two ways to run the project:

- **A. Everything in Docker** – quickest; one command, nothing else to install except Docker.
- **B. Development mode** – services run from VS Code and the website from `ng serve`, so code changes show up immediately.

Both need the `.env` file from step 2 below.

### A. Everything in Docker

```powershell
Copy-Item .env.example .env          # then fill in your own values (see step 2)
docker compose --profile app up -d --build
```

Open **http://localhost**. The first build takes a few minutes (it downloads Maven, Node and all
libraries); later builds reuse Docker's cache and are much faster.

| Command | What it does |
|---|---|
| `docker compose --profile app ps` | list the containers and their status |
| `docker compose --profile app logs -f gateway-service` | follow one service's log |
| `docker compose --profile app up -d --build` | rebuild and restart after code changes |
| `docker compose --profile app down` | stop everything (data is kept in the `mongo-data` volume) |

Containers run with `-Duser.timezone=Asia/Kolkata` (see each `Dockerfile`), so dates read from MongoDB
match development mode.

### B. Development mode

#### 1. Prerequisites

- **JDK 25** (e.g. Eclipse Temurin) – no separate Maven install needed, each service has `mvnw`
- **Node.js 24** and the **Angular CLI**: `npm install -g @angular/cli`
- **Docker Desktop** (for MongoDB)
- **VS Code** – open the folder and accept the recommended extensions

#### 2. Secrets

```powershell
Copy-Item .env.example .env
```

Open `.env` and set your own values:

| Variable | Used for |
|---|---|
| `MONGO_ROOT_USERNAME`, `MONGO_ROOT_PASSWORD` | MongoDB login (Docker creates this user) |
| `JWT_SECRET` | Signs login tokens. At least 32 characters. Must be the same for every service (they all read this file). |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | The admin account created when auth-service starts |

`.env` is in `.gitignore` – never commit it.

#### 3. Start MongoDB

```powershell
docker compose up -d        # without "--profile app": starts ONLY MongoDB
```

Optional – add ~1000 test coupons (run from the project root):

```powershell
docker cp scripts/seed-coupons.js deals-mongodb:/tmp/seed-coupons.js
docker exec deals-mongodb mongosh -u <MONGO_ROOT_USERNAME> -p <MONGO_ROOT_PASSWORD> --authenticationDatabase admin deals_db --file /tmp/seed-coupons.js
```

#### 4. Start the backend

**In VS Code:** *Run and Debug* panel → choose **"All backend services"** → ▶.
(Or the Spring Boot Dashboard: select all four services → ▶.)

**Or in four terminals:**

```powershell
cd backend\coupon-service;  .\mvnw spring-boot:run
cd backend\auth-service;    .\mvnw spring-boot:run
cd backend\user-service;    .\mvnw spring-boot:run
cd backend\gateway-service; .\mvnw spring-boot:run
```

Each is ready when it prints `Started ...Application`. Health check: http://localhost:8080/actuator/health

> Use one way at a time. Running services from a terminal while VS Code is also building the same
> project can make devtools restart a service halfway through a rebuild.
>
> Both modes use the same MongoDB container, so coupons and accounts created in one show up in the other.

#### 5. Start the website

```powershell
cd frontend\deals-web
npm install        # first time only
ng serve
```

Open **http://localhost:4200**. Log in as admin with the `ADMIN_EMAIL` / `ADMIN_PASSWORD` from `.env`.

---

## API

All calls go through the gateway: `http://localhost:8080` in development mode, `http://localhost/api/...` in Docker. A login token is sent as
`Authorization: Bearer <token>` (the Angular app does this automatically).

| Method | Path | Who | Notes |
|---|---|---|---|
| POST | `/api/auth/register` | anyone | `{ name, email, password }` – creates a USER account |
| POST | `/api/auth/login` | anyone | `{ email, password }` → `{ token, expiresInSeconds, user }` |
| GET | `/api/auth/me` | logged in | the current user |
| GET | `/api/coupons` | anyone | one page of coupons. Query: `search`, `category`, `includeExpired`, `page`, `size` (max 100), `sort` (e.g. `discount,desc`). **`code` is `null` unless logged in.** |
| GET | `/api/coupons/{id}` | anyone | one coupon (same code rule) |
| GET | `/api/coupons/categories` | anyone | all category names, A–Z |
| POST / PUT / DELETE | `/api/coupons`, `/api/coupons/{id}` | **admin** | create / update / delete |
| GET / PUT | `/api/users/me/profile` | logged in | phone and city |
| GET | `/api/users/me/saved-coupons` | logged in | saved coupons, with codes |
| POST / DELETE | `/api/users/me/saved-coupons/{couponId}` | logged in | save / remove |

Errors use the standard `ProblemDetail` JSON: `{ "status": 404, "detail": "Coupon not found with id: ..." }`.
Each service's `requests.http` file has working examples.

---

## Security and data

- **Passwords** are never stored. auth-service saves a **BCrypt hash** (`$2a$10$...`, 60 characters) in the
  `user_accounts` collection; login compares hashes. Emails are unique (database index).
- **Login tokens (JWT)** are signed with `JWT_SECRET`, expire after 60 minutes and contain the user id, name,
  email and role. Every service checks the signature itself – no service needs to call auth-service to do so.
- **Who can do what** is enforced in the gateway (`gateway-service/.../config/SecurityConfig.java`).
  The Angular route guards only hide pages; the server is the real check.
- **Coupon codes for members only:** coupon-service reads the login token on each request and fills in
  `code` only when one is present (`CouponController` → `CouponService` → `CouponResponse.from(..., includeCode)`).
  user-service passes the user's token on when it asks coupon-service for saved coupons.
- **Dates** use one `Clock` in the `Asia/Kolkata` time zone, so coupons expire at midnight India time on any server.
- **In Docker only the website is exposed.** Services have no published ports, so the admin-only rules in the
  gateway can't be bypassed by calling a service directly.

MongoDB database `deals_db`:

| Collection | Owned by | Contents |
|---|---|---|
| `coupons` | coupon-service | code (unique), provider, category, description, discount, expiryDate |
| `user_accounts` | auth-service | name, email (unique), passwordHash, role (USER / ADMIN) |
| `user_profiles` | user-service | phone, city (id = user id) |
| `saved_coupons` | user-service | userId, couponId, savedAt (unique per user + coupon) |

---

## Tests

```powershell
cd backend\coupon-service; .\mvnw test     # repeat for each service
```

- **Unit tests** (service classes with Mockito mocks) – run in milliseconds.
- **Web layer tests** (`@WebMvcTest` + MockMvc) – status codes, JSON, validation, security rules.
- **Database tests** (`@DataMongoTest`) – the real search queries, against a separate `deals_test` database.
- The "context loads" tests and database tests need MongoDB running (`docker compose up -d`).
- **CI:** GitHub Actions ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)) runs all of this automatically on
  every push to `main` and every pull request – the four services' tests (each with a throwaway MongoDB), the
  Angular production build, and all Docker image builds. Results: the repository's **Actions** tab, or the badge above.

---

## What changed – the rebuild, step by step

| Phase | Update |
|---|---|
| 0 – Setup | Old code moved to `legacy/` (tag `legacy-v1`), leaked database passwords removed, `.gitignore`, `.env`, MongoDB in Docker. |
| 1 – coupon-service | First Spring Boot service: controller → service → repository layers, DTOs, validation, global error handling. |
| 2 – Testing | Unit tests with Mockito and web layer tests with MockMvc. |
| 3 – auth-service | Sign up and log in, BCrypt password hashes (the legacy app stored plain numbers), JWT tokens, admin account. |
| 4 – user-service | Profiles and saved coupons (replaces the legacy, unfinished `cart`); first service-to-service call. |
| 5 – API gateway | One entry point (replaces the retired Zuul), role-based rules, CORS in one place (replaces `@CrossOrigin` on every controller). |
| 6 – Angular app | One Angular app (replaces three Angular 12 apps): home page, coupon browsing, signals. |
| 7 – Login & admin | Login/register pages, HTTP interceptor for the token, route guards, saved coupons, admin dashboard. |
| 8 – Scale & redesign | Server-side search and paging (tested with 1000 coupons), batch lookups, gzip, Angular Material redesign, lazy-loaded pages. |
| 8.1 – Fixes | Type-safe query field names, time-zone-aware `Clock` for coupon expiry, VS Code configuration cleanup. |
| 8.2 – Members-only codes | Guests can browse every coupon, but codes are only sent to logged-in users. Guests see "Log in to see code"; after logging in or signing up they return to the page they were on. |
| 9 – Docker | A Dockerfile per service and for the website (nginx), one `docker-compose.yml` for the whole system. Only the website is exposed, closing the "call a service directly" gap. Service addresses are now environment variables with `localhost` defaults, so development mode is unchanged. |
| **10 – CI** | **GitHub Actions pipeline: on every push it builds and tests all four services (with a throwaway MongoDB), builds the Angular app, then builds every Docker image. Fixed the `mvnw` scripts' executable flag, which would have failed on Linux.** |

### Next

- **Continuous delivery:** publish the Docker images to a registry (e.g. GitHub Container Registry) and deploy them
  automatically after CI passes.
- **Google sign-in** (planned in the original project): Google's ID token would be verified by auth-service,
  which then issues the project's own JWT, so no other service needs to change.
