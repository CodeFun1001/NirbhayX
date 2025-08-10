package com.img.nirbhayx.data


import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.img.nirbhayx.R

@Entity(tableName = "safety_tips")
data class SafetyTip(
    @PrimaryKey val id: String,

    val titleKey: String,
    val contentKey: String,

    val category: SafetyCategory,
    val priority: Int = 0,

    @DrawableRes val imageResId: Int? = null,
    val videoUrl: String? = null,

    val steps: List<String>? = null,
    val emergencyNumbers: List<String>? = null,
    val isBookmarked: Boolean = false
)

// SafetyCategory.kt - Enum for categories
enum class SafetyCategory(val displayNameKey: String, val iconRes: Int) {
    SELF_DEFENSE("category_self_defense", R.drawable.ic_self_defense),
    CYBER_SAFETY("category_cyber_safety", R.drawable.ic_cyber_security),
    WOMEN_SAFETY("category_women_safety", R.drawable.ic_women_safety),
    NIGHT_TRAVEL("category_night_travel", R.drawable.ic_night_travel),
    EMERGENCY_CONTACTS("category_emergency_contacts", R.drawable.ic_emergency_contacts),
    PUBLIC_TRANSPORT("category_public_transport", R.drawable.ic_transport),
    HOME_SAFETY("category_home_safety", R.drawable.ic_home),
    WORKPLACE_SAFETY("category_workplace_safety", R.drawable.ic_workplace),
    MENTAL_SAFETY("category_mental_safety", R.drawable.ic_mental_health),
    DISASTER_PREP("category_disaster_prep", R.drawable.ic_disaster),
    CHILD_SAFETY("category_child_safety", R.drawable.ic_child_safety),
    FIRST_AID("category_first_aid", R.drawable.ic_first_aid),
    SENIOR_SAFETY("category_senior_safety", R.drawable.ic_senior_care),
    LGBTQ_SAFETY("category_lgbtq_safety", R.drawable.ic_rainbow),
    SOLO_TRAVEL("category_solo_travel", R.drawable.ic_solo_travel),
    LEGAL_RIGHTS("category_legal_rights", R.drawable.ic_legal)
}

@Entity(tableName = "bookmarked_tips")
data class BookmarkedTip(
    @PrimaryKey val tipId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
