package com.tuto.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent")
data class AgentEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
        ){
}