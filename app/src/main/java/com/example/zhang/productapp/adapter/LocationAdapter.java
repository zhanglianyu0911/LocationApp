package com.example.zhang.productapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhang.productapp.R;
import com.example.zhang.productapp.model.LocationModel;
import com.example.zhang.productapp.ui.DetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrew Zhang on 6/23/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ProductViewHolder> {
    private Context mContext;
    private List<LocationModel> productList;
    public LocationAdapter(List<LocationModel> productList) {
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.row_item, parent, false);
        ProductViewHolder viewHolder = new ProductViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {

        final LocationModel productData = productList.get(position);

        holder.local.setText(productData.getLocation());
        holder.address.setText(productData.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationModel dataSet = new LocationModel(productData.getLocation(),
                                        productData.getAddress(),
                                        productData.getArrivalTime(),
                                        productData.getLocLatitude(),
                                        productData.getLocLongitude());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(mContext.getResources().getString(R.string.parcelable_key),dataSet);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_value) TextView local;
        @BindView(R.id.address_value) TextView address;

        public ProductViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }

    }
}
