package io.eion.smartpackaging


class SmartPackageDistributor {

    // test for upsource
    private val boxLimit = PackagedBox.boxLimit

    private val orders: MutableList<NamedOrder> = mutableListOf()

    private val boxes: MutableList<PackagedBox> = mutableListOf()


    fun addAndFulfillOrders(orders: List<NamedOrder>) {

        this.addOrders(orders)
        this.fulfillOrders()
    }

    fun fulfillOrders() {
        println("Total orders - beef: ${this.totalBeef()}, pork: ${this.totalPork()}")

        this.computeBoxPackaging()
        val verified = this.verifyPackages()
        println("Package verification result: $verified")

        this.computeDeliveries()
    }

    /**
     * Adds order to this class fluently.
     */
    fun addOrder(order: NamedOrder): SmartPackageDistributor {
        orders.add(order)
        return this
    }

    private fun addOrders(orders: List<NamedOrder>): SmartPackageDistributor {
        this.orders.addAll(orders)
        return this
    }

    private fun computeBoxPackaging() {

        val copiedOrders = orders.toMutableList()
        println("Current orders: $copiedOrders")

        var box = PackagedBox()
        boxes.add(box)

        for (copiedOrder in copiedOrders) {
            try {
                val chunkedBoxes = box.addOrder(copiedOrder)

                chunkedBoxes?.forEach { chunkedBox ->
                    boxes.add(chunkedBox)
                }

            } catch (e: Exception) {
                box = PackagedBox()
                boxes.add(box)
                box.addOrder(copiedOrder)
            }
        }

        optimizePackaging()
        println("Total boxes: ${boxes.size}")

        boxes.forEach { b -> println("Box content: $b") }
    }

    private fun optimizePackaging() {

        val unsealedBoxes = boxes.filter { box -> !box.isSealed() }.toMutableList()
        val unsealedPackagedTotal = unsealedBoxes.map { box -> box.packageTotal() }
                .sum()

        val optimalBoxCount = if (unsealedPackagedTotal % boxLimit > 0) unsealedPackagedTotal / boxLimit + 1 else unsealedPackagedTotal / boxLimit

        // can optimize
        if (optimalBoxCount < unsealedBoxes.size) {
            var boxCountToOptimize = unsealedBoxes.size - optimalBoxCount
            println("Optimization opportunity discovered, attempting to optimize $boxCountToOptimize boxes.")

            // eliminate boxes that cannot be combined with other boxes
            val iter = unsealedBoxes.iterator()
            iter.forEach { box ->
                val candidateForBoxMerge = unsealedBoxes.filter { b -> b != box }
                        .filter { b -> b.packageTotal() + box.packageTotal() <= boxLimit }
                        .count() > 0

                if (!candidateForBoxMerge) {
                    iter.remove()
                }
            }

            println("Eligible to merge boxes: $unsealedBoxes")
            val listIterator = unsealedBoxes.listIterator()

            while (boxCountToOptimize > 0) {
                listIterator.forEach { unsealedBox ->
                    while (listIterator.hasNext()) {
                        val boxToMerge = listIterator.next()

                        if (unsealedBox.mergePackageBox(boxToMerge)) {
                            listIterator.remove()
                            boxes.remove(boxToMerge)
                            boxCountToOptimize -= 1
                        }
                    }
                }
            }
        }
    }

    private fun computeDeliveries() {

        val deliveries = mutableListOf<Delivery>()
        var delivery = Delivery()
        deliveries.add(delivery)

        // handle box bundles first
        boxes.filter { it.boxBundle != null }
                .map { it.boxBundle!!.packagedBoxes }
                .forEach {
                    delivery.addPackagedBoxes(it)

                    if (delivery.deliverySealed) {
                        delivery = Delivery()
                        deliveries.add(delivery)
                    }
                }

        boxes.filter { it.boxBundle == null }
                .forEach {
                    delivery.addPackagedBox(it)

                    if (delivery.deliverySealed) {
                        delivery = Delivery()
                        deliveries.add(delivery)
                    }
                }

        if (deliveries.filter { !it.deliverySealed }.count() > 0 && deliveries.filter { it.isDeliveryExceededLimit() }.count() > 0) {
            // opportunity to optimize delivery
            println("Need optimization")
        }

        println("Number of created deliveries: ${deliveries.size}")

        deliveries.forEach {
            println("Delivery - package count: ${it.boxesToDeliver.size}")

            it.boxesToDeliver.forEach { println(it) }
        }

    }

    fun verifyPackages(): Boolean {

        val totalPackagedCount = boxes.flatMap { box -> box.namedOrders }
                .map { order -> order.total() }
                .sum()

        println("Total packaged count: $totalPackagedCount")
        return totalPackagedCount == totalBeef() + totalPork()
    }

    fun totalBeef(): Int {
        return orders.stream().mapToInt { namedOrder -> namedOrder.beef }
                .sum()
    }

    fun totalPork(): Int {
        return orders.stream().mapToInt { namedOrder -> namedOrder.pork }
                .sum()
    }
}


val order1 = listOf(
        NamedOrder("Anita", beef = 5),
        NamedOrder("Cliff", 5, 6),
        NamedOrder("Daniel", beef = 10),
        NamedOrder("Edison", 5, 5),
        NamedOrder("Felix", 6, 6),
        NamedOrder("Jane", beef = 10),
        NamedOrder("Otis", beef = 5),
        NamedOrder("Olga", beef = 6),
        NamedOrder("Ayi", beef = 2),
        NamedOrder("Uncle", beef = 6)
)

fun main(args: Array<String>) {

    val yearEndOrder = SmartPackageDistributor()
    yearEndOrder.addAndFulfillOrders(order1)

    val previousOrder = SmartPackageDistributor()
    previousOrder.addOrder(NamedOrder("Cherry", 6, 3))
            .addOrder(NamedOrder("Anke", beef = 4))
            .addOrder(NamedOrder("Afra", beef = 4))
            .addOrder(NamedOrder("Chavez", beef = 6))
            .addOrder(NamedOrder("Darren", beef = 4))
            .addOrder(NamedOrder("Eric", 10, 2))
            .addOrder(NamedOrder("Grace", beef = 5))
            .addOrder(NamedOrder("Jacky", beef = 30))
            .addOrder(NamedOrder("Jennifer", beef = 3))
            .addOrder(NamedOrder("Katherine", beef = 15))
            .addOrder(NamedOrder("Kristine", beef = 4))
            .addOrder(NamedOrder("Olga", beef = 6))
            .addOrder(NamedOrder("Sebrina", 15, 20))
            .addOrder(NamedOrder("Sky", beef = 6))
            .addOrder(NamedOrder("Stoney", beef = 5))
            .addOrder(NamedOrder("YP", pork = 5))
    previousOrder.fulfillOrders()

}