package com.tuto.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "property_table")
class PropertyEntity constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "price") val price: Int,
//    @ColumnInfo(name = "list_photo") val photo: String,
    @ColumnInfo(name = "list_photo") val photoList: List<String>,
    @ColumnInfo(name = "county") val county: String,
    @ColumnInfo(name = "surface") val surface: Int,
//    @ColumnInfo(name = "property_location") val latLng: LatLng,
//    @ColumnInfo(name = "description") val description: String,
//    @ColumnInfo(name = "room") val room: Int,
//    @ColumnInfo(name = "bedroom") val bedroom: Int,
//    @ColumnInfo(name = "bathroom") val bathroom: Int,
//    @ColumnInfo(name = "agent_name") val agent: String,
//    @ColumnInfo(name = "property_sale_status") val propertyStatus: Boolean,
//    @ColumnInfo(name = "property_on_sale_since") val propertyOnSaleSince: String,
//    @ColumnInfo(name = "date_of_sale") val propertyDateOfSale: String,
//    @ColumnInfo(name = "point_of_interest") val pointOfInterest: List<String>
) {
}

