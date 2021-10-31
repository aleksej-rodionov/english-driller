package space.rodionov.englishdriller.ui

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.databinding.CardstackItemBinding
import space.rodionov.englishdriller.feature_words.domain.model.Word

class DrillerAdapter : ListAdapter<Word, DrillerAdapter.DrillerViewHolder>(WordDiff()) {

    private lateinit var mTTS: TextToSpeech

    private var nativeToForeign: Boolean = false
    private var mode: Int = 0

    fun updateTransDir(nativToForeign: Boolean) { nativeToForeign = nativToForeign }
    fun updateMode(newMode: Int) { mode = newMode }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrillerViewHolder {
        val binding = CardstackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrillerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrillerViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }

    inner class DrillerViewHolder(private val binding: CardstackItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.apply {
                tvUpper.text = if(nativeToForeign) word.rus else word.foreign
                tvDowner.text = if(nativeToForeign) word.foreign else word.rus
            }
        }
    }

    //=============================CUSTOM METHODS==========================

//    private fun speak(tv: TextView) {
//        val text = tv.text.toString()
//        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null)
//    }
}





