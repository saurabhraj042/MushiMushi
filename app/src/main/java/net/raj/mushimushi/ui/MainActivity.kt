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
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        setContentView(R.layout.activity_main)
    }
}