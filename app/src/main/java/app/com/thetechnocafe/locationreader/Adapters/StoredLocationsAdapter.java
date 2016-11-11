package app.com.thetechnocafe.locationreader.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.locationreader.MainLocationReader.LocationModel;
import app.com.thetechnocafe.locationreader.R;

/**
 * Created by gurleensethi on 11/11/16.
 */

public class StoredLocationsAdapter extends RecyclerView.Adapter<StoredLocationsAdapter.StoredLocationsViewHolder> {
    private Context mContext;
    private List<LocationModel> mList;

    public StoredLocationsAdapter(Context context, List<LocationModel> list) {
        mContext = context;
        mList = list;
    }

    //View holder class
    class StoredLocationsViewHolder extends RecyclerView.ViewHolder {
        private TextView mLatitudeTextView;
        private TextView mLongitudeTextView;
        private TextView mLocationNameTextView;
        private ImageButton mDeleteImageButton;

        StoredLocationsViewHolder(View view) {
            super(view);

            mLatitudeTextView = (TextView) view.findViewById(R.id.stored_location_latitude_text_view);
            mLongitudeTextView = (TextView) view.findViewById(R.id.stored_location_longitude_text_view);
            mLocationNameTextView = (TextView) view.findViewById(R.id.stored_location_name_text_view);
            mDeleteImageButton = (ImageButton) view.findViewById(R.id.stored_location_delete_image_button);

            setUpOnClickListeners();
        }

        private void setUpOnClickListeners() {
            mDeleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        //Data bind
        public void bindData(int position) {
            LocationModel model = mList.get(position);
            mLocationNameTextView.setText(model.getLocationName());
            mLatitudeTextView.setText(String.valueOf(model.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(model.getLongitude()));
        }
    }

    @Override
    public StoredLocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_stored_locations, parent, false);
        return new StoredLocationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoredLocationsViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateList(List<LocationModel> list) {
        mList = list;
    }
}
