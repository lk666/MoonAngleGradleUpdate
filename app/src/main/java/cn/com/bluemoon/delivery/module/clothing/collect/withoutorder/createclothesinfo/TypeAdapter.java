//package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.createclothesinfo;
// // TODO: lk 2016/6/30 解决v4与v7的冲突，改用RecycleView实现横向列表
///**
// * 服务类型adapter
// * Created by lk on 2016/6/30.
// */
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import cn.com.bluemoon.delivery.R;
//import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;
//import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
//
//public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> implements
//        OnListItemClickListener {
//    private LayoutInflater layoutInflater;
//    private List<ClothesTypeInfo> list;
//    private int selectedIndex;
//    OnListItemClickListener listener;
//
//    private int colorSelected;
//    private int colorNormai;
//
//    public TypeAdapter(Context context, OnListItemClickListener listener) {
//        selectedIndex = 0;
//        this.listener = listener;
//        layoutInflater = LayoutInflater.from(context);
//        colorSelected = context.getResources().getColor(R.color.text_red);
//        colorNormai = context.getResources().getColor(R.color.text_black_light);
//    }
//
//    public void setSelectedIndex(int selectedIndex) {
//        this.selectedIndex = selectedIndex;
//    }
//
//    public void setList(List<ClothesTypeInfo> list) {
//        this.list = list;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new ViewHolder(layoutInflater.inflate(R.layout
//                .item_clothes_type_info, parent, false), this);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        ClothesTypeInfo item = list.get(position);
//        holder.tvTypeName.setText(item.getTypeName());
//
//        if (position == selectedIndex) {
//            holder.tvTypeName.setTextColor(colorSelected);
//            holder.vUnderLine.setVisibility(View.VISIBLE);
//        } else {
//            holder.tvTypeName.setTextColor(colorNormai);
//            holder.vUnderLine.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    @Override
//    public void onItemClick(Object item, View view, int position) {
//        if (position < getItemCount()) {
//            if (listener != null) {
//                listener.onItemClick(list.get(position), view, position);
//            }
//        }
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @Bind(R.id.tv_type_name)
//        TextView tvTypeName;
//        @Bind(R.id.v_underline)
//        View vUnderLine;
//        OnListItemClickListener listener;
//
//        public ViewHolder(View itemView, OnListItemClickListener listener) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//            this.listener = listener;
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            int position = getLayoutPosition();
//            if (position != getOldPosition() && listener != null) {
//                listener.onItemClick(position, view, position);
//            }
//        }
//    }
//}
