package dk.byggepiloten.firma.data.network

import retrofit2.Response

class FakeEmailService : EmailService {
    override suspend fun sendEmail(request: EmailRequest): Response<Map<String, Any>> {
        // Simuler et succesfuldt svar uden at sende en rigtig e-mail
        val responseBody = mapOf("success" to true, "url" to "http://fake.url/confirm?token=fake_token")
        return Response.success(responseBody)
    }
}