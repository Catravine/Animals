package com.carolinecourtney.animals

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carolinecourtney.animals.di.AppModule
import com.carolinecourtney.animals.di.DaggerViewModelComponent
import com.carolinecourtney.animals.model.Animal
import com.carolinecourtney.animals.model.AnimalApiService
import com.carolinecourtney.animals.model.ApiKey
import com.carolinecourtney.animals.util.SharedPreferencesHelper
import com.carolinecourtney.animals.viewmodel.ListViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

class ListViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var animalService: AnimalApiService

    @Mock
    lateinit var prefs: SharedPreferencesHelper

    val application = Mockito.mock(Application::class.java)

    var listViewModel = ListViewModel(application, true)

    private val key = "Test Key"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        DaggerViewModelComponent.builder()
            .appModule(AppModule(application))
            .apiModule(ApiModuleTest(animalService))
            .prefsModule(PrefsModuleTest(prefs))
            .build()
            .inject(listViewModel)
    }

    @Test
    fun getAnimalsSuccess() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val animal = Animal("cow", null, null, null, null, null, null)
        val animalList = listOf(animal)

        val testSingle = Single.just(animalList)

        Mockito.`when`(animalService.getAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1, listViewModel.animals.value?.size)
        Assert.assertEquals(false, listViewModel.loadError.value)
        Assert.assertEquals(false, listViewModel.loading.value)
    }

    @Test
    fun getAnimalsFailure() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val testSingle = Single.error<List<Animal>>(Throwable())
        val keySingle = Single.just(ApiKey("Okay", key))

        Mockito.`when`(animalService.getAnimals(key)).thenReturn(testSingle)
        Mockito.`when`(animalService.getApiKey()).thenReturn(keySingle)

        listViewModel.refresh()

        Assert.assertEquals(null, listViewModel.animals.value)
        Assert.assertEquals(false, listViewModel.loading.value)
        Assert.assertEquals(true, listViewModel.loadError.value)
    }

    @Test
    fun keySuccess() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val apiKey = ApiKey("OK", key)
        val keySingle = Single.just(apiKey)

        Mockito.`when`(animalService.getApiKey()).thenReturn(keySingle)

        val animal = Animal("cow", null, null, null, null, null, null)
        val animalList = listOf(animal)

        val testSingle = Single.just(animalList)
        Mockito.`when`(animalService.getAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1, listViewModel.animals.value?.size)
        Assert.assertEquals(false, listViewModel.loadError.value)
        Assert.assertEquals(false, listViewModel.loading.value)
    }

    @Test
    fun getKeyFailure() {
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val keySingle = Single.error<ApiKey>(Throwable())

        Mockito.`when`(animalService.getApiKey()).thenReturn(keySingle)

        listViewModel.refresh()

        Assert.assertEquals(null, listViewModel.animals.value)
        Assert.assertEquals(false, listViewModel.loading.value)
        Assert.assertEquals(true, listViewModel.loadError.value)
    }

    @Before
    fun setupRxSchedulers() {
        val immediate = object: Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, true)
            }
        }

        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
    }
}