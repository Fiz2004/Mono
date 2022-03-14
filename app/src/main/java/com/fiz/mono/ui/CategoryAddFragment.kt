package com.fiz.mono.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCategoryAddBinding

class CategoryAddFragment : Fragment() {
    private var _binding: FragmentCategoryAddBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: IconCategoryAdapter

    var selectedItem: Int? = null

    val list = mutableListOf(
        R.drawable.user, R.drawable.plane, R.drawable.chair, R.drawable.baby,
        R.drawable.bank, R.drawable.gym, R.drawable.cycles, R.drawable.bird,
        R.drawable.boat, R.drawable.books, R.drawable.brain, R.drawable.building,
        R.drawable.birthday, R.drawable.camera, R.drawable.car, R.drawable.cat,
        R.drawable.study, R.drawable.coffee, R.drawable.coin, R.drawable.pie,
        R.drawable.cook, R.drawable.coin, R.drawable.dog, R.drawable.facemask,
        R.drawable.medican, R.drawable.flower, R.drawable.dinner, R.drawable.gas,
        R.drawable.gift, R.drawable.bag, R.drawable.challenge, R.drawable.music,
        R.drawable.house, R.drawable.map, R.drawable.glass, R.drawable.money,
        R.drawable.package1, R.drawable.run, R.drawable.pill, R.drawable.food,
        R.drawable.`fun`, R.drawable.receipt, R.drawable.lawer, R.drawable.market,
        R.drawable.shower, R.drawable.football, R.drawable.store, R.drawable.study,
        R.drawable.tennis_ball, R.drawable.wc, R.drawable.train, R.drawable.cup,
        R.drawable.clothes, R.drawable.wallet, R.drawable.sea, R.drawable.party,
        R.drawable.fix
    )

    val args: CategoryAddFragmentArgs by navArgs()
    var type: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryAddBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = args.type

        adapter = IconCategoryAdapter { position ->

            selectedItem?.let { adapter.notifyItemChanged(it) }
            adapter.notifyItemChanged(position)
            if (selectedItem == position) {
                selectedItem = null
                binding.addButton.visibility = View.GONE
            } else {
                selectedItem = position
                binding.addButton.visibility = View.VISIBLE
            }

            adapter.selectedItem = selectedItem
        }
        adapter.submitList(list)
        binding.expenseRecyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addButton.setOnClickListener {
            if (selectedItem != null && binding.categoryNameEditText.text?.isNotBlank() == true) {

                val action =
                    CategoryAddFragmentDirections
                        .actionCategoryAddFragmentToCategoryFragment(
                            binding.categoryNameEditText.text.toString(),
                            list[selectedItem!!],
                            type
                        )
                view.findNavController().navigate(action)
                return@setOnClickListener
            }
        }

    }

}