package com.img.nirbhayx.data

import com.img.nirbhayx.R
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class SafetyTipsRepository (
    private val safetyTipDao: SafetyTipDao
) {
    fun getAllTips(): Flow<List<SafetyTip>> = safetyTipDao.getAllTips()

    fun getBookmarkedTips(): Flow<List<SafetyTip>> =
        safetyTipDao.getBookmarkedTips()

    fun isBookmarked(tipId: String): Flow<Boolean> =
        safetyTipDao.isBookmarked(tipId)

    suspend fun toggleBookmark(tipId: String, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            safetyTipDao.removeBookmark(tipId)
        } else {
            safetyTipDao.bookmarkTip(BookmarkedTip(tipId))
        }
    }

    suspend fun initializeTips() {
        val tips = getSafetyTipsData()
        safetyTipDao.insertAllTips(tips)
    }

    private fun getSafetyTipsData(): List<SafetyTip> {
        return listOf(
            // Self-Defense Tips
            SafetyTip(
                id = "self_defense_wrist_grab",
                titleKey = "tip_wrist_grab_escape_title",
                contentKey = "tip_wrist_grab_escape_content",
                category = SafetyCategory.SELF_DEFENSE,
                priority = 1,
                imageResId = R.drawable.ic_alert, //R.drawable.img_wrist_grab_defense
                steps = listOf(
                    "step_wrist_grab_1",
                    "step_wrist_grab_2",
                    "step_wrist_grab_3",
                    "step_wrist_grab_4"
                )
            ),
            SafetyTip(
                id = "self_defense_pressure_points",
                titleKey = "tip_pressure_points_title",
                contentKey = "tip_pressure_points_content",
                category = SafetyCategory.SELF_DEFENSE,
                priority = 2,
                imageResId = R.drawable.ic_alert,//R.drawable.img_pressure_points
                steps = listOf(
                    "step_pressure_eyes",
                    "step_pressure_nose",
                    "step_pressure_throat",
                    "step_pressure_groin",
                    "step_pressure_knees"
                )
            ),
            SafetyTip(
                id = "self_defense_carry_items",
                titleKey = "tip_carry_items_title",
                contentKey = "tip_carry_items_content",
                category = SafetyCategory.SELF_DEFENSE,
                priority = 3,
                steps = listOf(
                    "carry_pepper_spray",
                    "carry_whistle",
                    "carry_personal_alarm",
                    "carry_keys_defense"
                )
            ),

            // Cyber Safety Tips
            SafetyTip(
                id = "cyber_phishing_awareness",
                titleKey = "tip_phishing_title",
                contentKey = "tip_phishing_content",
                category = SafetyCategory.CYBER_SAFETY,
                priority = 1,
                steps = listOf(
                    "verify_sender_email",
                    "check_suspicious_links",
                    "never_share_passwords",
                    "use_two_factor_auth"
                )
            ),
            SafetyTip(
                id = "cyber_social_media_privacy",
                titleKey = "tip_social_privacy_title",
                contentKey = "tip_social_privacy_content",
                category = SafetyCategory.CYBER_SAFETY,
                priority = 2,
                steps = listOf(
                    "review_privacy_settings",
                    "limit_location_sharing",
                    "avoid_oversharing",
                    "verify_friend_requests"
                )
            ),

            // Women's Safety Tips
            SafetyTip(
                id = "women_cab_safety",
                titleKey = "tip_cab_safety_title",
                contentKey = "tip_cab_safety_content",
                category = SafetyCategory.WOMEN_SAFETY,
                priority = 1,
                steps = listOf(
                    "share_ride_details",
                    "use_sos_features",
                    "sit_behind_driver",
                    "keep_emergency_contacts_ready"
                )
            ),
            SafetyTip(
                id = "women_harassment_response",
                titleKey = "tip_harassment_response_title",
                contentKey = "tip_harassment_response_content",
                category = SafetyCategory.WOMEN_SAFETY,
                priority = 2,
                steps = listOf(
                    "speak_loudly_firmly",
                    "move_to_crowd",
                    "yell_fire_not_help",
                    "contact_authorities"
                )
            ),

            // Emergency Contacts
            SafetyTip(
                id = "emergency_numbers_india",
                titleKey = "tip_emergency_numbers_title",
                contentKey = "tip_emergency_numbers_content",
                category = SafetyCategory.EMERGENCY_CONTACTS,
                priority = 1,
                emergencyNumbers = listOf(
                    "112", // All Emergency
                    "100", // Police
                    "101", // Fire
                    "102", // Ambulance
                    "108", // Emergency Ambulance
                    "1091", // Women Helpline
                    "1098", // Child Helpline
                    "181", // Women Helpline (24x7)
                    "1090" // Women Helpline NCW
                )
            ),

            // Night Travel Safety
            SafetyTip(
                id = "night_travel_planning",
                titleKey = "tip_night_travel_title",
                contentKey = "tip_night_travel_content",
                category = SafetyCategory.NIGHT_TRAVEL,
                priority = 1,
                steps = listOf(
                    "plan_route_advance",
                    "stick_well_lit_areas",
                    "walk_confidently",
                    "use_phone_flashlight",
                    "travel_in_groups"
                )
            ),

            // Public Transport Safety
            SafetyTip(
                id = "public_transport_seating",
                titleKey = "tip_transport_seating_title",
                contentKey = "tip_transport_seating_content",
                category = SafetyCategory.PUBLIC_TRANSPORT,
                priority = 1
            ),
            SafetyTip(
                id = "public_transport_awareness",
                titleKey = "tip_transport_awareness_title",
                contentKey = "tip_transport_awareness_content",
                category = SafetyCategory.PUBLIC_TRANSPORT,
                priority = 2
            ),

            // Home Safety
            SafetyTip(
                id = "home_security",
                titleKey = "tip_home_security_title",
                contentKey = "tip_home_security_content",
                category = SafetyCategory.HOME_SAFETY,
                priority = 1
            ),
            SafetyTip(
                id = "home_visitor_verification",
                titleKey = "tip_visitor_verification_title",
                contentKey = "tip_visitor_verification_content",
                category = SafetyCategory.HOME_SAFETY,
                priority = 2
            ),

            // Workplace / College Safety
            SafetyTip(
                id = "workplace_harassment",
                titleKey = "tip_workplace_harassment_title",
                contentKey = "tip_workplace_harassment_content",
                category = SafetyCategory.WORKPLACE_SAFETY,
                priority = 1
            ),
            SafetyTip(
                id = "campus_safety",
                titleKey = "tip_campus_safety_title",
                contentKey = "tip_campus_safety_content",
                category = SafetyCategory.WORKPLACE_SAFETY,
                priority = 2
            ),

            // Mental Safety
            SafetyTip(
                id = "mental_boundaries",
                titleKey = "tip_mental_boundaries_title",
                contentKey = "tip_mental_boundaries_content",
                category = SafetyCategory.MENTAL_SAFETY,
                priority = 1
            ),
            SafetyTip(
                id = "mental_gaslighting_awareness",
                titleKey = "tip_gaslighting_awareness_title",
                contentKey = "tip_gaslighting_awareness_content",
                category = SafetyCategory.MENTAL_SAFETY,
                priority = 2
            ),

            // Disaster Preparedness
            SafetyTip(
                id = "earthquake_safety",
                titleKey = "tip_earthquake_safety_title",
                contentKey = "tip_earthquake_safety_content",
                category = SafetyCategory.DISASTER_PREP,
                priority = 1
            ),
            SafetyTip(
                id = "fire_stampede_safety",
                titleKey = "tip_fire_safety_title",
                contentKey = "tip_fire_safety_content",
                category = SafetyCategory.DISASTER_PREP,
                priority = 2
            ),
            SafetyTip(
                id = "stampede_safety",
                titleKey = "tip_stampede_safety_title",
                contentKey = "tip_stampede_safety_content",
                category = SafetyCategory.DISASTER_PREP,
                priority = 3
            ),

            // Child / Teen Safety
            SafetyTip(
                id = "child_stranger_danger",
                titleKey = "tip_stranger_danger_title",
                contentKey = "tip_stranger_danger_content",
                category = SafetyCategory.CHILD_SAFETY,
                priority = 1
            ),
            SafetyTip(
                id = "child_online_safety",
                titleKey = "tip_internet_safety_kids_title",
                contentKey = "tip_internet_safety_kids_content",
                category = SafetyCategory.CHILD_SAFETY,
                priority = 2
            ),

            // First Aid
            SafetyTip(
                id = "first_aid_basics",
                titleKey = "tip_basic_first_aid_title",
                contentKey = "tip_basic_first_aid_content",
                category = SafetyCategory.FIRST_AID,
                priority = 1
            ),
            SafetyTip(
                id = "cpr_basics",
                titleKey = "tip_cpr_basics_title",
                contentKey = "tip_cpr_basics_content",
                category = SafetyCategory.FIRST_AID,
                priority = 2
            ),

            // Senior Safety
            SafetyTip(
                id = "senior_home_safety",
                titleKey = "tip_senior_home_safety_title",
                contentKey = "tip_senior_home_safety_content",
                category = SafetyCategory.SENIOR_SAFETY,
                priority = 1
            ),
            SafetyTip(
                id = "senior_scam_awareness",
                titleKey = "tip_senior_scam_awareness_title",
                contentKey = "tip_senior_scam_awareness_content",
                category = SafetyCategory.SENIOR_SAFETY,
                priority = 2
            ),

            // LGBTQ+ Safety
            SafetyTip(
                id = "lgbtq_community_safety",
                titleKey = "tip_lgbtq_community_safety_title",
                contentKey = "tip_lgbtq_community_safety_content",
                category = SafetyCategory.LGBTQ_SAFETY,
                priority = 1
            ),
            SafetyTip(
                id = "lgbtq_support_resources",
                titleKey = "tip_lgbtq_support_resources_title",
                contentKey = "tip_lgbtq_support_resources_content",
                category = SafetyCategory.LGBTQ_SAFETY,
                priority = 2
            ),

            // Solo Travel
            SafetyTip(
                id = "solo_travel_preparation",
                titleKey = "tip_solo_travel_preparation_title",
                contentKey = "tip_solo_travel_preparation_content",
                category = SafetyCategory.SOLO_TRAVEL,
                priority = 1
            ),
            SafetyTip(
                id = "solo_travel_accommodation",
                titleKey = "tip_solo_travel_accommodation_title",
                contentKey = "tip_solo_travel_accommodation_content",
                category = SafetyCategory.SOLO_TRAVEL,
                priority = 2
            ),

            // Legal Rights
            SafetyTip(
                id = "legal_womens_rights",
                titleKey = "tip_legal_rights_women_title",
                contentKey = "tip_legal_rights_women_content",
                category = SafetyCategory.LEGAL_RIGHTS,
                priority = 1
            ),
            SafetyTip(
                id = "legal_cyber_reporting",
                titleKey = "tip_cybercrime_reporting_title",
                contentKey = "tip_cybercrime_reporting_content",
                category = SafetyCategory.LEGAL_RIGHTS,
                priority = 2
            )
        )
    }
}