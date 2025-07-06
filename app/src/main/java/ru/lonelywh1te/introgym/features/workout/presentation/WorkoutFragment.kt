package ru.lonelywh1te.introgym.features.workout.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.dialogs.SubmitDialogFragment
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.core.ui.utils.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.helper.WorkoutExerciseSetHelper
import ru.lonelywh1te.introgym.features.workout.presentation.state.WorkoutFragmentState
import ru.lonelywh1te.introgym.features.workout.presentation.state.WorkoutFragmentUiData
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.workoutTrackingService.WorkoutServiceController
import ru.lonelywh1te.introgym.features.workout.presentation.workoutTrackingService.impl.WorkoutService
import java.time.LocalDateTime
import java.util.UUID

class WorkoutFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<WorkoutFragmentArgs>()
    private val viewModel by viewModel<WorkoutFragmentViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutExerciseItemAdapter: WorkoutExerciseItemAdapter

    private val workoutServiceController by inject<WorkoutServiceController>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setWorkoutId(args.workoutId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutExerciseItemAdapter = WorkoutExerciseItemAdapter().apply {
            setOnItemClickListener { item ->
                val workoutState = viewModel.workoutFragmentState.value as UIState.Success

                when (workoutState.data.state) {
                    WorkoutFragmentState.WORKOUT_IN_PROGRESS -> {
                        navigateToWorkoutExerciseExecution(item.workoutExerciseId)
                    }
                    WorkoutFragmentState.WORKOUT_FINISHED -> {
                        navigateToWorkoutExerciseExecution(item.workoutExerciseId)
                    }
                    else -> {
                        navigateToEditWorkoutExercisePlan(item.workoutExerciseId)
                    }
                }
            }
        }

        recycler = binding.rvWorkoutExerciseItems.apply {
            adapter = workoutExerciseItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        ItemTouchHelperCallback(
            dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            swipeDirs = ItemTouchHelper.LEFT,
        ).apply {
            setOnMoveListener { from, to ->
                workoutExerciseItemAdapter.move(from, to)
            }

            setOnMoveFinishedListener { from, to ->
                viewModel.moveWorkoutExercise(from, to)
            }

            setOnLeftSwipeListener { position ->
                val item = workoutExerciseItemAdapter.getItem(position)

                if (workoutExerciseItemAdapter.itemCount - 1 == 0) {
                    showDeleteWorkoutDialog(
                        onDismiss = {
                            workoutExerciseItemAdapter.refresh()
                        }
                    )
                } else {
                    viewModel.deleteWorkoutExercise(item.workoutExerciseId)
                }
            }

            attachToRecyclerView(recycler)
        }

        binding.btnAddExercise.setOnClickListener {
            navigateToPickExerciseFragment()
        }

        binding.btnStartWorkout.setOnClickListener {
            viewModel.startWorkout()
        }

        binding.btnStopWorkout.setOnClickListener {
            showSubmitStopWorkoutDialog()
        }

        startCollectFlows()
        setFragmentResultListeners()
    }

    override fun onStop() {
        super.onStop()
        updateWorkout()
    }

    private fun startWorkoutTrackingService(startDateTime: LocalDateTime? = null) {
        workoutServiceController.start(startDateTime)
        workoutServiceController.apply {
            start(startDateTime)
            bind(onServiceConnectedListener = { _, iBinder ->
                    val binder = iBinder as WorkoutService.LocalBinder
                    viewModel.setExecutionTimeFlow(binder.getTimeFlow())
            })
        }
    }

    private fun stopWorkoutTrackingService() {
        workoutServiceController.stop()
    }

    private fun startCollectFlows() {
        viewModel.workoutFragmentState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        setWorkoutFragmentData(state.data)
                    }
                    is UIState.Loading -> {
                        binding.etWorkoutName.setHint(null)
                        binding.etWorkoutDescription.setHint(null)
                    }
                    else -> {
                    // TODO: Not yet implemented
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.workoutExecutionTime.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { time ->
                binding.tvExecutionTime.text = time.format(DateAndTimeStringFormatUtils.fullTimeFormatter)
            }
            .launchIn(lifecycleScope)

        viewModel.workoutDeleted.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { _ ->
                Toast.makeText(requireContext(), "Тренировка удалена!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
            .launchIn(lifecycleScope)
    }

    private fun setWorkoutFragmentData(data: WorkoutFragmentUiData) {
        data.workout?.let {
            binding.etWorkoutName.setText(it.name)
            binding.etWorkoutDescription.setText(it.description)

            binding.etWorkoutName.setHint(getString(R.string.label_workout_name))
            binding.etWorkoutDescription.setHint(getString(R.string.label_workout_comment))
        }

        data.workoutExerciseItems?.let {
            workoutExerciseItemAdapter.update(it)
        }

        when(data.state) {
            WorkoutFragmentState.WORKOUT_TEMPLATE -> {
                binding.cvWorkoutControlPanel.isVisible = false
                binding.cvWorkoutResults.isVisible = false
            }
            WorkoutFragmentState.WORKOUT_NOT_STARTED -> {
                binding.btnStartWorkout.isVisible = true
                binding.btnStopWorkout.isVisible = false

                binding.cvWorkoutControlPanel.isVisible = true
                binding.cvWorkoutResults.isVisible = false
            }
            WorkoutFragmentState.WORKOUT_IN_PROGRESS -> {
                binding.btnStartWorkout.isVisible = false
                binding.btnStopWorkout.isVisible = true

                data.workoutLog?.let {
                    startWorkoutTrackingService(it.startDateTime)
                }

                binding.cvWorkoutControlPanel.isVisible = true
                binding.cvWorkoutResults.isVisible = false
            }
            WorkoutFragmentState.WORKOUT_FINISHED -> {
                data.workoutResult?.let {
                    binding.tvTotalTime.text = it.totalTime.format(DateAndTimeStringFormatUtils.fullTimeFormatter)
                    binding.tvTotalWeight.text = "${it.totalWeight} кг"
                    binding.tvTotalEffortPercent.text = "${it.totalEffort}%"
                    binding.tvProgress.text = "${it.progress}%"
                    binding.ivEffortIndicator.background.setTint(
                        MaterialColors.getColor(
                            requireContext(),
                            WorkoutExerciseSetHelper.getEffortColor(it.totalEffort),
                            R.attr.igWarmUpEffortColor
                        )
                    )

                    binding.cvWorkoutResults.isVisible = true
                }

                binding.cvWorkoutControlPanel.isVisible = false

            }
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(ExerciseListFragment.PICK_REQUEST_KEY) { _, bundle ->
            val pickedExerciseId = bundle.getLong(ExerciseListFragment.PICK_RESULT_BUNDLE_KEY)
            viewModel.addWorkoutExercise(pickedExerciseId)
        }
    }

    private fun navigateToPickExerciseFragment() {
        val action = WorkoutFragmentDirections.actionPickExercise(R.id.workoutFragment)
        findNavController().safeNavigate(action)
    }

    private fun navigateToEditWorkoutExercisePlan(workoutExerciseId: UUID) {
        val action = WorkoutFragmentDirections.toWorkoutExercisePlanEditorFragment(
            workoutExerciseId = workoutExerciseId
        )

        findNavController().safeNavigate(action)
    }

    private fun navigateToWorkoutExerciseExecution(workoutExerciseId: UUID) {
        val action = WorkoutFragmentDirections.toWorkoutExecutionFragment(workoutExerciseId)
        findNavController().safeNavigate(action)
    }

    private fun updateWorkout() {
        val name = binding.etWorkoutName.text.toString()
        val description = binding.etWorkoutDescription.text.toString()

        viewModel.updateWorkout(name, description)
    }

    private fun showDeleteWorkoutDialog(onDismiss: (() -> Unit)? = null) {
        SubmitDialogFragment.Builder()
            .setTitle(getString(R.string.label_delete_workout))
            .setMessage(getString(R.string.label_submit_message_delete_workout))
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete))
            .setIconTint(MaterialColors.getColor(binding.root, R.attr.igErrorColor))
            .setOnDismissDialog {
                onDismiss?.invoke()
            }
            .setPositiveButton(getString(R.string.label_cancel)) { dialog ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.label_delete)) { dialog ->
                viewModel.deleteWorkout()
                stopWorkoutTrackingService()

                dialog.dismiss()
            }
            .build()
            .show(childFragmentManager, SubmitDialogFragment.TAG)
    }

    private fun showSubmitStopWorkoutDialog() {
        SubmitDialogFragment.Builder()
            .setTitle(getString(R.string.label_finish_workout))
            .setMessage(getString(R.string.label_finish_workout_submit_message))
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_energy))
            .setIconTint(MaterialColors.getColor(binding.root, R.attr.igErrorColor))
            .setPositiveButton(getString(R.string.label_cancel)) { dialog ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.label_finish)) { dialog ->
                viewModel.stopWorkout()
                stopWorkoutTrackingService()

                dialog.dismiss()
            }
            .build()
            .show(childFragmentManager, SubmitDialogFragment.TAG)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.workout_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.deleteWorkout -> showDeleteWorkoutDialog()
        }

        return true
    }
}