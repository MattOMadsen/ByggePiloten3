package dk.byggepiloten.firma.data.model.task

enum class RoomType(val displayName: String, val requiresMembrane: Boolean) {
    KØKKEN("Køkken", false),
    BADEVÆRELSE("Badeværelse", true),
    GANG("Gang", false),
    ANDET("Andet", false)
}