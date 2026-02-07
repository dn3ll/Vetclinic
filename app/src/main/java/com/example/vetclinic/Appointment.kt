package com.example.vetclinic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petId: Int,
    val doctor: String,
    val procedure: String,
    val date: Long
)