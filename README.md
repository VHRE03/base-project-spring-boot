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

El registry Maven de GitHub **siempre requiere autenticación**, aunque el paquete sea público.
El token se crea una sola vez por usuario y sirve para todos los proyectos que consuman
paquetes de GitHub Packages:

1. Entra a GitHub → clic en tu foto de perfil → **`Settings`**.
2. Al final del menú izquierdo: **`Developer settings`** → **`Personal access tokens`** → **`Tokens (classic)`**.
3. **`Generate new token` → `Generate new token (classic)`**.
4. Asígnale un nombre descriptivo (p. ej. `read-github-packages`) y una expiración adecuada.
5. En **`Select scopes`** marca únicamente **`read:packages`**.
6. Genera y **copia el token inmediatamente** (GitHub no lo vuelve a mostrar).

> ⚠️ **Debe ser un token CLASSIC, no "fine-grained".** El registry Maven de GitHub Packages
> solo acepta tokens *classic*: los *fine-grained* (`github_pat_...`) devuelven `401
> Unauthorized` aunque tengan permisos equivalentes. Verifica el prefijo del token generado:
> los classic empiezan con **`ghp_`**.

### 2. Configurar credenciales en Maven (`~/.m2/settings.xml`)

**Este archivo no va dentro de ningún proyecto**: es la configuración de Maven a nivel de
**máquina/usuario**, y la necesita el lado que **consume** la librería (las máquinas del
equipo y el CI de los proyectos consumidores). El proyecto base **no** lo requiere — publica
automáticamente con el `GITHUB_TOKEN` del runner de GitHub Actions.

Crea o edita el archivo `~/.m2/settings.xml` (Linux/macOS) o `%USERPROFILE%\.m2\settings.xml` (Windows):

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">
    <servers>
        <server>
            <!-- REGLA CRITICA: este <id> debe coincidir EXACTAMENTE con el <id> del
                 <repository> declarado en el pom.xml del proyecto consumidor -->
            <id>github</id>
            <username>USUARIO_GITHUB</username>        <!-- tu usuario de GitHub -->
            <password>ghp_XXXXXXXXXXXXXXXXXXXX</password> <!-- el PAT classic del paso 1 -->
        </server>
    </servers>
</settings>
```

Puntos clave de esta configuración:

- **`<id>github</id>`**: Maven empareja las credenciales con el repositorio *por id*. Si el
  `<id>` del `<server>` no coincide con el `<id>` del `<repository>` del pom, Maven hace la
  petición de forma anónima y GitHub responde `401 Unauthorized`.
- **`<username>`**: tu usuario de GitHub (con un PAT classic cualquier valor no vacío
  funciona, pero usa el real).
- **`<password>`**: el token completo, sin espacios ni saltos de línea.
- Si usas **IntelliJ**, este archivo se toma por defecto (`Settings → Build Tools → Maven →
  User settings file`); tras editarlo, recarga el proyecto Maven para que lo vuelva a leer.

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
- uses: actions/setup-java@v5
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

## Solución de problemas

### `401 Unauthorized` al resolver la dependencia

Significa que la petición llegó a GitHub **sin credenciales válidas**. Causas en orden de
frecuencia:

1. **No existe `~/.m2/settings.xml`**, o el `<id>` del `<server>` no coincide con el `<id>`
   del `<repository>` del pom → Maven pide el paquete de forma anónima.
2. **El token es *fine-grained*** (`github_pat_...`) → no funcionan con Packages; usa uno
   **classic** (`ghp_...`).
3. **El token no tiene el scope `read:packages`**, o expiró / fue revocado.

Puedes aislar el problema de Maven probando el token directamente con `curl` (es la misma
URL que Maven intenta descargar):

```bash
curl -o /dev/null -s -w "%{http_code}\n" -u USUARIO:ghp_tu_token \
  https://maven.pkg.github.com/VHRE03/base-project-spring-boot/com/vhre/base-project-spring-boot-starter/maven-metadata.xml
```

| Código | Significado |
|---|---|
| `200` | Token válido y paquete publicado — Maven ya debería funcionar |
| `401` | Problema con el token (classic vs fine-grained, scope o expiración) |
| `404` | Token válido pero el paquete no existe — verifica que el workflow de publicación terminó en verde |

### El error persiste aunque el token ya es correcto

Maven **cachea los fallos de descarga** en el repositorio local (verás el aviso
`...failed to transfer... This failure was cached in the local repository and resolution
will not be reattempted...`). Fuerza el reintento de dos formas:

```bash
# Opción A: compilar forzando la actualización de snapshots
./mvnw -U clean compile

# Opción B: eliminar el estado cacheado de la dependencia
rm -rf ~/.m2/repository/com/vhre/base-project-spring-boot-starter/
```

## Versionado recomendado

- **SNAPSHOT** (`0.0.1-SNAPSHOT`): cada push a `main` re-publica la última versión de desarrollo.
  Ideal para integración interna, pero evita usarla para desplegar a producción.
- **RELEASE** (`X.Y.Z`): crea una Release en GitHub con tag `vX.Y.Z` y el workflow publica la
  versión estable. Recomendada para proyectos consumidores.

> Los deploys de SNAPSHOT acumulan versiones en el paquete (GitHub las trata como versiones
> separadas). Limpia versiones antiguas desde la página del paquete
> (`https://github.com/VHRE03/base-project-spring-boot/packages`) o vía API cuando crezca el almacenamiento.
