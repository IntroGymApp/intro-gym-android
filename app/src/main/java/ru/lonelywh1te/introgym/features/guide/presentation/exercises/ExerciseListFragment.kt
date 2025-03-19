package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import ru.lonelywh1te.introgym.databinding.FragmentExerciseListBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseFilterFragment.Companion.FILTER_RESULT_BUNDLE_KEY
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExerciseListAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseListFragmentViewModel

class ExerciseListFragment : Fragment(), MenuProvider {
    private var _binding: FragmentExerciseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseListFragmentViewModel>()
    private val args by navArgs<ExerciseListFragmentArgs>()

    private val isPickMode get() = args.isPickMode
    private val callerFragmentId get() = args.callerFragmentId

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getExerciseItems(args.categoryId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseListBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExerciseListAdapter(isPickMode).apply {
            setOnItemClickListener { exercise ->
                if (isPickMode) {
                    setFragmentResult(PICK_REQUEST_KEY, bundleOf(PICK_RESULT_BUNDLE_KEY to exercise.id))
                    findNavController().popBackStack(callerFragmentId, false)
                } else {
                    navigateToExerciseFragment(exercise.id, exercise.name)
                }
            }
        }
        recycler = binding.rvExerciseList.apply {
            adapter = this@ExerciseListFragment.adapter
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

    private fun setExerciseListState(exerciseList: List<ExerciseItem>, isFilter: Boolean) {
        binding.tvListLabel.text = if (isFilter) getString(R.string.label_search_results) else getString(R.string.label_exercises)
        binding.groupNoResult.visibility = if (exerciseList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun startCollectFlows() {
        viewModel.exerciseItems.flowWithLifecycle(lifecycle)
            .onEach { list ->
                adapter.exerciseList = list

                val isFilter = !(viewModel.searchQuery.value.isEmpty() && viewModel.selectedTagsIds.value.isEmpty())
                setExerciseListState(list, isFilter)
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToFilterFragment() {
        val selectedTagsIds = viewModel.selectedTagsIds.value.toIntArray()

        val action = ExerciseListFragmentDirections.toExerciseFilterFragment(selectedTagsIds)
        findNavController().safeNavigate(action)
    }

    private fun navigateToExerciseFragment(exerciseId: Long, label: String){
        val action = ExerciseListFragmentDirections.toExerciseFragment(exerciseId, label)
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

    companion object {
        const val PICK_REQUEST_KEY = "EXERCISE_PICK_REQUEST"
        const val PICK_RESULT_BUNDLE_KEY = "EXERCISE_PICK_RESULT"
    }
}