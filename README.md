# GreonTrack Admin (Android)

[![Android CI](https://github.com/jdmonteroa/GreonTrackMovil/actions/workflows/android-ci.yml/badge.svg)](https://github.com/jdmonteroa/GreonTrackMovil/actions/workflows/android-ci.yml)

App móvil **de administración** de GreonTrack. La app web es para los usuarios;
esta app es solo para cuentas con `role = 'admin'` en la tabla `perfiles`.

Hecha con Kotlin + Jetpack Compose + supabase-kt. Se conecta a la misma base de
datos de Supabase que la web.

## Pantallas
- **Login**: solo deja entrar a administradores.
- **Panel**: usuarios, activos, dispositivos, kWh, costo, CO₂, consultas a Greon,
  sniffers, y gráficas de los últimos 14 días.
- **Usuarios**: buscador, filtro por rol y detalle de cada usuario (con cambio de rol).
- **Dispositivos**: todos los aparatos registrados, con su dueño.
- **Ajustes**: tarifa por kWh, factor de CO₂, tema (sistema, claro u oscuro) y cerrar sesión.

En celular en vertical se navega con la barra inferior; en horizontal o en tablet
aparece un menú lateral como el de la web y el panel se acomoda en 3 o 4 columnas.

## Cómo ponerla a funcionar

1. **Supabase → SQL Editor**: pega y ejecuta `supabase/admin_setup.sql`.
2. En ese mismo archivo, al final, descomenta la línea `update ... set role = 'admin'`
   con tu correo y ejecútala para volverte admin.
3. **Credenciales**: abre `supabase.properties` (raíz del proyecto) y pega:
   ```
   SUPABASE_URL=https://xxxx.supabase.co
   SUPABASE_ANON_KEY=eyJhbGciOi...
   ```
   Son los mismos valores de `VITE_SUPABASE_URL` y `VITE_SUPABASE_ANON_KEY` del `.env` de la web.
4. Abre la carpeta en Android Studio, espera el *Gradle Sync* y dale ▶ Run.

## Estructura
```
app/src/main/java/com/greontrack/admin/
├── MainActivity.kt          punto de entrada: splash, tema y navegación
├── data/                    capa de datos
│   ├── SupabaseProvider.kt  cliente de Supabase (lee supabase.properties)
│   ├── AdminRepository.kt   llamadas a las funciones RPC
│   ├── Models.kt            modelos de datos
│   └── PreferenciaTema.kt   tema elegido en Ajustes (SharedPreferences)
├── ui/
│   ├── ViewModels.kt        lógica de cada pantalla
│   ├── theme/Theme.kt       colores (claro y oscuro) y tipografías de la web
│   ├── components/          tarjetas, gráficas, skeletons, animaciones,
│   │                        mascota Greon y menú lateral
│   └── screens/             pantallas
└── util/Formato.kt          números y fechas en español

app/src/test/                pruebas unitarias
.github/workflows/           pipeline de GitHub Actions
.github/dependabot.yml       revisión de librerías (Dependabot)
```

## Integración continua (GitHub Actions)
El workflow `android-ci.yml` corre en cada push a `master`, en cada pull request
y a mano desde la pestaña **Actions**:

1. **Pruebas unitarias** (`./gradlew testDebugUnitTest`).
2. **Android Lint** (`./gradlew lintDebug`): análisis estático del código. El
   reporte se publica en **Security → Code scanning**.
3. **APK de depuración** (`./gradlew assembleDebug`): queda para descargar en
   la corrida, junto con los reportes de pruebas y de Lint.

Para que el APK del pipeline se conecte a Supabase, agrega en
**Settings → Secrets and variables → Actions → Variables** las variables
`SUPABASE_URL` y `SUPABASE_ANON_KEY`. Sin ellas compila igual, pero la app abre
en "Falta configurar Supabase".

## Seguridad
- La app solo usa la **anon key**. Los datos de todos los usuarios se piden a
  funciones `security definer` que revisan en el servidor que seas admin.
- `admin_setup.sql` también agrega un trigger para que ningún usuario pueda
  ponerse `admin` a sí mismo desde la web.
- Las credenciales viven en `supabase.properties`, que está en `.gitignore`.
- Toda la comunicación con Supabase va por HTTPS.
- **SAST**: Android Lint revisa el código en cada corrida del pipeline.
- **SCA**: Dependabot revisa las librerías del proyecto. Las actualizaciones
  de versión están apagadas (`open-pull-requests-limit: 0`) para no aceptar
  por error una versión que rompa la app; solo quedan las de seguridad.
