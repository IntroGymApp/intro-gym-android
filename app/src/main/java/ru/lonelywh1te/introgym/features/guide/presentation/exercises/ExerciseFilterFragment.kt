package ru.lonelywh1te.introgym.features.guide.presentation.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args by navArgs<ExerciseFilterFragmentArgs>()

    private val selectedTagsIds by lazy { args.selectedTagsIds.toMutableList() }

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

        muscleTagsAdapter = setupTagAdapter()
        equipmentTagsAdapter = setupTagAdapter()
        difficultyTagsAdapter = setupTagAdapter()

        muscleTagsRecycler = setupRecycler(binding.rvMuscleTags, muscleTagsAdapter)
        equipmentTagsRecycler = setupRecycler(binding.rvEquipmentTags, equipmentTagsAdapter)
        difficultyTagsRecycler = setupRecycler(binding.rvDifficultyTags, difficultyTagsAdapter)

        binding.btnSubmit.setOnClickListener {
            navigateUpWithResult()
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.muscleTags.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { tags ->
                muscleTagsAdapter.update(tags)
            }
            .launchIn(lifecycleScope)

        viewModel.equipmentTags.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { tags ->
                equipmentTagsAdapter.update(tags)
            }
            .launchIn(lifecycleScope)

        viewModel.difficultyTags.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { tags ->
                difficultyTagsAdapter.update(tags)
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateUpWithResult() {
        val bundle = Bundle().apply {
            putIntArray(FILTER_RESULT_BUNDLE_KEY, selectedTagsIds.toIntArray())
        }

        setFragmentResult(FILTER_REQUEST_KEY, bundle)
        findNavController().navigateUp()
    }

    private fun setupRecycler(recycler: RecyclerView, adapter: RecyclerView.Adapter<*>): RecyclerView {
        return recycler.apply {
            this.adapter = adapter
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupTagAdapter(): TagsAdapter {
        return TagsAdapter(selectedTagsIds).apply {
            setOnSelectedItemListener { tag ->
                if (selectedTagsIds.contains(tag.id)) selectedTagsIds.remove(tag.id) else selectedTagsIds.add(tag.id)
            }
        }
    }

    companion object {
        const val FILTER_REQUEST_KEY = "filter_exercise"
        const val FILTER_RESULT_BUNDLE_KEY = "selected_tags_ids"
    }
}