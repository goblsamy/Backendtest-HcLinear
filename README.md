# Football Transfer

Ebben a projektben a Football Transfer feladat megoldásom található. Spring Boot keretrendszert és Java nyelvet használtam, PostgreSQL adatbázissal és Flyway migrációval.

## Előfeltételek

- Java 21
- Docker (Docker Desktop)

Gradle telepítése nem szükséges, mert a projekt a Gradle wrappert használja.

## Indítás

1. Indítsd el a Docker Desktopot.
2. A `development` könyvtárban indítsd el az adatbázist:

   docker compose up -d

3. Indítsd el az alkalmazást a `dev` profillal:
    - IntelliJ-ben: *Run/Debug Configurations* → `BackendTestApplication` → *Active profiles*: `dev`
    - Terminálból:

     ./gradlew bootRun --args="--spring.profiles.active=dev"


Indítás után az alkalmazás a `http://localhost:8080` címen érhető el.

Megjegyzés: az adatbázis-konténer a gépen nem az alapértelmezett 5432-es, hanem az **5433**-as porton érhető el.

## Konfiguráció

- application.yml (alap konfiguráció):** az általános JPA/Hibernate és Flyway beállítások, az explicit szerverport és a logolási beállítások.
- application-dev.yml (dev profil):** az adatbázis-kapcsolat, valamint a fejlesztéshez szükséges JPA- és logolási beállítások.

Dev profil nélkül, a kapcsolati adatok külön megadása nélkül az alkalmazás nem indul el.

A BackendtestApplicationTests osztályon lévő @ActiveProfiles("dev") annotáció biztosítja, hogy a teszt a dev profillal fusson. 
A ./gradlew build csak akkor fut le sikeresen, ha a docker konténer is fut.


## Eltérések a kapott projekthez képest


A `docker-compose.yml` fájlban a port-kivezetés kikommentezve érkezett, ezért a konténer elindult, de a gépről nem volt elérhető. A kivezetést visszakapcsoltam, az 5433-as gépoldali porttal.

### .gitignore

A `.gitignore` fájl módosításra került, mert a szintaxisa nem működött.

## API-dokumentáció (Postman collection)

| Metódus | Útvonal                                  | Leírás                                               | Siker |
|---------|------------------------------------------|------------------------------------------------------|-------|
| POST    | `/api/teams`                             | Csapat létrehozása                                   | 201   |
| GET     | `/api/teams`                             | Összes csapat (játékosszámmal és a kapitány nevével) | 200   |
| GET     | `/api/teams/{id}`                        | Egy csapat lekérdezése                               | 200   |
| DELETE  | `/api/teams/{id}`                        | Csapat törlése                                       | 204   |
| PUT     | `/api/teams/{teamId}/captain/{playerId}` | Kapitány kijelölése                                  | 200   |
| POST    | `/api/players`                           | Játékos létrehozása                                  | 201   |
| GET     | `/api/players`                           | Összes játékos (teljes névvel és a csapat nevével)   | 200   |
| GET     | `/api/players/{id}`                      | Egy játékos lekérdezése                              | 200   |
| DELETE  | `/api/players/{id}`                      | Játékos törlése                                      | 204   |

A kérésekhez és a válaszokhoz DTO-kat használok, az entitások nem kerülnek ki a service rétegből.

## Tervezési döntések

### Adatmodell

- Az azonosítók Long típusúak (az adatbázisban BIGSERIAL).
- A játékos és a csapat között kétirányú kapcsolat van: a játékos felől `@ManyToOne`, a csapat felől `@OneToMany`. A játékosnak nem kötelező csapattal rendelkeznie.
- A kapitányt a teams tábla captain_id oszlopa tárolja (egyirányú @OneToOne). Az oszlop egyedi, így egy játékos legfeljebb egy csapat kapitánya lehet.
- A pozíciót (GK, DF, MF, FW) a PlayerType enum tárolja. Az adatbázisban szövegként mentődik, és egy CHECK megszorítás csak a négy értéket engedi.
- A pénzösszegek (költségkeret, piaci érték) Javában BigDecimal, az adatbázisban NUMERIC(15, 0) típusúak, egész euróban értendők.

### Üzleti szabályok

- Csapat törlésekor a csapat játékosai nem törlődnek, csak a csapatuk lesz null. Csapat nélküli játékos is megengedett, és így a játékosokhoz kapcsolódó adatok megmaradnak.
- Kapitányt csak a csapat tagjai közül lehet kijelölni (ellenkező esetben 422). Ha a kapitányt törlik, a csapat kapitánya automatikusan null lesz.
- A mezszám opcionális (csapat nélküli játékosnak nincs), 1 és 99 közötti, és csapaton belül egyedi (ütközés esetén 409).
- Az alapítási év legkisebb elfogadott értéke 1857, ami az első futballklub, Sheffield F.C. alapításának dátuma. A legnagyobb az aktuális év.
- A játékosnak legalább 15 évesnek kell lennie.
- A két minimumértéket static final konstansokban tárolom a service osztályokban.

### Validáció három szinten

- DTO (annotációk): a mezőnkénti, egyszerű szabályok (kötelező mezők, hosszak, tartományok). Gyors, nem igényel adatbázist, és a kliens mezőnként érthető hibaüzenetet kap.
- Service: azok a szabályok, amelyekhez adatbázis-lekérdezés kell (egyedi csapatnév, foglalt mezszám, csapattagság), vagy amelyek az aktuális dátumtól függenek, ezért nem fejezhetők ki fix annotációval (alapítási év, életkor). Ezek külön privát metódusokban vannak.
- Adatbázis (megszorítások): az utolsó ellenőrzés. Akkor is megvédi az adatokat, ha két egyidejű kérés egyszerre megy át a service ellenőrzésén, vagy ha egy módosítás megkerüli a service réteget. Az ilyenkor keletkező DataIntegrityViolationException-t a hibakezelő 409-es válaszra fordítja.

### Hibakezelés

Minden hiba egységes formátumban érkezik:

- `errorType`: a hiba fajtája (az `ErrorType` enum egy értéke),
- `message`: rövid összefoglaló,
- `details`: a részletek listája (validációs hibánál mezőnként).

| Státusz | `errorType`                | Mikor                                                |
|---------|----------------------------|------------------------------------------------------|
| 400     | `VALIDATION_ERROR`         | Hibás mezőérték                                      |
| 400     | `FORMAT_ERROR`             | Hibás JSON, érvénytelen érték vagy útvonal-paraméter |
| 404     | `NOT_FOUND_ERROR`          | Nem létező csapat, játékos vagy végpont              |
| 405     | `METHOD_NOT_ALLOWED_ERROR` | Nem támogatott HTTP-metódus                          |
| 409     | `ALREADY_EXISTS_ERROR`     | Foglalt csapatnév vagy mezszám                       |
| 422     | `BUSINESS_RULE_ERROR`      | Üzleti szabály megsértése                            |
| 500     | `UNEXPECTED_SERVER_ERROR`  | Váratlan hiba (belső részletek nélkül)               |

A saját kivételek közös ősosztályokból származnak (NotFoundException, AlreadyExistsException, BusinessRuleException, FieldValidationException), így a GlobalExceptionHandler ősosztályonként egyetlen metódussal kezeli őket.

### Egyéb

- Az open-in-view ki van kapcsolva. Az entitások DTO-vá alakítása ezért a @Transactional service-metódusokon belül történik, ahol a lazy betöltésű kapcsolatok elérhetők.
- A mapperek MapStructtal készültek. A nem triviális mezőket (a játékos teljes neve, a kapitány neve, a játékosok száma) default metódusok számolják.
- A repositoryk két saját metódusa (existsByName, existsByShirtNumberAndTeamId) a Spring Data Query Method, metódusnévből generált lekérdezéseit használja.

A Postman collection a `resources/postman/` könyvtárban található.