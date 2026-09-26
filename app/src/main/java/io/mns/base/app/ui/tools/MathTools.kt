package io.mns.base.app.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

val MATH_TOOLS: List<ToolItem> = listOf(
    ToolItem(
        id = 1,
        name = "Everyday Calculator",
        description = "Standard arithmetic evaluator with instant calculation and error checking.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Calculate
    ) { CalculatorTool() },

    ToolItem(
        id = 2,
        name = "Percentage Calculator",
        description = "Compute percentage of values, ratio percentages, and percentage increase or decrease.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Percent
    ) { PercentageCalculatorTool() },

    ToolItem(
        id = 3,
        name = "Tip & Bill Splitter",
        description = "Calculate dining tips, total bills, and split payments evenly across parties.",
        category = ToolCategory.MATH,
        icon = Icons.Default.AttachMoney
    ) { TipSplitterTool() },

    ToolItem(
        id = 4,
        name = "Discount & Sales Tax",
        description = "Compute item discounts, markdown savings, sales taxes, and final out-the-door totals.",
        category = ToolCategory.MATH,
        icon = Icons.Default.LocalOffer
    ) { DiscountTaxTool() },

    ToolItem(
        id = 5,
        name = "Loan EMI Calculator",
        description = "Estimate monthly mortgage or car loan installments, total interest, and payback total.",
        category = ToolCategory.MATH,
        icon = Icons.Default.AccountBalance
    ) { LoanEmiTool() },

    ToolItem(
        id = 6,
        name = "Compound Interest",
        description = "Compound growth calculator with variable annual frequency and tenure projection.",
        category = ToolCategory.MATH,
        icon = Icons.AutoMirrored.Filled.TrendingUp
    ) { CompoundInterestTool() },

    ToolItem(
        id = 7,
        name = "Fuel Cost & Trip Planner",
        description = "Estimate fuel consumption, trip expenses, and per-passenger fuel split.",
        category = ToolCategory.MATH,
        icon = Icons.Default.LocalGasStation
    ) { FuelCostTool() },

    ToolItem(
        id = 8,
        name = "Unit: Length & Distance",
        description = "Convert between meters, kilometers, centimeters, millimeters, miles, feet, and inches.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Straighten
    ) { LengthConverterTool() },

    ToolItem(
        id = 9,
        name = "Unit: Weight & Mass",
        description = "Convert between kilograms, grams, milligrams, pounds, ounces, and metric tons.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Scale
    ) { WeightConverterTool() },

    ToolItem(
        id = 10,
        name = "Unit: Temperature",
        description = "Live bidirectional temperature conversion across Celsius, Fahrenheit, and Kelvin.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Thermostat
    ) { TemperatureConverterTool() },

    ToolItem(
        id = 11,
        name = "Unit: Surface Area",
        description = "Convert between square meters, square feet, square kilometers, acres, and hectares.",
        category = ToolCategory.MATH,
        icon = Icons.Default.SquareFoot
    ) { AreaConverterTool() },

    ToolItem(
        id = 12,
        name = "Unit: Liquid Volume",
        description = "Convert between liters, milliliters, US gallons, quarts, pints, and fluid ounces.",
        category = ToolCategory.MATH,
        icon = Icons.Default.LocalDrink
    ) { VolumeConverterTool() },

    ToolItem(
        id = 13,
        name = "Unit: Speed & Velocity",
        description = "Convert between km/h, miles per hour, meters per second, and nautical knots.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Speed
    ) { SpeedConverterTool() },

    ToolItem(
        id = 14,
        name = "Unit: Digital Storage",
        description = "Convert between Bytes, KB, MB, GB, TB, and Petabytes in binary 1024 basis.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Storage
    ) { StorageConverterTool() },

    ToolItem(
        id = 15,
        name = "Base Number Converter",
        description = "Simultaneous live conversion between Decimal, Binary, Octal, and Hexadecimal.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Transform
    ) { BaseConverterTool() },

    ToolItem(
        id = 16,
        name = "Roman Numeral Converter",
        description = "Bidirectional conversion between Arabic integers (1-3999) and Roman numerals.",
        category = ToolCategory.MATH,
        icon = Icons.Default.FormatListNumbered
    ) { RomanNumeralTool() },

    ToolItem(
        id = 17,
        name = "Prime Factorizer & GCD/LCM",
        description = "Calculate prime factors, greatest common divisor (GCD), and least common multiple (LCM).",
        category = ToolCategory.MATH,
        icon = Icons.Default.Functions
    ) { PrimeGcdLcmTool() },

    ToolItem(
        id = 18,
        name = "Quadratic Equation Solver",
        description = "Compute real and complex roots for quadratic formulas: ax² + bx + c = 0.",
        category = ToolCategory.MATH,
        icon = Icons.Default.Calculate
    ) { QuadraticSolverTool() },

    ToolItem(
        id = 19,
        name = "Aspect Ratio Scaler",
        description = "Calculate aspect ratios (16:9, 4:3, 1:1) and proportional dimension scaling.",
        category = ToolCategory.MATH,
        icon = Icons.Default.AspectRatio
    ) { AspectRatioTool() },

    ToolItem(
        id = 20,
        name = "Profit Margin & Markup",
        description = "Analyze gross profit, profit margin percentage, and sales markup from cost.",
        category = ToolCategory.MATH,
        icon = Icons.Default.MonetizationOn
    ) { MarginMarkupTool() }
)

// Tool 1: Everyday Calculator
@Composable
fun CalculatorTool() {
    var expr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    fun evaluate(e: String): String {
        return try {
            val clean = e.trim()
            if (clean.isBlank()) return "0"
            val tokens = mutableListOf<String>()
            var num = ""
            for (ch in clean) {
                if (ch.isDigit() || ch == '.') {
                    num += ch
                } else if (ch in "+-*/") {
                    if (num.isNotEmpty()) {
                        tokens.add(num)
                        num = ""
                    }
                    tokens.add(ch.toString())
                }
            }
            if (num.isNotEmpty()) tokens.add(num)
            while (tokens.isNotEmpty() && tokens.last() in "+-*/") {
                tokens.removeAt(tokens.lastIndex)
            }
            if (tokens.isEmpty()) return "0"
            if (tokens[0] in "+-" && tokens.size > 1) {
                val sign = tokens.removeAt(0)
                val firstNum = tokens.removeAt(0)
                tokens.add(0, if (sign == "-") "-$firstNum" else firstNum)
            } else if (tokens[0] in "+-*/") {
                tokens.removeAt(0)
            }
            if (tokens.isEmpty()) return "0"

            var i = 1
            while (i < tokens.size - 1) {
                if (tokens[i] == "*" || tokens[i] == "/") {
                    val op = tokens[i]
                    val left = tokens[i - 1].toDoubleOrNull() ?: 0.0
                    val right = tokens[i + 1].toDoubleOrNull() ?: 1.0
                    val res = if (op == "*") left * right else if (right != 0.0) left / right else 0.0
                    tokens[i - 1] = res.toString()
                    tokens.removeAt(i)
                    tokens.removeAt(i)
                    i--
                }
                i++
            }
            var total = tokens[0].toDoubleOrNull() ?: 0.0
            var j = 1
            while (j < tokens.size - 1) {
                val op = tokens[j]
                val right = tokens[j + 1].toDoubleOrNull() ?: 0.0
                total = if (op == "+") total + right else total - right
                j += 2
            }
            if (total % 1.0 == 0.0) total.toLong().toString() else "%.4f".format(total).trimEnd('0').trimEnd('.')
        } catch (_: Exception) {
            "0"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (expr.isEmpty()) "0" else expr,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        val rows = listOf(
            listOf("C", "DEL", "%", "/"),
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    val isAction = key in listOf("C", "DEL", "=", "+", "-", "*", "/", "%")
                    val isEquals = key == "="
                    val weight = if (key == "0") 2f else 1f

                    Button(
                        onClick = {
                            when (key) {
                                "C" -> {
                                    expr = ""
                                    result = "0"
                                }
                                "DEL" -> {
                                    if (expr.isNotEmpty()) {
                                        expr = expr.dropLast(1)
                                        result = evaluate(expr)
                                    }
                                }
                                "=" -> {
                                    result = evaluate(expr)
                                    expr = result
                                }
                                "%" -> {
                                    try {
                                        val v = (expr.toDoubleOrNull() ?: 0.0) / 100.0
                                        expr = v.toString()
                                        result = expr
                                    } catch (_: Exception) {}
                                }
                                else -> {
                                    expr += key
                                    result = evaluate(expr)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(weight)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isEquals -> MaterialTheme.colorScheme.primary
                                isAction -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            },
                            contentColor = when {
                                isEquals -> MaterialTheme.colorScheme.onPrimary
                                isAction -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    ) {
                        Text(text = key, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Tool 2: Percentage Calculator
@Composable
fun PercentageCalculatorTool() {
    var v1 by remember { mutableStateOf("25") }
    var v2 by remember { mutableStateOf("200") }

    val num1 = v1.toDoubleOrNull() ?: 0.0
    val num2 = v2.toDoubleOrNull() ?: 0.0

    val pOfY = (num1 / 100.0) * num2
    val whatPercent = if (num2 != 0.0) (num1 / num2) * 100.0 else 0.0
    val change = if (num1 != 0.0) ((num2 - num1) / num1) * 100.0 else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = v1,
                onValueChange = { v1 = it },
                label = { Text("Value A") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = v2,
                onValueChange = { v2 = it },
                label = { Text("Value B") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "What is $v1% of $v2?", value = "%.2f".format(pOfY))
        ResultCard(label = "$v1 is what % of $v2?", value = "%.2f%%".format(whatPercent))
        ResultCard(
            label = "% Change from $v1 to $v2",
            value = "${if (change >= 0) "+" else ""}${"%.2f".format(change)}%",
            color = if (change >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
        )
    }
}

// Tool 3: Tip & Bill Splitter
@Composable
fun TipSplitterTool() {
    var billText by remember { mutableStateOf("75.00") }
    var tipPercent by remember { mutableStateOf(15f) }
    var splitPeople by remember { mutableStateOf(2) }

    val bill = billText.toDoubleOrNull() ?: 0.0
    val tipAmount = bill * (tipPercent / 100.0)
    val totalBill = bill + tipAmount
    val perPerson = if (splitPeople > 0) totalBill / splitPeople else totalBill

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = billText,
            onValueChange = { billText = it },
            label = { Text("Bill Amount ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Tip: ${tipPercent.toInt()}%", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10, 15, 18, 20, 25).forEach { pct ->
                FilterChip(
                    selected = tipPercent.toInt() == pct,
                    onClick = { tipPercent = pct.toFloat() },
                    label = { Text("$pct%") }
                )
            }
        }
        Slider(
            value = tipPercent,
            onValueChange = { tipPercent = it },
            valueRange = 0f..40f,
            steps = 39
        )

        Text("Split between: $splitPeople people", fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilledTonalButton(onClick = { if (splitPeople > 1) splitPeople-- }) {
                Text("-", fontSize = 20.sp)
            }
            Text("$splitPeople", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            FilledTonalButton(onClick = { if (splitPeople < 30) splitPeople++ }) {
                Text("+", fontSize = 20.sp)
            }
        }

        ResultCard(label = "Tip Amount", value = "$%.2f".format(tipAmount))
        ResultCard(label = "Total with Tip", value = "$%.2f".format(totalBill))
        ResultCard(label = "Per Person", value = "$%.2f".format(perPerson), color = MaterialTheme.colorScheme.primary)
    }
}

// Tool 4: Discount & Sales Tax
@Composable
fun DiscountTaxTool() {
    var priceText by remember { mutableStateOf("120.00") }
    var discountText by remember { mutableStateOf("20") }
    var taxText by remember { mutableStateOf("8.5") }

    val price = priceText.toDoubleOrNull() ?: 0.0
    val discPct = discountText.toDoubleOrNull() ?: 0.0
    val taxPct = taxText.toDoubleOrNull() ?: 0.0

    val savings = price * (discPct / 100.0)
    val discountedPrice = price - savings
    val taxAmount = discountedPrice * (taxPct / 100.0)
    val finalTotal = discountedPrice + taxAmount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("Original Price ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = discountText,
                onValueChange = { discountText = it },
                label = { Text("Discount (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = taxText,
                onValueChange = { taxText = it },
                label = { Text("Sales Tax (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "You Save (Discount)", value = "-$%.2f".format(savings), color = Color(0xFF10B981))
        ResultCard(label = "Subtotal after Discount", value = "$%.2f".format(discountedPrice))
        ResultCard(label = "Tax Amount", value = "+$%.2f".format(taxAmount))
        ResultCard(label = "Final Out-the-Door Price", value = "$%.2f".format(finalTotal), color = MaterialTheme.colorScheme.primary)
    }
}

// Tool 5: Loan EMI Calculator
@Composable
fun LoanEmiTool() {
    var principalText by remember { mutableStateOf("25000") }
    var rateText by remember { mutableStateOf("6.5") }
    var tenureMonthsText by remember { mutableStateOf("36") }

    val p = principalText.toDoubleOrNull() ?: 0.0
    val annualRate = rateText.toDoubleOrNull() ?: 0.0
    val n = tenureMonthsText.toDoubleOrNull() ?: 1.0
    val monthlyRate = (annualRate / 100.0) / 12.0

    val emi = if (monthlyRate > 0 && n > 0) {
        val factor = (1.0 + monthlyRate).pow(n)
        p * monthlyRate * factor / (factor - 1.0)
    } else {
        if (n > 0) p / n else 0.0
    }
    val totalPayable = emi * n
    val totalInterest = max(0.0, totalPayable - p)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = principalText,
            onValueChange = { principalText = it },
            label = { Text("Principal Amount ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = rateText,
                onValueChange = { rateText = it },
                label = { Text("Annual Rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = tenureMonthsText,
                onValueChange = { tenureMonthsText = it },
                label = { Text("Tenure (Months)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "Monthly EMI Payment", value = "$%.2f".format(emi), color = MaterialTheme.colorScheme.primary)
        ResultCard(label = "Total Interest Accrued", value = "$%.2f".format(totalInterest))
        ResultCard(label = "Total Repayment", value = "$%.2f".format(totalPayable))
    }
}

// Tool 6: Compound Interest
@Composable
fun CompoundInterestTool() {
    var principalText by remember { mutableStateOf("10000") }
    var rateText by remember { mutableStateOf("7.0") }
    var yearsText by remember { mutableStateOf("10") }
    var compoundFreq by remember { mutableStateOf(12) } // 12 = monthly

    val p = principalText.toDoubleOrNull() ?: 0.0
    val r = (rateText.toDoubleOrNull() ?: 0.0) / 100.0
    val t = yearsText.toDoubleOrNull() ?: 0.0
    val n = compoundFreq.toDouble()

    val finalAmount = if (n > 0 && t > 0) p * (1.0 + r / n).pow(n * t) else p
    val earned = finalAmount - p

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = principalText,
            onValueChange = { principalText = it },
            label = { Text("Starting Principal ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = rateText,
                onValueChange = { rateText = it },
                label = { Text("Annual Return (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = yearsText,
                onValueChange = { yearsText = it },
                label = { Text("Tenure (Years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Text("Compounding Frequency:", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "Annual", 4 to "Quarterly", 12 to "Monthly", 365 to "Daily").forEach { (freq, lbl) ->
                FilterChip(
                    selected = compoundFreq == freq,
                    onClick = { compoundFreq = freq },
                    label = { Text(lbl) }
                )
            }
        }

        ResultCard(label = "Final Future Value", value = "$%.2f".format(finalAmount), color = MaterialTheme.colorScheme.primary)
        ResultCard(label = "Total Compound Interest Earned", value = "$%.2f".format(earned), color = Color(0xFF10B981))
    }
}

// Tool 7: Fuel Cost & Trip Planner
@Composable
fun FuelCostTool() {
    var distanceText by remember { mutableStateOf("350") }
    var consumptionText by remember { mutableStateOf("7.5") }
    var priceText by remember { mutableStateOf("1.65") }
    var passengersText by remember { mutableStateOf("3") }

    val dist = distanceText.toDoubleOrNull() ?: 0.0
    val consumption = consumptionText.toDoubleOrNull() ?: 0.0 // L/100km
    val price = priceText.toDoubleOrNull() ?: 0.0
    val passengers = max(1, passengersText.toIntOrNull() ?: 1)

    val fuelNeeded = (dist / 100.0) * consumption
    val totalCost = fuelNeeded * price
    val perPassenger = totalCost / passengers

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = distanceText,
                onValueChange = { distanceText = it },
                label = { Text("Trip Distance (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = consumptionText,
                onValueChange = { consumptionText = it },
                label = { Text("Rate (L/100km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Fuel Price ($/L)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = passengersText,
                onValueChange = { passengersText = it },
                label = { Text("Passengers") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "Estimated Fuel Consumed", value = "%.1f Liters".format(fuelNeeded))
        ResultCard(label = "Total Fuel Expense", value = "$%.2f".format(totalCost), color = MaterialTheme.colorScheme.primary)
        ResultCard(label = "Cost Per Passenger", value = "$%.2f".format(perPassenger))
    }
}

// Tool 8: Length Converter
@Composable
fun LengthConverterTool() {
    GenericConverterTool(
        title = "Length",
        units = listOf(
            "Meters" to 1.0,
            "Kilometers" to 1000.0,
            "Centimeters" to 0.01,
            "Millimeters" to 0.001,
            "Miles" to 1609.344,
            "Yards" to 0.9144,
            "Feet" to 0.3048,
            "Inches" to 0.0254
        )
    )
}

// Tool 9: Weight Converter
@Composable
fun WeightConverterTool() {
    GenericConverterTool(
        title = "Weight",
        units = listOf(
            "Kilograms" to 1.0,
            "Grams" to 0.001,
            "Milligrams" to 0.000001,
            "Pounds (lbs)" to 0.45359237,
            "Ounces (oz)" to 0.0283495,
            "Metric Tons" to 1000.0
        )
    )
}

// Tool 10: Temperature Converter
@Composable
fun TemperatureConverterTool() {
    var cText by remember { mutableStateOf("25") }
    val c = cText.toDoubleOrNull() ?: 0.0
    val f = (c * 9.0 / 5.0) + 32.0
    val k = c + 273.15

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = cText,
            onValueChange = { cText = it },
            label = { Text("Celsius (°C)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        ResultCard(label = "Fahrenheit", value = "%.2f °F".format(f), color = Color(0xFFF59E0B))
        ResultCard(label = "Kelvin", value = "%.2f K".format(k), color = Color(0xFF3B82F6))
    }
}

// Tool 11: Area Converter
@Composable
fun AreaConverterTool() {
    GenericConverterTool(
        title = "Surface Area",
        units = listOf(
            "Square Meters (m²)" to 1.0,
            "Square Kilometers (km²)" to 1000000.0,
            "Square Feet (ft²)" to 0.092903,
            "Acres" to 4046.86,
            "Hectares" to 10000.0
        )
    )
}

// Tool 12: Volume Converter
@Composable
fun VolumeConverterTool() {
    GenericConverterTool(
        title = "Volume",
        units = listOf(
            "Liters" to 1.0,
            "Milliliters" to 0.001,
            "US Gallons" to 3.78541,
            "US Quarts" to 0.946353,
            "US Pints" to 0.473176,
            "Fluid Ounces (fl oz)" to 0.0295735,
            "Cups" to 0.236588
        )
    )
}

// Tool 13: Speed Converter
@Composable
fun SpeedConverterTool() {
    GenericConverterTool(
        title = "Speed",
        units = listOf(
            "km/h" to 1.0,
            "Miles/Hour (mph)" to 1.60934,
            "Meters/Sec (m/s)" to 3.6,
            "Knots" to 1.852,
            "Feet/Sec (ft/s)" to 1.09728
        )
    )
}

// Tool 14: Digital Storage Converter
@Composable
fun StorageConverterTool() {
    GenericConverterTool(
        title = "Storage",
        units = listOf(
            "Bytes" to 1.0,
            "Kilobytes (KB)" to 1024.0,
            "Megabytes (MB)" to 1024.0 * 1024.0,
            "Gigabytes (GB)" to 1024.0 * 1024.0 * 1024.0,
            "Terabytes (TB)" to 1024.0 * 1024.0 * 1024.0 * 1024.0
        )
    )
}

// Tool 15: Base Converter
@Composable
fun BaseConverterTool() {
    var decText by remember { mutableStateOf("255") }
    val number = decText.toLongOrNull() ?: 0L

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = decText,
            onValueChange = { decText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Decimal (Base 10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        ResultCard(label = "Binary (Base 2)", value = java.lang.Long.toBinaryString(number))
        ResultCard(label = "Octal (Base 8)", value = java.lang.Long.toOctalString(number))
        ResultCard(label = "Hexadecimal (Base 16)", value = "0x" + java.lang.Long.toHexString(number).uppercase(), color = MaterialTheme.colorScheme.primary)
    }
}

// Tool 16: Roman Numeral Converter
@Composable
fun RomanNumeralTool() {
    var inputArabic by remember { mutableStateOf("2026") }

    fun intToRoman(num: Int): String {
        if (num <= 0 || num > 3999) return "Enter 1 - 3999"
        val values = listOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val romanLiterals = listOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val sb = StringBuilder()
        var v = num
        for (i in values.indices) {
            while (v >= values[i]) {
                v -= values[i]
                sb.append(romanLiterals[i])
            }
        }
        return sb.toString()
    }

    val num = inputArabic.toIntOrNull() ?: 0
    val roman = intToRoman(num)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = inputArabic,
            onValueChange = { inputArabic = it },
            label = { Text("Arabic Number (1 - 3999)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        ResultCard(label = "Roman Numeral Equivalent", value = roman, color = MaterialTheme.colorScheme.primary)
    }
}

// Tool 17: Prime Factorizer & GCD/LCM
@Composable
fun PrimeGcdLcmTool() {
    var numAText by remember { mutableStateOf("60") }
    var numBText by remember { mutableStateOf("84") }

    fun primeFactors(n: Long): List<Long> {
        var num = n
        val factors = mutableListOf<Long>()
        var d = 2L
        while (d * d <= num) {
            while (num % d == 0L) {
                factors.add(d)
                num /= d
            }
            d++
        }
        if (num > 1) factors.add(num)
        return factors
    }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    fun lcm(a: Long, b: Long): Long = if (a == 0L || b == 0L) 0L else abs(a * b) / gcd(a, b)

    val a = numAText.toLongOrNull() ?: 1L
    val b = numBText.toLongOrNull() ?: 1L

    val factorsA = primeFactors(a)
    val g = gcd(a, b)
    val l = lcm(a, b)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = numAText,
                onValueChange = { numAText = it },
                label = { Text("Number A") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = numBText,
                onValueChange = { numBText = it },
                label = { Text("Number B") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "Prime Factors of A ($a)", value = if (factorsA.isEmpty()) "None" else factorsA.joinToString(" × "))
        ResultCard(label = "Greatest Common Divisor (GCD)", value = "$g", color = MaterialTheme.colorScheme.primary)
        ResultCard(label = "Least Common Multiple (LCM)", value = "$l")
    }
}

// Tool 18: Quadratic Solver
@Composable
fun QuadraticSolverTool() {
    var aText by remember { mutableStateOf("1") }
    var bText by remember { mutableStateOf("-5") }
    var cText by remember { mutableStateOf("6") }

    val a = aText.toDoubleOrNull() ?: 1.0
    val b = bText.toDoubleOrNull() ?: 0.0
    val c = cText.toDoubleOrNull() ?: 0.0

    val delta = (b * b) - (4 * a * c)
    val roots = when {
        a == 0.0 -> "Linear equation: x = ${-c / b}"
        delta > 0 -> {
            val r1 = (-b + sqrt(delta)) / (2 * a)
            val r2 = (-b - sqrt(delta)) / (2 * a)
            "Real Roots:\nx₁ = %.4f\nx₂ = %.4f".format(r1, r2)
        }
        delta == 0.0 -> {
            val r = -b / (2 * a)
            "Single Root:\nx = %.4f".format(r)
        }
        else -> {
            val real = -b / (2 * a)
            val imag = sqrt(-delta) / (2 * a)
            "Complex Roots:\nx = %.4f ± %.4fi".format(real, imag)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Formula: ax² + bx + c = 0", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = aText,
                onValueChange = { aText = it },
                label = { Text("a") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = bText,
                onValueChange = { bText = it },
                label = { Text("b") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = cText,
                onValueChange = { cText = it },
                label = { Text("c") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "Discriminant (Δ)", value = "%.2f".format(delta))
        ResultCard(label = "Solutions", value = roots, color = MaterialTheme.colorScheme.primary)
    }
}

// Tool 19: Aspect Ratio Scaler
@Composable
fun AspectRatioTool() {
    var widthText by remember { mutableStateOf("1920") }
    var heightText by remember { mutableStateOf("1080") }
    var targetWidthText by remember { mutableStateOf("1280") }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    val w = widthText.toDoubleOrNull() ?: 1.0
    val h = heightText.toDoubleOrNull() ?: 1.0
    val tw = targetWidthText.toDoubleOrNull() ?: 1.0

    val g = gcd(w.toLong(), h.toLong())
    val ratioW = if (g > 0) w.toLong() / g else 16
    val ratioH = if (g > 0) h.toLong() / g else 9

    val scaledHeight = if (w > 0) (tw / w) * h else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = widthText,
                onValueChange = { widthText = it },
                label = { Text("Base Width") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("Base Height") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "Simplified Aspect Ratio", value = "$ratioW:$ratioH", color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = targetWidthText,
            onValueChange = { targetWidthText = it },
            label = { Text("Target New Width") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        ResultCard(label = "Scaled Proportional Height", value = "%.1f px".format(scaledHeight))
    }
}

// Tool 20: Margin & Markup
@Composable
fun MarginMarkupTool() {
    var costText by remember { mutableStateOf("50.00") }
    var sellingText by remember { mutableStateOf("80.00") }

    val cost = costText.toDoubleOrNull() ?: 0.0
    val selling = sellingText.toDoubleOrNull() ?: 0.0

    val grossProfit = selling - cost
    val margin = if (selling > 0) (grossProfit / selling) * 100.0 else 0.0
    val markup = if (cost > 0) (grossProfit / cost) * 100.0 else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = costText,
                onValueChange = { costText = it },
                label = { Text("Cost Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = sellingText,
                onValueChange = { sellingText = it },
                label = { Text("Selling Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        ResultCard(label = "Gross Profit", value = "$%.2f".format(grossProfit), color = Color(0xFF10B981))
        ResultCard(label = "Profit Margin", value = "%.2f%%".format(margin), color = MaterialTheme.colorScheme.primary)
        ResultCard(label = "Markup on Cost", value = "%.2f%%".format(markup))
    }
}

// Reusable UI Helpers
@Composable
fun GenericConverterTool(
    title: String,
    units: List<Pair<String, Double>>
) {
    var inputText by remember { mutableStateOf("1") }
    var selectedFromIndex by remember { mutableStateOf(0) }
    var selectedToIndex by remember { mutableStateOf(min(1, units.size - 1)) }

    val inputVal = inputText.toDoubleOrNull() ?: 0.0
    val baseValue = inputVal * units[selectedFromIndex].second
    val convertedVal = if (units[selectedToIndex].second != 0.0) baseValue / units[selectedToIndex].second else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Value to convert") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text("From Unit:", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            units.forEachIndexed { idx, (name, _) ->
                FilterChip(
                    selected = selectedFromIndex == idx,
                    onClick = { selectedFromIndex = idx },
                    label = { Text(name, fontSize = 12.sp) }
                )
            }
        }

        Text("To Unit:", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            units.forEachIndexed { idx, (name, _) ->
                FilterChip(
                    selected = selectedToIndex == idx,
                    onClick = { selectedToIndex = idx },
                    label = { Text(name, fontSize = 12.sp) }
                )
            }
        }

        ResultCard(
            label = "Result (${units[selectedToIndex].first})",
            value = if (abs(convertedVal) >= 10000 || (abs(convertedVal) < 0.001 && convertedVal != 0.0)) {
                "%.4e".format(convertedVal)
            } else {
                "%.4f".format(convertedVal).trimEnd('0').trimEnd('.')
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ResultCard(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
