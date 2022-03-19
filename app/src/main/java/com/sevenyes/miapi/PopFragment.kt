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
import com.sevenyes.miapi.adapter.PopAdapter
import com.sevenyes.miapi.databinding.FragmentPopBinding
import com.sevenyes.miapi.network.RetroFItService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class PopFragment : Fragment() {


    private val binding by lazy {
        FragmentPopBinding.inflate(layoutInflater)
    }

    private val popAdapter by lazy {
        PopAdapter(onPopClick = {
            Intent(Intent.ACTION_VIEW).apply {
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

        binding.rvPop.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = popAdapter
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        RetroFItService.retrofit.getPop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    popAdapter.updatePop(it.tracks)
                },
                {
                    Log.d("TAG", "onResume: ${it.printStackTrace()}")
                }
            ).also {
                disposable.add(it)
            }

    }
}