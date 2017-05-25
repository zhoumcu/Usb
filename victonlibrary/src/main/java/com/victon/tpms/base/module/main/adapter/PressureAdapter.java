package com.victon.tpms.base.module.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.victon.tpms.R;
import com.victon.tpms.common.utils.CommonUtils;
import com.victon.tpms.common.utils.Constants;
import com.victon.tpms.common.utils.Logger;
import com.victon.tpms.common.utils.SharedPreferences;
import com.victon.tpms.common.view.BleData;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/24.
 */
public class PressureAdapter extends BaseAdapter {

    private static final int ROW_NUMBER = 2;
    private GridView mGv;
    private Map<Integer, BleData> mapList;
    private List<BleData> clist;
    private Context mContext;

    public PressureAdapter(Context context, GridView gv, List<BleData> clist) {
        this.mContext = context;
        this.clist = clist;
        this.mGv = gv;
    }

    public PressureAdapter(Context context,GridView gv, Map<Integer, BleData> mapList) {
        this.mContext = context;
        this.mapList = mapList;
        this.mGv = gv;
        Logger.e(mapList.size()+"...............");
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_grid_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //高度计算
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                mGv.getHeight()/ROW_NUMBER);
        convertView.setLayoutParams(param);
        if(mapList.get(position)==null) return convertView;
        holder.tvPreesure.setText(mapList.get(position).getStringPress());
        holder.tvTemp.setText(mapList.get(position).getTemp()+"");
//        ManageDevice.status[] statusData = ManageDevice.status.values();
        //状态检测
//        if(mapList.get(position).getStatus()==1||mapList.get(position).getStatus()==2||mapList.get(position).getStatus()==4) {
//            holder.tvNotes.setText(statusData[mapList.get(position).getStatus()] + " ");
//        }else{
//            holder.tvNotes.setText("");
//        }
        holder.tvNotes.setText(mapList.get(position).getErrorState());

        holder.preesureunit.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        holder.tempunit.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));

        if(mapList.get(position).isNoReceviceData()){
            holder.bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.home_bg));
            holder.preesureunit.setTextColor(mContext.getResources().getColor(R.color.dark_gray_ap));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.dark_gray_ap));
            holder.preesureunit.setText("-.-");
            holder.tvTemp.setText("-");
            return convertView;
        }
        if(mapList.get(position).isException()){
            holder.bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            holder.preesureunit.setTextColor(mContext.getResources().getColor(R.color.errored));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            holder.bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.dark_gray_ap));
            holder.preesureunit.setTextColor(mContext.getResources().getColor(R.color.blue_night));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        return convertView;
    }
    public void updateItem(List<BleData> list) {
        clist = list;
        notifyDataSetChanged();
    }
    /**
     * 刷新指定item
     *
     * @param index item在gridView中的位置
     */
    public void updateItem(GridView gridView,BleData data,int index) {
        if (gridView == null||index>4) {
            return;
        }
        // 获取当前可以看到的item位置
        int visiblePosition = gridView.getFirstVisiblePosition();
        // 如添加headerview后 firstview就是hearderview
        // 所有索引+1 取第一个view
        // View view = listview.getChildAt(index - visiblePosition + 1);
        // 获取点击的view
//        Logger.e("visiblePosition:"+visiblePosition+"index:"+index);
        View view = gridView.getChildAt(index - visiblePosition);
        if(view==null) return;
        TextView preesure = (TextView) view.findViewById(R.id.tv_preesure);
        TextView tvTemp = (TextView) view.findViewById(R.id.tv_temp);
        TextView tvNotes = (TextView) view.findViewById(R.id.tv_notes);
        LinearLayout bgGround = (LinearLayout) view.findViewById(R.id.bg_ground);
        TextView preesureunit = (TextView) view.findViewById(R.id.preesureunit);
        TextView tempunit = (TextView) view.findViewById(R.id.tempunit);
        ImageView imgWarm = (ImageView) view.findViewById(R.id.img_warm);
        // 获取mDataList.set(ids, item);更新的数据
//        BleData data = (BleData) getItem(index);
        // 重新设置界面显示数据
        preesure.setText(data.getStringPress());
        tvTemp.setText(data.getTemp()+"");
        preesureunit.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        tempunit.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
//        ManageDevice.status[] statusData = ManageDevice.status.values();
        //状态检测
//        if(data.getStatus()==1||data.getStatus()==2||data.getStatus()==4) {
//            tvNotes.setText(statusData[data.getStatus()] + " ");
//        }else{
//            tvNotes.setText("");
//        }
        tvNotes.setText(data.getErrorState());
        if(data.isNoReceviceData()){
            bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.home_bg));
            preesureunit.setTextColor(mContext.getResources().getColor(R.color.dark_gray_ap));
            tvTemp.setTextColor(mContext.getResources().getColor(R.color.dark_gray_ap));
            preesureunit.setText("-.-");
            tvTemp.setText("-");
            return;
        }
        if(data.isException()){
            bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            preesureunit.setTextColor(mContext.getResources().getColor(R.color.errored));
            tvTemp.setTextColor(mContext.getResources().getColor(R.color.white));
//            imgWarm.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.pm_warning));
        }else {
            bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.dark_gray_ap));
            preesureunit.setTextColor(mContext.getResources().getColor(R.color.blue_night));
            tvTemp.setTextColor(mContext.getResources().getColor(R.color.white));
            imgWarm.setImageDrawable(null);
        }
        Logger.e("更新item数据："+index);
    }
    public void setListViewHeightBasedOnChildren(Activity activity,GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        int itemHeight = CommonUtils.getScreenHeight(activity)/2;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
//            listItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight));
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = itemHeight*2;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);

        notifyDataSetChanged();
    }

    public void updateItem(Map<Integer, BleData> bleDataMap) {
        mapList = bleDataMap;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView preesureunit;
        TextView tvPreesure;
        TextView tvTemp;
        TextView tempunit;
        TextView tvNotes;
        LinearLayout bgGround;
        ImageView imgWarm;

        ViewHolder(View view) {
            preesureunit = (TextView) view.findViewById(R.id.preesureunit);
            tvPreesure = (TextView) view.findViewById(R.id.tv_preesure);
            tvTemp = (TextView) view.findViewById(R.id.tv_temp);
            tempunit = (TextView) view.findViewById(R.id.tempunit);
            tvNotes = (TextView) view.findViewById(R.id.tv_notes);
            bgGround = (LinearLayout) view.findViewById(R.id.bg_ground);
            imgWarm = (ImageView) view.findViewById(R.id.img_warm);
        }
    }
}
