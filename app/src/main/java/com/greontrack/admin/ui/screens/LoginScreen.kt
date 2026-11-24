package com.greontrack.admin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greontrack.admin.R
import com.greontrack.admin.ui.components.AnimoGreon
import com.greontrack.admin.ui.components.MascotaGreon
import com.greontrack.admin.ui.theme.Greon
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    enviando: Boolean,
    error: String?,
    alEntrar: (correo: String, contrasena: String) -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var verContrasena by rememberSaveable { mutableStateOf(false) }
    val foco = LocalFocusManager.current

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Greon.VerdeOscuro, Greon.VerdeOscuro2)))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo y nombre en una fila
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logo_greon),
                    contentDescription = "Logo de GreonTrack",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("GreonTrack", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            }
            Spacer(Modifier.height(12.dp))

            // Greon saluda (y se pone triste si algo sale mal), como en la web
            SaludoGreon(triste = error != null)
            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Greon.VerdeMedio.copy(alpha = 0.22f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Icon(
                    Icons.Rounded.AdminPanelSettings,
                    contentDescription = null,
                    tint = Greon.VerdeClaro,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Panel de administración", style = MaterialTheme.typography.labelMedium, color = Greon.VerdeClaro)
            }
            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Greon.Superficie),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                // En tablet u horizontal la tarjeta no pasa de 460 dp para que no se vea estirada
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(22.dp)) {
                    Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall, color = Greon.TextoTitulo)
                    Text(
                        "Usa tu cuenta de GreonTrack con rol de administrador.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Greon.TextoSuave
                    )
                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { verContrasena = !verContrasena }) {
                                Icon(
                                    if (verContrasena) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = if (verContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            foco.clearFocus()
                            alEntrar(correo, contrasena)
                        }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error != null) {
                        Spacer(Modifier.height(14.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Greon.ErrorBg)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = Greon.Error)
                            Spacer(Modifier.width(10.dp))
                            Text(error, color = Greon.Error, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            foco.clearFocus()
                            alEntrar(correo, contrasena)
                        },
                        enabled = !enviando,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Greon.Verde),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (enviando) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text("Entrar al panel")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Monitorea · Ahorra · Impacta",
                style = MaterialTheme.typography.labelMedium,
                color = Greon.VerdeClaro.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Frases de Greon en el login (las mismas de la web, AuthLayout.tsx). */
private val SALUDOS = listOf(
    "¡Hola! Soy Greon 🌱",
    "Juntos cuidamos el planeta, un watt a la vez.",
    "¡Qué gusto verte por aquí!",
    "Pequeñas acciones, grandes cambios 💚"
)

/**
 * Greon con un globito de texto arriba. Saluda solo al abrir la pantalla
 * y cambia de frase cada vez que lo tocas. Si hubo un error, se pone triste.
 */
@Composable
private fun SaludoGreon(triste: Boolean) {
    var saludo by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        delay(600.milliseconds)
        if (saludo == null) saludo = SALUDOS.first()
    }
    val texto = if (triste) "¡Uy! Algo salió mal. Revisa el mensaje de abajo." else saludo

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GloboAnimado(texto)
        MascotaGreon(
            ancho = 150.dp,
            animo = if (triste) AnimoGreon.Triste else AnimoGreon.Feliz,
            descripcion = "Greon, la mascota de GreonTrack. Tócalo para que te salude",
            alTocar = { saludo = SALUDOS.filter { it != saludo }.random() }
        )
    }
}

/**
 * Globito que aparece con animacion. Va en su propia funcion para que
 * Kotlin use el AnimatedVisibility normal y no el de ColumnScope.
 */
@Composable
private fun GloboAnimado(texto: String?) {
    // Espacio reservado para el globito, para que la pantalla no "brinque" al aparecer
    Box(Modifier.heightIn(min = 58.dp), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = texto != null,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut()
        ) {
            GloboTexto(texto ?: "")
        }
    }
}

@Composable
private fun GloboTexto(texto: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Greon.VerdeOscuro,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
        // Piquito del globo apuntando hacia Greon
        Box(
            Modifier
                .offset(y = (-6).dp)
                .size(12.dp)
                .rotate(45f)
                .background(Color.White)
        )
    }
}

/** Se muestra si falta llenar supabase.properties. */
@Composable
fun SinConfigurarScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Greon.VerdeOscuro)
            .systemBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Greon.Superficie)
        ) {
            Column(Modifier.padding(22.dp)) {
                MascotaGreon(ancho = 110.dp, animo = AnimoGreon.Triste, flotar = false)
                Spacer(Modifier.height(10.dp))
                Text("Falta configurar Supabase", style = MaterialTheme.typography.headlineSmall, color = Greon.TextoTitulo)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Abre el archivo supabase.properties (en la carpeta raíz del proyecto) y " +
                            "pega tu SUPABASE_URL y tu SUPABASE_ANON_KEY. Son los mismos valores del " +
                            ".env de la app web.\n\nDespués haz File > Sync Project with Gradle Files " +
                            "y vuelve a ejecutar la app.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Greon.Texto
                )
            }
        }
    }
}