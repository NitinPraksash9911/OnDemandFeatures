package com.example.ondemandfeatures.dfm

import android.content.Context
import android.util.Log
import com.example.ondemandfeatures.App
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory


object DfmCallingActions {
    const val TAG = "DynamicFeatures"
    const val DML_PKG = "com.example.readmind"
    const val DynamicFeatureCallingImpl_CLASSNAME = "$DML_PKG.DynamicFeatureCallingImpl"
    val splitInstallManager = SplitInstallManagerFactory.create(App.getInstance())
    lateinit var dynamicFeatureCalling: DynamicFeatureCalling

    private fun getDfmCalling(): DynamicFeatureCalling {

        if (this::dynamicFeatureCalling.isInitialized.not()) {
            //using reflection
            dynamicFeatureCalling = Class.forName(DynamicFeatureCallingImpl_CLASSNAME)
                .newInstance() as DynamicFeatureCalling
            Log.d(
                TAG,
                "DfmCallingActions->getDfmCalling create new instance of DynamicFeatureCalling"
            )

        }
        return dynamicFeatureCalling

    }

    fun callMethodDFM() {
        if (splitInstallManager.installedModules.contains("ReadMind")) {
            getDfmCalling().callMethodFromDynamicModule()
        } else {
            Log.d(TAG, "DfmCallingActions: ReadMind feature is not installed")
        }
    }

    fun launchDfm(context: Context) {
        if (splitInstallManager.installedModules.contains("ReadMind")) {
            getDfmCalling().launch(context)
        } else {
            Log.d(TAG, "DfmCallingActions: ReadMind feature is not installed")
        }
    }
}

