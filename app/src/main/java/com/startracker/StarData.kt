package com.startracker

data class StarData(
    val id: Int,
    val rightAscension: Double,
    val declination: Double,
    val magnitude: Float,
    val name: String? = null
)

data class Constellation(
    val name: String,
    val starIds: List<Int>
)
