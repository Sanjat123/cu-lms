package com.mandalnet.culms.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.models.Subject;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    private List<Subject> subjectList;

    public SubjectAdapter(List<Subject> list) { this.subjectList = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.name.setText(subject.getName());
        holder.code.setText(subject.getCode());
        holder.attendance.setText("Attendance: " + subject.getAttendance());
    }

    @Override
    public int getItemCount() { return subjectList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, code, attendance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSubjectName);
            code = itemView.findViewById(R.id.tvSubjectCode);
            attendance = itemView.findViewById(R.id.tvAttendance);
        }
    }
}