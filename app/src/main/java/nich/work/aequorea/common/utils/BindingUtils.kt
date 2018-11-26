package nich.work.aequorea.common.utils

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import nich.work.aequorea.data.entity.Author

@BindingAdapter("image")
fun setImage(view: ImageView, imageUrl: String?) {
    imageUrl?.let { ImageHelper.loadImage(view.context, imageUrl, view) }
}

@BindingAdapter("roundImage")
fun setRoundImage(view: ImageView, imageUrl: String?) {
    ImageHelper.loadImage(view.context, imageUrl, view, true)
}

@BindingAdapter("image", "placeHolder")
fun setImage(view: ImageView, imageUrl: String?, placeHolder: Drawable) {
    ImageHelper.loadImage(view.context, imageUrl, view, placeHolder)
}

@BindingAdapter("authorClickListener")
fun setAuthorListener(view: View, author: Author?) {
    author?.let { _ -> view.setOnClickListener { IntentUtils.startAuthorActivity(view.context, view, author) } }
}

@BindingAdapter("visible")
fun setVisibility(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("visibleIfExist")
fun setVisibility(view: View, o: Any?) {
    if (o != null) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("visibleIfNotExist")
fun adjustVisibility(view: View, o: Any?) {
    if (o == null) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

