package com.pawan.cariadandroidtest.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pawan.cariadandroidtest.databinding.FragmentStationDetailsBottomSheetBinding
import com.pawan.cariadandroidtest.model.ChargingStationResponseItem

/**
 * [StationDetailsBottomSheetFragment] To show Charging Station details
 * */
class StationDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private val sharedViewModel: MapSharedViewModel by activityViewModels()
    private lateinit var binding: FragmentStationDetailsBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStationDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        sharedViewModel.isDetailsDialogDismissed(true)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.stationDetailsData.observe(viewLifecycleOwner, observeStationDetails)
    }

    private val observeStationDetails = Observer<ChargingStationResponseItem?> { station ->
        station?.let {
            val addressData = it.AddressInfo
            val address =
                "${addressData.AddressLine1}, ${addressData.Town}, ${addressData.StateOrProvince}, ${addressData.Country.Title}"
            binding.tvAddressInfo.text = address
            binding.tvStationCount.text = it.NumberOfPoints.toString()
        }
    }
}