package com.finde.deliveryapp

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.finde.deliveryapp.databinding.ActivityMainUiBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mapbox.mapboxsdk.Mapbox


class MainActivity : AppCompatActivity() {


    //    private var symbol:Symbol? = null
    private lateinit var binding: ActivityMainUiBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, getString(R.string.map_box_token))
        super.onCreate(savedInstanceState)
        binding = ActivityMainUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment

        val navHostController = navHostFragment.navController


        val dialogMultiplePermissionsListener: MultiplePermissionsListener =
            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(applicationContext)
                .withTitle("Action Required")
                .withMessage("The app needs both camera,location to work properly")
                .withButtonText("Okay")
                .build()


        Dexter.withContext(applicationContext)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(dialogMultiplePermissionsListener)


    }


}
