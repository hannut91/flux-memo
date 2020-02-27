package com.hyejineee.fluxmemo.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyejineee.fluxmemo.ActionType
import com.hyejineee.fluxmemo.Actions
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
        getMemos()

        Dispatcher.dispatch(Actions(ActionType.GET_MEMOS,""))
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    fun goToWriteMemoActivity(view: View) {
        startActivity(Intent(this, WriteMemoActivity::class.java))
    }

    private fun goToMemoDetailActivity(memoId: Long) {
        val i = Intent(this, WriteMemoActivity::class.java)
            .putExtra("memoId", memoId)
        startActivity(i)

    }

    private fun setView() {
        setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = lifecycleOwner
            memoList.layoutManager = LinearLayoutManager(this@MainActivity)
            memoList.adapter = memosAdapter
        }
    }

    private fun getMemos() {
        memoViewModel.onMemosChange
            .subscribe { memosAdapter.memos = it }
            .addTo(compositeDisposable)
    }
}