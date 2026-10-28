# 🏡 SimpleHomesNK

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-Bedrock%20Edition-43A047?style=for-the-badge&logo=minecraft&logoColor=white" alt="Minecraft Bedrock Edition">
  <img src="https://img.shields.io/badge/Server-NukkitPetteriM1Edition-2196F3?style=for-the-badge" alt="NukkitPetteriM1Edition">
  <img src="https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 25">
  <img src="https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="SQLite">
</p>

<p align="center">
  <b>Un sistema de hogares sencillo, configurable y persistente para servidores Minecraft Bedrock.</b>
</p>

---

## 📖 ¿Qué es SimpleHomesNK?

**SimpleHomesNK** es un plugin desarrollado para servidores Minecraft Bedrock que utilizan **NukkitPetteriM1Edition**. Permite a los jugadores guardar ubicaciones importantes, regresar a ellas cuando lo necesiten y administrar sus hogares mediante comandos e interfaces gráficas.

El objetivo es ofrecer un sistema de hogares práctico, con límites configurables según los permisos del jugador, almacenamiento persistente y un sistema de teletransporte con tiempo de espera para mejorar la experiencia de juego.

El plugin utiliza Java, Maven, SQLite y **FormConstructor de MEFRREEX** para gestionar los hogares, almacenar los datos y mostrar formularios interactivos.

## ✨ Características principales

- 🏠 **Gestión de hogares:** crear, visitar, eliminar y consultar hogares guardados.
- 📝 **Renombrar hogares:** cambiar el nombre de un hogar desde la interfaz gráfica.
- 💾 **Guardado persistente:** los hogares permanecen guardados después de reiniciar el servidor.
- 🔐 **Límites por permisos:** configura cuántos hogares puede tener cada grupo mediante permisos compatibles con LuckPerms.
- ⏳ **Teletransporte con espera:** el jugador debe esperar antes de ser teletransportado.
- 🛑 **Cancelación del teletransporte:** moverse o recibir daño durante la espera cancela el proceso.
- 🧭 **Comprobación del destino:** el plugin utiliza lógica de seguridad para buscar una ubicación apropiada para el teletransporte.
- 🖥️ **Interfaz gráfica:** permite consultar y administrar los hogares desde un menú.
- ⚙️ **Configuración sencilla:** modifica los límites y el tiempo de espera desde `config.yml`.

## 📋 Comandos

| Comando | Descripción |
|---|---|
| `/sethome` | Guarda un hogar con el nombre predeterminado. |
| `/sethome <nombre>` | Guarda un hogar con un nombre personalizado. |
| `/home` | Teletransporta al jugador a su hogar predeterminado. |
| `/home <nombre>` | Teletransporta al jugador al hogar indicado. |
| `/delhome <nombre>` | Elimina el hogar indicado. |
| `/homes` | Abre el menú para consultar y administrar los hogares. |

> Los comandos requieren los permisos correspondientes definidos en el plugin. Consulta `plugin.yml` para comprobar sus declaraciones.

## 🖥️ Interfaz de hogares

El menú de `/homes` permite acceder a los hogares guardados y realizar acciones desde la interfaz.

Entre las funciones disponibles se encuentran:

- **Teleport:** inicia el teletransporte al hogar seleccionado.
- **Rename:** abre un formulario para cambiar el nombre del hogar.
- **Back:** permite regresar al menú anterior.

### ✏️ Sistema para renombrar hogares

La función de renombrado utiliza un formulario personalizado de **FormConstructor**. El jugador introduce el nuevo nombre y el plugin valida la entrada antes de guardar el cambio.

El sistema actualiza el nombre del hogar en la base de datos, conservando su ubicación y los demás datos asociados.

## 🔐 Sistema de límites

SimpleHomesNK permite establecer límites diferentes según los permisos que tenga cada jugador.

Configuración inicial:

| Grupo | Permiso | Límite |
|---|---|---:|
| Jugador predeterminado | `simplehomes.limit.3` | 3 hogares |
| VIP | `simplehomes.limit.5` | 5 hogares |
| VIP+ | `simplehomes.limit.7` | 7 hogares |
| VIPExt+ | `simplehomes.limit.10` | 10 hogares |
| Operador | Según `op-unlimited` | Ilimitado si está activado |

Los nombres de los rangos son ejemplos de uso. Puedes adaptar los permisos a los grupos de tu servidor mediante LuckPerms.

## ⚙️ Configuración

El archivo principal de configuración es `config.yml`.

```yaml
settings:
  op-unlimited: true
  default-limit: 3
  teleport-delay: 3

home-limits:
  simplehomes.limit.3: 3
  simplehomes.limit.5: 5
  simplehomes.limit.7: 7
  simplehomes.limit.10: 10
```

### ¿Qué hace cada opción?

| Opción | Función |
|---|---|
| `op-unlimited` | Permite que los operadores tengan hogares ilimitados cuando está activado. |
| `default-limit` | Establece el límite predeterminado de hogares. |
| `teleport-delay` | Define los segundos de espera antes del teletransporte. |
| `home-limits` | Relaciona cada permiso con su límite de hogares. |

**Ejemplo:** si `teleport-delay` está configurado en `3`, el jugador debe esperar tres segundos antes de llegar al hogar. Si se mueve o recibe daño durante la espera, el teletransporte se cancela.

## 🛠️ Instalación

### Requisitos

- Un servidor compatible con **NukkitPetteriM1Edition**.
- **Java JDK 25** para compilar el proyecto según su configuración actual. Comprueba la compatibilidad de Java con tu versión del servidor antes de cambiar el entorno de producción.
- **FormConstructor de MEFRREEX**, utilizado para los formularios interactivos.
- El archivo JAR compilado de SimpleHomesNK.
- **LuckPerms**, si deseas gestionar los límites de hogares mediante rangos y permisos.

### 📥 Enlaces de descarga

| Componente | Versión o requisito | Enlace |
|---|---|---|
| NukkitPetteriM1Edition | Versión compatible con tu servidor | [Repositorio de GitHub](https://github.com/PetteriM1/NukkitPetteriM1Edition) |
| Java JDK | 25 | [Descargar JDK 25](https://jdk.java.net/25/) |
| FormConstructor — MEFRREEX | La versión utilizada por el proyecto | [Repositorio oficial](https://github.com/MEFRREEX/FormConstructor) · [Versiones y descargas](https://github.com/MEFRREEX/FormConstructor/releases) |
| SQLite JDBC | 3.50.3.0 | [Descargar desde GitHub](https://github.com/xerial/sqlite-jdbc/releases/tag/3.50.3.0) |
| LuckPerms | Versión compatible | [Repositorio oficial](https://github.com/LuckPerms/LuckPerms) |

**Importante sobre FormConstructor:** este proyecto utiliza la versión obtenida del repositorio de [MEFRREEX/FormConstructor](https://github.com/MEFRREEX/FormConstructor). Descarga la versión que corresponda al archivo `FormConstructor-3.1.0.jar` utilizado al compilar el proyecto. Si esa versión no aparece en las publicaciones del repositorio, revisa sus etiquetas y archivos disponibles antes de sustituirla por otra versión.

SQLite JDBC se integra en el JAR compilado mediante Maven. No necesitas instalar ese controlador como un plugin independiente cuando utilizas el JAR final generado correctamente.

### Instalación en el servidor

1. Descarga el archivo JAR de SimpleHomesNK desde la sección [Releases](https://github.com/BrunoJaegerK/SimpleHomesNK/releases), cuando haya una versión publicada.
2. Copia el JAR en la carpeta `plugins` de tu servidor.
3. Instala la versión compatible de FormConstructor de MEFRREEX siguiendo las instrucciones de su repositorio.
4. Inicia o reinicia el servidor y comprueba que el plugin se cargue correctamente.
5. Revisa la carpeta de datos del plugin y configura `config.yml` según las necesidades de tu servidor.
6. Prueba `/sethome`, `/home` y `/homes` dentro del servidor.

> Si todavía no hay una Release publicada, puedes compilar el proyecto siguiendo las instrucciones de desarrollo que aparecen a continuación.

## 👨‍💻 Compilar desde el código fuente

SimpleHomesNK utiliza **Maven** para gestionar las dependencias y generar el archivo JAR.

### Requisitos de desarrollo

- **JDK 25**.
- IntelliJ IDEA u otro IDE compatible con Maven.
- Maven.
- `server.jar` de NukkitPetteriM1Edition.
- `FormConstructor-3.1.0.jar`, correspondiente a la versión utilizada por el proyecto.

### Pasos

**1. Clona el repositorio:**

```bash
git clone https://github.com/BrunoJaegerK/SimpleHomesNK.git
```

**2.** Abre el proyecto en IntelliJ IDEA como un proyecto Maven y configura el JDK 25.

**3.** Coloca las dependencias locales en la carpeta `libs` con estos nombres:

```text
libs/
├── server.jar
└── FormConstructor-3.1.0.jar
```

**4.** Ejecuta la compilación desde la terminal, en la raíz del proyecto:

```bash
mvn clean package
```

**5.** Cuando Maven finalice correctamente, busca el JAR generado en la carpeta `target`.

**Importante:** las dependencias JAR locales se excluyen del repositorio para evitar subir archivos externos o pesados. Debes conseguir las versiones compatibles por separado y colocarlas en las rutas esperadas por el proyecto.

## 🗂️ Estructura del proyecto

```text
SimpleHomesNK/
├── libs/
│   ├── server.jar
│   └── FormConstructor-3.1.0.jar
├── src/
│   └── main/
│       ├── java/
│       │   └── com/brunojaegerk/simplehomes/
│       │       ├── SimpleHomes.java
│       │       ├── DatabaseManager.java
│       │       ├── HomeTeleportManager.java
│       │       ├── commands/
│       │       │   ├── SetHomeCommand.java
│       │       │   ├── HomeCommand.java
│       │       │   └── DelHomeCommand.java
│       │       └── homes/
│       │           ├── Home.java
│       │           └── HomeManager.java
│       └── resources/
│           ├── config.yml
│           └── plugin.yml
├── pom.xml
└── README.md
```

### 📚 ¿Para qué sirve cada archivo?

| Archivo | Responsabilidad |
|---|---|
| `SimpleHomes.java` | Clase principal del plugin. Inicializa los componentes y registra los comandos y eventos necesarios. |
| `DatabaseManager.java` | Gestiona la conexión y las operaciones de la base de datos SQLite. |
| `HomeTeleportManager.java` | Controla la espera del teletransporte, las cancelaciones y la lógica relacionada con el destino. |
| `Home.java` | Representa un hogar y sus datos, como nombre, mundo, coordenadas, orientación y propietario. |
| `HomeManager.java` | Centraliza la gestión de los hogares, incluidos su registro, consulta, eliminación y renombrado. |
| `SetHomeCommand.java` | Implementa el comando para guardar hogares. |
| `HomeCommand.java` | Implementa el comando para visitar hogares guardados. |
| `DelHomeCommand.java` | Implementa el comando para eliminar hogares. |
| `config.yml` | Define los límites de hogares y el tiempo de espera del teletransporte. |
| `plugin.yml` | Contiene los metadatos del plugin y la declaración de sus comandos y permisos. |
| `pom.xml` | Configura Maven, las dependencias, la compilación y la generación del JAR. |

## 💾 Base de datos

SimpleHomesNK utiliza **SQLite** para conservar los hogares de los jugadores.

Cada hogar almacena información necesaria para identificar al propietario y reconstruir su ubicación, incluyendo:

- UUID y nombre del jugador.
- Nombre del hogar.
- Mundo donde se encuentra.
- Coordenadas X, Y y Z.
- Orientación del jugador: yaw y pitch.
- Marca de tiempo asociada al registro.

Esto permite que los hogares no dependan de que el jugador permanezca conectado. Los datos se guardan en la base de datos del plugin y se mantienen después de reiniciar el servidor.

La configuración de SQLite se integra en el proyecto mediante Maven y el empaquetado del controlador JDBC.

## 🐛 Reportes y sugerencias

¿Encontraste un error o tienes un problema con SimpleHomesNK?

- **Código fuente:** [GitHub — SimpleHomesNK](https://github.com/BrunoJaegerK/SimpleHomesNK)
- **Reportar errores:** utiliza la sección [Issues](https://github.com/BrunoJaegerK/SimpleHomesNK/issues).
- **Revisar versiones publicadas:** visita [Releases](https://github.com/BrunoJaegerK/SimpleHomesNK/releases).

Al reportar un error, incluye la versión del servidor, la versión del plugin, los pasos para reproducirlo y los mensajes relevantes de la consola.

## 📜 Licencia

Consulta la licencia publicada en el repositorio para conocer las condiciones de uso, modificación y distribución del proyecto.

Si el repositorio todavía no contiene un archivo de licencia, no se debe asumir que el proyecto está publicado bajo una licencia de código abierto específica.

---

<p align="center">
  <b>🏡 SimpleHomesNK</b>
</p>

<p align="center">
  Este plugin fue creado para <b>DELUXEMINE NETWORK</b> y su desarrollo depende totalmente de ellos y de su apoyo.
</p>
