# GreonTrack Admin (Android)

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
- **Ajustes**: tarifa por kWh, factor de CO₂ y cerrar sesión.

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
├── MainActivity.kt          navegación y barra inferior
├── data/
│   ├── SupabaseProvider.kt  cliente de Supabase (lee supabase.properties)
│   ├── AdminRepository.kt   llamadas a las funciones RPC
│   └── Models.kt            modelos de datos
├── ui/
│   ├── ViewModels.kt
│   ├── theme/Theme.kt       colores de la web
│   ├── components/          tarjetas, gráficas, estados
│   └── screens/             pantallas
└── util/Formato.kt          números y fechas en español
```

## Seguridad
- La app solo usa la **anon key**. Los datos de todos los usuarios se piden a
  funciones `security definer` que revisan en el servidor que seas admin.
- `admin_setup.sql` también agrega un trigger para que ningún usuario pueda
  ponerse `admin` a sí mismo desde la web.
