package com.likwidskin.cabAgg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by kanavarora on 3/16/16.
 */
public class SetDestinationView extends LinearLayout {

    private TextView textView;
    private ImageView pinView;
    public boolean isPickup;

    private GoogleApiClient mGoogleApiClient;

    private AddressResultReceiver mResultReceiver;
    private Location location;

    public SetDestinationView(Context context) {
        super(context);
        initViews(context);
    }

    public SetDestinationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public SetDestinationView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        initViews(context);
    }
    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.set_destination_view, this);
        this.textView = (TextView)findViewById(R.id.textView);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    public void setWithPin(Location loc) {
        if (mGoogleApiClient.isConnected()) {
            startReverseGeoIntentService(loc);
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        //mAddressRequested = true;
        //updateUIWidgets();
    }

    public void setWithAddress(Location loc, String address) {
        this.location = location;
        this.textView.setText(address);

    }

    private void setText(String address) {
        this.textView.setText(address);
    }

    private void startReverseGeoIntentService (Location location){
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setView(this);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location);
        getContext().startService(intent);
    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        private SetDestinationView mDestinationView;
        private void setView(SetDestinationView destinationView) {
            this.mDestinationView = destinationView;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                String address = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
                this.mDestinationView.setText(address);
            } else {
                this.mDestinationView.setText("");
            }
            //displayAddressOutput();

            /*
            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }
            */

        }
    }
}
