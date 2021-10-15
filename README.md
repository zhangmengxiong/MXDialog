# MXDialog

#### 介绍
通用Dialog
最新版本：[![](https://jitpack.io/v/com.gitee.zhangmengxiong/MXDialog.svg)](https://jitpack.io/#com.gitee.zhangmengxiong/MXDialog)


#### 功能特性
- TipDialog封装

##### 1、通过 dependence 引入MXVideo
```groovy
    dependencies {
         implementation 'com.gitee.zhangmengxiong:MXDialog:x.x.x'
    }
```

##### 2、集成
```kotlin
    MXTipDialog(this).apply {
    setCancelable(false) // 不可返回
    setBackGroundColor(Color.parseColor("#22000000")) // 背景图
    setTitle("提示") // 标题
    setMessage("这是一个提示！！！") // 内容 
    setDismissDelay(10) // 10秒后消失
    setPosition(MXDialogPosition.CENTER.also { // 内容位置
        it.translationY = -50 // 内容Y轴位移
        it.translationX = 100 // X轴位移
        it.marginTop = 20  // 顶部边距
        it.marginBottom = 10 // 底部边距
    })
    setCancelBtn(visible = true, text = "不要", color = Color.GRAY) {// 取消按钮样式
        // 取消按钮点击
    }
    setActionBtn {
        // 确认按钮点击
    }
}.show()
```

##### 3、Confirm弹窗
Confirm弹窗不可返回
```kotlin
MXTipDialog.confirm(this, "请确认") { confirm ->
    toast("确认结果：$confirm")
}
``` 
类似还有：
```kotlin
// 错误提示
MXTipDialog.error(this, "错误提示")
``` 
```kotlin
// 成功提示
MXTipDialog.success(this, "成功提示")
``` 

```kotlin
// Warn提示
MXTipDialog.warn(this, "Warn提示")
``` 
