package uniquindio.edu.co.juegomemoria.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uniquindio.edu.co.juegomemoria.ui.screens.GameScreen
import uniquindio.edu.co.juegomemoria.ui.screens.HomeScreen
import uniquindio.edu.co.juegomemoria.ui.screens.ResultScreen
import uniquindio.edu.co.juegomemoria.viewmodel.GameViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // Pantalla 1: Inicio
        composable("home") {
            HomeScreen(
                onStartGame = { playerName ->
                    gameViewModel.resetGame()
                    navController.navigate("game/$playerName")
                }
            )
        }

        // Pantalla 2: Juego
        composable(
            route = "game/{playerName}",
            arguments = listOf(navArgument("playerName") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            GameScreen(
                playerName = playerName,
                onGameFinished = { attempts ->
                    navController.navigate("result/$playerName/$attempts") {
                        popUpTo("game/$playerName") { inclusive = true }
                    }
                },
                gameViewModel = gameViewModel
            )
        }

        // Pantalla 3: Resultado
        composable(
            route = "result/{playerName}/{attempts}",
            arguments = listOf(
                navArgument("playerName") { type = NavType.StringType },
                navArgument("attempts") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            val attempts = backStackEntry.arguments?.getInt("attempts") ?: 0
            ResultScreen(
                playerName = playerName,
                attempts = attempts,
                onPlayAgain = {
                    gameViewModel.resetGame()
                    navController.navigate("game/$playerName") {
                        popUpTo("result/$playerName/$attempts") { inclusive = true }
                    }
                },
                onGoHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}