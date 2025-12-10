package dk.byggepiloten.firma.data.model

enum class RoomType(val displayName: String, val requiresMembrane: Boolean) {
    KØKKEN("Køkken", false),
    BADEVÆRELSE("Badeværelse", true),
    GANG("Gang", false),
    ANDET("Andet", false)
}