package com.example.greenspot

import android.content.Context
import androidx.room.Room
import com.example.greenspot.database.PlantDatabase
import com.example.greenspot.database.migration_1_2
import com.example.greenspot.database.migration_2_3
//import com.example.greenspot.database.migration_2_3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "plant-database"

class PlantRepository private constructor(context: Context,
      private val coroutineScope: CoroutineScope = GlobalScope
){

    private val database: PlantDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            PlantDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(migration_1_2, migration_2_3)
        .build()

    fun getPlants(): Flow<List<Plant>> = database.plantDao().getPlants()
    suspend fun getPlant(id: UUID): Plant = database.plantDao().getPlant(id)

    suspend fun deletePlant(plant: Plant){
        coroutineScope.launch {
            database.plantDao().deletePlant(plant)
        }
    }
    fun updatePlant(plant: Plant) {
        coroutineScope.launch {
            database.plantDao().updatePlant(plant)
        }
    }

    suspend fun addPlant(plant: Plant) {
        database.plantDao().addPlant(plant)
    }

    companion object {
        private var INSTANCE: PlantRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PlantRepository(context)
            }
        }
        fun get(): PlantRepository {
            return INSTANCE ?:
            throw IllegalStateException("PlantRepository must be initialized")
        }
    }
}
