package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ActivityAiplayview
import com.example.myapplication.ActivityTamaguchiview
import com.example.myapplication.ActivityTwoplayerview
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) { textView.text = it }

        // 버튼 클릭 리스너 설정
        // 이 코드가 있어야 프래그먼트 전환후에도 버튼이 동작함
        setupButtons()

        return root
    }

    // 버튼 클릭 리스너 설정 메서드
    private fun setupButtons() {
        binding.button1.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityTamaguchiview::class.java))
        }

        binding.button2.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityTwoplayerview::class.java))
        }

        binding.button3.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityAiplayview::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}