package ru.lonelywh1te.introgym.features.guide.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.databinding.FragmentExerciseCategoriesBinding
import ru.lonelywh1te.introgym.features.guide.presentation.adapter.ExerciseCategoryAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.adapter.ExerciseListAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseCategoriesFragmentViewModel

class ExerciseCategoriesFragment : Fragment() {
    private var _binding: FragmentExerciseCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseCategoriesFragmentViewModel>()

    private lateinit var recycler: RecyclerView

    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private lateinit var exerciseCategoryAdapter: ExerciseCategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseListAdapter = ExerciseListAdapter().apply {
            setOnItemClickListener { exerciseId ->
                navigateToExerciseFragment(exerciseId)
            }
        }

        exerciseCategoryAdapter = ExerciseCategoryAdapter().apply {
            setOnItemClickListener { categoryId ->
                navigateToExerciseListFragment(categoryId)
            }
        }

        recycler = binding.rvExerciseCategories.apply {
            adapter = exerciseCategoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        setOnChangeSearchTextListener()
        startCollectFlows()
    }

    private fun setOnChangeSearchTextListener() {
        binding.etSearchExercise.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    binding.tvListLabel.text = getString(R.string.label_exercise_categories)

                    recycler.adapter = exerciseCategoryAdapter
                } else {
                    binding.tvListLabel.text = getString(R.string.label_search_results)

                    recycler.adapter = exerciseListAdapter
                    viewModel.searchExercisesByName(s.toString())
                }
            }
        })
    }

    private fun startCollectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { list ->
                    exerciseCategoryAdapter.categoriesList = list
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchExercisesResult.collect { result ->
                    exerciseListAdapter.exerciseList = result
                }
            }
        }
    }

    private fun navigateToExerciseListFragment(categoryId: Long) {
        val action = ExerciseCategoriesFragmentDirections.toExerciseListFragment(categoryId)
        findNavController().navigate(action)
    }

    private fun navigateToExerciseFragment(categoryId: Long) {
        val action = ExerciseCategoriesFragmentDirections.toExerciseFragment(categoryId)
        findNavController().navigate(action)
    }

    companion object {
        private const val LOG_TAG = "ExerciseCategoriesFragment"
    }
}