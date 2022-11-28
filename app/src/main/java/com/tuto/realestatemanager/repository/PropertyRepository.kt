package com.tuto.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.ui.list.PropertyViewState
import javax.inject.Inject


class PropertyRepository @Inject constructor() {

    val propertiesListLiveData: LiveData<List<PropertyEntity>> = MutableLiveData(
        listOf(

            PropertyEntity(
                1,
                "LOFT",
                7_500_000,
                listOf(
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg"
                ),
                "New-York",
                2_500
            ),

            PropertyEntity(
                2,
                "PENTHOUSE",
                5_800_000,
                listOf(
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                ),
                "Los Angeles",
                2_000
            ),

            PropertyEntity(
                1,
                "LOFT",
                7_500_000,
                listOf(
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg"
                ),
                "New-York",
                2_500
            ),

            PropertyEntity(
                2,
                "PENTHOUSE",
                5_800_000,
                listOf(
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                ),
                "Los Angeles",
                2_000
            ),

            PropertyEntity(
                1,
                "LOFT",
                7_500_000,
                listOf(
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/4/properties/Property-b2660000000001e2000457b5fd0a-31614642.jpg"
                ),
                "New-York",
                2_500
            ),

            PropertyEntity(
                2,
                "PENTHOUSE",
                5_800_000,
                listOf(
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                    "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg",
                ),
                "Los Angeles",
                2_000
            )
        )
    )

    fun getPropertyByIdLiveData(id : Int): LiveData<PropertyEntity> = propertiesListLiveData.map { propertyEntities ->
        propertyEntities.first{it.id == id}
    }




}