package com.itp.android.simpleadapter_kotlin

/**
 * Created by decky on 5/21/15.
 */
class AdapterGroupItem<T : AdapterItem> @JvmOverloads constructor(var childrens: AdapterDataSet<T> = AdapterDataSet<T>()) : AdapterItem()