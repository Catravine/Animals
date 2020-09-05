package com.carolinecourtney.animals.di

import android.widget.ListView
import com.carolinecourtney.animals.viewmodel.ListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, PrefsModule::class, AppModule::class])
interface ViewModelComponent {

    fun inject(viewModel: ListViewModel)
}