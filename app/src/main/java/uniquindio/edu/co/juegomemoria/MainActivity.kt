package uniquindio.edu.co.juegomemoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uniquindio.edu.co.juegomemoria.navigation.AppNavigation
import uniquindio.edu.co.juegomemoria.ui.theme.JuegoMemoriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JuegoMemoriaTheme {
                AppNavigation()
            }
        }
    }
}