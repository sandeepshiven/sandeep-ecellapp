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
import java.util.Collections;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_position;
        public TextView tv_name;
        public TextView tv_department;
        public TextView tv_score;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_position = (TextView) v.findViewById(R.id.tv_position);
            tv_department = (TextView) v.findViewById(R.id.tv_department);
            tv_score = (TextView) v.findViewById(R.id.tv_score);
        }

    }

    private ArrayList<Member> membersList;

    public MemberAdapter(ArrayList<Member> members){
        membersList = new ArrayList<Member>();
        membersList = members;
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View memberView = inflater.inflate(R.layout.item_member, parent, false);
        ViewHolder viewHolder = new ViewHolder(memberView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MemberAdapter.ViewHolder holder, int position) {
        Member member = membersList.get(position);

        holder.tv_name.setText(member.getName());
        holder.tv_department.setText(member.getDepartment());
        holder.tv_position.setText(String.valueOf(position+1));
        holder.tv_score.setText(member.getScore().toString());

    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }


}

