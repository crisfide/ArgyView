package com.markets.argyview.data.entidades
//
//import androidx.annotation.NonNull
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "activos",
//    foreignKeys = [
//        ForeignKey(
//            entity = TipoEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["tipoId"],
//            onDelete = ForeignKey.NO_ACTION)
//    ]
//)
//data class ActivoEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id:Int,
//    @NonNull
//    val ticker:String,
//
//    //fk
//    val tipoId:Int
//)
