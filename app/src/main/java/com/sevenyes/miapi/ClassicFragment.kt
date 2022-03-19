package com.sevenyes.miapi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sevenyes.miapi.adapter.ClassicAdapter
import com.sevenyes.miapi.databinding.FragmentClassicBinding
import com.sevenyes.miapi.network.RetroFItService
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ClassicFragment : Fragment() {

    private val binding by lazy {
        FragmentClassicBinding.inflate(layoutInflater)
    }
    private val classicAdapter by lazy {
        ClassicAdapter(onClassicTrackClick = {
            Intent(android.content.Intent.ACTION_VIEW).apply {
               setDataAndType(Uri.parse(it.previewUrl), "audio/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(this)
            }
        })
    }
    private var disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding.rvClassic.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = classicAdapter
        }

        binding.RefreshClassicFragment.setOnRefreshListener {

            Toast.makeText(requireContext(), "KK", Toast.LENGTH_SHORT).show()
            reload()

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        reload()

    }

    fun reload (){
        RetroFItService.retrofit.getClassic()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    classicAdapter.updateClassic(it.tracks)
                    binding.RefreshClassicFragment.isRefreshing=false
                },
                {
                    Log.d("TAG", "onResume: ${it.printStackTrace()}")
                }
            ).also {
                disposable.add(it)
            }
    }
    private fun SwipeRefreshLayout() {

    }

}