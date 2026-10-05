package com.v1kth0rx.totitox.ui.game

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v1kth0rx.totitox.R
import com.v1kth0rx.totitox.domain.Difficulty
import com.v1kth0rx.totitox.domain.GameResult
import com.v1kth0rx.totitox.domain.Player
import com.v1kth0rx.totitox.data.IconStyle

@Composable
fun GameScreen(
    iconStyle: IconStyle,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    GameContent(
        uiState = uiState,
        iconStyle = iconStyle,
        onCellClick = viewModel::onCellClick,
        onDifficultyChange = viewModel::onDifficultyChange,
        onNewGame = viewModel::onNewGame,
        onDismissDialog = viewModel::dismissGameOverDialog,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameUiState,
    iconStyle: IconStyle,
    onCellClick: (Int) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onNewGame: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val boardContent: @Composable (Modifier) -> Unit = { boardModifier ->
        Box(
            modifier = boardModifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val cellPlayer = uiState.board.cells[index]
                            
                            val playerDesc = when (cellPlayer) {
                                Player.X -> stringResource(R.string.a11y_cell_x)
                                Player.O -> stringResource(R.string.a11y_cell_o)
                                null -> stringResource(R.string.a11y_cell_empty)
                            }
                            val a11yDesc = stringResource(R.string.a11y_cell_description, row + 1, col + 1, playerDesc)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = cellPlayer == null && !uiState.isAiThinking && uiState.result is GameResult.InProgress) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onCellClick(index)
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
                    onClick = { onDifficultyChange(diff) },
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

        Button(onClick = onNewGame) {
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
                        onClick = { onDifficultyChange(diff) },
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

            Button(onClick = onNewGame) {
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
            onDismissRequest = onDismissDialog,
            title = { Text(title) },
            text = { Text(stringResource(R.string.game_over)) },
            confirmButton = {
                TextButton(onClick = onNewGame) {
                    Text(stringResource(R.string.rematch))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) {
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
        GameContent(
            uiState = GameUiState(),
            iconStyle = IconStyle.CLASSIC,
            onCellClick = {},
            onDifficultyChange = {},
            onNewGame = {},
            onDismissDialog = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 640, heightDp = 360, name = "Landscape")
@Composable
fun GameScreenLandscapePreview() {
    MaterialTheme {
        GameContent(
            uiState = GameUiState(),
            iconStyle = IconStyle.CLASSIC,
            onCellClick = {},
            onDifficultyChange = {},
            onNewGame = {},
            onDismissDialog = {}
        )
    }
}
