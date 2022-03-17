package com.pawan.cariadandroidtest.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pawan.cariadandroidtest.R
import com.pawan.cariadandroidtest.databinding.FragmentStationMapsBinding
import com.pawan.cariadandroidtest.model.ChargingStationResponse
import com.pawan.cariadandroidtest.model.ChargingStationResponseItem

/**
 * [StationMapsFragment] To load google map
 * */
class StationMapsFragment : Fragment() {

    private val sharedViewModel: MapSharedViewModel by activityViewModels()
    private var mGoogleMap: GoogleMap? = null
    private val markerMap = HashMap<Marker?, ChargingStationResponseItem>()
    private lateinit var binding: FragmentStationMapsBinding

    private val callback = OnMapReadyCallback { googleMap ->
        mGoogleMap = googleMap
        setData()
    }

    private val markerClickEvent = GoogleMap.OnMarkerClickListener {
        with(sharedViewModel) {
            clickedMarker = it
            it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            val item = markerMap[it]
            setClickedMarkerData(item)

            findNavController().navigate(
                R.id.action_stationMapFragment_to_stationDetailsBottomSheetFragment
            )
        }
        return@OnMarkerClickListener true
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.startRepeatingJob()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStationMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        initObserver()
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.stopRepeatingJob()
    }

    private fun initObserver() {
        with(sharedViewModel) {
            getIsLoadingLiveData().observe(viewLifecycleOwner) {
                if (it) binding.pb.visibility = View.VISIBLE else binding.pb.visibility = View.GONE
            }

            getDisplayErrorLiveData().observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(), it,
                    Toast.LENGTH_LONG
                ).show()
            }

            networkError().observe(viewLifecycleOwner) {
                if (it) Toast.makeText(
                    requireContext(), requireContext().resources.getString(R.string.network_error),
                    Toast.LENGTH_LONG
                ).show()
            }

            detailsIsDismissedLiveData.observe(viewLifecycleOwner, isDetailsDismissedObserver)
        }
    }

    private val observeStationListData = Observer<ChargingStationResponse?> { list ->
        val bounds = LatLngBounds.Builder()

        mGoogleMap?.let { map ->
            with(map) {
                clearOldData()
                list.forEach { stationItem ->
                    val loc =
                        LatLng(stationItem.AddressInfo.Latitude, stationItem.AddressInfo.Longitude)
                    bounds.include(loc)
                    val marker = addMarker(
                        MarkerOptions().position(loc).title(stationItem.AddressInfo.Title)
                    )
                    marker?.tag = stationItem.ID
                    markerMap[marker] = stationItem
                }
                animateCamera(CameraUpdateFactory.zoomTo(16.0f))
                moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0))
            }
        }
    }

    private val isDetailsDismissedObserver = Observer<Boolean> {
        if (it) sharedViewModel.clickedMarker?.setIcon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
    }

    /**
     * To set click event on marker and observing Charging Station data from server
     * */
    private fun setData() {
        mGoogleMap?.setOnMarkerClickListener(markerClickEvent)
        sharedViewModel.stationMutableList.observe(viewLifecycleOwner, observeStationListData)
    }

    /**
     * Clear map, Marker list and saved marker after clicked.
     * Close Station details bottom sheet if was opened
     * */
    private fun clearOldData() {
        if (markerMap.size > 0) {
            mGoogleMap?.clear()
            markerMap.clear()
            sharedViewModel.clickedMarker = null

            val navHostFragment: Fragment? =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_container)
            navHostFragment?.let {
                it.childFragmentManager.fragments.forEach { fragment ->
                    when(fragment){
                        is StationDetailsBottomSheetFragment -> fragment.dismiss()
                }
                }
            }
        }
    }
}