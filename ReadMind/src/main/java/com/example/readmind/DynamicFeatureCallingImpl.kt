package com.example.readmind

import com.example.ondemandfeatures.dfm.DynamicFeatureCalling

class DynamicFeatureCallingImpl : DynamicFeatureCalling {

    override fun callMethodFromDynamicModule() {

        ReadMind.dynamicMethod()
    }
}