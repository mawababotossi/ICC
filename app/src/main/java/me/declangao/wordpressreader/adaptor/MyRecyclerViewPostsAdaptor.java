package me.declangao.wordpressreader.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.model.Post;

/**
 * RecyclerView Adaptor
 */
public class MyRecyclerViewPostsAdaptor extends RecyclerView.Adapter<MyRecyclerViewPostsAdaptor.ViewHolder> {
    // A list of posts
    private List<Post> posts;
    private Context mContext;
    private boolean isEvent;
    private boolean isAudio;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public MyRecyclerViewPostsAdaptor(ArrayList<Post> posts, OnItemClickListener listener) {
        this.posts = posts;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_item, viewGroup, false);

        if(posts.get(i).isEvent()) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_agenda_item, viewGroup, false);
            isEvent = true;
        }else if(posts.get(i).isAudio()){
            isAudio = true;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_audio_item, viewGroup, false);
        }

        mContext = viewGroup.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Glide.with(mContext)
                .load(posts.get(i).getThumbnailUrl())
                //.load(posts.get(i).getFeaturedImageUrl()) //I can load fetured image instead
                .centerCrop()
                .into(viewHolder.thumbnailImageView);

        viewHolder.title.setText(posts.get(i).getTitle());

        int count = posts.get(i).getCommentCount();
        String countText = (count == 1 || count == 0) ? count + " Comment" : count + " Comments";
        viewHolder.commentCount.setText(countText);

        String where = "";
        String when = "";
        String whenWeekDay = "";
        String whenMonth = "";
        String whenDate = "";

        try {
            where = posts.get(i).getCustomFields().get("where").toString().replace("[\"", "").replace("\"]","");
            when = posts.get(i).getCustomFields().get("when").toString().replace("[\"", "").replace("\"]","");
            String[] whenArray = when.split(" ");
            whenWeekDay = whenArray[0];
            whenDate = whenArray[1];
            whenMonth = whenArray[2];
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(isEvent) {
            viewHolder.whenWhere.setText(when +" - "+ where);
            viewHolder.whenDate.setText(whenDate);
            viewHolder.whenWeekDay.setText(whenWeekDay.substring(0, 3)+".");
        }
        else if(isAudio) {
            viewHolder.date.setText(posts.get(i).getDate().substring(0,10));
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(posts.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder { // must be a static class !!!!
        ImageView thumbnailImageView;
        TextView title;
        TextView date;
        TextView whenWhere;
        TextView whenDate;
        TextView whenWeekDay;

        TextView commentCount;

        public ViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            commentCount = (TextView) itemView.findViewById(R.id.comment_count);

            if(MyRecyclerViewPostsAdaptor.this.isEvent) {
                whenWhere = (TextView) itemView.findViewById(R.id.when_where);
                whenDate = (TextView) itemView.findViewById(R.id.day);
                whenWeekDay = (TextView) itemView.findViewById(R.id.week_day);
            }

            if(MyRecyclerViewPostsAdaptor.this.isAudio) {
                date = (TextView) itemView.findViewById(R.id.date);
            }
        }

    }
}
