package com.a2l.software.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.a2l.software.myapplication.databinding.FragmentImageBinding
import com.a2l.software.myapplication.model.ImageEntity
import com.bumptech.glide.Glide

class ImageFragment : Fragment() {

    companion object {
        fun instance(entity: ImageEntity): ImageFragment {
            val fragment = ImageFragment()
            val bundle = Bundle()
            bundle.putSerializable("abc", entity)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentImageBinding
    private var entity: ImageEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey("abc")) {
                entity = it.getSerializable("abc") as? ImageEntity
            }
        }
        entity?.let {
            Glide
                .with(requireContext())
                .load(Uri.parse(it.path))
                .centerCrop()
                .into(binding.image)
        }
    }
}