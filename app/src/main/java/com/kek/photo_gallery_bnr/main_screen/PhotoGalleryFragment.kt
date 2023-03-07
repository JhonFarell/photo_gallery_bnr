package com.kek.photo_gallery_bnr.main_screen

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.kek.photo_gallery_bnr.R
import com.kek.photo_gallery_bnr.databinding.FragmentPhotoGalleryBinding
import com.kek.photo_gallery_bnr.utils.Constances
import com.kek.photo_gallery_bnr.utils.Constances.LOG_TAG_PHOTO_FRAGMENT
import com.kek.photo_gallery_bnr.utils.Constances.POLL_WORK
import com.kek.photo_gallery_bnr.utils.PollWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class PhotoGalleryFragment : Fragment() {
    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Can't resolve binding"
        }

    private var searchView: SearchView? = null
    private var pollingMenuItem: MenuItem? = null

    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.uiState.collect {
                    binding.photoGrid.adapter = PhotoViewAdapter(
                        it.images) { photoPageUri ->
                        findNavController().navigate(
                            PhotoGalleryFragmentDirections.showPhoto(photoPageUri)
                        )
                    }

                    searchView?.setQuery(it.searchState, false)
                    updatePolingState(it.isPoling)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.isLoading.collect {
                    binding.apply {
                        progressBar.visibility = when (it) {
                            true -> View.VISIBLE
                            else -> View.GONE
                        }
                        searchView?.isEnabled = when (it) {
                            true -> false
                            else -> true
                        }
                    }
                }
            }
        }
    }

    private fun updatePolingState(isPoling: Boolean) {
        val toogleItemTitle = if (isPoling) {
            R.string.stop_polling
        } else R.string.start_polling

        pollingMenuItem?.setTitle(toogleItemTitle)

        if(isPoling) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val periodicRequest = PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                POLL_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem.actionView as? SearchView
        pollingMenuItem = menu.findItem(R.id.menu_item_toogle_polling)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(LOG_TAG_PHOTO_FRAGMENT, "Query: $query")
                photoGalleryViewModel.setQuery(query ?: "")

                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchView?.windowToken, 0)

                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(LOG_TAG_PHOTO_FRAGMENT, "New text: $newText")
                return false
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.setQuery("")
                true
            }
            R.id.menu_item_toogle_polling -> {
                photoGalleryViewModel.toogleIsPoling()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        searchView = null
        pollingMenuItem = null
    }

}
