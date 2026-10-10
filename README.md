# Football Transfer

Ebben a projektben a Football Transfer feladat megoldásom található. Spring Boot keretrendszert és Java nyelvet használtam, PostgreSQL adatbázissal és Flyway migrációval.

## Előfeltételek

- Java 21
- Docker (Docker Desktop)

Gradle telepítése nem szükséges, mert a projekt a Gradle wrappert használja.

## Indítás

1. Indítsd el a Docker Desktopot.
2. A `development` könyvtárban indítsd el az adatbázist:
```
   docker compose up -d
```
3. Indítsd el az alkalmazást a `dev` profillal:
    - IntelliJ-ben: *Run/Debug Configurations* → `BackendTestApplication` → *Active profiles*: `dev`
    - Terminálból:
```
     ./gradlew bootRun --args="--spring.profiles.active=dev"
```

Indítás után az alkalmazás a `http://localhost:8080` címen érhető el.

> **Megjegyzés:** az adatbázis-konténer a gépen nem az alapértelmezett 5432-es, hanem az **5433**-as porton érhető el, hogy ne ütközzön egy esetleg helyben futó PostgreSQL-lel.

## Konfiguráció

- **`application.yml` (alap konfiguráció):** az általános JPA/Hibernate és Flyway beállítások, az explicit szerverport és a logolási beállítások.
- **`application-dev.yml` (dev profil):** az adatbázis-kapcsolat, valamint a fejlesztéshez szükséges JPA- és logolási beállítások.

Dev profil nélkül, a kapcsolati adatok külön megadása nélkül az alkalmazás nem indul el.

## Eltérések a kapott projekthez képest

### Gradle wrapper

Az eredeti repó Gradle 9.2.1-es wrappert tartalmazott, ami nem kompatibilis a projektben használt Spring Boot 3.2.0 Gradle pluginjával: a `bootJar` taszk olyan Gradle API-t használ, amit a 9.0-s verzió eltávolított. Emellett a `.gitignore`-t hozzáadó commit a wrapper fájljait kivette a verziókezelésből. A wrappert 8.14.3-as verzióval állítottam vissza, ami a Spring Boot 3.2-vel kompatibilis legutolsó 8-as Gradle-verzió.

### docker-compose port

A `docker-compose.yml` fájlban a port-kivezetés kikommentezve érkezett, ezért a konténer elindult, de a gépről nem volt elérhető. A kivezetést visszakapcsoltam, az 5433-as gépoldali porttal.

### .gitignore

A `.gitignore` fájl módosításra került, mert a szintaxisa nem működött.

## Architektúrális, implementációs döntések

*(folyamatosan bővül)*

## API-dokumentáció (Postman collection)

*(folyamatosan bővül)*