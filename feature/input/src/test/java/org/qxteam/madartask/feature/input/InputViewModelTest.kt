package org.qxteam.madartask.feature.input

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.UserRepository
import org.qxteam.madartask.core.model.usecase.SaveUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class InputViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var saveUserUseCase: SaveUserUseCase
    private lateinit var viewModel: InputViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        saveUserUseCase = SaveUserUseCase(fakeUserRepository)
        viewModel = InputViewModel(saveUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.age)
        assertEquals("", state.jobTitle)
        assertEquals("Male", state.gender)
        assertNull(state.nameError)
        assertNull(state.ageError)
        assertNull(state.jobTitleError)
    }

    @Test
    fun `validation fails on empty name`() = runTest(testDispatcher) {
        viewModel.onNameChange("")
        viewModel.onAgeChange("25")
        viewModel.onJobTitleChange("Engineer")
        
        viewModel.saveUser()
        
        val state = viewModel.uiState.value
        assertEquals("Name cannot be empty", state.nameError)
    }

    @Test
    fun `validation fails on invalid age`() = runTest(testDispatcher) {
        viewModel.onNameChange("John Doe")
        viewModel.onAgeChange("150")
        viewModel.onJobTitleChange("Engineer")
        
        viewModel.saveUser()
        
        val state = viewModel.uiState.value
        assertEquals("Please enter a valid age (1-120)", state.ageError)
    }

    @Test
    fun `validation succeeds and saves user`() = runTest(testDispatcher) {
        viewModel.onNameChange("John Doe")
        viewModel.onAgeChange("30")
        viewModel.onJobTitleChange("Developer")
        viewModel.onGenderChange("Female")
        
        viewModel.saveUser()
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.age)
        
        val users = fakeUserRepository.savedUsers
        assertEquals(1, users.size)
        assertEquals("John Doe", users[0].name)
        assertEquals(30, users[0].age)
        assertEquals("Developer", users[0].jobTitle)
        assertEquals("Female", users[0].gender)
    }

    private class FakeUserRepository : UserRepository {
        val savedUsers = mutableListOf<User>()

        override fun getAllUsers(): Flow<List<User>> {
            val flow = MutableSharedFlow<List<User>>(replay = 1)
            flow.tryEmit(savedUsers)
            return flow
        }

        override suspend fun saveUser(user: User) {
            savedUsers.add(user)
        }

        override suspend fun deleteUser(user: User) {
            // Unused in this test
        }
    }
}
