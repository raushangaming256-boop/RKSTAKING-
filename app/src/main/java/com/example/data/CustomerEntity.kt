package com.example.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
@Entity(
    tableName = "customers",
    indices = [
        Index(value = ["name"]),
        Index(value = ["phone"])
    ]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String = "",
    val secondaryPhone: String = "",
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val autoSmsEnabled: Boolean = false,
    val autoSmsFrequency: String = "Daily", // "Daily", "Every 2 Days", "Every 4 Days", "Every 7 Days"
    val autoSmsTime: String = "10:00 AM",
    val lastAutoSmsSent: Long = 0L
)
