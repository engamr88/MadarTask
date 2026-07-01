package org.qxteam.madartask.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.qxteam.madartask.core.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val jobTitle: String,
    val gender: String
) {
    fun toDomainModel(): User {
        return User(
            id = id,
            name = name,
            age = age,
            jobTitle = jobTitle,
            gender = gender
        )
    }

    companion object {
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                age = user.age,
                jobTitle = user.jobTitle,
                gender = user.gender
            )
        }
    }
}
