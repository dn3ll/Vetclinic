package com.example.vetclinic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Upsert
    suspend fun upsertAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("SELECT * FROM Appointment ORDER BY date ASC")
    fun getAllAppointments(): Flow<List<Appointment>>
}