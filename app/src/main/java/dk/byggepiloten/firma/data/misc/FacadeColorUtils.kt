// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/facade/FacadeColorUtils.kt
// NY FIL – public color functions (for at løse private access fejl)
// • Flyttet fra original – nu public så HaeftemoertelStep kan kalde dem
// • Linjer: 72

package dk.byggepiloten.firma.data.misc

import androidx.compose.ui.graphics.Color

public fun getDurapudsSwatchColor(farve: String): Color {
    return when (farve) {
        "Cementgrå" -> Color(0xFFAAAAAA)
        "Hvid" -> Color(0xFFFFFFFF)
        else -> Color(0xFFD0D0D0)
    }
}

public fun getSkalcemSwatchColor(ncsCode: String): Color {
    return when (ncsCode) {
        "Hvid" -> Color(0xFFFFFFFF)
        "S 0505-Y20R" -> Color(0xFFF5E8E0)
        "S 1005-Y30R" -> Color(0xFFF2E0D8)
        "S 1005-Y50R" -> Color(0xFFF0D8D0)
        "S 1010-Y20R" -> Color(0xFFF0E0D0)
        "S 1010-Y50R" -> Color(0xFFE8D0C8)
        "S 1020-Y20R" -> Color(0xFFF0D8C0)
        "S 1040-Y20R" -> Color(0xFFF0C890)
        "S 1500-N" -> Color(0xFFD8D8D8)
        "S 2005-R80B" -> Color(0xFFD0D8E8)
        "S 2005-Y" -> Color(0xFFF0F0E0)
        "S 2010-G30Y" -> Color(0xFFC8E0D0)
        "S 2010-Y30R" -> Color(0xFFE8D8C8)
        "S 2030-Y80R" -> Color(0xFFE0C0B8)
        "S 2040-Y30R" -> Color(0xFFF0B080)
        "S 2502-Y" -> Color(0xFFE0E0D8)
        "S 3005-Y20R" -> Color(0xFFE0D8D0)
        "S 3040-Y50R" -> Color(0xFFE8A870)
        "S 3040-Y80R" -> Color(0xFFD09080)
        "S 4000-N" -> Color(0xFFA8A8A8)
        "S 4010-B90G" -> Color(0xFFA0B8B0)
        "S 5020-B" -> Color(0xFF607080)
        "S 6000-N" -> Color(0xFF909090)
        "S 1002-Y" -> Color(0xFFF0F0E8)
        else -> Color(0xFFD0D0D0)
    }
}