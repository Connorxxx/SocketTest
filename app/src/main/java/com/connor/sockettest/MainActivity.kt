package com.connor.sockettest

import androidx.navigation.findNavController
import com.connor.sockettest.databinding.ActivityMainBinding
import com.drake.engine.base.EngineActivity


class MainActivity : EngineActivity<ActivityMainBinding>(R.layout.activity_main){

    override fun initData() {

    }

    override fun initView() {
        val navController = findNavController(R.id.nav)
    }

}