package space.rodionov.englishdriller.feature_words.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
data class Category(
    val categoryNameRus: String,
    val categoryNumber: Int,
    val categoryShown: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
}