package org.qxteam.madartask.feature.display

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.UserRepository
import org.qxteam.madartask.core.model.usecase.DeleteUserUseCase
import org.qxteam.madartask.core.model.usecase.GetAllUsersUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class DisplayViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase
    private lateinit var deleteUserUseCase: DeleteUserUseCase
    private lateinit var viewModel: DisplayViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        getAllUsersUseCase = GetAllUsersUseCase(fakeUserRepository)
        deleteUserUseCase = DeleteUserUseCase(fakeUserRepository)
        viewModel = DisplayViewModel(getAllUsersUseCase, deleteUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.users.isEmpty())
    }

    @Test
    fun `emits saved users list and completes loading`() = runTest(testDispatcher) {
        val users = listOf(
            User(id = 1, name = "Alice", age = 28, jobTitle = "Doctor", gender = "Female"),
            User(id = 2, name = "Bob", age = 32, jobTitle = "Designer", gender = "Male")
        )
        
        val job = launch {
            viewModel.uiState.collect {}
        }
        
        fakeUserRepository.emitUsers(users)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.users.size)
        assertEquals("Alice", state.users[0].name)
        assertEquals("Bob", state.users[1].name)
        
        job.cancel()
    }

    @Test
    fun `deleteUser invokes deleteUserUseCase`() = runTest(testDispatcher) {
        val userToDelete = User(id = 1, name = "Alice", age = 28, jobTitle = "Doctor", gender = "Female")
        
        viewModel.deleteUser(userToDelete)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(1, fakeUserRepository.deletedUsers.size)
        assertEquals(userToDelete, fakeUserRepository.deletedUsers[0])
    }

    private class FakeUserRepository : UserRepository {
        private val usersFlow = MutableSharedFlow<List<User>>(replay = 1)
        val deletedUsers = mutableListOf<User>()

        fun emitUsers(users: List<User>) {
            usersFlow.tryEmit(users)
        }

        override fun getAllUsers(): Flow<List<User>> {
            return usersFlow
        }

        override suspend fun saveUser(user: User) {
            // Unused in this test
        }

        override suspend fun deleteUser(user: User) {
            deletedUsers.add(user)
        }
    }
}
