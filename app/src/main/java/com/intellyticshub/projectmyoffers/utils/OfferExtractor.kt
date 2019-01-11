package com.intellyticshub.projectmyoffers.utils

import java.text.SimpleDateFormat
import java.util.*

class OfferExtractor(private val message: String) {


    fun extractOfferCode(): String {

        val identifiers = "(code|CODE|Code|coupon|Coupon|COUPON)"

        val codeFilter = Regex("([A-Z0-9]|-|_)+")
        val filterOperation = { string: String ->
            codeFilter.findAll(string).filter { thisMatch -> thisMatch.value != "CODE" && thisMatch.value != "C" }
                .elementAt(0).value
        }

        fun String.validateCode() = length > 2 && first() != '-' && first() != '_'

        val codeRegex0 = Regex("$identifiers(:|-|\\s|is|IS|Is|of|Of|OF)+([A-Z0-9]|-|_)+")
        val result0 = codeRegex0.find(message)
        val code0 = result0?.let { filterOperation(it.value) }
        code0?.let {
            if (it.validateCode())
                return it
        }

        val codeRegex1 = Regex("([A-Z0-9]|-|_)+ $identifiers")
        val result1 = codeRegex1.find(message)
        val code1 = result1?.let { filterOperation(it.value) }
        code1?.let {
            if (it.validateCode())
                return it
        }

        return "none"
    }

    fun extractOffer(): String {
        val offerRegex0 = Regex(
            "((max |min |)(cashback|discount)( is | of | )([0-9]+(%| %|rs| rs)|rs(.|. | )[0-9]+))|((flat |)([0-9]+(%| %|rs| rs)|rs(.|. | )[0-9]+)(off| off))|(([0-9]+(%| %|rs| rs)|rs(.|. | )[0-9]+)( max| max | min| min| |)(cashback|discount))",
            RegexOption.IGNORE_CASE
        )
        val offer = offerRegex0.findAll(message).fold("") { acc, matchResult ->
            acc + matchResult.value + ". "
        }

        return if (offer != "") offer else "none"
    }

    fun extractExpiryDate(defaultYYYY: String): ExpiryDateInfo {
        val dateExtractor = DateExtractor(defaultYYYY)
        return dateExtractor.extractDateMillis(message)
    }


    data class ExpiryDateInfo(
        val expiryDate: String,
        val expiryTimeInMillis: Long
    )

    private class DateExtractor(private val defaultYYYY: String) {

        private val MM = "(0[1-9]|1[012])"
        private val MMM = "(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)"
        private val MMMMM = "(january|february|march|april|may|june|july|august|september|october|november|december)"

        private val dd = "([1-9]|0[1-9]|[12][0-9]|3[01])"

        private val yyyy = "2\\d\\d\\d"
        private val yy = "\\d\\d"


        private val datePatterns0 = mapOf(
            "dd/MM/yyyy" to "$dd/$MM/$yyyy",
            "dd/MM/yy" to "$dd/$MM/$yy",
            "MM/dd/yyyy" to "$MM/$dd/$yyyy",
            "MM/dd/yy" to "$MM/$dd/$yy",
            "yyyy/MM/dd" to "$yyyy/$MM/$dd",
            "yyyy/dd/MM" to "$yyyy/$dd/$MM",
            "MM/yyyy" to "$MM/$yyyy",

            "dd MM yyyy" to "$dd $MM $yyyy",
            "dd MM yy" to "$dd $MM $yy",
            "MM dd yyyy" to "$MM $dd $yyyy",
            "MM dd yy" to "$MM $dd $yy",
            "yyyy MM dd" to "$yyyy $MM $dd",
            "yyyy dd MM" to "$yyyy $dd $MM",
            "MM yyyy" to "$MM $yyyy",

            "dd-MM-yyyy" to "$dd-$MM-$yyyy",
            "dd-MM-yy" to "$dd-$MM-$yy",
            "MM-dd-yyyy" to "$MM-$dd-$yyyy",
            "MM-dd-yy" to "$MM-$dd-$yy",
            "yyyy-MM-dd" to "$yyyy-$MM-$dd",
            "yyyy-dd-MM" to "$yyyy-$dd-$MM",
            "MM-yyyy" to "$MM-$yyyy",

            "dd.MM.yyyy" to "$dd\\.$MM\\.$yyyy",
            "dd.MM.yy" to "$dd\\.$MM\\.$yy",
            "MM.dd.yyyy" to "$MM\\.$dd\\.$yyyy",
            "MM.dd.yy" to "$MM\\.$dd\\.$yy",
            "yyyy.MM.dd" to "$yyyy\\.$MM\\.$dd",
            "yyyy.dd.MM" to "$yyyy\\.$dd\\.$MM",
            "MM.yyyy" to "$MM\\.$yyyy",

            "dd MMM yyyy" to "$dd $MMM $yyyy",
            "dd MMM yy" to "$dd $MMM $yy",
            "MMM dd yyyy" to "$MMM $dd $yyyy",
            "MMM dd yy" to "$MMM $dd $yy",
            "yyyy MMM dd" to "$yyyy $MMM $dd",
            "yyyy dd MMM" to "$yyyy $dd $MMM",
            "MMM yyyy" to "$MMM $yyyy",

            "dd-MMM-yyyy" to "$dd-$MMM-$yyyy",
            "dd-MMM-yy" to "$dd-$MMM-$yy",
            "MMM-dd-yyyy" to "$MMM-$dd-$yyyy",
            "MMM-dd-yy" to "$MMM-$dd-$yy",
            "yyyy-MMM-dd" to "$yyyy-$MMM-$dd",
            "yyyy-dd-MMM" to "$yyyy-$dd-$MMM",
            "MMM-yyyy" to "$MMM-$yyyy",

            "dd/MMM/yyyy" to "$dd/$MMM/$yyyy",
            "dd/MMM/yy" to "$dd/$MMM/$yy",
            "MMM/dd/yyyy" to "$MMM/$dd/$yyyy",
            "MMM/dd/yy" to "$MMM/$dd/$yy",
            "yyyy/MMM/dd" to "$yyyy/$MMM/$dd",
            "yyyy/dd/MMM" to "$yyyy/$dd/$MMM",
            "MMM/yyyy" to "$MMM/$yyyy",

            "dd.MMM.yyyy" to "$dd\\.$MMM\\.$yyyy",
            "dd.MMM.yy" to "$dd\\.$MMM\\.$yy",
            "MMM.dd.yyyy" to "$MMM\\.$dd\\.$yyyy",
            "MMM.dd.yy" to "$MMM\\.$dd\\.$yy",
            "yyyy.MMM.dd" to "$yyyy\\.$MMM\\.$dd",
            "yyyy.dd.MMM" to "$yyyy\\.$dd\\.$MMM",
            "MMM.yyyy" to "$MMM\\.$yyyy",

            "dd MMMMM yyyy" to "$dd $MMMMM $yyyy",
            "dd MMMMM yy" to "$dd $MMMMM $yy",
            "MMMMM dd yyyy" to "$MMMMM $dd $yyyy",
            "MMMMM dd yy" to "$MMMMM $dd $yy",
            "yyyy MMMMM dd" to "$yyyy $MMMMM $dd",
            "yyyy dd MMMMM" to "$yyyy $dd $MMMMM",
            "MMMMM yyyy" to "$MMMMM $yyyy",

            "dd-MMMMM-yyyy" to "$dd-$MMMMM-$yyyy",
            "dd-MMMMM-yy" to "$dd-$MMMMM-$yy",
            "MMMMM-dd-yyyy" to "$MMMMM-$dd-$yyyy",
            "MMMMM-dd-yy" to "$MMMMM-$dd-$yy",
            "yyyy-MMMMM-dd" to "$yyyy-$MMMMM-$dd",
            "yyyy-dd-MMMMM" to "$yyyy-$dd-$MMMMM",
            "MMMMM-yyyy" to "$MMMMM-$yyyy",

            "dd/MMMMM/yyyy" to "$dd/$MMMMM/$yyyy",
            "dd/MMMMM/yy" to "$dd/$MMMMM/$yy",
            "MMMMM/dd/yyyy" to "$MMMMM/$dd/$yyyy",
            "MMMMM/dd/yy" to "$MMMMM/$dd/$yy",
            "yyyy/MMMMM/dd" to "$yyyy/$MMMMM/$dd",
            "yyyy/dd/MMMMM" to "$yyyy/$dd/$MMMMM",
            "MMMMM/yyyy" to "$MMMMM/$yyyy",

            "dd.MMMMM.yyyy" to "$dd\\.$MMMMM\\.$yyyy",
            "dd.MMMMM.yy" to "$dd\\.$MMMMM\\.$yy",
            "MMMMM.dd.yyyy" to "$MMMMM\\.$dd\\.$yyyy",
            "MMMMM.dd.yy" to "$MMMMM\\.$dd\\.$yy",
            "yyyy.MMMMM.dd" to "$yyyy\\.$MMMMM\\.$dd",
            "yyyy.dd.MMMMM" to "$yyyy\\.$dd\\.$MMMMM",
            "MMMMM.yyyy" to "$MMMMM\\.$yyyy",


            "MMM, yyyy" to "$MMM, $yyyy",

            "MMMMM, yyyy" to "$MMMMM, $yyyy"
        )

        private val datePatterns1 = mapOf(
            "dd/MM" to "$dd/$MM",
            "MM/dd" to "$MM/$dd",

            "dd MMM" to "$dd $MMM",
            "ddMMM" to "$dd$MMM",

            "dd'st' MMM" to "${dd}st $MMM",
            "dd'nd' MMM" to "${dd}nd $MMM",
            "dd'rd' MMM" to "${dd}rd $MMM",
            "dd'th' MMM" to "${dd}th $MMM",

            "dd'st,' MMM" to "${dd}st, $MMM",
            "dd'nd,' MMM" to "${dd}nd, $MMM",
            "dd'rd,' MMM" to "${dd}rd, $MMM",
            "dd'th,' MMM" to "${dd}th, $MMM",

            "dd MMMMM" to "$dd $MMMMM",

            "dd'st' MMMMM" to "${dd}st $MMMMM",
            "dd'nd' MMMMM" to "${dd}nd $MMMMM",
            "dd'rd' MMMMM" to "${dd}rd $MMMMM",
            "dd'th' MMMMM" to "${dd}th $MMMMM",

            "dd'st,' MMMMM" to "${dd}st, $MMMMM",
            "dd'nd,' MMMMM" to "${dd}nd, $MMMMM",
            "dd'rd,' MMMMM" to "${dd}rd, $MMMMM",
            "dd'th,' MMMMM" to "${dd}th, $MMMMM",


            "MMM dd" to "$MMM $dd",

            "MMM dd'st'" to "$MMM ${dd}st",
            "MMM dd'nd'" to "$MMM ${dd}nd",
            "MMM dd'rd'" to "$MMM ${dd}rd",
            "MMM dd'th'" to "$MMM ${dd}th",

            "MMM',' dd'st'" to "$MMM, ${dd}st",
            "MMM',' dd'nd'" to "$MMM, ${dd}nd",
            "MMM',' dd'rd'" to "$MMM, ${dd}rd",
            "MMM',' dd'th'" to "$MMM, ${dd}th",

            "MMMMM dd" to "$MMMMM $dd",

            "MMMMM dd'st'" to "$MMMMM ${dd}st",
            "MMMMM dd'nd'" to "$MMMMM ${dd}nd",
            "MMMMM dd'rd'" to "$MMMMM ${dd}rd",
            "MMMMM dd'th'" to "$MMMMM ${dd}th",

            "MMMMM',' dd'st'" to "$MMMMM, ${dd}st",
            "MMMMM',' dd'nd'" to "$MMMMM, ${dd}nd",
            "MMMMM',' dd'rd'" to "$MMMMM, ${dd}rd",
            "MMMMM',' dd'th'" to "$MMMMM, ${dd}th"
        )

        private val datePatterns2 = mapOf(
            "dd'st' MMM yyyy" to "${dd}st $MMM $yyyy",
            "dd'nd' MMM yyyy" to "${dd}nd $MMM $yyyy",
            "dd'rd' MMM yyyy" to "${dd}rd $MMM $yyyy",
            "dd'th' MMM yyyy" to "${dd}th $MMM $yyyy",

            "dd MMM',' yyyy" to "$dd $MMM, $yyyy",

            "dd'st' MMM',' yyyy" to "${dd}st $MMM, $yyyy",
            "dd'nd' MMM',' yyyy" to "${dd}nd $MMM, $yyyy",
            "dd'rd' MMM',' yyyy" to "${dd}rd $MMM, $yyyy",
            "dd'th' MMM',' yyyy" to "${dd}th $MMM, $yyyy",

            "MMM dd'st' yyyy" to "$MMM ${dd}st $yyyy",
            "MMM dd'nd' yyyy" to "$MMM ${dd}nd $yyyy",
            "MMM dd'rd' yyyy" to "$MMM ${dd}rd $yyyy",
            "MMM dd'th' yyyy" to "$MMM ${dd}th $yyyy",

            "MMM dd'st,' yyyy" to "$MMM ${dd}st, $yyyy",
            "MMM dd'nd,' yyyy" to "$MMM ${dd}nd, $yyyy",
            "MMM dd'rd,' yyyy" to "$MMM ${dd}rd, $yyyy",
            "MMM dd'th,' yyyy" to "$MMM ${dd}th, $yyyy",


            "dd'st' MMMMM yyyy" to "${dd}st $MMMMM $yyyy",
            "dd'nd' MMMMM yyyy" to "${dd}nd $MMMMM $yyyy",
            "dd'rd' MMMMM yyyy" to "${dd}rd $MMMMM $yyyy",
            "dd'th' MMMMM yyyy" to "${dd}th $MMMMM $yyyy",

            "dd MMMMM',' yyyy" to "$dd $MMMMM, $yyyy",
            "dd'st' MMMMM',' yyyy" to "${dd}st $MMMMM, $yyyy",
            "dd'nd' MMMMM',' yyyy" to "${dd}nd $MMMMM, $yyyy",
            "dd'rd' MMMMM',' yyyy" to "${dd}rd $MMMMM, $yyyy",
            "dd'th' MMMMM',' yyyy" to "${dd}th $MMMMM, $yyyy",

            "MMMMM dd'st' yyyy" to "$MMMMM ${dd}st $yyyy",
            "MMMMM dd'nd' yyyy" to "$MMMMM ${dd}nd $yyyy",
            "MMMMM dd'rd' yyyy" to "$MMMMM ${dd}rd $yyyy",
            "MMMMM dd'th' yyyy" to "$MMMMM ${dd}th $yyyy",

            "MMMMM dd'st,' yyyy" to "$MMMMM ${dd}st, $yyyy",
            "MMMMM dd'nd,' yyyy" to "$MMMMM ${dd}nd, $yyyy",
            "MMMMM dd'rd,' yyyy" to "$MMMMM ${dd}rd, $yyyy",
            "MMMMM dd'th,' yyyy" to "$MMMMM ${dd}th, $yyyy"
        )


        fun extractDateMillis(message: String): ExpiryDateInfo {

            val extractOperation = { pattern: Map.Entry<String, String>, setNo: Int ->
                val regex = Regex(pattern.value, RegexOption.IGNORE_CASE)
                val results = regex.findAll(message)
                var maxTimeMillis = -1L
                var expiry = "none"
                results.forEach {
                    val sdf = SimpleDateFormat(if (setNo == 1) "${pattern.key} yyyy" else pattern.key, Locale.ENGLISH)
                    val date = sdf.parse(
                        if (setNo == 1) "${it.value} $defaultYYYY" else it.value
                    )
                    if (date.time > maxTimeMillis) {
                        maxTimeMillis = date.time
                        expiry = it.value
                    }
                }
                ExpiryDateInfo(expiry, maxTimeMillis)
            }

            for (pattern in datePatterns0) {
                val expiryInfo = extractOperation(pattern, 0)
                if (expiryInfo.expiryTimeInMillis != -1L)
                    return expiryInfo
            }

            var maxTime = ExpiryDateInfo("none", -1L)


            for (pattern in datePatterns2) {
                val expiryInfo = extractOperation(pattern, 2)
                if (expiryInfo.expiryTimeInMillis > maxTime.expiryTimeInMillis)
                    maxTime = expiryInfo
            }

            if (maxTime.expiryTimeInMillis != -1L) return maxTime

            for (pattern in datePatterns1) {
                val expiryInfo = extractOperation(pattern, 1)
                if (expiryInfo.expiryTimeInMillis > maxTime.expiryTimeInMillis)
                    maxTime = expiryInfo
            }

            if (maxTime.expiryTimeInMillis != -1L) return maxTime

            val lastRegex = Regex("last day|expiring today|midnight|off today", RegexOption.IGNORE_CASE)
            val result = lastRegex.find(message)

            result?.let {
                maxTime = ExpiryDateInfo("findFromCurrTime", -2L)
            }

            if (maxTime.expiryTimeInMillis == -1L) {
                maxTime = ExpiryDateInfo("none", Long.MAX_VALUE)
            }
            return maxTime
        }


    }
}