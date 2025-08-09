package dev.gaddal.echojournal.core.domain.util

/**
 * A sealed class that represents the different sorting orders used in an application.
 * SortOrder is used to indicate the direction of sorting, such as ascending or descending.
 */
sealed class SortOrder {

    /**
     * Represents the sorting order in ascending order.
     * This object is used to indicate that elements should be arranged in ascending order,
     * typically from the smallest to the largest.
     */
    data object Ascending : SortOrder()

    /**
     * Represents the sorting order in descending order.
     * This object is used to indicate that elements should be arranged in descending order,
     * typically from the largest to the smallest.
     */
    data object Descending : SortOrder()
}
