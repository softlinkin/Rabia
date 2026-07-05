package rabia.urdu.keyboard.ui.keyboard

data class KeyRow(val keys: List<KeyDef>)

data class KeyDef(
    val label: String,
    val shiftedLabel: String = "",
    val widthWeight: Float = 1f,
    val isAction: Boolean = false,
    val actionType: KeyActionType = KeyActionType.CHARACTER,
    /** Alternate characters shown when long-pressing the key (Rabia-style). */
    val alternates: List<String> = emptyList()
) {
    /** What gets typed when this key is pressed (depends on shift state). */
    fun typedLabel(shifted: Boolean): String =
        if (shifted && shiftedLabel.isNotEmpty()) shiftedLabel else label
}

enum class KeyActionType {
    CHARACTER,
    SHIFT,
    DELETE,
    SPACE,
    ENTER,
    SYMBOLS,
    LANGUAGE,
    PUNCTUATION
}

enum class KeyboardMode { ENGLISH, URDU, SYMBOLS }

object KeyboardLayout {

    /** English QWERTY layout (matches the Rabia keyboard English mode). */
    val englishRows: List<KeyRow> = listOf(
        KeyRow(listOf(
            KeyDef("q", "Q"), KeyDef("w", "W"), KeyDef("e", "E"),
            KeyDef("r", "R"), KeyDef("t", "T"), KeyDef("y", "Y"),
            KeyDef("u", "U"), KeyDef("i", "I"), KeyDef("o", "O"), KeyDef("p", "P")
        )),
        KeyRow(listOf(
            KeyDef("a", "A"), KeyDef("s", "S"), KeyDef("d", "D"),
            KeyDef("f", "F"), KeyDef("g", "G"), KeyDef("h", "H"),
            KeyDef("j", "J"), KeyDef("k", "K"), KeyDef("l", "L")
        )),
        KeyRow(listOf(
            KeyDef("", widthWeight = 1.3f, isAction = true, actionType = KeyActionType.SHIFT),
            KeyDef("z", "Z"), KeyDef("x", "X"), KeyDef("c", "C"),
            KeyDef("v", "V"), KeyDef("b", "B"), KeyDef("n", "N"), KeyDef("m", "M"),
            KeyDef("", widthWeight = 1.3f, isAction = true, actionType = KeyActionType.DELETE)
        )),
        KeyRow(listOf(
            KeyDef("123", widthWeight = 1.2f, isAction = true, actionType = KeyActionType.SYMBOLS),
            KeyDef(",", ".", widthWeight = 1f),
            KeyDef("", widthWeight = 4f, isAction = true, actionType = KeyActionType.SPACE),
            KeyDef(".", "?", widthWeight = 1f),
            KeyDef("ابپ", widthWeight = 1.2f, isAction = true, actionType = KeyActionType.LANGUAGE),
            KeyDef("", widthWeight = 1.3f, isAction = true, actionType = KeyActionType.ENTER)
        ))
    )

    /**
     * Urdu layout matching the reference screenshot of the classic Rabia Urdu Keyboard.
     * Row distribution mirrors the image: 10, 9, 9, 6 keys with the globe, 123, spacebar,
     * punctuation, ABC and enter keys on the bottom row.
     */
    val urduRows: List<KeyRow> = listOf(
        KeyRow(listOf(
            KeyDef("چ", shiftedLabel = "+"),
            KeyDef("ص", shiftedLabel = "×"),
            KeyDef("ٹ", shiftedLabel = "÷"),
            KeyDef("ف", shiftedLabel = "؛"),
            KeyDef("غ", shiftedLabel = ":"),
            KeyDef("ع", shiftedLabel = "؛"),
            KeyDef("ه", shiftedLabel = "َ"),
            KeyDef("خ", shiftedLabel = "ُ"),
            KeyDef("ح", shiftedLabel = "ِ"),
            KeyDef("ج", shiftedLabel = "ّ")
        )),
        KeyRow(listOf(
            KeyDef("ش", shiftedLabel = "ژ"),
            KeyDef("س", shiftedLabel = "ث"),
            KeyDef("ی", shiftedLabel = "ي"),
            KeyDef("ب", shiftedLabel = "ـ"),
            KeyDef("ل", shiftedLabel = "ﻹ"),
            KeyDef("ا", shiftedLabel = "آ"),
            KeyDef("ت", shiftedLabel = "ٹ"),
            KeyDef("ن", shiftedLabel = "ں"),
            KeyDef("م", shiftedLabel = "ة")
        )),
        KeyRow(listOf(
            KeyDef("", widthWeight = 1f, isAction = true, actionType = KeyActionType.SHIFT),
            KeyDef("ک", shiftedLabel = "گ"),
            KeyDef("ظ", shiftedLabel = "ط"),
            KeyDef("ز", shiftedLabel = "ذ"),
            KeyDef("ر", shiftedLabel = "ڑ"),
            KeyDef("ذ", shiftedLabel = "ژ"),
            KeyDef("د", shiftedLabel = "ڈ"),
            KeyDef("پ", shiftedLabel = "ث"),
            KeyDef("", widthWeight = 1f, isAction = true, actionType = KeyActionType.DELETE)
        )),
        KeyRow(listOf(
            KeyDef("", widthWeight = 1f, isAction = true, actionType = KeyActionType.LANGUAGE),
            KeyDef("123", widthWeight = 1f, isAction = true, actionType = KeyActionType.SYMBOLS),
            KeyDef("", widthWeight = 4f, isAction = true, actionType = KeyActionType.SPACE),
            KeyDef(".,", widthWeight = 1f, actionType = KeyActionType.PUNCTUATION),
            KeyDef("ABC", widthWeight = 1.5f, isAction = true, actionType = KeyActionType.LANGUAGE),
            KeyDef("", widthWeight = 1.5f, isAction = true, actionType = KeyActionType.ENTER)
        ))
    )

    /** Numbers + symbols page. */
    val symbolRows: List<KeyRow> = listOf(
        KeyRow(listOf(
            KeyDef("1", "!"), KeyDef("2", "@"), KeyDef("3", "#"), KeyDef("4", "$"),
            KeyDef("5", "%"), KeyDef("6", "^"), KeyDef("7", "&"), KeyDef("8", "*"),
            KeyDef("9", "("), KeyDef("0", ")")
        )),
        KeyRow(listOf(
            KeyDef("-", "_"), KeyDef("/", "\\"), KeyDef(":", ";"), KeyDef("'", "\""),
            KeyDef("+", "="), KeyDef("[", "]"), KeyDef("{", "}"), KeyDef("|", "§")
        )),
        KeyRow(listOf(
            KeyDef("", widthWeight = 1.3f, isAction = true, actionType = KeyActionType.SHIFT),
            KeyDef("<", "«"), KeyDef(">", "»"), KeyDef("?", "؟"), KeyDef(",", "،"),
            KeyDef(".", "…"), KeyDef("~", "`"),
            KeyDef("", widthWeight = 1.3f, isAction = true, actionType = KeyActionType.DELETE)
        )),
        KeyRow(listOf(
            KeyDef("ABC", widthWeight = 1.2f, isAction = true, actionType = KeyActionType.LANGUAGE),
            KeyDef(",", widthWeight = 1f),
            KeyDef("", widthWeight = 4f, isAction = true, actionType = KeyActionType.SPACE),
            KeyDef(".", widthWeight = 1f),
            KeyDef("ابپ", widthWeight = 1.2f, isAction = true, actionType = KeyActionType.LANGUAGE),
            KeyDef("", widthWeight = 1.3f, isAction = true, actionType = KeyActionType.ENTER)
        ))
    )

    fun rowsFor(mode: KeyboardMode): List<KeyRow> = when (mode) {
        KeyboardMode.ENGLISH -> englishRows
        KeyboardMode.URDU -> urduRows
        KeyboardMode.SYMBOLS -> symbolRows
    }
}
