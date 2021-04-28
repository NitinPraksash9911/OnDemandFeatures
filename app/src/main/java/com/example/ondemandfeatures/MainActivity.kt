package com.example.ondemandfeatures

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ondemandfeatures.databinding.ActivityMainBinding
import com.example.ondemandfeatures.dfm.DfmCallingActions
import com.example.ondemandfeatures.dfm.DynamicFeatureCalling
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

const val TAG = "DynamicFeatures"
private const val DML_PKG = "com.example.readmind"
private const val READ_MIND_CLASSNAME = "$DML_PKG.ReadMind"
private const val DynamicFeatureCallingImpl_CLASSNAME = "$DML_PKG.DynamicFeatureCallingImpl"
private const val CONFIRMATION_REQUEST_CODE = 1


class MainActivity : AppCompatActivity() {
    private var DML_SESSION_ID: Int = 0
    lateinit var binding: ActivityMainBinding
    private lateinit var manager: SplitInstallManager
    private val moduleReadMind by lazy { getString(R.string.title_readmind) }

    /** Listener used to handle changes in state for install requests. */
    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        val names = state.moduleNames().joinToString(" - ")
        if (state.sessionId() == DML_SESSION_ID) {
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
                onSuccessfulLoad(names, launch = !multiInstall)
                    toastAndLog("$names is installed")

                }

                SplitInstallSessionStatus.INSTALLING -> displayLoadingState(
                    state,
                    "Installing $names"
                )
                SplitInstallSessionStatus.FAILED -> {
                    toastAndLog("Error: ${state.errorCode()} for module ${state.moduleNames()}")
                }
            }
        }
    }

    /** Display a loading state to the user. */
    private fun displayLoadingState(state: SplitInstallSessionState, message: String) {

        val max = state.totalBytesToDownload()
        val progress = state.bytesDownloaded()
        binding.progressBar.max = max.toInt()
        binding.progressBar.progress = progress.toInt()
        Log.d(TAG, "displayLoadingState: progress: $progress, max: $max")
        binding.progressTv.text = (progress.div(max).times(100)).toString()

    }

    /**
     * Define what to do once a feature module is loaded successfully.
     * @param moduleName The name of the successfully loaded module.
     * @param launch `true` if the feature module should be launched, else `false`.
     */
    private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            when (moduleName) {
                moduleReadMind -> DfmCallingActions.launchDfm(this)

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
        binding.installBtn.setOnClickListener {
            loadAndLaunchModule(moduleReadMind)

        }
        binding.uninstallBtn.setOnClickListener {
            unInstallModule(moduleReadMind)
        }

        binding.callMethod.setOnClickListener {
            DfmCallingActions.callMethodDFM()
        }


    }


    private fun unInstallModule(moduleName: String) {
        if (manager.installedModules.contains(moduleName)) {
            manager.deferredUninstall(listOf(moduleName))
            toastAndLog("it will remove within 24hr by playStore API")

        } else {
            toastAndLog("not present")
        }

    }

    /**
     * Load a feature by module name.
     * @param moduleName The name of the feature module to load.
     */
    private fun loadAndLaunchModule(moduleName: String) {
        // Skip loading if the module already is installed. Perform success action directly.
        if (manager.installedModules.contains(moduleName)) {
            onSuccessfulLoad(moduleName, launch = true)
            return
        }

        // Create request to install a feature module by name.
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        toastAndLog("installing read mind feature")
        manager.startInstall(request).addOnSuccessListener { id ->
            DML_SESSION_ID = id
        }.addOnFailureListener {

        }

    }

    fun toastAndLog(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        Log.d(TAG, text)
    }

    /** This required for user confirmation if DFM size is greater than 10mb */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            // Handle the user's decision. For example, if the user selects "Cancel",
            // you may want to disable certain functionality that depends on the module.
            if (resultCode == Activity.RESULT_CANCELED) {
                finish()

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}