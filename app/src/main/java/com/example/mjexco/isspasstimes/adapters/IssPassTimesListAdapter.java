package com.example.mjexco.isspasstimes.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mjexco.isspasstimes.R;
import com.example.mjexco.isspasstimes.objects.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Adapter class to display ISS pass times in Recycler View
 */

public class IssPassTimesListAdapter extends RecyclerView.Adapter<IssPassTimesListAdapter.PassTimesViewHolder>{
    private List<Response> passTimes;
    private int itemView;

    public IssPassTimesListAdapter(List<Response> passTimes, int itemView) {
        this.passTimes = passTimes;
        this.itemView = itemView;
    }

    @Override
    public PassTimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemView, parent, false);
        return new PassTimesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PassTimesViewHolder holder, int position) {
        holder.timeStamp.setText(getTimeStamp(passTimes.get(position).getRisetime()));
        holder.duration.setText(String.format("%ds", passTimes.get(position).getDuration()));
    }

    /**
     * Parses integer time stamp into readable data for display
     * @param time unreadable time stamp returned
     * @return readable time stamp for display
     */
    private String getTimeStamp(int time){
        String date = "unknown";
        String dateFormat ="dd-MM-yyyy - hh:mm a";
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTimeInMillis(time * 1000L);
            date = DateFormat.format(dateFormat, cal.getTimeInMillis()).toString();

            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
            dateFormatter.setTimeZone(TimeZone.getDefault());
            date = dateFormatter.format(value);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public int getItemCount() {
        return passTimes.size();
    }

    static class PassTimesViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView timeStamp;
        TextView duration;

        PassTimesViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_layout);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
