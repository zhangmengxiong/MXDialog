package com.mx.dialog.utils

internal data class MXButtonProps(
    val visible: Boolean = true,
    val text: CharSequence? = null,
    val textColor: Int? = null,
    val onclick: (() -> Unit)? = null
)