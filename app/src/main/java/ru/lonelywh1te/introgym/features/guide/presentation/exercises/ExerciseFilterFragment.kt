package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.databinding.FragmentExerciseFilterBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter.TagsAdapter
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseFilterFragmentViewModel

class ExerciseFilterFragment : Fragment() {
    private var _binding: FragmentExerciseFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ExerciseFilterFragmentViewModel>()

    private lateinit var muscleTagsRecycler: RecyclerView
    private lateinit var equipmentTagsRecycler: RecyclerView
    private lateinit var difficultyTagsRecycler: RecyclerView
    private lateinit var muscleTagsAdapter: TagsAdapter
    private lateinit var equipmentTagsAdapter: TagsAdapter
    private lateinit var difficultyTagsAdapter: TagsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        muscleTagsAdapter = TagsAdapter()
        equipmentTagsAdapter = TagsAdapter()
        difficultyTagsAdapter = TagsAdapter()

        muscleTagsRecycler = binding.rvMuscleTags.apply {
            adapter = muscleTagsAdapter
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
            isNestedScrollingEnabled = false
        }

        equipmentTagsRecycler = binding.rvEquipmentTags.apply {
            adapter = equipmentTagsAdapter
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
            isNestedScrollingEnabled = false
        }

        difficultyTagsRecycler = binding.rvDifficultyTags.apply {
            adapter = difficultyTagsAdapter
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
            isNestedScrollingEnabled = false
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.muscleTags.flowWithLifecycle(lifecycle)
            .onEach { tags ->
                muscleTagsAdapter.tags = tags
            }
            .launchIn(lifecycleScope)

        viewModel.equipmentTags.flowWithLifecycle(lifecycle)
            .onEach { tags ->
                equipmentTagsAdapter.tags = tags
            }
            .launchIn(lifecycleScope)

        viewModel.difficultyTags.flowWithLifecycle(lifecycle)
            .onEach { tags ->
                difficultyTagsAdapter.tags = tags
            }
            .launchIn(lifecycleScope)
    }
}