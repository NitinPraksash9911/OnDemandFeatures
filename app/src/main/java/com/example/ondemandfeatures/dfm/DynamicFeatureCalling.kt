package com.example.ondemandfeatures.dfm

import android.content.Context

interface DynamicFeatureCalling {

    fun callMethodFromDynamicModule()

    fun launch(context: Context, vararg flags: Int)
}