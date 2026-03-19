# Reflexión sobre el uso de IA - Actividad Juego de Memoria

---

## Preguntas de Reflexión

### ¿Qué prompts utilizó para solicitar ayuda a la IA?

Durante el desarrollo del proyecto Juego de Memoria, utilicé los siguientes prompts principales:

1. **Prompt de Arquitectura Inicial**:
   *"Actúa como un desarrollador senior experto en Android (Kotlin + Jetpack Compose), arquitectura MVVM y buenas prácticas de desarrollo móvil. Necesito crear un juego de memoria con las siguientes especificaciones: tablero 4x4 con 8 parejas, máximo 2 cartas volteadas, retardo de 1 segundo para cartas incorrectas, tres pantallas (inicio, juego, resultados), manejo de nombre de jugador y conteo de intentos. Diseña la arquitectura completa con MVVM y proporciona el código base para todas las clases."*

2. **Prompt de Corrección de Bugs**:
   *"Tengo un bug en mi GameViewModel donde los IDs de las cartas no corresponden a sus posiciones después de hacer shuffle. El código actual es: `val shuffled = (symbols + symbols).mapIndexed { index, symbol -> Card(id = index, value = symbol) }.shuffled()`. Además, necesito implementar la validación para no permitir seleccionar la misma carta dos veces seguidas. Ayúdame a corregir estos problemas manteniendo la arquitectura MVVM."*

3. **Prompt de Mejoras de UI/UX**:
   *"Quiero mejorar la interfaz de mi juego de memoria agregando: animaciones más fluidas al voltear cartas, indicador visual de cuántas parejas faltan por encontrar, sistema de calificación con estrellas según los intentos, y mejor accesibilidad con contentDescription. Mantén el código limpio y siguiendo las mejores prácticas de Compose."*

### ¿Qué sugerencias o respuestas de la IA fueron útiles para el desarrollo del proyecto?

Las sugerencias más útiles proporcionadas por la IA fueron:

1. **Estructura MVVM Sólida**: La IA me guió para implementar una separación clara de responsabilidades con:
   - Modelo `Card.kt` con data class inmutable
   - `GameViewModel` con StateFlow para gestión reactiva
   - Screens separadas para cada pantalla

2. **Manejo de Estado Reactivo**: El uso de `StateFlow` y `collectAsStateWithLifecycle()` fue crucial para mantener la UI sincronizada con el estado del juego.

3. **Navegación Compose**: La implementación de Navigation Compose con paso de parámetros entre pantallas fue sugerida y explicada detalladamente.

4. **Corrutinas para Delay**: El uso de `viewModelScope.launch` con `delay(1000)` para implementar el retardo de 1 segundo fue una solución elegante.

5. **Animaciones de Volteo**: La sugerencia de usar `animateFloatAsState` con `rotationY` creó una experiencia visual fluida.

### ¿Qué aspectos del código tuvo que corregir o modificar manualmente?

A pesar de la excelente ayuda de la IA, tuve que realizar varias correcciones manuales:

1. **Bug Crítico de IDs**: La IA inicialmente me proporcionó un código con IDs inconsistentes después del shuffle. Tuve que analizar y corregir manualmente la lógica de generación de cartas.

2. **Validación de Reglas**: Implementé manualmente la validación para evitar seleccionar la misma carta dos veces, ya que la solución inicial de la IA no contemplaba este requisito específico.

3. **Optimización de Estado**: Tuve que exponer `flippedCards` como StateFlow para mejor seguimiento del estado en la UI.

4. **Ajustes de UI**: Realicé ajustes manuales en los espaciados, colores y tamaños para mejorar la experiencia visual en diferentes dispositivos.

5. **Manejo de Edge Cases**: Agregué validaciones para nombres vacíos, manejo de rotación de pantalla y limpieza del back stack de navegación.

### ¿Qué aprendió del proceso de trabajar con IA?

El proceso de trabajar con IA me enseñó lecciones valiosas:

1. **La IA es un acelerador, no un reemplazo**: La IA proporcionó estructuras y patrones rápidamente, pero el entendimiento profundo y la corrección de bugs requirieron mi intervención.

2. **Importancia del Prompt Engineering**: Aprendí que la calidad y especificidad de los prompts mejoran la calidad de las respuestas.

3. **Pensamiento Crítico Esencial**: No todo lo que genera la IA es correcto. Desarrollé un escepticismo saludable y la habilidad de revisar críticamente el código generado.

4. **Aprendizaje Acelerado**: La IA me expuso a patrones y buenas prácticas que habría tardado más en descubrir por mi cuenta.

5. **Iteración y Refinamiento**: El proceso no fue lineal. Tuve que iterar múltiples veces, refinando tanto los prompts como el código resultante.

### ¿Qué errores aparecieron y cómo los solucionó?

#### **Error 1: IDs Inconsistentes después de Shuffle**
- **Problema**: Las cartas tenían IDs que no coincidían con sus posiciones después de mezclarlas
- **Síntomas**: Comportamiento errático al voltear cartas
- **Solución**: Reescribí la lógica para generar IDs después del shuffle:
  ```kotlin
  val symbolsPairs = (symbols + symbols).shuffled()
  val shuffled = symbolsPairs.mapIndexed { index, symbol -> 
      Card(id = index, value = symbol) 
  }
  ```

#### **Error 2: Estado No Reactivo Completo**
- **Problema**: `_flippedCards` no era expuesto como StateFlow
- **Síntomas**: Dificultad para seguir el estado actual en la UI
- **Solución**: Agregué `val flippedCards: StateFlow<List<Int>> = _flippedCards`

#### **Error 3: Falta de Validación de Misma Carta**
- **Problema**: Se podía hacer clic en la misma carta ya volteada
- **Síntomas**: Violación de las reglas del juego
- **Solución**: Agregué validación en `onCardClicked()`:
  ```kotlin
  if (_flippedCards.value.contains(cardId)) {
      return // No permitir seleccionar la misma carta
  }
  ```

#### **Error 4: Memory Leaks en Corrutinas**
- **Problema**: Corrutinas potenciales no canceladas
- **Síntomas**: Comportamiento inesperado al rotar pantalla
- **Solución**: Uso correcto de `viewModelScope` y `collectAsStateWithLifecycle`