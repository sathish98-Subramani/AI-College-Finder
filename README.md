# AI College Finder & Recommendation System

Discover, search, compare and get personalized recommendations for Indian colleges,
built entirely on a real dataset of Indian institutions — no invented colleges or
fabricated statistics anywhere in the app.

## 1. Architecture

```
Student Browser
      │
      ▼
   Vercel  (React + Vite frontend)
      │  HTTPS REST API
      ▼
Spring Boot backend  (Render / Railway / any Docker host)
      │  JDBC / JPA (SSL)
      ▼
Neon PostgreSQL
      │
      ▼
College dataset (seeded on first boot)

Chat message ──▶ Spring Boot ──▶ retrieve matching colleges from Neon ──▶
                  (optional) AI API, grounded in that data only ──▶ reply
```

The browser never talks to Neon directly — only to the Spring Boot REST API.

## 2. Tech stack

**Frontend**: React 19, Vite, Tailwind CSS v4, React Router, Axios, Recharts,
React Leaflet + OpenStreetMap, Lucide icons — deployed to Vercel.

**Backend**: Java 17, Spring Boot 3.3, Spring Data JPA/Hibernate, Spring Security
+ JWT, springdoc-openapi (Swagger), Apache Commons CSV, Maven — deployable as a
Docker image to Render, Railway, or any container host.

**Database**: PostgreSQL on Neon.

## 3. Dataset mapping — important

The original project brief described a richer schema (branch-level cutoffs, seats,
median package, campus area, lat/long, etc.). The dataset actually supplied
(`dataset/college_dataset.csv` / `.sql`, 50 real colleges) has a different, real
set of columns. **The backend's `College` entity mirrors that dataset exactly —
every field is a real column from the source, nothing was invented to match the
original longer wish-list.** Columns used:

`id, college_name, city, state, institution_type, courses, annual_fee_lakh_approx,
cutoff_exam, cutoff_note, placement_rate_pct_approx, average_package_lpa_approx,
highest_package_approx, rating_demo_out_of_5, hostel, website,
nirf_engineering_rank_2025, data_status, data_reference_year`

Two adaptations, both using only real, public information (not academic/financial
statistics about the colleges themselves):

- **Map coordinates**: the dataset has no latitude/longitude, so the backend
  resolves each college's **city** to a public city-center coordinate
  (`backend/.../util/CityCoordinates.java`) purely to place a marker on the map.
  This is a city location, not an exact campus pin.
- **Recommendation weights**: the brief's course(20%)/branch(15%)/facilities(5%)
  split assumed branch- and facility-level data that isn't in this dataset. The
  scoring engine (`RecommendationServiceImpl`) consolidates this into course
  25% / location 15% / budget 20% / cutoff-and-NIRF-rank fit 15% / placement 15%
  / rating 5% / hostel 5% — every input is a column that actually exists.

## 4. Folder structure

```
ai-college-finder/
├── backend/            Spring Boot API
├── frontend/            React + Vite app
├── dataset/             the real college dataset (csv + sql)
├── docs/
└── README.md
```

Backend package layout: `controller / dto (request,response) / entity / repository /
service (+impl) / security / config / exception / specification / util`.

Frontend layout: `components / pages / services / context / hooks / utils`.

## 5. Database schema

Tables: `users, colleges, favorites, search_history, recommendations`. Indexes on
`college_name, state, city, rating, annual_fee_lakh, placement_rate_pct`, and a
unique constraint on `(user_id, college_id)` for favorites.

`spring.jpa.hibernate.ddl-auto`: **`update`** by default (auto-creates/extends the
schema — convenient for first deploys and a solo/small project), switching to
**`validate`** under the `prod` Spring profile once you've confirmed the schema is
stable, so a bad migration can't silently alter production data. Flip this by
setting `SPRING_PROFILES_ACTIVE=prod` once you're happy with the schema.

## 6. Local development

**Database** — use a free [Neon](https://neon.tech) project for local dev too (no
local Postgres/MySQL required), or point `DATABASE_URL` at any local Postgres.

**Backend**
```bash
cd backend
cp .env.example .env   # fill in DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, JWT_SECRET
# export the variables from .env into your shell, or use an IDE run config
./mvnw spring-boot:run
```
On first startup with an empty `colleges` table, the app automatically seeds the
50-college dataset from `backend/src/main/resources/data/college_dataset.csv`.
Swagger UI: `http://localhost:8080/swagger-ui.html`. Health check: `GET /api/health`.

**Frontend**
```bash
cd frontend
cp .env.example .env   # VITE_API_BASE_URL=http://localhost:8080/api
npm install
npm run dev            # http://localhost:5173
```

## 7. Environment variables

**Backend** (`backend/.env.example`)
```
DATABASE_URL=jdbc:postgresql://<neon-host>/<db>?sslmode=require
DATABASE_USERNAME=
DATABASE_PASSWORD=
JWT_SECRET=              # 32+ random characters
JWT_EXPIRATION_MS=86400000
FRONTEND_URL=https://your-project.vercel.app
AI_API_KEY=               # optional - chatbot works from the DB without it
AI_API_URL=https://api.anthropic.com/v1/messages
AI_MODEL=claude-sonnet-4-5
ADMIN_EMAIL=              # optional - auto-creates an admin account on boot
ADMIN_PASSWORD=
PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

**Frontend** (`frontend/.env.example`)
```
VITE_API_BASE_URL=https://your-backend-domain.com/api
```

## 8. Deployment flow

1. Create a Neon PostgreSQL project → copy the connection string.
2. Deploy `backend/` (Dockerfile included) to Render or Railway as a Docker
   service, or any Docker-compatible host.
3. Set the backend's environment variables (section 7) in that platform's dashboard.
4. Confirm `GET https://<backend-domain>/api/health` returns `{"status":"UP"}` and
   `colleges` auto-seeded (check `GET /api/colleges?size=1`).
5. Deploy `frontend/` to Vercel: import the repo, set the project root to
   `frontend/`, framework preset **Vite**.
6. In Vercel → Project → Settings → Environment Variables, add
   `VITE_API_BASE_URL=https://<backend-domain>/api` for Production (and Preview
   if you want previews hitting the same backend), then redeploy.
7. Back on the backend host, set `FRONTEND_URL` to the real Vercel URL and
   redeploy so CORS allows it.
8. (Optional) Set `ADMIN_EMAIL` / `ADMIN_PASSWORD` and redeploy once to create
   your admin login, then unset `ADMIN_PASSWORD` if you like.
9. Test: register/login, browse & filter colleges, college details + map,
   recommendations, favorites, compare, AI chat, admin dashboard + CSV import.

`vercel.json` in `frontend/` rewrites all paths to `index.html` so client-side
routes (`/dashboard`, `/college/1`, etc.) don't 404 on refresh.

## 9. CORS

The backend only allows `http://localhost:5173` and whatever `FRONTEND_URL` is
set to — credentials are enabled, so a wildcard origin is intentionally not used.

## 10. API overview

`POST /api/auth/register|login|logout` · `GET/PUT /api/users/profile` ·
`GET /api/colleges` (search/filter/paginate/sort) · `GET /api/colleges/{id}` ·
`GET /api/colleges/{id}/similar` · `POST/PUT/DELETE /api/colleges/{id}` (admin) ·
`POST /api/compare` · `POST/GET /api/recommendations` ·
`POST/DELETE /api/favorites/{id}` · `GET /api/favorites` ·
`GET /api/search-history` · `POST /api/ai/chat` ·
`GET /api/admin/dashboard`, `/api/admin/students`, `POST /api/admin/colleges/import`.
Full interactive docs at `/swagger-ui.html`.

## 11. Testing

`cd backend && ./mvnw test` runs H2-backed tests covering registration/login,
college search & filtering, and recommendation scoring (see `src/test/java`).

## 12. Troubleshooting

- **CORS error in the browser** → `FRONTEND_URL` on the backend doesn't match
  the Vercel URL exactly (check `https://` and no trailing slash).
- **`/api/colleges` returns an empty page** → the startup seeder only runs when
  the table is empty; check backend logs for "Seeded N colleges", or POST a CSV
  to `/api/admin/colleges/import` manually.
- **401 on every request** → `JWT_SECRET` differs between the token that was
  issued and the one currently configured (e.g. after redeploy with no
  persisted secret) — sign in again.
- **Map shows nothing for a college** → that college's city isn't in the static
  `CityCoordinates` lookup; add it there.

## 13. Future enhancements

- Branch- and seat-level data, once available, to sharpen the recommendation
  cutoff logic beyond the current NIRF-rank proxy.
- Real per-campus coordinates instead of city-center coordinates.
- Rate limiting and refresh tokens for the JWT flow.
