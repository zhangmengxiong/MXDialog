package com.mx.dialog.tip

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mx.dialog.R
import com.mx.dialog.base.MXBaseCardDialog
import com.mx.dialog.utils.MXButtonStyle
import com.mx.dialog.utils.MXTextProp
import com.mx.dialog.utils.MXUtils
import com.mx.dialog.views.MXRatioFrameLayout

abstract class MXTipBaseDialog(context: Context) : MXBaseCardDialog(context) {
    private var btnLay: LinearLayout? = null
    private var tipTypeImg: ImageView? = null
    private var contentLay: MXRatioFrameLayout? = null
    private var titleTxv: TextView? = null
    private var delayTxv: TextView? = null
    private var cancelBtn: TextView? = null
    private var btnDivider: View? = null

    private var titleStr: CharSequence? = null
    private var titleGravity: Int = Gravity.LEFT

    private var cancelProp: MXTextProp? = null
    private val actionProps = ArrayList<MXTextProp>()

    private var buttonStyle = MXButtonStyle.ActionFocus
    private var tipType = MXDialogType.NONE

    private var maxContentRatio: Float = 0f
    private var minContentHeightDP: Float = 0f

    override fun getContentLayoutId(): Int {
        return R.layout.mx_content_tip
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentLay = findViewById(R.id.mxContentLay)
        // 从重构方法创建Content内容
        generalContentView(contentLay!!)?.let { view ->
            contentLay?.removeAllViews()
            contentLay?.addView(
                view,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        initView()
    }

    abstract fun generalContentView(parent: FrameLayout): View?

    protected open fun initView() {
        btnLay = findViewById(R.id.mxBtnLay)
        tipTypeImg = findViewById(R.id.mxTipTypeImg)
        titleTxv = findViewById(R.id.mxTitleTxv)
        delayTxv = findViewById(R.id.mxDelayTxv)
        cancelBtn = findViewById(R.id.mxCancelBtn)
        btnDivider = findViewById(R.id.mxBtnDivider)
    }

    override fun onDismissTicket(maxSecond: Int, remindSecond: Int) {
        delayTxv?.text = "$remindSecond s"
        delayTxv?.visibility = View.VISIBLE
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        initDialog()
    }

    override fun initDialog() {
        super.initDialog()

        if (titleTxv == null) return
        titleTxv?.text = titleStr ?: context.getString(R.string.mx_dialog_tip_title)
        titleTxv?.gravity = titleGravity

        contentLay?.setMaxHeightRatio(maxContentRatio)
        contentLay?.minimumHeight = MXUtils.dp2px(context, minContentHeightDP)

        kotlin.run { // 处理按钮
            processCancelBtn()

            val btnLay = btnLay
            if (btnLay != null) {
                (0..btnLay.childCount).mapNotNull {
                    btnLay.getChildAt(it)
                }.forEachIndexed { index, view ->
                    if (index > 0) btnLay.removeView(view)
                }
                actionProps.forEachIndexed { index, actionProp ->
                    processActionBtn(actionProp)
                }
                if (cancelBtn?.visibility == View.VISIBLE || actionProps.isNotEmpty()) {
                    btnLay.visibility = View.VISIBLE
                } else {
                    btnLay.visibility = View.GONE
                }
            }
        }

        when (tipType) {
            MXDialogType.NONE -> {
                tipTypeImg?.visibility = View.GONE
            }

            MXDialogType.SUCCESS -> {
                tipTypeImg?.visibility = View.VISIBLE
                tipTypeImg?.setImageResource(R.drawable.mx_dialog_icon_success)
            }

            MXDialogType.WARN -> {
                tipTypeImg?.visibility = View.VISIBLE
                tipTypeImg?.setImageResource(R.drawable.mx_dialog_icon_warn)
            }

            MXDialogType.ERROR -> {
                tipTypeImg?.visibility = View.VISIBLE
                tipTypeImg?.setImageResource(R.drawable.mx_dialog_icon_error)
            }
        }
    }

    private fun processCancelBtn() {
        val button = cancelBtn ?: return
        val prop = cancelProp
        val showCancelBtn = (prop == null || prop.visible) && isCancelable()

        if (showCancelBtn) {
            val cancelProp = prop ?: MXTextProp(
                context.resources.getString(R.string.mx_dialog_button_cancel_text),
                true,
                context.resources.getColor(R.color.mx_dialog_color_text_cancel),
                15f
            )

            button.text = cancelProp.text
            cancelProp.attachTextColor(button, R.color.mx_dialog_color_text_cancel)
            cancelProp.attachTextSize(button, R.dimen.mx_dialog_text_size_button)
            button.setOnClickListener {
                dismiss()
                cancelProp.onclick?.invoke()
            }
            button.visibility = View.VISIBLE
        } else {
            button.visibility = View.GONE
        }
    }

    private fun processActionBtn(prop: MXTextProp?): TextView? {
        val btnLay = btnLay ?: return null

        val showActionBtn = (prop == null || prop.visible)
        if (!showActionBtn) return null

        LayoutInflater.from(context).inflate(
            R.layout.mx_content_action_btn, btnLay, true
        )
        val button = (btnLay.getChildAt(btnLay.childCount - 1) as TextView?) ?: return null
        val actionProp = prop ?: MXTextProp(
            context.resources.getString(R.string.mx_dialog_button_action_text),
            true,
            context.resources.getColor(R.color.mx_dialog_color_text_action),
            15f
        )
        button.text = actionProp.text
        actionProp.attachTextColor(button, R.color.mx_dialog_color_text_action)
        actionProp.attachTextSize(button, R.dimen.mx_dialog_text_size_button)
        button.setOnClickListener {
            dismiss()
            actionProp.onclick?.invoke()
        }
        button.visibility = View.VISIBLE

        val cornerDP = getCardBackgroundRadiusDP()
        MXButtonStyle.attach(
            buttonStyle, btnLay, cancelBtn,
            button, btnDivider, cornerDP
        )
        return button
    }

    /**
     * 设置活动按钮
     */
    fun addActionBtn(
        text: CharSequence? = null,
        visible: Boolean = true,
        textColor: Int? = null,
        textSizeSP: Float? = null,
        onclick: (() -> Unit)? = null
    ) {
        actionProps.add(
            MXTextProp(
                text ?: context.resources.getString(R.string.mx_dialog_button_action_text),
                visible, textColor, textSizeSP, onclick = onclick
            )
        )

        initDialog()
    }

    /**
     * 清空活动按钮
     */
    fun cleanActionBtn() {
        actionProps.clear()

        initDialog()
    }

    /**
     * 设置取消按钮
     * @param visible 按钮是否可见
     * @param text 按钮文字内容
     * @param textColor 文字颜色
     * @param onclick 取消按钮响应方法，
     * @see #setOnCancelListener(DialogInterface.OnCancelListener)
     */
    fun setCancelBtn(
        text: CharSequence? = null,
        visible: Boolean = true,
        textColor: Int? = null,
        textSizeSP: Float? = null,
        onclick: (() -> Unit)? = null
    ) {
        cancelProp = MXTextProp(
            text ?: context.resources.getString(R.string.mx_dialog_button_cancel_text),
            visible, textColor, textSizeSP, onclick = onclick
        )
        initDialog()
    }

    override fun setTitle(title: CharSequence?) {
        titleStr = title
        titleGravity = Gravity.LEFT
        initDialog()
    }

    override fun setTitle(titleId: Int) {
        titleStr = context.getString(titleId)
        titleGravity = Gravity.LEFT
        initDialog()
    }

    fun setTitle(title: CharSequence?, gravity: Int = Gravity.LEFT) {
        titleStr = title
        titleGravity = gravity
        initDialog()
    }

    /**
     * 设置内容最大宽高比
     */
    fun setMaxContentRatio(ratio: Float) {
        maxContentRatio = ratio

        initDialog()
    }

    /**
     * 设置内容最大宽高比
     */
    fun setMinContentHeightDP(heightDP: Float) {
        minContentHeightDP = heightDP

        initDialog()
    }

    fun setTipType(type: MXDialogType?) {
        this.tipType = type ?: MXDialogType.NONE

        initDialog()
    }

    fun setButtonStyle(style: MXButtonStyle) {
        this.buttonStyle = style

        initDialog()
    }
}