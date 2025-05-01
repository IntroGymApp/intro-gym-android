package ru.lonelywh1te.introgym.features.workout.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.core.ui.dialogs.SubmitDialogFragment
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogState
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutControlViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutFragmentViewModel
import java.time.LocalDateTime

class WorkoutFragment : Fragment(), MenuProvider {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<WorkoutFragmentArgs>()
    private val viewModel by viewModel<WorkoutFragmentViewModel>()
    private val workoutControlViewModel by viewModel<WorkoutControlViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutExerciseItemAdapter: WorkoutExerciseItemAdapter

    private var workoutTrackingService: WorkoutTrackingService? = null
    private val workoutTrackingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as WorkoutTrackingService.LocalBinder
            workoutTrackingService = binder.getService()

            lifecycleScope.launch {
                workoutTrackingService?.timeStateFlow?.collectLatest { time ->
                    binding.tvExecutionTime.text = time.format(DateAndTimeStringFormatUtils.fullTimeFormatter)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            workoutTrackingService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadWorkout(args.workoutId)
        workoutControlViewModel.loadWorkoutLog(args.workoutId)
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
                navigateToEditWorkoutExercisePlan(item.workoutExerciseId)
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
                viewModel.moveWorkoutExerciseItem(from, to)
            }

            setOnMoveFinishedListener { from, to ->
                viewModel.moveWorkoutExercise(from, to)
            }

            setOnLeftSwipeListener { position ->
                val item = workoutExerciseItemAdapter.getItem(position)
                viewModel.deleteWorkoutExercise(item.workoutExerciseId)
            }

            attachToRecyclerView(recycler)
        }

        binding.btnAddExercise.setOnClickListener {
            navigateToPickExerciseFragment()
        }

        binding.btnStartWorkout.setOnClickListener {
            workoutControlViewModel.startWorkout()
        }

        binding.btnStopWorkout.setOnClickListener {
            workoutControlViewModel.stopWorkout()

            stopWorkoutTrackingService()
            unbindWorkoutTrackerService()
        }

        startCollectFlows()
        setFragmentResultListeners()
    }

    override fun onStop() {
        super.onStop()
        updateWorkout()
    }

    private fun startWorkoutTrackingService(startDateTime: LocalDateTime? = null) {
        requireContext().startService(
            Intent(requireContext(), WorkoutTrackingService::class.java).apply {
                action = WorkoutTrackingService.ACTION_START
                startDateTime?.let { putExtra(WorkoutTrackingService.START_LOCAL_DATE_TIME_EXTRA, startDateTime.toString()) }
            }
        )
    }

    private fun stopWorkoutTrackingService() {
        requireContext().startService(
            Intent(requireContext(), WorkoutTrackingService::class.java).apply {
                action = WorkoutTrackingService.ACTION_STOP
            }
        )
    }

    private fun bindWorkoutTrackerService() {
        requireContext().bindService(
            Intent(requireContext(), WorkoutTrackingService::class.java),
            workoutTrackingServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun unbindWorkoutTrackerService() {
        workoutTrackingService?.let {
            requireContext().unbindService(workoutTrackingServiceConnection)
        }
    }

    private fun startCollectFlows() {
        viewModel.workout.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workout ->
                workout?.let {
                    binding.etWorkoutName.setText(workout.name)
                    binding.etWorkoutDescription.setText(workout.description)
                    binding.cvWorkoutControlPanel.isVisible = !it.isTemplate
                }
            }
            .launchIn(lifecycleScope)

        viewModel.workoutExerciseItems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { items ->
                workoutExerciseItemAdapter.update(items)
            }
            .launchIn(lifecycleScope)

        viewModel.workoutDeleted.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { _ ->
                // TODO: заменить на snackbar с отменой

                Toast.makeText(requireContext(), "Тренировка удалена!", Toast.LENGTH_LONG).show()

                findNavController().navigateUp()
            }
            .launchIn(lifecycleScope)

        workoutControlViewModel.workoutLog.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutLog ->
                // TODO: переделать

                workoutLog?.let {
                    val workoutState = WorkoutLogState.get(workoutLog)

                    when (workoutState) {
                        WorkoutLogState.NotStarted -> {
                            binding.cvWorkoutControlPanel.isVisible = true
                            binding.btnStartWorkout.isVisible = true
                            binding.btnStopWorkout.isVisible = false
                        }
                        WorkoutLogState.InProgress -> {
                            binding.cvWorkoutControlPanel.isVisible = true
                            binding.btnStartWorkout.isVisible = false
                            binding.btnStopWorkout.isVisible = true

                            startWorkoutTrackingService(workoutLog.startDateTime)
                            bindWorkoutTrackerService()
                        }
                        WorkoutLogState.Finished -> {
                            // TODO: добавить панель итогов
                            
                            binding.cvWorkoutControlPanel.isVisible = false
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(ExerciseListFragment.PICK_REQUEST_KEY) { _, bundle ->
            val pickedExerciseId = bundle.getLong(ExerciseListFragment.PICK_RESULT_BUNDLE_KEY)
            viewModel.addWorkoutExercise(pickedExerciseId)
        }

        setFragmentResultListener(WorkoutExercisePlanEditorFragment.FRAGMENT_REQUEST_KEY) { _, bundle ->
            val workoutExercisePlan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE_KEY, WorkoutExercisePlan::class.java)
            } else {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE_KEY)
            }

            val workoutExerciseComment = bundle.getString(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_COMMENT_BUNDLE_KEY, "")

            workoutExercisePlan?.let { plan ->
                viewModel.updateWorkoutExercisePlan(plan)
                viewModel.updateWorkoutExerciseComment(plan.workoutExerciseId, workoutExerciseComment)
            }
        }
    }

    private fun navigateToPickExerciseFragment() {
        val action = WorkoutFragmentDirections.actionPickExercise(R.id.workoutFragment)
        findNavController().navigate(action)
    }

    private fun navigateToEditWorkoutExercisePlan(workoutExerciseId: Long) {
        val workoutExercise = viewModel.getWorkoutExerciseById(workoutExerciseId)

        val action = WorkoutFragmentDirections.toWorkoutExercisePlanEditorFragment(
            workoutExercise = workoutExercise
        )

        findNavController().navigate(action)
    }

    private fun updateWorkout() {
        val name = binding.etWorkoutName.text.toString()
        val description = binding.etWorkoutDescription.text.toString()

        viewModel.updateWorkout(name, description)
    }

    private fun deleteWorkout() {
        SubmitDialogFragment.Builder()
            .setTitle(getString(R.string.label_delete_workout))
            .setMessage(getString(R.string.label_submit_message_delete_workout))
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete))
            .setIconTint(MaterialColors.getColor(binding.root, R.attr.igErrorColor))
            .setPositiveButton(getString(R.string.label_cancel)) { dialog ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.label_delete)) { dialog ->
                viewModel.deleteWorkout()
                dialog.dismiss()
            }
            .build()
            .show(childFragmentManager, SubmitDialogFragment.TAG)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.deleteWorkout -> deleteWorkout()
        }

        return true
    }

}