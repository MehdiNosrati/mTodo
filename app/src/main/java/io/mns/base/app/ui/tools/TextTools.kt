package io.mns.base.app.ui.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

val TEXT_TOOLS: List<ToolItem> = listOf(
    ToolItem(
        id = 21,
        name = "Text Statistics & Analyzer",
        description = "Instant word count, characters, sentences, paragraphs, and reading duration.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.TextFields
    ) { TextAnalyzerTool() },

    ToolItem(
        id = 22,
        name = "Case Converter",
        description = "Transform text into UPPERCASE, lowercase, Title Case, camelCase, snake_case, and kebab-case.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.FormatSize
    ) { CaseConverterTool() },

    ToolItem(
        id = 23,
        name = "Base64 Encoder & Decoder",
        description = "Bidirectional UTF-8 to Base64 data encoding and decoding with validation.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Code
    ) { Base64Tool() },

    ToolItem(
        id = 24,
        name = "URL Encoder & Decoder",
        description = "Standard percent-encoding for URLs, API parameters, and query strings.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Link
    ) { UrlEncoderTool() },

    ToolItem(
        id = 25,
        name = "Cryptographic Hash Generator",
        description = "Simultaneously generate MD5, SHA-1, SHA-256, and SHA-512 hashes from input text.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Security
    ) { HashGeneratorTool() },

    ToolItem(
        id = 26,
        name = "Morse Code Translator",
        description = "Bidirectional translation between standard text and International Morse Code.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.GraphicEq
    ) { MorseCodeTool() },

    ToolItem(
        id = 27,
        name = "ROT13 & Caesar Cipher",
        description = "Cryptographic alphabet shift cipher with custom key offset (-25 to +25) and ROT13.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.VpnKey
    ) { CaesarCipherTool() },

    ToolItem(
        id = 28,
        name = "Text Inverter & Reverser",
        description = "Reverse entire character strings, reverse word ordering, or flip upside-down.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.SyncAlt
    ) { TextReverserTool() },

    ToolItem(
        id = 29,
        name = "Binary & Hex String Encoder",
        description = "Encode UTF-8 characters to binary bitstream (0100...) and hexadecimal byte values.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Memory
    ) { BinaryHexTool() },

    ToolItem(
        id = 30,
        name = "Lorem Ipsum Generator",
        description = "Generate customizable placeholder dummy text by paragraphs, sentences, or words.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.FormatQuote
    ) { LoremIpsumTool() },

    ToolItem(
        id = 31,
        name = "Duplicate Line Stripper",
        description = "Deduplicate text files and lists with optional whitespace trimming and sorting.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.FilterList
    ) { DuplicateRemoverTool() },

    ToolItem(
        id = 32,
        name = "Line Sorter & Alphabetizer",
        description = "Sort lines alphabetically A-Z, Z-A, or by line length ascending/descending.",
        category = ToolCategory.TEXT,
        icon = Icons.AutoMirrored.Filled.Sort
    ) { LineSorterTool() },

    ToolItem(
        id = 33,
        name = "URL Slugifier",
        description = "Convert headlines and titles into clean, web-friendly SEO slug strings.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Http
    ) { SlugifierTool() },

    ToolItem(
        id = 34,
        name = "Character Frequency Counter",
        description = "Analyze the occurrence rate and percentage of every character in a document.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.BarChart
    ) { CharFrequencyTool() },

    ToolItem(
        id = 35,
        name = "Palindrome Checker",
        description = "Test if text reads identical in reverse, ignoring whitespace and punctuation.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.CheckCircle
    ) { PalindromeTool() },

    ToolItem(
        id = 36,
        name = "UUID v4 Generator",
        description = "Generate cryptographically secure GUID / UUID version 4 tokens with batch copy.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Fingerprint
    ) { UuidGeneratorTool() },

    ToolItem(
        id = 37,
        name = "JSON Formatter & Minifier",
        description = "Pretty-print indented JSON or minify to single-line with syntax validation.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.DataArray
    ) { JsonFormatterTool() },

    ToolItem(
        id = 38,
        name = "Password Entropy Meter",
        description = "Analyze bit entropy, character set diversity, and brute-force crack resilience.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.Password
    ) { PasswordEntropyTool() },

    ToolItem(
        id = 39,
        name = "Whitespace Cleaner",
        description = "Remove multiple consecutive spaces, strip empty blank lines, and trim text borders.",
        category = ToolCategory.TEXT,
        icon = Icons.Default.SpaceBar
    ) { WhitespaceCleanerTool() },

    ToolItem(
        id = 40,
        name = "Markdown Quick Preview",
        description = "Render headers, bold, italics, blockquotes, and lists from Markdown syntax.",
        category = ToolCategory.TEXT,
        icon = Icons.AutoMirrored.Filled.Notes
    ) { MarkdownPreviewTool() }
)

// Tool 21: Text Statistics & Analyzer
@Composable
fun TextAnalyzerTool() {
    var text by remember { mutableStateOf("Welcome to the 100 Super Utilities app. Everything you need is right here in your pocket!") }

    val charCount = text.length
    val charNoSpace = text.count { !it.isWhitespace() }
    val words = text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    val wordCount = if (text.isBlank()) 0 else words.size
    val sentences = text.split("[.!?]+".toRegex()).filter { it.isNotBlank() }
    val sentenceCount = if (text.isBlank()) 0 else sentences.size
    val paragraphs = text.split("\n+".toRegex()).filter { it.isNotBlank() }
    val paragraphCount = if (text.isBlank()) 0 else paragraphs.size
    val readingTimeSeconds = if (wordCount > 0) (wordCount * 60) / 200 else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Input Text") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) { ResultCard("Words", "$wordCount") }
            Box(modifier = Modifier.weight(1f)) { ResultCard("Characters", "$charCount") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) { ResultCard("Without Spaces", "$charNoSpace") }
            Box(modifier = Modifier.weight(1f)) { ResultCard("Sentences", "$sentenceCount") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) { ResultCard("Paragraphs", "$paragraphCount") }
            Box(modifier = Modifier.weight(1f)) { ResultCard("Reading Time", "${readingTimeSeconds}s", MaterialTheme.colorScheme.primary) }
        }
    }
}

// Tool 22: Case Converter
@Composable
fun CaseConverterTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Hello World from Jetpack Compose") }

    fun toTitleCase(s: String): String =
        s.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

    fun toCamelCase(s: String): String {
        val parts = s.split("[\\s_-]+".toRegex()).filter { it.isNotBlank() }
        if (parts.isEmpty()) return ""
        return parts.first().lowercase() + parts.drop(1).joinToString("") {
            it.lowercase().replaceFirstChar { ch -> ch.uppercase() }
        }
    }

    fun toSnakeCase(s: String): String =
        s.trim().lowercase().replace("[\\s-]+".toRegex(), "_")

    fun toKebabCase(s: String): String =
        s.trim().lowercase().replace("[\\s_]+".toRegex(), "-")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input Text") },
            modifier = Modifier.fillMaxWidth()
        )

        val variations = listOf(
            "UPPERCASE" to input.uppercase(),
            "lowercase" to input.lowercase(),
            "Title Case" to toTitleCase(input),
            "camelCase" to toCamelCase(input),
            "snake_case" to toSnakeCase(input),
            "kebab-case" to toKebabCase(input),
            "CONSTANT_CASE" to toSnakeCase(input).uppercase()
        )

        variations.forEach { (label, value) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                    IconButton(onClick = {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText(label, value))
                        Toast.makeText(context, "Copied $label", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                }
            }
        }
    }
}

// Tool 23: Base64
@Composable
fun Base64Tool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Android Super App") }
    var isEncodeMode by remember { mutableStateOf(true) }

    val output = remember(input, isEncodeMode) {
        try {
            if (isEncodeMode) {
                android.util.Base64.encodeToString(input.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
            } else {
                String(android.util.Base64.decode(input, android.util.Base64.DEFAULT), Charsets.UTF_8)
            }
        } catch (e: Exception) {
            "Error: Invalid Base64 String"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = isEncodeMode,
                onClick = { isEncodeMode = true },
                label = { Text("Encode to Base64") }
            )
            FilterChip(
                selected = !isEncodeMode,
                onClick = { isEncodeMode = false },
                label = { Text("Decode from Base64") }
            )
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(if (isEncodeMode) "Plain Text" else "Base64 String") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )

        ResultWithCopy(label = if (isEncodeMode) "Base64 Result" else "Decoded Text", value = output, context = context)
    }
}

// Tool 24: URL Encoder & Decoder
@Composable
fun UrlEncoderTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("https://example.com/search?q=kotlin & compose+android") }
    var isEncode by remember { mutableStateOf(true) }

    val output = remember(input, isEncode) {
        try {
            if (isEncode) URLEncoder.encode(input, "UTF-8")
            else URLDecoder.decode(input, "UTF-8")
        } catch (e: Exception) {
            "Error decoding URL"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isEncode, onClick = { isEncode = true }, label = { Text("Encode URL") })
            FilterChip(selected = !isEncode, onClick = { isEncode = false }, label = { Text("Decode URL") })
        }
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("URL / Parameters") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )
        ResultWithCopy(label = "Output", value = output, context = context)
    }
}

// Tool 25: Hash Generator
@Composable
fun HashGeneratorTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("password123") }

    fun hash(str: String, algo: String): String {
        return try {
            val md = MessageDigest.getInstance(algo)
            val bytes = md.digest(str.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input String") },
            modifier = Modifier.fillMaxWidth()
        )

        listOf("MD5", "SHA-1", "SHA-256", "SHA-512").forEach { algo ->
            val hashVal = hash(input, algo)
            ResultWithCopy(label = algo, value = hashVal, context = context)
        }
    }
}

// Tool 26: Morse Code
@Composable
fun MorseCodeTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("SOS") }

    val morseMap = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.",
        'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..",
        'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
        'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..",
        '9' to "----.", '0' to "-----", ' ' to "/"
    )

    val morseOutput = input.uppercase().map { ch -> morseMap[ch] ?: ch.toString() }.joinToString(" ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Text to Encode") },
            modifier = Modifier.fillMaxWidth()
        )
        ResultWithCopy(label = "Morse Code", value = morseOutput, context = context)
    }
}

// Tool 27: Caesar & ROT13
@Composable
fun CaesarCipherTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Hello World") }
    var shift by remember { mutableStateOf(13) }

    fun caesar(str: String, k: Int): String {
        val s = (k % 26 + 26) % 26
        return str.map { ch ->
            when (ch) {
                in 'a'..'z' -> ('a'.code + (ch.code - 'a'.code + s) % 26).toChar()
                in 'A'..'Z' -> ('A'.code + (ch.code - 'A'.code + s) % 26).toChar()
                else -> ch
            }
        }.joinToString("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Text to Cipher") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Shift: $shift", fontWeight = FontWeight.SemiBold)
            Button(onClick = { shift = 13 }) {
                Text("Reset to ROT13")
            }
        }
        Slider(
            value = shift.toFloat(),
            onValueChange = { shift = it.toInt() },
            valueRange = 0f..25f,
            steps = 24
        )

        ResultWithCopy(label = "Shifted Output", value = caesar(input, shift), context = context)
    }
}

// Tool 28: Text Inverter & Reverser
@Composable
fun TextReverserTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("The quick brown fox jumps over the lazy dog") }

    val reversedChars = input.reversed()
    val reversedWords = input.split(" ").reversed().joinToString(" ")

    val upsideDownMap = mapOf(
        'a' to 'ɐ', 'b' to 'q', 'c' to 'ɔ', 'd' to 'p', 'e' to 'ǝ', 'f' to 'ɟ', 'g' to 'ƃ',
        'h' to 'ɥ', 'i' to 'ᴉ', 'j' to 'ɾ', 'k' to 'ʞ', 'l' to 'l', 'm' to 'ɯ', 'n' to 'u',
        'o' to 'o', 'p' to 'd', 'q' to 'b', 'r' to 'ɹ', 's' to 's', 't' to 'ʇ', 'u' to 'n',
        'v' to 'ʌ', 'w' to 'ʍ', 'x' to 'x', 'y' to 'ʎ', 'z' to 'z'
    )
    val upsideDown = input.lowercase().reversed().map { upsideDownMap[it] ?: it }.joinToString("")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input Text") },
            modifier = Modifier.fillMaxWidth()
        )

        ResultWithCopy("Reversed Characters", reversedChars, context)
        ResultWithCopy("Reversed Words", reversedWords, context)
        ResultWithCopy("Upside-Down Text", upsideDown, context)
    }
}

// Tool 29: Binary & Hex Encoder
@Composable
fun BinaryHexTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Kotlin") }

    val binary = input.map { ch ->
        Integer.toBinaryString(ch.code).padStart(8, '0')
    }.joinToString(" ")

    val hex = input.map { ch ->
        Integer.toHexString(ch.code).uppercase().padStart(2, '0')
    }.joinToString(" ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input String") },
            modifier = Modifier.fillMaxWidth()
        )

        ResultWithCopy("Binary (8-bit bytes)", binary, context)
        ResultWithCopy("Hexadecimal", hex, context)
    }
}

// Tool 30: Lorem Ipsum Generator
@Composable
fun LoremIpsumTool() {
    val context = LocalContext.current
    var count by remember { mutableStateOf(3) }
    val base = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

    val output = remember(count) {
        (1..count).joinToString("\n\n") { "$base" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Paragraphs: $count", fontWeight = FontWeight.SemiBold)
        Slider(
            value = count.toFloat(),
            onValueChange = { count = it.toInt() },
            valueRange = 1f..10f,
            steps = 8
        )

        ResultWithCopy("Generated Dummy Text", output, context)
    }
}

// Tool 31: Duplicate Line Stripper
@Composable
fun DuplicateRemoverTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Apple\nBanana\nOrange\nApple\nBanana\nGrape") }

    val lines = input.split("\n")
    val uniqueLines = lines.distinct()
    val removed = lines.size - uniqueLines.size
    val output = uniqueLines.joinToString("\n")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Multiline List") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        )

        ResultCard("Duplicates Removed", "$removed duplicate lines removed", Color(0xFF10B981))
        ResultWithCopy("Deduplicated Result", output, context)
    }
}

// Tool 32: Line Sorter
@Composable
fun LineSorterTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("Zebra\nAlpha\nBeta\nOmega\nGamma") }
    var sortOrder by remember { mutableStateOf(0) } // 0: A-Z, 1: Z-A, 2: Shortest, 3: Longest

    val lines = input.split("\n").filter { it.isNotBlank() }
    val sorted = when (sortOrder) {
        0 -> lines.sorted()
        1 -> lines.sortedDescending()
        2 -> lines.sortedBy { it.length }
        else -> lines.sortedByDescending { it.length }
    }.joinToString("\n")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input Lines") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("A to Z", "Z to A", "Shortest first", "Longest first").forEachIndexed { idx, label ->
                FilterChip(
                    selected = sortOrder == idx,
                    onClick = { sortOrder = idx },
                    label = { Text(label) }
                )
            }
        }

        ResultWithCopy("Sorted Output", sorted, context)
    }
}

// Tool 33: Slugifier
@Composable
fun SlugifierTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("10 Essential Android Architecture Tips for 2026!") }

    val slug = input.lowercase()
        .replace("[^a-z0-9\\s-]".toRegex(), "")
        .trim()
        .replace("\\s+".toRegex(), "-")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Article / Post Title") },
            modifier = Modifier.fillMaxWidth()
        )
        ResultWithCopy("URL-Friendly Slug", slug, context)
    }
}

// Tool 34: Character Frequency Counter
@Composable
fun CharFrequencyTool() {
    var input by remember { mutableStateOf("Mississippi River") }

    val freqs = input.filter { !it.isWhitespace() }
        .groupingBy { it.uppercaseChar() }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Letter Counts (Top 10):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                freqs.take(10).forEach { (char, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("'$char'", fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                        Text("$count times", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// Tool 35: Palindrome Checker
@Composable
fun PalindromeTool() {
    var input by remember { mutableStateOf("A man, a plan, a canal: Panama!") }

    val cleaned = input.filter { it.isLetterOrDigit() }.lowercase()
    val isPal = cleaned.isNotEmpty() && cleaned == cleaned.reversed()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Phrase or Sentence") },
            modifier = Modifier.fillMaxWidth()
        )

        ResultCard(
            label = "Palindrome Status",
            value = if (isPal) "✓ YES! It is a palindrome" else "✗ NO - Not a palindrome",
            color = if (isPal) Color(0xFF10B981) else Color(0xFFEF4444)
        )
        ResultCard(label = "Cleaned Normalized", value = cleaned)
        ResultCard(label = "Reversed Normalized", value = cleaned.reversed())
    }
}

// Tool 36: UUID v4 Generator
@Composable
fun UuidGeneratorTool() {
    val context = LocalContext.current
    var count by remember { mutableStateOf(3) }
    var uppercase by remember { mutableStateOf(false) }
    var uuids by remember {
        mutableStateOf(List(3) { UUID.randomUUID().toString() })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                uuids = List(count) {
                    val id = UUID.randomUUID().toString()
                    if (uppercase) id.uppercase() else id
                }
            }) {
                Text("Generate New")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Uppercase", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(checked = uppercase, onCheckedChange = { uppercase = it })
            }
        }

        uuids.forEach { id ->
            val formatted = if (uppercase) id.uppercase() else id.lowercase()
            ResultWithCopy("UUID v4", formatted, context)
        }
    }
}

// Tool 37: JSON Formatter & Minifier
@Composable
fun JsonFormatterTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("{\"app\":\"mTodo\",\"version\":2.5,\"features\":[\"offline\",\"speed\"]}") }
    var output by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun format(indent: Int) {
        try {
            val trimmed = input.trim()
            output = if (trimmed.startsWith("[")) {
                val arr = JSONArray(trimmed)
                if (indent == 0) arr.toString() else arr.toString(indent)
            } else {
                val obj = JSONObject(trimmed)
                if (indent == 0) obj.toString() else obj.toString(indent)
            }
            errorMsg = null
        } catch (e: Exception) {
            errorMsg = "Invalid JSON: ${e.localizedMessage}"
        }
    }

    LaunchedEffect(input) {
        format(2)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Raw JSON") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { format(2) }) { Text("Pretty (2 sp)") }
            Button(onClick = { format(4) }) { Text("Pretty (4 sp)") }
            FilledTonalButton(onClick = { format(0) }) { Text("Minify") }
        }

        if (errorMsg != null) {
            ResultCard("Syntax Error", errorMsg!!, Color(0xFFEF4444))
        } else {
            ResultWithCopy("Formatted Output", output, context)
        }
    }
}

// Tool 38: Password Entropy Meter
@Composable
fun PasswordEntropyTool() {
    var password by remember { mutableStateOf("SuperSafe#2026!") }

    val poolSize = remember(password) {
        var size = 0
        if (password.any { it.isLowerCase() }) size += 26
        if (password.any { it.isUpperCase() }) size += 26
        if (password.any { it.isDigit() }) size += 10
        if (password.any { !it.isLetterOrDigit() }) size += 32
        max(1, size)
    }

    val entropyBits = remember(password, poolSize) {
        if (password.isEmpty()) 0.0 else password.length * log2(poolSize.toDouble())
    }

    val strength = when {
        entropyBits < 35 -> "Very Weak" to Color(0xFFEF4444)
        entropyBits < 55 -> "Moderate" to Color(0xFFF59E0B)
        entropyBits < 80 -> "Strong" to Color(0xFF3B82F6)
        else -> "Very Strong (Bulletproof)" to Color(0xFF10B981)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter Password to Test") },
            modifier = Modifier.fillMaxWidth()
        )

        ResultCard("Entropy Rating", strength.first, strength.second)
        ResultCard("Information Entropy", "%.1f bits".format(entropyBits))
        ResultCard("Character Pool Size", "$poolSize possible symbols per char")
    }
}

// Tool 39: Whitespace Cleaner
@Composable
fun WhitespaceCleanerTool() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("  Too   many     spaces \n\n\n  between   words and   lines!   ") }

    val cleaned = input.split("\n")
        .map { line -> line.trim().replace("\\s+".toRegex(), " ") }
        .filter { it.isNotBlank() }
        .joinToString("\n")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Messy Text with Whitespace") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        ResultWithCopy("Cleaned Output", cleaned, context)
    }
}

// Tool 40: Markdown Quick Preview
@Composable
fun MarkdownPreviewTool() {
    var mdText by remember { mutableStateOf("# Super Markdown\nThis is a **bold** statement and *italic* note.\n\n- Feature One\n- Feature Two\n- Feature Three\n\n> Simple is better than complex.") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = mdText,
            onValueChange = { mdText = it },
            label = { Text("Markdown Source") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Rendered Preview:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                mdText.split("\n").forEach { line ->
                    val trimmed = line.trim()
                    when {
                        trimmed.startsWith("### ") -> Text(trimmed.removePrefix("### "), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        trimmed.startsWith("## ") -> Text(trimmed.removePrefix("## "), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        trimmed.startsWith("# ") -> Text(trimmed.removePrefix("# "), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        trimmed.startsWith("- ") -> Row {
                            Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(trimmed.removePrefix("- "))
                        }
                        trimmed.startsWith("> ") -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Text(trimmed.removePrefix("> "), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                        trimmed.isNotBlank() -> Text(trimmed)
                    }
                }
            }
        }
    }
}

// Reusable Helper
@Composable
fun ResultWithCopy(label: String, value: String, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText(label, value))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
            }
        }
    }
}
