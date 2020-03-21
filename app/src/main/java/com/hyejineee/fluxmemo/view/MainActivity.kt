package com.hyejineee.fluxmemo.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.RxBus
import com.hyejineee.fluxmemo.RxEvent
import com.hyejineee.fluxmemo.databinding.ActivityMainBinding
import com.hyejineee.fluxmemo.view.adapter.MemoAdapter
import com.hyejineee.fluxmemo.viewmodels.MemoViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val memoViewModel: MemoViewModel by viewModel()
    private val memosAdapter = MemoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setView()
        setEvents()

        memoViewModel

        RxBus.publish(RxEvent.SubscribeMemos())
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    fun goToWriteMemoActivity(view: View) {
        startActivity(Intent(this, WriteMemoActivity::class.java))
    }

    private fun setView() {
        setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = lifecycleOwner
            memoList.layoutManager = LinearLayoutManager(this@MainActivity)
            memoList.adapter = memosAdapter
        }
    }

    private fun setEvents() {
        RxBus.listen(RxEvent.MemosChange::class.java)
            .subscribe { (memos) -> memosAdapter.memos = memos }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.MemoClick::class.java)
            .subscribe { (memoId) ->
                val i = Intent(this, WriteMemoActivity::class.java)
                    .putExtra("memoId", memoId)
                startActivity(i)
            }.addTo(compositeDisposable)
    }
}
