// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/OfferViewModel.kt
// FULD, FUNKTIONSYGTIG VERSION – oprettet for at løse Unresolved reference 'OfferViewModel'.
// Trin-for-trin forklaring:
// 1. Importér nødvendige: ViewModel, Hilt, flows, coroutines.
// 2. Definer data class Offer med felter fra screen: taskId, price, comment.
// 3. @HiltViewModel class OfferViewModel: Bruger MutableStateFlow til offers-liste (lokal for nu).
// 4. saveOffer(): Tilføj ny Offer til listen (kan udvides med repo til persistens).
// 5. offers: StateFlow<List<Offer>> til at observere ændringer.
// 6. Offline-first: Kan injicere repository via Hilt for Room/Firestore.
// 7. For nu: Lokal liste for at gøre det kørbart uden ekstra deps.
// 8. Løser errors om offers, saveOffer – nu defineret.
// 9. Kan udvides med reel repo senere.
// 10. Simpel, men fuldt funktionsdygtig til compile og test.

package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class Offer(
    val taskId: String,
    val price: Float,
    val comment: String
)

@HiltViewModel
class OfferViewModel @Inject constructor() : ViewModel() {

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    fun saveOffer(taskId: String, price: Float, comment: String) {
        _offers.update { current ->
            current + Offer(taskId, price, comment)
        }
        // Udvid med repo.save(offer) her for persistens
    }
}