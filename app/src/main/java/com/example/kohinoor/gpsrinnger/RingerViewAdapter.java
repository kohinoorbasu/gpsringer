package com.example.kohinoor.gpsrinnger;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kohinoor.gpsrinnger.model.LocationGPSModel;

import java.util.List;

/**
 * Created by Kohinoor on 1/6/2017.
 */

public class RingerViewAdapter extends BaseAdapter {

    private ViewHolder _holder = null;
    private List<LocationGPSModel> _data = null;
    private LayoutInflater _inflater = null;
    private Activity _parent = null;
    public RingerViewAdapter() {
    }

    public RingerViewAdapter(Activity act, List<LocationGPSModel> map)
    {
        this._data= map;
        this._parent = act;
        _inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if (_data != null)
            return _data.size();

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (_data != null && position < _data.size())
            return _data.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        try
        {
            View vi = convertView;
            if(convertView == null)
            {
                vi = _inflater.inflate(R.layout.listview_row, null);
                _holder = new ViewHolder();
                _holder.txtLocation = (TextView)vi.findViewById(R.id.txtLocation); // city name
                _holder.txtRingerStatus = (TextView)vi.findViewById(R.id.txtRingerStatus); // city weather overview
                vi.setTag(_holder);
            }
            else
            {
                _holder = (ViewHolder)vi.getTag();
            }

            _holder.txtLocation.setText(_data.get(position).get_locationName());
            _holder.txtRingerStatus.setText("Ringer: " + Integer.toString(_data.get(position).get_Vol()));

            ImageView dltbutton = (ImageView) vi.findViewById(R.id.btnDelete);
            dltbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                        LocationGPSModel rec = _data.get(position);
                        if (((SettingsActivity)_parent).btnListItemDelete_onClick(v, rec))
                        {
                            _data.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.d(ex.getMessage(), ex.getClass().getName());
                    }
                }
            });

            return vi;
        }
        catch (Exception ex)
        {
            Log.d(ex.getMessage(), ex.getMessage());
            return null;
        }
    }

    static class ViewHolder{
        TextView txtLocation;
        TextView txtRingerStatus;
    }
}
