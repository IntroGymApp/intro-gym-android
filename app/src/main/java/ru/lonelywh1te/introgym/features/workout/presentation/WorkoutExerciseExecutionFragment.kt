package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.dialogs.PickHmsBottomSheetDialogFragment
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.core.ui.utils.ImageLoader
import ru.lonelywh1te.introgym.core.ui.utils.InputFilters
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutExerciseExecutionBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseSetAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExerciseExecutionViewModel
import java.time.LocalTime
import java.util.UUID

class WorkoutExerciseExecutionFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutExerciseExecutionBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutExerciseExecutionViewModel>()
    private val args by navArgs<WorkoutExerciseExecutionFragmentArgs>()

    private lateinit var setsRecycler: RecyclerView
    private lateinit var workoutExerciseSetAdapter: WorkoutExerciseSetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setWorkoutExerciseId(args.workoutExerciseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutExerciseExecutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        workoutExerciseSetAdapter = WorkoutExerciseSetAdapter().apply {
            setOnItemClickListener { exerciseSetId ->
                showWorkoutExerciseSetEditorDialog(exerciseSetId)
            }
        }
        setsRecycler = binding.rvSets.apply {
            adapter = workoutExerciseSetAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.groupEffortButtons.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    binding.btnWarmUpEffort.id -> viewModel.setEffort(Effort.WARMUP)
                    binding.btnLowEffort.id -> viewModel.setEffort(Effort.LOW)
                    binding.btnMidEffort.id -> viewModel.setEffort(Effort.MEDIUM)
                    binding.btnHardEffort.id -> viewModel.setEffort(Effort.HARD)
                    binding.btnMaxEffort.id -> viewModel.setEffort(Effort.MAX)
                }
            }
        }

        binding.etTime.setOnClickListener {
            showPickHmsDialog()
        }

        binding.etWeight.filters = arrayOf(InputFilters.DecimalDigitsInputFilter())

        binding.btnAddSet.setOnClickListener {
            addWorkoutExerciseSet()
        }

        // TODO: вынести в VM

        binding.btnMinusReps.setOnClickListener {
            var reps = (binding.etReps.text.takeIf { it.toString() != "" } ?: "0").toString().toInt()
            if (reps > 0) reps--

            binding.etReps.setText(reps.toString())
        }

        binding.btnPlusReps.setOnClickListener {
            var reps = (binding.etReps.text.takeIf { it.toString() != "" } ?: "0").toString().toInt()
            if (reps < 999) reps++

            binding.etReps.setText(reps.toString())
        }

        binding.btnMinusWeight.setOnClickListener {
            var weight = (binding.etWeight.text.takeIf { it.toString() != "" } ?: "0").toString().toFloat()
            if (weight > 0) weight -= 2.5f

            binding.etWeight.setText(weight.toString())
        }

        binding.btnPlusWeight.setOnClickListener {
            var weight = (binding.etWeight.text.takeIf { it.toString() != "" } ?: "0").toString().toFloat()
            if (weight < 9999.99f) weight += 2.5f

            binding.etWeight.setText(weight.toString())
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.workoutExercise.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutExercise ->
                workoutExercise?.let {
                    setWorkoutExerciseData(it)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.workoutExercisePlan.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutExercisePlan ->
                workoutExercisePlan?.let {
                    setWorkoutExercisePlanData(workoutExercisePlan)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.exercise.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { exercise ->
                exercise?.let {
                    setExerciseData(it)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.workoutExerciseSets.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutExerciseSets ->
                workoutExerciseSetAdapter.update(workoutExerciseSets)

                binding.tvSetsTitle.isVisible = workoutExerciseSets.isNotEmpty()
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToWorkoutExercisePlanEditor() {
        val action = WorkoutExerciseExecutionFragmentDirections.toWorkoutExercisePlanEditorFragment(
            workoutExerciseId = args.workoutExerciseId
        )

        findNavController().safeNavigate(action)
    }

    private fun addWorkoutExerciseSet() {
        val reps = if (binding.etReps.isVisible) binding.etReps.text.toString() else ""
        val weight = if (binding.etWeight.isVisible) binding.etWeight.text.toString() else ""
        val time = if (binding.etTime.isVisible && binding.etTime.text.toString().isNotBlank()) {
            LocalTime.parse(binding.etTime.text.toString(), DateAndTimeStringFormatUtils.fullTimeFormatter).toSecondOfDay().toString()
        } else ""
        val distance = if (binding.etDistance.isVisible) binding.etDistance.text.toString() else ""

        viewModel.addWorkoutExerciseSet(reps, weight, distance, time)
    }

    private fun setWorkoutExerciseData(workoutExercise: WorkoutExercise) {
        binding.tvExerciseComment.apply {
            text = workoutExercise.comment
            isVisible = workoutExercise.comment.isNotBlank()
        }
    }

    private fun showPickHmsDialog() {
        val localtime: LocalTime? = if (binding.etTime.text.toString().isNotBlank()) {
            LocalTime.parse(binding.etTime.text.toString(), DateAndTimeStringFormatUtils.fullTimeFormatter)
        } else {
            null
        }

        if (childFragmentManager.findFragmentByTag(PickHmsBottomSheetDialogFragment.TAG) == null) {
            PickHmsBottomSheetDialogFragment.instance(localtime).show(childFragmentManager, PickHmsBottomSheetDialogFragment.TAG)
            setPickHmsDialogResultListener()
        }
    }

    private fun showWorkoutExerciseSetEditorDialog(exerciseSetId: UUID) {
        if (childFragmentManager.findFragmentByTag(WorkoutExerciseSetEditorFragment.TAG) == null) {
            WorkoutExerciseSetEditorFragment.instance(exerciseSetId)
                .show(childFragmentManager, WorkoutExerciseSetEditorFragment.TAG)
        }
    }

    private fun setPickHmsDialogResultListener() {
        childFragmentManager.setFragmentResultListener(PickHmsBottomSheetDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val localtime = bundle.getString(PickHmsBottomSheetDialogFragment.RESULT_LOCALTIME_STRING)?.let {
                LocalTime.parse(it, DateAndTimeStringFormatUtils.fullTimeFormatter)
            }

            localtime?.let {
                binding.etTime.setText(localtime.format(DateAndTimeStringFormatUtils.fullTimeFormatter))
            }

            return@setFragmentResultListener
        }
    }

    private fun setWorkoutExercisePlanData(workoutExercisePlan: WorkoutExercisePlan) {
        binding.llWeightInput.isVisible = workoutExercisePlan.weightKg != null
        if (workoutExercisePlan.weightKg != null) {
            binding.etWeight.setHint(workoutExercisePlan.weightKg.toString())
        }

        binding.llRepsInput.isVisible = workoutExercisePlan.reps != null
        if (workoutExercisePlan.reps != null) {
            binding.etReps.setHint(workoutExercisePlan.reps.toString())
        }

        binding.llTimeInput.isVisible = workoutExercisePlan.timeInSec != null
        if (workoutExercisePlan.timeInSec != null) {
            binding.etTime.setHint(
                LocalTime.ofSecondOfDay(workoutExercisePlan.timeInSec.toLong())
                    .format(DateAndTimeStringFormatUtils.fullTimeFormatter)
            )
        }

        binding.llDistanceInput.isVisible = workoutExercisePlan.distanceInMeters != null
        if (workoutExercisePlan.distanceInMeters != null) {
            binding.etDistance.setHint(workoutExercisePlan.distanceInMeters.toString())
        }
    }

    private fun setExerciseData(exercise: Exercise) {
        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.EXERCISE_ANIMATION, exercise.animFilename), binding.ivExerciseAnimation)

        binding.tvExerciseName.text = exercise.name
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.workout_exercise_execution_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.edit_workout_exercise_plan -> navigateToWorkoutExercisePlanEditor()
        }

        return true
    }
}