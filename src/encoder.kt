interface Encoder {
    fun charIndex(c: Char, tableIndex: Int): Int?
    fun encodeCallsign(callsign: String): UInt?
    fun encodePosition(locator: String): Position?
    fun encodeGrid(callsign: String, locator: String): UInt?

    fun encodeJTmessage(message: String): Array<UByte>
}

data class Position(val latitude: Float, val longitude: Float)

fun createEncoder(): Encoder {
    return encoder()
}

private class encoder(nBase: UInt = 41414u, nTokens: UInt = 2063592u, max22: UInt = 4194304u, maxGrid: UInt = 32400u) : Encoder {
    private val nBase = nBase
    private val nTokens = nTokens
    private val max22 = max22
    private val maxGrid = maxGrid

    override fun charIndex(c: Char, tableIndex: Int): Int {
        val index = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ+-./?".find { it == c }?.let { " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ+-./?".indexOf(it) }

        if (tableIndex == 0) {
            return index ?: 0
        }

        if (index != null) {
            if (index > 36) {
                return 0
            }

            if (tableIndex == 1) {
                return index
            }

            if (tableIndex == 2 && index > 0) {
                return index - 1
            }

            if (tableIndex == 3 && index > 1) {
                if (c.isDigit()) {
                    return index - 1
                }
            }

            if (tableIndex == 4) {
                if (index == 0) {
                    return 0
                }

                if (index > 10) {
                    return index - 10
                }
            }


        }
        return 0
    }

    override fun encodeCallsign(callsign: String): UInt? {
        var localCallsign = callsign.trim().toUpperCase()

        if (callsign.isEmpty()) {
            return null
        }

        if (callsign.startsWith("3DA0")) {
            localCallsign = callsign.substring(4, 7)
        }

        if (callsign.startsWith("3X")) {
            localCallsign = "Q" + callsign.substring(2, 7)
        }

        if (callsign.startsWith("CQ ")) {
            if (callsign.length >= 6 && callsign[3].isDigit()) {
                localCallsign = callsign.substring(3, 6)
                val nFreq = callsign.substring(3, 6)

                return nBase + 3u + nFreq.toUInt()
            }
        }

        return null
    }

    override fun encodePosition(locator: String): Position {
        return Position(0.0f, 0.0f)
    }

    override fun encodeGrid(callsign: String, locator: String): UInt {
        return 0u
    }

    override fun encodeJTmessage(message: String): Array<UByte> {
        return arrayOf(0u)
    }
}