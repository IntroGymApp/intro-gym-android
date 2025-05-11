package ru.lonelywh1te.introgym.core.ui.utils

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import ru.lonelywh1te.introgym.R

class ItemTouchHelperCallback(dragDirs: Int, swipeDirs: Int): ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    private var fromPosition: Int? = null

    private var onMove: ((from: Int, to: Int) -> Unit)? = null
    private var onMoveFinished: ((from: Int, to: Int) -> Unit)? = null
    private var onLeftSwipe: ((position: Int) -> Unit)? = null
    private var onRightSwipe: ((position: Int) -> Unit)? = null

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        if (fromPosition == null) fromPosition = from

        onMove?.invoke(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            onLeftSwipe?.invoke(viewHolder.adapterPosition)
        } else if (direction == ItemTouchHelper.RIGHT) {
            onRightSwipe?.invoke(viewHolder.adapterPosition)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val from = fromPosition
        val to = viewHolder.adapterPosition

        from?.let {
            if (from < 0 || to < 0) return

            onMoveFinished?.invoke(from, to)
            fromPosition = null
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val cornerRadius = (10).dp().toFloat()

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {

                // Фон

                val rect = RectF(
                    itemView.right.toFloat() + dX - (itemView.width / 2),
                    itemView.top.toFloat() + itemView.paddingTop,
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat() - itemView.paddingBottom
                )
                val paint = Paint().apply {
                    color = MaterialColors.getColor(itemView.context, R.attr.igErrorColor, Color.RED)
                    isAntiAlias = true
                }

                c.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

                // Иконка

                val icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete)
                val iconSize = icon?.intrinsicHeight ?: 0

                val iconRightMargin = 30.dp()
                val iconTop = rect.centerY() - iconSize
                val iconLeft = itemView.right - iconSize - iconRightMargin
                val iconRight = itemView.right - iconRightMargin

                icon?.setBounds(iconLeft, iconTop.toInt(), iconRight, (iconTop + iconSize).toInt())
                icon?.setTint(MaterialColors.getColor(itemView.context, R.attr.igBackgroundColor, Color.WHITE))
                icon?.draw(c)

                // Текст

                val text = itemView.context.getString(R.string.label_delete)
                val textPaint = Paint().apply {
                    color = MaterialColors.getColor(itemView.context, R.attr.igBackgroundColor, Color.WHITE)
                    textSize = 12.dp().toFloat()
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = ResourcesCompat.getFont(itemView.context, R.font.geist_regular)
                }

                val textX = (iconLeft + iconRight) / 2f
                val textY = iconTop + iconSize + 5.dp() - textPaint.ascent()

                c.drawText(text, textX, textY, textPaint)

            } else if (dX > 0) {
                // TODO: Not yet implemented
            }

        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }

    fun setOnMoveListener(listener: ((from: Int, to: Int) -> Unit)?) {
        onMove = listener
    }

    fun setOnMoveFinishedListener(listener: ((from: Int, to: Int) -> Unit)?) {
        onMoveFinished = listener
    }

    fun setOnLeftSwipeListener(listener: ((position: Int) -> Unit)?) {
        onLeftSwipe = listener
    }

    fun setOnRightSwipeListener(listener: ((position: Int) -> Unit)?) {
        onRightSwipe = listener
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) = ItemTouchHelper(this).attachToRecyclerView(recyclerView)


    private fun Int.dp(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
    private fun Float.dp(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics).toInt()
}