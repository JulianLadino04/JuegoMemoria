package uniquindio.edu.co.juegomemoria.model

data class Card(
    val id: Int,           // Identificador único de cada carta
    val value: String,     // El emoji o símbolo que representa la carta
    val isFlipped: Boolean = false,   // ¿Está volteada?
    val isMatched: Boolean = false    // ¿Ya encontró su pareja?
)