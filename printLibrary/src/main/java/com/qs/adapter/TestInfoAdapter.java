package com.qs.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qs.bean.TestInfo;
import com.qs.demo3506.R;
public class TestInfoAdapter extends BaseAdapter{
	private Context context;
	private List<TestInfo> items;
	private LayoutInflater inflater;
	public TestInfoAdapter(Context context, List<TestInfo> items){
		this.context=context;
		this.items=items;
		this.inflater=LayoutInflater.from(context);
	}
	public void setData(List<TestInfo>items){
		this.items=items;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub           
		return items.size(); 
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}
	@Override
	public long getItemId(int position) {  
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_testinfo, null);
			holder.mName=(TextView)convertView.findViewById(R.id.mTv);
			holder.mIco = (ImageView)convertView.findViewById(R.id.mIv);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}  
		final TestInfo item =items.get(position);
		holder.mName.setText(item.getName());
		int mIcoRes = items.get(position).getIco();
		if(mIcoRes==-1){
			holder.mIco.setImageResource(R.drawable.ic_launcher);
		}else{
			holder.mIco.setImageResource(mIcoRes);
		}
		
		return convertView;                 
	} 
	class ViewHolder{
		ImageView mIco;
		TextView mName;
	
		
	}
	

}
