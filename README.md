# base-project-spring-boot-starter

Librería base modular para microservicios Spring Boot de VHRE. Proporciona clases reutilizables
para CRUD completo con soft-delete, auditoría, paginación y manejo de errores:

| Componente | Descripción |
|---|---|
| `BaseEntity` | Entidad JPA raíz: `id` (UUID), auditoría (`@CreatedDate` / `@LastModifiedDate`) y soft-delete (`is_deleted` con `@SQLRestriction`) |
| `BaseDTO` | DTO raíz con los mismos campos de auditoría e identificador validado con `@Null` |
| `BaseMapper` | Contrato de mapeo Entity ↔ DTO para MapStruct (protegido: nunca sobreescribe `id`, auditoría ni `deleted` en updates) |
| `BaseService` / `BaseServiceImpl` | CRUD transaccional genérico: findAll paginado, findById, save, update, delete (soft) |
| `BaseController` | Endpoints REST genéricos (`GET`, `GET/{id}`, `POST`, `PUT/{id}`, `DELETE/{id}`) con activación selectiva (`ApiMethod.READ_ALL`, etc.) |
| `GlobalExceptionHandler` | Manejo centralizado de errores con respuestas estandarizadas |

## Publicación con GitHub Packages

La librería se publica automáticamente en **GitHub Packages** mediante
[`.github/workflows/publish.yml`](.github/workflows/publish.yml) — sin secretos manuales, usa el
`GITHUB_TOKEN` propio del repositorio:

| Evento | Artefacto publicado |
|---|---|
| Push a `main` | `com.vhre:base-project-spring-boot-starter:0.0.1-SNAPSHOT` (siempre la última) |
| Release publicada con tag `vX.Y.Z` | `com.vhre:base-project-spring-boot-starter:X.Y.Z` (versión estable) |

> **Nota:** una Release solo dispara el workflow si su tag apunta a un commit que ya contenga el
> workflow (el tag `v0.0.1` es anterior a esta configuración).

## Cómo usarla en un proyecto Spring Boot

### 1. Crear un Personal Access Token (lectura)

El registry Maven de GitHub **siempre requiere autenticación**, aunque el paquete sea público:

1. GitHub → `Settings` → `Developer settings` → `Personal access tokens` → **Tokens (classic)**.
2. Generar un token con el permiso **`read:packages`**.
3. Guardar el token como contraseña (usuario = tu usuario de GitHub).

### 2. Configurar credenciales en Maven (`~/.m2/settings.xml`)

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">
    <servers>
        <server>
            <!-- Debe coincidir con el <id> del <repository> del pom del consumidor -->
            <id>github</id>
            <username>USUARIO_GITHUB</username>
            <password>TOKEN_PAT_CON_READ_PACKAGES</password>
        </server>
    </servers>
</settings>
```

### 3. Registrar el repositorio y la dependencia en el `pom.xml`

```xml
<repositories>
    <repository>
        <!-- El <id> debe coincidir con el <server> de settings.xml -->
        <id>github</id>
        <url>https://maven.pkg.github.com/VHRE03/base-project-spring-boot</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <!-- Necesario solo si consumes la versión SNAPSHOT -->
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.vhre</groupId>
        <artifactId>base-project-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version> <!-- o una versión estable, p. ej. 0.1.0 -->
    </dependency>
</dependencies>
```

> ⚠️ **Migración desde JitPack:** las coordenadas cambian. JitPack publicaba la librería como
> `com.github.VHRE03:base-project-spring-boot:<hash>`; en GitHub Packages se usan las coordenadas
> reales del `pom.xml`: `com.vhre:base-project-spring-boot-starter:<versión>`. Elimina también el
> repositorio `https://jitpack.io`.

### 4. (Opcional) Consumir desde CI

Si el consumidor también corre en GitHub Actions, no uses un PAT: pasa un secret con un token
con permiso `read:packages` y configúralo igual que en la publicación:

```yaml
- uses: actions/setup-java@v4
  with:
    java-version: '21'
    distribution: 'temurin'
    server-id: github
    server-username: GH_PACKAGES_USER   # nombre de las variables de entorno
    server-password: GH_PACKAGES_TOKEN
# ... y en el paso de build:
  env:
    GH_PACKAGES_USER: ${{ secrets.GH_PACKAGES_USER }}
    GH_PACKAGES_TOKEN: ${{ secrets.GH_PACKAGES_TOKEN }}
```

## Versionado recomendado

- **SNAPSHOT** (`0.0.1-SNAPSHOT`): cada push a `main` re-publica la última versión de desarrollo.
  Ideal para integración interna, pero evita usarla para desplegar a producción.
- **RELEASE** (`X.Y.Z`): crea una Release en GitHub con tag `vX.Y.Z` y el workflow publica la
  versión estable. Recomendada para proyectos consumidores.

> Los deploys de SNAPSHOT acumulan versiones en el paquete (GitHub las trata como versiones
> separadas). Limpia versiones antiguas desde la página del paquete
> (`https://github.com/VHRE03/base-project-spring-boot/packages`) o vía API cuando crezca el almacenamiento.
