# Architecture & Data Model Reference

## Entity relationships

```
User (1) ──< Favorite >── (1) College
User (1) ──< SearchHistory
User (1) ──< Recommendation >── (1) College
```

- `User`: id, fullName, email (unique), password (BCrypt), phone, state, city,
  academicPercentage, entranceExam, entranceScore, preferredCourse,
  preferredBranch, budgetLakh, hostelRequired, role (STUDENT|ADMIN), timestamps.
- `College`: id (preserved from source dataset), collegeName, city, state,
  institutionType, courses, annualFeeLakh, cutoffExam, cutoffNote,
  placementRatePct, averagePackageLpa, highestPackageApprox, rating, hostel,
  website, nirfRank, dataStatus, dataReferenceYear, latitude/longitude
  (city-level, resolved at import time), timestamps.
- `Favorite`: user_id + college_id (unique pair), createdAt.
- `SearchHistory`: user_id, searchQuery, filtersJson, createdAt.
- `Recommendation`: user_id, college_id, score, matchedCriteria,
  unmatchedCriteria, reason, createdAt — stores the last generated ranking per
  user so `GET /api/recommendations` doesn't require recomputation.

## Request flow — AI chatbot (never hallucinates)

```
User question
   │
   ▼
Spring Boot keyword-matches the question against the colleges table
(city, state, course, hostel, budget mentions)
   │
   ▼
Grounded context assembled from those DB rows only
   │
   ▼
If AI_API_KEY set → forwarded to the configured AI API with a system
prompt that forbids inventing any college fact
   │                                   │
   │ (no key / API call fails)         │ (success)
   ▼                                   ▼
Reply built directly from the      Reply phrased by the AI, still
matched DB rows                    grounded only in the same context
```

## Recommendation scoring (see README section 3 for why the weights were adapted)

| Criterion                    | Weight | Field used                          |
|-------------------------------|--------|--------------------------------------|
| Course match                  | 25%    | `courses`                            |
| Location match                | 15%    | `state`, `city`                      |
| Budget fit                    | 20%    | `annualFeeLakh`                      |
| Cutoff / difficulty fit       | 15%    | `nirfRank` (proxy) vs entrance percentile |
| Placement rate                | 15%    | `placementRatePct`                   |
| Rating                        | 5%     | `rating`                             |
| Hostel                        | 5%     | `hostel`                             |

## Deployment-ready checklist

- [ ] Neon database created
- [ ] `DATABASE_URL` / username / password working from the backend
- [ ] Dataset seeded (automatic on first boot, or via CSV import)
- [ ] Backend deployed (Docker) and `/api/health` returns UP
- [ ] Swagger reachable at `/swagger-ui.html`
- [ ] JWT auth working (register → login → `/api/users/profile`)
- [ ] Frontend deployed to Vercel
- [ ] `VITE_API_BASE_URL` set in Vercel
- [ ] `FRONTEND_URL` set on the backend to the real Vercel domain
- [ ] CORS confirmed (no browser console errors)
- [ ] Search, recommendations, favorites, compare, AI chat, admin dashboard all
      verified against the deployed URLs
- [ ] Mobile responsiveness spot-checked
- [ ] No `.env` files committed to GitHub
