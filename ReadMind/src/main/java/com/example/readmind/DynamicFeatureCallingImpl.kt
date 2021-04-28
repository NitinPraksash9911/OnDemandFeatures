package com.example.readmind

import android.content.Context
import com.example.ondemandfeatures.dfm.DynamicFeatureCalling

class DynamicFeatureCallingImpl : DynamicFeatureCalling {

    override fun callMethodFromDynamicModule() {

        ReadMind.dynamicMethod()
    }

    override fun launch(context: Context, vararg flags: Int) {

        ReadMind.launch(context)
    }
}