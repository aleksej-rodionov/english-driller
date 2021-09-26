package space.rodionov.englishdriller.ui;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.Locale;

import space.rodionov.englishdriller.R;
import space.rodionov.englishdriller.feature_words.domain.model.Word;

public class JavaDrillerAdapter extends ListAdapter<Word, JavaDrillerAdapter.JavaDrillerViewHolder> {

//    private NativeLanguage nativeLanguage;
    private Boolean nativToForeign;

    Context context;
    private TextToSpeech mTTS;

    public void setNativToForeign(Boolean nativToForeign) {
        this.nativToForeign = nativToForeign;
    }

    protected JavaDrillerAdapter(@NonNull DiffUtil.ItemCallback diffCallback, Boolean nativToForeign, Context context) {
        super(diffCallback);
        this.nativToForeign = nativToForeign;
        this.context = context;
    }

    public class JavaDrillerViewHolder extends CardStackView.ViewHolder {
        public TextView tvUpper;
        public TextView tvDowner;
        public CardView btnSpeak;

        public JavaDrillerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUpper = itemView.findViewById(R.id.tv_upper);
            tvDowner = itemView.findViewById(R.id.tv_downer);
            btnSpeak = itemView.findViewById(R.id.btn_speak);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != CardStackView.NO_POSITION) {
                    tvDowner.setVisibility(View.VISIBLE);
                    if (btnSpeak.getVisibility() == View.INVISIBLE) {
                        btnSpeak.setVisibility(View.VISIBLE);
                        btnSpeak.setEnabled(true);
                    }
                }
            });

            btnSpeak.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != CardStackView.NO_POSITION) {
                    if (!nativToForeign) {
                        speak(tvUpper);
                    } else if (nativToForeign) {
                        speak(tvDowner);
                    }
                }
            });

            mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTTS.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "Language not supported");
                        } else {
                            btnSpeak.setEnabled(true);
                        }
                    } else {
                        Log.e("TTS", "Initialization failed");
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public JavaDrillerAdapter.JavaDrillerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardstack_item, parent, false);
        JavaDrillerAdapter.JavaDrillerViewHolder javaDrillerViewHolder = new JavaDrillerAdapter.JavaDrillerViewHolder(view);
        return javaDrillerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JavaDrillerAdapter.JavaDrillerViewHolder holder, int position) {
        Word currentWord = getItem(position);

        if (!nativToForeign) {
            holder.tvUpper.setText(currentWord.getForeign());
                holder.tvDowner.setText(currentWord.getRus());
        } else if (nativToForeign) {
                holder.tvUpper.setText(currentWord.getRus());
            holder.tvDowner.setText(currentWord.getForeign());
            holder.btnSpeak.setVisibility(View.INVISIBLE);
            holder.btnSpeak.setEnabled(false);
        }
        holder.tvDowner.setVisibility(View.INVISIBLE);
    }

    public Word getWordAt(int position) {
        return getItem(position);
    }

    static class JavaDrillerDiff extends DiffUtil.ItemCallback<Word> {
        @Override
        public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
            return oldItem.getRus().equals(newItem.getRus()) && oldItem.getForeign().equals(newItem.getForeign());
        }
    }

    private void speak(TextView tv) {
        String text = tv.getText().toString();
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}

