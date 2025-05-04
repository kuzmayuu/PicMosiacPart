package com.android.picmosaic

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

class CollageAdapter(private val imageUris: List<Uri>) : RecyclerView.Adapter<CollageAdapter.CollageViewHolder>() {

    private var borderWidth = 8
    private var cornerRadius = 0
    private val transformations = mutableMapOf<Int, ImageTransformation>()

    data class ImageTransformation(
        val scaleFactor: Float,
        val translationX: Float,
        val translationY: Float
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_collage_image, parent, false)
        return CollageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollageViewHolder, position: Int) {
        val uri = imageUris[position]

        // Create a drawable with the specified corner radius
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadius = this@CollageAdapter.cornerRadius.toFloat()
        }

        // Set padding as border
        holder.imageView.setPadding(
            borderWidth,
            borderWidth,
            borderWidth,
            borderWidth
        )

        holder.imageView.background = drawable
        holder.imageView.clipToOutline = true

        // Load image with Glide maintaining aspect ratio
        Glide.with(holder.imageView.context)
            .load(uri)
            .apply(RequestOptions().centerCrop()) // Ensures consistent crop behavior
            .into(holder.imageView)

        // Setup pinch-to-zoom
        holder.setupPinchToZoom(position)
    }

    override fun getItemCount(): Int = imageUris.size

    fun setBorderProperties(width: Int, radius: Int) {
        borderWidth = width
        cornerRadius = radius
        notifyDataSetChanged()
    }

    fun getTransformations(): Map<Int, ImageTransformation> = transformations

    class CollageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.collageImage)
        private var scaleGestureDetector: ScaleGestureDetector? = null
        private var scaleFactor = 1.0f
        private var lastTouchX = 0f
        private var lastTouchY = 0f
        private var matrix = Matrix()
        private var translationX = 0f
        private var translationY = 0f

        fun setupPinchToZoom(position: Int) {
            scaleGestureDetector = ScaleGestureDetector(itemView.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f) // Limit scale between 0.5x and 3x
                    
                    matrix.setScale(scaleFactor, scaleFactor)
                    matrix.postTranslate(translationX, translationY)
                    imageView.imageMatrix = matrix
                    return true
                }
            })

            imageView.setOnTouchListener { _, event ->
                scaleGestureDetector?.onTouchEvent(event)

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchX = event.x
                        lastTouchY = event.y
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = event.x - lastTouchX
                        val deltaY = event.y - lastTouchY
                        
                        translationX += deltaX
                        translationY += deltaY
                        
                        matrix.setScale(scaleFactor, scaleFactor)
                        matrix.postTranslate(translationX, translationY)
                        imageView.imageMatrix = matrix
                        
                        lastTouchX = event.x
                        lastTouchY = event.y
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        // Store the transformation when touch is released
                        (itemView.context as? EditCollageActivity)?.let { activity ->
                            activity.updateImageTransformation(
                                adapterPosition,
                                ImageTransformation(scaleFactor, translationX, translationY)
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
}