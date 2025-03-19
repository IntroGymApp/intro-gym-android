package ru.lonelywh1te.introgym.core.ui

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ImageLoader(private val context: Context) {
    fun load(
        uri: Uri,
        imageView: ImageView,
        crossFade: Boolean = true,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.DATA
    ) {
        var requestBuilder = Glide.with(context).load(uri).diskCacheStrategy(diskCacheStrategy)

        if (crossFade) requestBuilder = requestBuilder.transition(DrawableTransitionOptions.withCrossFade())

        requestBuilder.into(imageView)
    }
}