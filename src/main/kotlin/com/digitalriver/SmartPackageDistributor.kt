package com.digitalriver


class SmartPackageDistributor {

    private val boxLimit = 25

    val orders: MutableList<NamedOrder> = mutableListOf()

    val boxes: MutableList<PackagedBox> = mutableListOf()


    fun addOrder(order: NamedOrder): SmartPackageDistributor {
        orders.add(order)
        return this
    }

    fun computePacking() {

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
                    while(listIterator.hasNext()) {
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

    fun verifyPackage(): Boolean {

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

fun main(args: Array<String>) {

    val smartPackageDistributor = SmartPackageDistributor()
    smartPackageDistributor.addOrder(NamedOrder("Cherry", 6, 3))
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

    println("Total orders - beef: ${smartPackageDistributor.totalBeef()}, pork: ${smartPackageDistributor.totalPork()}")

    smartPackageDistributor.computePacking()
    val verified = smartPackageDistributor.verifyPackage()
    println("Package verification result: $verified")
}