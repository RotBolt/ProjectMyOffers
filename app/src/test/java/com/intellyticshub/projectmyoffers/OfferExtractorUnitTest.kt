package com.intellyticshub.projectmyoffers

import com.intellyticshub.projectmyoffers.utils.OfferExtractor
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OfferExtractorUnitTest {

    private val message =
        "SPAR M-COUPON! Show Coupon F8X7WN to Rs.100 off on purchase off Rs.899 & above. Valid till 31 Dec. T&C"
    private val message1 =
        "Fill your tummy with half the bill. Get 50% off your first 2 Uber eats orders, use code 50TREAT. TCA. Order Now"

    private val message2 = "Its your Birthday month & we have a Gift for you! Get Rs.300 OFF on Fashion Shopping of Rs.1500." +
            "\n \n Use Code: 609380671480 \n valid only @ fbb/Big Bazaar stores till 10 Jan + earn PAYBACK Points" +
            "\n or \n Use BDAY300 on: bit.ly/fbbonline TnC\n Ignore, if used"

    @Test
    fun extractOfferCodeTest() {

        var offerExtractor = OfferExtractor(message)
        assert(offerExtractor.extractOfferCode() == "F8X7WN")
        offerExtractor = OfferExtractor(message1)
        assert(offerExtractor.extractOfferCode() == "50TREAT")
        offerExtractor = OfferExtractor(message2)
        assert(offerExtractor.extractOfferCode()=="609380671480")

    }

    @Test
    fun extractOfferTest() {
        var offerExtractor = OfferExtractor(message)
        assert(offerExtractor.extractOffer() == "Rs.100 off. ")
        offerExtractor = OfferExtractor(message1)
        assert(offerExtractor.extractOffer() == "50% off. ")
        offerExtractor = OfferExtractor(message2)
        assert(offerExtractor.extractOffer()=="Rs.300 OFF. ")

    }

    @Test
    fun extractExpiryTest() {
        val defaultYYYY = "2019"
        var offerExtractor = OfferExtractor(message)
        println(offerExtractor.extractExpiryDate(defaultYYYY))
        offerExtractor = OfferExtractor(message1)
        println(offerExtractor.extractExpiryDate(defaultYYYY))
        offerExtractor = OfferExtractor(message2)
        println(offerExtractor.extractExpiryDate(defaultYYYY))
        offerExtractor = OfferExtractor("25Oct")
        println(offerExtractor.extractExpiryDate(defaultYYYY))

    }
}
