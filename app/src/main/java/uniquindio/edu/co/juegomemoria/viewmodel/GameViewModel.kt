package uniquindio.edu.co.juegomemoria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import uniquindio.edu.co.juegomemoria.model.Card
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    // Lista de emojis (8 pares)
    private val symbols = listOf("🐶","🐱","🐭","🐹","🐰","🦊","🐻","🐼")

    // Estado del tablero
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards

    // Cartas actualmente volteadas (máximo 2)
    private val _flippedCards = MutableStateFlow<List<Int>>(emptyList())
    val flippedCards: StateFlow<List<Int>> = _flippedCards

    // Número de intentos
    private val _attempts = MutableStateFlow(0)
    val attempts: StateFlow<Int> = _attempts

    // Bloquear el tablero mientras se compara un par
    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked

    // Número de parejas encontradas
    private val _matchedPairs = MutableStateFlow(0)
    val matchedPairs: StateFlow<Int> = _matchedPairs

    // ¿El juego terminó?
    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver

    init {
        resetGame()
    }

    fun resetGame() {
        // Duplicar los símbolos y mezclarlos
        val symbolsPairs = (symbols + symbols).shuffled()
        val shuffled = symbolsPairs.mapIndexed { index, symbol -> 
            Card(id = index, value = symbol) 
        }

        _cards.value = shuffled
        _flippedCards.value = emptyList()
        _attempts.value = 0
        _isLocked.value = false
        _matchedPairs.value = 0
        _isGameOver.value = false
    }

    fun onCardClicked(cardId: Int) {
        // Si el tablero está bloqueado, ignorar clicks
        if (_isLocked.value) return

        val currentFlipped = _flippedCards.value

        // Si la carta ya está volteada o encontrada, ignorar
        val card = _cards.value.find { it.id == cardId } ?: return
        if (card.isFlipped || card.isMatched) return

        // No permitir seleccionar la misma carta dos veces seguidas
        if (currentFlipped.contains(cardId)) return

        // Voltear la carta clickeada
        _cards.value = _cards.value.map {
            if (it.id == cardId) it.copy(isFlipped = true) else it
        }

        val newFlipped = currentFlipped + cardId

        if (newFlipped.size == 2) {
            _isLocked.value = true
            _attempts.value += 1
            _flippedCards.value = emptyList()

            viewModelScope.launch {
                delay(1000) // 1 segundo de espera

                val first = _cards.value.find { it.id == newFlipped[0] }
                val second = _cards.value.find { it.id == newFlipped[1] }

                if (first?.value == second?.value) {
                    // ¡Son pareja! Marcarlas como encontradas
                    _cards.value = _cards.value.map {
                        if (it.id == newFlipped[0] || it.id == newFlipped[1])
                            it.copy(isMatched = true)
                        else it
                    }
                    _matchedPairs.value += 1
                } else {
                    // No son pareja, ocultarlas de nuevo
                    _cards.value = _cards.value.map {
                        if (it.id == newFlipped[0] || it.id == newFlipped[1])
                            it.copy(isFlipped = false)
                        else it
                    }
                }

                _isLocked.value = false

                // Verificar si el juego terminó
                if (_cards.value.all { it.isMatched }) {
                    _isGameOver.value = true
                }
            }
        } else {
            _flippedCards.value = newFlipped
        }
    }
}
