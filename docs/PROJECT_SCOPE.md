# MangaVault Project Scope

MangaVault is a full-stack online story reading website. The target architecture is a React client communicating with a Spring Boot REST API backed by a relational database.

## User roles

- `GUEST`: browse, search, view story details, and read chapters.
- `USER`: manage a profile, favorites, reading history, comments, and ratings.
- `ADMIN`: manage users, stories, chapters, categories, comments, and basic statistics.

## MVP

1. Story list and search.
2. Story details and chapter list.
3. Chapter reader.
4. Registration and JWT authentication.
5. Admin CRUD for stories and chapters.

## Repository layout

```text
MangaVault/
|-- frontend/             React client (planned)
|-- src/                  Spring Boot backend (current Maven layout)
|-- docs/                 Project and architecture documentation
|-- postman/              API testing collections
|-- AGENTS.md             Engineering scope and conventions
|-- README.md             Learning tasks and current progress
`-- pom.xml               Backend build configuration
```

The backend currently remains at the repository root to preserve the working Maven project. Moving it into a `backend/` directory should be handled as a separate migration together with command and documentation updates.
