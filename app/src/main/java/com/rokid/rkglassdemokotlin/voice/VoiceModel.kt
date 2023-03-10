package com.rokid.rkglassdemokotlin.voice

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.rokid.axr.phone.glassvoice.RKGlassVoice
import com.rokid.axr.phone.glassvoice.RKLanguage
import com.rokid.axr.phone.glassvoice.RKOfflineCommandManager
import com.rokid.axr.phone.glassvoice.RKOfflineWords
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.base.BaseEvent
import com.rokid.rkglassdemokotlin.databinding.DialogAddOfflineBinding
import com.rokid.rkglassdemokotlin.databinding.RcItemBottomButtonBinding
import com.rokid.rkglassdemokotlin.databinding.RcItemVoiceAddedBinding


class VoiceModel(
    val topHeight: MutableLiveData<Int> = MutableLiveData(),
    val isCH: MutableLiveData<Boolean> = MutableLiveData(),
    val isEN: MutableLiveData<Boolean> = MutableLiveData(),
    val action: (BaseEvent) -> Unit
) {
    private lateinit var offlineManager: RKOfflineCommandManager
    private lateinit var adapter: AddedAdapter

    init {
        isCH.observeForever {
            if (it == true) {
                initZHOffline()
            }
        }
        isEN.observeForever {
            if (it == true) {
                initENOffline()
            }
        }
    }

    fun onBackPressed(v: View){
        action(object : BaseEvent(){
            override fun doEvent(context: Context) {
                if (context !is AppCompatActivity) return
                context.onBackPressed()
            }
        })
    }

    fun getRcAdapter(context: AppCompatActivity): AddedAdapter {
        this.adapter = AddedAdapter(context) {

            val binding = DialogAddOfflineBinding.inflate(LayoutInflater.from(context), null, false)
            binding.lifecycleOwner = context
            val data = OfflineCMD()
            binding.data = data
            val dialogBuilder = AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(binding.root)
                .setPositiveButton(R.string.add) { dialogInterface, _ ->
                    if (!TextUtils.isEmpty(data.inputCMD.value)) {
                        if (RKLanguage.R2_WORDS_LANGUAGE_ZH_CN == adapter.languageType) {
                            if (!TextUtils.isEmpty(data.inputPinyin.value)) {
                                addOffline(data.inputCMD.value!!, data.inputPinyin.value!!)
                            }
                        } else {
                            addOffline(data.inputCMD.value!!)
                        }
                        dialogInterface.dismiss()
                    }
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            data.messageSrc.postValue(
                when (adapter.languageType) {
                    RKLanguage.R2_WORDS_LANGUAGE_ZH_CN -> R.string.add_notice_zh
                    else -> R.string.add_notice_en
                }
            )
            data.showPinyin.postValue(
                when (adapter.languageType) {
                    RKLanguage.R2_WORDS_LANGUAGE_ZH_CN -> true
                    else -> null
                }
            )
            dialogBuilder.create().show()
        }
        return adapter
    }

    fun addOffline(cmd: String, pinyin: String? = null) {
        val item = AddedItem(cmd){ i, _ ->
            when (i) {
                0 -> removeOffline(cmd)
            }
        }
        val words = when (adapter.languageType) {
            RKLanguage.R2_WORDS_LANGUAGE_ZH_CN -> {
                RKOfflineWords(cmd, pinyin!!) { p0 ->
                    if (p0 == cmd) {
                        item.count++
                    }
                }
            }
            else -> {
                RKOfflineWords(cmd, cmd) { p0 ->
                    if (p0 == cmd) {
                        item.count++
                    }
                }
            }
        }
        adapter.insertData(item)
        offlineManager.addOfflineCommands(arrayOf(words))
    }


    fun initOffline(offlineManager: RKOfflineCommandManager) {
        this.offlineManager = offlineManager
        isCH.postValue(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initZHOffline() {
        offlineManager.clearOfflineCommands()
        RKGlassVoice.getInstance().changeVoiceCommandLanguage(RKLanguage.R2_WORDS_LANGUAGE_ZH_CN)

        adapter.clearOthers()
        adapter.languageType = RKLanguage.R2_WORDS_LANGUAGE_ZH_CN
        val addItem1 = AddedItem("大点声"){ i, _ ->
            when (i) {
                0 -> removeOffline("大点声")
            }
        }
        adapter.insertData(addItem1)
        val dadiansheng = RKOfflineWords("大点声", "da dian sheng") { p0 ->
            if (p0 == "大点声") {
                addItem1.count++
            }
        }

        val addItem2 = AddedItem("小点声"){ i, _ ->
            when (i) {
                0 -> removeOffline("小点声")
            }
        }
        adapter.insertData(addItem2)
        val xiaodiansheng = RKOfflineWords("小点声", "xiao dian sheng") { p0 ->
            if (p0 == "小点声") {
                addItem2.count++
            }
        }

        val addItem3 = AddedItem("亮一点"){ i, _ ->
            when (i) {
                0 -> removeOffline("亮一点")
            }
        }
        adapter.insertData(addItem3)
        val liangyidian = RKOfflineWords("亮一点", "liang yi dian") { p0 ->
            if (p0 == "亮一点") {
                addItem3.count++
            }
        }

        val addItem4 = AddedItem("暗一点"){ i, _ ->
            when (i) {
                0 -> removeOffline("暗一点")
            }
        }
        adapter.insertData(addItem4)
        val anyidian = RKOfflineWords("暗一点", "an yi dian") { p0 ->
            if (p0 == "暗一点") {
                addItem4.count++
            }
        }


        offlineManager.addOfflineCommands(
            arrayOf(
                dadiansheng,
                xiaodiansheng,
                liangyidian,
                anyidian,
                addZhItem("扫一扫", "sao yi sao"),
                addZhItem("我的相机", "wo de xiang ji"),
                addZhItem("我的相册", "wo de xiang ce"),
                addZhItem("我的文件", "wo de wen jian"),
                addZhItem("系统设置", "xi tong she zhi"),
                addZhItem("打开第一个", "da kai di yi ge"),
                addZhItem("打开第二个", "da kai di er ge"),
                addZhItem("打开第三个", "da kai di san ge"),
                addZhItem("打开第四个", "da kai di si ge"),
                addZhItem("打开第五个", "da kai di wu ge"),
                addZhItem("打开第六个", "da kai di liu ge"),
                addZhItem("打开第七个", "da kai di qi ge"),
            )
        )

    }

    private fun addZhItem(word: String, pinyin: String): RKOfflineWords{
        val item = AddedItem(word){i, _->
            when(i){
                0->removeOffline(word)
            }
        }
        adapter.insertData(item)
        val words = RKOfflineWords(word, pinyin?:""){p0 ->
            if (p0 == word){
                item.count ++
            }
        }

        return words

    }


    @SuppressLint("NotifyDataSetChanged")
    fun initENOffline() {
        offlineManager.clearOfflineCommands()
        RKGlassVoice.getInstance().changeVoiceCommandLanguage(RKLanguage.R2_WORDS_LANGUAGE_EN_US)

        adapter.clearOthers()
        adapter.languageType = RKLanguage.R2_WORDS_LANGUAGE_EN_US
        val addItem1 = AddedItem("Voice up") { i, _ ->
            when (i) {
                0 -> removeOffline("Voice up")
            }
        }
        adapter.insertData(addItem1)
        val dadiansheng = RKOfflineWords("Voice up", "voice up") { p0 ->
            if (p0 == "Voice up") {
                addItem1.count++
            }
        }

        val addItem2 = AddedItem("Voice down"){ i, _ ->
            when (i) {
                0 -> removeOffline("Voice down")
            }
        }
        adapter.insertData(addItem2)
        val xiaodiansheng = RKOfflineWords("Voice down", "voice down") { p0 ->
            if (p0 == "Voice down") {
                addItem2.count++
            }
        }

        val addItem3 = AddedItem("Brightness up"){ i, _ ->
            when (i) {
                0 -> removeOffline("Brightness up")
            }
        }
        adapter.insertData(addItem3)
        val liangyidian = RKOfflineWords("Brightness up", "brightness up") { p0 ->
            if (p0 == "Brightness up") {
                addItem3.count++
            }
        }

        val addItem4 = AddedItem("Brightness down"){ i, _ ->
            when (i) {
                0 -> removeOffline("Brightness down")
            }
        }
        adapter.insertData(addItem4)
        val anyidian = RKOfflineWords("Brightness down", "brightness down") { p0 ->
            if (p0 == "Brightness down") {
                addItem4.count++
            }
        }

        offlineManager.addOfflineCommands(
            arrayOf(
                dadiansheng,
                xiaodiansheng,
                liangyidian,
                anyidian
            )
        )

    }

    fun removeOffline(str: String) {
        offlineManager.removeOfflineCommands(arrayListOf(str))
        adapter.remove(str)
    }

}

class AddedAdapter(val context: AppCompatActivity, action: (Int) -> Unit) :
    RecyclerView.Adapter<VH>() {

    val items: ArrayList<ViewItem> = ArrayList()
    var languageType = RKLanguage.R2_WORDS_LANGUAGE_ZH_CN

    private val addOtherItem: AddOtherItem = AddOtherItem { i, v ->
        when (i) {
            0 -> {
                action(0)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun remove(str: String){

        var somethingRemoved = false
        for (i in items.indices){
            if (items[i] is AddedItem && (items[i] as AddedItem).cmd == str){
                items.removeAt(i)
                somethingRemoved = true
                break
            }
        }
        if (somethingRemoved){
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearOthers() {
        items.clear()
        items.add(addOtherItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertData(cmd: AddedItem) {
        items.add(itemCount - 1, cmd)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = when (viewType) {
        1 -> {
            VH(
                RcItemVoiceAddedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    .apply {
                        this.lifecycleOwner = context
                    })
        }
        else -> {
            VH(
                RcItemBottomButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).apply {
                    this.lifecycleOwner = context
                })
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                (holder.dataBinding as RcItemVoiceAddedBinding).data = items[position] as AddedItem
            }
            else -> {
                (holder.dataBinding as RcItemBottomButtonBinding).data =
                    items[position] as AddOtherItem
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].type
}

data class AddedItem(
    val cmd: String,
    val commend: MutableLiveData<String> = MutableLiveData(),
    val countString: MutableLiveData<String> = MutableLiveData(),
    val event: (Int, View) -> Unit
) : ViewItem(1, event) {

    init {
        commend.postValue(cmd)
    }

    var count = 0
        set(value) {
            field = value
            countString.postValue(if (count > 0) "$value" else "0")
        }

    fun onRemove(v: View) {
        event(0, v)
    }

}

data class AddOtherItem(val event: (Int, View) -> Unit) : ViewItem(-1, event) {
    fun onItemClicked(v: View) {
        event(0, v)
    }
}

open class ViewItem(val type: Int, val action: (Int, View) -> Unit?)

class VH(val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root)

data class OfflineCMD(
    val messageSrc: MutableLiveData<Int> = MutableLiveData(),
    val inputCMD: MutableLiveData<String> = MutableLiveData(),
    val inputPinyin: MutableLiveData<String> = MutableLiveData(),
    val showPinyin: MutableLiveData<Boolean> = MutableLiveData(),
)