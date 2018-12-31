package com.intellyticshub.projectmyoffers

import com.intellyticshub.projectmyoffers.utils.OfferExtractor
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OfferExtractorUnitTest {


    @Test
    fun extractOfferCodeTest(){
        val message = "USE CODE TASTY! get 50% off up to Rs.100 on foods"
        val offerExtractor = OfferExtractor(message)
        assert(offerExtractor.extractOfferCode()=="TASTY")
    }

    @Test
    fun extractOfferTest(){
        val message="50% cashback on orders above Rs 699 (Max Discount Rs 450)"
        val offerExtractor = OfferExtractor(" ")
        println(offerExtractor.extractOffer())
    }

    @Test
    fun extractExpiryTest(){
        val offerExtractor= OfferExtractor("From 25th November")
       println(offerExtractor.extractExpiryDate("2018").first)
    }
}
