package com.example.vetclinic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val selectedType: String,
    val name: String,
    val age: Int,
    val photoLocation: String? = null
)