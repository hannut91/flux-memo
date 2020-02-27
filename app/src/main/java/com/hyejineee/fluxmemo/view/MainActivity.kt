package com.hyejineee.fluxmemo.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyejineee.fluxmemo.ActionType
import com.hyejineee.fluxmemo.Action
import com.hyejineee.fluxmemo.Dispatcher
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.databinding.ActivityMainBinding
import com.hyejineee.fluxmemo.view.adapter.MemoAdapter
import com.hyejineee.fluxmemo.viewmodels.MemoViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val memoViewModel: MemoViewModel by viewModel()
    private val memosAdapter = MemoAdapter(::goToMemoDetailActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setView()
        setEvents()

        Dispatcher.dispatch(Action(ActionType.GET_MEMOS,""))
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    fun goToWriteMemoActivity(view: View) {
        goToMemoDetailActivity(-1)
    }

    private fun goToMemoDetailActivity(memoId: Long) {
        Dispatcher.dispatch(Action(ActionType.GET_MEMO, memoId))
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
        memoViewModel.onMemosChange
            .subscribe { memosAdapter.memos = it }
            .addTo(compositeDisposable)
    }
}
