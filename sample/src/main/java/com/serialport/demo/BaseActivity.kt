package com.serialport.demo

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding : ViewDataBinding = DataBindingUtil.setContentView(this,getLayoutId())
        dataBinding.lifecycleOwner = this
        initVariable(dataBinding)
        initView()
        initListener()
        bindData(savedInstanceState)
    }
    open fun getLayoutId():Int{
        return 0
    }
    open fun initVariable(dataBinding : ViewDataBinding){

    }
    open fun initView(){

    }
    open fun initListener(){

    }
    open fun bindData(savedInstanceState: Bundle?){

    }


}