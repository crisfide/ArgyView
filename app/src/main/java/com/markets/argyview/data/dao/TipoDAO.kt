package com.markets.argyview.data.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//import com.markets.argyview.data.entidades.TipoEntity
//
//@Dao
//interface TipoDAO {
//    @Insert
//    suspend fun insertar(tipoEntity: TipoEntity)
//
//    @Update
//    suspend fun actualizar(tipoEntity: TipoEntity)
//
//    @Delete
//    suspend fun eliminar(tipoEntity: TipoEntity)
//
//    @Query("SELECT * FROM tipos")
//    suspend fun obtenerTodos(): List<TipoEntity>
//}