package com.example.pharmacy.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val sex: String = "",
    val profileCompleted: Int = 0,
    val role: Int = 0
    ) : Parcelable {
}
