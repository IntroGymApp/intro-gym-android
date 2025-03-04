package ru.lonelywh1te.introgym.features.guide.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.databinding.FragmentExerciseBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.presentation.adapter.ExecutionStepsAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.adapter.ExecutionTipsAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseFragmentViewModel


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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exercise.collect { exercise ->
                    exercise?.let { setExerciseData(it) }
                }
            }
        }
    }

    private fun setExerciseData(exercise: Exercise) {
        Glide.with(requireContext())
            .load(AssetPath.get(AssetType.EXERCISE_ANIMATION, exercise.animFilename))
            .into(binding.ivExerciseAnimation)

        binding.tvExerciseName.text = exercise.name
        binding.tvExerciseDescription.text = exercise.description
        executionStepsAdapter.steps = exercise.steps
        executionTipsAdapter.tips = exercise.tips
    }
}