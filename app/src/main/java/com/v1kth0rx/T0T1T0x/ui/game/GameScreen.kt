package com.v1kth0rx.T0T1T0x.ui.game

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.v1kth0rx.T0T1T0x.R
import com.v1kth0rx.T0T1T0x.domain.Difficulty
import com.v1kth0rx.T0T1T0x.domain.GameResult
import com.v1kth0rx.T0T1T0x.domain.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    iconStyle: IconStyle = IconStyle.CLASSIC,
    viewModel: GameViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity,
        factory = GameViewModel.Factory
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Feedback al terminar partida
    LaunchedEffect(uiState.result) {
        if (uiState.result !is GameResult.InProgress) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val boardContent: @Composable (Modifier) -> Unit = { boardModifier ->
        Box(
            modifier = boardModifier
                .defaultMinSize(minWidth = 144.dp, minHeight = 144.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0 until 3) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (col in 0 until 3) {
                            val index = row * 3 + col
                            val cellPlayer = uiState.board.cells[index]
                            
                            val emptyDesc = stringResource(R.string.a11y_cell_empty)
                            val xDesc = stringResource(R.string.a11y_cell_x)
                            val oDesc = stringResource(R.string.a11y_cell_o)
                            val a11yDesc = stringResource(
                                R.string.a11y_cell_description,
                                row + 1, col + 1,
                                when (cellPlayer) {
                                    Player.X -> xDesc
                                    Player.O -> oDesc
                                    null -> emptyDesc
                                }
                            )

                            // Verifica si esta celda es parte de la línea ganadora
                            val isWinningCell = (uiState.result as? GameResult.Win)?.line?.contains(index) == true

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isWinningCell) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.background
                                    )
                                    .clickable(enabled = cellPlayer == null && !uiState.isAiThinking && uiState.result is GameResult.InProgress) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.onCellClick(index)
                                    }
                                    .semantics { contentDescription = a11yDesc },
                                contentAlignment = Alignment.Center
                            ) {
                                this@Row.AnimatedVisibility(
                                    visible = cellPlayer != null,
                                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                                ) {
                                    PlayerMark(
                                        player = cellPlayer,
                                        style = iconStyle,
                                        color = if (cellPlayer == uiState.humanPlayer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val controlsContent: @Composable () -> Unit = {
        // Score
        Text(
            text = stringResource(
                id = R.string.score_format,
                uiState.scoreWins,
                uiState.scoreLosses,
                uiState.scoreDraws
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dificultad
        val difficulties = listOf(Difficulty.BEGINNER, Difficulty.MEDIUM, Difficulty.EXPERT)
        val difficultyStrings = listOf(
            stringResource(R.string.difficulty_beginner),
            stringResource(R.string.difficulty_medium),
            stringResource(R.string.difficulty_expert)
        )
        SingleChoiceSegmentedButtonRow {
            difficulties.forEachIndexed { index, diff ->
                SegmentedButton(
                    selected = uiState.difficulty == diff,
                    onClick = { viewModel.onDifficultyChange(diff) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = difficulties.size)
                ) {
                    Text(difficultyStrings[index])
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Turn indicator
        val turnText = if (uiState.isAiThinking) stringResource(R.string.ai_thinking) else stringResource(R.string.your_turn)
        Text(
            text = turnText,
            style = MaterialTheme.typography.titleLarge,
            color = if (uiState.isAiThinking) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.onNewGame() }) {
            Text(stringResource(R.string.new_game))
        }
    }

    if (isLandscape) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                boardContent(
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                controlsContent()
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Replicating the exact original layout for portrait
            // Score
            Text(
                text = stringResource(
                    id = R.string.score_format,
                    uiState.scoreWins,
                    uiState.scoreLosses,
                    uiState.scoreDraws
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dificultad
            val difficulties = listOf(Difficulty.BEGINNER, Difficulty.MEDIUM, Difficulty.EXPERT)
            val difficultyStrings = listOf(
                stringResource(R.string.difficulty_beginner),
                stringResource(R.string.difficulty_medium),
                stringResource(R.string.difficulty_expert)
            )
            SingleChoiceSegmentedButtonRow {
                difficulties.forEachIndexed { index, diff ->
                    SegmentedButton(
                        selected = uiState.difficulty == diff,
                        onClick = { viewModel.onDifficultyChange(diff) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = difficulties.size)
                    ) {
                        Text(difficultyStrings[index])
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Turn indicator
            val turnText = if (uiState.isAiThinking) stringResource(R.string.ai_thinking) else stringResource(R.string.your_turn)
            Text(
                text = turnText,
                style = MaterialTheme.typography.titleLarge,
                color = if (uiState.isAiThinking) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(0.9f),
                contentAlignment = Alignment.Center
            ) {
                boardContent(Modifier.fillMaxWidth().aspectRatio(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { viewModel.onNewGame() }) {
                Text(stringResource(R.string.new_game))
            }
        }
    }

    if (uiState.isGameOverDialogVisible && uiState.result !is GameResult.InProgress) {
        val title = when (uiState.result) {
            is GameResult.Win -> if ((uiState.result as GameResult.Win).player == uiState.humanPlayer) stringResource(R.string.game_over_win) else stringResource(R.string.game_over_lose)
            GameResult.Draw -> stringResource(R.string.game_over_draw)
            else -> ""
        }
        AlertDialog(
            onDismissRequest = { viewModel.dismissGameOverDialog() },
            title = { Text(title) },
            text = { Text(stringResource(R.string.game_over)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onNewGame() }) {
                    Text(stringResource(R.string.rematch))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissGameOverDialog() }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Portrait")
@Composable
fun GameScreenPortraitPreview() {
    MaterialTheme {
        GameScreen(viewModel = GameViewModel())
    }
}

@Preview(showBackground = true, widthDp = 640, heightDp = 360, name = "Landscape")
@Composable
fun GameScreenLandscapePreview() {
    MaterialTheme {
        GameScreen(viewModel = GameViewModel())
    }
}
