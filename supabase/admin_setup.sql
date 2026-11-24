-- =====================================================================
--  GreonTrack Admin (app Android) — funciones para el modo administrador
--  Ejecuta TODO este archivo una sola vez en Supabase > SQL Editor > Run.
--  Es seguro volver a ejecutarlo (usa "create or replace").
--
--  Cómo funciona: la app NO lee las tablas directamente. Llama a estas
--  funciones (RPC), que primero revisan que quien llama tenga
--  perfiles.role = 'admin'. Si no es admin, regresan error 42501.
--  Así no hay que tocar las políticas RLS que ya usa la app web.
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1) ¿El usuario actual es admin?
-- ---------------------------------------------------------------------
create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1 from public.perfiles
    where id = auth.uid() and role = 'admin'
  );
$$;


-- ---------------------------------------------------------------------
-- 2) Números del dashboard
-- ---------------------------------------------------------------------
create or replace function public.admin_dashboard_stats()
returns json
language plpgsql
security definer
set search_path = public
as $$
declare
  v_tz  text := 'America/Mexico_City';
  v_hoy date := (now() at time zone 'America/Mexico_City')::date;
  r     json;
begin
  if not public.is_admin() then
    raise exception 'Acceso solo para administradores' using errcode = '42501';
  end if;

  select json_build_object(
    'usuarios_total',        (select count(*) from perfiles),
    'usuarios_nuevos_7d',    (select count(*) from perfiles where created_at >= now() - interval '7 days'),
    'usuarios_activos_7d',   (select count(*) from auth.users where last_sign_in_at >= now() - interval '7 days'),
    'dispositivos_total',    (select count(*) from dispositivos),
    'dispositivos_agente',   (select count(*) from dispositivos where origen = 'agente'),
    'sugeridos_pendientes',  (select count(*) from dispositivos_sugeridos where estado = 'pendiente'),
    'registros_uso_total',   (select count(*) from registros_uso),
    'recomendaciones_total', (select count(*) from recomendaciones),
    'kwh_total',             (select coalesce(sum(kwh), 0)   from calculos),
    'costo_total',           (select coalesce(sum(costo), 0) from calculos),
    'co2_total',             (select coalesce(sum(co2), 0)   from calculos),
    'kwh_30d',               (select coalesce(sum(kwh), 0)   from calculos where fecha >= now() - interval '30 days'),
    'consultas_greon_total', (select coalesce(sum(greon_consultas_total), 0) from perfiles),
    'sniffers_activos',      (select count(*) from sniffer_estado where ultimo_sondeo >= now() - interval '24 hours'),
    'tarifa_kwh',            (select tarifa_kwh from configuracion where id = 1),
    'factor_co2',            (select factor_co2 from configuracion where id = 1),

    'registros_por_dia', (
      select coalesce(json_agg(json_build_object(
               'dia', g.d::date,
               'total', (select count(*) from perfiles p
                         where (p.created_at at time zone v_tz)::date = g.d::date)
             ) order by g.d), '[]'::json)
      from generate_series((v_hoy - 13)::timestamp, v_hoy::timestamp, interval '1 day') as g(d)
    ),

    'kwh_por_dia', (
      select coalesce(json_agg(json_build_object(
               'dia', g.d::date,
               'total', (select coalesce(sum(c.kwh), 0) from calculos c
                         where (c.fecha at time zone v_tz)::date = g.d::date)
             ) order by g.d), '[]'::json)
      from generate_series((v_hoy - 13)::timestamp, v_hoy::timestamp, interval '1 day') as g(d)
    ),

    'roles', (
      select coalesce(json_agg(json_build_object('nombre', x.role, 'total', x.c) order by x.c desc), '[]'::json)
      from (select role, count(*) as c from perfiles group by role) x
    ),

    'dispositivos_por_tipo', (
      select coalesce(json_agg(json_build_object('nombre', x.tipo, 'total', x.c) order by x.c desc), '[]'::json)
      from (select tipo, count(*) as c from dispositivos group by tipo order by count(*) desc limit 6) x
    )
  ) into r;

  return r;
end;
$$;


-- ---------------------------------------------------------------------
-- 3) Lista de usuarios (con correo, que vive en auth.users)
-- ---------------------------------------------------------------------
create or replace function public.admin_usuarios()
returns table (
  id uuid,
  nombre text,
  email text,
  role text,
  created_at timestamptz,
  ultimo_acceso timestamptz,
  dispositivos bigint,
  kwh_total numeric,
  greon_consultas integer
)
language plpgsql
security definer
set search_path = public
as $$
#variable_conflict use_column
begin
  if not public.is_admin() then
    raise exception 'Acceso solo para administradores' using errcode = '42501';
  end if;

  return query
  select p.id,
         p.nombre,
         u.email::text,
         p.role,
         p.created_at,
         u.last_sign_in_at,
         (select count(*) from dispositivos d where d.usuario_id = p.id),
         (select coalesce(sum(c.kwh), 0) from calculos c where c.usuario_id = p.id),
         p.greon_consultas_total
  from perfiles p
  left join auth.users u on u.id = p.id
  order by p.created_at desc;
end;
$$;


-- ---------------------------------------------------------------------
-- 4) Detalle de un usuario
-- ---------------------------------------------------------------------
create or replace function public.admin_usuario_detalle(p_usuario uuid)
returns json
language plpgsql
security definer
set search_path = public
as $$
declare
  v_tz  text := 'America/Mexico_City';
  v_hoy date := (now() at time zone 'America/Mexico_City')::date;
  r     json;
begin
  if not public.is_admin() then
    raise exception 'Acceso solo para administradores' using errcode = '42501';
  end if;

  select json_build_object(
    'id',                           p.id,
    'nombre',                       p.nombre,
    'email',                        u.email,
    'role',                         p.role,
    'created_at',                   p.created_at,
    'ultimo_acceso',                u.last_sign_in_at,
    'aviso_privacidad_aceptado_en', p.aviso_privacidad_aceptado_en,
    'greon_consultas',              p.greon_consultas_total,
    'kwh_total',   (select coalesce(sum(c.kwh), 0)   from calculos c where c.usuario_id = p.id),
    'costo_total', (select coalesce(sum(c.costo), 0) from calculos c where c.usuario_id = p.id),
    'co2_total',   (select coalesce(sum(c.co2), 0)   from calculos c where c.usuario_id = p.id),
    'registros_uso', (select count(*) from registros_uso ru
                      join dispositivos d on d.id = ru.dispositivo_id
                      where d.usuario_id = p.id),
    'recomendaciones', (select count(*) from recomendaciones rc where rc.usuario_id = p.id),
    'sniffer_ultimo_sondeo', (select s.ultimo_sondeo from sniffer_estado s where s.usuario_id = p.id),

    -- Consumo de este usuario en los ultimos 14 dias (para la mini grafica)
    'kwh_por_dia', (
      select coalesce(json_agg(json_build_object(
               'dia', g.d::date,
               'total', (select coalesce(sum(c.kwh), 0) from calculos c
                         where c.usuario_id = p.id
                           and (c.fecha at time zone v_tz)::date = g.d::date)
             ) order by g.d), '[]'::json)
      from generate_series((v_hoy - 13)::timestamp, v_hoy::timestamp, interval '1 day') as g(d)
    ),

    -- Lugar del usuario entre todos por kWh registrados (1 = el que mas consume)
    'ranking_consumo', (
      select x.pos
      from (
        select q.id, rank() over (order by q.kwh desc) as pos
        from (
          select p2.id,
                 coalesce((select sum(c2.kwh) from calculos c2 where c2.usuario_id = p2.id), 0) as kwh
          from perfiles p2
        ) q
      ) x
      where x.id = p.id
    ),
    'usuarios_total', (select count(*) from perfiles),

    'dispositivos', (
      select coalesce(json_agg(json_build_object(
               'id', d.id,
               'nombre', d.nombre,
               'tipo', d.tipo,
               'consumo_watts_promedio', d.consumo_watts_promedio,
               'origen', d.origen,
               'created_at', d.created_at
             ) order by d.created_at desc), '[]'::json)
      from dispositivos d where d.usuario_id = p.id
    )
  )
  into r
  from perfiles p
  left join auth.users u on u.id = p.id
  where p.id = p_usuario;

  if r is null then
    raise exception 'Usuario no encontrado' using errcode = 'P0002';
  end if;

  return r;
end;
$$;


-- ---------------------------------------------------------------------
-- 5) Todos los dispositivos (con el nombre del dueño)
-- ---------------------------------------------------------------------
create or replace function public.admin_dispositivos()
returns table (
  id uuid,
  nombre text,
  tipo text,
  consumo_watts_promedio numeric,
  origen text,
  created_at timestamptz,
  usuario_id uuid,
  usuario_nombre text,
  kwh_total numeric
)
language plpgsql
security definer
set search_path = public
as $$
#variable_conflict use_column
begin
  if not public.is_admin() then
    raise exception 'Acceso solo para administradores' using errcode = '42501';
  end if;

  return query
  select d.id,
         d.nombre,
         d.tipo,
         d.consumo_watts_promedio,
         d.origen,
         d.created_at,
         d.usuario_id,
         p.nombre,
         (select coalesce(sum(c.kwh), 0) from calculos c where c.dispositivo_id = d.id)
  from dispositivos d
  left join perfiles p on p.id = d.usuario_id
  order by d.created_at desc;
end;
$$;


-- ---------------------------------------------------------------------
-- 6) Cambiar el rol de un usuario
-- ---------------------------------------------------------------------
create or replace function public.admin_cambiar_rol(p_usuario uuid, p_role text)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if not public.is_admin() then
    raise exception 'Acceso solo para administradores' using errcode = '42501';
  end if;

  if p_role not in ('general', 'analitico', 'admin') then
    raise exception 'Rol no válido: %', p_role;
  end if;

  if p_usuario = auth.uid() and p_role <> 'admin' then
    raise exception 'No puedes quitarte a ti mismo el rol de admin';
  end if;

  update perfiles set role = p_role where id = p_usuario;

  if not found then
    raise exception 'Usuario no encontrado' using errcode = 'P0002';
  end if;
end;
$$;


-- ---------------------------------------------------------------------
-- 7) Actualizar tarifa por kWh y factor de CO2
-- ---------------------------------------------------------------------
create or replace function public.admin_actualizar_config(p_tarifa numeric, p_factor numeric)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
  if not public.is_admin() then
    raise exception 'Acceso solo para administradores' using errcode = '42501';
  end if;

  if p_tarifa is null or p_tarifa <= 0 or p_factor is null or p_factor <= 0 then
    raise exception 'La tarifa y el factor deben ser mayores a 0';
  end if;

  insert into configuracion (id, tarifa_kwh, factor_co2, updated_at)
  values (1, p_tarifa, p_factor, now())
  on conflict (id) do update
    set tarifa_kwh = excluded.tarifa_kwh,
        factor_co2 = excluded.factor_co2,
        updated_at = now();
end;
$$;


-- ---------------------------------------------------------------------
-- 8) SEGURIDAD: que nadie se pueda poner "admin" a sí mismo.
--    La política actual de perfiles deja que cada usuario edite su fila
--    completa, incluyendo "role". Este trigger lo impide (solo un admin
--    puede cambiar roles). No afecta a la web: ella nunca cambia el rol.
-- ---------------------------------------------------------------------
create or replace function public.proteger_rol_perfil()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  -- Sin sesión de usuario = SQL Editor, triggers internos o service_role.
  if auth.uid() is null then
    return new;
  end if;

  if tg_op = 'INSERT' then
    if new.role <> 'general' and not public.is_admin() then
      new.role := 'general';
    end if;
  elsif new.role is distinct from old.role and not public.is_admin() then
    raise exception 'Solo un administrador puede cambiar roles' using errcode = '42501';
  end if;

  return new;
end;
$$;

drop trigger if exists trg_proteger_rol on public.perfiles;
create trigger trg_proteger_rol
  before insert or update on public.perfiles
  for each row execute function public.proteger_rol_perfil();


-- ---------------------------------------------------------------------
-- 9) Permisos: solo usuarios con sesión pueden llamar estas funciones
-- ---------------------------------------------------------------------
revoke all on function public.is_admin()                                from public, anon;
revoke all on function public.admin_dashboard_stats()                   from public, anon;
revoke all on function public.admin_usuarios()                          from public, anon;
revoke all on function public.admin_usuario_detalle(uuid)               from public, anon;
revoke all on function public.admin_dispositivos()                      from public, anon;
revoke all on function public.admin_cambiar_rol(uuid, text)             from public, anon;
revoke all on function public.admin_actualizar_config(numeric, numeric) from public, anon;

grant execute on function public.is_admin()                                to authenticated;
grant execute on function public.admin_dashboard_stats()                   to authenticated;
grant execute on function public.admin_usuarios()                          to authenticated;
grant execute on function public.admin_usuario_detalle(uuid)               to authenticated;
grant execute on function public.admin_dispositivos()                      to authenticated;
grant execute on function public.admin_cambiar_rol(uuid, text)             to authenticated;
grant execute on function public.admin_actualizar_config(numeric, numeric) to authenticated;


-- =====================================================================
-- 10) HAZTE ADMIN: cambia el correo por el tuyo y ejecuta esta línea
--     (quita los dos guiones del inicio).
-- =====================================================================
-- update public.perfiles set role = 'admin'
--   where id = (select id from auth.users where email = 'TU_CORREO@ejemplo.com');
