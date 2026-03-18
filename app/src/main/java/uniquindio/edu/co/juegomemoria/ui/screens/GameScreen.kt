package uniquindio.edu.co.juegomemoria.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import uniquindio.edu.co.juegomemoria.model.Card
import uniquindio.edu.co.juegomemoria.viewmodel.GameViewModel

@Composable
fun GameScreen(
    playerName: String,
    onGameFinished: (Int) -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val cards by gameViewModel.cards.collectAsStateWithLifecycle()
    val attempts by gameViewModel.attempts.collectAsStateWithLifecycle()
    val isGameOver by gameViewModel.isGameOver.collectAsStateWithLifecycle()

    // Cuando el juego termina, navegar a resultados
    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            onGameFinished(attempts)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🧠 Juego de Memoria",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Jugador: $playerName",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Intentos: $attempts",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cards, key = { it.id }) { card ->
                CardItem(
                    card = card,
                    onClick = { gameViewModel.onCardClicked(card.id) }
                )
            }
        }
    }
}

@Composable
fun CardItem(card: Card, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    val isShowingFront = rotation > 90f

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer { rotationY = rotation }
            .background(
                color = when {
                    card.isMatched -> Color(0xFF81C784)  // Verde si ya encontró pareja
                    isShowingFront -> Color(0xFFFFF9C4)  // Amarillo claro si está volteada
                    else -> Color(0xFF1565C0)             // Azul si está oculta
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !card.isFlipped && !card.isMatched) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isShowingFront) {
            Text(
                text = card.value,
                fontSize = 28.sp,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        }
    }
}