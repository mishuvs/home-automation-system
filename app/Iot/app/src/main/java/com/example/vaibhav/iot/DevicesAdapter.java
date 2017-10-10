package com.example.vaibhav.iot;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhav.iot.data.DeviceContract;
import com.example.vaibhav.iot.data.IotDbHelper;
import com.example.vaibhav.iot.utilities.Client;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Vaibhav on 9/18/2017.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private static final String TAG = DevicesAdapter.class.getName();
    FirebaseDatabase firebaseDatabase;
    private Cursor mCursor;
    private Context mContext;
    private String Value;
    private int task;
    private int GPIO_NO;

    DevicesAdapter(Context context){
        mContext = context;
        //default constructor
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item,parent,false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, int position) {

        if( mCursor == null || !mCursor.moveToFirst() ){
            Log.i(TAG,"Either cursor is null or number of rows = 0");
            return;
        }

        mCursor.moveToPosition(position);
        //set device name:
        String deviceName = mCursor.getString(mCursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_DEVICE_NAME));
        holder.deviceName.setText(deviceName);

        //set device trigger state:

        final int isChecked = mCursor.getInt(mCursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_DEVICE_STATE));
        holder.deviceTrigger.setChecked(isChecked==1);

        //set device type and port number:
        holder.deviceType = mCursor.getString(mCursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_DEVICE_TYPE));
        holder.portNumber = mCursor.getInt(mCursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_DEVICE_PORT_NUMBER));

        //set device trigger:
        holder.id = mCursor.getInt(mCursor.getColumnIndex(DeviceContract.DeviceEntry._ID));
       /* firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference_ip = firebaseDatabase.getReference("GPIO_"+holder.portNumber);
        reference_ip.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Value = dataSnapshot.getValue(String.class);
                if (Value.equals("0"))
                {

                    holder.deviceTrigger.setChecked(false);
                    task = 1;
                }
                else
                {
                    holder.deviceTrigger.setChecked(true);
                    task=0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }



        });*/

        holder.deviceTrigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               int i = (b) ? 1 : 0;
                Log.i("Device_Adapter","State changed "+holder.portNumber);
                    if (b)
                    {

                            Client client = new Client("192.168.1.10",holder.portNumber,i);
                            client.execute();
                            Log.i("Device_Adapter","State changed "+holder.portNumber);
                    }
                    else
                    {
                        Client client = new Client("192.168.1.10",holder.portNumber,i);
                        client.execute();
                    }
                IotDbHelper dbHelper = new IotDbHelper(mContext);
                String whereClause = DeviceContract.DeviceEntry._ID + " = " + holder.id;



                ContentValues values = new ContentValues();
                values.put(DeviceContract.DeviceEntry.COLUMN_LAST_TRIGGERED, System.currentTimeMillis());
                values.put(DeviceContract.DeviceEntry.COLUMN_DEVICE_STATE, (holder.deviceTrigger.isChecked()) ? 1 : 0);

                dbHelper.getWritableDatabase().update(DeviceContract.DeviceEntry.TABLE_NAME,values,whereClause,null);
                setIcon(holder);
            }

        });

        setIcon(holder);

        holder.deviceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.deviceTrigger.setChecked(!holder.deviceTrigger.isChecked());
                setIcon(holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        if ( mCursor != null && mCursor.moveToFirst() ) {
            mCursor.moveToFirst();
            return mCursor.getCount();
        }
        else return 0;
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView deviceName;
        SwitchCompat deviceTrigger;
        int id, portNumber;
        String deviceType;
        ImageView deviceIcon;

        DeviceViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceTrigger = itemView.findViewById(R.id.device_trigger);
            deviceIcon = itemView.findViewById(R.id.device_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //do something here:
            Intent i = new Intent(mContext,DeviceDetailActivity.class);
            i.putExtra("deviceName",deviceName.getText().toString());
            i.putExtra("deviceState",deviceTrigger.isChecked());
            i.putExtra("deviceId",id);
            i.putExtra("deviceType",deviceType);
            i.putExtra("portNumber",portNumber);
            mContext.startActivity(i);
            //Log.i("Device_Adapter","State changed ");

        }

    }

    public static void setIcon(DeviceViewHolder holder){
        if(holder.deviceTrigger.isChecked()){
            holder.deviceIcon.setImageResource(R.drawable.bulbon);
        }
        else holder.deviceIcon.setImageResource(R.drawable.bulboff);
    }
}
