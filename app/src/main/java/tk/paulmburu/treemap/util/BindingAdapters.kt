package tk.paulmburu.treemap.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("arboristDetails")
fun bindTextView(textView: TextView, title: String?){
    title.let { textView.setText(it) }
}

