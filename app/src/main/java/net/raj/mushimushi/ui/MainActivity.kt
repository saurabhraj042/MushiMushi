package net.raj.mushimushi.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.grpc.android.BuildConfig
import net.raj.mushimushi.R
import timber.log.Timber
import timber.log.Timber.DebugTree


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(DebugTree())
        setContentView(R.layout.activity_main)
        Timber.d("OnCreate")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("OnStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("OnResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("OnPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("OnStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("OnDestroy")
    }

}