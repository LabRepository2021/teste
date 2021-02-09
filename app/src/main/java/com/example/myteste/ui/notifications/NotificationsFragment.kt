package com.example.myteste.ui.notifications

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myteste.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    var adManagerAdView: AdManagerAdView? = null
    lateinit var progressBar: ProgressBar
    lateinit var linearLayout: LinearLayout
    lateinit var textView: TextView
    lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            //  textView.text = it
        })

        button = root.findViewById(R.id.button)
        linearLayout = root.findViewById(R.id.root_layout)
        textView = root.findViewById(R.id.text)
        progressBar = ProgressBar(activity)

        var params: ActionBar.LayoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )

        adManagerAdView = AdManagerAdView(activity)
        adManagerAdView?.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))

        adManagerAdView?.setAdSizes(AdSize(335, 110))
        adManagerAdView?.adUnitId = "/22158792083/bannerTeste"
        adManagerAdView?.loadAd(AdManagerAdRequest.Builder().build())
        params.setMargins(0, 0, 0, 0)
        linearLayout.addView(adManagerAdView)
        linearLayout.addView(progressBar)


        progressBar.visibility = View.VISIBLE

        adManagerAdView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adManagerAdView?.recordManualImpression()
                progressBar.visibility = View.GONE
                textView.text = adManagerAdView?.responseInfo?.responseId
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }
        }
        return root
    }

    /** Chamado ao sair da atividade  */
    override fun onPause() {
        adManagerAdView?.pause()
        super.onPause()
    }

    /** Chamado ao retornar à atividade  */
    override fun onResume() {
        super.onResume()
        adManagerAdView?.resume()
    }

    /** Chamado antes que a atividade seja destruída  */
    override fun onDestroy() {
        adManagerAdView?.destroy()
        super.onDestroy()
    }

}
