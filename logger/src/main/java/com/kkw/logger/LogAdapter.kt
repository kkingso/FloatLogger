package com.kkw.logger

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kkw.logger.databinding.ItemLogBinding

/**
 * 日志适配器
 * @author kkw
 * @date 2023/11/14
 */
class LogAdapter : BaseQuickAdapter<LogEntity, LogAdapter.ViewHolder>() {

    private lateinit var mBinding: ItemLogBinding

    class ViewHolder(itemView: ItemLogBinding) : RecyclerView.ViewHolder(itemView.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mBinding = ItemLogBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: LogEntity?) {
        mBinding.logMsg.text = item?.msg
    }
}

/**
 * 日志实体类
 * @author kkw
 * @date 2023/11/14
 */
data class LogEntity(val msg: String?)