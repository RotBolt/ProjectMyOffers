package com.intellyticshub.projectmyoffers.utils

import java.text.SimpleDateFormat
import java.util.*

class OfferExtractor(private val message: String) {

    fun extractOfferCode(): String {

        val identifiers = "(code|CODE|Code|coupon|Coupon|COUPON)"

        val codeFilter = Regex("[A-Z0-9]+")
        val filterOperation = { string: String ->
            codeFilter.findAll(string).filter { thisMatch -> thisMatch.value != "CODE" && thisMatch.value != "C" }
                .elementAt(0).value
        }

        val codeRegex0 = Regex("$identifiers(:|-|\\s|[a-z])+[A-Z0-9]+")
        val result0 = codeRegex0.find(message)
        val code0 = result0?.let { filterOperation(it.value) }
        code0?.let { return it }

        val codeRegex1 = Regex("[A-Z0-9]+ $identifiers")
        val result1 = codeRegex1.find(message)
        val code1 = result1?.let { filterOperation(it.value) }
        code1?.let { return it }

        return "none"
    }

    fun extractOffer(): String {
        val offerRegex0 = Regex(
            "((max |)(cashback|discount)( is | of | )([0-9]+(%| %|rs| rs)|rs(.|. | )[0-9]+))|((flat |)([0-9]+((%| %|rs| rs)|rs(.|. | )[0-9]+)(off| off))(([A-Za-z]|\\s)+ ([0-9]+(rs| rs)|rs(.|. | )[0-9]+)|))|(([0-9]+(%| %|rs| rs)|rs(.|. | )[0-9]+)( max| max | |)(cashback|discount))",
            RegexOption.IGNORE_CASE
        )
        return offerRegex0.findAll(message).fold("") { acc, matchResult ->
            acc + matchResult.value + ". "
        }
    }

    fun extractExpiryDate(defaultYYYY: String) {
        val dateExtractor = DateExtractor(defaultYYYY)
        val timeMillis = dateExtractor.extractDateMillis(message)
        println("timeinmillis $timeMillis")
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        println(calendar.time)
    }


    class DateExtractor(private val defaultYYYY: String) {

        private val MM = "([1-9]|1[012])"
        private val MMM = "(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)"
        private val MMMMM = "(january|february|march|april|may|june|july|august|september|october|november|december)"

        private val dd = "([1-9]|0[1-9]|[12][0-9]|3[01])"

        private val yyyy = "2\\d\\d\\d"
        private val yy = "\\d\\d"


        private val datePatterns0 = mapOf(
            Pair("dd/MM/yyyy", "$dd/$MM/$yyyy"),
            Pair("dd/MM/yy", "$dd/$MM/$yy"),
            Pair("MM/dd/yyyy", "$MM/$dd/$yyyy"),
            Pair("MM/dd/yy", "$MM/$dd/$yy"),
            Pair("yyyy/MM/dd", "$yyyy/$MM/$dd"),
            Pair("yyyy/dd/MM", "$yyyy/$dd/$MM"),
            Pair("MM/yyyy", "$MM/$yyyy"),

            Pair("dd MM yyyy", "$dd $MM $yyyy"),
            Pair("dd MM yy", "$dd $MM $yy"),
            Pair("MM dd yyyy", "$MM $dd $yyyy"),
            Pair("MM dd yy", "$MM $dd $yy"),
            Pair("yyyy MM dd", "$yyyy $MM $dd"),
            Pair("yyyy dd MM", "$yyyy $dd $MM"),
            Pair("MM yyyy", "$MM $yyyy"),

            Pair("dd-MM-yyyy", "$dd-$MM-$yyyy"),
            Pair("dd-MM-yy", "$dd-$MM-$yy"),
            Pair("MM-dd-yyyy", "$MM-$dd-$yyyy"),
            Pair("MM-dd-yy", "$MM-$dd-$yy"),
            Pair("yyyy-MM-dd", "$yyyy-$MM-$dd"),
            Pair("yyyy-dd-MM", "$yyyy-$dd-$MM"),
            Pair("MM-yyyy", "$MM-$yyyy"),

            Pair("dd.MM.yyyy", "$dd.$MM.$yyyy"),
            Pair("dd.MM.yy", "$dd.$MM.$yy"),
            Pair("MM.dd.yyyy", "$MM.$dd.$yyyy"),
            Pair("MM.dd.yy", "$MM.$dd.$yy"),
            Pair("yyyy.MM.dd", "$yyyy.$MM.$dd"),
            Pair("yyyy.dd.MM", "$yyyy.$dd.$MM"),
            Pair("MM.yyyy", "$MM.$yyyy"),

            Pair("dd MMM yyyy", "$dd $MMM $yyyy"),
            Pair("dd MMM yy", "$dd $MMM $yy"),
            Pair("MMM dd yyyy", "$MMM $dd $yyyy"),
            Pair("MMM dd yy", "$MMM $dd $yy"),
            Pair("yyyy MMM dd", "$yyyy $MMM $dd"),
            Pair("yyyy dd MMM", "$yyyy $dd $MMM"),
            Pair("MMM yyyy", "$MMM $yyyy"),

            Pair("dd-MMM-yyyy", "$dd-$MMM-$yyyy"),
            Pair("dd-MMM-yy", "$dd-$MMM-$yy"),
            Pair("MMM-dd-yyyy", "$MMM-$dd-$yyyy"),
            Pair("MMM-dd-yy", "$MMM-$dd-$yy"),
            Pair("yyyy-MMM-dd", "$yyyy-$MMM-$dd"),
            Pair("yyyy-dd-MMM", "$yyyy-$dd-$MMM"),
            Pair("MMM-yyyy", "$MMM-$yyyy"),

            Pair("dd/MMM/yyyy", "$dd/$MMM/$yyyy"),
            Pair("dd/MMM/yy", "$dd/$MMM/$yy"),
            Pair("MMM/dd/yyyy", "$MMM/$dd/$yyyy"),
            Pair("MMM/dd/yy", "$MMM/$dd/$yy"),
            Pair("yyyy/MMM/dd", "$yyyy/$MMM/$dd"),
            Pair("yyyy/dd/MMM", "$yyyy/$dd/$MMM"),
            Pair("MMM/yyyy", "$MMM/$yyyy"),

            Pair("dd.MMM.yyyy", "$dd.$MMM.$yyyy"),
            Pair("dd.MMM.yy", "$dd.$MMM.$yy"),
            Pair("MMM.dd.yyyy", "$MMM.$dd.$yyyy"),
            Pair("MMM.dd.yy", "$MMM.$dd.$yy"),
            Pair("yyyy.MMM.dd", "$yyyy.$MMM.$dd"),
            Pair("yyyy.dd.MMM", "$yyyy.$dd.$MMM"),
            Pair("MMM.yyyy", "$MMM.$yyyy"),

            Pair("dd MMMMM yyyy", "$dd $MMMMM $yyyy"),
            Pair("dd MMMMM yy", "$dd $MMMMM $yy"),
            Pair("MMMMM dd yyyy", "$MMMMM $dd $yyyy"),
            Pair("MMMMM dd yy", "$MMMMM $dd $yy"),
            Pair("yyyy MMMMM dd", "$yyyy $MMMMM $dd"),
            Pair("yyyy dd MMMMM", "$yyyy $dd $MMMMM"),
            Pair("MMMMM yyyy", "$MMMMM $yyyy"),

            Pair("dd-MMMMM-yyyy", "$dd-$MMMMM-$yyyy"),
            Pair("dd-MMMMM-yy", "$dd-$MMMMM-$yy"),
            Pair("MMMMM-dd-yyyy", "$MMMMM-$dd-$yyyy"),
            Pair("MMMMM-dd-yy", "$MMMMM-$dd-$yy"),
            Pair("yyyy-MMMMM-dd", "$yyyy-$MMMMM-$dd"),
            Pair("yyyy-dd-MMMMM", "$yyyy-$dd-$MMMMM"),
            Pair("MMMMM-yyyy", "$MMMMM-$yyyy"),

            Pair("dd/MMMMM/yyyy", "$dd/$MMMMM/$yyyy"),
            Pair("dd/MMMMM/yy", "$dd/$MMMMM/$yy"),
            Pair("MMMMM/dd/yyyy", "$MMMMM/$dd/$yyyy"),
            Pair("MMMMM/dd/yy", "$MMMMM/$dd/$yy"),
            Pair("yyyy/MMMMM/dd", "$yyyy/$MMMMM/$dd"),
            Pair("yyyy/dd/MMMMM", "$yyyy/$dd/$MMMMM"),
            Pair("MMMMM/yyyy", "$MMMMM/$yyyy"),

            Pair("dd.MMMMM.yyyy", "$dd.$MMMMM.$yyyy"),
            Pair("dd.MMMMM.yy", "$dd.$MMMMM.$yy"),
            Pair("MMMMM.dd.yyyy", "$MMMMM.$dd.$yyyy"),
            Pair("MMMMM.dd.yy", "$MMMMM.$dd.$yy"),
            Pair("yyyy.MMMMM.dd", "$yyyy.$MMMMM.$dd"),
            Pair("yyyy.dd.MMMMM", "$yyyy.$dd.$MMMMM"),
            Pair("MMMMM.yyyy", "$MMMMM.$yyyy"),


            Pair("MMM, yyyy", "$MMM, $yyyy"),

            Pair("MMMMM, yyyy", "$MMMMM, $yyyy")
        )

        private val datePatterns1 = mapOf(
            Pair("dd/MM", "$dd/$MM"),
            Pair("MM/dd", "$MM/$dd"),

            Pair("dd MMM", "$dd $MMM"),

            Pair("dd'st' MMM", "${dd}st $MMM"),
            Pair("dd'nd' MMM", "${dd}nd $MMM"),
            Pair("dd'rd' MMM", "${dd}rd $MMM"),
            Pair("dd'th' MMM", "${dd}th $MMM"),

            Pair("dd'st,' MMM", "${dd}st, $MMM"),
            Pair("dd'nd,' MMM", "${dd}nd, $MMM"),
            Pair("dd'rd,' MMM", "${dd}rd, $MMM"),
            Pair("dd'th,' MMM", "${dd}th, $MMM"),

            Pair("dd MMMMM", "$dd $MMMMM"),

            Pair("dd'st' MMMMM", "${dd}st $MMMMM"),
            Pair("dd'nd' MMMMM", "${dd}nd $MMMMM"),
            Pair("dd'rd' MMMMM", "${dd}rd $MMMMM"),
            Pair("dd'th' MMMMM", "${dd}th $MMMMM"),

            Pair("dd'st,' MMMMM", "${dd}st, $MMMMM"),
            Pair("dd'nd,' MMMMM", "${dd}nd, $MMMMM"),
            Pair("dd'rd,' MMMMM", "${dd}rd, $MMMMM"),
            Pair("dd'th,' MMMMM", "${dd}th, $MMMMM"),


            Pair("MMM dd", "$MMM $dd"),

            Pair("MMM dd'st'", "$MMM ${dd}st"),
            Pair("MMM dd'nd'", "$MMM ${dd}nd"),
            Pair("MMM dd'rd'", "$MMM ${dd}rd"),
            Pair("MMM dd'th'", "$MMM ${dd}th"),

            Pair("MMM',' dd'st'", "$MMM, ${dd}st"),
            Pair("MMM',' dd'nd'", "$MMM, ${dd}nd"),
            Pair("MMM',' dd'rd'", "$MMM, ${dd}rd"),
            Pair("MMM',' dd'th'", "$MMM, ${dd}th"),

            Pair("MMMMM dd", "$MMMMM $dd"),

            Pair("MMMMM dd'st'", "$MMMMM ${dd}st"),
            Pair("MMMMM dd'nd'", "$MMMMM ${dd}nd"),
            Pair("MMMMM dd'rd'", "$MMMMM ${dd}rd"),
            Pair("MMMMM dd'th'", "$MMMMM ${dd}th"),

            Pair("MMMMM',' dd'st'", "$MMMMM, ${dd}st"),
            Pair("MMMMM',' dd'nd'", "$MMMMM, ${dd}nd"),
            Pair("MMMMM',' dd'rd'", "$MMMMM, ${dd}rd"),
            Pair("MMMMM',' dd'th'", "$MMMMM, ${dd}th")
        )

        private val datePatterns2 = mapOf(
            Pair("dd'st' MMM yyyy", "${dd}st $MMM $yyyy"),
            Pair("dd'nd' MMM yyyy", "${dd}nd $MMM $yyyy"),
            Pair("dd'rd' MMM yyyy", "${dd}rd $MMM $yyyy"),
            Pair("dd'th' MMM yyyy", "${dd}th $MMM $yyyy"),

            Pair("dd MMM',' yyyy", "$dd $MMM, $yyyy"),
            Pair("dd'st' MMM',' yyyy", "${dd}st $MMM, $yyyy"),
            Pair("dd'nd' MMM',' yyyy", "${dd}nd $MMM, $yyyy"),
            Pair("dd'rd' MMM',' yyyy", "${dd}rd $MMM, $yyyy"),
            Pair("dd'th' MMM',' yyyy", "${dd}th $MMM, $yyyy"),

            Pair("MMM dd'st' yyyy", "$MMM ${dd}st $yyyy"),
            Pair("MMM dd'nd' yyyy", "$MMM ${dd}nd $yyyy"),
            Pair("MMM dd'rd' yyyy", "$MMM ${dd}rd $yyyy"),
            Pair("MMM dd'th' yyyy", "$MMM ${dd}th $yyyy"),

            Pair("MMM dd'st,' yyyy", "$MMM ${dd}st, $yyyy"),
            Pair("MMM dd'nd,' yyyy", "$MMM ${dd}nd, $yyyy"),
            Pair("MMM dd'rd,' yyyy", "$MMM ${dd}rd, $yyyy"),
            Pair("MMM dd'th,' yyyy", "$MMM ${dd}th, $yyyy"),


            Pair("dd'st' MMMMM yyyy", "${dd}st $MMMMM $yyyy"),
            Pair("dd'nd' MMMMM yyyy", "${dd}nd $MMMMM $yyyy"),
            Pair("dd'rd' MMMMM yyyy", "${dd}rd $MMMMM $yyyy"),
            Pair("dd'th' MMMMM yyyy", "${dd}th $MMMMM $yyyy"),

            Pair("dd MMMMM',' yyyy", "$dd $MMMMM, $yyyy"),
            Pair("dd'st' MMMMM',' yyyy", "${dd}st $MMMMM, $yyyy"),
            Pair("dd'nd' MMMMM',' yyyy", "${dd}nd $MMMMM, $yyyy"),
            Pair("dd'rd' MMMMM',' yyyy", "${dd}rd $MMMMM, $yyyy"),
            Pair("dd'th' MMMMM',' yyyy", "${dd}th $MMMMM, $yyyy"),

            Pair("MMMMM dd'st' yyyy", "$MMMMM ${dd}st $yyyy"),
            Pair("MMMMM dd'nd' yyyy", "$MMMMM ${dd}nd $yyyy"),
            Pair("MMMMM dd'rd' yyyy", "$MMMMM ${dd}rd $yyyy"),
            Pair("MMMMM dd'th' yyyy", "$MMMMM ${dd}th $yyyy"),

            Pair("MMMMM dd'st,' yyyy", "$MMMMM ${dd}st, $yyyy"),
            Pair("MMMMM dd'nd,' yyyy", "$MMMMM ${dd}nd, $yyyy"),
            Pair("MMMMM dd'rd,' yyyy", "$MMMMM ${dd}rd, $yyyy"),
            Pair("MMMMM dd'th,' yyyy", "$MMMMM ${dd}th, $yyyy")
        )


        fun extractDateMillis(message: String): Long {


            val extractOperation = { pattern: Map.Entry<String, String>, setNo: Int ->
                val regex = Regex(pattern.value, RegexOption.IGNORE_CASE)
                val results = regex.findAll(message)
                var maxTimeMillis = -1L
                results.forEach {
                    val sdf = SimpleDateFormat(
                        if (setNo == 1) "${pattern.key} yyyy" else pattern.key,
                        Locale("hin")
                    )

                    val date = sdf.parse(
                        if (setNo == 1) "${it.value} $defaultYYYY" else it.value
                    )
                    if (date.time > maxTimeMillis)
                        maxTimeMillis = date.time
                }
                maxTimeMillis
            }

            for (pattern in datePatterns0) {
                val expiryTimeMillis = extractOperation(pattern, 0)
                if (expiryTimeMillis != -1L)
                    return expiryTimeMillis
            }

            var maxTimeMillis = -1L


            for (pattern in datePatterns2) {
                val expiryTimeMillis = extractOperation(pattern, 2)
                if (expiryTimeMillis > maxTimeMillis)
                    maxTimeMillis = expiryTimeMillis
            }

            if (maxTimeMillis != -1L) return maxTimeMillis

            for (pattern in datePatterns1) {
                val expiryTimeMillis = extractOperation(pattern, 1)
                if (expiryTimeMillis > maxTimeMillis)
                    maxTimeMillis = expiryTimeMillis
            }

            return maxTimeMillis

        }


    }
}