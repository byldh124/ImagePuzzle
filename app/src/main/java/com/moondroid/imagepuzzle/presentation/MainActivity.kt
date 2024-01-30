package com.moondroid.imagepuzzle.presentation

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moondroid.imagepuzzle.common.ItemHelper
import com.moondroid.imagepuzzle.common.PuzzleItem
import com.moondroid.imagepuzzle.common.debug
import com.moondroid.imagepuzzle.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mContext: Context by lazy { this }

    private var stage = 0
    private lateinit var originBitmap: Bitmap

    private val adapter = ImageAdapter(::changeImage)

    private var items = ArrayList<PuzzleItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler2.layoutManager =
            GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        binding.recycler2.adapter = adapter

        setItem()

        binding.btnAnotherImage.setOnClickListener {
            stage++
            setItem()
        }

        binding.btnOriginImage.setOnClickListener {
            OriginImageDialog(mContext, originBitmap).show()
        }
    }

    private fun setItem() {
        blank = 3
        if (stage >= ItemHelper.itemList.size) {
            stage = 0
        }
        CoroutineScope(Dispatchers.IO).launch {
            ItemHelper.getBitmapFromUrl(stage)?.let { bitmap ->
                withContext(Dispatchers.Main) {
                    originBitmap = bitmap
                    val bitmaps = ItemHelper.divide(bitmap, 3)
                    val room = ItemHelper.getRandomRoom()

                    items = ArrayList()
                    items.add(PuzzleItem(bitmaps[0], 0))
                    items.add(PuzzleItem(null, 10))
                    items.add(PuzzleItem(null, 10))
                    items.add(PuzzleItem(null, 10))

                    for (i in room.indices) {
                        items.add(PuzzleItem(bitmaps[room[i] - 1], room[i]))
                    }
                    adapter.submitList(items)
                }
            }
        }

    }

    private var blank = 3

    private fun changeImage(position: Int) {
        when (position) {
            0 -> if (blank == 3) {
                swap(position)
                var isFinish = 0
                var i = 4
                while (i < 12) {
                    if (items[i].index != i - 2) break
                    isFinish++
                    i++
                }
                if (isFinish == 8) {
                    blank = 3
                    debug("finish")
                    correct()
                }
            }

            3 -> if (blank == 0 || blank == 4 || blank == 6) swap(position)
            4 -> if (blank == 3 || blank == 5 || blank == 7) swap(position)
            5 -> if (blank == 4 || blank == 8) swap(position)
            6 -> if (blank == 3 || blank == 7 || blank == 9) swap(position)
            7 -> if (blank == 4 || blank == 6 || blank == 8 || blank == 10) swap(position)
            8 -> if (blank == 5 || blank == 7 || blank == 11) swap(position)
            9 -> if (blank == 6 || blank == 10) swap(position)
            10 -> if (blank == 7 || blank == 9 || blank == 11) swap(position)
            11 -> if (blank == 8 || blank == 10) swap(position)
        }
    }

    private fun swap(position: Int) {
        Collections.swap(items, position, blank)
        blank = position
        adapter.submitList(items)
    }

    private fun correct() {
        binding.ivAnswer.isVisible = true
        Handler(Looper.getMainLooper()).postDelayed({
            binding.ivAnswer.isVisible = false
            stage++
            setItem()
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}