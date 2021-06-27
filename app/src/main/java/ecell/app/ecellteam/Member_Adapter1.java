package ecell.app.ecellteam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public  class Member_Adapter1 extends RecyclerView.Adapter<Member_Adapter1.ViewHolder>{



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public TextView tv_department;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            tv_name = (TextView) v.findViewById(R.id.tv_name);

            tv_department = (TextView) v.findViewById(R.id.tv_department);

        }

    }

    private ArrayList<Member> membersList;

    public Member_Adapter1(ArrayList<Member> members){
        membersList = new ArrayList<Member>();
        membersList = members;
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public Member_Adapter1.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View memberView = inflater.inflate(R.layout.name_members, parent, false);
        ViewHolder viewHolder = new ViewHolder(memberView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Member_Adapter1.ViewHolder holder, int position) {
        Member member = membersList.get(position);

        holder.tv_name.setText(member.getName());
        holder.tv_department.setText(member.getDepartment());
        // holder.tv_position.setText(String.valueOf(position+1));


    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }


}