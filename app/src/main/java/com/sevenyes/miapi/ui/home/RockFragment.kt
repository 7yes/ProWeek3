package com.sevenyes.miapi.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sevenyes.miapi.adapter.RockAdapter
import com.sevenyes.miapi.databinding.FragmentRockBinding
import com.sevenyes.miapi.network.RetroFItService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RockFragment : Fragment(){

    private var _binding: FragmentRockBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val rockAdapter =  RockAdapter(onRockClick = {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(it.previewUrl), "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(this)
        }
    })
    private var disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRockBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvRock.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false )
            adapter = rockAdapter
        }
        return root
    }

    override fun onResume() {
        super.onResume()
            RetroFItService.retrofit.getRock()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        rockAdapter.updateRock(it.tracks)
                    },
                    {
                        Log.d("TAG", "onResume: ${it.printStackTrace()}")
                    }
                ).also {
                    disposable.add(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}