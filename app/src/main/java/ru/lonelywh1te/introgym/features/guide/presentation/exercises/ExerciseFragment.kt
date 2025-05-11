package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.utils.ImageLoader
import ru.lonelywh1te.introgym.databinding.FragmentExerciseBinding
import ru.lonelywh1te.introgym.databinding.ItemExecutionStepBinding
import ru.lonelywh1te.introgym.databinding.ItemExecutionTipBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseFragmentViewModel

class ExerciseFragment : Fragment() {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseFragmentViewModel>()
    private val args by navArgs<ExerciseFragmentArgs>()

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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = exercise.name

        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.EXERCISE_ANIMATION, exercise.animFilename), binding.ivExerciseAnimation)

        binding.tvExerciseName.text = exercise.name
        binding.tvExerciseDescription.text = exercise.description

        setStepsData(exercise.steps)
        setTipsData(exercise.tips)

        if (exercise.steps.isEmpty()) {
            binding.tvExecutionLabel.visibility = View.GONE
        }
    }

    private fun setStepsData(steps: List<String>) {
        val stepsLinearLayout = binding.llExecutionSteps

        stepsLinearLayout.removeAllViews()
        steps.forEachIndexed { index, text ->
            val bind = ItemExecutionStepBinding.inflate(LayoutInflater.from(requireContext()), stepsLinearLayout, false)
            bind.tvStepNumber.text = getString(R.string.step_number, (index + 1).toString())
            bind.tvStepDescription.text = text

            stepsLinearLayout.addView(bind.root)
        }
    }

    private fun setTipsData(tips: List<String>) {
        val tipsLinearLayout = binding.llExecutionTips

        tipsLinearLayout.removeAllViews()
        tips.forEach { text ->
            val bind = ItemExecutionTipBinding.inflate(LayoutInflater.from(requireContext()), tipsLinearLayout, false)
            bind.tvTipDescription.text = text

            tipsLinearLayout.addView(bind.root)
        }
    }
}