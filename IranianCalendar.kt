package pkg.to.app.date

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

/**
 *
 * A KMP-compatible implementation of Gregorian and Jalali calendars using [LocalDateTime]
 * @see "https://en.wikipedia.org/wiki/Gregorian_calendar"
 * @see "https://en.wikipedia.org/wiki/Jalali_calendar"
 *
 */
class IranianCalendar {

    /**
     *
     * Iranian Year based on Jalali calendar
     *
     */
    var iranianYear = 0
        private set

    /**
     *
     * Iranian Month based on Jalali calendar
     *
     */
    var iranianMonth = 0
        private set

    /**
     *
     * Iranian Day based on Jalali calendar
     *
     */
    var iranianDay = 0
        private set

    /**
     *
     * Gregorian Year based on Gregorian calendar
     *
     */
    var gregorianYear = 0
        private set

    /**
     *
     * Gregorian Month based on Gregorian calendar
     *
     */
    var gregorianMonth = 0
        private set

    /**
     *
     * Gregorian Day based on Gregorian calendar
     *
     */
    var gregorianDay = 0
        private set

    /**
     *
     *  Used For converting between different calendars
     *
     */
    private val calendar = Calendar()

    /**
     *
     * Holds Local Time And Original Gregorian Date
     *
     */
    private var dateTime: LocalDateTime? = null

    /**
     *
     * List of Iranian Month's Name
     *
     */
    private val iranianMonthList = arrayOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )

    /**
     *
     * List of Iranian Week's Name
     *
     */
    private val weekDays = arrayOf(
        "دوشنبه",
        "سه شنبه",
        "چهارشنبه",
        "پنج شنبه",
        "جمعه",
        "شنبه",
        "یکشنبه"
    )

    /**
     *
     * Create instance by gregorian date and local time
     * @param localDateTime [LocalDateTime]
     *
     */
    constructor(localDateTime: LocalDateTime) {
        setGregorianDateByDateTime(localDateTime)
        dateTime = localDateTime
    }

    /**
     *
     * Create instance by separated date's parts (year,month,...)
     * @param date [JalaliDateFrame]
     *
     */
    constructor(date: JalaliDateFrame) {
        setIranianDate(date.year, date.month, date.days)
        dateTime = LocalDateTime(
            gregorianYear,
            gregorianMonth,
            gregorianDay,
            date.hour,
            date.minute,
            date.second
        )
    }


    /**
     *
     * Create instance by separated date's parts (year,month,...)
     * @param date [GregorianDateFrame]
     *
     */
    constructor(date: GregorianDateFrame) {
        setGregorianDate(date.year, date.month, date.days)
        dateTime = LocalDateTime(
            gregorianYear,
            gregorianMonth,
            gregorianDay,
            date.hour,
            date.minute,
            date.second
        )
    }


    /**
     *
     * Create instance by standard timestamp format(2025-01-01T12:04:38Z,..)
     * @param timeStamp [String]
     *
     * @see "https://en.wikipedia.org/wiki/ISO_8601"
     *
     */
    constructor(timeStamp: String) {
        setGregorianDateByDateTime(LocalDateTime.parse(timeStamp))
    }


    /**
     *
     * Create instance by epoch and given time zone (default is currentSystemDefault)
     * @param epochInMillis [Long]
     *
     */
    constructor(epochInMillis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()) {
        setGregorianDateByDateTime(
            Instant.fromEpochMilliseconds(epochInMillis).toLocalDateTime(timeZone)
        )
    }


    /**
     *
     * Local Time
     *
     */
    val localTime: String
        get() = "${dateTime?.toTimeString()}"




    /**
     *
     * Month length by month index
     *
     */
    val monthLength: Int = when {
        iranianMonth < 7 -> 31
        iranianMonth < 12 -> 30
        iranianMonth == 12 -> if (calendar.isLeap()) 30 else 29
        else -> -1
    }

    /**
     *
     * Current Iranian Month Name
     *
     */
    val iranianMonthName: String
        get() = iranianMonthList[abs(iranianMonth - 1)]


    /**
     *
     * Current Week Day Name
     *
     */
    val weekDayName: String
        get() = weekDays[dayOfWeek]


    /**
     *
     * Short Full Persian Date (1403/10/1)
     *
     */
    val iranianShortDate: String
        get() = "$iranianYear/$iranianMonth/$iranianDay"




    /**
     *
     * User-Friendly Persian Date
     *
     */
    val iranianFullFriendlyDate: String
        get() = " $iranianDay $iranianMonthName $iranianYear"


    /**
     *
     * User-Friendly Persian Date + Week Name
     *
     */
    val iranianFullFriendlyWithWeekDate: String
        get() = "$weekDayName $iranianDay $iranianMonthName $iranianYear"


    /**
     *
     * User-Friendly Persian Date with time
     *
     */
    val iranianFullFriendlyDateTime: String
        get() = " $iranianDay $iranianMonthName $iranianYear  $localTime"


    /**
     *
     * Short Gregorian Full Date (2021/10/1)
     *
     */
    val gregorianShortDate: String
        get() = "$gregorianYear/$gregorianMonth/$gregorianDay"


    /**
     *
     * DayOfWeek
     *
     */
    val dayOfWeek: Int
        get() = calendar.jdn % 7


    /**
     *
     * SetIranianDate
     * Sets the date according to the Iranian calendar and adjusts the other dates.
     *
     * @param year  int
     * @param month int
     * @param day   int
     */
    private fun setIranianDate(year: Int, month: Int, day: Int): IranianCalendar {
        iranianYear = year
        iranianMonth = month
        iranianDay = day
        setJDN(calendar.iranianDateToJDN())
        calculateBaseOnJDN()
        return this
    }

    /**
     *
     * setGregorianDate
     * Sets the date according to the Gregorian calendar and adjusts the other dates.
     *
     * @param year  int
     * @param month int
     * @param day   int
     */
    private fun setGregorianDate(year: Int, month: Int, day: Int) {
        gregorianYear = year
        gregorianMonth = month
        gregorianDay = day
        setJDN(calendar.gregorianDateToJDN(year, month, day))
        calculateBaseOnJDN()
    }

    /**
     *
     * Sets the date according to the Gregorian calendar and adjusts the other dates.
     *
     * @param date  [LocalDateTime]
     */
    private fun setGregorianDateByDateTime(date: LocalDateTime) {
        gregorianYear = date.year
        gregorianMonth = date.monthNumber
        gregorianDay = date.dayOfMonth
        setJDN(calendar.gregorianDateToJDN(date.year, date.monthNumber, date.dayOfMonth))
        calculateBaseOnJDN()
    }

    private fun calculateBaseOnJDN() {
        calendar.jdnToIranian()
        calendar.jdnToGregorian()
    }

    private fun setJDN(jdn: Int) {
        calendar.jdn = jdn
    }


    /**
     *
     * An Extension on [LocalDateTime] that returns user-friendly time  00:00
     *
     */
    fun LocalDateTime.toTimeString(): String {
        fun doubleDecimal(decimal: Int) = if (decimal < 10) "0$decimal" else "$decimal"
        return "${doubleDecimal(time.hour)} : ${doubleDecimal(time.minute)}"
    }


    /**
     *
     *  Go to icNext julian day number (JDN) and adjusts the other dates.
     *
     */
    fun nextDay() {
        calendar.jdn++
        calendar.jdnToIranian()
        calendar.jdnToGregorian()
    }

    /**
     *
     *
     * Overload the nextDay() method to accept the number of days to go ahead and
     * adjusts the other dates accordingly.
     *
     * @param days int
     */
    fun nextDay(days: Int) {
        calendar.jdn += days
        calendar.jdnToIranian()
        calendar.jdnToGregorian()
    }

    /**
     *
     *
     * Go to previous julian day number (JDN) and adjusts the other dates.
     *
     */
    fun previousDay() {
        calendar.jdn--
        calendar.jdnToIranian()
        calendar.jdnToGregorian()
    }

    /**
     *
     *
     * Overload the previousDay() method to accept the number of days to go backward
     * and adjusts the other dates accordingly.
     *
     * @param days int
     *
     *
     */
    fun previousDay(days: Int) {
        calendar.jdn -= days
        calendar.jdnToIranian()
        calendar.jdnToGregorian()
    }


    override fun toString(): String {
        return "Gregorian $gregorianShortDate \n Iranian $iranianShortDate"
    }


    private inner class Calendar {

        /**
         *
         * Calendar Parameters
         *
         */
        var leap = 0
        var jdn = 0
        var march = 0


        /**
         * This method determines if the Iranian (Jalali) year is leap (366-day long)
         * or is the common year (365 days), and finds the day in March (Gregorian
         * Calendar)of the first day of the Iranian year ('irYear').Iranian year (irYear)
         * ranges from (-61 to 3177).This method will set the following private data
         * members as follows:
         * leap: Number of years since the last leap year (0 to 4)
         * Gy: Gregorian year of the beginning of Iranian year
         * march: The March day of Farvardin the 1st (first day of jaYear)
         */
        private fun iranianCalendar() {
            val breaks = intArrayOf(
                -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
                1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
            )
            var jump: Int
            gregorianYear = iranianYear + 621
            var leapJ = -14
            var jp = breaks[0]
            var j = 1
            do {
                val jm = breaks[j]
                jump = jm - jp
                if (iranianYear >= jm) {
                    leapJ += jump / 33 * 8 + jump % 33 / 4
                    jp = jm
                }
                j++
            } while (j < 20 && iranianYear >= jm)
            var n = iranianYear - jp
            leapJ += n / 33 * 8 + (n % 33 + 3) / 4
            if (jump % 33 == 4 && jump - n == 4) leapJ++
            val leapG = gregorianYear / 4 - (gregorianYear / 100 + 1) * 3 / 4 - 150
            march = 20 + leapJ - leapG
            if (jump - n < 6) n = n - jump + (jump + 4) / 33 * 33
            leap = ((n + 1) % 33 - 1) % 4
            if (leap == -1) leap = 4
        }

        /**
         * This method determines if the Iranian (Jalali) year is leap (366-day long)
         * or is the common year (365 days), and finds the day in March (Gregorian
         * Calendar)of the first day of the Iranian year ('irYear').Iranian year (irYear)
         * ranges from (-61 to 3177).This method will set the following private data
         * members as follows:
         * leap: Number of years since the last leap year (0 to 4)
         * Gy: Gregorian year of the beginning of Iranian year
         * march: The March day of Farvardin the 1st (first day of jaYear)
         */
        fun isLeap(irYear: Int = iranianYear): Boolean {
            val breaks = intArrayOf(
                -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
                1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
            )
            var jump: Int
            gregorianYear = irYear + 621
            var leapJ = -14
            var jp = breaks[0]
            var j = 1
            do {
                val jm = breaks[j]
                jump = jm - jp
                if (irYear >= jm) {
                    leapJ += jump / 33 * 8 + jump % 33 / 4
                    jp = jm
                }
                j++
            } while (j < 20 && irYear >= jm)
            var n = irYear - jp
            leapJ += n / 33 * 8 + (n % 33 + 3) / 4
            if (jump % 33 == 4 && jump - n == 4) leapJ++
            val leapG = gregorianYear / 4 - (gregorianYear / 100 + 1) * 3 / 4 - 150
            march = 20 + leapJ - leapG
            if (jump - n < 6) n = n - jump + (jump + 4) / 33 * 33
            leap = ((n + 1) % 33 - 1) % 4
            if (leap == -1) leap = 4
            return leap == 4 || leap == 0
        }

        /**
         * Converts a date of the Iranian calendar to the Julian Day Number. It first
         * invokes the 'IranianCalender' private method to convert the Iranian date to
         * Gregorian date and then returns the Julian Day Number based on the Gregorian
         * date. The Iranian date is obtained from 'irYear'(1-3100),'irMonth'(1-12) and
         * 'irDay'(1-29/31).
         *
         * @return long (Julian Day Number)
         */
        fun iranianDateToJDN(): Int {
            iranianCalendar()
            return gregorianDateToJDN(
                gregorianYear,
                3,
                march
            ) + (iranianMonth - 1) * 31 - iranianMonth / 7 * (iranianMonth - 7) + iranianDay - 1
        }

        /**
         * Converts the current value of 'JDN' Julian Day Number to a date in the
         * Iranian calendar. The caller should make sure that the current value of
         * 'JDN' is set correctly. This method first converts the JDN to Gregorian
         * calendar and then to Iranian calendar.
         */
        fun jdnToIranian() {
            jdnToGregorian()
            iranianYear = gregorianYear - 621
            iranianCalendar()
            val jdn1F = gregorianDateToJDN(gregorianYear, 3, march)
            var k = jdn - jdn1F
            if (k >= 0) {
                if (k <= 185) {
                    iranianMonth = 1 + k / 31
                    iranianDay = k % 31 + 1
                    return
                } else k -= 186
            } else {
                iranianYear--
                k += 179
                if (leap == 1) k++
            }
            iranianMonth = 7 + k / 30
            iranianDay = k % 30 + 1
        }


        /**
         * Calculates the julian day number (JDN) from Gregorian calendar dates. This
         * integer number corresponds to the noon of the date (i.e. 12 hours of
         * Universal Time). This method was tested to be good (valid) since 1 March,
         * -100100 (of both calendars) up to a few millions (10^6) years into the
         * future. The algorithm is based on D.A.Hatcher, Q.Jl.R.Astron.Soc. 25(1984),
         * 53-55 slightly modified by K.M. Borkowski, Post.Astron. 25(1987), 275-279.
         *
         * @param year  int
         * @param month int
         * @param day   int
         * @return int
         */
        fun gregorianDateToJDN(year: Int, month: Int, day: Int): Int {
            var jdn =
                (year + (month - 8) / 6 + 100100) * 1461 / 4 + (153 * ((month + 9) % 12) + 2) / 5 + day - 34840408
            jdn = jdn - (year + 100100 + (month - 8) / 6) / 100 * 3 / 4 + 752
            return jdn
        }

        /**
         * Calculates Gregorian calendar dates from the julian day number (JDN) for
         * the period since JDN=-34839655 (i.e. the year -100100 of both calendars) to
         * some millions (10^6) years ahead of the present. The algorithm is based on
         * D.A. Hatcher, Q.Jl.R.Astron.Soc. 25(1984), 53-55 slightly modified by K.M.
         * Borkowski, Post.Astron. 25(1987), 275-279).
         */
        fun jdnToGregorian() {
            var j = 4 * jdn + 139361631
            j += ((4 * jdn + 183187720) / 146097 * 3 / 4 * 4 - 3908)
            val i = j % 1461 / 4 * 5 + 308
            gregorianDay = i % 153 / 5 + 1
            gregorianMonth = i / 153 % 12 + 1
            gregorianYear = j / 1461 - 100100 + (8 - gregorianMonth) / 6
        }
    }
}
