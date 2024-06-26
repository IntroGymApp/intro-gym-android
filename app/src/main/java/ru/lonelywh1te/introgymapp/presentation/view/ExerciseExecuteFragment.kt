package ru.lonelywh1te.introgymapp.presentation.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.color.MaterialColors
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.lonelywh1te.introgymapp.R
import ru.lonelywh1te.introgymapp.databinding.FragmentExerciseExecuteBinding
import ru.lonelywh1te.introgymapp.domain.AssetsPath
import ru.lonelywh1te.introgymapp.domain.model.ExerciseHistory
import ru.lonelywh1te.introgymapp.domain.model.ExerciseWithInfo
import ru.lonelywh1te.introgymapp.presentation.view.adapter.ExerciseHistoryAdapter
import ru.lonelywh1te.introgymapp.presentation.viewModel.ExerciseExecuteFragmentViewModel

class ExerciseExecuteFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentExerciseExecuteBinding
    private lateinit var exerciseWithInfo: ExerciseWithInfo
    private lateinit var viewModel: ExerciseExecuteFragmentViewModel
    private lateinit var recycler: RecyclerView
    private val args: ExerciseExecuteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseWithInfo = args.exerciseWithInfo
        viewModel = getViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        binding = FragmentExerciseExecuteBinding.inflate(layoutInflater)

        val adapter = ExerciseHistoryAdapter()
        recycler = binding.rvExerciseHistory

        recycler.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.ibAddExerciseHistory.setOnClickListener {
            addExerciseSet()
            hideKeyboard(it)
        }

        viewModel.exerciseHistory.observe(viewLifecycleOwner) {
            adapter.exerciseHistory = it.toMutableList()

            binding.tvExerciseSets.text = "${it.size} / ${exerciseWithInfo.exercise.sets}"

            if (it.size > exerciseWithInfo.exercise.sets) {
                binding.tvExerciseSets.setTextColor(ContextCompat.getColor(requireContext(), R.color.negative_color))
            } else {
                binding.tvExerciseSets.setTextColor(MaterialColors.getColor(binding.tvExerciseSets, R.attr.ig_defaultTextColor))
            }
        }

        setExerciseData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getExerciseHistoryById(exerciseWithInfo.exercise.id)
    }

    private fun setExerciseData() {
        if (!exerciseWithInfo.exercise.note.isNullOrEmpty()) {
            binding.tvExerciseNote.text = exerciseWithInfo.exercise.note
            binding.exerciseDescriptionLayout.visibility = View.VISIBLE
        } else {
            binding.exerciseDescriptionLayout.visibility = View.GONE
        }

        binding.tvExerciseSets.text = "0 / ${exerciseWithInfo.exercise.sets}"
        binding.tvExerciseReps.text = exerciseWithInfo.exercise.reps.toString()
        binding.tvExerciseWeight.text = exerciseWithInfo.exercise.weight.toString()

        Glide.with(binding.root)
            .load((Uri.parse("${AssetsPath.EXERCISE_INFO_IMG}/${exerciseWithInfo.exerciseInfo.img}")))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivExerciseInfoImage)
    }

    private fun addExerciseSet() {
        if (binding.etCompletedReps.text.toString().isEmpty() || binding.etCompletedWeight.text.toString().isEmpty()) {
            if (binding.etCompletedReps.text.toString().isEmpty()) binding.etCompletedReps.error = "Введите количество повторений"
            if (binding.etCompletedWeight.text.toString().isEmpty()) binding.etCompletedWeight.error = "Введите вес"
            return
        }

        val exerciseId = exerciseWithInfo.exercise.id
        val reps = binding.etCompletedReps.text.toString().toInt()
        val weight = binding.etCompletedWeight.text.toString().toInt()
        val date = args.date

        val exerciseHistory = ExerciseHistory(exerciseId, reps, weight, date)

        viewModel.addExerciseHistory(exerciseHistory)

//        binding.etCompletedReps.setText("")
//        binding.etCompletedWeight.setText("")
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.exercise_info_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.aboutExercise -> {
                val action = ExerciseExecuteFragmentDirections.toExerciseInfoFragment(exerciseWithInfo.exerciseInfo.name, exerciseWithInfo.exerciseInfo)
                findNavController().navigate(action)
            }
        }

        return true
    }
}