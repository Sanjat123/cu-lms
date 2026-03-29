package com.mandalnet.culms.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.models.Resource;
import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {
    private List<Resource> resourceList;

    public ResourceAdapter(List<Resource> list) { 
        this.resourceList = list; 
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = resourceList.get(position);
        holder.title.setText(resource.getTitle());
        holder.type.setText(resource.getType() + " - " + resource.getCategory());
        // Icon based on type
        if ("PDF".equals(resource.getType())) {
            holder.icon.setImageResource(android.R.drawable.ic_menu_send);
        } else {
            holder.icon.setImageResource(android.R.drawable.ic_menu_info_details);
        }
    }

    @Override
    public int getItemCount() { return resourceList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivFileType);
            title = itemView.findViewById(R.id.tvResourceTitle);
            type = itemView.findViewById(R.id.tvResourceType);
            itemView.setOnClickListener(v -> {
                // Launch viewer
                // Intent to PdfViewerActivity
            });
        }
    }
}
