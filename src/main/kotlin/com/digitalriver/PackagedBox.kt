package com.digitalriver

class PackagedBox {

    private val boxLimit = 25

    val namedOrders: MutableList<NamedOrder> = mutableListOf()

    private var currentTotal: Int = 0

    private var sealed: Boolean = false

    var boxBundle: BoxBundle? = null


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

    private fun handleLargeOrders(mainOrder: NamedOrder): List<PackagedBox> {

        var beefCount = mainOrder.beef
        var porkCount = mainOrder.pork
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

            val chunkedOrder = NamedOrder(mainOrder.name, beef = beef, pork = pork)
            mainOrder.linkOrder(chunkedOrder)

            val packagedBox = PackagedBox()
            packagedBox.addOrder(chunkedOrder)
            chunkedBoxes.add(packagedBox)
        }

        if (chunkedBoxes.isNotEmpty()) {
            val boxBundle = BoxBundle()

            chunkedBoxes.forEach {
                boxBundle.bundlePackageBox(it)
            }
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

            // if the box to merge contains reference to other boxes
            if (boxToMerge.boxBundle != null) {
                if (this.boxBundle == null) {
                    this.boxBundle = BoxBundle()
                }

                boxToMerge.boxBundle?.packagedBoxes!!
                        .filter { it != boxToMerge }
                        .forEach { this.boxBundle!!.bundlePackageBox(it) }
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