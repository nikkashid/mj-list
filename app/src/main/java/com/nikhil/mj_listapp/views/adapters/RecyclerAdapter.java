package com.nikhil.mj_listapp.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.mj_listapp.R;
import com.nikhil.mj_listapp.database.EntityTable;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
{
	private static final String TAG = "RecyclerAdapter";

	Context context;

	List<EntityTable> al_elements = new ArrayList<>();

	public RecyclerAdapter(Context context)
	{
		this.context = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View itemView;
		itemView = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{
		try
		{
			EntityTable innerPojo = al_elements.get(position);

			JSONObject innerObject = new JSONObject(innerPojo.getData());

			holder.artistName.setText(innerObject.getString("artistName"));
			holder.collectionName.setText(innerObject.getString("collectionName"));
			holder.trackName.setText(innerObject.getString("trackName"));

			if (innerObject.has("artworkUrl100") && !innerObject.getString("artworkUrl100").equalsIgnoreCase(""))
			{
				Picasso.with(context)
						.load(innerObject.getString("artworkUrl100"))
						.into(holder.iv_albumImage);
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "onBindViewHolder: " + e);
		}
	}

	@Override
	public int getItemCount()
	{
		return al_elements.size();
	}

	public void setList(List<EntityTable> entityTables)
	{
		this.al_elements = entityTables;
		notifyDataSetChanged();
	}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		TextView artistName, trackName, collectionName;

		ImageView iv_albumImage;

		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);

			iv_albumImage = (ImageView) itemView.findViewById(R.id.iv_albumImage);
			artistName = (TextView) itemView.findViewById(R.id.artistName);
			collectionName = (TextView) itemView.findViewById(R.id.collectionName);
			trackName = (TextView) itemView.findViewById(R.id.trackName);
		}
	}
}
