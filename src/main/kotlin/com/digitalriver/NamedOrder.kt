package com.digitalriver

data class NamedOrder(val name: String, val beef: Int = 0, val pork: Int = 0) {

    private val linkedOrders : List<NamedOrder> = mutableListOf()

    fun total() : Int = beef + pork
}
