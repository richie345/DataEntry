package dataentry.ochanya.com.dataentry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ephraim on 1/14/2020.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private String[] fullname;
    private String[] reg_date;
    private String[] user_id;
    private Context con;

    //Viewer to connect the adapter layout
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_fullname;
        private TextView tv_reg_date;
        public MyViewHolder(View v){
            super(v);
            //textView = (TextView) v.findViewById(R.id.mytext);
            //code to display from layout here
            tv_fullname=(TextView) v.findViewById(R.id.recycler_name);
            tv_reg_date=(TextView) v.findViewById(R.id.recycler_date);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(String[] fn, String[] rd, String[] id){
        fullname=fn;
        reg_date=rd;
        user_id=id;
    }

    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        con=context;
        LayoutInflater inflater=LayoutInflater.from(context);
        View recycler=inflater.inflate(R.layout.activity_recycler_adapter, parent, false);

        MyViewHolder vh=new MyViewHolder(recycler);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Get string values from each array using its position on the vview row
        final String us_fn=fullname[position];
        final String us_id=user_id[position];
        final String us_rd=reg_date[position];

        //get components in the recycler layout
        TextView tv_us_fn=holder.tv_fullname;
        TextView tv_us_rd=holder.tv_reg_date;

        //asign values to components in the recycler layout
        tv_us_fn.setText(us_fn);
        tv_us_rd.setText(us_rd);

        //redirection code here
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fullname.length;
    }
}
