package com.example.data

enum class TagType {
    RURAL, ADDED, KEY
}

data class HabitItem(
    val id: String,
    val title: String,
    val description: String,
    val tag: String? = null,
    val tagType: TagType? = null
)

data class HabitCategory(
    val id: String,
    val num: String,
    val title: String,
    val icon: String,
    val items: List<HabitItem>
)

object ProtocolData {
    val categories = listOf(
        HabitCategory(
            id = "hair",
            num = "01",
            title = "Hair",
            icon = "💇",
            items = listOf(
                HabitItem(
                    id = "hair_zinc",
                    title = "Zinc 30mg daily",
                    description = "Take after meals — reduces nausea on empty stomach."
                ),
                HabitItem(
                    id = "hair_pumpkin",
                    title = "Pumpkin Seed Oil",
                    description = "1 tbsp daily or pumpkin seeds (कद्दू के बीज) — cheaply available at any mandi."
                ),
                HabitItem(
                    id = "hair_protein",
                    title = "Dietary Protein 130g+ daily",
                    description = "Daal, egg, soyabean, chhena, paneer — your farm & local market can cover this fully."
                ),
                HabitItem(
                    id = "hair_sarso",
                    title = "Sarso Tel (Mustard Oil) Scalp Massage",
                    description = "Weekly oiling, 30 min before bath. Improves blood circulation. Rural gold.",
                    tag = "Rural Hack",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "hair_neem",
                    title = "Neem Leaf Water Rinse",
                    description = "Boil neem leaves, cool it, rinse scalp weekly — natural DHT & dandruff control.",
                    tag = "Added",
                    tagType = TagType.ADDED
                )
            )
        ),
        HabitCategory(
            id = "skin",
            num = "02",
            title = "Skin",
            icon = "✨",
            items = listOf(
                HabitItem(
                    id = "skin_vit_c",
                    title = "Vitamin C Serum — every morning",
                    description = "Apply before going outside. Combats sun damage from outdoor work & farming."
                ),
                HabitItem(
                    id = "skin_niacinamide",
                    title = "Niacinamide — every night",
                    description = "Controls oiliness and pigmentation. Rural skin wins with this one."
                ),
                HabitItem(
                    id = "skin_water",
                    title = "3 litres water daily",
                    description = "Easy target — track with a 1L bottle, refill 3× a day."
                ),
                HabitItem(
                    id = "skin_sugar",
                    title = "Cut refined sugar",
                    description = "Biscuit, white bread, meetha — replace with gud (jaggery) + fruits."
                ),
                HabitItem(
                    id = "skin_cover",
                    title = "Wear cotton & cover skin in sun",
                    description = "Kurta/dupatta over face when working in field — reduces hyperpigmentation.",
                    tag = "Rural Hack",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "skin_multani",
                    title = "Multani Mitti face mask weekly",
                    description = "Deeply cleanses pores, cheap, natural, available everywhere.",
                    tag = "Added",
                    tagType = TagType.ADDED
                )
            )
        ),
        HabitCategory(
            id = "libido",
            num = "03",
            title = "Libido & Testosterone",
            icon = "🔥",
            items = listOf(
                HabitItem(
                    id = "libido_d3",
                    title = "Vitamin D3 + K2 — morning with fat",
                    description = "Take with ghee roti or a handful of peanuts. Sunlight is free — 20 min daily before 9am is a D3 booster too.",
                    tag = "Rural Advantage",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "libido_ashwagandha",
                    title = "Ashwagandha 600mg daily",
                    description = "Take at night. Lowers cortisol, raises T, improves sleep quality. Available at every pansari shop."
                ),
                HabitItem(
                    id = "libido_compound",
                    title = "Heavy compound training 4–5×/week",
                    description = "Squat, push-up, pull-up, deadlift with stones or sacks if no gym. Farm labour counts — add weighted carries.",
                    tag = "Rural Hack",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "libido_kaunch",
                    title = "Kaunch Beej (Mucuna Pruriens)",
                    description = "Natural testosterone support, dopamine precursor. Available at pansari. 1 tsp powder with milk at night.",
                    tag = "Added",
                    tagType = TagType.ADDED
                ),
                HabitItem(
                    id = "libido_ghee",
                    title = "Desi Ghee 1–2 tbsp daily",
                    description = "Healthy fat = raw material for testosterone. Your kitchen probably already has it.",
                    tag = "Added",
                    tagType = TagType.ADDED
                )
            )
        ),
        HabitCategory(
            id = "sleep",
            num = "04",
            title = "Sleep",
            icon = "🌙",
            items = listOf(
                HabitItem(
                    id = "sleep_phone",
                    title = "Phone off at 9:30pm — hard rule",
                    description = "Tell your family too. Rural advantage: quiet nights, no city noise. Use it.",
                    tag = "Non-Negotiable",
                    tagType = TagType.KEY
                ),
                HabitItem(
                    id = "sleep_magnesium",
                    title = "Magnesium Glycinate 400mg — 30 min before bed",
                    description = "Deepens sleep quality. If not available locally, plain Magnesium tablets work at 50% effect."
                ),
                HabitItem(
                    id = "sleep_wake",
                    title = "Sleep before 10:30pm, wake with sun",
                    description = "Rural lifestyle already aligned — protect it. 6–7 hrs quality sleep beats 9 hrs with phone use.",
                    tag = "Rural Advantage",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "sleep_no_heavy",
                    title = "No heavy meals after 8pm",
                    description = "Light dinner — daal, sabzi, roti. Heavy digestion destroys deep sleep.",
                    tag = "Added",
                    tagType = TagType.ADDED
                )
            )
        ),
        HabitCategory(
            id = "mind",
            num = "05",
            title = "Live in Present",
            icon = "🌿",
            items = listOf(
                HabitItem(
                    id = "mind_cold_water",
                    title = "Cold water on face at wake-up — no phone first",
                    description = "Activates the nervous system instantly. Step outside, see the sky first thing. Rural mornings are a gift."
                ),
                HabitItem(
                    id = "mind_breath",
                    title = "5-minute breath reset — before every meal & meeting",
                    description = "Inhale 4 sec → hold 4 → exhale 6. Drops cortisol, sharpens focus."
                ),
                HabitItem(
                    id = "mind_nature",
                    title = "Observe nature daily — 10 minutes",
                    description = "Sit near your field, river, or open ground. No phone, no talk. Watch wind, birds, sky. This is free meditation — desi mindfulness.",
                    tag = "Rural Gold",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "mind_read",
                    title = "Read 10 pages daily — physical book",
                    description = "Trading, biography, health — anything that grows you. Replaces mindless scrolling.",
                    tag = "Added",
                    tagType = TagType.ADDED
                ),
                HabitItem(
                    id = "mind_no_social",
                    title = "No social media before noon",
                    description = "Morning hours are for you, not the algorithm.",
                    tag = "Added",
                    tagType = TagType.ADDED
                )
            )
        ),
        HabitCategory(
            id = "porn",
            num = "06",
            title = "Breaking the Addiction",
            icon = "⚔️",
            items = listOf(
                HabitItem(
                    id = "porn_fast",
                    title = "30-Day Heart Fast",
                    description = "Zero consumption. One day at a time. Track your streak below — your brain needs ~3 weeks to start rewiring dopamine.",
                    tag = "Day 1 Today",
                    tagType = TagType.KEY
                ),
                HabitItem(
                    id = "porn_replace",
                    title = "Replace trigger window with walking or training",
                    description = "Identify the time it usually happens. Fill that window with a walk in the field or a workout. The body can't be horny and exhausted at the same time."
                ),
                HabitItem(
                    id = "porn_journal",
                    title = "When the urge arrives — write in journal",
                    description = "Write exactly what you feel, what triggered it, where you are. The act of writing breaks the trance. Keep a small diary near you."
                ),
                HabitItem(
                    id = "porn_phone",
                    title = "Phone in another room at night",
                    description = "This removes the #1 access point. Simple, and brutal in effectiveness.",
                    tag = "Non-Negotiable",
                    tagType = TagType.KEY
                ),
                HabitItem(
                    id = "porn_handpump",
                    title = "Cold water splash at the moment of urge",
                    description = "Walk to the handpump. Splash face & neck. Physiological pattern interrupt — resets nervous system.",
                    tag = "Rural Hack",
                    tagType = TagType.RURAL
                ),
                HabitItem(
                    id = "porn_gratitude",
                    title = "Daily gratitude — 3 lines in journal at bedtime",
                    description = "Anger, emptiness, and boredom feed the addiction. Gratitude builds a sense of enough. Write 3 things daily.",
                    tag = "Added",
                    tagType = TagType.ADDED
                )
            )
        )
    )
}
