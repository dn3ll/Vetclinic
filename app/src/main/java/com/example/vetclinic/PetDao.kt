package com.example.vetclinic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Upsert
    suspend fun upsertPet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("SELECT * FROM Pet WHERE id = :id")
    suspend fun getPetById(id: Int): Pet?

    @Query("SELECT * FROM Pet")
    fun getAllPets(): Flow<List<Pet>>
}