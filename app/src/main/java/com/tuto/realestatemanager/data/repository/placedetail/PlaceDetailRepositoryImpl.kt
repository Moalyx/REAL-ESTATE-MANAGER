package com.tuto.realestatemanager.data.repository.placedetail

import com.google.android.gms.maps.model.LatLng
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.placedetail.model.PlaceDetailResponse
import com.tuto.realestatemanager.domain.place.PlaceDetailRepository
import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import javax.inject.Inject

class PlaceDetailRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : PlaceDetailRepository {

    override suspend fun getAddressById(id: String): AddressComponentsEntity? {

        val response: PlaceDetailResponse =
            googleApi.getPlaceDetailResponse(BuildConfig.GOOGLE_AUTOCOMPLETE_KEY, id)

        val streetNumber =
            response.placeResult?.addressComponents?.find { addressComponentsResponse ->
                "street_number" in addressComponentsResponse.types
            }?.longName
        val fullAddress =
            response.placeResult?.addressComponents?.find { addressComponentsResponse ->
                "route" in addressComponentsResponse.types
            }?.longName
        val city = response.placeResult?.addressComponents?.find { addressComponentsResponse ->
            "locality" in addressComponentsResponse.types
        }?.longName
        val state = response.placeResult?.addressComponents?.find { addressComponentsResponse ->
            "administrative_area_level_2" in addressComponentsResponse.types
        }?.longName
        val country = response.placeResult?.addressComponents?.find { addressComponentsResponse ->
            "country" in addressComponentsResponse.types
        }?.longName
        val zipCode = response.placeResult?.addressComponents?.find { addressComponentsResponse ->
            "postal_code" in addressComponentsResponse.types
        }?.longName

        //val latLng = "${response.placeResult?.geometry?.location?.lng.toString()}, ${response.placeResult?.geometry?.location?.lng.toString()}"

        val lat = response.placeResult?.geometry?.location?.lat
        val lng = response.placeResult?.geometry?.location?.lng

        return AddressComponentsEntity(
            streetNumber = streetNumber ?: return null,
            fullAddress = fullAddress ?: return null,
            city = city ?: return null,
            state = state ?: return null,
            zipCode = zipCode ?: return null,
            country = country ?: return null,
            lat = lat ?: return null,
            lng = lng ?: return null

        )
    }
}