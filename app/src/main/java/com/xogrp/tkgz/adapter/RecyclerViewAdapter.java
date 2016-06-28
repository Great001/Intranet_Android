package com.xogrp.tkgz.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.xogrp.tkgz.R;

import java.util.List;

/**
 * Created by hliao on 6/21/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private List<PoiInfo> mList;
    private Context mContext;
    private Cursor mCursor;
    private OnClickListener mClickListener;

   public  RecyclerViewAdapter(Context context){
        this.mContext=context;
    }

    public void setPoiList(List<PoiInfo> list){
        this.mList=list;
    }

    public void setPoiCursor(Cursor cursor){
        this.mCursor=cursor;
    }



    @Override
    public int getItemCount() {
        return mList==null?mCursor.getCount():mList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.poiresult_layout,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(mList!=null) {
            holder.tvAddrName.setText(mList.get(position).name);
            holder.tvAddrDetail.setText(mList.get(position).address);
        }
        else if(mCursor.moveToPosition(position)){
            holder.tvAddrDetail.setText(mCursor.getString(mCursor.getColumnIndex("addrDetail")));
            holder.tvAddrName.setText(mCursor.getString(mCursor.getColumnIndex("addrName")));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener!=null) {
                    mClickListener.OnItemClick(position, mList == null ? 0 : 1);
                }
            }
        });

    }

    class  MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvAddrName,tvAddrDetail;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvAddrName=(TextView)itemView.findViewById(R.id.tv_PoiResult_addrName);
            tvAddrDetail=(TextView)itemView.findViewById(R.id.tv_PoiResult_addrDetail);
        }
    }

    public interface OnClickListener{
        void OnItemClick(int position, int flag);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.mClickListener=onClickListener;
    }

}
