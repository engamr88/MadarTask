package org.qxteam.madartask.feature.display

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.feature.display.databinding.ItemUserBinding

class UserAdapter(
    private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val context = binding.root.context

            // Initials
            val initials = user.name.trim().split(" ")
            val initialStr = if (initials.size > 1) {
                "${initials[0].firstOrNull() ?: ""}${initials[1].firstOrNull() ?: ""}"
            } else {
                "${user.name.firstOrNull() ?: ""}"
            }.uppercase()

            binding.tvAvatarInitials.text = initialStr
            binding.tvUserName.text = user.name
            binding.tvUserJob.text = user.jobTitle
            binding.tvUserAge.text = "Age: ${user.age}"
            binding.chipGender.text = user.gender

            // Colors based on gender
            val (avatarBgColor, avatarTextColor, chipTextColor) = when (user.gender.lowercase()) {
                "male" -> Triple(
                    getThemeColor(context, com.google.android.material.R.attr.colorPrimaryContainer),
                    getThemeColor(context, com.google.android.material.R.attr.colorOnPrimaryContainer),
                    getThemeColor(context, com.google.android.material.R.attr.colorPrimary)
                )
                "female" -> Triple(
                    Color.parseColor("#FFD1DF"), // Soft pink
                    Color.parseColor("#C2185B"), // Dark pink
                    Color.parseColor("#C2185B")
                )
                else -> Triple(
                    getThemeColor(context, com.google.android.material.R.attr.colorTertiaryContainer),
                    getThemeColor(context, com.google.android.material.R.attr.colorOnTertiaryContainer),
                    getThemeColor(context, com.google.android.material.R.attr.colorTertiary)
                )
            }

            binding.cardAvatar.setCardBackgroundColor(avatarBgColor)
            binding.tvAvatarInitials.setTextColor(avatarTextColor)
            binding.chipGender.setTextColor(chipTextColor)

            binding.btnDelete.setOnClickListener {
                onDeleteClick(user)
            }
        }
    }

    private fun getThemeColor(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
