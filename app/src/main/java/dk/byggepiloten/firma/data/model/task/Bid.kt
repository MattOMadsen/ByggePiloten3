package dk.byggepiloten.firma.data.model.task

data class Bid(
    val id: String = "",
    val contractorId: String = "",
    val contractorName: String = "",
    val price: Float = 0f,
    val hours: Int = 0,
    val materials: String = "",
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"
)