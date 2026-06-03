# Codex Session Backup

Ngay tao: 2026-06-03

May hien tai chuan bi cai lai he dieu hanh sang Ubuntu. File nay dung de khoi phuc ngu canh lam viec voi Codex sau khi cai lai may.

## Project

- Ten project: Manga Vault
- Thu muc goc tren Windows: `C:\HOC TAP\TruyentranhWeb\TruyentranhWeb`
- Cong nghe: Java, Spring Boot, Maven
- Muc tieu hoc tap: xay backend nho de luu manga ca nhan, di tung task nho de hieu Spring Boot.

## Trang thai hien tai

Project da co cac phan chinh:

- Health check API: `GET /api/health`
- Response wrapper chung: `ApiResponse<T>`
- Database layer voi JPA/MySQL trong `pom.xml` va `application.properties`
- Entity `Manga`
- Enum `MangaStatus`
- Enum `Visibility`
- Repository `MangaRepository`
- DTO request: `MangaCreationRequest`
- DTO response: `MangaResponse`
- Service `MangaService` de tao manga
- Controller `MangaController` voi API tao manga: `POST /api/mangas`

README hien ghi task tiep theo la:

```text
Task 3.7: Tao API GET /api/mangas de lay danh sach manga
```

Muc tieu cua task tiep theo la hoc luong doc du lieu tu database va tra danh sach response cho client.

## Git va backup da tao

- Da tao Git repo local trong project.
- Commit backup co message `Backup before Ubuntu reinstall`.
- Sau khi khoi phuc, dung `git log --oneline -1` de xem hash commit moi nhat.
- Da tao Git bundle trong project: `MangaVault_git_backup_2026-06-03.bundle`
- Da tao file zip project o thu muc cha:

```text
C:\HOC TAP\TruyentranhWeb\MangaVault_FULL_BACKUP_2026-06-03.zip
```

- Da tao backup cau hinh Codex o thu muc cha:

```text
C:\HOC TAP\TruyentranhWeb\Codex_CONFIG_BACKUP_2026-06-03.zip
```

- Thu muc staging cau hinh Codex van con o:

```text
C:\HOC TAP\TruyentranhWeb\Codex_CONFIG_STAGING_2026-06-03
```

## File/chuc nang quan trong

- `pom.xml`: cau hinh Maven dependencies
- `src/main/resources/application.properties`: cau hinh datasource/JPA
- `src/main/java/com/daniel/mangavault/MangaVaultApplication.java`: entrypoint Spring Boot
- `src/main/java/com/daniel/mangavault/controller/HealthController.java`: health check API
- `src/main/java/com/daniel/mangavault/controller/MangaController.java`: manga API
- `src/main/java/com/daniel/mangavault/service/MangaService.java`: business logic cho manga
- `src/main/java/com/daniel/mangavault/repository/MangaRepository.java`: Spring Data JPA repository
- `src/main/java/com/daniel/mangavault/entity/Manga.java`: JPA entity
- `src/main/java/com/daniel/mangavault/enums/MangaStatus.java`: trang thai manga
- `src/main/java/com/daniel/mangavault/enums/Visibility.java`: do hien thi manga
- `src/main/java/com/daniel/mangavault/dto/request/MangaCreationRequest.java`: request DTO tao manga
- `src/main/java/com/daniel/mangavault/dto/response/MangaResponse.java`: response DTO manga
- `src/main/java/com/daniel/mangavault/dto/response/ApiResponse.java`: response wrapper chung

## Cach khoi phuc sau khi cai Ubuntu

1. Cai Git, Java JDK va Maven neu can.
2. Clone repo tu remote Git hoac copy lai thu muc backup cua project.
3. Mo project bang Codex.
4. Dua file nay cho Codex va noi: "Tiep tuc tu CODEX_SESSION_BACKUP.md".
5. Neu dung MySQL local, tao lai database `manga_vault` va cap nhat username/password trong `application.properties`.

## Lenh nen chay sau khi khoi phuc

```bash
git status
./mvnw test
./mvnw spring-boot:run
```

Sau khi app chay, test nhanh:

```http
GET http://localhost:8080/api/health
POST http://localhost:8080/api/mangas
```

Luu y: ngay truoc khi backup, lenh `.\mvnw.cmd test` tren Windows bi fail vi Spring Boot khong ket noi duoc MySQL tai `localhost:3306` (`Communications link failure`). Sau khi sang Ubuntu can cai/chay MySQL, tao database `manga_vault`, va cap nhat `application.properties` truoc khi test full context Spring Boot.

## Viec can lam tiep

- Them service method lay danh sach manga, co the dat ten `getMangas()` hoac `getAllMangas()`.
- Dung `mangaRepository.findAll()` de doc danh sach.
- Map tung `Manga` entity sang `MangaResponse`.
- Them endpoint `GET /api/mangas` trong `MangaController`.
- Tra ve `ApiResponse<List<MangaResponse>>`.
- Chay test/build de dam bao project compile.

## Ghi chu ve Codex

Neu muon giu cau hinh Codex ca nhan, truoc khi cai lai may nen backup thu muc:

```text
C:\Users\LAMTHANHDUC\.codex
```

Sau khi sang Ubuntu, co the copy lai vao:

```text
~/.codex
```

Thu muc nay nam ngoai project, nen can copy bang File Explorer/USB/Drive neu Codex khong co quyen ghi ra ngoai workspace.
