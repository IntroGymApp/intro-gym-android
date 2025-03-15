package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.databinding.FragmentExerciseCategoriesBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExerciseCategoryAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExerciseListAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseCategoriesFragmentViewModel

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
            setOnItemClickListener { exercise ->
                navigateToExerciseFragment(exercise.id, exercise.name)
            }
        }

        exerciseCategoryAdapter = ExerciseCategoryAdapter().apply {
            setOnItemClickListener { category ->
                navigateToExerciseListFragment(category.id, category.name)
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
        viewModel.categories.flowWithLifecycle(lifecycle)
            .onEach { list ->
                exerciseCategoryAdapter.categoriesList = list
            }
            .launchIn(lifecycleScope)

        viewModel.searchExercisesResult.flowWithLifecycle(lifecycle)
            .onEach { result ->
                exerciseListAdapter.exerciseList = result
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToExerciseListFragment(categoryId: Long, label: String) {
        val action = ExerciseCategoriesFragmentDirections.toExerciseListFragment(categoryId, label)
        findNavController().navigate(action)
    }

    private fun navigateToExerciseFragment(categoryId: Long, label: String) {
        val action = ExerciseCategoriesFragmentDirections.toExerciseFragment(categoryId, label)
        findNavController().navigate(action)
    }

    companion object {
        private const val LOG_TAG = "ExerciseCategoriesFragment"
    }
}