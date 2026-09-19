package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class TrainType {
    DIESEL,
    STEAM,
    ELECTRIC_MAGLEV
}

enum class TrainDriveMode {
    FORWARD,
    NEUTRAL,
    REVERSE,
    DYNAMIC_BRAKE,
    EMERGENCY_BRAKE
}

enum class SoundProfileType {
    EMD_2STROKE_DIESEL,
    GE_4STROKE_TURBODIESEL,
    DELTIC_NAPIER_DIESEL,
    F40PH_SCREAMER_DIESEL,
    SNCF_DIESEL_HYDRAULIC,
    ALCO_V16_DIESEL,
    BIG_BOY_ARTICULATED_STEAM,
    PACIFIC_CLASSIC_STEAM,
    DREYFUSS_STREAMLINE_STEAM,
    ROYAL_HUDSON_STEAM,
    GHOST_SPECTRAL_STEAM,
    HYPER_COLOSSUS_STEAM,
    LNER_A3_STEAM_WHISTLE,
    EURO_ELECTRIC_103,
    TGV_HIGH_SPEED_ELECTRIC,
    SHINKANSEN_BULLET_ELECTRIC,
    SIEMENS_VECTRON_ELECTRIC,
    PRR_GG1_ARTDECO_ELECTRIC,
    SOVIET_VL85_ELECTRIC,
    CLASS_390_PENDOLINO_ELECTRIC,
    GAS_TURBINE_BIG_BLOW,
    CYBER_MAGLEV_PLASMA
}

enum class RailCarType {
    LUMBER_FLATCAR,
    FUEL_TANKER,
    CONTAINER_BOXCAR,
    PASSENGER_COACH,
    HEAVY_COAL_HOPPER,
    SHINKANSEN_E2_COACH,
    PENDOLINO_COACH,
    PENDOLINO_REAR_CAB,
    METRA_GALLERY_COACH,
    METRA_CAB_CAR,
    CD_PENDOLINO_COACH,
    CD_PENDOLINO_CAB,
    CD_COMFORTJET_COACH,
    CD_REGIO_COACH,
    AMTRAK_ACELA_COACH,
    AMTRAK_ACELA_CAB,
    STEAM_PULLMAN_COACH,
    TOW_RESCUE_TENDER,
    CSX_FREIGHT_BOXCAR,
    CSX_COAL_HOPPER,
    BNSF_HERITAGE_BOXCAR,
    BNSF_COAL_HOPPER,
    BN_CASCADE_GREEN_HOPPER,
    BN_CASCADE_GREEN_BOXCAR,
    BN_EXECUTIVE_BOXCAR,
    SANTA_FE_WARBONNET_BOXCAR,
    SANTA_FE_BLUEBONNET_TANKER,
    UNION_PACIFIC_HOPPER,
    NORFOLK_SOUTHERN_HOPPER
}

enum class TrainCategory(val displayName: String, val icon: String, val subtitle: String) {
    ALL("All Fleet", "🚆", "Complete Locomotive & Multiple Unit Roster"),
    COMMUTER_METRA("Commuter & Metra", "🏙️", "Metra Bi-Level Express, Calumet Electrics & Regional Commuter Lines"),
    CZECH_CD("Czech Railways (ČD)", "🇨🇿", "České Dráhy SuperCity Pendolino, ComfortJet, Panter & Diesel Fleet"),
    HIGH_SPEED("High-Speed & Bullet", "⚡", "Shinkansen, Acela Express, Tilting Pendolino & Supersonic Maglev"),
    STEAM_TITAN("Steam Titans", "🚂", "Union Pacific Big Boy, NYC Dreyfuss Hudson, Pacific 4-6-2 & Steam Colossi"),
    FREIGHT_DIESEL("Heavy Freight & Diesel", "📦", "EMD Road Switchers, SD70ACe Haulers & Classic Heavy Diesels"),
    ELECTRIC_CHAMPION("Electric Champions", "⚡", "Siemens Vectron, DB Class 103, PRR GG1 Art-Deco & Electric Giants")
}

enum class EnvironmentType {
    DESERT_CANYON,
    ALPINE_PEAKS,
    INDUSTRIAL_VALLEY,
    REDWOOD_COAST,
    ARCTIC_PASS
}

data class LiveryVariant(
    val id: String,
    val trainId: String,
    val variantCode: String, // e.g. "V1", "V2", "V3"
    val displayName: String,
    val operatorName: String,
    val subtitle: String,
    val priceDiamonds: Int = 0,
    val primaryColor: Long,
    val secondaryColor: Long,
    val accentColor: Long,
    val description: String
) {
    val versionCode: String get() = variantCode
    val name: String get() = displayName
    val bodyColorHex: Long get() = primaryColor
    val roofColorHex: Long get() = secondaryColor
    val accentColorHex: Long get() = accentColor
}

data class TrainModel(
    val id: String,
    val name: String,
    val type: TrainType,
    val soundProfile: SoundProfileType = SoundProfileType.EMD_2STROKE_DIESEL,
    val countryFlag: String,
    val baseSpeed: Float,        // e.g. 42.0f
    val baseReliability: Float,  // e.g. 12.3f
    val basePower: Float,        // e.g. 24.9f
    val baseAdherence: Float,    // e.g. 7.8f
    val priceGold: Int = 0,
    val priceDiamonds: Int = 0,
    val isSecret: Boolean = false,
    val maxUpgradeLevel: Int = 50,
    val description: String = "",
    val defaultBodyColor: Long = 0xFFF5B700,
    val defaultRoofColor: Long = 0xFF2A2E33,
    val defaultStripeColor: Long = 0xFFD32F2F,
    val customLiveryVariants: List<LiveryVariant> = emptyList()
) {
    val liveryVariants: List<LiveryVariant>
        get() = if (customLiveryVariants.isNotEmpty()) {
            customLiveryVariants
        } else {
            GameContent.getLiveryVariantsForTrain(id)
        }
    val availableLiveries: List<LiveryVariant> get() = liveryVariants

    val category: TrainCategory
        get() = when {
            id.startsWith("metra_") || id == "emd_f40ph_metra" -> TrainCategory.COMMUTER_METRA
            id.startsWith("cd_") || id.startsWith("nightjet_") -> TrainCategory.CZECH_CD
            type == TrainType.STEAM -> TrainCategory.STEAM_TITAN
            id.contains("shinkansen") || id.contains("acela") || id.contains("maglev") || id.contains("pendolino") || id.contains("american_flyer") -> TrainCategory.HIGH_SPEED
            id.contains("vectron") || id.contains("class_103") || id.contains("gg1") || id.contains("class_92") || id.contains("deltic") || id.contains("vl85") || type == TrainType.ELECTRIC_MAGLEV -> TrainCategory.ELECTRIC_CHAMPION
            type == TrainType.DIESEL -> TrainCategory.FREIGHT_DIESEL
            else -> TrainCategory.COMMUTER_METRA
        }
}

data class RailCar(
    val id: String,
    val name: String,
    val type: RailCarType,
    val weightKg: Float,
    val cargoDescription: String
)

data class UpgradeModule(
    val id: String,
    val name: String,
    val iconType: String,
    val currentLevel: Int,
    val maxLevel: Int = 50,
    val baseCostSilver: Int,
    val statBoostLabel: String
)

enum class ContractCategory {
    FREIGHT,
    CARGO,
    PASSENGER
}

data class ContractJob(
    val id: String,
    val title: String,
    val category: ContractCategory = ContractCategory.FREIGHT,
    val originStation: String,
    val destinationStation: String,
    val distanceMeters: Float, // Represented in km scale in gameplay
    val hillSeverity: String,
    val environment: EnvironmentType,
    val railCars: List<RailCar>,
    val rewardSilver: Int,
    val rewardGold: Int,
    val rewardDiamonds: Int,
    val xpReward: Int,
    val requiredDriverLevel: Int = 1,
    val isHighTier: Boolean = false,
    val estimatedMinutes: String = "1-2 min"
)

typealias HorrorMusicTrack = BreakcoreMusicTrack

data class BreakcoreMusicTrack(
    val id: String,
    val title: String,
    val subtitle: String,
    val tempoBpm: Int,
    val scaleRootFreq: Float,
    val moodDescription: String
)

data class PromoCode(
    val code: String,
    val rewardDescription: String,
    val silverReward: Int = 0,
    val goldReward: Int = 0,
    val diamondReward: Int = 0,
    val blueprintReward: Int = 0,
    val secretTrainId: String? = null,
    val secretTrainIds: List<String> = emptyList(),
    val coolantReward: Int = 0,
    val sandReward: Int = 0,
    val skinColor: Long? = null
)

object GameContent {

    val PENDOLINO_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_VIRGIN",
            trainId = "secret_virgin_pendolino_390",
            variantCode = "V1",
            displayName = "( V1 ) Virgin Trains",
            operatorName = "Virgin Trains West Coast",
            subtitle = "Red & Silver Sweeping Ribbon",
            priceDiamonds = 0,
            primaryColor = 0xFFE2E8F0L,
            secondaryColor = 0xFFDC2626L,
            accentColor = 0xFFFACC15L,
            description = "Iconic Virgin West Coast high-speed bullet train with sweeping Virgin red ribbon, platinum silver body, and warning yellow nose shield."
        ),
        LiveryVariant(
            id = "V2_CROSSCOUNTRY",
            trainId = "secret_virgin_pendolino_390",
            variantCode = "V2",
            displayName = "( V2 ) CrossCountry",
            operatorName = "CrossCountry UK",
            subtitle = "Maroon, White & Magenta Doors (Photo Spec)",
            priceDiamonds = 15,
            primaryColor = 0xFFF8FAFCL,
            secondaryColor = 0xFF2A1521L,
            accentColor = 0xFFD92662L,
            description = "Accurate CrossCountry Super Voyager / Class 390 livery with dark maroon geometric cab, vermilion red roof line, crisp white body, and vivid magenta passenger doors."
        ),
        LiveryVariant(
            id = "V3_AVANTI",
            trainId = "secret_virgin_pendolino_390",
            variantCode = "V3",
            displayName = "( V3 ) Avanti West Coast",
            operatorName = "Avanti West Coast",
            subtitle = "Petrol Teal & Pearl Triangle (Photo Spec)",
            priceDiamonds = 15,
            primaryColor = 0xFFF1F5F9L,
            secondaryColor = 0xFF164E63L,
            accentColor = 0xFFDC2626L,
            description = "Official Avanti West Coast flagship livery featuring deep petrol teal aerodynamic cab front, vermilion red roof, pearl white bodyside, and iconic red triangle chevron emblems."
        )
    )

    // === METRA 7-TRAIN LIVERY ROSTER (3 DESIGNS PER TRAIN) ===
    val METRA_F40PH_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_METRA_CLASSIC",
            trainId = "metra_f40ph_screamer",
            variantCode = "V1",
            displayName = "( V1 ) Classic Metra Blue & Orange",
            operatorName = "Metra Commuter Rail",
            subtitle = "Silver Fluted Body & Blue Cowl",
            priceDiamonds = 0,
            primaryColor = 0xFFCBD5E1L, // Silver Stainless
            secondaryColor = 0xFF1E3A8AL, // Metra Navy Blue
            accentColor = 0xFFF97316L, // Warning Orange
            description = "Iconic Chicago Metra commuter scheme with navy blue cowl front, brushed stainless steel ribbed bodyside, and vivid safety orange warning nose stripes."
        ),
        LiveryVariant(
            id = "V2_METRA_PATRIOT",
            trainId = "metra_f40ph_screamer",
            variantCode = "V2",
            displayName = "( V2 ) Metra Patriot Veteran Tribute",
            operatorName = "Metra #120 'Patriot'",
            subtitle = "Stars & Stripes Digital Camo",
            priceDiamonds = 10,
            primaryColor = 0xFF0F172AL, // Navy Dark
            secondaryColor = 0xFFDC2626L, // Patriot Red
            accentColor = 0xFFFFFFFFL, // White Stars
            description = "Metra's special tribute unit honoring military veterans with patriotic stars, stripes, navy blue cab, and commemorative graphics."
        ),
        LiveryVariant(
            id = "V3_METRA_RAVEN",
            trainId = "metra_f40ph_screamer",
            variantCode = "V3",
            displayName = "( V3 ) Modern Metra Raven Fade",
            operatorName = "Metra Modernized",
            subtitle = "Charcoal Roof & Cyan Gradient",
            priceDiamonds = 15,
            primaryColor = 0xFFE2E8F0L, // Silver
            secondaryColor = 0xFF1E293BL, // Charcoal
            accentColor = 0xFF0284C7L, // Cyan Wave
            description = "Contemporary rebuilt F40PH-3 spec featuring a sleek dark charcoal roof, silver bodyside, and sweeping modern blue gradient wave."
        )
    )

    val METRA_MP36PH_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_MP36_METRA_WAVE",
            trainId = "metra_mp36ph_express",
            variantCode = "V1",
            displayName = "( V1 ) Metra Streamlined Blue Wave",
            operatorName = "Metra MPI MP36PH-3S",
            subtitle = "Aerodynamic Sloped Nose",
            priceDiamonds = 0,
            primaryColor = 0xFFCBD5E1L,
            secondaryColor = 0xFF1E3A8AL,
            accentColor = 0xFFF97316L,
            description = "Modern MPI MP36PH commuter diesel with streamlined aerodynamic nose, bold Metra navy blue wrap, and high-visibility front safety chevron."
        ),
        LiveryVariant(
            id = "V2_MP36_ROCK_ISLAND",
            trainId = "metra_mp36ph_express",
            variantCode = "V2",
            displayName = "( V2 ) Rock Island Heritage Tribute",
            operatorName = "Chicago, Rock Island & Pacific",
            subtitle = "Bold Red & Jet Black Scheme",
            priceDiamonds = 10,
            primaryColor = 0xFFDC2626L,
            secondaryColor = 0xFF18181BL,
            accentColor = 0xFFFFFFFFL,
            description = "Historic tribute to Chicago's legendary Rock Island Line with striking red and black color blocking and white herald lettering."
        ),
        LiveryVariant(
            id = "V3_MP36_RTA_RETRO",
            trainId = "metra_mp36ph_express",
            variantCode = "V3",
            displayName = "( V3 ) RTA 1970s Retro Tri-Color",
            operatorName = "Regional Transportation Authority",
            subtitle = "Earth Brown & Sunburst Orange",
            priceDiamonds = 15,
            primaryColor = 0xFF5D4037L,
            secondaryColor = 0xFFF97316L,
            accentColor = 0xFFFACC15L,
            description = "Vintage 1970s RTA commuter scheme featuring earth brown body, sunburst orange stripes, and golden yellow accents."
        )
    )

    val METRA_F59PHI_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_F59_METRA_FLASH",
            trainId = "metra_f59phi_silver",
            variantCode = "V1",
            displayName = "( V1 ) Metra Silver Flash Aero",
            operatorName = "Metra EMD F59PHI",
            subtitle = "High-Speed Bullet Nose",
            priceDiamonds = 0,
            primaryColor = 0xFFE2E8F0L,
            secondaryColor = 0xFF1E3A8AL,
            accentColor = 0xFF0284C7L,
            description = "Streamlined EMD F59PHI aerodynamic cowl with polished silver bullet nose and dynamic Metra electric blue swooshes."
        ),
        LiveryVariant(
            id = "V2_F59_ILLINOIS_CENTRAL",
            trainId = "metra_f59phi_silver",
            variantCode = "V2",
            displayName = "( V2 ) Illinois Central Heritage",
            operatorName = "Illinois Central Railroad",
            subtitle = "Chocolate Brown & Warm Orange",
            priceDiamonds = 10,
            primaryColor = 0xFF451A03L,
            secondaryColor = 0xFFEA580CL,
            accentColor = 0xFFFACC15L,
            description = "Classic Illinois Central City of New Orleans tribute livery with rich chocolate brown body and bright orange lightning stripes."
        ),
        LiveryVariant(
            id = "V3_F59_MILWAUKEE_ROAD",
            trainId = "metra_f59phi_silver",
            variantCode = "V3",
            displayName = "( V3 ) Milwaukee Road Hiawatha",
            operatorName = "The Milwaukee Road",
            subtitle = "Hiawatha Orange & Maroon Grey",
            priceDiamonds = 15,
            primaryColor = 0xFFF97316L,
            secondaryColor = 0xFF475569L,
            accentColor = 0xFF881337L,
            description = "Legendary Milwaukee Road high-speed express scheme featuring brilliant Hiawatha orange, charcoal grey roof, and maroon pinstripes."
        )
    )

    val METRA_HIGHLINER_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_HIGHLINER_STAINLESS",
            trainId = "metra_highliner_emu",
            variantCode = "V1",
            displayName = "( V1 ) Metra Electric Stainless EMU",
            operatorName = "Metra Electric District",
            subtitle = "Double-Decker Gallery Electric",
            priceDiamonds = 0,
            primaryColor = 0xFFE2E8F0L,
            secondaryColor = 0xFF1E3A8AL,
            accentColor = 0xFFF97316L,
            description = "Bi-level electric multiple-unit gallery car with roof pantograph, stainless fluted sides, and navy blue window band serving Millennium Station."
        ),
        LiveryVariant(
            id = "V2_HIGHLINER_MODERN",
            trainId = "metra_highliner_emu",
            variantCode = "V2",
            displayName = "( V2 ) Modern Metra Electric Wave",
            operatorName = "Metra Electric Modern",
            subtitle = "Dynamic Cyan & Orange Chevron",
            priceDiamonds = 10,
            primaryColor = 0xFFF8FAFCL,
            secondaryColor = 0xFF0284C7L,
            accentColor = 0xFFF97316L,
            description = "Modernized Highliner EMU with high-contrast pearl white livery, electric cyan speedlines, and orange safety warning door markings."
        ),
        LiveryVariant(
            id = "V3_HIGHLINER_IC_ORANGE",
            trainId = "metra_highliner_emu",
            variantCode = "V3",
            displayName = "( V3 ) Heritage IC Electric Orange",
            operatorName = "IC Suburban Electric",
            subtitle = "Heritage Highliner Orange Nose",
            priceDiamonds = 15,
            primaryColor = 0xFF94A3B8L,
            secondaryColor = 0xFFEA580CL,
            accentColor = 0xFFFFFFFFL,
            description = "Original 1970s Highliner heritage paint with unpainted stainless body and full-coverage warning orange end panels."
        )
    )

    val METRA_CHARGER_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_CHARGER_METRA_RIBBON",
            trainId = "metra_charger_sc44",
            variantCode = "V1",
            displayName = "( V1 ) Metra Blue Ribbon Charger",
            operatorName = "Metra Siemens SC-44",
            subtitle = "Tier 4 Eco-Electric Streamliner",
            priceDiamonds = 0,
            primaryColor = 0xFFE2E8F0L,
            secondaryColor = 0xFF1E3A8AL,
            accentColor = 0xFF06B6D4L,
            description = "Next-generation Siemens Charger with Cummins QSK95 diesel-electric power, aerodynamic European-American hybrid nose, and Metra blue ribbon wrap."
        ),
        LiveryVariant(
            id = "V2_CHARGER_LINCOLN",
            trainId = "metra_charger_sc44",
            variantCode = "V2",
            displayName = "( V2 ) State of Illinois Pride",
            operatorName = "IDOT Lincoln Service",
            subtitle = "Navy, White & Scarlet Flag",
            priceDiamonds = 10,
            primaryColor = 0xFF0F172AL,
            secondaryColor = 0xFFDC2626L,
            accentColor = 0xFFFFFFFFL,
            description = "Illinois Department of Transportation high-speed corridor livery with navy body, scarlet red speedbands, and pearl white roof."
        ),
        LiveryVariant(
            id = "V3_CHARGER_CARBON",
            trainId = "metra_charger_sc44",
            variantCode = "V3",
            displayName = "( V3 ) Stealth Carbon Commuter",
            operatorName = "Metra Nightliner Ops",
            subtitle = "Matte Carbon & Electric Cyan",
            priceDiamonds = 15,
            primaryColor = 0xFF18181BL,
            secondaryColor = 0xFF27272AL,
            accentColor = 0xFF22D3EEL,
            description = "Tactical stealth commuter livery with dark carbon matte finish and glowing neon electric cyan pinstriping."
        )
    )

    val METRA_E8_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_E8_METRA_TRANSITION",
            trainId = "metra_e8_streamliner",
            variantCode = "V1",
            displayName = "( V1 ) Metra Heritage Transition Blue",
            operatorName = "Metra Heritage Division",
            subtitle = "Classic Twin-Engine Bulldog Nose",
            priceDiamonds = 0,
            primaryColor = 0xFFCBD5E1L,
            secondaryColor = 0xFF1E3A8AL,
            accentColor = 0xFFF97316L,
            description = "Vintage 1950s EMD E8 dual-engine streamliner in Chicago commuter transition blue with cast stainless steel side intake grilles."
        ),
        LiveryVariant(
            id = "V2_E8_BURLINGTON",
            trainId = "metra_e8_streamliner",
            variantCode = "V2",
            displayName = "( V2 ) CB&Q Burlington Silver Streak",
            operatorName = "Chicago, Burlington & Quincy",
            subtitle = "Zephyr Stainless Steel & Crimson",
            priceDiamonds = 10,
            primaryColor = 0xFFE2E8F0L,
            secondaryColor = 0xFFDC2626L,
            accentColor = 0xFFFFFFFFL,
            description = "Legendary Burlington Route streamliner livery featuring brilliant stainless steel corrugated fluting and crimson red nose emblem."
        ),
        LiveryVariant(
            id = "V3_E8_CNW400",
            trainId = "metra_e8_streamliner",
            variantCode = "V3",
            displayName = "( V3 ) Chicago & North Western 400",
            operatorName = "Chicago & North Western",
            subtitle = "Famous Stagecoach Yellow & Green",
            priceDiamonds = 15,
            primaryColor = 0xFF14532DL,
            secondaryColor = 0xFFFACC15L,
            accentColor = 0xFFDC2626L,
            description = "Renowned C&NW 'Twin Cities 400' express scheme featuring dark Pullman green, stagecoach yellow nose band, and red ball herald."
        )
    )

    val METRA_CORADIA_LIVERY_VARIANTS = listOf(
        LiveryVariant(
            id = "V1_CORADIA_METRA_FAST",
            trainId = "metra_alstom_coradia_dual",
            variantCode = "V1",
            displayName = "( V1 ) Metra Electric Fastliner",
            operatorName = "Metra Express Interurban",
            subtitle = "High-Speed Dual Electric / Pantograph",
            priceDiamonds = 0,
            primaryColor = 0xFFE2E8F0L,
            secondaryColor = 0xFF1E3A8AL,
            accentColor = 0xFF38BDF8L,
            description = "High-speed dual-power commuter train capable of overhead 25kV electric and diesel operation with roof pantographs and aerodynamic nose."
        ),
        LiveryVariant(
            id = "V2_CORADIA_SKYLINE",
            trainId = "metra_alstom_coradia_dual",
            variantCode = "V2",
            displayName = "( V2 ) Chicago Skyline Edition",
            operatorName = "City of Chicago Express",
            subtitle = "Navy Blue & 6-Pointed Red Stars",
            priceDiamonds = 10,
            primaryColor = 0xFF0F172AL,
            secondaryColor = 0xFF0284C7L,
            accentColor = 0xFFEF4444L,
            description = "Dedicated Chicago municipal edition featuring navy blue body, sky blue stripes, and the iconic four 6-pointed Chicago stars."
        ),
        LiveryVariant(
            id = "V3_CORADIA_MIDNIGHT",
            trainId = "metra_alstom_coradia_dual",
            variantCode = "V3",
            displayName = "( V3 ) Polar Midnight Express",
            operatorName = "Metra Winter Nightliner",
            subtitle = "Midnight Black & Aurora Teal",
            priceDiamonds = 15,
            primaryColor = 0xFF030712L,
            secondaryColor = 0xFF0D9488L,
            accentColor = 0xFF2DD4BFL,
            description = "Sleek nighttime aero livery with metallic obsidian midnight body, aurora teal lighting bands, and illuminated high-speed nose."
        )
    )

    fun getLiveryVariantsForTrain(trainId: String): List<LiveryVariant> {
        return when (trainId) {
            "secret_virgin_pendolino_390" -> PENDOLINO_LIVERY_VARIANTS
            "metra_f40ph_screamer" -> METRA_F40PH_LIVERY_VARIANTS
            "metra_mp36ph_express" -> METRA_MP36PH_LIVERY_VARIANTS
            "metra_f59phi_silver" -> METRA_F59PHI_LIVERY_VARIANTS
            "metra_highliner_emu" -> METRA_HIGHLINER_LIVERY_VARIANTS
            "metra_charger_sc44" -> METRA_CHARGER_LIVERY_VARIANTS
            "metra_e8_streamliner" -> METRA_E8_LIVERY_VARIANTS
            "metra_alstom_coradia_dual" -> METRA_CORADIA_LIVERY_VARIANTS

            "emd_f40ph_via" -> listOf(
                LiveryVariant("V1_VIA_CLASSIC", trainId, "V1", "( V1 ) VIA Rail Canada Classic", "VIA Rail Canada", "Silver Stainless & Yellow Chevron", 0, 0xFFD4D8DDL, 0xFF334155L, 0xFFF59E0BL, "Iconic Canadian passenger diesel with bright yellow nose chevron, stainless steel sides and 'love the way' motif."),
                LiveryVariant("V2_VIA_RENAISSANCE", trainId, "V2", "( V2 ) VIA Renaissance Teal", "VIA Rail Corridor", "Corridor Teal & Dark Slate", 10, 0xFF0D9488L, 0xFF1E293BL, 0xFFFACC15L, "Modern European-inspired corridor scheme featuring sleek teal body, dark slate cab roof, and bright gold pinstripes."),
                LiveryVariant("V3_VIA_GOLD", trainId, "V3", "( V3 ) VIA 40th Anniversary Gold", "VIA Special Fleet", "Historic Gold & Obsidian", 15, 0xFFEAB308L, 0xFF0F172AL, 0xFFFFFFFFL, "Commemorative 40th Anniversary celebration livery in metallic gold and piano obsidian with maple leaf roundels.")
            )

            "emd_f40ph_metra" -> listOf(
                LiveryVariant("V1_METRA_PATRIOT_120", trainId, "V1", "( V1 ) Metra #120 Patriot Veteran", "Metra Commuter Rail", "Veteran Tribute Digital Camo", 0, 0xFF2F4F4FL, 0xFF1E3A8AL, 0xFFEF4444L, "Metra 'Honoring All Who Served' custom veteran tribute livery with blue cab, camo body and American flag graphics."),
                LiveryVariant("V2_METRA_CLASSIC_120", trainId, "V2", "( V2 ) Classic Chicago Blue & Orange", "Metra Chicago", "Stainless Fluted & Safety Orange", 10, 0xFFCBD5E1L, 0xFF1E3A8AL, 0xFFF97316L, "Traditional Metra commuter service livery featuring fluted stainless steel side panels, royal blue cowl, and warning orange sill stripe."),
                LiveryVariant("V3_METRA_RAVEN_120", trainId, "V3", "( V3 ) Modernized Raven Slate", "Metra City Express", "Matte Charcoal & Cyan Speedline", 15, 0xFF1E293BL, 0xFF0284C7L, 0xFF38BDF8L, "Modernized high-visibility dark scheme featuring stealth charcoal body with vivid cyan aero gradient.")
            )

            "emd_gp9_highhood" -> listOf(
                LiveryVariant("V1_NS_THOROUGHBRED", trainId, "V1", "( V1 ) NS Thoroughbred Horsehead", "Norfolk Southern", "Jet Black & White Stallion", 0, 0xFF0F172AL, 0xFF000000L, 0xFFFFFFFFL, "Norfolk Southern high-hood workhorse with rearing thoroughbred stallion logo and high-visibility frame reflectors."),
                LiveryVariant("V2_SOUTHERN_TUXEDO", trainId, "V2", "( V2 ) Southern Railway Tuxedo", "Southern Railway", "Virginia Green & Gold Lining", 10, 0xFF14532DL, 0xFF000000L, 0xFFFACC15L, "Prestigious Southern Railway 'Tuxedo' scheme featuring deep forest green body, imitation gold striping, and high nose herald."),
                LiveryVariant("V3_NKP_HIGHHOOD", trainId, "V3", "( V3 ) Nickel Plate Road Blue", "Nickel Plate Road", "High-Hood Blue & White Stripe", 15, 0xFF1E3A8AL, 0xFF0F172AL, 0xFFFFFFFFL, "Classic Nickel Plate Road freight scheme with deep corporate blue and lightning white nose bands.")
            )

            "emd_gp9_normal" -> listOf(
                LiveryVariant("V1_CN_ZEBRA", trainId, "V1", "( V1 ) CN Forest Green & Chevrons", "Canadian National", "Taiga Green & White Stripes", 0, 0xFF166534L, 0xFF0F172AL, 0xFFFFFFFFL, "Classic Canadian National low-nose GP9 in taiga green with diagonal white safety hazard zebra chevrons."),
                LiveryVariant("V2_CP_PACMAN", trainId, "V2", "( V2 ) CP Rail Action Red Multimark", "CP Rail", "Action Red & White Pac-Man", 10, 0xFFDC2626L, 0xFFFFFFFFL, 0xFF000000L, "Famous Canadian Pacific Action Red scheme featuring the legendary 1968 Multimark herald and white cab letters."),
                LiveryVariant("V3_BC_RAIL", trainId, "V3", "( V3 ) BC Rail Mountain Dash", "BC Rail", "Two-Tone Blue, Red & White", 15, 0xFF0284C7L, 0xFFDC2626L, 0xFFFFFFFFL, "British Columbia Railway iconic Pacific Northwest scheme with bright red cab face, sky blue body and white separation stripe.")
            )

            "siemens_vectron_red" -> listOf(
                LiveryVariant("V1_DB_TRAFFIC_RED", trainId, "V1", "( V1 ) DB Traffic Red", "Deutsche Bahn Cargo", "Verkehrsrot Red & White DB Logo", 0, 0xFFDC2626L, 0xFF475569L, 0xFFFFFFFFL, "Standard Deutsche Bahn European cross-border freight livery in bold Verkehrsrot red with illuminated LED headlights."),
                LiveryVariant("V2_OBB_NIGHTJET", trainId, "V2", "( V2 ) ÖBB Nightjet Midnight", "ÖBB Nightjet", "Midnight Blue & Gold Stars", 10, 0xFF1E3A8AL, 0xFF0F172AL, 0xFFDC2626L, "Austria Federal Railways trans-European overnight express scheme in deep midnight navy with crimson ribbon and starry night constellation."),
                LiveryVariant("V3_PKP_CARGO", trainId, "V3", "( V3 ) PKP Cargo Oceanic Navy", "PKP Cargo Poland", "Ocean Blue & Lime Green Chevron", 15, 0xFF0369A1L, 0xFF0F172AL, 0xFF84CC16L, "Polish state railway heavy freight scheme featuring dynamic ocean blue body and vivid lime green warning nose graphics.")
            )

            "siemens_vectron_black" -> listOf(
                LiveryVariant("V1_MRCE_BLACK", trainId, "V1", "( V1 ) MRCE Dispolok Shadow", "MRCE Mitsui Rail", "Matte Obsidian & Warning Yellow", 0, 0xFF1E293BL, 0xFF0F172AL, 0xFFFACC15L, "Prestigious pan-European leasing livery in matte stealth obsidian black with warning yellow cab nose accents."),
                LiveryVariant("V2_TX_FLAME", trainId, "V2", "( V2 ) TX Logistik Flame Rider", "TX Logistik", "Volcanic Flame Red & Charcoal", 10, 0xFF991B1BL, 0xFF18181BL, 0xFFF97316L, "Dynamic artistic scheme featuring racing orange-red flames flowing across the aerodynamic sidewalls."),
                LiveryVariant("V3_BOXXPRESS", trainId, "V3", "( V3 ) BoxXpress Eco Green", "BoxXpress Germany", "Anthracite & Neon Green", 15, 0xFF27272AL, 0xFF15803DL, 0xFF22C55EL, "Container express shuttle scheme in dark anthracite with high-contrast electric lime graphics.")
            )

            "siemens_vectron_demo" -> listOf(
                LiveryVariant("V1_SIEMENS_DEMO", trainId, "V1", "( V1 ) Siemens Factory Demo", "Siemens Mobility", "Pure White & Electric Cyan Wave", 0, 0xFFF8FAFCL, 0xFF475569L, 0xFF06B6D4L, "Siemens Mobility international test locomotive in clean alpine white with electric cyan aerodynamics."),
                LiveryVariant("V2_GREEN_DEAL", trainId, "V2", "( V2 ) European Green Deal", "European Rail Union", "Emerald Green & Platinum", 10, 0xFF047857L, 0xFFE2E8F0L, 0xFFFACC15L, "Zero-emission high-efficiency corridor scheme celebrating green rail freight with emerald highlights."),
                LiveryVariant("V3_ELP_PLATINUM", trainId, "V3", "( V3 ) European Locomotive Pool", "ELP Switzerland", "Platinum Silver & Golden Star", 15, 0xFFCBD5E1L, 0xFF1E293BL, 0xFFD97706L, "Hybrid dual-mode high-power leasing edition with brushed titanium finish and gold trim.")
            )

            "amtrak_acela_bullet" -> listOf(
                LiveryVariant("V1_ACELA_ORIGINAL", trainId, "V1", "( V1 ) Acela High-Speed Original", "Amtrak Northeast Corridor", "Turquoise Blue & Navy Wave", 0, 0xFFCBD5E1L, 0xFF1E3A8AL, 0xFF0D9488L, "Original 2000 Northeast Direct livery with signature turquoise swoosh, navy window band, and aerodynamic nose cone."),
                LiveryVariant("V2_ACELA_PHASE6", trainId, "V2", "( V2 ) Acela Phase VI Modern", "Amtrak Flagship", "Midnight Blue, Platinum & Red Sill", 10, 0xFFE2E8F0L, 0xFF1E3A8AL, 0xFFDC2626L, "Modernized Amtrak Phase VI high-speed livery with crisp blue window line, red sill stripe, and silver metallic body."),
                LiveryVariant("V3_ACELA_PROTOTYPE", trainId, "V3", "( V3 ) Acela Super Prototype", "High-Speed R&D", "Titanium Carbon & Gold Speedline", 15, 0xFF334155L, 0xFF0F172AL, 0xFFFACC15L, "Advanced speed trial prototype scheme in brushed titanium carbon with golden high-speed telemetry ribbons.")
            )

            "siemens_american_flyer" -> listOf(
                LiveryVariant("V1_AMERICAN_FLYER", trainId, "V1", "( V1 ) American Flyer Charger", "Amtrak Long Distance", "Patriotic Blue, White & Red", 0, 0xFFF8FAFCL, 0xFF1E3A8AL, 0xFFDC2626L, "State-of-the-art Siemens Charger diesel in patriotic navy blue, bright white, and crimson red wave."),
                LiveryVariant("V2_AMTRAK_MIDWEST", trainId, "V2", "( V2 ) Amtrak Midwest Regional", "Midwest DOT Rail", "Horizon Blue & Yellow Chevron", 10, 0xFF0284C7L, 0xFF1E3A8AL, 0xFFFACC15L, "Multi-state Midwest commuter and intercity livery with horizon blue cab and safety yellow warning chevron."),
                LiveryVariant("V3_BRIGHTLINE_YELLOW", trainId, "V3", "( V3 ) Brightline Sunshine Neon", "Brightline Florida", "Neon Sunshine Yellow & Graphite", 15, 0xFFFACC15L, 0xFF1E293BL, 0xFF06B6D4L, "Ultra-modern Florida higher-speed rail scheme featuring vibrant neon sunshine yellow and metallic graphite.")
            )

            "cd_effishunter_742" -> listOf(
                LiveryVariant("V1_CD_NAVY", trainId, "V1", "( V1 ) CD Czech Railway Classic", "České Dráhy", "Najbrt Navy & Sky Blue", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Official Najbrt corporate design of Czech Railways in two-tone blue and light grey accents."),
                LiveryVariant("V2_CD_CARGO", trainId, "V2", "( V2 ) CD Cargo Heavy Freight", "ČD Cargo", "Crimson Red & Granite Grey", 10, 0xFF991B1BL, 0xFF475569L, 0xFFFACC15L, "Czech heavy industrial rail freight scheme in rugged crimson red and industrial granite grey."),
                LiveryVariant("V3_CSD_RETRO", trainId, "V3", "( V3 ) CSD 1970s Retro Industrial", "Československé Dráhy", "Mustard Yellow & Moss Green", 15, 0xFFCA8A04L, 0xFF14532DL, 0xFFDC2626L, "Historical Czechoslovak State Railways industrial shunting scheme with mustard yellow nose and moss green body.")
            )

            "up_big_boy_4014" -> listOf(
                LiveryVariant("V1_BIG_BOY_1941", trainId, "V1", "( V1 ) 1941 Historic Delivery", "Union Pacific Railroad", "Coal Black & Graphite Smokebox", 0, 0xFF111827L, 0xFF374151L, 0xFFD4D8DDL, "Original 1941 ALCO delivery appearance with deep satin coal black boiler, graphite smokebox, and polished brass bell."),
                LiveryVariant("V2_BIG_BOY_GOLD", trainId, "V2", "( V2 ) UP Heritage Gold Trim", "Union Pacific Heritage", "Sovereign Black & Gold Leaf Lettering", 10, 0xFF0B0F19L, 0xFF1F2937L, 0xFFEAB308L, "Exquisite VIP exhibition livery featuring 24K gold leaf Union Pacific lettering and polished steel side rods."),
                LiveryVariant("V3_BIG_BOY_2019", trainId, "V3", "( V3 ) 2019 Great Race to Ogden", "UP Steam Excursion", "Polished Steel & Red Number Plate", 15, 0xFF18181BL, 0xFF4B5563L, 0xFFDC2626L, "Modern fully-restored excursion appearance celebrating the Transcontinental Railroad 150th Anniversary.")
            )

            "pacific_462" -> listOf(
                LiveryVariant("V1_PACIFIC_BRUNSWICK", trainId, "V1", "( V1 ) British Brunswick Green", "Express Passenger Line", "Brunswick Green & Gold Lining", 0, 0xFF14532DL, 0xFF0F172AL, 0xFFFACC15L, "Classic British express steam livery in hand-lined Brunswick racing green with gold boiler bands."),
                LiveryVariant("V2_PACIFIC_LMS_CRIMSON", trainId, "V2", "( V2 ) LMS Coronation Crimson Lake", "London Midland & Scottish", "Crimson Lake & Vermilion", 10, 0xFF7F1D1DL, 0xFF000000L, 0xFFFACC15L, "Prestigious Coronation Scot express scheme in rich deep crimson lake with gold and vermilion pinstriping."),
                LiveryVariant("V3_PACIFIC_JET_BLACK", trainId, "V3", "( V3 ) Midnight Jet Black Express", "Heavy Haul Steam", "Piano Jet Black & Polished Steel", 15, 0xFF000000L, 0xFF1E293BL, 0xFFE2E8F0L, "Powerful midnight express steam look with lustrous piano black finish and polished stainless steel motion gear.")
            )

            "nyc_dreyfuss_hudson" -> listOf(
                LiveryVariant("V1_DREYFUSS_GREY", trainId, "V1", "( V1 ) 20th Century Limited Two-Tone", "New York Central", "Two-Tone Grey & Aluminum Ribs", 0, 0xFF64748BL, 0xFF334155L, 0xFFE2E8F0L, "Henry Dreyfuss iconic Art Deco streamline casing with cascading bullet nose and finned radiator grilles."),
                LiveryVariant("V2_DREYFUSS_SILVER", trainId, "V2", "( V2 ) Platinum Silver Streamliner", "NYC Empire Express", "Mirror Chrome & Scarlet Disc", 10, 0xFFE2E8F0L, 0xFF94A3B8L, 0xFFDC2626L, "Polished aluminum high-speed bullet train appearance with glowing red central pilot fin."),
                LiveryVariant("V3_DREYFUSS_GOLD", trainId, "V3", "( V3 ) Empire State Gold Edition", "NYC Luxury Pullman", "Champagne Gold & Charcoal", 15, 0xFFCA8A04L, 0xFF1E293BL, 0xFFFACC15L, "VIP Luxury presentation edition featuring radiant champagne gold nose bullet and charcoal flanking panels.")
            )

            "cp_royal_hudson" -> listOf(
                LiveryVariant("V1_ROYAL_BLUE", trainId, "V1", "( V1 ) Royal Tour Sapphire Blue", "Canadian Pacific 2850", "Sapphire Blue & Stainless Sheath", 0, 0xFF1E3A8AL, 0xFF0F172AL, 0xFFCBD5E1L, "Specially painted in sapphire blue and stainless steel for King George VI and Queen Elizabeth 1939 Royal Train."),
                LiveryVariant("V2_CP_MAROON", trainId, "V2", "( V2 ) CP Tuscan Maroon & Gold", "Canadian Pacific", "Tuscan Maroon & Gold Pinstripes", 10, 0xFF581C87L, 0xFF1E293BL, 0xFFFACC15L, "Standard Canadian Pacific passenger steam livery in rich Tuscan maroon with Canadian beaver crests."),
                LiveryVariant("V3_EMPRESS_BLACK", trainId, "V3", "( V3 ) Empress Piano Black", "CPKC Tour Flagship", "Piano Gloss Black & Steel", 15, 0xFF09090BL, 0xFF27272AL, 0xFFE2E8F0L, "Modern CPKC continental steam tour livery in flawless mirror-finish black with bright chrome motion gear.")
            )

            "burlington_f7" -> listOf(
                LiveryVariant("V1_CBQ_ZEPHYR", trainId, "V1", "( V1 ) CB&Q Stainless Steel Zephyr", "Burlington Route", "Fluted Stainless & Crimson Red", 0, 0xFFCBD5E1L, 0xFFDC2626L, 0xFFFFFFFFL, "Classic Burlington Route streamlined cab diesel in corrugated stainless steel with crimson red nose and wings."),
                LiveryVariant("V2_SANTA_FE_WARBONNET", trainId, "V2", "( V2 ) Santa Fe Famous Warbonnet", "Atchison, Topeka & Santa Fe", "Warbonnet Red & Stainless Silver", 10, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFFACC15L, "The most famous locomotive livery in rail history: bright red bonnet with yellow speed stripes on stainless steel."),
                LiveryVariant("V3_GN_BIG_SKY", trainId, "V3", "( V3 ) Great Northern Big Sky Blue", "Great Northern Railway", "Big Sky Blue & White", 15, 0xFF0284C7L, 0xFFFFFFFFL, 0xFF0F172AL, "Renowned Great Northern passenger scheme with vivid mountain sky blue, bright white stripe, and Rocky the mountain goat.")
            )

            "br_class_55_deltic" -> listOf(
                LiveryVariant("V1_BR_GREEN", trainId, "V1", "( V1 ) BR Two-Tone Forest Green", "British Railways", "Two-Tone Green & Yellow Warning Ends", 0, 0xFF14532DL, 0xFF166534L, 0xFFFACC15L, "Classic 1960s British Railways East Coast Main Line two-tone forest green with safety warning yellow nose panel."),
                LiveryVariant("V2_BR_BLUE", trainId, "V2", "( V2 ) BR Corporate Blue & Yellow", "BR InterCity", "Corporate Rail Blue & Full Yellow Nose", 10, 0xFF1E3A8AL, 0xFFFACC15L, 0xFFFFFFFFL, "1970s British Rail corporate blue era featuring full bright yellow nose cone and white arrows of direction."),
                LiveryVariant("V3_ROYAL_SCOTSMAN", trainId, "V3", "( V3 ) Royal Scotsman Maroon", "Royal Scotsman Luxury", "Royal Maroon & Gold Coat of Arms", 15, 0xFF5B1327L, 0xFF1E293BL, 0xFFFACC15L, "Luxury Scottish touring train livery in lustrous deep maroon with gold lettering and cast nameplates.")
            )

            "ge_ac4400cw" -> listOf(
                LiveryVariant("V1_CSX_YN3", trainId, "V1", "( V1 ) CSX Dark Future YN3", "CSX Transportation", "Dark Future Blue & Boxcar Yellow", 0, 0xFF002B66L, 0xFF0F172AL, 0xFFFACC15L, "CSX Transportation flagship 'Dark Future' scheme in deep blue body, boxcar yellow nose face, and yellow frame sill."),
                LiveryVariant("V2_BNSF_HERITAGE2", trainId, "V2", "( V2 ) BNSF Heritage II Pumpkin", "BNSF Railway", "Pumpkin Orange, Dark Green & Yellow", 10, 0xFFEA580CL, 0xFF14532DL, 0xFFFACC15L, "Legendary BNSF Heritage II freight scheme featuring vivid pumpkin orange body, dark green cab top, and yellow divider stripes."),
                LiveryVariant("V3_BNSF_WARBONNET_RED", trainId, "V3", "( V3 ) BNSF Warbonnet Red & Silver", "BNSF Super Fleet", "Gleaming Silver & Blazing Red", 15, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFFACC15L, "Iconic BNSF Warbonnet adaptation in brushed stainless steel with vermilion red nose bonnet and yellow speed pinstripes."),
                LiveryVariant("V4_BN_CASCADE_GREEN", trainId, "V4", "( V4 ) Burlington Northern Cascade Green", "Burlington Northern", "Taiga Green & Stark White Cab Face", 15, 0xFF15803DL, 0xFF09090BL, 0xFFFFFFFFL, "Classic Burlington Northern 'Cascade Green' scheme with stark white front nose face for maximum grade-crossing visibility."),
                LiveryVariant("V5_NS_HORSEHEAD", trainId, "V5", "( V5 ) Norfolk Southern Thoroughbred", "Norfolk Southern", "Piano Jet Black & White Stallion", 15, 0xFF09090BL, 0xFF18181BL, 0xFFFFFFFFL, "Norfolk Southern heavy freight scheme in high-gloss jet black with white rearing stallion horsehead herald and reflective sill striping."),
                LiveryVariant("V6_SANTA_FE_SUPERFLEET", trainId, "V6", "( V6 ) Santa Fe Super Fleet Warbonnet", "Santa Fe Railway", "Classic Red & Silver Transcon", 15, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFFACC15L, "Renowned Santa Fe Super Fleet transcontinental scheme with bright red warbonnet and yellow Santa Fe cross herald."),
                LiveryVariant("V7_UP_BUILDING_AMERICA", trainId, "V7", "( V7 ) Union Pacific Armor Yellow", "Union Pacific", "Armor Yellow, Gray Roof & Red Sill", 15, 0xFFFACC15L, 0xFF475569L, 0xFFDC2626L, "Famous Union Pacific 'Building America' scheme in high-visibility Armor Yellow with Harbor Mist Gray roof and red lettering.")
            )

            "emd_sd70ace" -> listOf(
                LiveryVariant("V1_BNSF_SWOOP", trainId, "V1", "( V1 ) BNSF Swoop Heritage III", "BNSF Railway", "Black, Orange & BNSF Swoop Wedge", 0, 0xFF0F172AL, 0xFFEA580CL, 0xFFFACC15L, "Modern BNSF wedge swoop scheme with satin black cab top, pumpkin orange body, and bold white/black wedge lettering."),
                LiveryVariant("V2_NS_HORSEHEAD", trainId, "V2", "( V2 ) Norfolk Southern Thoroughbred", "Norfolk Southern", "Jet Black & Rearing Stallion", 10, 0xFF09090BL, 0xFF18181BL, 0xFFFFFFFFL, "Norfolk Southern mainline heavy freight scheme in jet black with white rearing stallion horsehead herald."),
                LiveryVariant("V3_BN_CASCADE_GREEN", trainId, "V3", "( V3 ) Burlington Northern Green", "Burlington Northern", "Cascade Green & White Herald", 15, 0xFF15803DL, 0xFF000000L, 0xFFFFFFFFL, "Iconic Burlington Northern 'Cascade Green' heritage scheme with white interlocking BN logo and white cab face."),
                LiveryVariant("V4_CSX_DARK_FUTURE", trainId, "V4", "( V4 ) CSX Dark Future YN3", "CSX Transportation", "Corporate Dark Blue & Boxcar Yellow", 15, 0xFF002B66L, 0xFF0F172AL, 0xFFFACC15L, "CSX mainline unit freight scheme with deep blue body, boxcar yellow cab nose, and yellow sill stripe."),
                LiveryVariant("V5_BNSF_WARBONNET_RED", trainId, "V5", "( V5 ) BNSF Red Warbonnet", "BNSF Railway", "Stainless Silver & Warbonnet Red", 15, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFDC2626L, "Modernized BNSF Warbonnet high-priority intermodal paint scheme with blazing red front bonnet and bold road letters."),
                LiveryVariant("V6_SANTA_FE_WARBONNET", trainId, "V6", "( V6 ) Santa Fe Superfleet Warbonnet", "Santa Fe Railway", "Classic Red Bonnet & Silver", 15, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFFACC15L, "Timeless Santa Fe Warbonnet red and silver freight livery with yellow nose whiskers."),
                LiveryVariant("V7_ATSF_BLUEBONNET", trainId, "V7", "( V7 ) Santa Fe Bluebonnet", "Santa Fe Freight", "Pacific Blue & Stainless Silver", 15, 0xFF0284C7L, 0xFFE2E8F0L, 0xFFFACC15L, "Rare Santa Fe freight Bluebonnet scheme with vibrant royal blue bonnet and polished silver sides.")
            )

            "burlington_f7" -> listOf(
                LiveryVariant("V1_CBQ_ZEPHYR", trainId, "V1", "( V1 ) CB&Q Stainless Steel Zephyr", "Burlington Route", "Fluted Stainless & Crimson Red", 0, 0xFFCBD5E1L, 0xFFDC2626L, 0xFFFFFFFFL, "Classic Burlington Route streamlined cab diesel in corrugated stainless steel with crimson red nose and wings."),
                LiveryVariant("V2_SANTA_FE_WARBONNET", trainId, "V2", "( V2 ) Santa Fe Famous Warbonnet", "Atchison, Topeka & Santa Fe", "Warbonnet Red & Stainless Silver", 10, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFFACC15L, "The most famous locomotive livery in rail history: bright red bonnet with yellow speed stripes on stainless steel."),
                LiveryVariant("V3_GN_BIG_SKY", trainId, "V3", "( V3 ) Great Northern Big Sky Blue", "Great Northern Railway", "Big Sky Blue & White", 15, 0xFF0284C7L, 0xFFFFFFFFL, 0xFF0F172AL, "Renowned Great Northern passenger scheme with vivid mountain sky blue, bright white stripe, and Rocky the mountain goat."),
                LiveryVariant("V4_BN_CASCADE_GREEN", trainId, "V4", "( V4 ) Burlington Northern Cascade Green", "Burlington Northern", "Cascade Green & White Nose", 15, 0xFF15803DL, 0xFF09090BL, 0xFFFFFFFFL, "Post-merger Burlington Northern green and black scheme with white BN nose logo.")
            )

            "alco_pa1" -> listOf(
                LiveryVariant("V1_SANTA_FE_SUPERCHIEF", trainId, "V1", "( V1 ) Santa Fe Super Chief Warbonnet", "Santa Fe Railway", "Warbonnet Red & Silver Streak", 0, 0xFFE2E8F0L, 0xFFDC2626L, 0xFFFACC15L, "The undisputed king of American diesels: ALCO PA-1 in gleaming silver with iconic Santa Fe red warbonnet."),
                LiveryVariant("V2_ATSF_BLUEBONNET", trainId, "V2", "( V2 ) ATSF Bluebonnet Freight", "Santa Fe Freight", "Freight Blue & Yellow Warbonnet", 10, 0xFF0284C7L, 0xFFE2E8F0L, 0xFFFACC15L, "Striking freight adaptation of the warbonnet featuring rich royal blue body and warm silver accents."),
                LiveryVariant("V3_SP_DAYLIGHT", trainId, "V3", "( V3 ) Southern Pacific Daylight", "Southern Pacific", "Daylight Scarlet & Sunset Orange", 15, 0xFFEA580CL, 0xFFDC2626L, 0xFF000000L, "World's most colorful train livery: Southern Pacific scarlet red, sunset orange, and black roofline.")
            )

            "db_class_103" -> listOf(
                LiveryVariant("V1_TEE_CRIMSON", trainId, "V1", "( V1 ) TEE Crimson & Cream", "Deutsche Bundesbahn", "Crimson Lake & Light Ivory Cream", 0, 0xFFFEF3C7L, 0xFF991B1BL, 0xFF1E293BL, "Legendary Trans-Europ-Express prestige scheme in rich crimson lake with ivory cream upper body and silver roof."),
                LiveryVariant("V2_TOURISTIKZUG", trainId, "V2", "( V2 ) Touristikzug Alpine Rainbow", "DB Touristik", "Alpine Sky Blue, Yellow & Green", 10, 0xFF0284C7L, 0xFF22C55EL, 0xFFFACC15L, "Celebrated 1990s holiday express scheme featuring cheerful sunshine yellow, leaf green, and sky blue rainbow waves."),
                LiveryVariant("V3_ORIENT_RED", trainId, "V3", "( V3 ) DB Orient Red & White Bib", "DB Fernverkehr", "Orientrot Red & Pastel White", 15, 0xFFB91C1CL, 0xFFFFFFFFL, 0xFF334155L, "1980s intercity scheme in distinctive Orient Red with a prominent pastel white contrast bib on the aerodynamic nose.")
            )

            "tgv_sud_est" -> listOf(
                LiveryVariant("V1_TGV_ORANGE", trainId, "V1", "( V1 ) Historic 1981 Tangerine Orange", "SNCF TGV", "Tangerine Orange, White & Grey", 0, 0xFFEA580CL, 0xFF475569L, 0xFFFFFFFFL, "Original 1981 world-speed-record TGV Sud-Est livery in vibrant tangerine orange with white window bands."),
                LiveryVariant("V2_TGV_ATLANTIQUE", trainId, "V2", "( V2 ) TGV Atlantique Silver & Blue", "SNCF Atlantique", "Metallic Silver & Cobalt Blue", 10, 0xFFCBD5E1L, 0xFF1E3A8AL, 0xFF0284C7L, "Second-generation high-speed livery in shimmering metallic silver with deep cobalt blue sweeping window ribbon."),
                LiveryVariant("V3_TGV_INOUI", trainId, "V3", "( V3 ) TGV inOui Carmillon Modern", "SNCF inOui", "Carmillon Maroon, Anthracite & White", 15, 0xFFF8FAFCL, 0xFF831843L, 0xFF334155L, "Current flagship high-speed rail scheme featuring clean white aerodynamic nose, Carmillon pink-red doors, and anthracite roof.")
            )

            "shinkansen_0_series" -> listOf(
                LiveryVariant("V1_0_CLASSIC_BLUE", trainId, "V1", "( V1 ) Classic Tokaido Blue & White", "JR Central / JNR", "Snow White & Royal Blue Ribbon", 0, 0xFFF8FAFCL, 0xFF1E3A8AL, 0xFF38BDF8L, "Original 1964 Tokyo Olympic bullet train livery in pristine snow white with signature royal blue waist ribbon."),
                LiveryVariant("V2_0_HIKARI_GOLD", trainId, "V2", "( V2 ) Hikari 50th Anniversary Gold", "JR West Heritage", "Pearl White & Sovereign Gold", 10, 0xFFF8FAFCL, 0xFFEAB308L, 0xFF1E293BL, "50th Anniversary celebration livery featuring pure pearl white body and radiant golden bullet nose ribbon."),
                LiveryVariant("V3_0_WEST_HIKARI", trainId, "V3", "( V3 ) Sanyo West Hikari Green", "JR West Sanyo", "Platinum Grey & Bright Green Stripe", 15, 0xFFE2E8F0L, 0xFF16A34AL, 0xFF1E293BL, "1990s Sanyo Shinkansen scheme in platinum light grey with distinctive bright emerald green waist stripe.")
            )

            "prr_gg1" -> listOf(
                LiveryVariant("V1_GG1_BRUNSWICK", trainId, "V1", "( V1 ) Raymond Loewy Brunswick Green", "Pennsylvania Railroad", "Brunswick Green & 5-Stripe Gold", 0, 0xFF14532DL, 0xFF0B0F19L, 0xFFFACC15L, "Masterpiece of industrial design: Raymond Loewy welded steel electric locomotive in dark green with 5 continuous gold whiskers."),
                LiveryVariant("V2_GG1_TUSCAN_RED", trainId, "V2", "( V2 ) Congressional Tuscan Red", "Pennsylvania Railroad", "Tuscan Red & 5-Stripe Gold", 10, 0xFF7F1D1DL, 0xFF0B0F19L, 0xFFFACC15L, "Prestigious Congressional passenger scheme in deep Tuscan red with 5 gold stripes and Keystone heralds."),
                LiveryVariant("V3_GG1_BICENTENNIAL", trainId, "V3", "( V3 ) American Bicentennial 1976", "Conrail / PRR", "Red, White & Blue Stars", 15, 0xFF1E3A8AL, 0xFFDC2626L, 0xFFFFFFFFL, "Historic 1976 American Bicentennial patriotic paint scheme with red, white, and blue stripes and stars.")
            )

            "soviet_vl85" -> listOf(
                LiveryVariant("V1_VL85_TAIGA", trainId, "V1", "( V1 ) Soviet Taiga Green", "Soviet Railways (SZD)", "Taiga Green & Soviet Red Star", 0, 0xFF166534L, 0xFF1F2937L, 0xFFDC2626L, "Original 12-axle Siberian heavy freight electric in deep taiga green with yellow warning face and cast red star."),
                LiveryVariant("V2_VL85_RZD_MODERN", trainId, "V2", "( V2 ) Russian Railways RZD Red & Grey", "RZD Freight", "Traffic Red & Granite Platinum", 10, 0xFFDC2626L, 0xFF475569L, 0xFFE2E8F0L, "Modern Russian Railways corporate livery with bold red cab front and two-tone platinum side chevron."),
                LiveryVariant("V3_VL85_BAIKAL", trainId, "V3", "( V3 ) Siberian Arctic Subzero Blue", "Baikal-Amur Line", "Arctic Glacier Blue & Ice White", 15, 0xFF0284C7L, 0xFF0F172AL, 0xFFE0F2FEL, "Special extreme-cold weather scheme in glacier cyan blue with white snow-deflector stripes.")
            )

            "secret_ghost_train" -> listOf(
                LiveryVariant("V1_GHOST_ECTO", trainId, "V1", "( V1 ) Spectral Ecto-Green", "Phantom Express", "Ectoplasmic Green & Mist", 0, 0xFF064E3BL, 0xFF052E16L, 0xFF22C55EL, "Eerie glowing ectoplasmic green apparition with floating spirit smoke and translucent phantom aura."),
                LiveryVariant("V2_GHOST_BLOODMOON", trainId, "V2", "( V2 ) Blood Moon Crimson Ghost", "Spectral Haunt", "Blood Moon Crimson & Nether Ash", 10, 0xFF450A0AL, 0xFF18181BL, 0xFFEF4444L, "Terrifying blood moon crimson wraith train surrounded by pulsating scarlet flames."),
                LiveryVariant("V3_GHOST_VOID", trainId, "V3", "( V3 ) Nether Void Violet Spectre", "Abyssal Railway", "Obsidian Void & Phantom Violet", 15, 0xFF2E1065L, 0xFF09090BL, 0xFFA855F7L, "Cosmic horror phantom train glowing with abyssal violet plasma and eerie glowing headlights.")
            )

            "secret_hyper_steam" -> listOf(
                LiveryVariant("V1_HYPER_GOLD", trainId, "V1", "( V1 ) Golden Empire Colossus", "Imperial Steam", "24K Solid Gold Leaf & Obsidian", 0, 0xFF18181BL, 0xFFCA8A04L, 0xFFFACC15L, "Mighty high-pressure streamliner steam locomotive adorned in polished obsidian and 24K gold fluting."),
                LiveryVariant("V2_HYPER_MAGMA", trainId, "V2", "( V2 ) Volcanic Magma Crimson", "Infernal Steam Engine", "Molten Crimson & Burnished Steel", 10, 0xFF7F1D1DL, 0xFF1C1917L, 0xFFF97316L, "Volcanic theme featuring glowing magma furnace grates, crimson boiler casing, and heat-treated titanium."),
                LiveryVariant("V3_HYPER_ION", trainId, "V3", "( V3 ) Quantum Blue Ion Steam", "Cyber-Steam Labs", "Ion Cyan Plasma & Cobalt", 15, 0xFF082F49L, 0xFF0F172AL, 0xFF38BDF8L, "Experimental quantum steam locomotive with glowing cyan ionization coils along the boiler.")
            )

            "secret_golden_emd" -> listOf(
                LiveryVariant("V1_GOLDEN_SOVEREIGN", trainId, "V1", "( V1 ) 24K Solid Gold Sovereign", "Royal Gold Reserve", "Pure 24K Mirror Gold & Platinum", 0, 0xFFEAB308L, 0xFFCA8A04L, 0xFFFEF08AL, "Opulent solid gold heavy diesel locomotive with diamond-encrusted headlight bezels and mirror gloss polish."),
                LiveryVariant("V2_GOLDEN_ROSE", trainId, "V2", "( V2 ) Royal Rose Gold Edition", "Crown Imperial", "Lustrous Rose Gold & Diamond Trim", 10, 0xFFFB7185L, 0xFF9F1239L, 0xFFFFFFFFL, "Rare luxury edition cast in precious rose gold alloy with royal crests and sparkling platinum trim."),
                LiveryVariant("V3_GOLDEN_PLATINUM", trainId, "V3", "( V3 ) Platinum Palladium Imperial", "Monarch Rail", "Pure White Platinum & Cyan Diamond", 15, 0xFFE2E8F0L, 0xFF94A3B8L, 0xFF06B6D4L, "Ultra-high tier imperial locomotive forged from mirror platinum with illuminated cyan neon ground effects.")
            )

            "secret_cyber_maglev" -> listOf(
                LiveryVariant("V1_CYBER_CYAN", trainId, "V1", "( V1 ) Cyberpunk Neon Cyan", "Neo-Tokyo Hyperloop", "Carbon Black & Laser Cyan", 0, 0xFF09090BL, 0xFF18181BL, 0xFF00F0FFL, "Ultra-futuristic magnetic levitation train with glowing neon cyan circuitry and dark carbon weave."),
                LiveryVariant("V2_CYBER_SYNTHWAVE", trainId, "V2", "( V2 ) Synthwave Solar Orange", "Outrun Velocity", "Sunset Magenta & Solar Neon Orange", 10, 0xFF18181BL, 0xFFDB2777L, 0xFFF97316L, "Retro-futuristic 1980s outrun aesthetic with neon magenta body panels and high-speed solar orange stripes."),
                LiveryVariant("V3_CYBER_MATRIX", trainId, "V3", "( V3 ) Stealth Matrix Green", "Cyber Defense Grid", "Matte Obsidian & Terminal Green", 15, 0xFF050505L, 0xFF14532DL, 0xFF22C55EL, "Stealth cyber rail train with active camouflage matte black finish and streaming matrix digital rain.")
            )

            "secret_shinkansen_e2" -> listOf(
                LiveryVariant("V1_E2_HAYATE", trainId, "V1", "( V1 ) Hayate Azalea Pink", "JR East Shinkansen", "Pearl Snow White & Azalea Pink", 0, 0xFFFAFAFAL, 0xFF192A56L, 0xFFE6007EL, "Iconic Tohoku Shinkansen high-speed bullet train in pearl white, midnight indigo skirt, and azalea pink ribbon."),
                LiveryVariant("V2_E2_ASAMA", trainId, "V2", "( V2 ) Asama Crimson & Gold", "JR East Nagano", "Alpine White, Crimson & Gold", 10, 0xFFF8FAFCL, 0xFF991B1BL, 0xFFEAB308L, "Nagano Winter Olympic bullet train livery with bold crimson red skirt, alpine white, and gold accents."),
                LiveryVariant("V3_E2_TOHOKU_EVERGREEN", trainId, "V3", "( V3 ) Tohoku Alpine Evergreen", "JR East Eco Express", "Snow White, Evergreen & Violet", 15, 0xFFF8FAFCL, 0xFF14532DL, 0xFF8B5CF6L, "Scenic Tohoku mountain express scheme featuring rich evergreen forest green skirt and delicate violet stripe.")
            )

            "tow_steam_lner_1472" -> listOf(
                LiveryVariant("V1_SCOTSMAN_GREEN", trainId, "V1", "( V1 ) LNER Apple Green", "LNER Flying Scotsman", "Apple Green & Gold Pinstripes", 0, 0xFF15803DL, 0xFF14532DL, 0xFFFACC15L, "World's most famous steam locomotive in iconic LNER Apple Green livery with heavy-duty recovery tender."),
                LiveryVariant("V2_SCOTSMAN_WARTIME", trainId, "V2", "( V2 ) Wartime NE Austerity Black", "Ministry of War Transport", "Matte Austerity Black & White Lettering", 10, 0xFF18181BL, 0xFF27272AL, 0xFFFFFFFFL, "Historic World War II heavy rescue blackout scheme in matte industrial black with bold 'NE' lettering."),
                LiveryVariant("V3_SCOTSMAN_BR_BLUE", trainId, "V3", "( V3 ) BR Express Passenger Blue", "British Railways", "Cobalt Passenger Blue & Red Lining", 15, 0xFF1E3A8AL, 0xFF0F172AL, 0xFFDC2626L, "Rare British Railways 1950s express blue livery with crimson red pinstriping and polished brass boiler fittings.")
            )

            "tow_diesel_heavy_rescue" -> listOf(
                LiveryVariant("V1_TITAN_HAZARD", trainId, "V1", "( V1 ) Hazard Amber & Red Chevrons", "Emergency Rail Rescue", "High-Vis Amber & Safety Chevrons", 0, 0xFFEAB308L, 0xFF1E293BL, 0xFFDC2626L, "High-visibility breakdown rescue livery with hazard amber body, safety red diagonal chevrons, and roof strobes."),
                LiveryVariant("V2_TITAN_TACTICAL", trainId, "V2", "( V2 ) Tactical Heavy Recovery", "Rapid Incident Command", "Dark Navy Blue & Strobe Orange", 10, 0xFF0F172AL, 0xFF1E3A8AL, 0xFFF97316L, "Heavy industrial tactical emergency response unit in dark navy blue with emergency orange warning stripes."),
                LiveryVariant("V3_TITAN_FLAME_RED", trainId, "V3", "( V3 ) Emergency Fire & Rescue", "Rail Fire Corps", "Flame Red & Fluorescent Yellow", 15, 0xFFDC2626L, 0xFF991B1BL, 0xFFFACC15L, "Dedicated derailment fire suppression unit in bright flame red with fluorescent lime-yellow hazard markings.")
            )

            "tow_electric_dual_rescue" -> listOf(
                LiveryVariant("V1_AEROTOW_ELECTRIC", trainId, "V1", "( V1 ) High-Speed Electric Blue", "Cross-Channel Heavy Rescue", "Electric Blue & Warning Yellow", 0, 0xFF0284C7L, 0xFF334155L, 0xFFFACC15L, "High-speed corridor rescue locomotive in electric blue with full bright yellow warning nose."),
                LiveryVariant("V2_AEROTOW_TRIPLE_GREY", trainId, "V2", "( V2 ) Railfreight Triple-Grey", "British Railfreight", "Three-Tone Grey & Petroleum Red", 10, 0xFF64748BL, 0xFF334155L, 0xFFDC2626L, "Legendary British 1990s Triple-Grey sectoral freight livery with petroleum red roof stripe and cast depot plaques."),
                LiveryVariant("V3_AEROTOW_SAFETY_YELLOW", trainId, "V3", "( V3 ) Network Rail High-Vis", "Infrastructure Safety Unit", "Safety Warning Yellow & Dark Charcoal", 15, 0xFFFACC15L, 0xFF1E293BL, 0xFF0284C7L, "Official railway infrastructure emergency support scheme in all-over safety yellow with reflective micro-prisms.")
            )

            // === DEDICATED ČESKÉ DRÁHY & EUROPEAN ROSTER VARIANTS ===
            "nightjet_vectron_obb_cd" -> listOf(
                LiveryVariant("V1_NIGHTJET_BLUE", trainId, "V1", "( V1 ) ÖBB Nightjet Midnight", "ÖBB / ČD Nightjet", "Midnight Indigo & Starlight Stars", 0, 0xFF1E3A8AL, 0xFF0F172AL, 0xFFDC2626L, "Pan-European overnight sleeper express scheme in midnight indigo with constellation starlight ribbons and red roof accents."),
                LiveryVariant("V2_CD_NAJBRT_VECTRON", trainId, "V2", "( V2 ) ČD Najbrt EuroCity", "České Dráhy", "Najbrt Blue, Sky Blue & Light Grey", 10, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Official Czech Railways EuroCity livery in iconic Najbrt two-tone blue and pearl white."),
                LiveryVariant("V3_DB_TRAFFIC_RED", trainId, "V3", "( V3 ) DB Cargo Traffic Red", "Deutsche Bahn", "Verkehrsrot Red & White DB", 15, 0xFFDC2626L, 0xFF475569L, 0xFFFFFFFFL, "German rail traffic red cross-border freight and passenger express scheme.")
            )

            "cd_db_shark_link" -> listOf(
                LiveryVariant("V1_SHARK_NAJBRT", trainId, "V1", "( V1 ) RegioShark Najbrt Blue", "ČD RegioShark (Pesa 844)", "Aerodynamic Shark Nose & Sky Blue", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Iconic aggressive shark-nose diesel railcar in ČD Najbrt livery with deep blue cab wrap and light grey passenger saloons."),
                LiveryVariant("V2_SHARK_TRI_COLOR", trainId, "V2", "( V2 ) DB Regio Red Link", "DB Regio Shark", "Traffic Red & Light Grey", 10, 0xFFDC2626L, 0xFF1E293BL, 0xFFFFFFFFL, "Cross-border German regional specification in DB Verkehrsrot red with illuminated LED destination displays."),
                LiveryVariant("V3_SHARK_CARBON", trainId, "V3", "( V3 ) Carbon Night Predator", "ČD Prototype Fleet", "Obsidian Carbon & Neon Cyan", 15, 0xFF18181BL, 0xFF27272AL, 0xFF06B6D4L, "High-tech carbon test prototype with illuminated cyan headlights and tinted panoramic glass.")
            )

            "cd_vectron_comfortjet" -> listOf(
                LiveryVariant("V1_COMFORTJET_FLAGSHIP", trainId, "V1", "( V1 ) ComfortJet Najbrt Flagship", "České Dráhy ComfortJet", "Najbrt II Turquoise & Navy Ribbon", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFF38BDF8L, "Modernized 230 km/h ComfortJet intercity flagship livery in Najbrt II styling with streamlined aerodynamic nose."),
                LiveryVariant("V2_COMFORTJET_SILVER", trainId, "V2", "( V2 ) EuroCity Titanium Silver", "ČD International", "Brushed Titanium & Sapphire", 10, 0xFFCBD5E1L, 0xFF1E3A8AL, 0xFFDC2626L, "International high-speed express scheme with brushed titanium metallic body and scarlet safety accents."),
                LiveryVariant("V3_COMFORTJET_GOLD", trainId, "V3", "( V3 ) 180 Years Railway Jubilee", "ČD Heritage Express", "Jubilee Gold & Obsidian Blue", 15, 0xFFEAB308L, 0xFF0F172AL, 0xFFFFFFFFL, "Commemorative Austrian-Czech railway jubilee scheme in sovereign gold and navy obsidian.")
            )

            "cd_pendolino_class_680" -> listOf(
                LiveryVariant("V1_CD_PENDOLINO_ORIGINAL", trainId, "V1", "( V1 ) ČD SuperCity Pendolino", "České Dráhy SC 680", "Silver Metallic & Sapphire Ribbon", 0, 0xFFE2E8F0L, 0xFF1E3A8AL, 0xFFFACC15L, "Tilting high-speed Italian-Czech SuperCity bullet train with silver metallic body, royal sapphire window band, and yellow warning pilot."),
                LiveryVariant("V2_CD_PENDOLINO_NAJBRT", trainId, "V2", "( V2 ) SuperCity Najbrt Modern", "ČD SuperCity", "Najbrt Sky Blue & Dark Slate", 10, 0xFF0284C7L, 0xFF1E3A8AL, 0xFFFFFFFFL, "Updated modern Najbrt livery featuring high-contrast sky blue and white sweeping nose ribbons."),
                LiveryVariant("V3_CD_PENDOLINO_RED", trainId, "V3", "( V3 ) Frecciarossa Alpine Red", "Alpine Trans-Euro", "Frecciarossa Crimson & Platinum", 15, 0xFFDC2626L, 0xFF0F172AL, 0xFFE2E8F0L, "High-speed Alpine red livery celebrating the Pendolino tilting technology across Europe.")
            )

            "cd_regiopanter_640" -> listOf(
                LiveryVariant("V1_PANTER_NAJBRT", trainId, "V1", "( V1 ) RegioPanter Najbrt Blue", "ČD RegioPanter 640", "Two-Tone Blue & Green Door Accents", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFF22C55EL, "Low-floor modern electric multiple unit with spacious panoramic windows and green passenger access doors."),
                LiveryVariant("V2_PANTER_MORAVIA", trainId, "V2", "( V2 ) South Moravian Red & White", "IDS JMK Moravia", "Wine Red & Pure White Band", 10, 0xFF991B1BL, 0xFFFFFFFFL, 0xFFFACC15L, "South Moravian regional transport scheme in rich wine red and white stripes."),
                LiveryVariant("V3_PANTER_CYBER", trainId, "V3", "( V3 ) High-Voltage Electric Neon", "RegioPanter Express", "Slate Charcoal & Neon Lime", 15, 0xFF1E293BL, 0xFF84CC16L, 0xFF06B6D4L, "Modern urban commuter scheme in matte charcoal slate and neon electric lime accents.")
            )

            "cd_db_eurocity_186" -> listOf(
                LiveryVariant("V1_EC_TRAXX_NAJBRT", trainId, "V1", "( V1 ) ČD EuroCity Najbrt", "České Dráhy EuroCity", "Najbrt II Turquoise & Navy Ribbon", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Cross-border EuroCity Traxx in official Najbrt corporate blue with grey side panels."),
                LiveryVariant("V2_EC_DB_RED", trainId, "V2", "( V2 ) DB Regio & Fernverkehr Red", "Deutsche Bahn", "Traffic Red & Light Grey", 10, 0xFFDC2626L, 0xFF475569L, 0xFFFFFFFFL, "German Railway traffic red livery with prominent white DB logo on nose."),
                LiveryVariant("V3_EC_CAPTRAIN", trainId, "V3", "( V3 ) Captrain Lime & Charcoal", "Captrain International", "Anthracite & Neon Electric Lime", 15, 0xFF18181BL, 0xFF84CC16L, 0xFFFFFFFFL, "European open-access freight operator livery in dark anthracite and vivid electric lime.")
            )

            "cd_alstom_traxx_388" -> listOf(
                LiveryVariant("V1_TRAXX_388_CARGO", trainId, "V1", "( V1 ) ČD Cargo Blue Traxx 3", "ČD Cargo", "Marine Blue & Cyan Speed Chevron", 0, 0xFF002B66L, 0xFF0284C7L, 0xFFFFFFFFL, "State-of-the-art multi-system heavy freight Traxx MS3 in ČD Cargo royal blue."),
                LiveryVariant("V2_TRAXX_388_REGIOJET", trainId, "V2", "( V2 ) RegioJet Bright Yellow", "RegioJet Central Europe", "Sunshine Yellow & Midnight Black", 10, 0xFFFACC15L, 0xFF0F172AL, 0xFFFFFFFFL, "Popular private intercity express scheme in radiant sunshine yellow and jet black."),
                LiveryVariant("V3_TRAXX_388_METRANS", trainId, "V3", "( V3 ) Metrans Intermodal Red & Blue", "Metrans Intermodal", "Container Blue, White & Red", 15, 0xFF1E3A8AL, 0xFFDC2626L, 0xFFFFFFFFL, "Pan-European maritime container train scheme in bold blue with red cab whiskers.")
            )

            "cd_siemens_smartron" -> listOf(
                LiveryVariant("V1_SMARTRON_CAPRI", trainId, "V1", "( V1 ) Capri Blue Factory Standard", "Siemens Smartron", "Capri Blue & High-Vis Yellow", 0, 0xFF0284C7L, 0xFF1E3A8AL, 0xFFFACC15L, "Standardized high-reliability Siemens locomotive in vibrant Capri blue and warning yellow."),
                LiveryVariant("V2_SMARTRON_METRANS", trainId, "V2", "( V2 ) Unipetrol Chemical Orange", "Unipetrol Doprava", "Vivid Safety Orange & Slate Grey", 10, 0xFFEA580CL, 0xFF334155L, 0xFFFFFFFFL, "Heavy tank car freight scheme in high-visibility chemical safety orange."),
                LiveryVariant("V3_SMARTRON_SILVER", trainId, "V3", "( V3 ) Platinum Testbed Demo", "Siemens Rail Lab", "Brushed Platinum & Neon Cyan", 15, 0xFFCBD5E1L, 0xFF0F172AL, 0xFF06B6D4L, "Siemens aerodynamic trial scheme with metallic platinum silver wrap and cyan accents.")
            )

            "cd_interpanter_660" -> listOf(
                LiveryVariant("V1_INTERPANTER_NAJBRT", trainId, "V1", "( V1 ) Fast InterCity Najbrt", "ČD InterPanter 660", "Najbrt II Long-Distance Blue & White", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Long-distance multi-car electric express unit with high-speed nose and panoramic comfort coaches."),
                LiveryVariant("V2_INTERPANTER_GOLD", trainId, "V2", "( V2 ) Moravia Wine Express", "ČD Moravia", "Rich Burgundy & Gold Trim", 10, 0xFF831843L, 0xFFEAB308L, 0xFFFFFFFFL, "Inter-regional express scheme in lustrous burgundy wine with gold pinstripes."),
                LiveryVariant("V3_INTERPANTER_MIDNIGHT", trainId, "V3", "( V3 ) NightPanter Midnight Aero", "ČD Special Ops", "Midnight Obsidian & Cyan Laser", 15, 0xFF09090BL, 0xFF0284C7L, 0xFF38BDF8L, "Modern high-speed night runner package in obsidian with illuminated laser cyan waistlines.")
            )

            "cd_gorilla_class_350" -> listOf(
                LiveryVariant("V1_GORILA_CREAM_RED", trainId, "V1", "( V1 ) Classic Retro Cream & Crimson", "ČSD / ZSSK Gorila 350", "Ivory Cream & Crimson Red Ribbons", 0, 0xFF991B1BL, 0xFFFEF3C7L, 0xFF334155L, "Legendary Czechoslovak 160 km/h dual-system locomotive in classic cream and crimson red."),
                LiveryVariant("V2_GORILA_ZSSK_RED", trainId, "V2", "( V2 ) ZSSK Blonski Red & White", "ZSSK Slovakia", "Slovak National Red & Light Grey", 10, 0xFFDC2626L, 0xFFF8FAFCL, 0xFF475569L, "Modern Blonski design of Slovak Railways featuring red cab face and light grey body."),
                LiveryVariant("V3_GORILA_NAJBRT", trainId, "V3", "( V3 ) ČD EuroCity Najbrt Blue", "České Dráhy", "Najbrt Two-Tone Blue & Pearl", 15, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Czech EuroCity adaptation in deep Najbrt corporate blue and sky blue speedlines.")
            )

            "cd_pershing_class_163" -> listOf(
                LiveryVariant("V1_PERSHING_GREEN_YELLOW", trainId, "V1", "( V1 ) Vintage Taiga Green & Yellow", "ČSD Pershing 163", "Taiga Green & Safety Yellow Stripe", 0, 0xFF14532DL, 0xFFFACC15L, 0xFF475569L, "Original 3kV DC 'Pershing' electric locomotive in taiga green with wide 600mm yellow hazard band."),
                LiveryVariant("V2_PERSHING_NAJBRT", trainId, "V2", "( V2 ) ČD Modern Najbrt II", "České Dráhy", "Najbrt Sky Blue & Dark Slate", 10, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "Contemporary Czech commuter workhorse in high-contrast Najbrt two-tone blue."),
                LiveryVariant("V3_PERSHING_CARGO", trainId, "V3", "( V3 ) ČD Cargo Blue Fleet", "ČD Cargo", "Dark Blue Body & White Cargo Shield", 15, 0xFF002B66L, 0xFF1E3A8AL, 0xFFFFFFFFL, "Freight subsidiary scheme in rugged heavy-duty blue with white lettering.")
            )

            "cd_eso_class_362" -> listOf(
                LiveryVariant("V1_ESO_YELLOW_BLUE", trainId, "V1", "( V1 ) Classic Two-Tone Yellow & Blue", "ČD Eso 362 (Ace)", "Signal Yellow & Corporate Blue", 0, 0xFF002B66L, 0xFFFACC15L, 0xFFFFFFFFL, "Famous multi-system 3kV/25kV 'Eso' (Ace) electric locomotive with bright yellow cab nose."),
                LiveryVariant("V2_ESO_NAJBRT", trainId, "V2", "( V2 ) ČD Express Najbrt Flagship", "České Dráhy", "Najbrt II Navy & Light Grey", 10, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFF8FAFCL, "High-speed 140 km/h passenger express scheme in modern Najbrt livery."),
                LiveryVariant("V3_ESO_ZSSK_RETRO", trainId, "V3", "( V3 ) Retro Orange & Cream 1985", "ČSD Heritage", "Czechoslovak Orange & Warm Cream", 15, 0xFFEA580CL, 0xFFFEF3C7L, 0xFF1F2937L, "1980s delivery livery in vivid orange and warm cream with chrome cab crests.")
            )

            "cd_alstom_coradia_stream" -> listOf(
                LiveryVariant("V1_STREAM_NAJBRT", trainId, "V1", "( V1 ) High-Speed Stream Najbrt", "ČD RegioStream", "Aerodynamic Two-Tone Blue & Glass", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFF38BDF8L, "Ultra-modern aerodynamic electric multiple unit with smooth tapered bullet nose."),
                LiveryVariant("V2_STREAM_EMERALD", trainId, "V2", "( V2 ) Green Deal Eco Stream", "European Green Fleet", "Emerald Green & Pure White", 10, 0xFF047857L, 0xFFFFFFFFL, 0xFFFACC15L, "Zero-emission intercity livery in emerald green and crisp white."),
                LiveryVariant("V3_STREAM_CARBON", trainId, "V3", "( V3 ) Stealth Obsidian Stream", "ČD Nightline", "Matte Carbon & Electric Cyan", 15, 0xFF0F172AL, 0xFF0284C7L, 0xFF06B6D4L, "Modern night express package in stealth carbon with glowing cyan LED accents.")
            )

            "cd_vectron_dual_mode_248" -> listOf(
                LiveryVariant("V1_DUAL_VECTRON_NAJBRT", trainId, "V1", "( V1 ) Dual-Mode Najbrt Hybrid", "ČD Dual Power 248", "Najbrt Blue, Sky Blue & Warning Yellow", 0, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFFACC15L, "Versatile 25kV electric and diesel dual-mode locomotive for continuous electrified and non-electrified routes."),
                LiveryVariant("V2_DUAL_VECTRON_DB_ECO", trainId, "V2", "( V2 ) DB Eco Cargo Dual-Power", "DB Cargo Eco", "Traffic Red & Eco Leaf Green", 10, 0xFFDC2626L, 0xFF15803DL, 0xFFFFFFFFL, "German-Czech cross-border eco hybrid scheme in Verkehrsrot red and leaf green."),
                LiveryVariant("V3_DUAL_VECTRON_MATTE", trainId, "V3", "( V3 ) Stealth Shadow Operator", "European Rail Logistics", "Matte Black & Danger Yellow", 15, 0xFF18181BL, 0xFF27272AL, 0xFFFACC15L, "Heavy industrial multi-national freight livery in matte black with yellow warning nose cone.")
            )

            "cd_brejlovec_class_754" -> listOf(
                LiveryVariant("V1_BREJLOVEC_GOGGLES", trainId, "V1", "( V1 ) Classic Goggles Red & Yellow", "ČD Brejlovec 754", "Crimson Red, Yellow Band & Goggles", 0, 0xFFDC2626L, 0xFF475569L, 0xFFFACC15L, "World-renowned 'Goggles' (Brejlovec) diesel locomotive with distinctive recessed cab window frames in high-contrast yellow."),
                LiveryVariant("V2_BREJLOVEC_NAJBRT", trainId, "V2", "( V2 ) Najbrt II Speed Blue", "České Dráhy", "Najbrt Two-Tone Blue & White", 10, 0xFF1E3A8AL, 0xFF0284C7L, 0xFFFFFFFFL, "Corporate Najbrt II scheme with royal blue cab and sky blue body stripes."),
                LiveryVariant("V3_BREJLOVEC_GREEN", trainId, "V3", "( V3 ) Vintage Taiga Green 1975", "ČSD Heritage", "Taiga Green & Cream Roof", 15, 0xFF15803DL, 0xFFFEF3C7L, 0xFFFACC15L, "Original 1975 delivery scheme in rich taiga forest green and cream upper roof.")
            )

            else -> listOf(
                LiveryVariant("V1_STD", trainId, "V1", "( V1 ) Standard Service", "Rail Operator", "Factory Standard Livery", 0, 0xFFCBD5E1L, 0xFF1E3A8AL, 0xFFFACC15L, "Standard production railway livery with high-visibility safety markings."),
                LiveryVariant("V2_HERITAGE", trainId, "V2", "( V2 ) Heritage Edition", "Historic Fleet", "Heritage Custom Scheme", 10, 0xFF14532DL, 0xFFDC2626L, 0xFFFFFFFFL, "Preserved historical commemorative paint scheme with polished brass accents."),
                LiveryVariant("V3_MODERN", trainId, "V3", "( V3 ) Modernized Express", "High Speed Line", "Aerodynamic Modern Scheme", 15, 0xFF0F172AL, 0xFF0284C7L, 0xFF38BDF8L, "Modernized aero package with dark body and high-contrast illuminated speedlines.")
            )
        }
    }

    val BREAKCORE_MUSIC_TRACKS = listOf(
        BreakcoreMusicTrack("track_1", "1. Wabash Cannonball Express", "Country • Bluegrass Banjo & Flatpicking Guitar (124 BPM)", 124, 130.81f, "Classic highballing country railroad tune with rolling banjo, acoustic guitar flatpicking and driving train beat"),
        BreakcoreMusicTrack("track_2", "2. Freight Train Country Blues", "Country • Boom-Chick Bass & Honky-Tonk Swing (112 BPM)", 112, 116.54f, "Warm fingerpicked country blues with alternating root-fifth bass, brushed train snares and acoustic riffs"),
        BreakcoreMusicTrack("track_3", "3. Blue Ridge Mountain Haul", "Country • Upbeat Bluegrass Stomp & Fiddle Licks (132 BPM)", 132, 146.83f, "Fast Appalachian mountain bluegrass with driving acoustic rhythm, Scruggs banjo rolls and bright leads"),
        BreakcoreMusicTrack("track_4", "4. Midnight Prairie Special", "Country • Mellow Country Acoustic & Slide Harmonies (104 BPM)", 104, 110f, "Warm nostalgic country ballad evoking moonlit rolling prairie tracks, pedal steel hums and acoustic strums"),
        BreakcoreMusicTrack("track_5", "5. Nashville Rail Yard Shuffle", "Country • Telecaster Twang & Country Two-Step (120 BPM)", 120, 123.47f, "Energetic Nashville country two-step with rhythmic train shuffle, snappy snare cracks and twangy guitar fills"),
        BreakcoreMusicTrack("track_6", "6. Coal Miner's Highball", "Country • Kentucky Bluegrass Breakdown (136 BPM)", 136, 130.81f, "Thundering mountain coal train anthem featuring lightning-fast banjo picking and driving foot stomps"),
        BreakcoreMusicTrack("track_7", "7. Texas Panhandle Flyer", "Country • Western Swing & Train Beats (118 BPM)", 118, 98f, "Classic western country swing with walking upright bass, swung brush rhythms and soaring open prairie melodies"),
        BreakcoreMusicTrack("track_8", "8. Smoky Mountain Rambler", "Country • Old-Time Folk Fingerpicking (108 BPM)", 108, 116.54f, "Soulful acoustic folk fingerstyle capturing morning mist over the Great Smoky Mountains and winding steel rails"),
        BreakcoreMusicTrack("track_9", "9. Golden Spike Jubilee", "Country • Grand Country Breakdown (128 BPM)", 128, 146.83f, "Celebratory country railroad breakdown with galloping acoustic guitars, banjo flourishes and driving rhythm"),
        BreakcoreMusicTrack("track_10", "10. Shenandoah Valley Line", "Country • Sweet Country Strum & Mandolin Chops (115 BPM)", 115, 103.83f, "Peaceful Virginia railroad melody with rich acoustic strumming, mandolin chops and rolling countryside charm"),
        BreakcoreMusicTrack("track_11", "11. Highballing Through Dixie", "Country • Driving Southern Honky-Tonk (126 BPM)", 126, 130.81f, "Upbeat southern country locomotive anthem with punchy kicks, alternating bass and spirited country leads"),
        BreakcoreMusicTrack("track_12", "12. Cotton Belt Sunset", "Country • Slow Prairie Country Reverie (96 BPM)", 96, 110f, "Atmospheric golden-hour country track with gentle acoustic strums, sweet harmonic intervals and relaxed pacing"),
        BreakcoreMusicTrack("track_13", "13. Grand Ole Rail Opry", "Country • Traditional Appalachian Barn Stomp (130 BPM)", 130, 123.47f, "Joyful barn dance country stomp with high-energy train rhythm, syncopated banjo rolls and rich harmonies"),
        BreakcoreMusicTrack("track_14", "14. Wild West Iron Horse", "Country • Outlaw Country Rhythm & Whistle Echoes (116 BPM)", 116, 98f, "Driving outlaw country track with rugged acoustic rhythm, percussive chugs and frontier railroad grit"),
        BreakcoreMusicTrack("track_15", "15. Appalachian Express Finale", "Country • Virtuoso Bluegrass & Banjo Fireworks (138 BPM)", 138, 146.83f, "Climactic high-speed bluegrass showdown with virtuosic banjo runs, thumping stand-up bass and joyful train beat")
    )


    val HORROR_MUSIC_TRACKS = BREAKCORE_MUSIC_TRACKS

    val ALL_TRAINS = listOf(
        // 1. EMD F40PH-2D VIA Rail #6437 (Photo 1)
        TrainModel(
            id = "emd_f40ph_via",
            name = "EMD F40PH-2D VIA #6437",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.F40PH_SCREAMER_DIESEL,
            countryFlag = "🇨🇦",
            baseSpeed = 58f,
            baseReliability = 28.0f,
            basePower = 42.0f,
            baseAdherence = 12.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = false,
            description = "Iconic Canadian passenger diesel with bright yellow nose chevron, stainless steel sides and 'love the way' motif.",
            defaultBodyColor = 0xFFD4D8DD, // Silver body
            defaultRoofColor = 0xFF334155, // Slate roof
            defaultStripeColor = 0xFFF59E0B // VIA Yellow nose chevron
        ),

        // 2. EMD F40PH-3 Metra #120 'Patriot' (Photo 2)
        TrainModel(
            id = "emd_f40ph_metra",
            name = "EMD F40PH-3 Metra #120",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.F40PH_SCREAMER_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 56f,
            baseReliability = 27.5f,
            basePower = 40.5f,
            baseAdherence = 11.8f,
            priceGold = 4500,
            priceDiamonds = 45,
            isSecret = false,
            description = "Metra 'Honoring All Who Served' custom veteran tribute livery with blue cab, camo body and American flag.",
            defaultBodyColor = 0xFF2F4F4F, // Camo slate green
            defaultRoofColor = 0xFF1E3A8A, // Metra blue cab/roof
            defaultStripeColor = 0xFFEF4444 // Red/white/blue flag accent
        ),

        // 3. EMD GP9 High Hood Norfolk Southern #2758 (Photo 3)
        TrainModel(
            id = "emd_gp9_highhood",
            name = "EMD GP9 High Hood NS #2758",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 44f,
            baseReliability = 24.0f,
            basePower = 36.0f,
            baseAdherence = 12.5f,
            priceGold = 7500,
            priceDiamonds = 70,
            isSecret = false,
            description = "Classic high-nose road switcher in Norfolk Southern black with white horsehead logo and numberboards.",
            defaultBodyColor = 0xFF1C1F24, // NS Black
            defaultRoofColor = 0xFF0F1115,
            defaultStripeColor = 0xFFFFFFFF // White NS Horsehead & numbers
        ),

        // 4. EMD GP9-RM CN Rebuild #1751 (Photo 4)
        TrainModel(
            id = "emd_gp9_normal",
            name = "EMD GP9-RM CN #1751",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇨🇦",
            baseSpeed = 46f,
            baseReliability = 25.0f,
            basePower = 37.0f,
            baseAdherence = 12.8f,
            priceGold = 10500,
            priceDiamonds = 95,
            isSecret = false,
            description = "Halifax Southwestern / CN chopped low-nose road switcher rebuild in forest green and white chevron livery.",
            defaultBodyColor = 0xFF1E4D2B, // Forest green
            defaultRoofColor = 0xFF122E1A,
            defaultStripeColor = 0xFFFFFFFF // White chevron
        ),

        // 5. Siemens Vectron DB Cargo Red (Photo 8)
        TrainModel(
            id = "siemens_vectron_red",
            name = "Siemens Vectron DB Cargo",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇩🇪",
            baseSpeed = 72f,
            baseReliability = 35.0f,
            basePower = 52.0f,
            baseAdherence = 15.0f,
            priceGold = 24000,
            priceDiamonds = 170,
            isSecret = false,
            description = "High-performance European freight electric locomotive in DB Traffic Red with quad pantographs.",
            defaultBodyColor = 0xFFDC2626, // DB Traffic Red
            defaultRoofColor = 0xFF1E293B, // Dark grey roof
            defaultStripeColor = 0xFFFFFFFF // White DB logo
        ),

        // 6. Siemens Vectron MRCE Black (Photo 9)
        TrainModel(
            id = "siemens_vectron_black",
            name = "Siemens Vectron MRCE Black",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇪🇺",
            baseSpeed = 72f,
            baseReliability = 36.0f,
            basePower = 52.0f,
            baseAdherence = 15.2f,
            priceGold = 26000,
            priceDiamonds = 180,
            isSecret = false,
            description = "Stealth European Bo-Bo electric in MRCE matte black with yellow warning panels and dual pantographs.",
            defaultBodyColor = 0xFF1E2124, // Matte black
            defaultRoofColor = 0xFF0F1113,
            defaultStripeColor = 0xFFEAB308 // MRCE Warning Yellow
        ),

        // 7. Siemens Vectron Demo White (Photo 10)
        TrainModel(
            id = "siemens_vectron_demo",
            name = "Siemens Vectron Demo White",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇪🇺",
            baseSpeed = 74f,
            baseReliability = 37.0f,
            basePower = 53.0f,
            baseAdherence = 15.5f,
            priceGold = 28000,
            priceDiamonds = 195,
            isSecret = false,
            description = "Siemens factory demonstration multi-system electric with modern white/grey styling and Vectron branding.",
            defaultBodyColor = 0xFFF1F5F9, // Clean white/light grey
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFF0284C7 // Siemens cyan/blue
        ),

        // 8. Amtrak Acela Express #2000 (Photo 6)
        TrainModel(
            id = "amtrak_acela_bullet",
            name = "Amtrak Acela Express #2000",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
            countryFlag = "🇺🇸",
            baseSpeed = 82f,
            baseReliability = 38.0f,
            basePower = 56.0f,
            baseAdherence = 16.0f,
            priceGold = 34000,
            priceDiamonds = 240,
            isSecret = false,
            description = "America's premier high-speed bullet locomotive with aerodynamic wedge nose and acela wing livery.",
            defaultBodyColor = 0xFFCBD5E1, // Silver grey
            defaultRoofColor = 0xFF1E3A8A, // Blue roof ribbon
            defaultStripeColor = 0xFF0284C7 // Acela cyan wing
        ),

        // 9. Siemens American Flyer Bullet (Photo 5)
        TrainModel(
            id = "siemens_american_flyer",
            name = "Siemens American Flyer",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
            countryFlag = "🇺🇸",
            baseSpeed = 80f,
            baseReliability = 37.0f,
            basePower = 55.0f,
            baseAdherence = 15.8f,
            priceGold = 32000,
            priceDiamonds = 225,
            isSecret = false,
            description = "High-speed streamlined bullet express with aerodynamic bullet nose, blue roof and red speed stripes.",
            defaultBodyColor = 0xFFE2E8F0,
            defaultRoofColor = 0xFF1D4ED8,
            defaultStripeColor = 0xFFEF4444
        ),

        // 10. ČD 742 EffiShunter Center-Cab (Photo 7)
        TrainModel(
            id = "cd_effishunter_742",
            name = "ČD 742 EffiShunter",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🇨🇿",
            baseSpeed = 48f,
            baseReliability = 29.0f,
            basePower = 41.0f,
            baseAdherence = 13.5f,
            priceGold = 15000,
            priceDiamonds = 115,
            isSecret = false,
            description = "European center-cab road switcher (České dráhy 742 700-0) with dual low hoods and blue/white striping.",
            defaultBodyColor = 0xFF1E40AF, // Czech Railway Blue
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFFF8FAFC // White stripe
        ),

        // 11. Union Pacific Big Boy 4014
        TrainModel(
            id = "up_big_boy_4014",
            name = "UP Big Boy 4014 (4-8-8-4)",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.BIG_BOY_ARTICULATED_STEAM,
            countryFlag = "🇺🇸",
            baseSpeed = 46f,
            baseReliability = 24.0f,
            basePower = 52.0f,
            baseAdherence = 16.5f,
            priceGold = 25000,
            priceDiamonds = 180,
            isSecret = false,
            description = "The world's largest operating articulated steam locomotive with double 8-wheel drive banks.",
            defaultBodyColor = 0xFF1B1E22,
            defaultRoofColor = 0xFF0D0F11,
            defaultStripeColor = 0xFFB8860B
        ),

        // 12. Pacific 4-6-2 Iron Titan
        TrainModel(
            id = "pacific_462",
            name = "Pacific 4-6-2 Iron Titan",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.PACIFIC_CLASSIC_STEAM,
            countryFlag = "🇬🇧",
            baseSpeed = 40f,
            baseReliability = 20.0f,
            basePower = 34.0f,
            baseAdherence = 10.5f,
            priceGold = 5000,
            priceDiamonds = 50,
            isSecret = false,
            description = "Classic British iron steam locomotive with roaring firebox and large driving wheels.",
            defaultBodyColor = 0xFF233B2B,
            defaultRoofColor = 0xFF181A1B,
            defaultStripeColor = 0xFFD4AF37
        ),

        // 13. NYC Hudson J3a 'Dreyfuss'
        TrainModel(
            id = "nyc_dreyfuss_hudson",
            name = "NYC J3a Dreyfuss Hudson",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.DREYFUSS_STREAMLINE_STEAM,
            countryFlag = "🇺🇸",
            baseSpeed = 55f,
            baseReliability = 26.0f,
            basePower = 41.0f,
            baseAdherence = 12.0f,
            priceGold = 22000,
            priceDiamonds = 160,
            isSecret = false,
            description = "World-famous 1938 Art Deco streamlined steam masterpiece designed for the 20th Century Limited.",
            defaultBodyColor = 0xFF334155,
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFF94A3B8
        ),

        // 14. CP Royal Hudson 2850
        TrainModel(
            id = "cp_royal_hudson",
            name = "CP Royal Hudson 2850",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.ROYAL_HUDSON_STEAM,
            countryFlag = "🇨🇦",
            baseSpeed = 50f,
            baseReliability = 27.0f,
            basePower = 40.0f,
            baseAdherence = 11.5f,
            priceGold = 21000,
            priceDiamonds = 150,
            isSecret = false,
            description = "Semi-streamlined 4-6-4 Royal Hudson in royal blue and black with polished casing.",
            defaultBodyColor = 0xFF1E3A8A,
            defaultRoofColor = 0xFF0F172A,
            defaultStripeColor = 0xFFFFD700
        ),

        // 15. Burlington F7 Streamliner
        TrainModel(
            id = "burlington_f7",
            name = "Burlington F7 Streamliner",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 52f,
            baseReliability = 22.0f,
            basePower = 35.0f,
            baseAdherence = 10.0f,
            priceGold = 9000,
            priceDiamonds = 85,
            isSecret = false,
            description = "Streamlined bulldog cab diesel express with stainless steel plating.",
            defaultBodyColor = 0xFFB01E28,
            defaultRoofColor = 0xFF4A4E54,
            defaultStripeColor = 0xFFF5B700
        ),

        // 16. British Rail Class 55 'Deltic'
        TrainModel(
            id = "br_class_55_deltic",
            name = "BR Class 55 'Deltic'",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.DELTIC_NAPIER_DIESEL,
            countryFlag = "🇬🇧",
            baseSpeed = 57f,
            baseReliability = 23.0f,
            basePower = 43.0f,
            baseAdherence = 12.0f,
            priceGold = 18500,
            priceDiamonds = 140,
            isSecret = false,
            description = "Twin Napier Deltic opposed-piston two-stroke diesel with distinct racing engine sound.",
            defaultBodyColor = 0xFF1B4D3E,
            defaultRoofColor = 0xFF263238,
            defaultStripeColor = 0xFFFFD700
        ),

        // 17. GE AC4400CW Mountain Titan
        TrainModel(
            id = "ge_ac4400cw",
            name = "GE AC4400CW Hauler",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 50f,
            baseReliability = 30.0f,
            basePower = 52.0f,
            baseAdherence = 17.0f,
            priceGold = 27000,
            priceDiamonds = 185,
            isSecret = false,
            description = "4400 HP computerized AC mountain hauler with steerable high-adherence bogies.",
            defaultBodyColor = 0xFF1565C0,
            defaultRoofColor = 0xFF263238,
            defaultStripeColor = 0xFFFFC107
        ),

        // 18. EMD SD70ACe Heavy Freight
        TrainModel(
            id = "emd_sd70ace",
            name = "EMD SD70ACe Freight",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 52f,
            baseReliability = 31.0f,
            basePower = 50.0f,
            baseAdherence = 16.5f,
            priceGold = 29000,
            priceDiamonds = 195,
            isSecret = false,
            description = "4300 HP modern AC traction heavy freight titan with angular flared radiators.",
            defaultBodyColor = 0xFFFFB300,
            defaultRoofColor = 0xFF212121,
            defaultStripeColor = 0xFFC62828
        ),

        // 19. ALCO PA-1 Warbonnet
        TrainModel(
            id = "alco_pa1",
            name = "ALCO PA-1 Warbonnet",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.ALCO_V16_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 54f,
            baseReliability = 22.0f,
            basePower = 38.0f,
            baseAdherence = 11.0f,
            priceGold = 16000,
            priceDiamonds = 120,
            isSecret = false,
            description = "Regarded as the most handsome diesel ever built, sporting the legendary red & silver Warbonnet scheme.",
            defaultBodyColor = 0xFFC62828,
            defaultRoofColor = 0xFF78909C,
            defaultStripeColor = 0xFFFFD54F
        ),

        // 20. DB Class 103 TEE
        TrainModel(
            id = "db_class_103",
            name = "DB Class 103 TEE",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇩🇪",
            baseSpeed = 68f,
            baseReliability = 28.0f,
            basePower = 46.0f,
            baseAdherence = 13.5f,
            priceGold = 23000,
            priceDiamonds = 165,
            isSecret = false,
            description = "Legendary Trans-Europ-Express heavy electric locomotive with streamlined curved nose.",
            defaultBodyColor = 0xFFE5DCC3,
            defaultRoofColor = 0xFF2C3238,
            defaultStripeColor = 0xFF8B0000
        ),

        // 21. TGV Sud-Est 01
        TrainModel(
            id = "tgv_sud_est",
            name = "TGV Sud-Est 01",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
            countryFlag = "🇫🇷",
            baseSpeed = 78f,
            baseReliability = 28.0f,
            basePower = 50.0f,
            baseAdherence = 13.0f,
            priceGold = 31000,
            priceDiamonds = 215,
            isSecret = false,
            description = "Iconic French high-speed orange bullet train with aerodynamic wedge nose.",
            defaultBodyColor = 0xFFFF6F00,
            defaultRoofColor = 0xFF37474F,
            defaultStripeColor = 0xFFECEFF1
        ),

        // 22. Shinkansen 0 Series Bullet
        TrainModel(
            id = "shinkansen_0_series",
            name = "Shinkansen 0 Series Bullet",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SHINKANSEN_BULLET_ELECTRIC,
            countryFlag = "🇯🇵",
            baseSpeed = 75f,
            baseReliability = 33.0f,
            basePower = 48.0f,
            baseAdherence = 14.0f,
            priceGold = 30000,
            priceDiamonds = 210,
            isSecret = false,
            description = "Pioneering Japanese high-speed streamliner with rounded airplane-style bullet nose.",
            defaultBodyColor = 0xFFFAFAFA,
            defaultRoofColor = 0xFF455A64,
            defaultStripeColor = 0xFF0D47A1
        ),

        // 23. PRR GG1 Electric Champion
        TrainModel(
            id = "prr_gg1",
            name = "PRR GG1 Electric Champion",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.PRR_GG1_ARTDECO_ELECTRIC,
            countryFlag = "🇺🇸",
            baseSpeed = 66f,
            baseReliability = 29.0f,
            basePower = 48.0f,
            baseAdherence = 14.5f,
            priceGold = 26000,
            priceDiamonds = 175,
            isSecret = false,
            description = "Art-Deco bi-directional electric titan designed by Raymond Loewy with 5 gold pinstripes.",
            defaultBodyColor = 0xFF1B382B,
            defaultRoofColor = 0xFF121E17,
            defaultStripeColor = 0xFFFFD700
        ),

        // 24. Soviet VL85 12-Axle Heavy
        TrainModel(
            id = "soviet_vl85",
            name = "Soviet VL85 12-Axle",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SOVIET_VL85_ELECTRIC,
            countryFlag = "🇷🇺",
            baseSpeed = 48f,
            baseReliability = 32.0f,
            basePower = 55.0f,
            baseAdherence = 18.0f,
            priceGold = 35000,
            priceDiamonds = 240,
            isSecret = false,
            description = "Dual-section 12-axle Siberian heavy freight electric monster built for brutal subzero passes.",
            defaultBodyColor = 0xFF2E7D32,
            defaultRoofColor = 0xFF212121,
            defaultStripeColor = 0xFFE0E0E0
        ),

        // === 4 SECRET PROMO CODE TRAINS ===
        // 25. Phantom Express 9000
        TrainModel(
            id = "secret_ghost_train",
            name = "Phantom Express 9000",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.GHOST_SPECTRAL_STEAM,
            countryFlag = "👻",
            baseSpeed = 65f,
            baseReliability = 35.0f,
            basePower = 55.0f,
            baseAdherence = 16.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            description = "Spectral ghost locomotive that glides with green phantom steam! Unlocked via code.",
            defaultBodyColor = 0xFF163E3C,
            defaultRoofColor = 0xFF0D2524,
            defaultStripeColor = 0xFF00FFCC
        ),

        // 26. Hyper Steam Colossus 4-8-4
        TrainModel(
            id = "secret_hyper_steam",
            name = "Hyper Steam Colossus 4-8-4",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.HYPER_COLOSSUS_STEAM,
            countryFlag = "⚡",
            baseSpeed = 70f,
            baseReliability = 38.0f,
            basePower = 58.0f,
            baseAdherence = 17.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            description = "Massive golden-trimmed supercharged steam monster with triple boilers.",
            defaultBodyColor = 0xFF291E14,
            defaultRoofColor = 0xFF120D09,
            defaultStripeColor = 0xFFFFD700
        ),

        // 27. EMD Golden Eagle GP60
        TrainModel(
            id = "secret_golden_emd",
            name = "EMD Golden Eagle GP60",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "👑",
            baseSpeed = 72f,
            baseReliability = 40.0f,
            basePower = 60.0f,
            baseAdherence = 18.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            description = "Ultra-rare 24K gold diesel locomotive crafted for elite railroad tycoons.",
            defaultBodyColor = 0xFFD4AF37,
            defaultRoofColor = 0xFF3D3212,
            defaultStripeColor = 0xFFFFFFFF
        ),

        // 28. CyberRail X-Maglev
        TrainModel(
            id = "secret_cyber_maglev",
            name = "CyberRail X-Maglev",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.CYBER_MAGLEV_PLASMA,
            countryFlag = "🚀",
            baseSpeed = 88f,
            baseReliability = 42.0f,
            basePower = 65.0f,
            baseAdherence = 20.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Next-gen aerodynamic magnetic levitation train with ionized plasma thrusters.",
            defaultBodyColor = 0xFF0F172A,
            defaultRoofColor = 0xFF020617,
            defaultStripeColor = 0xFF00F0FF
        ),

        // 29. Shinkansen E2 Series 1000 'Hayate' (Hidden Secret Supersonic Prototype - LVL 100 MAX)
        TrainModel(
            id = "secret_shinkansen_e2",
            name = "Shinkansen E2-1000 'Hayate'",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SHINKANSEN_BULLET_ELECTRIC,
            countryFlag = "🇯🇵",
            baseSpeed = 165f, // Extremely fast! Reaches 350 - 450+ km/h
            baseReliability = 48.0f,
            basePower = 98.0f,
            baseAdherence = 24.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 100, // Exclusive Max Level 100!
            description = "新幹線E2系1000番台電車 - Supersonic Japanese bullet train with aerodynamic duckbill nose, azalea pink stripe, deep indigo skirt, red low-noise aerofoil pantographs, and matching streamlined coaches. Max level 100 capable of reaching speeds beyond 300+ km/h!",
            defaultBodyColor = 0xFFFAFAFA, // Pearl Snow White
            defaultRoofColor = 0xFFE2E8F0, // Aerodynamic Slate White
            defaultStripeColor = 0xFFE6007E // Vivid Azalea Pink / Magenta
        ),

        // 30. LNER 1472 Flying Scotsman Heavy Tow Steam Locomotive (Towinclude)
        TrainModel(
            id = "tow_steam_lner_1472",
            name = "LNER 1472 Steam Tow Locomotive",
            type = TrainType.STEAM,
            soundProfile = SoundProfileType.LNER_A3_STEAM_WHISTLE,
            countryFlag = "🇬🇧",
            baseSpeed = 82f,
            baseReliability = 44.0f,
            basePower = 85.0f,
            baseAdherence = 24.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "LNER 1472 Class A1/A3 4-6-2 Pacific heavy steam towing locomotive in iconic Apple Green livery with matching coal tender. Features high-pitch to low-shift chime whistle and high tractive towing power.",
            defaultBodyColor = 0xFF1B6E34, // Apple Green
            defaultRoofColor = 0xFF1E2124, // Cab Roof Black
            defaultStripeColor = 0xFFF59E0B // LNER Gold Lining
        ),

        // 31. Titan HD-5000 Diesel Rescue & Tow Locomotive (Towinclude)
        TrainModel(
            id = "tow_diesel_heavy_rescue",
            name = "Titan HD-5000 Diesel Tow Locomotive",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🛠️",
            baseSpeed = 75f,
            baseReliability = 46.0f,
            basePower = 90.0f,
            baseAdherence = 28.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Heavy-duty industrial breakdown and towing diesel locomotive with high-traction bogies, rescue winches, and mid-sounding resonant throat horn.",
            defaultBodyColor = 0xFFEAB308, // Hazard Amber
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFFDC2626 // Safety Red Chevrons
        ),

        // 32. AeroTow Class 92 Electric Rescue Locomotive (Towinclude)
        TrainModel(
            id = "tow_electric_dual_rescue",
            name = "AeroTow Class 92 Electric Rescue Locomotive",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "⚡",
            baseSpeed = 85f,
            baseReliability = 45.0f,
            basePower = 94.0f,
            baseAdherence = 26.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Twin-pantograph European heavy rescue electric towing locomotive with high-output traction motors and authentic British 2-tone warning horn.",
            defaultBodyColor = 0xFF0284C7, // Electric Blue
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFFFACC15 // Warning Yellow
        ),

        // 33. British Rail Class 390 Virgin Pendolino (Supersonic Bullet Train - LVL 100 MAX)
        TrainModel(
            id = "secret_virgin_pendolino_390",
            name = "Virgin Class 390 Pendolino",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.CLASS_390_PENDOLINO_ELECTRIC,
            countryFlag = "🇬🇧",
            baseSpeed = 170f, // Supersonic bullet train
            baseReliability = 50.0f,
            basePower = 100.0f,
            baseAdherence = 26.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 100, // Exclusive Max Level 100!
            description = "Class 390 British tilting high-speed bullet train in iconic Virgin Trains red & silver livery. Features aerodynamic nose, dual-tone British horn, matching streamlined passenger coaches, and trailing backwards train cab. Capable of speeds beyond 300+ km/h!",
            defaultBodyColor = 0xFFE2E8F0, // Aerodynamic Metallic Silver
            defaultRoofColor = 0xFFDC2626, // Virgin Red Roof & Nose Cap
            defaultStripeColor = 0xFFDC2626 // Bold Virgin Red Bodyside Ribbon
        ),

        // === 7 METRA COMMUTER LOCOMOTIVES & MULTIPLE UNITS ===

        // 34. Metra EMD F40PH-3 "City of Chicago" (Diesel Commuter Legend)
        TrainModel(
            id = "metra_f40ph_screamer",
            name = "Metra EMD F40PH-3 Commuter",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.F40PH_SCREAMER_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 125f,
            baseReliability = 92.0f,
            basePower = 78.0f,
            baseAdherence = 24.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Legendary 3,200 HP turbocharged diesel locomotive operating Chicago commuter services. Features authentic HEP screamer generator, stainless steel body fluting, and 3 custom liveries (Classic, Patriot #120, Raven Fade).",
            defaultBodyColor = 0xFFCBD5E1,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFFF97316,
            customLiveryVariants = METRA_F40PH_LIVERY_VARIANTS
        ),

        // 35. Metra MPI MP36PH-3S "Streamlined Commuter" (Diesel Express)
        TrainModel(
            id = "metra_mp36ph_express",
            name = "Metra MPI MP36PH-3S Express",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 135f,
            baseReliability = 94.0f,
            basePower = 85.0f,
            baseAdherence = 25.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "High-visibility aerodynamic cowl commuter diesel generating 3,600 HP. Equipped with microprocessor traction control and 3 heritage liveries (Blue Wave, Rock Island Tribute, RTA 1970s Retro).",
            defaultBodyColor = 0xFFCBD5E1,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFFF97316,
            customLiveryVariants = METRA_MP36PH_LIVERY_VARIANTS
        ),

        // 36. Metra EMD F59PHI "Aero Commuter" (Streamlined Diesel)
        TrainModel(
            id = "metra_f59phi_silver",
            name = "Metra EMD F59PHI Aero",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 140f,
            baseReliability = 95.0f,
            basePower = 82.0f,
            baseAdherence = 24.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Aerodynamically sculpted composite cowl locomotive with high-speed nose and dual isolated cab. Includes 3 unique liveries (Silver Flash, Illinois Central Heritage, Milwaukee Road Hiawatha).",
            defaultBodyColor = 0xFFE2E8F0,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFF0284C7,
            customLiveryVariants = METRA_F59PHI_LIVERY_VARIANTS
        ),

        // 37. Metra Highliner Gallery EMU (Electric Multiple Unit - Metra Electric District)
        TrainModel(
            id = "metra_highliner_emu",
            name = "Metra Highliner Gallery EMU",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇺🇸",
            baseSpeed = 130f,
            baseReliability = 96.0f,
            basePower = 90.0f,
            baseAdherence = 27.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Electric multiple-unit bi-level gallery car with roof pantographs, regenerative dynamic braking, and 1,500V DC electric catenary propulsion. Features 3 designs (Stainless EMU, Modern Wave, Heritage IC Orange).",
            defaultBodyColor = 0xFFE2E8F0,
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFF1E3A8A,
            customLiveryVariants = METRA_HIGHLINER_LIVERY_VARIANTS
        ),

        // 38. Metra Siemens SC-44 Charger (Eco Tier 4 Modern Diesel-Electric)
        TrainModel(
            id = "metra_charger_sc44",
            name = "Metra Siemens SC-44 Charger",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 150f,
            baseReliability = 98.0f,
            basePower = 92.0f,
            baseAdherence = 26.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Advanced 4,400 HP Tier 4 diesel-electric streamliner equipped with insulated clean-power engine, IGBT inverters, and 3 modern paint designs (Blue Ribbon, Lincoln Service Pride, Stealth Carbon).",
            defaultBodyColor = 0xFFE2E8F0,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFF06B6D4,
            customLiveryVariants = METRA_CHARGER_LIVERY_VARIANTS
        ),

        // 39. Metra EMD E8 Streamliner (Twin-Engine Vintage Classic)
        TrainModel(
            id = "metra_e8_streamliner",
            name = "Metra EMD E8 Streamliner",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇺🇸",
            baseSpeed = 135f,
            baseReliability = 88.0f,
            basePower = 84.0f,
            baseAdherence = 25.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Iconic dual 567B prime mover 2,250 HP passenger streamliner with classic bulldog nose, porthole side windows, and 3 Chicago historic liveries (Metra Transition Blue, CB&Q Burlington Silver Streak, C&NW 400).",
            defaultBodyColor = 0xFFCBD5E1,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFFF97316,
            customLiveryVariants = METRA_E8_LIVERY_VARIANTS
        ),

        // 40. Metra ALP-45 / Coradia Dual-Power High-Speed Electric
        TrainModel(
            id = "metra_alstom_coradia_dual",
            name = "Metra Dual-Power Fastliner",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
            countryFlag = "🇺🇸",
            baseSpeed = 165f,
            baseReliability = 99.0f,
            basePower = 98.0f,
            baseAdherence = 28.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "State-of-the-art dual-mode electric locomotive running seamlessly on 25kV AC overhead catenary or onboard twin high-speed diesel generators. Features 3 striking designs (Fastliner Electric, Chicago Skyline, Polar Midnight).",
            defaultBodyColor = 0xFFE2E8F0,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFF38BDF8,
            customLiveryVariants = METRA_CORADIA_LIVERY_VARIANTS
        ),

        // === 16 ČESKÉ DRÁHY & EUROPEAN TRAINS (PROMO CODE: "Ceske") ===
        // 41. ČD / ÖBB Nightjet Vectron 193 (High-Speed Nightjet Express)
        TrainModel(
            id = "nightjet_vectron_obb_cd",
            name = "ČD / ÖBB Nightjet Vectron 193",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 160f,
            baseReliability = 98.0f,
            basePower = 94.0f,
            baseAdherence = 27.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "High-speed multi-system Siemens Vectron in iconic ÖBB / ČD Nightjet Midnight Blue with golden starfield constellations and crimson roof band.",
            defaultBodyColor = 0xFF0B1B3D, // Nightjet Midnight Blue
            defaultRoofColor = 0xFFDC2626, // Crimson accent
            defaultStripeColor = 0xFFFACC15 // Golden Star Constellations
        ),

        // 42. ČD Shark RegioShark DB Class 844 (Aerodynamic Shark DMU)
        TrainModel(
            id = "cd_db_shark_link",
            name = "ČD Shark RegioShark DB 844",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🇨🇿",
            baseSpeed = 140f,
            baseReliability = 96.0f,
            basePower = 88.0f,
            baseAdherence = 26.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Iconic Pesa Link 'RegioShark' 844 DMU with distinctive predatory shark snout, turquoise-blue Najbrt graphics, and DB cross-border crests.",
            defaultBodyColor = 0xFF0284C7, // ČD Turquoise Blue
            defaultRoofColor = 0xFF1E293B, // Slate roof
            defaultStripeColor = 0xFF38BDF8 // Sky blue shark fin streak
        ),

        // 43. ČD / DB EuroCity BR 186 Traxx (Cross-Border EuroCity)
        TrainModel(
            id = "cd_db_eurocity_186",
            name = "ČD / DB EuroCity BR 186 Traxx",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇩🇪",
            baseSpeed = 150f,
            baseReliability = 95.0f,
            basePower = 92.0f,
            baseAdherence = 26.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Bombardier Traxx MS2 EuroCity electric locomotive operating cross-border Berlin-Prague services in DB Traffic Red and ČD EuroCity ribbon.",
            defaultBodyColor = 0xFFDC2626, // DB Traffic Red
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFF1E40AF // ČD Blue EuroCity ribbon
        ),

        // 44. ČD Cargo Alstom Traxx MS3 Class 388 (Heavy Freight Titan)
        TrainModel(
            id = "cd_alstom_traxx_388",
            name = "ČD Cargo Alstom Traxx 388",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 145f,
            baseReliability = 97.0f,
            basePower = 96.0f,
            baseAdherence = 29.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "State-of-the-art Alstom Traxx MS3 Class 388 multi-system freight locomotive in dark navy and electric turquoise ČD Cargo livery.",
            defaultBodyColor = 0xFF0F172A, // Deep Navy
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFF06B6D4 // Electric Turquoise
        ),

        // 45. ČD Siemens Smartron 192 (Precision Electric)
        TrainModel(
            id = "cd_siemens_smartron",
            name = "ČD Siemens Smartron 192",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 155f,
            baseReliability = 98.0f,
            basePower = 93.0f,
            baseAdherence = 27.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Siemens Smartron standard electric platform featuring Caprari Blue body, clean white aerodynamic front shield, and IGBT traction inverters.",
            defaultBodyColor = 0xFF0284C7, // Caprari Blue
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFFF8FAFC // Clean White
        ),

        // 46. ČD Vectron 193 ComfortJet (Flagship High-Speed)
        TrainModel(
            id = "cd_vectron_comfortjet",
            name = "ČD Vectron 193 ComfortJet",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 165f,
            baseReliability = 99.0f,
            basePower = 95.0f,
            baseAdherence = 28.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "České dráhy flagship 230 km/h ComfortJet locomotive in Najbrt 2 light gray, navy lower skirt, and sky blue lightning arrows.",
            defaultBodyColor = 0xFFE2E8F0, // Najbrt Light Gray
            defaultRoofColor = 0xFF1E3A8A, // Navy Roof
            defaultStripeColor = 0xFF0284C7 // Sky Blue Arrow
        ),

        // 47. ČD Class 680 Pendolino Silver (Tilting Bullet Express)
        TrainModel(
            id = "cd_pendolino_class_680",
            name = "ČD Class 680 Pendolino Silver",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 165f,
            baseReliability = 97.5f,
            basePower = 94.5f,
            baseAdherence = 28.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "SuperCity tilting high-speed train in shimmering silver body, sapphire blue window ribbon, and yellow warning wedge.",
            defaultBodyColor = 0xFFCBD5E1, // Silver Metallic
            defaultRoofColor = 0xFF1E3A8A, // Sapphire Blue
            defaultStripeColor = 0xFFFACC15 // Yellow Wedge
        ),

        // 48. ČD RegioPanter Class 640 EMU (Modern Electric Multiple-Unit)
        TrainModel(
            id = "cd_regiopanter_640",
            name = "ČD RegioPanter Class 640 EMU",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇨🇿",
            baseSpeed = 145f,
            baseReliability = 97.0f,
            basePower = 91.0f,
            baseAdherence = 27.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Modern Czech articulated electric commuter train with low-floor passenger bays, Najbrt blue livery, and lime-yellow boarding doors.",
            defaultBodyColor = 0xFF0284C7,
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFF84CC16 // Lime Yellow
        ),

        // 49. ČD InterPanter Class 660 Express (Long-Distance EMU)
        TrainModel(
            id = "cd_interpanter_660",
            name = "ČD InterPanter Class 660",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇨🇿",
            baseSpeed = 150f,
            baseReliability = 98.0f,
            basePower = 93.0f,
            baseAdherence = 27.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Long-distance InterPanter fast electric train with streamlined nose, quiet regenerative brakes, and dual high-speed pantographs.",
            defaultBodyColor = 0xFF1E3A8A,
            defaultRoofColor = 0xFF0F172A,
            defaultStripeColor = 0xFF38BDF8
        ),

        // 50. ČD / ZSSK Class 350 "Gorila" (Dual-System Express)
        TrainModel(
            id = "cd_gorilla_class_350",
            name = "ČD / ZSSK 350 'Gorila'",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇨🇿",
            baseSpeed = 145f,
            baseReliability = 94.0f,
            basePower = 92.0f,
            baseAdherence = 26.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Legendary Skoda 55E 'Gorila' express locomotive capable of 3kV DC & 25kV AC operation with split cockpit windshield and classic crème/red livery.",
            defaultBodyColor = 0xFFFDF6B2, // Vintage Crème
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFFDC2626 // Bold Red Stripe
        ),

        // 51. ČD Class 163 "Peršing" (DC Workhorse)
        TrainModel(
            id = "cd_pershing_class_163",
            name = "ČD Class 163 'Peršing'",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇨🇿",
            baseSpeed = 140f,
            baseReliability = 95.0f,
            basePower = 90.0f,
            baseAdherence = 26.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Iconic Skoda 71E DC locomotive nicknamed 'Peršing' with fluted corrugated side panels, robust chopper traction, and Najbrt blue body.",
            defaultBodyColor = 0xFF0284C7,
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFFE2E8F0
        ),

        // 52. ČD Class 362 "Eso" (Multi-System Speedster)
        TrainModel(
            id = "cd_eso_class_362",
            name = "ČD Class 362 'Eso'",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.EURO_ELECTRIC_103,
            countryFlag = "🇨🇿",
            baseSpeed = 145f,
            baseReliability = 96.0f,
            basePower = 91.5f,
            baseAdherence = 26.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Skoda 69ER multi-system fast locomotive nicknamed 'Eso' (Ace) with yellow front warning band and 140 km/h passenger gearing.",
            defaultBodyColor = 0xFF1E3A8A,
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFFEAB308 // Yellow Ace Stripe
        ),

        // 53. ČD Class 749 "Bardotka" Diesel (Vintage Growler)
        TrainModel(
            id = "cd_bardotka_class_749",
            name = "ČD Class 749 'Bardotka'",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.EMD_2STROKE_DIESEL,
            countryFlag = "🇨🇿",
            baseSpeed = 135f,
            baseReliability = 92.0f,
            basePower = 88.0f,
            baseAdherence = 25.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "World-famous CKD T478.1 diesel locomotive with famous rounded front nose contours and deep, throaty slow-speed diesel engine growl.",
            defaultBodyColor = 0xFFB91C1C, // Deep Red
            defaultRoofColor = 0xFF334155,
            defaultStripeColor = 0xFFFEF08A // Crème band
        ),

        // 54. ČD Class 754 "Brejlovec" (Goggles Diesel)
        TrainModel(
            id = "cd_brejlovec_class_754",
            name = "ČD Class 754 'Brejlovec'",
            type = TrainType.DIESEL,
            soundProfile = SoundProfileType.GE_4STROKE_TURBODIESEL,
            countryFlag = "🇨🇿",
            baseSpeed = 138f,
            baseReliability = 94.0f,
            basePower = 89.0f,
            baseAdherence = 26.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Famous CKD 'Brejlovec' (Diving Goggles) passenger diesel locomotive with prominent recessed windscreen frames and lightning chevron flash.",
            defaultBodyColor = 0xFFDC2626, // Red
            defaultRoofColor = 0xFF1E293B,
            defaultStripeColor = 0xFFFACC15 // Yellow Lightning Flash
        ),

        // 55. ČD Alstom Coradia Stream H2 (Next-Gen Hydrogen/Electric)
        TrainModel(
            id = "cd_alstom_coradia_stream",
            name = "ČD Alstom Coradia Stream",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 160f,
            baseReliability = 99.0f,
            basePower = 95.0f,
            baseAdherence = 27.5f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Next-generation Alstom Coradia Stream high-efficiency passenger train with aerodynamic glass nose, zero-emission mode, and digital traction.",
            defaultBodyColor = 0xFFE2E8F0,
            defaultRoofColor = 0xFF0284C7,
            defaultStripeColor = 0xFF06B6D4
        ),

        // 56. ČD Vectron Dual Mode Class 248 (Electro-Diesel Hybrid)
        TrainModel(
            id = "cd_vectron_dual_mode_248",
            name = "ČD Vectron Dual Mode 248",
            type = TrainType.ELECTRIC_MAGLEV,
            soundProfile = SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
            countryFlag = "🇨🇿",
            baseSpeed = 158f,
            baseReliability = 98.5f,
            basePower = 96.0f,
            baseAdherence = 28.0f,
            priceGold = 0,
            priceDiamonds = 0,
            isSecret = true,
            maxUpgradeLevel = 50,
            description = "Siemens Vectron Dual Mode Class 248 hybrid locomotive seamlessly switching between 15kV overhead electric catenary and high-power diesel traction.",
            defaultBodyColor = 0xFF0284C7,
            defaultRoofColor = 0xFF1E3A8A,
            defaultStripeColor = 0xFF10B981 // Eco Emerald Stripe
        )
    )

    val ALL_CONTRACTS = listOf(
        // === AUTHENTIC DYNAMIC IDENTICAL FLEET MATCHED CONSIST CONTRACT ===
        ContractJob(
            id = "contract_fleet_identical_consist",
            title = "Identical Fleet Consist: Matched Trainset Run (3,200 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Central Passenger Terminal",
            destinationStation = "Summit Alpine Vista",
            distanceMeters = 3200f,
            hillSeverity = "Smooth Foothills & Mountain Grade",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("fc_1", "Matched Fleet Coach #1", RailCarType.PASSENGER_COACH, 1250f, "Matched Passenger Salon"),
                RailCar("fc_2", "Matched Fleet Coach #2", RailCarType.PASSENGER_COACH, 1250f, "Matched Passenger Salon"),
                RailCar("fc_3", "Matched Fleet Coach #3", RailCarType.PASSENGER_COACH, 1280f, "Matched Fleet Lounge")
            ),
            rewardSilver = 35000,
            rewardGold = 2800,
            rewardDiamonds = 95,
            xpReward = 1800,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "2.0 min"
        ),
        ContractJob(
            id = "contract_matched_consist_express",
            title = "Matched Consist Express (3,500 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Grand Central Mainline Depot",
            destinationStation = "Highland Mountain Summit",
            distanceMeters = 3500f,
            hillSeverity = "Mainline Mountain Run",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("m_auto_1", "Train-Matched Rail Car #1", RailCarType.PASSENGER_COACH, 1300f, "Matched Fleet Cars"),
                RailCar("m_auto_2", "Train-Matched Rail Car #2", RailCarType.PASSENGER_COACH, 1300f, "Matched Fleet Cars"),
                RailCar("m_auto_3", "Train-Matched Rail Car #3", RailCarType.PASSENGER_COACH, 1350f, "Matched Fleet Cars")
            ),
            rewardSilver = 26000,
            rewardGold = 2200,
            rewardDiamonds = 80,
            xpReward = 1500,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "2.0 - 2.5 min"
        ),
        // === AUTHENTIC MATCHED CONSIST CONTRACTS ===
        ContractJob(
            id = "contract_matched_metra_rush",
            title = "Chicago Metra Bi-Level Rush (2,400 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Chicago Union Station",
            destinationStation = "Aurora Transportation Center",
            distanceMeters = 2400f,
            hillSeverity = "Midwest Commuter Line",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("m1", "Metra Gallery Coach #7021", RailCarType.METRA_GALLERY_COACH, 1400f, "Commuters & Passengers"),
                RailCar("m2", "Metra Gallery Coach #7045", RailCarType.METRA_GALLERY_COACH, 1400f, "Express Commuters"),
                RailCar("m_cab", "Metra Bi-Level Cab Car #8512", RailCarType.METRA_CAB_CAR, 1450f, "Engineer Cab & Passengers")
            ),
            rewardSilver = 12500,
            rewardGold = 950,
            rewardDiamonds = 35,
            xpReward = 650,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1.5 - 2 min"
        ),
        ContractJob(
            id = "contract_matched_cd_vindobona",
            title = "ČD Vindobona EuroCity Express (2,800 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Praha hlavní nádraží",
            destinationStation = "Brno hlavní nádraží",
            distanceMeters = 2800f,
            hillSeverity = "Bohemian Rolling Highlands",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("cd1", "ČD ComfortJet First Class Apmz", RailCarType.CD_COMFORTJET_COACH, 1350f, "EuroCity VIP Travelers"),
                RailCar("cd2", "ČD ComfortJet Second Class Bmz", RailCarType.CD_COMFORTJET_COACH, 1350f, "EuroCity Express Travelers"),
                RailCar("cd3", "ČD ComfortJet End Coach Bmpz", RailCarType.CD_COMFORTJET_COACH, 1350f, "Panoramic Lounge Passengers")
            ),
            rewardSilver = 15000,
            rewardGold = 1200,
            rewardDiamonds = 45,
            xpReward = 800,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1.8 - 2.2 min"
        ),
        ContractJob(
            id = "contract_matched_cd_pendolino",
            title = "ČD SuperCity Pendolino Tilting Sprint (3,000 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Praha hl.n. - Platform 6",
            destinationStation = "Ostrava Svinov Central",
            distanceMeters = 3000f,
            hillSeverity = "High-Speed Czech Corridor",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("cdp1", "ČD Class 081 First Class", RailCarType.CD_PENDOLINO_COACH, 1300f, "SuperCity Business Salon"),
                RailCar("cdp2", "ČD Class 082 Bistro Restaurant", RailCarType.CD_PENDOLINO_COACH, 1350f, "Gourmet Dining Passengers"),
                RailCar("cdpcab", "ČD Class 682 Driving Trailer", RailCarType.CD_PENDOLINO_CAB, 1320f, "Tail Aerodynamic Driving Cab")
            ),
            rewardSilver = 18500,
            rewardGold = 1450,
            rewardDiamonds = 55,
            xpReward = 950,
            requiredDriverLevel = 2,
            isHighTier = false,
            estimatedMinutes = "2 - 2.5 min"
        ),
        ContractJob(
            id = "contract_matched_acela_bullet",
            title = "Amtrak Acela 150 MPH NEC Bullet (3,200 km)",
            category = ContractCategory.PASSENGER,
            originStation = "New York Penn Station",
            destinationStation = "Washington Union Station",
            distanceMeters = 3200f,
            hillSeverity = "Northeast High-Speed Catenary",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("ac1", "Acela First Class Parlor Car", RailCarType.AMTRAK_ACELA_COACH, 1400f, "First Class Executives"),
                RailCar("ac2", "Acela Business Club Car", RailCarType.AMTRAK_ACELA_COACH, 1400f, "Express Commuters"),
                RailCar("accab", "Acela Trailing Power Car #2024", RailCarType.AMTRAK_ACELA_CAB, 1500f, "Auxiliary Propulsion Unit")
            ),
            rewardSilver = 21000,
            rewardGold = 1600,
            rewardDiamonds = 60,
            xpReward = 1100,
            requiredDriverLevel = 2,
            isHighTier = false,
            estimatedMinutes = "2 - 2.5 min"
        ),
        ContractJob(
            id = "contract_matched_steam_bigboy",
            title = "UP Big Boy 4014 Pullman Century Special (3,500 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Cheyenne Heritage Depot",
            destinationStation = "Sherman Hill Summit",
            distanceMeters = 3500f,
            hillSeverity = "Steep Sherman Hill Mountain Grade",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("pb1", "Pullman Heavyweight Parlor 'Cheyenne'", RailCarType.STEAM_PULLMAN_COACH, 1600f, "Heritage Steam Travelers"),
                RailCar("pb2", "Pullman Dining Car 'Wyoming'", RailCarType.STEAM_PULLMAN_COACH, 1650f, "Fine Steam Dining"),
                RailCar("pb3", "Pullman Observation Lounge 'Centurion'", RailCarType.STEAM_PULLMAN_COACH, 1600f, "Brass Open-Platform Lounge")
            ),
            rewardSilver = 24000,
            rewardGold = 1800,
            rewardDiamonds = 70,
            xpReward = 1300,
            requiredDriverLevel = 3,
            isHighTier = false,
            estimatedMinutes = "2.2 - 2.8 min"
        ),
        ContractJob(
            id = "contract_matched_cd_shark",
            title = "ČD RegioShark Forest Mountain Shuttle (2,200 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Liberec Central",
            destinationStation = "Česká Lípa Meadow",
            distanceMeters = 2200f,
            hillSeverity = "Jizera Mountain Curves",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("cdsh1", "ČD RegioShark Low-Floor Apmz", RailCarType.CD_REGIO_COACH, 1250f, "Mountain Hikers & Commuters"),
                RailCar("cdsh2", "ČD RegioShark Trailer Bmz", RailCarType.CD_REGIO_COACH, 1250f, "Regional Commuters")
            ),
            rewardSilver = 11000,
            rewardGold = 850,
            rewardDiamonds = 30,
            xpReward = 550,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1.4 - 1.8 min"
        ),
        ContractJob(
            id = "contract_matched_metra_rock_island",
            title = "Rock Island Metra Express: MP36PH-3S (2,600 km)",
            category = ContractCategory.PASSENGER,
            originStation = "LaSalle Street Station",
            destinationStation = "Joliet Gateway Center",
            distanceMeters = 2600f,
            hillSeverity = "Rock Island Express District",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("mri1", "Metra Bi-Level Gallery Coach #7050", RailCarType.METRA_GALLERY_COACH, 1400f, "South Side Commuters"),
                RailCar("mricab", "Metra Bi-Level Cab Car #8520", RailCarType.METRA_CAB_CAR, 1450f, "Cab Control Passengers")
            ),
            rewardSilver = 13500,
            rewardGold = 1050,
            rewardDiamonds = 40,
            xpReward = 720,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1.6 - 2 min"
        ),
        ContractJob(
            id = "contract_matched_shinkansen_hayate",
            title = "Tohoku Shinkansen E2 Hayate Bullet (3,600 km)",
            category = ContractCategory.PASSENGER,
            originStation = "Tokyo Station - Track 21",
            destinationStation = "Morioka Central Terminal",
            distanceMeters = 3600f,
            hillSeverity = "Tohoku High-Speed Viaducts",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("sh1", "Shinkansen E2 Standard Coach E226", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Hayate Express Passengers"),
                RailCar("sh2", "Shinkansen E2 Pantograph Coach E225", RailCarType.SHINKANSEN_E2_COACH, 1220f, "Aerofoil Catenary Car"),
                RailCar("sh3", "Shinkansen E2 Luxury Green Car E215", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Green Car First Class Salon")
            ),
            rewardSilver = 26000,
            rewardGold = 2000,
            rewardDiamonds = 75,
            xpReward = 1400,
            requiredDriverLevel = 3,
            isHighTier = false,
            estimatedMinutes = "2.2 - 2.8 min"
        ),
        // === LEVEL 1 CONTRACTS ===
        ContractJob(
            id = "contract_canyon_timber",
            title = "Canyon Timber Run (1,800 km)",
            originStation = "Red Rock Depot",
            destinationStation = "Canyon Mill",
            distanceMeters = 1800f,
            hillSeverity = "Gentle Canyon Hills",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Timber Flatcar #1", RailCarType.LUMBER_FLATCAR, 1200f, "Heavy Pine Logs")
            ),
            rewardSilver = 4800,
            rewardGold = 380,
            rewardDiamonds = 12,
            xpReward = 200,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1 - 1.5 min"
        ),
        ContractJob(
            id = "contract_lvl1_desert_gravel",
            title = "Desert Gravel Shuttle (1,600 km)",
            originStation = "Sandstone Quarry",
            destinationStation = "Canyon Siding",
            distanceMeters = 1600f,
            hillSeverity = "Flat Desert Track",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Ballast Hopper", RailCarType.HEAVY_COAL_HOPPER, 1100f, "Crushed Granite")
            ),
            rewardSilver = 4200,
            rewardGold = 320,
            rewardDiamonds = 10,
            xpReward = 180,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1 - 1.2 min"
        ),
        ContractJob(
            id = "contract_lvl1_pine_scenic",
            title = "Pine Valley Scenic Commuter (1,900 km)",
            originStation = "Pine Valley Junction",
            destinationStation = "Alpine Meadow",
            distanceMeters = 1900f,
            hillSeverity = "Gentle Forest Slope",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Passenger Coach 101", RailCarType.PASSENGER_COACH, 1000f, "Local Hikers")
            ),
            rewardSilver = 5200,
            rewardGold = 410,
            rewardDiamonds = 14,
            xpReward = 220,
            requiredDriverLevel = 1,
            isHighTier = false,
            estimatedMinutes = "1 - 1.5 min"
        ),
        ContractJob(
            id = "contract_lvl2_scrap_yard",
            title = "Industrial Scrap Haul (2,100 km)",
            originStation = "Foundry Yard",
            destinationStation = "Scrap Terminal",
            distanceMeters = 2100f,
            hillSeverity = "River Valley Flat",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Steel Boxcar", RailCarType.CONTAINER_BOXCAR, 1300f, "Recycled Iron Sheets")
            ),
            rewardSilver = 6500,
            rewardGold = 500,
            rewardDiamonds = 16,
            xpReward = 280,
            requiredDriverLevel = 2,
            isHighTier = false,
            estimatedMinutes = "1.2 - 1.6 min"
        ),
        ContractJob(
            id = "contract_lvl2_redwood_cedar",
            title = "Coastal Cedar Express (2,300 km)",
            originStation = "Eureka Mill",
            destinationStation = "Ocean Bluff Depot",
            distanceMeters = 2300f,
            hillSeverity = "Rolling Coastal Dunes",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Flatcar Cedar", RailCarType.LUMBER_FLATCAR, 1250f, "Aromatic Cedar Planks")
            ),
            rewardSilver = 7400,
            rewardGold = 580,
            rewardDiamonds = 18,
            xpReward = 320,
            requiredDriverLevel = 2,
            isHighTier = false,
            estimatedMinutes = "1.3 - 1.8 min"
        ),

        // === LEVEL 3-5 CONTRACTS ===
        ContractJob(
            id = "contract_desert_fuel",
            title = "Grand Mesa Petroleum (2,800 km)",
            originStation = "Canyon Mill",
            destinationStation = "Dusty Junction",
            distanceMeters = 2800f,
            hillSeverity = "Smooth Mesa Slopes",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Fuel Tanker A", RailCarType.FUEL_TANKER, 1600f, "Refined Diesel"),
                RailCar("c2", "Timber Flatcar", RailCarType.LUMBER_FLATCAR, 1200f, "Cedar Lumber")
            ),
            rewardSilver = 9500,
            rewardGold = 750,
            rewardDiamonds = 25,
            xpReward = 450,
            requiredDriverLevel = 3,
            isHighTier = false,
            estimatedMinutes = "1.5 - 2 min"
        ),
        ContractJob(
            id = "contract_lvl3_subzero_coal",
            title = "Frostbite Coal Supply (2,700 km)",
            originStation = "Northern Siding",
            destinationStation = "Tundra Power Station",
            distanceMeters = 2700f,
            hillSeverity = "Icy Undulations",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Coal Hopper #1", RailCarType.HEAVY_COAL_HOPPER, 1700f, "Heating Coal"),
                RailCar("c2", "Boxcar Arctic", RailCarType.CONTAINER_BOXCAR, 1200f, "Emergency Rations")
            ),
            rewardSilver = 9800,
            rewardGold = 780,
            rewardDiamonds = 26,
            xpReward = 470,
            requiredDriverLevel = 3,
            isHighTier = false,
            estimatedMinutes = "1.5 - 2 min"
        ),
        ContractJob(
            id = "contract_lvl4_chemical_corridor",
            title = "Chemical Valley Tanker Haul (3,100 km)",
            originStation = "Refinery Alpha",
            destinationStation = "Chemical Depot 4",
            distanceMeters = 3100f,
            hillSeverity = "Gentle River Valley Grades",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Acid Tanker", RailCarType.FUEL_TANKER, 1650f, "Sulfuric Acid"),
                RailCar("c2", "Polymer Boxcar", RailCarType.CONTAINER_BOXCAR, 1350f, "Industrial Plastics")
            ),
            rewardSilver = 12500,
            rewardGold = 950,
            rewardDiamonds = 30,
            xpReward = 580,
            requiredDriverLevel = 4,
            isHighTier = false,
            estimatedMinutes = "1.8 - 2.2 min"
        ),
        ContractJob(
            id = "contract_lvl4_glacier_tourist",
            title = "Glacier Vista Panorama (3,000 km)",
            originStation = "Alpine Base Camp",
            destinationStation = "Summit Lodge",
            distanceMeters = 3000f,
            hillSeverity = "Mountain Grade",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Vista Dome Coach", RailCarType.PASSENGER_COACH, 1150f, "Sightseers"),
                RailCar("c2", "Dining Car", RailCarType.PASSENGER_COACH, 1200f, "Gourmet Catering")
            ),
            rewardSilver = 13200,
            rewardGold = 1050,
            rewardDiamonds = 34,
            xpReward = 620,
            requiredDriverLevel = 4,
            isHighTier = false,
            estimatedMinutes = "1.8 - 2.2 min"
        ),
        ContractJob(
            id = "contract_lvl5_copper_canyon",
            title = "Copper Canyon Heavy Freight (3,400 km)",
            originStation = "Copper Ridge Mine",
            destinationStation = "Smelter Siding",
            distanceMeters = 3400f,
            hillSeverity = "Canyon Slopes",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Ore Hopper #1", RailCarType.HEAVY_COAL_HOPPER, 1850f, "Raw Copper Ore"),
                RailCar("c2", "Ore Hopper #2", RailCarType.HEAVY_COAL_HOPPER, 1850f, "Concentrate Slag"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1500f, "Heavy Diesel")
            ),
            rewardSilver = 16000,
            rewardGold = 1250,
            rewardDiamonds = 40,
            xpReward = 750,
            requiredDriverLevel = 5,
            isHighTier = false,
            estimatedMinutes = "2 - 2.5 min"
        ),

        // === LEVEL 6-9 CONTRACTS ===
        ContractJob(
            id = "contract_alpine_pass",
            title = "Alpine Glacier Pass (3,600 km)",
            originStation = "Pine Valley",
            destinationStation = "Glacier Summit",
            distanceMeters = 3600f,
            hillSeverity = "Mountain Incline",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Passenger Coach A", RailCarType.PASSENGER_COACH, 1100f, "Alpine Tourists"),
                RailCar("c2", "Container Boxcar", RailCarType.CONTAINER_BOXCAR, 1400f, "Skiing Gear"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Aviation Kerosene")
            ),
            rewardSilver = 18000,
            rewardGold = 1400,
            rewardDiamonds = 45,
            xpReward = 850,
            requiredDriverLevel = 6,
            isHighTier = false,
            estimatedMinutes = "2 - 2.5 min"
        ),
        ContractJob(
            id = "contract_lvl6_sequoia_timber",
            title = "Giant Sequoia Heavy Log Haul (3,700 km)",
            originStation = "Deep Forest Camp",
            destinationStation = "Coast Harbor Sawmill",
            distanceMeters = 3700f,
            hillSeverity = "Coastal Redwood Ridge",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Log Flatcar 1", RailCarType.LUMBER_FLATCAR, 1500f, "Virgin Redwood Logs"),
                RailCar("c2", "Log Flatcar 2", RailCarType.LUMBER_FLATCAR, 1500f, "Spruce Beams"),
                RailCar("c3", "Caboose Coach", RailCarType.PASSENGER_COACH, 950f, "Lumber Crew")
            ),
            rewardSilver = 20500,
            rewardGold = 1600,
            rewardDiamonds = 50,
            xpReward = 950,
            requiredDriverLevel = 6,
            isHighTier = false,
            estimatedMinutes = "2.1 - 2.6 min"
        ),
        ContractJob(
            id = "contract_lvl7_arctic_blizzard",
            title = "Arctic Expedition Fuel & Supply (3,800 km)",
            originStation = "Polar Outpost Base",
            destinationStation = "Glacier Research Dome",
            distanceMeters = 3800f,
            hillSeverity = "Subzero Glacier Ridges",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Cryo Tanker", RailCarType.FUEL_TANKER, 1700f, "Arctic Heating Oil"),
                RailCar("c2", "Insulated Container", RailCarType.CONTAINER_BOXCAR, 1500f, "Scientific Sensors"),
                RailCar("c3", "Heavy Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1800f, "Anthracite")
            ),
            rewardSilver = 23500,
            rewardGold = 1850,
            rewardDiamonds = 55,
            xpReward = 1050,
            requiredDriverLevel = 7,
            isHighTier = false,
            estimatedMinutes = "2.2 - 2.7 min"
        ),
        ContractJob(
            id = "contract_lvl8_steel_ingot",
            title = "Blast Furnace Ingot Express (3,850 km)",
            originStation = "Foundry Central",
            destinationStation = "Automotive Stamping Yard",
            distanceMeters = 3850f,
            hillSeverity = "Industrial Grade",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Heavy Flatcar", RailCarType.LUMBER_FLATCAR, 1900f, "Red-Hot Steel Slabs"),
                RailCar("c2", "Steel Coil Boxcar", RailCarType.CONTAINER_BOXCAR, 1600f, "Galvanized Steel Coils"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1650f, "Quenching Lubricant")
            ),
            rewardSilver = 27000,
            rewardGold = 2100,
            rewardDiamonds = 60,
            xpReward = 1200,
            requiredDriverLevel = 8,
            isHighTier = false,
            estimatedMinutes = "2.3 - 2.8 min"
        ),
        ContractJob(
            id = "contract_lvl9_pacific_intermodal",
            title = "Pacific Intermodal Container Express (3,950 km)",
            originStation = "Oakland Port Terminal",
            destinationStation = "Sacramento Inland Hub",
            distanceMeters = 3950f,
            hillSeverity = "Bay Valley Curves",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Intermodal Double Stack", RailCarType.CONTAINER_BOXCAR, 1600f, "Electronics Containers"),
                RailCar("c2", "Intermodal Double Stack", RailCarType.CONTAINER_BOXCAR, 1600f, "Import Goods"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Marine Diesel")
            ),
            rewardSilver = 30000,
            rewardGold = 2400,
            rewardDiamonds = 65,
            xpReward = 1320,
            requiredDriverLevel = 9,
            isHighTier = false,
            estimatedMinutes = "2.4 - 2.9 min"
        ),

        // === LEVEL 10-14 CONTRACTS ===
        ContractJob(
            id = "contract_industrial_heavy",
            title = "Smelting Heavy Haul (3,900 km)",
            originStation = "Smelting Yard",
            destinationStation = "Steel City Central",
            distanceMeters = 3900f,
            hillSeverity = "River Valley Grades",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Heavy Coal Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 1900f, "Coking Coal"),
                RailCar("c2", "Heavy Coal Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 1900f, "Iron Ore Pellets"),
                RailCar("c3", "Container Boxcar", RailCarType.CONTAINER_BOXCAR, 1400f, "Fabricated Steel")
            ),
            rewardSilver = 32000,
            rewardGold = 2600,
            rewardDiamonds = 70,
            xpReward = 1400,
            requiredDriverLevel = 10,
            isHighTier = false,
            estimatedMinutes = "2.5 - 3 min"
        ),
        ContractJob(
            id = "contract_lvl10_desert_lithium",
            title = "Mojave Lithium Battery Salt Haul (4,100 km)",
            originStation = "Salton Sea Brine Plant",
            destinationStation = "Gigafactory Terminal",
            distanceMeters = 4100f,
            hillSeverity = "Desert Mesa Curves",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Chemical Tanker", RailCarType.FUEL_TANKER, 1800f, "Lithium Carbonate"),
                RailCar("c2", "Boxcar Sealed", RailCarType.CONTAINER_BOXCAR, 1500f, "Cathode Precursors"),
                RailCar("c3", "Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1700f, "Refined Carbon")
            ),
            rewardSilver = 36000,
            rewardGold = 2900,
            rewardDiamonds = 78,
            xpReward = 1550,
            requiredDriverLevel = 10,
            isHighTier = false,
            estimatedMinutes = "2.5 - 3 min"
        ),
        ContractJob(
            id = "contract_lvl11_alpine_express",
            title = "Gotthard Base Mountain Express Route (4,200 km)",
            originStation = "Lucerne Freight Hub",
            destinationStation = "Lugano South Yard",
            distanceMeters = 4200f,
            hillSeverity = "Alpine Deep Incline",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Passenger Express Coach", RailCarType.PASSENGER_COACH, 1200f, "International Travelers"),
                RailCar("c2", "Express Cargo Boxcar", RailCarType.CONTAINER_BOXCAR, 1550f, "Swiss Precision Parts"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1650f, "Transformer Coolant")
            ),
            rewardSilver = 40000,
            rewardGold = 3200,
            rewardDiamonds = 85,
            xpReward = 1700,
            requiredDriverLevel = 11,
            isHighTier = false,
            estimatedMinutes = "2.6 - 3.1 min"
        ),
        ContractJob(
            id = "contract_lvl12_space_booster",
            title = "Space Coast Solid Rocket Booster (4,350 km)",
            originStation = "Propulsion Plant Utah",
            destinationStation = "Cape Launch Complex",
            distanceMeters = 4350f,
            hillSeverity = "Smooth High Speed Track",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Heavy Oversize Flatcar", RailCarType.LUMBER_FLATCAR, 2200f, "Solid Rocket Segment A"),
                RailCar("c2", "Heavy Oversize Flatcar", RailCarType.LUMBER_FLATCAR, 2200f, "Solid Rocket Segment B"),
                RailCar("c3", "Armored Security Boxcar", RailCarType.CONTAINER_BOXCAR, 1600f, "Flight Avionics"),
                RailCar("c4", "Cryo Tanker", RailCarType.FUEL_TANKER, 1750f, "Hydrazine Oxidizer")
            ),
            rewardSilver = 45000,
            rewardGold = 3600,
            rewardDiamonds = 95,
            xpReward = 1880,
            requiredDriverLevel = 12,
            isHighTier = true,
            estimatedMinutes = "2.6 - 3.1 min"
        ),
        ContractJob(
            id = "contract_lvl13_arctic_pipeline",
            title = "Trans-Alaska Crude & Pipeline Modules (4,400 km)",
            originStation = "Prudhoe Bay Yard",
            destinationStation = "Valdez Maritime Depot",
            distanceMeters = 4400f,
            hillSeverity = "Frozen Mountain Pass",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Heavy Crude Tanker 1", RailCarType.FUEL_TANKER, 1900f, "North Slope Crude"),
                RailCar("c2", "Heavy Crude Tanker 2", RailCarType.FUEL_TANKER, 1900f, "North Slope Crude"),
                RailCar("c3", "Pipe Flatcar", RailCarType.LUMBER_FLATCAR, 1600f, "48-inch Steel Insulated Pipes"),
                RailCar("c4", "Heavy Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1850f, "Bunker Fuel")
            ),
            rewardSilver = 49000,
            rewardGold = 4000,
            rewardDiamonds = 105,
            xpReward = 2050,
            requiredDriverLevel = 13,
            isHighTier = true,
            estimatedMinutes = "2.7 - 3.2 min"
        ),
        ContractJob(
            id = "contract_lvl14_silicon_semiconductor",
            title = "Silicon Coast Cleanroom Transport (4,450 km)",
            originStation = "Silicon Forest Fab",
            destinationStation = "Coastal Export Pier",
            distanceMeters = 4450f,
            hillSeverity = "Smooth Coastal Rails",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Vibration-Damped Boxcar", RailCarType.CONTAINER_BOXCAR, 1450f, "Photolithography EUV Tools"),
                RailCar("c2", "Cryo Gas Tanker", RailCarType.FUEL_TANKER, 1750f, "Ultra-Pure Nitrogen"),
                RailCar("c3", "Vault Boxcar", RailCarType.CONTAINER_BOXCAR, 1550f, "Silicon Wafers"),
                RailCar("c4", "VIP Escort Coach", RailCarType.PASSENGER_COACH, 1100f, "Cleanroom Engineers")
            ),
            rewardSilver = 52000,
            rewardGold = 4250,
            rewardDiamonds = 112,
            xpReward = 2150,
            requiredDriverLevel = 14,
            isHighTier = true,
            estimatedMinutes = "2.7 - 3.2 min"
        ),

        // === LEVEL 15-19 CONTRACTS (HIGH TIER) ===
        ContractJob(
            id = "contract_redwood_coast",
            title = "🔥 Redwood Coastal (4,500 km)",
            originStation = "Eureka Timber Port",
            destinationStation = "San Francisco Yard",
            distanceMeters = 4500f,
            hillSeverity = "Coastal Mountain Ridge",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Timber Flatcar A", RailCarType.LUMBER_FLATCAR, 1300f, "Giant Redwood Trunks"),
                RailCar("c2", "Passenger Coach VIP", RailCarType.PASSENGER_COACH, 1100f, "First Class"),
                RailCar("c3", "Container Boxcar", RailCarType.CONTAINER_BOXCAR, 1400f, "Naval Supplies"),
                RailCar("c4", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Marine Bunker Fuel")
            ),
            rewardSilver = 55000,
            rewardGold = 4500,
            rewardDiamonds = 120,
            xpReward = 2200,
            requiredDriverLevel = 15,
            isHighTier = true,
            estimatedMinutes = "2.5 - 3 min"
        ),
        ContractJob(
            id = "contract_lvl15_gold_bullion",
            title = "🏆 Federal Reserve Gold Bullion Vault (4,700 km)",
            originStation = "Denver Mint Complex",
            destinationStation = "Fort Knox Depository",
            distanceMeters = 4700f,
            hillSeverity = "High Mountain Passes",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Armored Vault Boxcar 1", RailCarType.CONTAINER_BOXCAR, 2400f, "99.99% Gold Bullion Bars"),
                RailCar("c2", "Armored Vault Boxcar 2", RailCarType.CONTAINER_BOXCAR, 2400f, "Silver Ingot Reserves"),
                RailCar("c3", "Security Garrison Coach", RailCarType.PASSENGER_COACH, 1300f, "Armed Secret Service Unit"),
                RailCar("c4", "Fuel Tanker", RailCarType.FUEL_TANKER, 1700f, "Emergency Generator Fuel")
            ),
            rewardSilver = 68000,
            rewardGold = 5500,
            rewardDiamonds = 145,
            xpReward = 2600,
            requiredDriverLevel = 15,
            isHighTier = true,
            estimatedMinutes = "2.8 - 3.3 min"
        ),
        ContractJob(
            id = "contract_lvl16_superconducting_maglev",
            title = "⚡ Superconducting Cryo-Freight (4,900 km)",
            originStation = "Quantum Research Lab",
            destinationStation = "Hyperloop Test Facility",
            distanceMeters = 4900f,
            hillSeverity = "Smooth High-Speed Line",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Cryo Tanker Alpha", RailCarType.FUEL_TANKER, 1850f, "Liquid Helium (4 Kelvin)"),
                RailCar("c2", "Cryo Tanker Beta", RailCarType.FUEL_TANKER, 1850f, "Liquid Hydrogen"),
                RailCar("c3", "Superconducting Magnet Car", RailCarType.CONTAINER_BOXCAR, 1900f, "Niobium-Titanium Coils"),
                RailCar("c4", "Observation Coach", RailCarType.PASSENGER_COACH, 1200f, "Lead Physicists")
            ),
            rewardSilver = 78000,
            rewardGold = 6400,
            rewardDiamonds = 170,
            xpReward = 3100,
            requiredDriverLevel = 16,
            isHighTier = true,
            estimatedMinutes = "2.9 - 3.4 min"
        ),
        ContractJob(
            id = "contract_lvl17_canyon_iron_ore",
            title = "💥 Grand Canyon Mega Unit Train (5,100 km)",
            originStation = "Pilbara Mining Pit",
            destinationStation = "Deepwater Export Pier",
            distanceMeters = 5100f,
            hillSeverity = "Continuous Canyon Slopes",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Rotary Dump Coal Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 2200f, "Magnetite Ore"),
                RailCar("c2", "Rotary Dump Coal Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 2200f, "Hematite Ore"),
                RailCar("c3", "Rotary Dump Coal Hopper 3", RailCarType.HEAVY_COAL_HOPPER, 2200f, "Raw Bauxite"),
                RailCar("c4", "Fuel Tanker", RailCarType.FUEL_TANKER, 1800f, "Locomotive Refueling Unit"),
                RailCar("c5", "Crew Caboose", RailCarType.PASSENGER_COACH, 1100f, "Rail Engineers")
            ),
            rewardSilver = 90000,
            rewardGold = 7400,
            rewardDiamonds = 200,
            xpReward = 3600,
            requiredDriverLevel = 17,
            isHighTier = true,
            estimatedMinutes = "3 - 3.5 min"
        ),
        ContractJob(
            id = "contract_lvl18_trans_andes_lithium",
            title = "🏔️ Trans-Andean High Altitude Haul (5,300 km)",
            originStation = "Atacama Salt Flat Yard",
            destinationStation = "Pacific Deep Harbor",
            distanceMeters = 5300f,
            hillSeverity = "Extreme Alpine Mountain Grade",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Heavy Mineral Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 2100f, "Pure Lithium Hydroxide"),
                RailCar("c2", "Heavy Mineral Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 2100f, "Potassium Nitrate"),
                RailCar("c3", "Heavy Mineral Hopper 3", RailCarType.HEAVY_COAL_HOPPER, 2100f, "Cobalt Concentrate"),
                RailCar("c4", "Oxygenated Fuel Tanker", RailCarType.FUEL_TANKER, 1850f, "High-Altitude Jet Fuel"),
                RailCar("c5", "High-Altitude Passenger Coach", RailCarType.PASSENGER_COACH, 1250f, "Mining Specialists")
            ),
            rewardSilver = 105000,
            rewardGold = 8600,
            rewardDiamonds = 240,
            xpReward = 4200,
            requiredDriverLevel = 18,
            isHighTier = true,
            estimatedMinutes = "3.1 - 3.6 min"
        ),
        ContractJob(
            id = "contract_lvl19_deep_bore_arctic",
            title = "❄️ Aurora Deep-Bore Geothermal Drill (5,450 km)",
            originStation = "Borehole Platform 9",
            destinationStation = "Arctic Geo-Plant Central",
            distanceMeters = 5450f,
            hillSeverity = "Glacier Ice Ridges",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Heavy Machinery Flatcar", RailCarType.LUMBER_FLATCAR, 2300f, "Diamond Drill Bit Assembly"),
                RailCar("c2", "Cryo Tanker Alpha", RailCarType.FUEL_TANKER, 1850f, "Deuterium Coolant"),
                RailCar("c3", "Cryo Tanker Beta", RailCarType.FUEL_TANKER, 1850f, "Liquid Argon"),
                RailCar("c4", "Armored Generator Car", RailCarType.CONTAINER_BOXCAR, 1950f, "Geothermal Turbine Core"),
                RailCar("c5", "Expedition Survival Coach", RailCarType.PASSENGER_COACH, 1300f, "Polar Drill Crew")
            ),
            rewardSilver = 118000,
            rewardGold = 9500,
            rewardDiamonds = 275,
            xpReward = 4700,
            requiredDriverLevel = 19,
            isHighTier = true,
            estimatedMinutes = "3.2 - 3.7 min"
        ),

        // === LEVEL 20 ULTRA TITAN CONTRACTS (INCLUDING THE REQUESTED NUCLEAR CONTRACT) ===
        // ⭐ REQUESTED HIGH-PAYING NUCLEAR CONTRACT ⭐
        ContractJob(
            id = "contract_classified_nuclear_core",
            title = "☢️ ULTRA-TITAN: Classified Nuclear Reactor Core & Enriched Uranium (7,500 km)",
            originStation = "Oak Ridge Nuclear Complex",
            destinationStation = "Yucca Deep Geologic Vault",
            distanceMeters = 7500f,
            hillSeverity = "High-Security Mountain Gradient",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Lead-Lined Reactor Cask Car", RailCarType.HEAVY_COAL_HOPPER, 2800f, "Castor Spent Fuel Reactor Cask"),
                RailCar("c2", "Heavy Isotope Vault Car", RailCarType.CONTAINER_BOXCAR, 2600f, "Enriched Uranium-235 Fuel Rods"),
                RailCar("c3", "Heavy Water Cryo Tanker", RailCarType.FUEL_TANKER, 2200f, "Heavy Water Coolant (D2O)"),
                RailCar("c4", "Radiation Scrubbing Boxcar", RailCarType.CONTAINER_BOXCAR, 2100f, "Boron Shielding & Neutralizers"),
                RailCar("c5", "Armed Military Escort Coach", RailCarType.PASSENGER_COACH, 1600f, "Elite Nuclear Security Team")
            ),
            rewardSilver = 650000, // MASSIVE NUCLEAR PAYOUT!
            rewardGold = 45000,
            rewardDiamonds = 1500,
            xpReward = 18000,
            requiredDriverLevel = 20,
            isHighTier = true,
            estimatedMinutes = "4.2 - 5 min"
        ),
        ContractJob(
            id = "contract_level20_titan",
            title = "⚡ LVL 20 TITAN: Trans-Continental (5,600 km)",
            originStation = "Atlantic Seaboard Yard",
            destinationStation = "Pacific Golden Terminal",
            distanceMeters = 5600f,
            hillSeverity = "High Mountain Smooth Curves",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Heavy Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 2000f, "Pure Anthracite"),
                RailCar("c2", "Petroleum Tanker", RailCarType.FUEL_TANKER, 1700f, "Rocket Fuel Oxidizer"),
                RailCar("c3", "Container Boxcar", RailCarType.CONTAINER_BOXCAR, 1500f, "Diamond Electronics"),
                RailCar("c4", "Timber Flatcar", RailCarType.LUMBER_FLATCAR, 1400f, "Sequoia Timber"),
                RailCar("c5", "VIP Luxury Coach", RailCarType.PASSENGER_COACH, 1200f, "Railroad Board of Directors")
            ),
            rewardSilver = 125000,
            rewardGold = 10000,
            rewardDiamonds = 300,
            xpReward = 5000,
            requiredDriverLevel = 20,
            isHighTier = true,
            estimatedMinutes = "3 - 3.5 min"
        ),
        ContractJob(
            id = "contract_level20_arctic",
            title = "❄️ LVL 20 TITAN: Arctic Aurora Frost (6,200 km)",
            originStation = "Subzero Terminal",
            destinationStation = "Northern Citadel Station",
            distanceMeters = 6200f,
            hillSeverity = "Glacier Ice Ridges",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Cryo Tanker A", RailCarType.FUEL_TANKER, 1700f, "Liquid Nitrogen"),
                RailCar("c2", "Cryo Tanker B", RailCarType.FUEL_TANKER, 1700f, "Liquid Methane"),
                RailCar("c3", "Armored Container", RailCarType.CONTAINER_BOXCAR, 1600f, "Isotope Generators"),
                RailCar("c4", "Heavy Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1900f, "Thermal Fuel")
            ),
            rewardSilver = 145000,
            rewardGold = 12000,
            rewardDiamonds = 350,
            xpReward = 5500,
            requiredDriverLevel = 20,
            isHighTier = true,
            estimatedMinutes = "3.5 - 4 min"
        ),
        ContractJob(
            id = "contract_level20_transcontinental",
            title = "🏆 LVL 20 TITAN: Apex Siberian Line (6,800 km)",
            originStation = "Baikal Fortress",
            destinationStation = "Vladivostok Pacific Gateway",
            distanceMeters = 6800f,
            hillSeverity = "Extreme Alpine Crests",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Heavy Coal Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 2200f, "Titanium Ore"),
                RailCar("c2", "Heavy Coal Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 2200f, "Rare Earth Metals"),
                RailCar("c3", "Armored Vault Boxcar", RailCarType.CONTAINER_BOXCAR, 1800f, "Bullion Reserve"),
                RailCar("c4", "Cryo Tanker", RailCarType.FUEL_TANKER, 1800f, "Deuterium Coolant"),
                RailCar("c5", "VIP Observation Car", RailCarType.PASSENGER_COACH, 1300f, "Chief Engineers")
            ),
            rewardSilver = 180000,
            rewardGold = 15000,
            rewardDiamonds = 450,
            xpReward = 6500,
            requiredDriverLevel = 20,
            isHighTier = true,
            estimatedMinutes = "3.8 - 4.2 min"
        ),

        // === ADDITIONAL DIVERSE GLOBAL CONTRACTS (22 MORE TO REACH FULL 51 CONTRACTS) ===
        ContractJob(
            id = "contract_extra_orient_express",
            title = "🌟 Orient Express Royal Champagne (5,000 km)",
            originStation = "Paris Gare de l'Est",
            destinationStation = "Istanbul Sirkeci",
            distanceMeters = 5000f,
            hillSeverity = "Continental Scenic Gradients",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Grand Suite Pullman", RailCarType.PASSENGER_COACH, 1300f, "Royal Ambassadors"),
                RailCar("c2", "Dining Salon Car", RailCarType.PASSENGER_COACH, 1300f, "Vintage Vintage Champagne"),
                RailCar("c3", "Baggage & Vault Car", RailCarType.CONTAINER_BOXCAR, 1500f, "Diplomatic Pouches")
            ),
            rewardSilver = 85000,
            rewardGold = 7000,
            rewardDiamonds = 190,
            xpReward = 3400,
            requiredDriverLevel = 16,
            isHighTier = true,
            estimatedMinutes = "3 - 3.4 min"
        ),
        ContractJob(
            id = "contract_extra_mojave_sandstorm",
            title = "🌪️ Mojave Sandstorm Emergency Sand Supply (3,200 km)",
            originStation = "Barstow Hub",
            destinationStation = "Needles Depot",
            distanceMeters = 3200f,
            hillSeverity = "Desert Rolling Dunes",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Sand Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 1750f, "Locomotive Traction Sand"),
                RailCar("c2", "Sand Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 1750f, "Locomotive Traction Sand"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Emergency Fuel")
            ),
            rewardSilver = 15000,
            rewardGold = 1150,
            rewardDiamonds = 36,
            xpReward = 700,
            requiredDriverLevel = 5,
            isHighTier = false,
            estimatedMinutes = "1.9 - 2.4 min"
        ),
        ContractJob(
            id = "contract_extra_pacific_grain",
            title = "🌾 Great Plains Golden Wheat Unit (3,500 km)",
            originStation = "Kansas City Elevator",
            destinationStation = "Houston Grain Port",
            distanceMeters = 3500f,
            hillSeverity = "Prairie Rolling Slopes",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Grain Covered Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 1800f, "Durum Wheat"),
                RailCar("c2", "Grain Covered Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 1800f, "Yellow Corn"),
                RailCar("c3", "Grain Covered Hopper 3", RailCarType.HEAVY_COAL_HOPPER, 1800f, "Barley Malt")
            ),
            rewardSilver = 17500,
            rewardGold = 1350,
            rewardDiamonds = 42,
            xpReward = 820,
            requiredDriverLevel = 6,
            isHighTier = false,
            estimatedMinutes = "2 - 2.5 min"
        ),
        ContractJob(
            id = "contract_extra_silicon_wafer_express",
            title = "💾 Semiconductor Mega Fab Nitrogen Shuttle (2,900 km)",
            originStation = "Phoenix Fab 52",
            destinationStation = "Chandler Testing Facility",
            distanceMeters = 2900f,
            hillSeverity = "Desert Valley",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Liquid Nitrogen Cryo Car", RailCarType.FUEL_TANKER, 1700f, "Liquid Nitrogen"),
                RailCar("c2", "Cleanroom Container", RailCarType.CONTAINER_BOXCAR, 1400f, "Monocrystalline Silicon")
            ),
            rewardSilver = 11000,
            rewardGold = 850,
            rewardDiamonds = 28,
            xpReward = 520,
            requiredDriverLevel = 3,
            isHighTier = false,
            estimatedMinutes = "1.6 - 2 min"
        ),
        ContractJob(
            id = "contract_extra_norwegian_fjord",
            title = "🇳🇴 Flåm Mountain Hydroelectric Turbine (4,000 km)",
            originStation = "Myrdal High Siding",
            destinationStation = "Flåm Fjord Harbor",
            distanceMeters = 4000f,
            hillSeverity = "Steep Alpine Ravines",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Heavy Turbine Flatcar", RailCarType.LUMBER_FLATCAR, 2100f, "Francis Hydro Turbine Runner"),
                RailCar("c2", "Copper Transformer Car", RailCarType.CONTAINER_BOXCAR, 1600f, "Substation Coils"),
                RailCar("c3", "Insulating Oil Tanker", RailCarType.FUEL_TANKER, 1650f, "Silicone Coolant")
            ),
            rewardSilver = 35000,
            rewardGold = 2800,
            rewardDiamonds = 75,
            xpReward = 1500,
            requiredDriverLevel = 10,
            isHighTier = false,
            estimatedMinutes = "2.5 - 3 min"
        ),
        ContractJob(
            id = "contract_extra_matterhorn_express",
            title = "🏔️ Glacier Express Deluxe Panorama (4,600 km)",
            originStation = "Zermatt Terminal",
            destinationStation = "St. Moritz Grand Depot",
            distanceMeters = 4600f,
            hillSeverity = "Alpine Spiral Curves",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Panorama Coach 1", RailCarType.PASSENGER_COACH, 1150f, "First Class"),
                RailCar("c2", "Panorama Coach 2", RailCarType.PASSENGER_COACH, 1150f, "Excellence Class"),
                RailCar("c3", "Service Gourmet Car", RailCarType.PASSENGER_COACH, 1200f, "Fine Dining Supplies"),
                RailCar("c4", "Luggage Vault Car", RailCarType.CONTAINER_BOXCAR, 1400f, "Ski Equipment")
            ),
            rewardSilver = 60000,
            rewardGold = 4900,
            rewardDiamonds = 130,
            xpReward = 2400,
            requiredDriverLevel = 15,
            isHighTier = true,
            estimatedMinutes = "2.7 - 3.2 min"
        ),
        ContractJob(
            id = "contract_extra_ruhr_coke",
            title = "🏭 Ruhr Valley Heavy Blast Coking (3,650 km)",
            originStation = "Dortmund Coking Works",
            destinationStation = "Duisburg Inland Port",
            distanceMeters = 3650f,
            hillSeverity = "Industrial Valley Curves",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Heavy Coal Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 1950f, "Foundry Coke"),
                RailCar("c2", "Heavy Coal Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 1950f, "Limestone Flux"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Coal Tar Byproducts")
            ),
            rewardSilver = 19500,
            rewardGold = 1550,
            rewardDiamonds = 48,
            xpReward = 920,
            requiredDriverLevel = 6,
            isHighTier = false,
            estimatedMinutes = "2.1 - 2.6 min"
        ),
        ContractJob(
            id = "contract_extra_chicago_meatpack",
            title = "🥩 Midwest Refrigerated Cold Express (3,750 km)",
            originStation = "Omaha Processing",
            destinationStation = "Chicago Union Stockyard",
            distanceMeters = 3750f,
            hillSeverity = "Rolling Prairie Tracks",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Reefer Boxcar 1", RailCarType.CONTAINER_BOXCAR, 1600f, "Cryo Frozen Provisions"),
                RailCar("c2", "Reefer Boxcar 2", RailCarType.CONTAINER_BOXCAR, 1600f, "Prime Beef"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1550f, "Refrigeration Propane")
            ),
            rewardSilver = 22000,
            rewardGold = 1750,
            rewardDiamonds = 52,
            xpReward = 1000,
            requiredDriverLevel = 7,
            isHighTier = false,
            estimatedMinutes = "2.2 - 2.7 min"
        ),
        ContractJob(
            id = "contract_extra_detroit_auto",
            title = "🚗 Motor City Triple-Deck Autorack (4,300 km)",
            originStation = "Dearborn Assembly",
            destinationStation = "Pacific Distribution Hub",
            distanceMeters = 4300f,
            hillSeverity = "Valley Straight Tracks",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Enclosed Autorack 1", RailCarType.CONTAINER_BOXCAR, 1750f, "Electric SUVs"),
                RailCar("c2", "Enclosed Autorack 2", RailCarType.CONTAINER_BOXCAR, 1750f, "Heavy Duty Pickups"),
                RailCar("c3", "Enclosed Autorack 3", RailCarType.CONTAINER_BOXCAR, 1750f, "Sports Coupes"),
                RailCar("c4", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Brake Fluid")
            ),
            rewardSilver = 44000,
            rewardGold = 3500,
            rewardDiamonds = 92,
            xpReward = 1850,
            requiredDriverLevel = 12,
            isHighTier = true,
            estimatedMinutes = "2.6 - 3.1 min"
        ),
        ContractJob(
            id = "contract_extra_pacific_salmon",
            title = "🐟 Alaska Salmon & Fishery Express (3,300 km)",
            originStation = "Ketchikan Harbor",
            destinationStation = "Seattle Cold Pier",
            distanceMeters = 3300f,
            hillSeverity = "Coastal Redwood Pass",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Reefer Boxcar", RailCarType.CONTAINER_BOXCAR, 1500f, "Flash-Frozen King Salmon"),
                RailCar("c2", "Fuel Tanker", RailCarType.FUEL_TANKER, 1600f, "Fleet Diesel"),
                RailCar("c3", "Timber Flatcar", RailCarType.LUMBER_FLATCAR, 1300f, "Smoked Alderwood")
            ),
            rewardSilver = 15500,
            rewardGold = 1200,
            rewardDiamonds = 38,
            xpReward = 720,
            requiredDriverLevel = 5,
            isHighTier = false,
            estimatedMinutes = "1.9 - 2.4 min"
        ),
        ContractJob(
            id = "contract_extra_mendo_wine",
            title = "🍷 Napa & Sonoma Valley Vintage Express (3,550 km)",
            originStation = "Napa Siding",
            destinationStation = "San Francisco Port Vault",
            distanceMeters = 3550f,
            hillSeverity = "Coastal Foothills",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Insulated Wine Boxcar", RailCarType.CONTAINER_BOXCAR, 1400f, "Cabernet Sauvignon Barrels"),
                RailCar("c2", "Passenger Parlor Car", RailCarType.PASSENGER_COACH, 1150f, "Sommelier Tour"),
                RailCar("c3", "Flatcar Timber", RailCarType.LUMBER_FLATCAR, 1300f, "French Oak Staves")
            ),
            rewardSilver = 17800,
            rewardGold = 1380,
            rewardDiamonds = 44,
            xpReward = 840,
            requiredDriverLevel = 6,
            isHighTier = false,
            estimatedMinutes = "2 - 2.5 min"
        ),
        ContractJob(
            id = "contract_extra_big_sur_tourist",
            title = "🌊 Big Sur Coastal Starlight (4,250 km)",
            originStation = "Monterey Depot",
            destinationStation = "Santa Barbara Siding",
            distanceMeters = 4250f,
            hillSeverity = "Coastal Cliff Grades",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("c1", "Superliner Coach 1", RailCarType.PASSENGER_COACH, 1200f, "Pacific Travelers"),
                RailCar("c2", "Superliner Sightseer Lounge", RailCarType.PASSENGER_COACH, 1250f, "Observation Deck"),
                RailCar("c3", "Superliner Dining Car", RailCarType.PASSENGER_COACH, 1200f, "Coastal Cuisine"),
                RailCar("c4", "Baggage Express Car", RailCarType.CONTAINER_BOXCAR, 1400f, "Express Mail")
            ),
            rewardSilver = 42000,
            rewardGold = 3400,
            rewardDiamonds = 90,
            xpReward = 1800,
            requiredDriverLevel = 11,
            isHighTier = true,
            estimatedMinutes = "2.6 - 3.1 min"
        ),
        ContractJob(
            id = "contract_extra_svalbard_seed",
            title = "🌱 Svalbard Global Seed Vault Safeguard (5,200 km)",
            originStation = "Longyearbyen Port",
            destinationStation = "Seed Vault High Mountain Terminal",
            distanceMeters = 5200f,
            hillSeverity = "Subzero Glacial Alpine Grade",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Thermal Sealed Boxcar 1", RailCarType.CONTAINER_BOXCAR, 1750f, "Cryopreserved Crop Seeds"),
                RailCar("c2", "Thermal Sealed Boxcar 2", RailCarType.CONTAINER_BOXCAR, 1750f, "Biodiversity Gene Banks"),
                RailCar("c3", "Liquid Nitrogen Tanker", RailCarType.FUEL_TANKER, 1800f, "Zero-Degree Nitrogen Supply"),
                RailCar("c4", "Armed Security Escort Car", RailCarType.PASSENGER_COACH, 1250f, "UN Agricultural Guard")
            ),
            rewardSilver = 96000,
            rewardGold = 7800,
            rewardDiamonds = 215,
            xpReward = 3850,
            requiredDriverLevel = 17,
            isHighTier = true,
            estimatedMinutes = "3 - 3.5 min"
        ),
        ContractJob(
            id = "contract_extra_murmansk_nickel",
            title = "⛏️ Kola Peninsula Superdeep Heavy Nickel (4,800 km)",
            originStation = "Nikel Mining Complex",
            destinationStation = "Murmansk Ice-Free Harbor",
            distanceMeters = 4800f,
            hillSeverity = "Tundra Rocky Slopes",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("c1", "Heavy Ore Hopper 1", RailCarType.HEAVY_COAL_HOPPER, 2150f, "Refined Nickel Briquettes"),
                RailCar("c2", "Heavy Ore Hopper 2", RailCarType.HEAVY_COAL_HOPPER, 2150f, "Platinum-Group Concentrate"),
                RailCar("c3", "Fuel Tanker", RailCarType.FUEL_TANKER, 1800f, "Arctic Anti-Freeze Diesel"),
                RailCar("c4", "Heavy Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1950f, "Foundry Anthracite")
            ),
            rewardSilver = 72000,
            rewardGold = 5900,
            rewardDiamonds = 155,
            xpReward = 2800,
            requiredDriverLevel = 16,
            isHighTier = true,
            estimatedMinutes = "2.8 - 3.3 min"
        ),
        ContractJob(
            id = "contract_extra_shinkansen_fastmail",
            title = "🚅 Tokaido Bullet Fast Freight & Mail (4,750 km)",
            originStation = "Tokyo Freight Terminal",
            destinationStation = "Shin-Osaka Express Siding",
            distanceMeters = 4750f,
            hillSeverity = "High-Speed Viaduct Lines",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("c1", "Aerodynamic Cargo Car 1", RailCarType.CONTAINER_BOXCAR, 1500f, "Next-Day Priority Parcels"),
                RailCar("c2", "Aerodynamic Cargo Car 2", RailCarType.CONTAINER_BOXCAR, 1500f, "Semiconductor Components"),
                RailCar("c3", "Aerodynamic Cargo Car 3", RailCarType.CONTAINER_BOXCAR, 1500f, "Medical Radioisotopes"),
                RailCar("c4", "High-Speed Pantograph Coach", RailCarType.PASSENGER_COACH, 1200f, "Railway Inspection Crew")
            ),
            rewardSilver = 70000,
            rewardGold = 5700,
            rewardDiamonds = 150,
            xpReward = 2700,
            requiredDriverLevel = 15,
            isHighTier = true,
            estimatedMinutes = "2.8 - 3.3 min"
        ),
        ContractJob(
            id = "contract_extra_australian_iron_ore",
            title = "🇦🇺 Pilbara Heavyweight Ore Train (5,500 km)",
            originStation = "Mount Whaleback Mine",
            destinationStation = "Port Hedland Dumper",
            distanceMeters = 5500f,
            hillSeverity = "Outback Heavy Rolling Terrain",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("c1", "Heavy Gondola 1", RailCarType.HEAVY_COAL_HOPPER, 2300f, "Direct Shipping Iron Ore"),
                RailCar("c2", "Heavy Gondola 2", RailCarType.HEAVY_COAL_HOPPER, 2300f, "Hematite Lumps"),
                RailCar("c3", "Heavy Gondola 3", RailCarType.HEAVY_COAL_HOPPER, 2300f, "Iron Ore Fines"),
                RailCar("c4", "Fuel Tanker", RailCarType.FUEL_TANKER, 1850f, "Heavy Haul Diesel"),
                RailCar("c5", "Crew Caboose", RailCarType.PASSENGER_COACH, 1200f, "Outback Conductor Crew")
            ),
            rewardSilver = 120000,
            rewardGold = 9800,
            rewardDiamonds = 285,
            xpReward = 4850,
            requiredDriverLevel = 19,
            isHighTier = true,
            estimatedMinutes = "3.2 - 3.7 min"
        ),
        ContractJob(
            id = "contract_extra_matterhorn_express_gold",
            title = "👑 Royal Swiss Bank Diamond Reserve (5,800 km)",
            originStation = "Zurich Vault Depot",
            destinationStation = "Geneva Diplomatic Port",
            distanceMeters = 5800f,
            hillSeverity = "Smooth Alpine Gradients",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("c1", "Armored Vault Car A", RailCarType.CONTAINER_BOXCAR, 2500f, "Certified Cut Diamonds"),
                RailCar("c2", "Armored Vault Car B", RailCarType.CONTAINER_BOXCAR, 2500f, "Sovereign Gold Reserves"),
                RailCar("c3", "Swiss Security Garrison", RailCarType.PASSENGER_COACH, 1400f, "Elite Vault Sentinels"),
                RailCar("c4", "Cryo Tanker", RailCarType.FUEL_TANKER, 1800f, "Transformer Coolant"),
                RailCar("c5", "VIP Inspection Car", RailCarType.PASSENGER_COACH, 1300f, "Federal Reserve Governors")
            ),
            rewardSilver = 140000,
            rewardGold = 11500,
            rewardDiamonds = 340,
            xpReward = 5400,
            requiredDriverLevel = 20,
            isHighTier = true,
            estimatedMinutes = "3.4 - 3.9 min"
        ),

        // === DEDICATED SHINKANSEN E2 SUPERSONIC CONTRACTS ===
        ContractJob(
            id = "contract_shinkansen_tohokusuper",
            title = "🚅 Tohoku Supersonic Shinkansen Line (6,500 km)",
            originStation = "Tokyo Central Terminal",
            destinationStation = "Shin-Aomori Super Depot",
            distanceMeters = 6500f,
            hillSeverity = "High-Speed Viaducts & Mountain Passes",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("shinkansen_c2", "E2-1000 Passenger Coach 2", RailCarType.SHINKANSEN_E2_COACH, 1100f, "Tokyo Express Commuters"),
                RailCar("shinkansen_c4", "E2-1000 Pantograph Car 4", RailCarType.SHINKANSEN_E2_COACH, 1150f, "Aerofoil Power Link"),
                RailCar("shinkansen_c6", "E2-1000 Pantograph Car 6", RailCarType.SHINKANSEN_E2_COACH, 1150f, "Aerofoil Power Link"),
                RailCar("shinkansen_c9", "E2-1000 First Class Green Car", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Green Car VIP Passengers"),
                RailCar("shinkansen_c10", "E2-1000 Trailing Bullet Coach", RailCarType.SHINKANSEN_E2_COACH, 1100f, "Tohoku Business Travelers")
            ),
            rewardSilver = 180000,
            rewardGold = 15000,
            rewardDiamonds = 450,
            xpReward = 6500,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.5 - 3.0 min"
        ),
        ContractJob(
            id = "contract_shinkansen_apexspeed",
            title = "⚡ Apex Speed Record Challenge: 350+ km/h (4,800 km)",
            originStation = "Morioka High-Speed Siding",
            destinationStation = "Hachinohe Express Junction",
            distanceMeters = 4800f,
            hillSeverity = "Straight Supersonic Corridor",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("shinkansen_c2", "E2-1000 Streamlined Coach", RailCarType.SHINKANSEN_E2_COACH, 1050f, "Speed Record Telemetry Crew"),
                RailCar("shinkansen_c4", "E2-1000 Pantograph Unit", RailCarType.SHINKANSEN_E2_COACH, 1100f, "High-Voltage Telemetry"),
                RailCar("shinkansen_c9", "E2-1000 First Class Green Car", RailCarType.SHINKANSEN_E2_COACH, 1150f, "JR Speed Record Observers")
            ),
            rewardSilver = 160000,
            rewardGold = 13500,
            rewardDiamonds = 400,
            xpReward = 5800,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "1.8 - 2.4 min"
        ),
        ContractJob(
            id = "contract_shinkansen_winterexpress",
            title = "❄️ Grand Hayate Winter Express (5,200 km)",
            originStation = "Sendai Station Terminal",
            destinationStation = "Morioka Snow Terminal",
            distanceMeters = 5200f,
            hillSeverity = "Sub-zero Snowdrifts & Viaducts",
            environment = EnvironmentType.ARCTIC_PASS,
            railCars = listOf(
                RailCar("shinkansen_c2", "E2-1000 Heated Coach A", RailCarType.SHINKANSEN_E2_COACH, 1100f, "Snow Country Travelers"),
                RailCar("shinkansen_c4", "E2-1000 Pantograph Car", RailCarType.SHINKANSEN_E2_COACH, 1150f, "De-icing Aerofoil Pantograph"),
                RailCar("shinkansen_c7", "E2-1000 Heated Coach B", RailCarType.SHINKANSEN_E2_COACH, 1100f, "Tohoku Hot Spring Tourists"),
                RailCar("shinkansen_c9", "E2-1000 First Class Green Car", RailCarType.SHINKANSEN_E2_COACH, 1200f, "First Class VIP Lounge")
            ),
            rewardSilver = 175000,
            rewardGold = 14200,
            rewardDiamonds = 420,
            xpReward = 6200,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.0 - 2.6 min"
        ),

        // === DEDICATED CLASS 390 PENDOLINO CONTRACTS (INCLUDING BACKWARDS TRAIN CAB) ===
        ContractJob(
            id = "contract_pendolino_westcoast_express",
            title = "🇬🇧 Class 390 West Coast High-Speed Express (6,200 km)",
            originStation = "London Euston Central",
            destinationStation = "Glasgow Central Terminal",
            distanceMeters = 6200f,
            hillSeverity = "Tilting High-Speed Curvature & Viaducts",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("pend_c1", "Class 390 Standard Coach #1", RailCarType.PENDOLINO_COACH, 1100f, "InterCity Travelers"),
                RailCar("pend_c2", "Class 390 Quiet Zone Coach #2", RailCarType.PENDOLINO_COACH, 1100f, "Commuters & Wifi Lounge"),
                RailCar("pend_c3", "Class 390 First Class Buffet Coach", RailCarType.PENDOLINO_COACH, 1150f, "First Class Dining & Cuisine"),
                RailCar("pend_c4", "Class 390 First Class Coach #4", RailCarType.PENDOLINO_COACH, 1100f, "VIP Passengers"),
                RailCar("pend_cab", "Class 390 Trailing Backward Cab Unit", RailCarType.PENDOLINO_REAR_CAB, 1200f, "Aerodynamic Rear Cab")
            ),
            rewardSilver = 190000,
            rewardGold = 16000,
            rewardDiamonds = 480,
            xpReward = 6800,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.2 - 2.8 min"
        ),
        ContractJob(
            id = "contract_pendolino_heavy_rescue_tow",
            title = "🚨 Heavy Breakdown Rail Rescue: Stranded Pendolino (5,800 km)",
            originStation = "Carlisle Marshalling Yard",
            destinationStation = "Crewe Heavy Maintenance Depot",
            distanceMeters = 5800f,
            hillSeverity = "Mainline Rescue Tow Corridor",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("pend_c1", "Class 390 Standard Coach #1", RailCarType.PENDOLINO_COACH, 1100f, "Evacuated Passenger Unit"),
                RailCar("pend_c2", "Class 390 Coach #2", RailCarType.PENDOLINO_COACH, 1100f, "Tilting Bogie Telemetry"),
                RailCar("pend_c3", "Class 390 First Class Coach", RailCarType.PENDOLINO_COACH, 1150f, "Inspection Equipment"),
                RailCar("pend_cab", "Class 390 Trailing Backward Cab Unit", RailCarType.PENDOLINO_REAR_CAB, 1200f, "Aerodynamic Rear Cab")
            ),
            rewardSilver = 185000,
            rewardGold = 15500,
            rewardDiamonds = 460,
            xpReward = 6600,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.0 - 2.5 min"
        ),

        // === DEDICATED METRA COMMUTER RAIL CONTRACTS (WITH DEDICATED BI-LEVEL GALLERY CARS) ===
        ContractJob(
            id = "contract_metra_bnsf_aurora_express",
            title = "🚆 Metra BNSF Line Rush Hour Express (4,400 km)",
            originStation = "Chicago Union Station",
            destinationStation = "Aurora Transportation Center",
            distanceMeters = 4400f,
            hillSeverity = "3-Track High-Density Commuter Corridor",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("metra_c1", "Metra Bi-Level Gallery Coach #1", RailCarType.METRA_GALLERY_COACH, 1200f, "Express Commuters"),
                RailCar("metra_c2", "Metra Bi-Level Gallery Coach #2", RailCarType.METRA_GALLERY_COACH, 1200f, "Commuter Passenger Deck"),
                RailCar("metra_c3", "Metra Bi-Level Quiet Car #3", RailCarType.METRA_GALLERY_COACH, 1200f, "Quiet Car Passengers"),
                RailCar("metra_c4", "Metra Bi-Level Gallery Cab Car #4", RailCarType.METRA_GALLERY_COACH, 1250f, "Trailing Gallery Cab Control")
            ),
            rewardSilver = 95000,
            rewardGold = 8000,
            rewardDiamonds = 240,
            xpReward = 3800,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.3 - 2.8 min"
        ),
        ContractJob(
            id = "contract_metra_electric_highliner_run",
            title = "⚡ Metra Electric District Fast Commuter (4,100 km)",
            originStation = "Millennium Station Terminal",
            destinationStation = "University Park Depot",
            distanceMeters = 4100f,
            hillSeverity = "1500V DC Overhead Catenary Suburb Line",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("metra_c1", "Highliner II EMU Trailing Unit #1", RailCarType.METRA_GALLERY_COACH, 1250f, "Lakefront Commuters"),
                RailCar("metra_c2", "Highliner II EMU Trailing Unit #2", RailCarType.METRA_GALLERY_COACH, 1250f, "South Shore Travelers"),
                RailCar("metra_c3", "Highliner II EMU Trailing Unit #3", RailCarType.METRA_GALLERY_COACH, 1250f, "Hyde Park Express Passengers"),
                RailCar("metra_c4", "Highliner II EMU Trailing Unit #4", RailCarType.METRA_GALLERY_COACH, 1250f, "Rear Catenary Control Car")
            ),
            rewardSilver = 88000,
            rewardGold = 7400,
            rewardDiamonds = 220,
            xpReward = 3500,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.1 - 2.6 min"
        ),
        ContractJob(
            id = "contract_metra_milwaukee_west_limited",
            title = "🏙️ Metra Milwaukee District West Limited (4,600 km)",
            originStation = "Chicago Western Avenue",
            destinationStation = "Big Timber Elgin Terminal",
            distanceMeters = 4600f,
            hillSeverity = "Fox River Valley Prairie Curves",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("metra_c1", "Metra Bi-Level Gallery Coach #1", RailCarType.METRA_GALLERY_COACH, 1200f, "Fox River Commuters"),
                RailCar("metra_c2", "Metra Bi-Level Gallery Coach #2", RailCarType.METRA_GALLERY_COACH, 1200f, "Schaumburg Travelers"),
                RailCar("metra_c3", "Metra Bi-Level Gallery Coach #3", RailCarType.METRA_GALLERY_COACH, 1200f, "Suburban Commuters"),
                RailCar("metra_c4", "Metra Bi-Level Gallery Cab Car #4", RailCarType.METRA_GALLERY_COACH, 1250f, "Gallery Push-Pull Cab")
            ),
            rewardSilver = 105000,
            rewardGold = 8800,
            rewardDiamonds = 260,
            xpReward = 4200,
            requiredDriverLevel = 1,
            isHighTier = true,
            estimatedMinutes = "2.4 - 2.9 min"
        ),

        // === DEDICATED DIESEL FREIGHT CARGO CONTRACTS (CSX, BNSF, BN GREEN, BN ORANGE, SANTA FE, UP, NS) ===
        ContractJob(
            id = "contract_freight_csx_darkfuture_coal",
            title = "🔷 CSX Appalachian Heavy Coal Unit Train (5,000 km)",
            originStation = "Cumberland Coal Siding",
            destinationStation = "Baltimore Curtis Bay Piers",
            distanceMeters = 5000f,
            hillSeverity = "Allegheny Mountain Heavy Drag",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("csx_c1", "CSX Dark Future Coal Hopper #1", RailCarType.CSX_COAL_HOPPER, 2200f, "Metallurgical Anthracite"),
                RailCar("csx_c2", "CSX Dark Future Coal Hopper #2", RailCarType.CSX_COAL_HOPPER, 2200f, "Bituminous Export Coal"),
                RailCar("csx_c3", "CSX Boxcar 'How Tomorrow Moves'", RailCarType.CSX_FREIGHT_BOXCAR, 1750f, "Industrial Foundry Supplies"),
                RailCar("csx_c4", "CSX Dark Future Coal Hopper #3", RailCarType.CSX_COAL_HOPPER, 2200f, "Power Plant Coal"),
                RailCar("csx_c5", "CSX Freight Boxcar #5", RailCarType.CSX_FREIGHT_BOXCAR, 1750f, "Mining Machinery Spare Parts")
            ),
            rewardSilver = 115000,
            rewardGold = 9500,
            rewardDiamonds = 280,
            xpReward = 4600,
            requiredDriverLevel = 5,
            isHighTier = true,
            estimatedMinutes = "2.8 - 3.3 min"
        ),
        ContractJob(
            id = "contract_freight_bnsf_transcon_super",
            title = "🔶 BNSF Southern Transcon Heavy Intermodal (5,600 km)",
            originStation = "Los Angeles Hobart Yard",
            destinationStation = "Chicago Corwith Yard",
            distanceMeters = 5600f,
            hillSeverity = "Cajon Pass & Mojave Heavy Grades",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("bnsf_c1", "BNSF Heritage II Boxcar #1", RailCarType.BNSF_HERITAGE_BOXCAR, 1800f, "Pacific Coast Electronics"),
                RailCar("bnsf_c2", "BNSF Swoop Wedge Boxcar #2", RailCarType.BNSF_HERITAGE_BOXCAR, 1800f, "High-Value Consumer Cargo"),
                RailCar("bnsf_c3", "BNSF Heavy Coal Hopper #3", RailCarType.BNSF_COAL_HOPPER, 2200f, "Wyoming Low-Sulfur Coal"),
                RailCar("bnsf_c4", "BNSF Heritage II Boxcar #4", RailCarType.BNSF_HERITAGE_BOXCAR, 1800f, "Refrigerated Produce"),
                RailCar("bnsf_c5", "BNSF Heavy Coal Hopper #5", RailCarType.BNSF_COAL_HOPPER, 2200f, "Industrial Ore Concentrates")
            ),
            rewardSilver = 135000,
            rewardGold = 11000,
            rewardDiamonds = 320,
            xpReward = 5200,
            requiredDriverLevel = 8,
            isHighTier = true,
            estimatedMinutes = "3.1 - 3.6 min"
        ),
        ContractJob(
            id = "contract_freight_bn_cascade_powder_river",
            title = "🌲 Burlington Northern Cascade Green Coal Drag (5,400 km)",
            originStation = "Gillette Powder River Mine",
            destinationStation = "Midwest Energy Generating Siding",
            distanceMeters = 5400f,
            hillSeverity = "High Plains Rolling Gradient",
            environment = EnvironmentType.DESERT_CANYON,
            railCars = listOf(
                RailCar("bn_c1", "BN Cascade Green Hopper #1", RailCarType.BN_CASCADE_GREEN_HOPPER, 2250f, "Powder River Basin Coal"),
                RailCar("bn_c2", "BN Cascade Green Hopper #2", RailCarType.BN_CASCADE_GREEN_HOPPER, 2250f, "Sub-bituminous Coal"),
                RailCar("bn_c3", "BN Executive Cream Boxcar #3", RailCarType.BN_EXECUTIVE_BOXCAR, 1800f, "Locomotive Maintenance Kits"),
                RailCar("bn_c4", "BN Cascade Green Hopper #4", RailCarType.BN_CASCADE_GREEN_HOPPER, 2250f, "Heavy Haul Coal"),
                RailCar("bn_c5", "BN Cascade Green Boxcar #5", RailCarType.BN_CASCADE_GREEN_BOXCAR, 1750f, "Mining Equipment")
            ),
            rewardSilver = 130000,
            rewardGold = 10500,
            rewardDiamonds = 310,
            xpReward = 5000,
            requiredDriverLevel = 7,
            isHighTier = true,
            estimatedMinutes = "3.0 - 3.5 min"
        ),
        ContractJob(
            id = "contract_freight_santa_fe_superfleet",
            title = "🔴 Santa Fe Super Fleet Warbonnet Fast Freight (5,500 km)",
            originStation = "Richmond San Francisco Bay Yard",
            destinationStation = "Clovis Division Hub",
            distanceMeters = 5500f,
            hillSeverity = "Tehachapi Loop Steep Mountains",
            environment = EnvironmentType.REDWOOD_COAST,
            railCars = listOf(
                RailCar("sf_c1", "Santa Fe Warbonnet Red Boxcar #1", RailCarType.SANTA_FE_WARBONNET_BOXCAR, 1800f, "Priority Transcon Freight"),
                RailCar("sf_c2", "Santa Fe Warbonnet Red Boxcar #2", RailCarType.SANTA_FE_WARBONNET_BOXCAR, 1800f, "California Citrus & Wine"),
                RailCar("sf_c3", "Santa Fe Bluebonnet Tanker #3", RailCarType.SANTA_FE_BLUEBONNET_TANKER, 1850f, "Refined Aviation Fuel"),
                RailCar("sf_c4", "Santa Fe Warbonnet Red Boxcar #4", RailCarType.SANTA_FE_WARBONNET_BOXCAR, 1800f, "Manufactured Parts"),
                RailCar("sf_c5", "Santa Fe Bluebonnet Tanker #5", RailCarType.SANTA_FE_BLUEBONNET_TANKER, 1850f, "High-Octane Naphtha")
            ),
            rewardSilver = 132000,
            rewardGold = 10800,
            rewardDiamonds = 315,
            xpReward = 5100,
            requiredDriverLevel = 7,
            isHighTier = true,
            estimatedMinutes = "3.0 - 3.5 min"
        ),
        ContractJob(
            id = "contract_freight_union_pacific_overland",
            title = "🇺🇸 Union Pacific 'Building America' Heavy Unit (5,700 km)",
            originStation = "North Platte Bailey Yard",
            destinationStation = "Salt Lake City Hub",
            distanceMeters = 5700f,
            hillSeverity = "Sherman Hill Continental Divide",
            environment = EnvironmentType.ALPINE_PEAKS,
            railCars = listOf(
                RailCar("up_c1", "Union Pacific Armor Yellow Hopper #1", RailCarType.UNION_PACIFIC_HOPPER, 2200f, "Wyoming Soda Ash"),
                RailCar("up_c2", "Union Pacific Armor Yellow Hopper #2", RailCarType.UNION_PACIFIC_HOPPER, 2200f, "Agricultural Potash"),
                RailCar("up_c3", "Union Pacific Boxcar #3", RailCarType.CONTAINER_BOXCAR, 1800f, "Automotive Components"),
                RailCar("up_c4", "Union Pacific Armor Yellow Hopper #4", RailCarType.UNION_PACIFIC_HOPPER, 2200f, "Refined Silica"),
                RailCar("up_c5", "Union Pacific Fuel Tanker #5", RailCarType.FUEL_TANKER, 1850f, "Bio-Diesel Fuel")
            ),
            rewardSilver = 140000,
            rewardGold = 11500,
            rewardDiamonds = 330,
            xpReward = 5300,
            requiredDriverLevel = 8,
            isHighTier = true,
            estimatedMinutes = "3.1 - 3.6 min"
        ),
        ContractJob(
            id = "contract_freight_norfolk_southern_pocahontas",
            title = "🐎 Norfolk Southern Thoroughbred Heavy Coal Run (5,200 km)",
            originStation = "Bluefield West Virginia Siding",
            destinationStation = "Norfolk Lamberts Point Pier 6",
            distanceMeters = 5200f,
            hillSeverity = "Blue Ridge Mountain Heavy Grades",
            environment = EnvironmentType.INDUSTRIAL_VALLEY,
            railCars = listOf(
                RailCar("ns_c1", "NS Thoroughbred Coal Hopper #1", RailCarType.NORFOLK_SOUTHERN_HOPPER, 2250f, "Pocahontas Metallurgical Coal"),
                RailCar("ns_c2", "NS Thoroughbred Coal Hopper #2", RailCarType.NORFOLK_SOUTHERN_HOPPER, 2250f, "Premium Coking Coal"),
                RailCar("ns_c3", "NS Heavy Freight Boxcar #3", RailCarType.CONTAINER_BOXCAR, 1800f, "Locomotive Traction Motors"),
                RailCar("ns_c4", "NS Thoroughbred Coal Hopper #4", RailCarType.NORFOLK_SOUTHERN_HOPPER, 2250f, "Export Steaming Coal"),
                RailCar("ns_c5", "NS Thoroughbred Coal Hopper #5", RailCarType.NORFOLK_SOUTHERN_HOPPER, 2250f, "Foundry Anthracite")
            ),
            rewardSilver = 125000,
            rewardGold = 10000,
            rewardDiamonds = 300,
            xpReward = 4800,
            requiredDriverLevel = 6,
            isHighTier = true,
            estimatedMinutes = "2.9 - 3.4 min"
        )
    )

    val PROMO_CODES = listOf(
        // ⭐ REQUESTED PROMO CODE: Ceske (Gives 16 České dráhy & European Trains) ⭐
        PromoCode(
            code = "Ceske",
            rewardDescription = "🇨🇿 ČESKÉ DRÁHY & EUROPEAN FLEET: 16 Trains (Nightjet Vectron, DB Shark 844, DB EuroCity 186, Alstom Traxx 388, Siemens Smartron, ComfortJet Vectron, ČD Pendolino 680, RegioPanter, InterPanter, Gorila 350, Peršing 163, Eso 362, Bardotka 749, Brejlovec 754, Coradia Stream, Vectron Dual Mode) + 300K Silver, 15K Gold & 600 Diamonds!",
            secretTrainIds = listOf(
                "nightjet_vectron_obb_cd",
                "cd_db_shark_link",
                "cd_db_eurocity_186",
                "cd_alstom_traxx_388",
                "cd_siemens_smartron",
                "cd_vectron_comfortjet",
                "cd_pendolino_class_680",
                "cd_regiopanter_640",
                "cd_interpanter_660",
                "cd_gorilla_class_350",
                "cd_pershing_class_163",
                "cd_eso_class_362",
                "cd_bardotka_class_749",
                "cd_brejlovec_class_754",
                "cd_alstom_coradia_stream",
                "cd_vectron_dual_mode_248"
            ),
            silverReward = 300000,
            goldReward = 15000,
            diamondReward = 600,
            blueprintReward = 300,
            coolantReward = 250,
            sandReward = 250
        ),

        // ⭐ PROMO CODE: Metra (Gives 7 Metra Commuter Trains) ⭐
        PromoCode(
            code = "Metra",
            rewardDescription = "🚆 METRA COMMUTER PACK: 7 Diesel & Electric Metra Locomotives with 3 Liveries each (F40PH, MP36PH, F59PHI, Highliner EMU, SC-44 Charger, E8 Streamliner, Coradia Fastliner) + 150K Silver, 7.5K Gold & 350 Diamonds!",
            secretTrainIds = listOf(
                "metra_f40ph_screamer",
                "metra_mp36ph_express",
                "metra_f59phi_silver",
                "metra_highliner_emu",
                "metra_charger_sc44",
                "metra_e8_streamliner",
                "metra_alstom_coradia_dual"
            ),
            silverReward = 150000,
            goldReward = 7500,
            diamondReward = 350,
            blueprintReward = 200,
            coolantReward = 150,
            sandReward = 150
        ),

        // ⭐ PROMO CODE: Towinclude (Gives 3 Towing / Rescue Trains) ⭐
        PromoCode(
            code = "Towinclude",
            rewardDescription = "🛠️ 3 TOWING TRAINS: LNER 1472 Steam Tow, Titan HD-5000 Diesel, & AeroTow Class 92 Electric + 75K Silver & 200 Diamonds!",
            secretTrainIds = listOf("tow_steam_lner_1472", "tow_diesel_heavy_rescue", "tow_electric_dual_rescue"),
            silverReward = 75000,
            goldReward = 3000,
            diamondReward = 200,
            blueprintReward = 150,
            coolantReward = 100,
            sandReward = 100
        ),

        // ⭐ PROMO CODE: Pendolino (Gives Class 390 Bullet Train) ⭐
        PromoCode(
            code = "Pendolino",
            rewardDescription = "🚅 SECRET BULLET TRAIN: Virgin Class 390 Pendolino (LVL 100 MAX, 350+ KM/H) + 120K Silver & 300 Diamonds!",
            secretTrainId = "secret_virgin_pendolino_390",
            silverReward = 120000,
            goldReward = 6000,
            diamondReward = 300,
            blueprintReward = 200
        ),

        // ⭐ PROMO CODE: Crazy fast (Gives Shinkansen E2-1000) ⭐
        PromoCode(
            code = "Crazy fast",
            rewardDescription = "⚡ SECRET TRAIN: Shinkansen E2-1000 'Hayate' (LVL 100 MAX, 350+ KM/H) + 100K Silver & 250 Diamonds!",
            secretTrainId = "secret_shinkansen_e2",
            silverReward = 100000,
            goldReward = 5000,
            diamondReward = 250,
            blueprintReward = 150
        ),

        PromoCode("BLOXWORKS", "50,000 Silver Coins & 500 Gold Coins", silverReward = 50000, goldReward = 500),
        PromoCode("TRAIN2D", "25,000 Silver Coins & 50 Diamonds", silverReward = 25000, diamondReward = 50),
        PromoCode("GHOST2026", "SECRET TRAIN: Phantom Express 9000 (LVL 50 MAX)!", secretTrainId = "secret_ghost_train", diamondReward = 20),
        PromoCode("HYPERSTEAM", "SECRET TRAIN: Hyper Steam Colossus 4-8-4 (LVL 50 MAX)!", secretTrainId = "secret_hyper_steam", goldReward = 1000),
        PromoCode("GOLDENEMD", "SECRET TRAIN: EMD Golden Eagle GP60 (LVL 50 MAX)!", secretTrainId = "secret_golden_emd", goldReward = 1500),
        PromoCode("MAGLEV2099", "SECRET TRAIN: CyberRail X-Maglev (LVL 50 MAX)!", secretTrainId = "secret_cyber_maglev", diamondReward = 100),
        PromoCode("DIAMONDCHEST", "250 Diamonds for custom skins!", diamondReward = 250),
        PromoCode("HILLCLIMBER", "10,000 Gold Coins + 100 Sand Reservoirs", goldReward = 10000, sandReward = 100),
        PromoCode("BOILERMASTER", "15,000 Silver + 100 Water Coolant Cans", silverReward = 15000, coolantReward = 100),
        PromoCode("SPEEDDEMON", "150 Diamonds + 20,000 Silver", silverReward = 20000, diamondReward = 150),
        PromoCode("DIESELPOWER", "35,000 Silver + 300 Gold", silverReward = 35000, goldReward = 300),
        PromoCode("BLUEPRINTSVIP", "100 Blueprints for workshop upgrades", blueprintReward = 100),
        PromoCode("FULLTHROTTLE", "40,000 Silver + 75 Diamonds", silverReward = 40000, diamondReward = 75),
        PromoCode("IRONHORSE", "5,000 Gold Coins + Vintage Rustproof Paint", goldReward = 5000, skinColor = 0xFF5D4037),
        PromoCode("CONDUCTOR", "250 Blueprints + 100 Diamonds", blueprintReward = 250, diamondReward = 100),
        PromoCode("SANDSTORM", "18,000 Silver + 500 Gold", silverReward = 18000, goldReward = 500, sandReward = 50),
        PromoCode("OVERHEATNOPE", "200 Coolant Packs + 50 Diamonds", coolantReward = 200, diamondReward = 50),
        PromoCode("RAILROADKING", "100,000 Silver + 2,000 Gold", silverReward = 100000, goldReward = 2000),
        PromoCode("CANYONRUNNER", "50,000 Silver + 120 Diamonds", silverReward = 50000, diamondReward = 120),
        PromoCode("MIDNIGHTTRAIN", "Midnight Stealth Skin + 80 Diamonds", diamondReward = 80, skinColor = 0xFF12151A),
        PromoCode("FREIGHTHERO", "30,000 Silver + 400 Gold", silverReward = 30000, goldReward = 400),
        PromoCode("STEAMVIBES", "25,000 Silver + 60 Diamonds", silverReward = 25000, diamondReward = 60),
        PromoCode("EXPRESSDELIVERY", "500 Gold + 50 Blueprints", goldReward = 500, blueprintReward = 50),
        PromoCode("MEGAENGINEER", "150,000 Silver + 5,000 Gold + 500 Diamonds + 200 Blueprints!", silverReward = 150000, goldReward = 5000, diamondReward = 500, blueprintReward = 200)
    )

    /**
     * Determines authentic matched trainset car count:
     * - Freight trains: 5 cars
     * - Electric train / Passenger trains: 7 cars
     * - Steam trains: 6 cars
     * - Other trains / Max limit: 8 cars maximum
     */
    fun getMatchedCarCountForTrain(train: TrainModel): Int {
        return when {
            train.type == TrainType.STEAM || train.category == TrainCategory.STEAM_TITAN -> 6
            train.type == TrainType.ELECTRIC_MAGLEV ||
                train.category == TrainCategory.HIGH_SPEED ||
                train.category == TrainCategory.COMMUTER_METRA ||
                train.category == TrainCategory.CZECH_CD ||
                train.category == TrainCategory.ELECTRIC_CHAMPION ||
                train.id.startsWith("metra_") || train.id == "emd_f40ph_metra" ||
                train.id.startsWith("cd_") || train.id.startsWith("nightjet_") ||
                train.id.contains("acela") || train.id.contains("shinkansen") ||
                train.id.contains("pendolino") || train.id.contains("passenger") -> 7
            train.category == TrainCategory.FREIGHT_DIESEL || train.type == TrainType.DIESEL -> 5
            else -> 8
        }.coerceIn(1, 8)
    }

    /**
     * Returns authentic matched railcars specifically designed for each train type.
     * Guaranteed car counts:
     * - Freight train: 5 cars
     * - Electric train / Passenger train: 7 cars
     * - Steam train: 6 cars
     * - Other / Custom max: up to 8 cars
     */
    fun getMatchedRailCarsForTrain(train: TrainModel, count: Int? = null): List<RailCar> {
        val targetCount = (count ?: getMatchedCarCountForTrain(train)).coerceIn(1, 8)

        val fullConsist: List<RailCar> = when {
            // Chicago Metra Fleet: 6 Bi-Level Gallery Coaches + 1 Bi-Level Cab Car (7 cars)
            train.id.startsWith("metra_") || train.id == "emd_f40ph_metra" -> {
                listOf(
                    RailCar("mc_1", "Metra Bi-Level Gallery Coach #7021", RailCarType.METRA_GALLERY_COACH, 1400f, "Commuters & Bicycles"),
                    RailCar("mc_2", "Metra Bi-Level Gallery Coach #7045", RailCarType.METRA_GALLERY_COACH, 1400f, "Express Passengers"),
                    RailCar("mc_3", "Metra Bi-Level Gallery Coach #7058", RailCarType.METRA_GALLERY_COACH, 1400f, "Quiet Zone Commuters"),
                    RailCar("mc_4", "Metra Bi-Level Gallery Coach #7082", RailCarType.METRA_GALLERY_COACH, 1400f, "Mainline Passengers"),
                    RailCar("mc_5", "Metra Bi-Level Gallery Coach #7104", RailCarType.METRA_GALLERY_COACH, 1400f, "Rush-Hour Commuters"),
                    RailCar("mc_6", "Metra Bi-Level Gallery Coach #7120", RailCarType.METRA_GALLERY_COACH, 1400f, "Intercity Passengers"),
                    RailCar("mc_cab", "Metra Bi-Level Cab Control Car #8512", RailCarType.METRA_CAB_CAR, 1450f, "Engineer Cab & Passengers"),
                    RailCar("mc_8", "Metra Bi-Level Overflow Coach #7135", RailCarType.METRA_GALLERY_COACH, 1400f, "Extra Capacity")
                )
            }
            // České Dráhy Pendolino Class 680 (7-car EMU consist + 8th spare)
            train.id == "cd_pendolino_class_680" -> {
                listOf(
                    RailCar("cd_p1", "ČD Class 081 First Class Coach", RailCarType.CD_PENDOLINO_COACH, 1300f, "First Class Tilting Express"),
                    RailCar("cd_p2", "ČD Class 082 Bistro Restaurant Car", RailCarType.CD_PENDOLINO_COACH, 1350f, "Gourmet Dining & Passengers"),
                    RailCar("cd_p3", "ČD Class 083 Second Class Standard Coach", RailCarType.CD_PENDOLINO_COACH, 1300f, "SuperCity Standard Travelers"),
                    RailCar("cd_p4", "ČD Class 084 Quiet Zone Coach", RailCarType.CD_PENDOLINO_COACH, 1300f, "Quiet Zone Passengers"),
                    RailCar("cd_p5", "ČD Class 085 Family & Luggage Coach", RailCarType.CD_PENDOLINO_COACH, 1300f, "Family Salon & Bicycles"),
                    RailCar("cd_p6", "ČD Class 086 Intercity Express Coach", RailCarType.CD_PENDOLINO_COACH, 1300f, "Express Passengers"),
                    RailCar("cd_pcab", "ČD Class 682 Tail Cab Unit", RailCarType.CD_PENDOLINO_CAB, 1320f, "Aerodynamic Driving Trailer"),
                    RailCar("cd_p8", "ČD Class 087 Intercity Auxiliary Coach", RailCarType.CD_PENDOLINO_COACH, 1300f, "Peak Hours Overflow")
                )
            }
            // České Dráhy EuroCity, ComfortJet & Regional Fleet
            train.id == "cd_vectron_comfortjet" || train.id.startsWith("cd_") || train.id.startsWith("nightjet_") -> {
                if (train.id.contains("shark") || train.id.contains("panter")) {
                    listOf(
                        RailCar("cdr_1", "ČD Regio Najbrt Coach Apmz", RailCarType.CD_REGIO_COACH, 1250f, "Regional Commuters"),
                        RailCar("cdr_2", "ČD Regio Low-Floor Coach Bmz #1", RailCarType.CD_REGIO_COACH, 1250f, "Bicycles & Travelers"),
                        RailCar("cdr_3", "ČD Regio Intercity Coach Bmz #2", RailCarType.CD_REGIO_COACH, 1250f, "Regional Passengers"),
                        RailCar("cdr_4", "ČD Regio Standard Coach Bmz #3", RailCarType.CD_REGIO_COACH, 1250f, "Suburban Commuters"),
                        RailCar("cdr_5", "ČD Regio Multi-Function Coach #4", RailCarType.CD_REGIO_COACH, 1250f, "Strollers & Travelers"),
                        RailCar("cdr_6", "ČD Regio Low-Floor Coach Bmz #5", RailCarType.CD_REGIO_COACH, 1250f, "Express Commuters"),
                        RailCar("cdr_7", "ČD Regio Control Driving Trailer Bfhpvee", RailCarType.CD_REGIO_COACH, 1280f, "Driving Trailer Passengers"),
                        RailCar("cdr_8", "ČD Regio Auxiliary Coach #8", RailCarType.CD_REGIO_COACH, 1250f, "Reserve Capacity")
                    )
                } else {
                    listOf(
                        RailCar("cdc_1", "ČD ComfortJet Najbrt First Class Apmz", RailCarType.CD_COMFORTJET_COACH, 1350f, "EuroCity VIP Salon"),
                        RailCar("cdc_2", "ČD ComfortJet Bistro Dining WRmz", RailCarType.CD_COMFORTJET_COACH, 1380f, "EuroCity Bistro & Bar"),
                        RailCar("cdc_3", "ČD ComfortJet Second Class Bmz #1", RailCarType.CD_COMFORTJET_COACH, 1350f, "EuroCity Express Travelers"),
                        RailCar("cdc_4", "ČD ComfortJet Second Class Bmz #2", RailCarType.CD_COMFORTJET_COACH, 1350f, "Quiet Compartment Passengers"),
                        RailCar("cdc_5", "ČD ComfortJet Family Cinema Coach Bdmpz", RailCarType.CD_COMFORTJET_COACH, 1350f, "Kids Cinema & Travelers"),
                        RailCar("cdc_6", "ČD ComfortJet Second Class Bmz #3", RailCarType.CD_COMFORTJET_COACH, 1350f, "International Express Commuters"),
                        RailCar("cdc_7", "ČD ComfortJet End Driving Coach Bmpz", RailCarType.CD_COMFORTJET_COACH, 1360f, "EuroCity Panoramic View"),
                        RailCar("cdc_8", "ČD ComfortJet Sleeper Berth WLABmz", RailCarType.CD_COMFORTJET_COACH, 1400f, "Nightjet Sleeper Salon")
                    )
                }
            }
            // Amtrak Acela Express (7-car consist + 8th power car)
            train.id.contains("acela") -> {
                listOf(
                    RailCar("acela_c1", "Acela First Class Parlor Car", RailCarType.AMTRAK_ACELA_COACH, 1400f, "NEC Business Travelers"),
                    RailCar("acela_c2", "Acela Cafe / Bistro Club Car", RailCarType.AMTRAK_ACELA_COACH, 1400f, "High-Speed Refreshments"),
                    RailCar("acela_c3", "Acela Business Class Coach #1", RailCarType.AMTRAK_ACELA_COACH, 1400f, "Northeast Corridor Commuters"),
                    RailCar("acela_c4", "Acela Quiet Zone Business Coach", RailCarType.AMTRAK_ACELA_COACH, 1400f, "Quiet Work Salon"),
                    RailCar("acela_c5", "Acela Business Class Coach #2", RailCarType.AMTRAK_ACELA_COACH, 1400f, "Express Business Travelers"),
                    RailCar("acela_c6", "Acela Business Class Coach #3", RailCarType.AMTRAK_ACELA_COACH, 1400f, "Metropolitan Travelers"),
                    RailCar("acela_cab", "Acela Trailing Power Car #2024", RailCarType.AMTRAK_ACELA_CAB, 1500f, "Auxiliary Propulsion Unit"),
                    RailCar("acela_c8", "Acela Auxiliary High-Speed Coach", RailCarType.AMTRAK_ACELA_COACH, 1400f, "NEC Extra Capacity")
                )
            }
            // Japanese Shinkansen E2-1000 (7-car consist + 8th nose car)
            train.id == "secret_shinkansen_e2" || train.id.contains("shinkansen") -> {
                listOf(
                    RailCar("shink_1", "Shinkansen E2 E226 Standard Coach #1", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Tohoku Express Passengers"),
                    RailCar("shink_2", "Shinkansen E2 E225 Pantograph Coach #2", RailCarType.SHINKANSEN_E2_COACH, 1220f, "Aerofoil High-Speed Car"),
                    RailCar("shink_3", "Shinkansen E2 E215 Green Car Luxury Salon", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Luxury First Class Salon"),
                    RailCar("shink_4", "Shinkansen E2 E226 Standard Coach #3", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Reserved Seat Passengers"),
                    RailCar("shink_5", "Shinkansen E2 E225 Pantograph Coach #4", RailCarType.SHINKANSEN_E2_COACH, 1220f, "High-Speed Propulsion Coach"),
                    RailCar("shink_6", "Shinkansen E2 E226 Standard Coach #5", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Intercity Commuters"),
                    RailCar("shink_7", "Shinkansen E2 E224 Trailing Aerodynamic Nose", RailCarType.SHINKANSEN_E2_COACH, 1250f, "Aerodynamic Driving Nose"),
                    RailCar("shink_8", "Shinkansen E2 E227 Auxiliary Coach #8", RailCarType.SHINKANSEN_E2_COACH, 1200f, "Tohoku Shinkansen Reserve")
                )
            }
            // British Class 390 Pendolino (7-car consist + 8th car)
            train.id.contains("pendolino") -> {
                listOf(
                    RailCar("pen_c1", "Class 390 Tilting Standard Coach #1", RailCarType.PENDOLINO_COACH, 1250f, "West Coast Main Line Travelers"),
                    RailCar("pen_c2", "Class 390 Buffet / First Dining Coach", RailCarType.PENDOLINO_COACH, 1280f, "First Class Dining"),
                    RailCar("pen_c3", "Class 390 Standard Tilting Coach #2", RailCarType.PENDOLINO_COACH, 1250f, "High-Speed Commuters"),
                    RailCar("pen_c4", "Class 390 Quiet Zone Tilting Coach", RailCarType.PENDOLINO_COACH, 1250f, "Quiet Lounge Passengers"),
                    RailCar("pen_c5", "Class 390 Standard Tilting Coach #3", RailCarType.PENDOLINO_COACH, 1250f, "Regional Travelers"),
                    RailCar("pen_c6", "Class 390 First Class Salon Coach", RailCarType.PENDOLINO_COACH, 1280f, "First Class Business Salon"),
                    RailCar("pen_cab", "Class 390 Aerodynamic Tail Cab #8500", RailCarType.PENDOLINO_REAR_CAB, 1300f, "Streamlined Trailing Cab"),
                    RailCar("pen_c8", "Class 390 Auxiliary Tilting Coach #8", RailCarType.PENDOLINO_COACH, 1250f, "West Coast Extra Capacity")
                )
            }
            // Steam Titans: Union Pacific Big Boy, NYC Hudson, Pacific 4-6-2, CP Royal Hudson (6 cars)
            train.type == TrainType.STEAM || train.category == TrainCategory.STEAM_TITAN -> {
                listOf(
                    RailCar("pull_1", "Pullman Heavyweight Parlor 'Cheyenne'", RailCarType.STEAM_PULLMAN_COACH, 1600f, "Century Limited Passengers"),
                    RailCar("pull_2", "Pullman Heritage Dining Salon 'Wyoming'", RailCarType.STEAM_PULLMAN_COACH, 1650f, "Fine Dining & Passengers"),
                    RailCar("pull_3", "Pullman Sleeper Car 'Mount Holyoke'", RailCarType.STEAM_PULLMAN_COACH, 1620f, "Pullman Sleeper Berths"),
                    RailCar("pull_4", "Pullman Club & Buffet Lounge 'Empire'", RailCarType.STEAM_PULLMAN_COACH, 1600f, "Steam Heritage Travelers"),
                    RailCar("pull_5", "Pullman Heavyweight Parlor 'Manhattan'", RailCarType.STEAM_PULLMAN_COACH, 1600f, "First Class Travelers"),
                    RailCar("pull_6", "Pullman Observation Lounge 'Centurion'", RailCarType.STEAM_PULLMAN_COACH, 1620f, "Heritage Open-Platform Lounge"),
                    RailCar("pull_7", "Pullman Heritage Baggage-Express Car", RailCarType.STEAM_PULLMAN_COACH, 1550f, "Railway Post & Cargo"),
                    RailCar("pull_8", "Pullman Presidential Salon 'Gloucester'", RailCarType.STEAM_PULLMAN_COACH, 1650f, "VIP Heritage Suite")
                )
            }
            // CSX Freight Diesels (5 cars)
            train.id.contains("csx") -> {
                listOf(
                    RailCar("csx_1", "CSX 50ft Boxcar", RailCarType.CSX_FREIGHT_BOXCAR, 1500f, "Manufactured Freight"),
                    RailCar("csx_2", "CSX Coal Hopper", RailCarType.CSX_COAL_HOPPER, 1700f, "Appalachian Coal"),
                    RailCar("csx_3", "Heavy Crude Oil Tanker", RailCarType.FUEL_TANKER, 1800f, "Refined Fuel"),
                    RailCar("csx_4", "CSX Covered Grain Hopper", RailCarType.UNION_PACIFIC_HOPPER, 1650f, "Midwest Grain"),
                    RailCar("csx_5", "CSX Intermodal Container Boxcar", RailCarType.CONTAINER_BOXCAR, 1750f, "Container Cargo"),
                    RailCar("csx_6", "CSX Heavy Timber Flatcar", RailCarType.LUMBER_FLATCAR, 1600f, "Lumber Timber"),
                    RailCar("csx_7", "CSX Chemical Tanker", RailCarType.FUEL_TANKER, 1800f, "Chemical Feedstock"),
                    RailCar("csx_8", "CSX Ballast Rotary Hopper", RailCarType.CSX_COAL_HOPPER, 1700f, "Track Ballast")
                )
            }
            // BNSF Freight Diesels (5 cars)
            train.id.contains("bnsf") -> {
                listOf(
                    RailCar("bnsf_1", "BNSF Wedge Boxcar", RailCarType.BNSF_HERITAGE_BOXCAR, 1500f, "Intermodal Freight"),
                    RailCar("bnsf_2", "BNSF High-Side Hopper", RailCarType.BNSF_COAL_HOPPER, 1700f, "Powder River Coal"),
                    RailCar("bnsf_3", "BNSF Grain Hopper", RailCarType.UNION_PACIFIC_HOPPER, 1600f, "Midwest Grain"),
                    RailCar("bnsf_4", "BNSF Crude Oil Tanker", RailCarType.FUEL_TANKER, 1800f, "Petroleum & Diesel"),
                    RailCar("bnsf_5", "BNSF Heavy Double-Stack Car", RailCarType.CONTAINER_BOXCAR, 1750f, "Intermodal Freight"),
                    RailCar("bnsf_6", "BNSF Lumber Centerbeam Flatcar", RailCarType.LUMBER_FLATCAR, 1600f, "Western Timber"),
                    RailCar("bnsf_7", "BNSF High-Capacity Grain Hopper", RailCarType.UNION_PACIFIC_HOPPER, 1650f, "Wheat Harvest"),
                    RailCar("bnsf_8", "BNSF Heritage Caboose Unit", RailCarType.CONTAINER_BOXCAR, 1500f, "Crew Conductor Caboose")
                )
            }
            // Heavy American Freight Diesels (EMD GP9, SD70ACe, UP, Santa Fe, etc.) (5 cars)
            train.category == TrainCategory.FREIGHT_DIESEL || train.type == TrainType.DIESEL ||
                train.id.contains("gp9") || train.id.contains("sd70") || train.id.contains("union_pacific") ||
                train.id.contains("up_") || train.id.contains("santa_fe") || train.id.contains("norfolk") -> {
                listOf(
                    RailCar("fr_1", "50ft Corrugated Boxcar", RailCarType.BNSF_HERITAGE_BOXCAR, 1500f, "Manufactured Freight"),
                    RailCar("fr_2", "Triple-Bay Covered Grain Hopper", RailCarType.UNION_PACIFIC_HOPPER, 1700f, "Midwest Wheat"),
                    RailCar("fr_3", "Heavy Crude Oil Tanker", RailCarType.SANTA_FE_BLUEBONNET_TANKER, 1800f, "Refined Diesel & Fuel"),
                    RailCar("fr_4", "High-Side Aggregate Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1850f, "Metallurgical Coal"),
                    RailCar("fr_5", "Intermodal High-Cube Boxcar", RailCarType.CONTAINER_BOXCAR, 1750f, "Industrial Machinery"),
                    RailCar("fr_6", "Heavy Lumber Transport Flatcar", RailCarType.LUMBER_FLATCAR, 1600f, "Raw Pine Timber"),
                    RailCar("fr_7", "Pressurized Liquid Gas Tanker", RailCarType.SANTA_FE_BLUEBONNET_TANKER, 1800f, "Pressurized Liquid Gas"),
                    RailCar("fr_8", "Heavy Mainline Rotary Coal Hopper", RailCarType.HEAVY_COAL_HOPPER, 1850f, "Smelter Coal")
                )
            }
            // Electric Champions & Modern Passenger (7 cars)
            train.type == TrainType.ELECTRIC_MAGLEV || train.category == TrainCategory.ELECTRIC_CHAMPION ||
                train.category == TrainCategory.HIGH_SPEED || train.category == TrainCategory.COMMUTER_METRA -> {
                listOf(
                    RailCar("pass_1", "Streamlined Intercity First Class Coach", RailCarType.PASSENGER_COACH, 1200f, "First Class Lounge"),
                    RailCar("pass_2", "Streamlined Bistro Dining Club Car", RailCarType.PASSENGER_COACH, 1250f, "Dining Passengers"),
                    RailCar("pass_3", "Streamlined Express Coach #1", RailCarType.PASSENGER_COACH, 1200f, "Regional Passengers"),
                    RailCar("pass_4", "Streamlined Express Coach #2", RailCarType.PASSENGER_COACH, 1200f, "Express Commuters"),
                    RailCar("pass_5", "Streamlined Quiet Zone Coach", RailCarType.PASSENGER_COACH, 1200f, "Quiet Work Salon"),
                    RailCar("pass_6", "Streamlined Commuter Coach #3", RailCarType.PASSENGER_COACH, 1200f, "Mainline Commuters"),
                    RailCar("pass_7", "Streamlined Observation Tail Coach", RailCarType.PASSENGER_COACH, 1220f, "Observation Panorama"),
                    RailCar("pass_8", "Streamlined Auxiliary Coach #8", RailCarType.PASSENGER_COACH, 1200f, "Peak Hour Travelers")
                )
            }
            // Default General Fallback Consist (max 8 cars)
            else -> {
                listOf(
                    RailCar("gen_1", "General Mainline Boxcar #1", RailCarType.CONTAINER_BOXCAR, 1400f, "General Cargo"),
                    RailCar("gen_2", "General Bulk Grain Hopper #2", RailCarType.UNION_PACIFIC_HOPPER, 1500f, "Bulk Grain"),
                    RailCar("gen_3", "General Liquid Fuel Tanker #3", RailCarType.FUEL_TANKER, 1600f, "Refined Fuel"),
                    RailCar("gen_4", "General Flatcar #4", RailCarType.LUMBER_FLATCAR, 1500f, "Structural Steel"),
                    RailCar("gen_5", "General Open Coal Hopper #5", RailCarType.HEAVY_COAL_HOPPER, 1600f, "Industrial Ore"),
                    RailCar("gen_6", "General Boxcar #6", RailCarType.CONTAINER_BOXCAR, 1400f, "Consumer Goods"),
                    RailCar("gen_7", "General Express Coach #7", RailCarType.PASSENGER_COACH, 1300f, "Express Courier"),
                    RailCar("gen_8", "General Trail Caboose #8", RailCarType.CONTAINER_BOXCAR, 1350f, "Conductor Cabin")
                )
            }
        }

        // Return exactly the target count of cars, capped strictly at maximum 8 cars
        return fullConsist.take(targetCount)
    }

    /**
     * Resolves the consist for a contract, ensuring matched railcars are provided:
     * - Freight trains: 5 matched cars
     * - Electric train / Passenger trains: 7 matched cars
     * - Steam trains: 6 matched cars
     * - Other trains: up to 8 matched cars (all trains max 8 cars)
     */
    fun resolveConsistForTrain(contract: ContractJob, train: TrainModel): List<RailCar> {
        val targetCount = getMatchedCarCountForTrain(train)
        if (contract.id == "contract_fleet_identical_consist" || contract.id.contains("matched")) {
            val matchedCars = getMatchedRailCarsForTrain(train, targetCount)
            return matchedCars.take(targetCount).mapIndexed { index, car ->
                car.copy(
                    id = "${train.id}_car_${index + 1}",
                    name = car.name
                )
            }
        }
        // Lumber flatcar contract specifically requested with a freight diesel
        if (contract.railCars.any { it.type == RailCarType.LUMBER_FLATCAR } && train.type == TrainType.DIESEL && !train.id.startsWith("metra_") && !train.id.startsWith("cd_")) {
            // Provide 5 timber/freight cars for freight trains
            return getMatchedRailCarsForTrain(train, 5)
        }
        // All other contracts automatically resolve to the train's matched train set (Freight 5, Electric/Pass 7, Steam 6, Max 8)
        return getMatchedRailCarsForTrain(train, targetCount)
    }
}

