package com.mx.dialog.list

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mx.dialog.R
import com.mx.dialog.utils.MXTextProp

open class MXListDialog(context: Context) :
    MXBaseListDialog(context) {
    private var isSingleSelectMod = true
    private var onSingleSelect: ((Int) -> Unit)? = null
    private var onMultipleSelect: ((List<Int>) -> Unit)? = null
    private var showSelectTag: Boolean = false
    private val selectIndexList = HashSet<Int>()
    private val listItemProp = MXTextProp()
    private val listAdapt by lazy {
        MXBaseListAdapt<String>(context, R.layout.mx_dialog_list_item) { itemView, position, item ->
            val infoTxv = itemView.findViewById<TextView>(R.id.mxInfoTxv)
            val selectTag = itemView.findViewById<ImageView>(R.id.mxSelectTag)
            listItemProp.attachTextHeight(infoTxv, R.dimen.mx_dialog_size_list_item_height)
            listItemProp.attachTextColor(infoTxv, R.color.mx_dialog_color_text)
            listItemProp.attachTextStyle(infoTxv, Typeface.NORMAL)
            listItemProp.attachTextSize(infoTxv, R.dimen.mx_dialog_text_size_content)
            listItemProp.attachTextGravity(infoTxv, Gravity.CENTER)
            if (showSelectTag) {
                if (selectIndexList.contains(position)) {
                    listItemProp.attachTextStyle(infoTxv, Typeface.BOLD)
                    selectTag?.setImageResource(R.drawable.mx_dialog_icon_select)
                } else {
                    selectTag?.setImageResource(R.drawable.mx_dialog_icon_unselect)
                }
                selectTag?.visibility = View.VISIBLE
            } else {
                selectTag?.visibility = View.GONE
            }

            infoTxv?.text = item
        }
    }


    override fun initDialog() {
        super.initDialog()
        setAdapt(listAdapt)
        if (isSingleSelectMod) {
            setItemClick { position ->
                dismiss()
                onSingleSelect?.invoke(position)
            }
        } else {
            setItemClick { position ->
                if (selectIndexList.contains(position)) {
                    selectIndexList.remove(position)
                } else {
                    selectIndexList.add(position)
                }
                listAdapt.notifyDataSetChanged()
            }
            setActionClick {
                dismiss()
                val list = selectIndexList.toList().sorted()
                onMultipleSelect?.invoke(list)
            }
        }
    }

    override fun showActionBtn(): Boolean {
        return !isSingleSelectMod
    }

    /**
     * ?????????????????????
     * @param list ????????????
     * @param showSelectTag ???????????? ??????/????????? ??????
     * @param selectIndex ????????????Index
     * @param itemHeightDP ??????Item???????????????
     * @param textColor ??????Item????????????
     * @param textSizeSP ??????Item????????????
     * @param textStyle ??????Item???????????? ???????????? Typeface.NORMAL  ?????????Typeface.ITALIC  ?????????Typeface.BOLD  ????????????Typeface.BOLD_ITALIC???
     * @param textGravity ??????Item??????Gravity??????
     * @param onSelect ??????Item??????????????????
     */
    fun setItems(
        list: List<String>,
        showSelectTag: Boolean? = null,
        selectIndex: Int? = null,
        itemHeightDP: Float? = null,
        textColor: Int? = null,
        textSizeSP: Float? = null,
        textStyle: Int? = null,
        textGravity: Int? = null,
        onSelect: ((index: Int) -> Unit)? = null
    ) {
        listAdapt.list.clear()
        listAdapt.list.addAll(list)
        this.onSingleSelect = onSelect
        this.showSelectTag = showSelectTag ?: (selectIndex != null) //
        this.selectIndexList.clear()
        selectIndex?.let { this.selectIndexList.add(it) }
        this.listItemProp.textHeightDP = itemHeightDP
        this.listItemProp.textColor = textColor
        this.listItemProp.textSizeSP = textSizeSP
        this.listItemProp.textStyle = textStyle
        this.listItemProp.textGravity = textGravity
        this.isSingleSelectMod = true

        initDialog()
    }

    /**
     * ?????????????????????
     * @param list ????????????
     * @param selectIndexList ????????????Index??????
     * @param itemHeightDP ??????Item???????????????
     * @param textColor ??????Item????????????
     * @param textSizeSP ??????Item????????????
     * @param textGravity ??????Item??????Gravity??????
     * @param onSelect ??????Item??????????????????
     */
    fun setMultipleItems(
        list: List<String>,
        selectIndexList: List<Int>? = null,
        itemHeightDP: Float? = null,
        textColor: Int? = null,
        textSizeSP: Float? = null,
        textGravity: Int? = null,
        onSelect: ((list: List<Int>) -> Unit)? = null
    ) {
        listAdapt.list.clear()
        listAdapt.list.addAll(list)
        this.onMultipleSelect = onSelect
        this.showSelectTag = true
        this.selectIndexList.clear()
        selectIndexList?.let { this.selectIndexList.addAll(it) }
        this.listItemProp.textHeightDP = itemHeightDP
        this.listItemProp.textColor = textColor
        this.listItemProp.textSizeSP = textSizeSP
        this.listItemProp.textGravity = textGravity
        this.isSingleSelectMod = false

        initDialog()
    }
}