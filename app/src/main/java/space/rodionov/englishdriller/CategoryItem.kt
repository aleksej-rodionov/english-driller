package space.rodionov.englishdriller

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
data class CategoryItem(
    val categoryNameRus: String,
    val categoryNumber: Int,
    val categoryShown: Boolean = true,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
) : Parcelable {
}