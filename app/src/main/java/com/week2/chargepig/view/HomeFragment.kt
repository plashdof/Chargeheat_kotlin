package com.week2.chargepig.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.week2.chargepig.*
import com.week2.chargepig.databinding.FragmentHomeBinding
import com.week2.chargepig.network.ProfileAPI
import com.week2.chargepig.network.RentAPI
import com.week2.chargepig.network.models.ProfileData
import com.week2.chargepig.network.models.RentData
import com.week2.chargepig.network.models.ResponseData
import com.week2.chargepig.view.qr.QrActivity
import com.week2.chargepig.view.qr.SuccessActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(){
    private lateinit var binding : FragmentHomeBinding
    private lateinit var navController: NavController

    private var RentRetro = Retrofit.getInstance().create(RentAPI::class.java)

    private var ProfileRetro = Retrofit.getInstance().create(ProfileAPI::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.btnFind.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_findFragment)
        }

        binding.btnEchopoint.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_echopointFragment)
        }


        ProfileRetro.getprofile(Singleton.id)
            .enqueue(object: Callback<ProfileData> {
                override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                    binding.tvEchopoint.text = "${response.body()!!.point.toString()}P"
                }

                override fun onFailure(call: Call<ProfileData>, t: Throwable) {}
            })

        binding.btnQr.setOnClickListener {

            val Activity = activity as MainActivity

            val integrator = IntentIntegrator(Activity)
            integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE) // ???????????? ??????????????? ?????? ????????? ?????? ??????
            integrator.setPrompt("????????? ????????? QR????????? ??????????????????") // ????????? ??? ????????? ??????
            integrator.setCameraId(0) // 0??? ?????? ?????????, 1??? ?????? ?????????
            integrator.setBeepEnabled(true) // ???????????? ???????????? ??? ??? ????????????
            integrator.setBarcodeImageEnabled(false) // ?????? ?????? ??? ????????? ????????? ????????????
            integrator.captureActivity = QrActivity::class.java
            activityResult.launch(integrator.createScanIntent()) // ??????
        }

    }

    private val activityResult = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val data = result.data

        val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, data)
        if(intentResult != null){

            //QRCode Scan ??????
            if(intentResult.contents != null){

                val data = RentData(intentResult.contents.toString().substring(22,28))

                if(intentResult.contents.toString().substring(22,28) != "001024"){
                    Toast.makeText(App.context(), "????????? ???????????? ????????????", Toast.LENGTH_SHORT).show()
                }else{
                    RentRetro
                        .rent(data)
                        .enqueue(object : Callback<ResponseData> {
                            override fun onResponse(
                                call: Call<ResponseData>,
                                response: Response<ResponseData>
                            ) {
                                Log.d("API??????", "${response.body()}")
                                if(response.body()?.code == 300){
                                    Toast.makeText(App.context(), "?????? ???????????? ??????????????????", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(App.context(), MainActivity::class.java)
                                    startActivity(intent)
                                }else if(response.body()?.code == 200){
                                    Toast.makeText(App.context(), "????????????: 001024 ?????? ??????", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(App.context(), SuccessActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                                Log.d("API??????", "?????? : $t")
                            }
                        })
                }

                //QRCode Scan result ????????????

//                if(Singleton.state){
//                    Toast.makeText(App.context(), "?????? ???????????? ??????????????????", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(App.context(), MainActivity::class.java)
//                    startActivity(intent)
//
//                }else{
//                    Toast.makeText(App.context(), "????????????: ${intentResult.contents.toString().substring(22,28)} ?????? ??????", Toast.LENGTH_SHORT).show()
//                    Singleton.state = true
//                    val intent = Intent(App.context(), SuccessActivity::class.java)
//                    startActivity(intent)
//                }
                

            }else{
                //QRCode Scan result ????????????
                Toast.makeText(App.context(), "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
            }
        }else{
            //QRCode Scan ??????
            Toast.makeText(App.context(), "QR????????? ??????????????????.", Toast.LENGTH_SHORT).show()
        }
    }


}