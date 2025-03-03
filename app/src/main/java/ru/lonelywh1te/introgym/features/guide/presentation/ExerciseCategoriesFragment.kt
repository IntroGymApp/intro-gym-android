package ru.lonelywh1te.introgym.features.guide.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.databinding.FragmentExerciseCategoriesBinding
import ru.lonelywh1te.introgym.features.guide.presentation.adapter.ExerciseCategoryAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseCategoriesViewModel

class ExerciseCategoriesFragment : Fragment() {
    private var _binding: FragmentExerciseCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseCategoriesViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ExerciseCategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExerciseCategoryAdapter()
        recycler = binding.rvExerciseCategories.apply {
            adapter = this@ExerciseCategoriesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { list ->
                    adapter.categoriesList = list
                }
            }
        }
    }

    companion object {
        private const val LOG_TAG = "ExerciseCategoriesFragment"
    }
}