package space.rodionov.englishdriller.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.Locale;

import space.rodionov.englishdriller.R;
import space.rodionov.englishdriller.feature_words.domain.model.Word;

public class JavaDrillerAdapter extends ListAdapter<Word, JavaDrillerAdapter.JavaDrillerViewHolder> {

    private Boolean nativToForeign;
        private int mode;
//    private LiveData<Integer> mode;
//    private LifecycleOwner owner;

    Context context;
    private TextToSpeech mTTS;

    public void setNativToForeign(Boolean nativToForeign) {
        this.nativToForeign = nativToForeign;
    }
    public void updateMode(int newMode) {
        this.mode = newMode;
//        notifyDataSetChanged();
    }

    protected JavaDrillerAdapter(@NonNull DiffUtil.ItemCallback diffCallback,
                                 Boolean nativToForeign, int mode,
                                 Context context/*, LiveData<Integer> mode,
                                 LifecycleOwner owner*/) {
        super(diffCallback);
        this.nativToForeign = nativToForeign;
        this.mode = mode;
        this.context = context;
//        this.mode = mode;
//        this.owner = owner;
    }

    public class JavaDrillerViewHolder extends CardStackView.ViewHolder {
        public TextView tvUpper;
        public TextView tvDowner;
        public CardView btnSpeak;
        public ImageView ivSpeak;
        public ImageView ivTop;
        public ImageView ivRight;
        public ImageView ivLeft;
        public ImageView ivBottom;
        public CardView card;
        public RelativeLayout rl;

        public JavaDrillerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUpper = itemView.findViewById(R.id.tv_upper);
            tvDowner = itemView.findViewById(R.id.tv_downer);
            btnSpeak = itemView.findViewById(R.id.btn_speak);
            ivSpeak = itemView.findViewById(R.id.iv_speak);
            ivTop = itemView.findViewById(R.id.iv_top);
            ivRight = itemView.findViewById(R.id.iv_right);
            ivLeft = itemView.findViewById(R.id.iv_left);
            ivBottom = itemView.findViewById(R.id.iv_bottom);
            card = itemView.findViewById(R.id.card_view);
            rl = itemView.findViewById(R.id.rl);

//            mode.observe(owner, Observer {
//
//            });

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

        if (mode == 1) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.transparentGrey));
            holder.btnSpeak.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
            holder.tvUpper.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.tvDowner.setTextColor(context.getResources().getColor(R.color.moonBlue));
            holder.ivLeft.setImageDrawable(context.getResources().getDrawable(R.drawable.repeat_practice_ru_night));
            holder.ivRight.setImageDrawable(context.getResources().getDrawable(R.drawable.repeat_practice_ru_night));
            holder.ivTop.setImageDrawable(context.getResources().getDrawable(R.drawable.repeat_practice_ru_night));
            holder.ivBottom.setImageDrawable(context.getResources().getDrawable(R.drawable.learned_ru_night));
        } else {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            holder.btnSpeak.setCardBackgroundColor(context.getResources().getColor(R.color.grey));
            holder.tvUpper.setTextColor(context.getResources().getColor(R.color.black95));
            holder.tvDowner.setTextColor(context.getResources().getColor(R.color.grey));
            holder.ivLeft.setImageDrawable(context.getResources().getDrawable(R.drawable.repeat_practice_ru));
            holder.ivRight.setImageDrawable(context.getResources().getDrawable(R.drawable.repeat_practice_ru));
            holder.ivTop.setImageDrawable(context.getResources().getDrawable(R.drawable.repeat_practice_ru));
            holder.ivBottom.setImageDrawable(context.getResources().getDrawable(R.drawable.learned_ru));
        }
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

