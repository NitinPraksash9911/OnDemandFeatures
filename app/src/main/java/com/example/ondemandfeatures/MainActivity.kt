package com.example.ondemandfeatures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.ondemandfeatures.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

private const val TAG = "DynamicFeatures"
private const val READ_MIND_CLASSNAME = "com.example.readmind.ReadMind"
private const val CONFIRMATION_REQUEST_CODE = 1


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var manager: SplitInstallManager
    private val moduleReadMind by lazy { getString(R.string.title_readmind) }

    /** Listener used to handle changes in state for install requests. */
    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        val names = state.moduleNames().joinToString(" - ")
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                //  In order to see this, the application has to be uploaded to the Play Store.
                displayLoadingState(state, "Downloading $names")
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                /*
                  This may occur when attempting to download a sufficiently large module.

                  In order to see this, the application has to be uploaded to the Play Store.
                  Then features can be requested until the confirmation path is triggered.
                 */
                manager.startConfirmationDialogForResult(state, this, CONFIRMATION_REQUEST_CODE)
            }
            SplitInstallSessionStatus.INSTALLED -> {
//                onSuccessfulLoad(names, launch = !multiInstall)
                toastAndLog("$names is installed")

            }

            SplitInstallSessionStatus.INSTALLING -> displayLoadingState(state, "Installing $names")
            SplitInstallSessionStatus.FAILED -> {
                toastAndLog("Error: ${state.errorCode()} for module ${state.moduleNames()}")
            }
        }
    }

    /** Display a loading state to the user. */
    private fun displayLoadingState(state: SplitInstallSessionState, message: String) {

        binding.progressBar.max = state.totalBytesToDownload().toInt()
        binding.progressBar.progress = state.bytesDownloaded().toInt()

    }

    /**
     * Define what to do once a feature module is loaded successfully.
     * @param moduleName The name of the successfully loaded module.
     * @param launch `true` if the feature module should be launched, else `false`.
     */
    private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            when (moduleName) {
                moduleReadMind -> launchActivity(READ_MIND_CLASSNAME)

            }
        }
    }

    /** Launch an activity by its class name. */
    private fun launchActivity(className: String) {
        val intent = Intent().setClassName(BuildConfig.APPLICATION_ID, className)
        startActivity(intent)
    }

    override fun onResume() {
        // Listener can be registered even without directly triggering a download.
        manager.registerListener(listener)
        super.onResume()
    }

    override fun onPause() {
        // Make sure to dispose of the listener once it's no longer needed.
        manager.unregisterListener(listener)
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        manager = SplitInstallManagerFactory.create(this)
        binding.readBtn.setOnClickListener {
//            onSuccessfulLoad(moduleReadMind, true)
            loadAndLaunchModule(moduleReadMind)

        }


    }

    /**
     * Load a feature by module name.
     * @param name The name of the feature module to load.
     */
    private fun loadAndLaunchModule(name: String) {
//        updateProgressMessage(getString(R.string.loading_module, name))
        // Skip loading if the module already is installed. Perform success action directly.
        if (manager.installedModules.contains(name)) {
//            updateProgressMessage(getString(R.string.already_installed))
            onSuccessfulLoad(name, launch = true)
            return
        }

        // Create request to install a feature module by name.
        val request = SplitInstallRequest.newBuilder()
            .addModule(name)
            .build()

        toastAndLog("installing read mind feature")
        manager.startInstall(request)

//        updateProgressMessage(getString(R.string.starting_install_for, name))
    }

    fun toastAndLog(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        Log.d(TAG, text)
    }
}