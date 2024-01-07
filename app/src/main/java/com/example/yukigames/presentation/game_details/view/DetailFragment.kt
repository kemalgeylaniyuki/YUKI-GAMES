package com.example.yukigames.presentation.game_details.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.yukigames.databinding.FragmentGameDetailsBinding
import com.example.yukigames.presentation.game_details.viewModel.GameDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!

    private val gameDetailsViewModel : GameDetailsViewModel by viewModels()
    private var job : Job? = null
    private var id = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            id = DetailFragmentArgs.fromBundle(it).id
        }

        gameDetailsViewModel.getGameDetails(id)

        observeViewModel()

    }

    fun observeViewModel(){

        job = viewLifecycleOwner.lifecycleScope.launch {
            gameDetailsViewModel.state.collect{

                it.game?.let {
                    binding.textViewName.text = "Name : " + it.name_original
                    binding.textViewDescription.text = "Description : " + it.description_raw
                    binding.textViewGenres.text = "GENRE : Genre"
                    binding.textViewReleased.text = "Released Date : " + it.released

                    val formattedRating : String = "Rating : %.1f".format(it.rating)
                    binding.textViewRating.text = formattedRating

                    binding.textViewWesite.text = it.website

                    Glide.with(binding.imageView)
                        .load(it.background_image)
                        .into(binding.imageView)

                }

                if (it.error.isNotBlank()){
                    binding.errorTextView.text = it.error
                    binding.errorTextView.visibility = View.VISIBLE
                }
                else {
                    binding.errorTextView.visibility = View.GONE
                }

                if (it.isLoading){
                    binding.progressBar2.visibility = View.VISIBLE
                }
                else {
                    binding.progressBar2.visibility = View.GONE
                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        _binding = null
    }

}