package pkg.to.app.date


interface DateFrame {
    val year: Int
    val month: Int
    val days: Int
    val hour: Int
    val second: Int
    val minute: Int
}

data class JalaliDateFrame(
    override val year: Int,
    override val month: Int,
    override val days: Int,
    override val hour: Int = 0,
    override val second: Int = 0,
    override val minute: Int = 0
) : DateFrame


data class GregorianDateFrame(
    override val year: Int,
    override val month: Int,
    override val days: Int,
    override val hour: Int = 0,
    override val second: Int = 0,
    override val minute: Int = 0
) : DateFrame
