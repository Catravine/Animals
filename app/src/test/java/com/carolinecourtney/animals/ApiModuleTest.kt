package com.carolinecourtney.animals

import com.carolinecourtney.animals.di.ApiModule
import com.carolinecourtney.animals.model.AnimalApiService

class ApiModuleTest(val mockService: AnimalApiService): ApiModule() {
    override fun provideAnimalApiService(): AnimalApiService {
        return mockService
    }
}