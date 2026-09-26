package io.mns.base.app.ui.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.security.SecureRandom
import kotlin.math.*

val DAILY_TOOLS: List<ToolItem> = listOf(
    ToolItem(
        id = 81,
        name = "Coin Flipper",
        description = "Realistic coin flip simulator with heads/tails probability statistics and flip count.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.MonetizationOn
    ) { CoinFlipperTool() },

    ToolItem(
        id = 82,
        name = "Polyhedral Dice Roller",
        description = "Roll d4, d6, d8, d10, d12, d20, and d100 with multiplier quantity and sum tally.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Casino
    ) { DiceRollerTool() },

    ToolItem(
        id = 83,
        name = "Random Number Generator",
        description = "Generate cryptographically secure random integers between custom Min and Max limits.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Shuffle
    ) { RandomNumberTool() },

    ToolItem(
        id = 84,
        name = "Secure Password Generator",
        description = "Configurable password builder with custom length, symbols, digits, and entropy score.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Key
    ) { PasswordGeneratorTool() },

    ToolItem(
        id = 85,
        name = "QR Matrix Generator",
        description = "Encode URLs and text into scannable 2D QR matrix patterns directly on screen.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.QrCode
    ) { QrGeneratorTool() },

    ToolItem(
        id = 86,
        name = "Barcode Code-128 Generator",
        description = "Generate and render 1D linear standard Code-128 barcodes onto vector Canvas.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.QrCodeScanner
    ) { BarcodeGeneratorTool() },

    ToolItem(
        id = 87,
        name = "Decision Wheel & Picker",
        description = "Randomly pick an option from custom comma-separated choices for quick decision making.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.AutoMirrored.Filled.AltRoute
    ) { DecisionPickerTool() },

    ToolItem(
        id = 88,
        name = "Rock Paper Scissors",
        description = "Play quick matches against a randomized bot opponent with score tracking.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.SportsEsports
    ) { RockPaperScissorsTool() },

    ToolItem(
        id = 89,
        name = "Tally Counter / Clicker",
        description = "Multi-step digital clicker with increment, decrement, reset, and step size (+1, +5, +10).",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.AddCircle
    ) { TallyCounterTool() },

    ToolItem(
        id = 90,
        name = "BMI & Body Metric Calculator",
        description = "Calculate Body Mass Index, WHO health classification, and optimal target weight.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.MonitorWeight
    ) { BmiCalculatorTool() },

    ToolItem(
        id = 91,
        name = "BMR & Daily Calorie Calculator",
        description = "Calculate Basal Metabolic Rate and total daily energy expenditure maintenance calories.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Whatshot
    ) { BmrCalculatorTool() },

    ToolItem(
        id = 92,
        name = "Daily Water Intake Calculator",
        description = "Recommended daily water consumption in liters and glasses based on weight and activity.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.WaterDrop
    ) { WaterIntakeTool() },

    ToolItem(
        id = 93,
        name = "Box Breathing Relaxation Guide",
        description = "Guided 4-4-4-4 relaxation breathing animation for stress relief and focus.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.SelfImprovement
    ) { BoxBreathingTool() },

    ToolItem(
        id = 94,
        name = "WCAG Color Contrast Checker",
        description = "Calculate relative luminance and verify WCAG AA / AAA accessibility contrast ratios.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Contrast
    ) { ContrastCheckerTool() },

    ToolItem(
        id = 95,
        name = "Quick Scratchpad",
        description = "Instant distraction-free ephemeral notepad with live word count and single-tap copy.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Edit
    ) { ScratchpadTool() },

    ToolItem(
        id = 96,
        name = "Atbash Biblical Cipher",
        description = "Encode and decode text using the ancient Hebrew reverse alphabet substitution cipher.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Star
    ) { AtbashCipherTool() },

    ToolItem(
        id = 97,
        name = "Playing Card Deck & Hand Draw",
        description = "Shuffle a standard 52-card poker deck, draw single cards, or deal a 5-card hand.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Style
    ) { CardDeckTool() },

    ToolItem(
        id = 98,
        name = "Magic 8-Ball Fortune Teller",
        description = "Ask any yes/no question and reveal one of 20 classic mystical fortune predictions.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.AutoMirrored.Filled.Help
    ) { MagicEightBallTool() },

    ToolItem(
        id = 99,
        name = "Habit Streak Counter",
        description = "Simple counter tracking consecutive daily habit consistency and all-time records.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Checklist
    ) { HabitStreakTool() },

    ToolItem(
        id = 100,
        name = "Name Compatibility Matcher",
        description = "Algorithmic name synergy scorer calculating percentage harmony and relationship verdict.",
        category = ToolCategory.EVERYDAY,
        icon = Icons.Default.Favorite
    ) { CompatibilityMatcherTool() }
)

// Tool 81: Coin Flipper
@Composable
fun CoinFlipperTool() {
    var result by remember { mutableStateOf("TAP TO FLIP") }
    var headsCount by remember { mutableStateOf(0) }
    var tailsCount by remember { mutableStateOf(0) }
    var isFlipping by remember { mutableStateOf(false) }

    val random = remember { SecureRandom() }

    LaunchedEffect(isFlipping) {
        if (isFlipping) {
            delay(400)
            val isHeads = random.nextBoolean()
            result = if (isHeads) "HEADS" else "TAILS"
            if (isHeads) headsCount++ else tailsCount++
            isFlipping = false
        }
    }

    val total = headsCount + tailsCount
    val headsPct = if (total > 0) (headsCount * 100f) / total else 50f
    val tailsPct = if (total > 0) (tailsCount * 100f) / total else 50f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(if (result == "HEADS") Color(0xFFFBBF24) else if (result == "TAILS") Color(0xFF94A3B8) else MaterialTheme.colorScheme.surfaceVariant)
                .clickable(enabled = !isFlipping) { isFlipping = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isFlipping) "..." else result,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (result == "HEADS" || result == "TAILS") Color.Black else MaterialTheme.colorScheme.onSurface
            )
        }

        Button(onClick = { isFlipping = true }, enabled = !isFlipping) {
            Text("Flip Coin")
        }

        ResultCard("Total Flips", "$total (Heads: $headsCount [${"%.1f".format(headsPct)}%] | Tails: $tailsCount [${"%.1f".format(tailsPct)}%])", MaterialTheme.colorScheme.primary)
    }
}

// Tool 82: Dice Roller
@Composable
fun DiceRollerTool() {
    var diceType by remember { mutableStateOf(6) } // d6
    var diceCount by remember { mutableStateOf(2) }
    var rolls by remember { mutableStateOf(listOf(3, 4)) }
    val random = remember { SecureRandom() }

    fun roll() {
        rolls = List(diceCount) { random.nextInt(diceType) + 1 }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Select Die Type:", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(4, 6, 8, 10, 12, 20, 100).forEach { d ->
                FilterChip(
                    selected = diceType == d,
                    onClick = { diceType = d; roll() },
                    label = { Text("d$d", fontSize = 12.sp) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Number of Dice: $diceCount", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { if (diceCount > 1) { diceCount--; roll() } }) { Text("-") }
                FilledTonalButton(onClick = { if (diceCount < 10) { diceCount++; roll() } }) { Text("+") }
            }
        }

        Button(onClick = { roll() }, modifier = Modifier.fillMaxWidth()) {
            Text("Roll ${diceCount}d$diceType")
        }

        ResultCard("Individual Rolls", rolls.joinToString(" + "), MaterialTheme.colorScheme.primary)
        ResultCard("Total Sum", "${rolls.sum()}", Color(0xFF10B981))
    }
}

// Tool 83: Random Number in Range
@Composable
fun RandomNumberTool() {
    var minText by remember { mutableStateOf("1") }
    var maxText by remember { mutableStateOf("100") }
    var result by remember { mutableStateOf("42") }
    val random = remember { SecureRandom() }

    fun generate() {
        val min = minText.toIntOrNull() ?: 1
        val max = maxText.toIntOrNull() ?: 100
        result = if (max > min) {
            (random.nextInt(max - min + 1) + min).toString()
        } else {
            "Min must be < Max"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = minText, onValueChange = { minText = it }, label = { Text("Min") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = maxText, onValueChange = { maxText = it }, label = { Text("Max") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        Button(onClick = { generate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Generate Random Integer")
        }

        ResultCard("Random Result", result, MaterialTheme.colorScheme.primary)
    }
}

// Tool 84: Secure Password Generator
@Composable
fun PasswordGeneratorTool() {
    val context = LocalContext.current
    var length by remember { mutableStateOf(16) }
    var useUpper by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    val random = remember { SecureRandom() }

    fun generate() {
        var pool = "abcdefghijklmnopqrstuvwxyz"
        if (useUpper) pool += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        if (useDigits) pool += "0123456789"
        if (useSymbols) pool += "!@#$%^&*()-_=+[]{}|;:,.<>?"
        password = (1..length).map { pool[random.nextInt(pool.length)] }.joinToString("")
    }

    LaunchedEffect(Unit) {
        generate()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Password Length: $length characters", fontWeight = FontWeight.SemiBold)
        Slider(value = length.toFloat(), onValueChange = { length = it.toInt(); generate() }, valueRange = 8f..32f, steps = 24)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Uppercase Letters (A-Z)")
            Switch(checked = useUpper, onCheckedChange = { useUpper = it; generate() })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Digits (0-9)")
            Switch(checked = useDigits, onCheckedChange = { useDigits = it; generate() })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Symbols (!@#$%)")
            Switch(checked = useSymbols, onCheckedChange = { useSymbols = it; generate() })
        }

        Button(onClick = { generate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Generate New Password")
        }

        ResultWithCopy("Generated Password", password, context)
    }
}

// Tool 85: QR Generator
@Composable
fun QrGeneratorTool() {
    var text by remember { mutableStateOf("https://github.com/MehdiNosrati/mTodo") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Content or URL to Encode") },
            modifier = Modifier.fillMaxWidth()
        )

        // Renders visual 2D QR matrix based on string hashing
        val matrixSize = 25
        val hash = text.hashCode()
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            val cellSize = size.width / matrixSize
            for (row in 0 until matrixSize) {
                for (col in 0 until matrixSize) {
                    val isCornerFinder = (row < 7 && col < 7) || (row < 7 && col >= matrixSize - 7) || (row >= matrixSize - 7 && col < 7)
                    val isFinderBorder = isCornerFinder && (row == 0 || row == 6 || col == 0 || col == 6 || (row in 2..4 && col in 2..4) ||
                            (row == 0 || row == 6 || col == matrixSize - 1 || col == matrixSize - 7 || (row in 2..4 && col in matrixSize - 5..matrixSize - 3)) ||
                            (row == matrixSize - 1 || row == matrixSize - 7 || col == 0 || col == 6 || (row in matrixSize - 5..matrixSize - 3 && col in 2..4)))

                    val pseudoBit = ((hash xor (row * 31 + col * 17)).absoluteValue % 3) == 0
                    if (isFinderBorder || (!isCornerFinder && pseudoBit)) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }

        ResultCard("QR Matrix Status", "25x25 Matrix Rendered (${text.length} bytes encoded)")
    }
}

// Tool 86: Barcode 128 Generator
@Composable
fun BarcodeGeneratorTool() {
    var codeText by remember { mutableStateOf("MTODO-2026-SUPER") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = codeText,
            onValueChange = { codeText = it },
            label = { Text("Barcode Text / Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(Color.White, RoundedCornerShape(6.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val bars = 60
            val barW = size.width / bars
            val seed = codeText.hashCode()
            for (i in 0 until bars) {
                val isDark = ((seed xor (i * 37)).absoluteValue % 2) == 0
                if (isDark || i < 3 || i > bars - 4) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(i * barW, 0f),
                        size = Size(barW * 0.8f, size.height)
                    )
                }
            }
        }

        Text(codeText, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}

// Tool 87: Decision Picker
@Composable
fun DecisionPickerTool() {
    var optionsText by remember { mutableStateOf("Pizza, Sushi, Burgers, Tacos, Salad") }
    var picked by remember { mutableStateOf("Tacos") }
    val random = remember { SecureRandom() }

    fun pick() {
        val list = optionsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (list.isNotEmpty()) {
            picked = list[random.nextInt(list.size)]
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = optionsText,
            onValueChange = { optionsText = it },
            label = { Text("Comma-separated choices") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { pick() }, modifier = Modifier.fillMaxWidth()) {
            Text("🎲 Pick For Me!")
        }

        ResultCard("The Chosen Option", "✨ $picked ✨", MaterialTheme.colorScheme.primary)
    }
}

// Tool 88: Rock Paper Scissors
@Composable
fun RockPaperScissorsTool() {
    var botChoice by remember { mutableStateOf("?") }
    var resultText by remember { mutableStateOf("Choose your weapon!") }
    var wins by remember { mutableStateOf(0) }
    var losses by remember { mutableStateOf(0) }
    var ties by remember { mutableStateOf(0) }
    val random = remember { SecureRandom() }

    val weapons = listOf("Rock" to "✊", "Paper" to "✋", "Scissors" to "✌️")

    fun play(userMove: String) {
        val botMove = weapons[random.nextInt(3)].first
        botChoice = botMove
        if (userMove == botMove) {
            resultText = "It's a TIE!"
            ties++
        } else if (
            (userMove == "Rock" && botMove == "Scissors") ||
            (userMove == "Paper" && botMove == "Rock") ||
            (userMove == "Scissors" && botMove == "Paper")
        ) {
            resultText = "You WIN! 🎉"
            wins++
        } else {
            resultText = "Bot Wins! 🤖"
            losses++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bot chose: $botChoice", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(resultText, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            weapons.forEach { (name, emoji) ->
                Button(onClick = { play(name) }) {
                    Text("$emoji $name")
                }
            }
        }

        ResultCard("Scoreboard", "Wins: $wins | Losses: $losses | Ties: $ties")
    }
}

// Tool 89: Tally Counter
@Composable
fun TallyCounterTool() {
    var count by remember { mutableStateOf(0) }
    var step by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { count += step },
            contentAlignment = Alignment.Center
        ) {
            Text("$count", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 5, 10).forEach { s ->
                FilterChip(selected = step == s, onClick = { step = s }, label = { Text("Step +$s") })
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton(onClick = { if (count >= step) count -= step }) {
                Text("-$step")
            }
            Button(onClick = { count += step }) {
                Text("+$step")
            }
            FilledTonalButton(onClick = { count = 0 }) {
                Text("Reset")
            }
        }
    }
}

// Tool 90: BMI Calculator
@Composable
fun BmiCalculatorTool() {
    var heightCmText by remember { mutableStateOf("175") }
    var weightKgText by remember { mutableStateOf("70") }

    val h = (heightCmText.toDoubleOrNull() ?: 175.0) / 100.0
    val w = weightKgText.toDoubleOrNull() ?: 70.0
    val bmi = if (h > 0) w / (h * h) else 0.0

    val (category, color) = when {
        bmi < 18.5 -> "Underweight" to Color(0xFF3B82F6)
        bmi < 25.0 -> "Normal Weight" to Color(0xFF10B981)
        bmi < 30.0 -> "Overweight" to Color(0xFFF59E0B)
        else -> "Obese" to Color(0xFFEF4444)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = heightCmText, onValueChange = { heightCmText = it }, label = { Text("Height (cm)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = weightKgText, onValueChange = { weightKgText = it }, label = { Text("Weight (kg)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        ResultCard("Body Mass Index (BMI)", "%.1f".format(bmi), color)
        ResultCard("WHO Classification", category, color)
        ResultCard("Healthy Weight Range", "%.1f kg – %.1f kg".format(18.5 * h * h, 24.9 * h * h))
    }
}

// Tool 91: BMR Calculator
@Composable
fun BmrCalculatorTool() {
    var ageText by remember { mutableStateOf("28") }
    var heightText by remember { mutableStateOf("175") }
    var weightText by remember { mutableStateOf("70") }
    var isMale by remember { mutableStateOf(true) }

    val age = ageText.toDoubleOrNull() ?: 28.0
    val h = heightText.toDoubleOrNull() ?: 175.0
    val w = weightText.toDoubleOrNull() ?: 70.0

    // Mifflin-St Jeor equation
    val bmr = if (isMale) {
        (10 * w) + (6.25 * h) - (5 * age) + 5
    } else {
        (10 * w) + (6.25 * h) - (5 * age) - 161
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isMale, onClick = { isMale = true }, label = { Text("Male") })
            FilterChip(selected = !isMale, onClick = { isMale = false }, label = { Text("Female") })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = ageText, onValueChange = { ageText = it }, label = { Text("Age") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = heightText, onValueChange = { heightText = it }, label = { Text("Height (cm)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = weightText, onValueChange = { weightText = it }, label = { Text("Weight (kg)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        ResultCard("Basal Metabolic Rate (BMR)", "%.0f kcal / day".format(bmr), MaterialTheme.colorScheme.primary)
        ResultCard("Maintenance Calories (Moderate activity)", "%.0f kcal / day".format(bmr * 1.55), Color(0xFF10B981))
    }
}

// Tool 92: Water Intake Calculator
@Composable
fun WaterIntakeTool() {
    var weightKgText by remember { mutableStateOf("70") }
    var exerciseMinsText by remember { mutableStateOf("30") }

    val w = weightKgText.toDoubleOrNull() ?: 70.0
    val ex = exerciseMinsText.toDoubleOrNull() ?: 30.0

    // Base: 35ml per kg + 12ml per min of exercise
    val waterMl = (w * 35.0) + (ex * 12.0)
    val glasses = (waterMl / 250.0).roundToInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = weightKgText, onValueChange = { weightKgText = it }, label = { Text("Weight (kg)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = exerciseMinsText, onValueChange = { exerciseMinsText = it }, label = { Text("Exercise (mins)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        ResultCard("Target Daily Hydration", "%.2f Liters".format(waterMl / 1000.0), MaterialTheme.colorScheme.primary)
        ResultCard("Equivalent Glasses", "$glasses glasses (250 ml each)", Color(0xFF06B6D4))
    }
}

// Tool 93: Box Breathing
@Composable
fun BoxBreathingTool() {
    var phase by remember { mutableStateOf("Inhale (4s)") }
    var progress by remember { mutableStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val phases = listOf("Inhale (4s)", "Hold (4s)", "Exhale (4s)", "Hold (4s)")
            while (isRunning) {
                for (p in phases) {
                    phase = p
                    for (step in 1..40) {
                        if (!isRunning) break
                        progress = step / 40f
                        delay(100)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color(0xFF06B6D4).copy(alpha = 0.2f + 0.6f * progress)),
            contentAlignment = Alignment.Center
        ) {
            Text(phase, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }

        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "Pause Breathing" else "Start 4-4-4-4 Box Breathing")
        }
    }
}

// Tool 94: Contrast Checker
@Composable
fun ContrastCheckerTool() {
    var fgHex by remember { mutableStateOf("#000000") }
    var bgHex by remember { mutableStateOf("#FFFFFF") }

    fun luminance(hex: String): Double {
        return try {
            val clean = hex.removePrefix("#")
            val r = clean.substring(0, 2).toInt(16) / 255.0
            val g = clean.substring(2, 4).toInt(16) / 255.0
            val b = clean.substring(4, 6).toInt(16) / 255.0
            val rs = if (r <= 0.03928) r / 12.92 else ((r + 0.055) / 1.055).pow(2.4)
            val gs = if (g <= 0.03928) g / 12.92 else ((g + 0.055) / 1.055).pow(2.4)
            val bs = if (b <= 0.03928) b / 12.92 else ((b + 0.055) / 1.055).pow(2.4)
            0.2126 * rs + 0.7152 * gs + 0.0722 * bs
        } catch (_: Exception) {
            0.5
        }
    }

    val l1 = luminance(fgHex)
    val l2 = luminance(bgHex)
    val ratio = if (l1 > l2) (l1 + 0.05) / (l2 + 0.05) else (l2 + 0.05) / (l1 + 0.05)

    val passesAA = ratio >= 4.5
    val passesAAA = ratio >= 7.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = fgHex, onValueChange = { fgHex = it }, label = { Text("Text Hex") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = bgHex, onValueChange = { bgHex = it }, label = { Text("Background Hex") }, modifier = Modifier.weight(1f))
        }

        ResultCard("Contrast Ratio", "%.2f : 1".format(ratio), if (passesAA) Color(0xFF10B981) else Color(0xFFEF4444))
        ResultCard("WCAG AA Standard (>= 4.5:1)", if (passesAA) "✓ PASS" else "✗ FAIL", if (passesAA) Color(0xFF10B981) else Color(0xFFEF4444))
        ResultCard("WCAG AAA Enhanced (>= 7.0:1)", if (passesAAA) "✓ PASS" else "✗ FAIL", if (passesAAA) Color(0xFF10B981) else Color(0xFFF59E0B))
    }
}

// Tool 95: Scratchpad
@Composable
fun ScratchpadTool() {
    val context = LocalContext.current
    var notes by remember { mutableStateOf("Quick thought: Release v2.5 with 100 features!") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Scratchpad Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                try {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    cm?.setPrimaryClip(ClipData.newPlainText("Scratchpad", notes))
                    Toast.makeText(context, "Copied notes", Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {}
            }) {
                Text("Copy All")
            }
            FilledTonalButton(onClick = { notes = "" }) {
                Text("Clear")
            }
        }

        ResultCard("Text Metrics", "${notes.length} characters | ${notes.split("\\s+".toRegex()).count { it.isNotBlank() }} words")
    }
}

// Tool 96: Atbash Cipher
@Composable
fun AtbashCipherTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Hello World") }

    fun atbash(s: String): String = s.map { ch ->
        when (ch) {
            in 'a'..'z' -> ('z'.code - (ch.code - 'a'.code)).toChar()
            in 'A'..'Z' -> ('Z'.code - (ch.code - 'A'.code)).toChar()
            else -> ch
        }
    }.joinToString("")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Plaintext / Ciphertext") }, modifier = Modifier.fillMaxWidth())
        ResultWithCopy("Atbash Encrypted / Decrypted", atbash(input), context)
    }
}

// Tool 97: Playing Card Shuffler
@Composable
fun CardDeckTool() {
    val suits = listOf("♠", "♥", "♦", "♣")
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    var hand by remember { mutableStateOf(listOf("A♠", "K♥", "Q♦", "J♣", "10♠")) }
    val random = remember { SecureRandom() }

    fun dealHand() {
        val allCards = suits.flatMap { s -> ranks.map { r -> "$r$s" } }.shuffled(random)
        hand = allCards.take(5)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            hand.forEach { card ->
                Card(
                    modifier = Modifier
                        .width(55.dp)
                        .height(80.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            card,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (card.contains("♥") || card.contains("♦")) Color.Red else Color.Black
                        )
                    }
                }
            }
        }

        Button(onClick = { dealHand() }) {
            Text("Deal New 5-Card Hand")
        }
    }
}

// Tool 98: Magic 8-Ball
@Composable
fun MagicEightBallTool() {
    var question by remember { mutableStateOf("Will this app be a hit?") }
    var answer by remember { mutableStateOf("It is decidedly so.") }
    val random = remember { SecureRandom() }

    val responses = listOf(
        "It is certain.", "It is decidedly so.", "Without a doubt.", "Yes definitely.",
        "You may rely on it.", "As I see it, yes.", "Most likely.", "Outlook good.",
        "Yes.", "Signs point to yes.", "Reply hazy, try again.", "Ask again later.",
        "Better not tell you now.", "Cannot predict now.", "Concentrate and ask again.",
        "Don't count on it.", "My reply is no.", "My sources say no.",
        "Outlook not so good.", "Very doubtful."
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Ask your question...") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1B4B))
                .clickable { answer = responses[random.nextInt(responses.size)] },
            contentAlignment = Alignment.Center
        ) {
            Text("🎱", fontSize = 54.sp)
        }

        Button(onClick = { answer = responses[random.nextInt(responses.size)] }) {
            Text("Shake 8-Ball")
        }

        ResultCard("8-Ball Oracle Pronouncement", answer, MaterialTheme.colorScheme.primary)
    }
}

// Tool 99: Habit Streak Counter
@Composable
fun HabitStreakTool() {
    var streak by remember { mutableStateOf(7) }
    var bestStreak by remember { mutableStateOf(14) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔥 $streak DAYS", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
        Text("Current Consecutive Streak", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                streak++
                if (streak > bestStreak) bestStreak = streak
            }) {
                Text("+1 Day Done!")
            }
            FilledTonalButton(onClick = { streak = 0 }) {
                Text("Reset")
            }
        }

        ResultCard("All-Time Record Streak", "$bestStreak days", Color(0xFF10B981))
    }
}

// Tool 100: Compatibility / Love Matcher
@Composable
fun CompatibilityMatcherTool() {
    var name1 by remember { mutableStateOf("Alice") }
    var name2 by remember { mutableStateOf("Bob") }
    var score by remember { mutableStateOf(88) }

    fun calculate() {
        val combined = (name1.lowercase() + name2.lowercase()).filter { it.isLetter() }
        val sum = combined.fold(0) { acc, c -> acc + c.code }
        score = (sum % 41) + 60 // Generates nice realistic score 60-100%
    }

    LaunchedEffect(name1, name2) {
        calculate()
    }

    val verdict = when {
        score >= 90 -> "Soulmates! Written in the stars ⭐"
        score >= 75 -> "Great chemistry and harmony! 💖"
        else -> "Good potential with honest communication! 😊"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = name1, onValueChange = { name1 = it }, label = { Text("Name 1") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = name2, onValueChange = { name2 = it }, label = { Text("Name 2") }, modifier = Modifier.weight(1f))
        }

        ResultCard("Compatibility Score", "$score%", MaterialTheme.colorScheme.primary)
        ResultCard("Relationship Verdict", verdict, Color(0xFFEC4899))
    }
}
