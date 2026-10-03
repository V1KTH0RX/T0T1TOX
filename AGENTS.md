# Reglas para Agentes - App "Totito"

- App "Totito" (tres en raya) en Kotlin + Jetpack Compose, un solo módulo `:app`.
- Arquitectura MVVM con StateFlow y flujo de datos unidireccional.
- UI con Material 3 Expressive (`androidx.compose.material3` en versión alpha).
- Navegación inferior de 3 pestañas: Perfil (izquierda), Juego (centro, inicial), Ajustes (derecha).
- Lógica del juego en paquete `domain`, Kotlin puro, sin imports de Android, con tests JUnit.
- Ajustes persistidos con DataStore Preferences.
- Nunca hardcodear secretos. `google-services.json` y `local.properties` fuera de git.
- Textos en español en `strings.xml`.
- Al terminar cada tarea: ejecutar `./gradlew assembleDebug` y `./gradlew test`.
