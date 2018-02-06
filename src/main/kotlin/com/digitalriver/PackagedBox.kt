package com.digitalriver

class PackagedBox {

    private val boxLimit = 25

    val namedOrders: MutableList<NamedOrder> = mutableListOf()

    private var currentTotal: Int = 0

    private var sealed: Boolean = false

    fun addOrder(namedOrder: NamedOrder) : List<PackagedBox>? {

        if (namedOrder.total() > boxLimit) {
            return handleLargeOrders(namedOrder)
        }

        if (currentTotal + namedOrder.total() > boxLimit) {
            throw RuntimeException("Exceeded box limit")
        }

        namedOrders.add(namedOrder)
        namedOrder.assignPackagedBox(this)
        currentTotal += namedOrder.total()

        if (currentTotal == boxLimit) {
            seal()
        }

        return null
    }

    private fun handleLargeOrders(namedOrder: NamedOrder): List<PackagedBox> {

        var beefCount = namedOrder.beef
        var porkCount = namedOrder.pork
        val chunkedBoxes = mutableListOf<PackagedBox>()

        while (beefCount > 0 || porkCount > 0) {
            val beef: Int = if (beefCount > boxLimit) boxLimit else beefCount
            var pork = 0
            val remaining = boxLimit - beef

            if (remaining > 0) {
                pork = if (porkCount > remaining) remaining else porkCount

            }

            beefCount -= beef
            porkCount -= pork

            val chunkedOrder = NamedOrder(namedOrder.name, beef = beef, pork = pork)
            namedOrder.linkOrder(chunkedOrder)

            val packagedBox = PackagedBox()
            packagedBox.addOrder(chunkedOrder)
            chunkedBoxes.add(packagedBox)
        }

        return chunkedBoxes
    }

    private fun seal() {
        sealed = true
    }

    fun mergePackageBox(boxToMerge : PackagedBox) : Boolean {

        if (currentTotal + boxToMerge.packageTotal() <= boxLimit) {
            boxToMerge.namedOrders.forEach { namedOrder ->
                addOrder(namedOrder)
            }

            return true
        }

        return false
    }

    fun isSealed(): Boolean {
        return sealed
    }

    fun packageTotal() : Int {
        return currentTotal
    }

    override fun toString(): String {
        return "PackagedBox(namedOrders=$namedOrders, currentTotal=$currentTotal, sealed=$sealed)"
    }


}