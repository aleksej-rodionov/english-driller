package space.rodionov.englishdriller.feature_words.domain.model
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "word_table")
@Parcelize
data class Word( // переделал val на var, но только потому что иначе Студио ругался что нет сеттеров и не запускал проект
    val rus: String,
    val foreign: String,
    val category: Int,
    val shown: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable


@Entity(tableName = "word1_table")
@Parcelize
data class Word1( // переделал val на var, но только потому что иначе Студио ругался что нет сеттеров и не запускал проект
    val rus: String,
    val foreign: String,
    val catName: String,
    val shown: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable

