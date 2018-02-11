package me.declangao.wordpressreader.adaptor;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.model.Post;

/**
 * RecyclerView Adaptor
 */
public class MyRecyclerViewCategoriesAdaptor extends RecyclerView.Adapter<MyRecyclerViewCategoriesAdaptor.ViewHolder> {
    // A list of posts
    private List<Category> cats;
    private Context mContext;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Category cat);
    }

    public MyRecyclerViewCategoriesAdaptor(ArrayList<Category> cats, OnItemClickListener listener) {
        this.cats = cats;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_home_item, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Glide.with(mContext)
                .load(cats.get(i).getImageUrl())
                .centerCrop()
                .into(viewHolder.thumbnailImageView);

        viewHolder.title.setText(cats.get(i).getName());

        Resources resources = mContext.getResources();
        final int resourceId = resources.getIdentifier("cat_"+cats.get(i).getSlug().replace("-", "_"), "drawable", mContext.getPackageName());

        viewHolder.catIconImageView.setBackgroundResource(resourceId);
        //return resources.getDrawable(resourceId);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(cats.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        ImageView catIconImageView;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail);
            catIconImageView = (ImageView) itemView.findViewById(R.id.cat_icon);
            title = (TextView) itemView.findViewById(R.id.title);
        }

    }
}
