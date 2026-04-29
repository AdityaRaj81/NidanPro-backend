# Deploying NidanPro-backend to Render

This file documents two simple, supported ways to deploy the backend to Render. Choose one of the options below.

Option A — Recommended: Use Docker runtime (guarantees Maven/JDK)

1. In the Render dashboard, create a new **Web Service** and connect your repo `AdityaRaj81/NidanPro-backend`.
2. Choose **Docker** as the runtime. Render will use the `Dockerfile` in the repository root to build the image.
3. Set environment variables required by the app (at minimum): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and any other secrets your app needs. Optionally set `PORT` (default 8080).
4. Deploy — Render will run the Docker build and start the container. The `Dockerfile` builds with Maven and runs the jar on OpenJDK 25.

Option B — Use Render's native build (no Docker)

1. In the Render dashboard, create a new **Web Service** and connect your repo.
2. Select the **Java** runtime.
3. In **Advanced** -> **Build Command**, set:

```bash
chmod +x mvnw && ./mvnw clean package -DskipTests
```

4. Set the **Start Command** to:

```bash
java -jar target/*.jar
```

5. Add required environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. For testing with the built-in H2 profile, set `SPRING_PROFILES_ACTIVE=local`.

Notes and troubleshooting
- If an existing Render service still shows `mvn: command not found`, edit the service settings and replace the build command as shown above (Render doesn't auto-update an existing service's configured build command when you add repo files).
- For local testing via Docker:

```bash
docker build -t nidanpro-backend:local -f Dockerfile .
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=local nidanpro-backend:local
```

Files added to help deploy:
- `Dockerfile` — multi-stage build (Maven builder + OpenJDK 25 runtime)
- `.dockerignore` — exclude target and local files from image
- `Procfile` — fallback start command used by some platforms
- `README_RENDER.md` — this file
