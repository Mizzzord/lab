package com.startracker

class StarCatalog {

    private val stars = mutableListOf<StarData>()
    val constellations = mutableListOf<Constellation>()

    init {
        initializeStars()
        initializeConstellations()
    }

    private fun initializeStars() {
        stars.add(StarData(1, 0.0, 0.0, 0.0f, "Полярная"))
        stars.add(StarData(2, 6.7525, -16.7161, 0.45f, "Сириус"))
        stars.add(StarData(3, 5.2423, -8.2016, 0.76f, "Канопус"))
        stars.add(StarData(4, 14.6598, -60.8350, 0.61f, "Альфа Центавра"))
        stars.add(StarData(5, 19.8463, 8.8683, 0.77f, "Вега"))
        stars.add(StarData(6, 6.3990, -52.6956, 0.86f, "Ригель"))
        stars.add(StarData(7, 5.9195, 7.4071, 0.13f, "Процион"))
        stars.add(StarData(8, 10.1269, 11.9672, 0.98f, "Бетельгейзе"))
        stars.add(StarData(9, 2.5303, 89.2641, 1.97f, "Полярная"))
        stars.add(StarData(10, 20.6905, 45.2803, 2.24f, "Денеб"))
        stars.add(StarData(11, 17.1462, -36.7123, 1.62f, "Ахернар"))
        stars.add(StarData(12, 4.5987, 16.5097, 1.90f, "Капелла"))
        stars.add(StarData(13, 12.4430, -63.0991, 1.25f, "Альфа Центавра B"))
        stars.add(StarData(14, 14.6608, -60.8350, 1.33f, "Альфа Центавра A"))
        stars.add(StarData(15, 16.4901, -26.4321, 1.50f, "Альдебаран"))
        stars.add(StarData(16, 3.3088, 15.1836, 1.58f, "Кастор"))
        stars.add(StarData(17, 3.3770, 15.1836, 1.90f, "Поллукс"))
        stars.add(StarData(18, 22.8708, -29.8281, 1.62f, "Фомальгаут"))
        stars.add(StarData(19, 1.9102, 29.0904, 1.76f, "Мирак"))
        stars.add(StarData(20, 2.0969, 29.0904, 2.07f, "Альголь"))
        
        for (i in 21..200) {
            val ra = (Math.random() * 360.0)
            val dec = (Math.random() * 180.0) - 90.0
            val mag = (Math.random() * 5.0 + 1.0).toFloat()
            stars.add(StarData(i, ra, dec, mag))
        }
    }

    private fun initializeConstellations() {
        constellations.add(Constellation("Большая Медведица", listOf(1, 12, 16, 17)))
        constellations.add(Constellation("Орион", listOf(6, 8, 15)))
        constellations.add(Constellation("Кассиопея", listOf(9, 10)))
    }

    fun getVisibleStars(azimuth: Float, pitch: Float, roll: Float): List<StarData> {
        val viewAzimuthRad = Math.toRadians(azimuth.toDouble())
        val viewPitchRad = Math.toRadians(pitch.toDouble())
        
        return stars.filter { star ->
            val raRad = Math.toRadians(star.rightAscension)
            val decRad = Math.toRadians(star.declination)
            
            val cosDec = Math.cos(decRad)
            val x = cosDec * Math.cos(raRad)
            val y = cosDec * Math.sin(raRad)
            val z = Math.sin(decRad)
            
            val cosPitch = Math.cos(viewPitchRad)
            val sinPitch = Math.sin(viewPitchRad)
            val cosAzimuth = Math.cos(viewAzimuthRad)
            val sinAzimuth = Math.sin(viewAzimuthRad)
            
            val x1 = x * cosAzimuth - y * sinAzimuth
            val y1 = x * sinAzimuth + y * cosAzimuth
            val z1 = z
            
            val z2 = y1 * sinPitch + z1 * cosPitch
            
            z2 > -0.2 && star.magnitude < 6.0f
        }
    }
}
