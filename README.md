# 🏡 SimpleHomesNK — PowerNukkitX beta

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-Bedrock%20Edition-43A047?style=for-the-badge&logo=minecraft&logoColor=white" alt="Minecraft Bedrock Edition">
  <img src="https://img.shields.io/badge/Server-PowerNukkitX%20Beta-7B1FA2?style=for-the-badge" alt="PowerNukkitX beta">
  <img src="https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 25">
  <img src="https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="SQLite">
</p>

Sistema de hogares con almacenamiento SQLite, permisos de LuckPerms, teletransporte seguro y menús Bedrock. Esta rama usa la API nativa actual de PowerNukkitX; no requiere el plugin externo FormConstructor.

> Esta es una rama de port independiente. La rama `main` conserva la versión original para NukkitPetteriM1Edition.

## ✨ Funciones

- 🏠 Crear, visitar, listar y eliminar hogares.
- 📝 Renombrar hogares desde un formulario nativo de PowerNukkitX.
- 💾 Guardado persistente en SQLite, asociado al UUID del jugador.
- 🔐 Límites configurables mediante permisos, compatibles con LuckPerms.
- ♾️ Hogares ilimitados para operadores, si está habilitado.
- ⏳ Cuenta regresiva de teletransporte configurable.
- 🛑 Cancelación del teletransporte por movimiento o daño.
- 🧭 Búsqueda de una ubicación segura cerca del destino.
- 🌎 Restricción opcional de mundos para crear hogares.
- 🗣️ Mensajes y textos editables en español.

## 📋 Comandos

| Comando | Función |
|---|---|
| `/sethome [nombre]` | Guarda un hogar. Sin nombre usa `casa`. |
| `/home [nombre]` | Visita el hogar indicado; sin nombre usa `casa`. |
| `/delhome <nombre>` | Elimina un hogar. |
| `/homes` | Abre el menú gráfico de hogares. |

## 🔐 Permisos

| Permiso | Descripción |
|---|---|
| `simplehomes.command.homes` | Acceso a `/homes`. |
| `simplehomes.command.sethome` | Acceso a `/sethome`. |
| `simplehomes.command.home` | Acceso a `/home`. |
| `simplehomes.command.delhome` | Acceso a `/delhome`. |
| `simplehomes.limit.3` | Límite configurado de 3 hogares. |
| `simplehomes.limit.5` | Límite configurado de 5 hogares. |
| `simplehomes.limit.7` | Límite configurado de 7 hogares. |
| `simplehomes.limit.10` | Límite configurado de 10 hogares. |

Los límites se configuran en `home-limits`. El plugin consulta `Player.hasPermission(...)`, por lo que LuckPerms puede gestionar los permisos sin una dependencia directa con sus clases internas.

## ⚙️ Configuración

Archivo: `plugins/SimpleHomesNk/config.yml`

```yaml
settings:
  op-unlimited: true
  default-limit: 3
  teleport-delay: 3
  teleport-cancel-on-move: true
  teleport-cancel-on-damage: true

allowed-worlds: []

home-limits:
  simplehomes.limit.3: 3
  simplehomes.limit.5: 5
  simplehomes.limit.7: 7
  simplehomes.limit.10: 10
```

- `op-unlimited`: hogares ilimitados para operadores.
- `default-limit`: límite predeterminado.
- `teleport-delay`: espera en segundos; `0` desactiva la espera.
- `teleport-cancel-on-move`: cancela si el jugador se desplaza.
- `teleport-cancel-on-damage`: cancela si recibe daño.
- `allowed-worlds`: lista de mundos permitidos para crear hogares. Vacía significa todos.
- `home-limits`: relación entre permisos y cantidad máxima de hogares.

Los mensajes se editan en `lang.yml`.

## 📦 Requisitos

- PowerNukkitX compilado desde la rama beta actual.
- Java 25.
- LuckPerms es opcional; se recomienda para administrar rangos y límites.

Los formularios usan `org.powernukkitx.form.window` y los componentes nativos de PNX. No instales FormConstructor por separado.

## 🛠️ Compilar

Necesitas JDK 25 y Maven.

```bash
git clone https://github.com/BrunoJaegerK/SimpleHomesNK.git
cd SimpleHomesNK
git checkout port/powernukkitx
mvn -U clean package
```

El JAR se genera en:

```text
target/SimpleHomes-PNX.jar
```

PowerNukkitX se declara con alcance `provided`; SQLite se empaqueta dentro del JAR.

## 🧪 Estado

- [x] Rama separada desde `main`.
- [x] Dependencia Maven configurada para PowerNukkitX beta.
- [x] Migración de paquetes `cn.nukkit` a `org.powernukkitx`.
- [x] Formularios convertidos a la API nativa de PNX.
- [x] Compatibilidad de bloques actualizada a identificadores string.
- [x] Restricción de mundos aplicada al crear hogares.
- [ ] Compilación verde de GitHub Actions.
- [ ] Pruebas funcionales en un servidor PowerNukkitX beta.

El port no se considera terminado hasta que compile y se pruebe dentro de un servidor.

## 🐛 Reportar errores

Abre un [Issue](https://github.com/BrunoJaegerK/SimpleHomesNK/issues) e incluye el commit de PowerNukkitX beta, la versión de Java, el error de consola y los pasos para reproducirlo.

## 📜 Licencia

Consulta el archivo [LICENSE](LICENSE).
