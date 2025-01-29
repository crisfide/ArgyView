package com.markets.argyview.data.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//import com.markets.argyview.data.entidades.ActivoEntity
//
//@Dao
//interface ActivoDAO {
//    @Insert
//    suspend fun insertar(activoEntity: ActivoEntity)
//
//    @Update
//    suspend fun actualizar(activoEntity: ActivoEntity)
//
//    @Delete
//    suspend fun eliminar(activoEntity: ActivoEntity)
//
//    @Query("SELECT * FROM activos")
//    suspend fun obtenerTodos(): List<ActivoEntity>
//
//    @Query("SELECT * FROM activos WHERE tipoId = :tipoId")
//    suspend fun obtenerPorTipo(tipoId:Int): List<ActivoEntity>
//}