package com.tuto.realestatemanager.domain.usecase.priceconverter

import com.tuto.realestatemanager.data.repository.priceconverterrepository.PriceConverterRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConvertPriceUseCase @Inject constructor(
    private val priceConverterRepositoryInterface: PriceConverterRepositoryInterface
) {
    fun invoke() = priceConverterRepositoryInterface.convertPrice()
}