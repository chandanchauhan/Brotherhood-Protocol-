package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HabitCategory
import com.example.data.HabitItem
import com.example.data.ProtocolData
import com.example.data.TagType
import com.example.ui.HabitViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ProtocolAppScreen(
                        viewModel = viewModel,
                        isDark = isDarkMode,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ProtocolAppScreen(
    viewModel: HabitViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val displayDate by viewModel.displayDate.collectAsState()
    val completions by viewModel.todayCompletions.collectAsState()
    val isCompletedDay by viewModel.isTodayCompletedDay.collectAsState()
    val streakCount by viewModel.currentStreak.collectAsState()

    val scrollState = rememberScrollState()

    // Base background with subtle gradients mirroring the HTML's background gradients
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                if (isDark) {
                    // radial background glow (top-left mud brown shadow)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x334A2F1A), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = size.width * 0.7f
                        )
                    )
                    // bottom-right leaf green glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1A3D6B3F), Color.Transparent),
                            center = Offset(size.width, size.height),
                            radius = size.width * 0.6f
                        )
                    )
                } else {
                    // Soft warm parchment paper gradients for light theme
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0x0A8B5E3C), Color.Transparent)
                        )
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. HERO HEADER AREA
            HeroHeader(
                isDark = isDark,
                onToggleTheme = { viewModel.toggleDarkMode() }
            )

            // 2. DATE SELECTOR
            DateSelector(
                displayDate = displayDate,
                selectedDate = selectedDate,
                onPreviousDay = { viewModel.adjustDate(-1) },
                onNextDay = { viewModel.adjustDate(1) },
                isNextDisabled = viewModel.isFutureDate(1),
                isDark = isDark
            )

            // Daily Progress Summary Bar
            DailyProgressPanel(
                categories = ProtocolData.categories,
                completions = completions,
                isDark = isDark
            )

            // 3. MAIN CARDS RESPONSIVE GRID LAYOUT
            ResponsiveCardsGrid(
                categories = ProtocolData.categories,
                completions = completions,
                isDark = isDark,
                onToggleHabit = { habitId -> viewModel.toggleHabitCompletion(habitId) }
            )

            // 3.5 DAILY REFLECTIONS JOURNAL
            val journalEntry by viewModel.currentJournalEntry.collectAsState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .widthIn(max = 600.dp)
            ) {
                JournalCard(
                    selectedDate = selectedDate,
                    journalEntry = journalEntry,
                    onSaveJournal = { wins, setbacks -> viewModel.saveJournalEntry(wins, setbacks) },
                    isDark = isDark,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4. CREED & STREAK SECTION
            CreedAndStreakSection(
                streakCount = streakCount,
                isCompletedDay = isCompletedDay,
                onToggleCompleteDay = { viewModel.toggleDayCompleted() },
                isDark = isDark
            )
        }
    }
}

@Composable
fun HeroHeader(
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val goldColor = if (isDark) Color(0xFFF2C45A) else Color(0xFF2C1810)
    val dividerColor = if (isDark) Color(0x33D4A853) else Color(0x332C1810)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Repeating fine diagonal lines to simulate the HTML's background repeatable linear gradient
                val interval = 100f
                for (x in -size.height.toInt()..size.width.toInt() step interval.toInt()) {
                    drawLine(
                        color = if (isDark) Color(0x08D4A853) else Color(0x062C1810),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x + size.height, size.height),
                        strokeWidth = 1f
                    )
                }
            }
            .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dark Mode Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .testTag("theme_toggle")
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x112C1810))
                    .size(44.dp)
            ) {
                Text(
                    text = if (isDark) "☀️" else "🌙",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "GHAZIPUR → GREATNESS",
            style = MaterialTheme.typography.labelLarge,
            color = if (isDark) Color(0xFFD4A853) else Color(0xFF8B5E3C),
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "THE BROTHERHOOD\nPROTOCOL",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 42.sp,
                lineHeight = 44.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black
            ),
            color = goldColor,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal elegant hairline gradient divider
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, if (isDark) Color(0xFFD4A853) else Color(0xFF2C1810), Color.Transparent)
                    )
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "मिट्टी से उठा — इरादे से चला",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (isDark) Color(0xFFD4A853) else Color(0xFF8B5E3C),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Built for the rural man who has sunlight, space, hard work, and zero excuses. No gym membership needed. No fancy supplements. Just discipline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 440.dp)
                .padding(horizontal = 8.dp),
            lineHeight = 18.sp
        )
    }
}

@Composable
fun DateSelector(
    displayDate: String,
    selectedDate: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    isNextDisabled: Boolean,
    isDark: Boolean
) {
    val containerBg = if (isDark) Color(0xFF4A2F1A).copy(alpha = 0.3f) else Color(0xFFF2EAD8).copy(alpha = 0.6f)
    val borderColor = if (isDark) Color(0xFF8B5E3C).copy(alpha = 0.25f) else Color(0xFF8B5E3C).copy(alpha = 0.15f)

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .widthIn(max = 380.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPreviousDay,
            modifier = Modifier.testTag("prev_day_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Day",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = selectedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        IconButton(
            onClick = onNextDay,
            enabled = !isNextDisabled,
            modifier = Modifier.testTag("next_day_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Day",
                tint = if (isNextDisabled) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DailyProgressPanel(
    categories: List<HabitCategory>,
    completions: Map<String, Boolean>,
    isDark: Boolean
) {
    val allHabitIds = categories.flatMap { it.items }.map { it.id }
    val completedCount = allHabitIds.count { completions[it] == true }
    val totalCount = allHabitIds.size
    val percentage = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val panelBg = if (isDark) Color(0xFF4A2F1A).copy(alpha = 0.4f) else Color(0x338B5E3C)
    val progressColor = if (isDark) Color(0xFFF2C45A) else Color(0xFF3D6B3F)

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(panelBg)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DAILY METRIC PROGRESS",
                style = MaterialTheme.typography.labelLarge,
                color = if (isDark) Color(0xFFD4A853) else Color(0xFF2C1810)
            )
            Text(
                text = "$completedCount / $totalCount completed",
                style = MaterialTheme.typography.labelLarge,
                color = if (isDark) Color(0xFFFAF3E0) else Color(0xFF2C1810)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF2C1810) else Color(0x1F2C1810))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(progressColor)
            )
        }
    }
}

@Composable
fun ResponsiveCardsGrid(
    categories: List<HabitCategory>,
    completions: Map<String, Boolean>,
    isDark: Boolean,
    onToggleHabit: (String) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val width = maxWidth
        if (width < 640.dp) {
            // 1 column list layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categories.forEach { category ->
                    HabitCategoryCard(
                        category = category,
                        completions = completions,
                        isDark = isDark,
                        onToggleHabit = onToggleHabit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else if (width < 960.dp) {
            // 2 column layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column chunks
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    categories.filterIndexed { index, _ -> index % 2 == 0 }.forEach { category ->
                        HabitCategoryCard(
                            category = category,
                            completions = completions,
                            isDark = isDark,
                            onToggleHabit = onToggleHabit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Right Column chunks
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    categories.filterIndexed { index, _ -> index % 2 == 1 }.forEach { category ->
                        HabitCategoryCard(
                            category = category,
                            completions = completions,
                            isDark = isDark,
                            onToggleHabit = onToggleHabit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // 3 column layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (col in 0..2) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        categories.filterIndexed { index, _ -> index % 3 == col }.forEach { category ->
                            HabitCategoryCard(
                                category = category,
                                completions = completions,
                                isDark = isDark,
                                onToggleHabit = onToggleHabit,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

// Model for category visual theme
data class CategoryVisual(
    val accentColors: Pair<Color, Color>,
    val titleTextCol: Color,
    val dotCol: Color
)

fun getCategoryVisual(categoryId: String, isDark: Boolean): CategoryVisual {
    return when (categoryId) {
        "hair" -> CategoryVisual(
            accentColors = Pair(Color(0xFFD4A853), Color(0xFF8B5E3C)),
            titleTextCol = if (isDark) Color(0xFFD4A853) else Color(0xFF7A581F),
            dotCol = Color(0xFFD4A853)
        )
        "skin" -> CategoryVisual(
            accentColors = Pair(Color(0xFFE8A598), Color(0xFFC07B8B)),
            titleTextCol = if (isDark) Color(0xFFE8A598) else Color(0xFF9E5A4D),
            dotCol = Color(0xFFC07B8B)
        )
        "libido" -> CategoryVisual(
            accentColors = Pair(Color(0xFFC0392B), Color(0xFF8B1A1A)),
            titleTextCol = if (isDark) Color(0xFFE57373) else Color(0xFF9E2316),
            dotCol = Color(0xFFE57373)
        )
        "sleep" -> CategoryVisual(
            accentColors = Pair(Color(0xFF5B9BD5), Color(0xFF2C5F8A)),
            titleTextCol = if (isDark) Color(0xFF5B9BD5) else Color(0xFF285C87),
            dotCol = Color(0xFF5B9BD5)
        )
        "mind" -> CategoryVisual(
            accentColors = Pair(Color(0xFF3D6B3F), Color(0xFF2A4F2C)),
            titleTextCol = if (isDark) Color(0xFF7FC97F) else Color(0xFF2B572B),
            dotCol = Color(0xFF7FC97F)
        )
        else -> CategoryVisual( // porn
            accentColors = Pair(Color(0xFF6B7280), Color(0xFF374151)),
            titleTextCol = if (isDark) Color(0xFF9CA3AF) else Color(0xFF4B5563),
            dotCol = Color(0xFF6B7280)
        )
    }
}

@Composable
fun HabitCategoryCard(
    category: HabitCategory,
    completions: Map<String, Boolean>,
    isDark: Boolean,
    onToggleHabit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = getCategoryVisual(category.id, isDark)
    val gradientBrush = Brush.horizontalGradient(listOf(visual.accentColors.first, visual.accentColors.second))

    val completedInCategory = category.items.count { completions[it.id] == true }
    val totalInCategory = category.items.size

    Card(
        modifier = modifier
            .testTag("category_card_${category.id}")
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isDark) Color(0xFF8B5E3C).copy(alpha = 0.15f) else Color(0xFF8B5E3C).copy(alpha = 0.25f),
                shape = RoundedCornerShape(0.dp) // Maintain HTML box aesthetic
            ),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Card accent top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(gradientBrush)
            )

            // Category Number giant watermark in top right
            Text(
                text = category.num,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = 8.dp)
            )

            Column(
                modifier = Modifier.padding(top = 22.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                // Category Header (Icon, Title, category progress)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = category.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = visual.titleTextCol
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                // Small progress count
                Text(
                    text = "$completedInCategory / $totalInCategory checked",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 34.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Item lists
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    category.items.forEach { item ->
                        HabitItemView(
                            item = item,
                            isCompleted = completions[item.id] == true,
                            dotColor = visual.dotCol,
                            isDark = isDark,
                            onToggle = { onToggleHabit(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitItemView(
    item: HabitItem,
    isCompleted: Boolean,
    dotColor: Color,
    isDark: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_item_row_${item.id}")
            .clickable(onClick = onToggle)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        // animated checkbox/circle bullet
        Box(
            modifier = Modifier
                .padding(top = 4.dp, end = 10.dp)
                .size(18.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isCompleted) dotColor else dotColor.copy(alpha = 0.4f),
                    shape = CircleShape
                )
                .background(
                    if (isCompleted) dotColor.copy(alpha = 0.15f) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = dotColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Checklist text & description
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Custom styled tag if available
            item.tag?.let { tagText ->
                Spacer(modifier = Modifier.height(4.dp))
                TagChip(
                    text = tagText,
                    tagType = item.tagType ?: TagType.RURAL,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
fun TagChip(
    text: String,
    tagType: TagType,
    isDark: Boolean
) {
    val (bgColor, textColor, borderColor) = when (tagType) {
        TagType.RURAL -> Triple(
            if (isDark) Color(0x3D3D6B3F) else Color(0x223D6B3F),
            if (isDark) Color(0xFF7FC97F) else Color(0xFF2E5E2E),
            if (isDark) Color(0x7F3D6B3F) else Color(0x443D6B3F)
        )
        TagType.ADDED -> Triple(
            if (isDark) Color(0x335B9BD5) else Color(0x1F5B9BD5),
            if (isDark) Color(0xFF93C5FD) else Color(0xFF2B5F8D),
            if (isDark) Color(0x665B9BD5) else Color(0x445B9BD5)
        )
        TagType.KEY -> Triple(
            if (isDark) Color(0x33D4A853) else Color(0x22D4A853),
            if (isDark) Color(0xFFF2C45A) else Color(0xFF70521C),
            if (isDark) Color(0x66D4A853) else Color(0x44D4A853)
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            ),
            color = textColor
        )
    }
}

@Composable
fun CreedAndStreakSection(
    streakCount: Int,
    isCompletedDay: Boolean,
    onToggleCompleteDay: () -> Unit,
    isDark: Boolean
) {
    val dividerColor = if (isDark) Color(0xFFD4A853).copy(alpha = 0.15f) else Color(0xFF2C1810).copy(alpha = 0.15f)
    val wheatColor = if (isDark) Color(0xFFD4A853) else Color(0xFF8B5E3C)
    val targetVal = 30

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Subtle horizontal divider on top of manifesto
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
            .padding(vertical = 36.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "THE RURAL MAN'S CREED",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = wheatColor,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"Gaon mein paida hua — iska matlab weak nahi.\"",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "\"Sunlight, mitti, mehnat — yeh tera gym hai.\"",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "\"Ek din mein nahi badlega — ek din se shuru hoga.\"",
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // STREAK PANEL BOX
        Row(
            modifier = Modifier
                .testTag("streak_panel")
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFFD4A853).copy(alpha = 0.25f) else Color(0xFF8B5E3C).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DAY STREAK",
                    style = MaterialTheme.typography.labelSmall,
                    color = wheatColor,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = String.format("%03d", streakCount),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = if (isDark) Color(0xFFF2C45A) else Color(0xFF2C1810),
                    lineHeight = 36.sp
                )
            }

            // Divider
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(if (isDark) Color(0x33D4A853) else Color(0x338B5E3C))
            )
            Spacer(modifier = Modifier.width(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TARGET",
                    style = MaterialTheme.typography.labelSmall,
                    color = wheatColor,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = String.format("%03d", targetVal),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = if (isDark) Color(0xFFF2C45A) else Color(0xFF2C1810),
                    lineHeight = 36.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // MARK DAY COMPLETED ACTION BUTTON
        Button(
            onClick = onToggleCompleteDay,
            modifier = Modifier
                .testTag("complete_day_button")
                .height(52.dp)
                .widthIn(min = 280.dp),
            shape = RoundedCornerShape(8.dp),
            colors = if (isCompletedDay) {
                ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF3D6B3F) else Color(0xFF2B572B),
                    contentColor = Color.White
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFFF2C45A) else Color(0xFF2C1810),
                    contentColor = if (isDark) Color(0xFF2C1810) else Color.White
                )
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isCompletedDay) Icons.Default.Done else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCompletedDay) "COMPLETED TODAY! (TAP TO UNDO)" else "MARK DATE COMPLETED",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Track your days. Stick where you can see it daily. Check off what you did. That's it.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 11.sp
            ),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun JournalCard(
    selectedDate: String,
    journalEntry: com.example.data.JournalEntry?,
    onSaveJournal: (wins: String, setbacks: String) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    // Keep local states synced with selected date or database entry changing
    var winsText by remember(selectedDate, journalEntry) { mutableStateOf(journalEntry?.winsText ?: "") }
    var setbacksText by remember(selectedDate, journalEntry) { mutableStateOf(journalEntry?.setbacksText ?: "") }
    var isSavingFeedbackVisible by remember(selectedDate, journalEntry) { mutableStateOf(false) }

    val accentBrush = Brush.horizontalGradient(
        colors = if (isDark) {
            listOf(Color(0xFF8B5E3C), Color(0xFFD4A853))
        } else {
            listOf(Color(0xFF4A2F1A), Color(0xFF8B5E3C))
        }
    )

    Card(
        modifier = modifier
            .testTag("journal_card")
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isDark) Color(0xFF8B5E3C).copy(alpha = 0.15f) else Color(0xFF8B5E3C).copy(alpha = 0.25f),
                shape = RoundedCornerShape(0.dp)
            ),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentBrush)
            )

            Text(
                text = "JN",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = 8.dp)
            )

            Column(
                modifier = Modifier.padding(top = 22.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📝", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Daily Discipline Journal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = if (isDark) Color(0xFFF2C45A) else Color(0xFF2C1810)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Reflect honestly on your wins and setbacks for this date below.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 34.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "TODAY'S WINS (आज की जीतैं)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) Color(0xFF7FC97F) else Color(0xFF2E5E2E),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = winsText,
                    onValueChange = { 
                        winsText = it
                        isSavingFeedbackVisible = false 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_wins_input"),
                    placeholder = { 
                        Text(
                            "What discipline did you build today? e.g., Worked out, didn't check social media, read 10 pages...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp
                        ) 
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Color(0xFF7FC97F) else Color(0xFF2E5E2E),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        unfocusedContainerColor = if (isDark) Color(0xFF2C1810).copy(alpha = 0.3f) else Color(0xFFFAF5E8).copy(alpha = 0.3f),
                        focusedContainerColor = if (isDark) Color(0xFF2C1810).copy(alpha = 0.5f) else Color(0xFFFAF5E8).copy(alpha = 0.5f)
                    ),
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "SETBACKS & TRIGGERS (कमियां और चुनौतियां)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) Color(0xFFE57373) else Color(0xFF9E2316),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = setbacksText,
                    onValueChange = { 
                        setbacksText = it
                        isSavingFeedbackVisible = false 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_setbacks_input"),
                    placeholder = { 
                        Text(
                            "What challenges did you face? Any triggers or urges? How will you counter them next time?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp
                        ) 
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Color(0xFFE57373) else Color(0xFF9E2316),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        unfocusedContainerColor = if (isDark) Color(0xFF2C1810).copy(alpha = 0.3f) else Color(0xFFFAF5E8).copy(alpha = 0.3f),
                        focusedContainerColor = if (isDark) Color(0xFF2C1810).copy(alpha = 0.5f) else Color(0xFFFAF5E8).copy(alpha = 0.5f)
                    ),
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        val hasUnsavedChanges = winsText != (journalEntry?.winsText ?: "") || setbacksText != (journalEntry?.setbacksText ?: "")
                        if (isSavingFeedbackVisible) {
                            Text(
                                text = "✓ Reflection saved successfully!",
                                color = if (isDark) Color(0xFF7FC97F) else Color(0xFF2E5E2E),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else if (hasUnsavedChanges) {
                            Text(
                                text = "● Unsaved changes...",
                                color = if (isDark) Color(0xFFFAF3E0).copy(alpha = 0.5f) else Color(0xFF2C1810).copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp
                            )
                        } else if (journalEntry != null) {
                            Text(
                                text = "• Reflection logged for this day",
                                color = if (isDark) Color(0xFFD4A853).copy(alpha = 0.6f) else Color(0xFF8B5E3C).copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSaveJournal(winsText, setbacksText)
                            isSavingFeedbackVisible = true
                        },
                        modifier = Modifier
                            .testTag("save_journal_button")
                            .height(40.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFFD4A853) else Color(0xFF2C1810),
                            contentColor = if (isDark) Color(0xFF2C1810) else Color.White
                        )
                    ) {
                        Text(
                            text = "SAVE JOURNAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
