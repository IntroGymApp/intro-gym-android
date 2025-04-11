package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.databinding.FragmentExerciseCategoriesBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseFilterFragment.Companion.FILTER_RESULT_BUNDLE_KEY
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment.Companion.PICK_REQUEST_KEY
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment.Companion.PICK_RESULT_BUNDLE_KEY
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExerciseCategoryAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExerciseListAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseCategoriesFragmentViewModel

class ExerciseCategoriesFragment : Fragment(), MenuProvider {
    private var _binding: FragmentExerciseCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseCategoriesFragmentViewModel>()
    private val args by navArgs<ExerciseCategoriesFragmentArgs>()

    private lateinit var recycler: RecyclerView

    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private lateinit var exerciseCategoryAdapter: ExerciseCategoryAdapter

    private val isPickMode get() = args.isPickMode
    private val callerFragmentId get() = args.callerFragmentId

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseCategoriesBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseListAdapter = ExerciseListAdapter(isPickMode).apply {
            setOnItemClickListener { exercise ->
                if (isPickMode) {
                    navigateToCallerFragment(exercise.id)
                } else {
                    navigateToExerciseFragment(exercise.id)
                }
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

        binding.etSearchExercise.addTextChangedListener { query ->
            viewModel.updateSearchQuery(query.toString())
        }

        setFragmentResultListener(ExerciseFilterFragment.FILTER_REQUEST_KEY) { _, bundle ->
            val selectedTagsIds = bundle.getIntArray(FILTER_RESULT_BUNDLE_KEY)?.toList() ?: emptyList()
            viewModel.updateSelectedTags(selectedTagsIds)
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.categories.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { list ->
                exerciseCategoryAdapter.update(list)
            }
            .launchIn(lifecycleScope)

        viewModel.exerciseList.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { result ->
                exerciseListAdapter.update(result)

                if (hasActiveSearchOrFilters()) {
                    showFilteredExercises(result)
                } else {
                    showExerciseCategories()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun hasActiveSearchOrFilters(): Boolean {
        return viewModel.searchQuery.value.isNotBlank() || viewModel.selectedTagsIds.value.isNotEmpty()
    }

    private fun showExerciseCategories() {
        binding.tvListLabel.text = getString(R.string.label_exercise_categories)
        recycler.adapter = exerciseCategoryAdapter
    }

    private fun showFilteredExercises(exerciseList: List<ExerciseItem>) {
        binding.tvListLabel.text = getString(R.string.label_search_results)
        recycler.adapter = exerciseListAdapter
        binding.groupNoResult.visibility = if (exerciseList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun navigateToCallerFragment(exerciseId: Long) {
        setFragmentResult(
            PICK_REQUEST_KEY,
            bundleOf(PICK_RESULT_BUNDLE_KEY to exerciseId)
        )

        findNavController().popBackStack(callerFragmentId, false)
    }

    private fun navigateToFilterFragment() {
        val selectedTagsIds = viewModel.selectedTagsIds.value.toIntArray()

        val action = ExerciseCategoriesFragmentDirections.toExerciseFilterFragment(selectedTagsIds)
        findNavController().safeNavigate(action)
    }

    private fun navigateToExerciseListFragment(categoryId: Long, label: String) {
        val action = ExerciseCategoriesFragmentDirections.toExerciseListFragment(categoryId, label, isPickMode, callerFragmentId)
        findNavController().safeNavigate(action)
    }

    private fun navigateToExerciseFragment(categoryId: Long) {
        val action = ExerciseCategoriesFragmentDirections.toExerciseFragment(categoryId)
        findNavController().safeNavigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.filter -> navigateToFilterFragment()
        }

        return true
    }
}