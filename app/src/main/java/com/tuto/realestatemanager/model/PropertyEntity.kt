package com.tuto.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_table")
class PropertyEntity constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "price") val price: Int,
//    @ColumnInfo(name = "address") val address: String,
//    @ColumnInfo(name = "address_complement") val addressComplement: String,
//    @ColumnInfo(name = "city") val city: String,
//    @ColumnInfo(name = "state") val state: String,
//    @ColumnInfo(name = "zipcode") val zipCode: Int,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "surface") val surface: Int,
//    @ColumnInfo(name = "property_location") val latLng: LatLng,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "room") val room: Int,
    @ColumnInfo(name = "bedroom") val bedroom: Int,
    @ColumnInfo(name = "bathroom") val bathroom: Int,
//    @ColumnInfo(name = "agent_name") val agent: String,
//    @ColumnInfo(name = "property_sale_status") val propertyStatus: Boolean,
    @ColumnInfo(name = "property_on_sale_since") val propertyOnSaleSince: String,
//    @ColumnInfo(name = "date_of_sale") val propertyDateOfSale: String,
//    @ColumnInfo(name = "point_of_interest") val pointOfInterest: List<String>
    @ColumnInfo(name = "poi_train") val poiTrain: Boolean,
    @ColumnInfo(name = "poi_airport") val poiAirport: Boolean,
    @ColumnInfo(name = "poi_ resto") val poiResto: Boolean,
    @ColumnInfo(name = "poi_school") val poiSchool: Boolean,
    @ColumnInfo(name = "poi_bus") val poiBus: Boolean,
    @ColumnInfo(name = "poi_park") val poiPark: Boolean,
) {
}

