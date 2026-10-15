# Football Transfer

Ebben a projektben a Football Transfer feladat megoldásom található. Spring Boot keretrendszert és Java nyelvet használtam, PostgreSQL adatbázissal és Flyway migrációval.

## Előfeltételek

- Java 21
- Docker (Docker Desktop)

Gradle telepítése nem szükséges, mert a projekt a Gradle wrappert használja.

## Indítás

1. Indítsd el a Docker Desktopot.
2. A development könyvtárban indítsd el az adatbázist:

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


A docker-compose.yml fájlban a port-kivezetés kikommentezve érkezett, ezért a konténer elindult, de a gépről nem volt elérhető. A kivezetést visszakapcsoltam, az 5433-as gépoldali porttal.

### .gitignore

A .gitignore fájl módosításra került, mert a szintaxisa nem működött.

## API-dokumentáció (Postman collection)

| Metódus | Útvonal                                      | Leírás                                               | Siker |
|---------|----------------------------------------------|------------------------------------------------------|-------|
| POST    | `/api/teams`                                 | Csapat létrehozása                                   | 201   |
| GET     | `/api/teams`                                 | Összes csapat (játékosszámmal és a kapitány nevével) | 200   |
| GET     | `/api/teams/{id}`                            | Csapat lekérdezése Id alapján                        | 200   |
| DELETE  | `/api/teams/{id}`                            | Csapat törlése Id alapján                            | 204   |
| PUT     | `/api/teams/{teamId}/captain/{playerId}`     | CsK kijelölése                                       | 200   |
| POST    | `/api/players`                               | Játékos létrehozása                                  | 201   |
| GET     | `/api/players`                               | Összes játékos (teljes névvel és a csapat nevével)   | 200   |
| GET     | `/api/players/{id}`                          | Játékos lekérdezése Id alapján                       | 200   |
| DELETE  | `/api/players/{id}`                          | Játékos törlése Id alapján                           | 204   |
| POST    | `/api/transfer-requests`                     | Átigazolási kérelem létrehozása                      | 201   |
| POST    | `/api/transfer-requests/{id}/accept`         | Kérelem elfogadása                                   | 200   |
| POST    | `/api/transfer-requests/{id}/reject`         | Kérelem elutasítása                                  | 200   |
| GET     | `/api/players/{id}/transfer-requests`        | Játékos kérelmei Id alapján                          | 200   |
| GET     | `/api/teams/{id}/transfer-requests/outgoing` | Csapat kimenő kérelmei Id alapján                    | 200   |
| GET     | `/api/teams/{id}/transfer-requests/incoming` | Csapat bejövő kérelmei Id alapján                    | 200   |
| GET     | `/api/transfer-documents`                    | Összes átigazolási dokumentum                        | 200   |
| GET     | `/api/transfer-documents/{id}`               | Dokumentum lekérdezése Id alapján                    | 200   |
| GET     | `/api/transfer-documents/{id}/pdf`           | Átigazolási dokumentum PDF-ben                       | 200   |

A kérésekhez és a válaszokhoz DTO-kat használok, az entitások nem kerülnek ki a service rétegből.

## Tervezési döntések

### Adatmodell

Az azonosítók Long típusúak (az adatbázisban BIGSERIAL).
A játékos és a csapat között kétirányú kapcsolat van: a játékos felől `@ManyToOne`, a csapat felől `@OneToMany`. A játékosnak nem kötelező csapattal rendelkeznie.
A kapitányt a teams tábla captain_id oszlopa tárolja (egyirányú @OneToOne). Az oszlop egyedi, így egy játékos legfeljebb egy csapat kapitánya lehet.
A pozíciót (GK, DF, MF, FW) a PlayerType enum tárolja. Az adatbázisban szövegként mentődik, és egy CHECK megszorítás csak a négy értéket engedi.
A pénzösszegek (költségkeret, piaci érték) Javában BigDecimal, az adatbázisban NUMERIC(15, 0) típusúak, egész euróban értendők.

### Üzleti szabályok

Csapat törlésekor a csapat játékosai nem törlődnek, csak a csapatuk lesz null. Csapat nélküli játékos is megengedett, és így a játékosokhoz kapcsolódó adatok megmaradnak.
Kapitányt csak a csapat tagjai közül lehet kijelölni (ellenkező esetben 422). Ha a kapitányt törlik, a csapat kapitánya automatikusan null lesz.
A mezszám opcionális (csapat nélküli játékosnak nincs), 1 és 99 közötti, és csapaton belül egyedi (ütközés esetén 409).
Az alapítási év legkisebb elfogadott értéke 1857, ami az első futballklub, Sheffield F.C. alapításának dátuma. A legnagyobb az aktuális év.
A játékosnak legalább 15 évesnek kell lennie.
A két minimumértéket static final konstansokban tárolom a service osztályokban.

### Validáció három szinten

DTO (annotációk): a mezőnkénti, egyszerű szabályok (kötelező mezők, hosszak, tartományok). 
Service: azok a szabályok, amelyekhez adatbázis-lekérdezés kell (egyedi csapatnév, foglalt mezszám, csapattagság), vagy amelyek az aktuális dátumtól függenek, ezért nem fejezhetők ki fix annotációval (alapítási év, életkor). Ezek külön privát metódusokban vannak.
Adatbázis (megszorítások): az utolsó ellenőrzés. Akkor is megvédi az adatokat, ha két egyidejű kérés egyszerre megy át a service ellenőrzésén, vagy ha egy módosítás megkerüli a service réteget. Az ilyenkor keletkező DataIntegrityViolationException-t a hibakezelő 409-es válaszra fordítja.

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

## Átigazolási kérelmek

A kérelem összege e-mailes egyeztetés alapján a játékos piaci értéke (marketValue). Ha a játékosnak nincs csapata, az összeg 0.

A kérelem állapotát enumban tárolom: PENDING, ACCEPTED, REJECTED, CLOSED. Az adatbázisban szövegként tárolom őket, egy CHECK megszorítással.

A lejárt állapotot nem tárolom, hanem a lejárati dátumból számolom: egy függő kérelem lejárt, ha a lejárati ideje elmúlt. Ezt a válaszban az expired mező mutatja. 
Ha a kliens nem ad meg lejáratot, az automatikusan 48 óra. Ha megad, annak jövőbeli dátumnak kell lennie.

Létrehozáskor ellenőrzöm, hogy a játékos és a célcsapat létezik-e, hogy a megadott forráscsapat megegyezik-e a játékos jelenlegi csapatával, és hogy a forrás és a cél nem ugyanaz.
Egy játékosnak több függő kérelme is lehet, de ugyanattól a célcsapattól csak egy.

Elfogadáskor újra ellenőrzök mindent, ami a létrehozás óta megváltozhatott: függőben van-e még a kérelem, nem járt-e le, a játékos még mindig a forráscsapatban van-e, van-e hely a célcsapatban (legfeljebb 25 játékos)
és van-e elég pénze a célcsapatnak. Ha bármelyik nem teljesül, 422-es hibát adok.

Sikeres átigazoláskor a célcsapat keretéből levonódik az összeg és a forráscsapathoz kerül, a játékos átkerül a célcsapatba, és a mezszáma felszabadul. 
Ha a játékos a forráscsapat kapitánya volt, a csapat kapitánya null lesz. A játékos többi függő kérelme automatikusan CLOSED állapotba kerül. 
Mindez egy tranzakcióban történik, így ha bármelyik lépés elbukik, semmi nem módosul.

Elutasítani csak függő kérelmet lehet. A lejárt kérelmet nem lehet elfogadni, de el lehet utasítani.

A kérelmek listázhatók játékos, forráscsapat (kimenő) és célcsapat (bejövő) szerint.

## Átigazolási dokumentum

Sikeres átigazoláskor automatikusan létrejön egy dokumentum a transfer_documents táblában, ugyanabban a tranzakcióban, mint maga az átigazolás.

A dokumentumnak van egy technikai azonosítója (id) és egy olvasható dokumentumszáma, például TRFR000014HU. Ez az előtagból egy hatjegyű, nullákkal kitöltött sorszámból és az utótagból (HU) áll.
A sorszámot egy Postgres-szekvencia adja, amit egy natív lekérdezéssel kérek le, a formázás pedig a service-ben történik. Az előtag és az utótag konstansok.

A szekvenciában lehetnek hézagok: ha egy tranzakció a sorszám lekérése után elbukik, az a szám nem kerül vissza. 
Mivel minden ellenőrzés a dokumentum létrehozása előtt lefut, ez ritka de ismert hiba.

A dokumentum idegen kulccsal hivatkozik a kérelemre (egyedi, tehát egy kérelemhez legfeljebb egy dokumentum tartozik), a játékosra és a csapatokra.
Nem snapshot jellegű, tehát ha a hivatkozott adatok később változnának, a dokumentum az aktuális adatokat mutatná.

A dokumentumokhoz külön service és controller tartozik, a lista és az Id alapján történő lekérdezés végpontjaival.

## PDF-export

Az átigazolási dokumentum PDF-ben is lekérhető a `GET /api/transfer-documents/{id}/pdf` végponton. A PDF a kiírás szerinti adatokat tartalmazza: a játékos nevét, a forrás- és a célcsapat nevét és városát (ha nincs forráscsapat, ezt jelzi), az átigazolási összeget és a létrehozás idejét.

A PDF előállításához az OpenPDF könyvtárat használtam. A 2.x verziót választottam, mert a 3.0-ban megváltozott a csomagnév (`com.lowagie` helyett `org.openpdf`), és a legtöbb elérhető példa még a régit használja.

A PDF-et egy külön osztály (TransferDocumentPdfGenerator) készíti a memóriában, fájl mentése nélkül. A service hívja meg, a tranzakción belül, mert a játékos és a csapatok lazy betöltésű kapcsolatok. A magyar ékezetek (például ő, ű) miatt a betűtípust CP1250 kódolással hozom létre.

A válasz application/pdf típusú, és a Content-Disposition fejléc miatt a böngésző vagy a Postman rögtön megjeleníti a fájlt.

### Tesztek

Unit teszteket a TransferRequestService osztályra írtam, melyet AI segítségével generáltam. Ezután ellenőriztem és futtattam őket, 10/10 passed eredménnyel.

### Egyéb

- Az open-in-view ki van kapcsolva. Az entitások DTO-vá alakítása ezért a @Transactional service-metódusokon belül történik, ahol a lazy betöltésű kapcsolatok elérhetők.
- A mapperek MapStructtal készültek. A nem triviális mezőket (a játékos teljes neve, a kapitány neve, a játékosok száma, stb...) default metódusok számolják.
- A repositoryk saját metódusai a Spring Data Query Method lekérdezéseit használják, illetve a korábban említett natív sql-t.
- A fejlesztés során AI-asszisztenst tanulásra, hibakeresésre és kódnézésre használtam; a kódot én írtam, és a tervezési döntéseket én hoztam.

A Postman collection az src/main/postman könyvtárban található.