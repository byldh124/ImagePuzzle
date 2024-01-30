package com.moondroid.imagepuzzle.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

object ItemHelper {
    var itemList: List<String> = emptyList()

    fun getUrls(stage: Int) =
        try {
            "http://moondroid.dothome.co.kr/wordcomplete/image/${itemList[stage]}.png"
        } catch (e: Exception) {
            logException(e)
            ""
        }

    fun divide(bitmap: Bitmap, block: Int): Array<Bitmap?> {
        val bitmaps = arrayOfNulls<Bitmap>(9)

        val width: Int = bitmap.width
        val height: Int = bitmap.height

        val widthUnit: Int = width / block
        val heightUnit: Int = height / block

        for (i in 0 until block) {
            for (j in 0 until block) {
                bitmaps[block * i + j] =
                    Bitmap.createBitmap(bitmap, widthUnit * j, heightUnit * i, widthUnit, heightUnit)
            }
        }
        return bitmaps
    }

    fun getBitmapFromUrl(stage: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val url = URL(getUrls(stage))
            val connection = url.openConnection() as HttpURLConnection
            debug("url : $url")
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            bitmap = BitmapFactory.decodeStream(input)
            connection.disconnect()
        } catch (e: Exception) {
            logException(e)
        }
        return bitmap
    }

    private val rooms = arrayOf(
        intArrayOf(4, 6, 8, 7, 9, 2, 5, 3),
        intArrayOf(2, 6, 3, 7, 9, 5, 8, 4),
        intArrayOf(5, 7, 9, 2, 4, 6, 3, 8),
        intArrayOf(4, 8, 9, 2, 7, 6, 5, 3),
        intArrayOf(5, 6, 3, 9, 7, 8, 4, 2),
        intArrayOf(6, 8, 3, 2, 9, 4, 7, 5),
        intArrayOf(7, 3, 4, 2, 9, 5, 6, 8),
        intArrayOf(3, 7, 9, 8, 5, 4, 6, 2),
        intArrayOf(9, 5, 4, 2, 3, 7, 8, 6)
    )

    fun getRandomRoom(): IntArray = rooms[Random.nextInt(9)]
}

data class PuzzleItem(
    var bitmap: Bitmap? = null,
    var index: Int = 0,
)