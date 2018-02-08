package com.digitalriver

class Delivery {

    private val dailyLimit = 4

    val boxesToDeliver = mutableSetOf<PackagedBox>()

    var deliverySealed: Boolean = false

    fun addPackagedBox(packagedBox : PackagedBox) {
        this.addPackagedBoxes(listOf(packagedBox))
    }

    fun addPackagedBoxes(packagedBoxes: Collection<PackagedBox>) {

        if (!deliverySealed) {
            boxesToDeliver.addAll(packagedBoxes)

            if (boxesToDeliver.size >= dailyLimit) {
                deliverySealed = true
            }
        }
    }

    fun isDeliveryExceededLimit() : Boolean {
        return boxesToDeliver.size > dailyLimit
    }

    override fun toString(): String {
        return "Delivery(boxesToDeliver=$boxesToDeliver, deliverySealed=$deliverySealed)"
    }


}