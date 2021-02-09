package com.example.myteste.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myteste.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeCustomFormatAd
import com.jama.carouselview.CarouselView
import java.util.stream.DoubleStream.builder

const val AD_MANAGER_AD_UNIT_ID = "/22158792083/TesteCarrossel"
const val TESTE_BANNER = "/22158792083/BannerCarrosselTesteAndroidEIos"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adLoader: AdLoader
    private lateinit var mProgressBarAdView: ProgressBar
    private lateinit var textViewR: TextView
    private lateinit var banner: ImageView
    private lateinit var banner2: ImageView
    private lateinit var frameTest: FrameLayout

    var currentCustomTemplateAd: NativeCustomFormatAd? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val bannerImg: ImageView = root.findViewById(R.id.ivBannerTransactionMenu)
        val bannerImg2: ImageView = root.findViewById(R.id.image2)


        val frame: FrameLayout = root.findViewById(R.id.ad_frame)
        banner2 = bannerImg2
        banner = bannerImg
        textViewR = textView
        frameTest = frame

        val progressBar: ProgressBar = root.findViewById(R.id.progress)
        mProgressBarAdView = progressBar

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        nativeAdManager()

        return root
    }


    private fun nativeAdManager() {
        mProgressBarAdView.visibility = View.GONE
        adLoader = AdLoader.Builder(
            requireContext(), "/22158792083/app_nativo/home/carrossel_1/banner_1")
            .forCustomFormatAd("11965647",
                { ad: NativeCustomFormatAd ->
                    var activityDestroyed = false
                    activityDestroyed = activity?.isDestroyed!!
                    if (activityDestroyed || activity?.isFinishing!! || activity?.isChangingConfigurations!!) {
                        ad.destroy()
                        return@forCustomFormatAd
                    }

                    currentCustomTemplateAd?.destroy()
                    currentCustomTemplateAd = ad

                    banner.setImageDrawable(ad.getImage("imagem1").drawable)
                    banner.setOnClickListener { ad.performClick("imagem1") }

                },
                { ad: NativeCustomFormatAd, s: String ->
                    ad.recordImpression()
                    ad.performClick(s)
                    Toast.makeText(requireContext(), "A custom click $s", Toast.LENGTH_SHORT).show()
                }).withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    textViewR.text= adError.message
                }
                override fun onAdLoaded() {

                }

            })
            .build()
        adLoader.loadAds(AdManagerAdRequest.Builder().build(),2)
       // adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }


    override fun onDestroy() {
        currentCustomTemplateAd?.destroy()
        super.onDestroy()
    }
}

