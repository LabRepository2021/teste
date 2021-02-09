package com.example.myteste.ui.dashboard

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myteste.R
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var adView: AdManagerAdView
    private var initialLayoutComplete = false

    val adSize: AdSize
        get() {
            val display = activity?.windowManager?.defaultDisplay
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)

            val density = outMetrics.density

            val layout: FrameLayout = view?.findViewById(R.id.ad_view_container)!!
            var adWidthPixels = layout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationBannerAdSizeWithWidth(activity, adWidth)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val layout: FrameLayout = root.findViewById(R.id.ad_view_container)

        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            //  textView.text = it
        })

        adView = AdManagerAdView(activity)
        layout.addView(adView)
        layout.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner(adSize)
            }
        }


        return root
    }


    /** Called when leaving the activity.  */
     override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called when returning to the activity  */
     override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /** Called before the activity is destroyed  */
     override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun loadBanner(adSize: AdSize) {
        adView.adUnitId = BACKFILL_AD_UNIT_ID
        adView.setAdSizes(adSize)
        val adRequest = AdManagerAdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    companion object {
        internal val BACKFILL_AD_UNIT_ID = "/30497360/adaptive_banner_test_iu/backfill"
    }
}

