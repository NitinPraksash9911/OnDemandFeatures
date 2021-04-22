package com.example.ondemandfeatures.dfm

import android.util.Log
import com.example.ondemandfeatures.App
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

const val TAG = "DynamicFeatures"
private const val DML_PKG = "com.example.readmind"
private const val DynamicFeatureCallingImpl_CLASSNAME = "$DML_PKG.DynamicFeatureCallingImpl"

object DfmCallingActions {
    private val splitInstallManager = SplitInstallManagerFactory.create(App.getInstance()!!)
    private lateinit var dynamicFeatureCalling: DynamicFeatureCalling

    private fun getDfmCalling(): DynamicFeatureCalling {

        if (this::dynamicFeatureCalling.isInitialized.not()) {
            //using reflection
            dynamicFeatureCalling = Class.forName(DynamicFeatureCallingImpl_CLASSNAME)
                .newInstance() as DynamicFeatureCalling
            Log.d(TAG, "DfmCallingActions->getDfmCalling create new instance of DynamicFeatureCalling")

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
}

