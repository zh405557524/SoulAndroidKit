package com.soul.soulkit.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soul.android_kit.R

/**
 * 测试模块列表适配器
 */
class TestModuleAdapter(
    private val onModuleClick: (TestModule) -> Unit
) : RecyclerView.Adapter<TestModuleAdapter.ViewHolder>() {

    private var modules = listOf<TestModule>()

    fun updateModules(newModules: List<TestModule>) {
        modules = newModules
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_module, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int = modules.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconText: TextView = itemView.findViewById(R.id.tvIcon)
        private val nameText: TextView = itemView.findViewById(R.id.tvName)
        private val descriptionText: TextView = itemView.findViewById(R.id.tvDescription)
        private val statusText: TextView = itemView.findViewById(R.id.tvStatus)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onModuleClick(modules[position])
                }
            }
        }

        fun bind(module: TestModule) {
            iconText.text = module.icon
            nameText.text = module.name
            descriptionText.text = module.description
            statusText.text = if (module.activityClass != null) "✅ 可测试" else "⏳ 待开发"
            
            // 设置可用状态的样式
            itemView.alpha = if (module.activityClass != null) 1.0f else 0.6f
            itemView.isEnabled = module.activityClass != null
        }
    }
} 