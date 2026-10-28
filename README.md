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

El plugin utiliza Java, Maven, SQLite y FormConstructor para gestionar los hogares, almacenar los datos y mostrar formularios interactivos.

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
- **Java JDK 25** para el entorno de desarrollo y la compilación configurada del proyecto. Comprueba la compatibilidad con la versión de tu servidor antes de actualizar el Java que utiliza en producción.
- **FormConstructor 3.1.0**, utilizado para los formularios interactivos.
- El archivo JAR compilado de SimpleHomesNK.
- **LuckPerms**, si deseas gestionar los límites de hogares mediante rangos y permisos.

### 📥 Enlaces de descarga

| Componente | Versión | Enlace |
|---|---|---|
| NukkitPetteriM1Edition | Según tu servidor | [Repositorio oficial](https://github.com/PetteriM1/NukkitPetteriM1Edition) · [Releases](https://github.com/PetteriM1/NukkitPetteriM1Edition/releases) |
| Java JDK | 25 | [Descargar JDK 25](https://jdk.java.net/25/) |
| FormConstructor | 3.1.0 | [Repositorio en GitHub](https://github.com/ContentForge/FormConstructor) · [Releases](https://github.com/ContentForge/FormConstructor/releases) |
| SQLite JDBC | 3.50.3.0 | [Descargar desde GitHub](https://github.com/xerial/sqlite-jdbc/releases/tag/3.50.3.0) |
| LuckPerms | Versión compatible | [Repositorio oficial](https://github.com/LuckPerms/LuckPerms) |

**Importante:** utiliza la versión `3.1.0` de FormConstructor. Si no aparece entre los archivos de las versiones publicadas, revisa las etiquetas y los archivos del repositorio antes de descargar otra versión.

SQLite JDBC se integra en el JAR compilado mediante Maven. No necesitas instalar ese controlador como un plugin independiente cuando utilizas el JAR final generado correctamente.

###
