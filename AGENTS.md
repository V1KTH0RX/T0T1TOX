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

## Flujo de trabajo del agente

### Autoverificación (obligatoria antes de terminar)
1. Relee todo el código que escribiste o modificaste.
2. Busca activamente: errores de lógica, estados mal manejados, fugas de corrutinas, casos borde (tablero lleno, toques repetidos, rotación de pantalla), problemas de accesibilidad y textos hardcodeados fuera de strings.xml.
3. Ejecuta ./gradlew assembleDebug y ./gradlew test. Si JAVA_HOME está vacío, usa el JDK de Android Studio: $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr".
4. Si algo falla, corrígelo y vuelve a ejecutar, hasta 3 intentos, sin pedirme aprobación.
5. Nunca debilites, borres ni comentes un test para que pase. Corrige el código.

### Cuándo NO pedirme aprobación
Leer archivos, crear o editar archivos dentro del proyecto, compilar y correr tests.

### Cuándo SÍ pedirme aprobación
- Borrar archivos o carpetas.
- Cambiar versiones de AGP, Kotlin o compileSdk.
- Añadir dependencias nuevas.
- Tocar archivos fuera del proyecto.
- Comandos de red o que usen mi cuenta de GitHub (git push incluido).

### Reporte final
Al terminar, resume: qué hiciste, qué verificaste, qué riesgos o dudas quedan y el conteo de tests por suite.
