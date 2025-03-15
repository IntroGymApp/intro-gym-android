package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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
import ru.lonelywh1te.introgym.databinding.FragmentExerciseListBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExerciseListAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseListFragmentViewModel

class ExerciseListFragment : Fragment() {
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

        startCollectFlows()
        setOnChangeSearchTextListener()
    }

    private fun setOnChangeSearchTextListener() {
        binding.etSearchExercise.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                binding.tvListLabel.text = getString(if (s.toString().isEmpty()) R.string.label_exercises else R.string.label_search_results)
                viewModel.searchExercisesByName(s.toString())
            }
        })
    }

    private fun startCollectFlows() {
        viewModel.exerciseItems.flowWithLifecycle(lifecycle)
            .onEach { list ->
                adapter.exerciseList = list
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToExerciseFragment(exerciseId: Long, label: String){
        val action = ExerciseListFragmentDirections.toExerciseFragment(exerciseId, label)
        findNavController().navigate(action)
    }

    companion object {
        const val PICK_REQUEST_KEY = "EXERCISE_PICK_REQUEST"
        const val PICK_RESULT_BUNDLE_KEY = "EXERCISE_PICK_RESULT"
    }
}