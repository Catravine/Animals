package com.carolinecourtney.animals.di

import com.carolinecourtney.animals.model.AnimalApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: AnimalApiService)
}