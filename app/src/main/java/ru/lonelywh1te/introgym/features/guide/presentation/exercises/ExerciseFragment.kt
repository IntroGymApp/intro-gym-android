package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.databinding.FragmentExerciseBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExecutionStepsAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.ExecutionTipsAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseFragmentViewModel


class ExerciseFragment : Fragment() {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseFragmentViewModel>()
    private val args by navArgs<ExerciseFragmentArgs>()

    private lateinit var stepsRecycler: RecyclerView
    private lateinit var tipsRecycler: RecyclerView

    private lateinit var executionStepsAdapter: ExecutionStepsAdapter
    private lateinit var executionTipsAdapter: ExecutionTipsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getExercise(args.exerciseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executionStepsAdapter = ExecutionStepsAdapter()
        executionTipsAdapter = ExecutionTipsAdapter()

        stepsRecycler = binding.rvExecutionSteps.apply {
            adapter = executionStepsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        tipsRecycler = binding.rvExecutionTips.apply {
            adapter = executionTipsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.exercise.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { exercise ->
                exercise?.let { setExerciseData(it) }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setExerciseData(exercise: Exercise) {
        Glide.with(requireContext())
            .load(AssetPath.get(AssetType.EXERCISE_ANIMATION, exercise.animFilename))
            .into(binding.ivExerciseAnimation)

        binding.tvExerciseName.text = exercise.name
        binding.tvExerciseDescription.text = exercise.description
        executionStepsAdapter.steps = exercise.steps
        executionTipsAdapter.tips = exercise.tips

        if (exercise.steps.isEmpty()) {
            binding.tvExecutionLabel.visibility = View.GONE
        }
    }
}