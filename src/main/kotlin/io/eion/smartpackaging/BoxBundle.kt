package io.eion.smartpackaging

class BoxBundle {

    val packagedBoxes = mutableSetOf<PackagedBox>()

    fun bundlePackageBox(packagedBox: PackagedBox) {
        packagedBoxes.add(packagedBox)
        packagedBox.boxBundle = this
    }
}