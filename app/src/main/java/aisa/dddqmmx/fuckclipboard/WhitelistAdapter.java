package aisa.dddqmmx.fuckclipboard;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WhitelistAdapter extends RecyclerView.Adapter<WhitelistAdapter.ViewHolder> {

    private final List<String> whitelist;
    private final OnItemRemoveListener removeListener;

    public interface OnItemRemoveListener {
        void onRemove(String pkg);
    }

    public WhitelistAdapter(List<String> whitelist, OnItemRemoveListener listener) {
        this.whitelist = whitelist;
        this.removeListener = listener;
    }

    public void addPackage(String pkg) {
        whitelist.add(pkg);
        notifyItemInserted(whitelist.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pkg = whitelist.get(position);
        holder.text.setText(pkg);
        holder.itemView.setOnLongClickListener(v -> {
            removeListener.onRemove(pkg);
            int index = whitelist.indexOf(pkg);
            whitelist.remove(pkg);
            notifyItemRemoved(index);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return whitelist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View view) {
            super(view);
            text = view.findViewById(android.R.id.text1);
        }
    }
}
