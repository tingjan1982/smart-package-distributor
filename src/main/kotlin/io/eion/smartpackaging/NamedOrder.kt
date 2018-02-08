package io.eion.smartpackaging

data class NamedOrder(val name: String, val beef: Int = 0, val pork: Int = 0) {

    val linkedOrders : MutableList<NamedOrder> = mutableListOf()

    var linkedMasterOrder: NamedOrder? = null

    lateinit var linkedPackagedBox: PackagedBox

    fun total() : Int = beef + pork

    fun linkOrder(namedOrder : NamedOrder) {
        linkedOrders.add(namedOrder)
        namedOrder.linkedMasterOrder = this
    }

    fun assignPackagedBox(packagedBox : PackagedBox) {
        this.linkedPackagedBox = packagedBox
    }
}
