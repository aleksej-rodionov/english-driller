package space.rodionov.englishdriller.data
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

// part 2
@Entity(tableName = "word_table")
@Parcelize
data class Word( // переделал val на var, но только потому что иначе Студио ругался что нет сеттеров и не запускал проект
    val rus: String,
    val foreign: String,
    val category: Int,
    var shown: Boolean = true,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
) : Parcelable {
}