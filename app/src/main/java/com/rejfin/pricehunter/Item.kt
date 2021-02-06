package com.rejfin.pricehunter
import android.view.View
import com.rejfin.pricehunter.databinding.ItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class Item(val product: Product) : BindableItem<ItemBinding>() {

    override fun initializeViewBinding(view: View): ItemBinding {
        return ItemBinding.bind(view)
    }

    override fun getLayout(): Int {
        return R.layout.item
    }

    override fun bind(viewBinding: ItemBinding, position: Int) {
        viewBinding.tvItemName.text = product.name
        viewBinding.tvPrice.text = "${product.price}${product.currency}"
        viewBinding.ivItemIcon.setImageResource(R.drawable.ic_default_product)
    }
}