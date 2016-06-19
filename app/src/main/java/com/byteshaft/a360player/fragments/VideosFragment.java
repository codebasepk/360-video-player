package com.byteshaft.a360player.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.a360player.R;
import com.byteshaft.a360player.player.MD360PlayerActivity;
import com.byteshaft.a360player.utils.AppGlobals;
import com.byteshaft.a360player.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;

public class VideosFragment extends Fragment {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private CustomView mViewHolder;
    private CardsAdapter mCardsAdapter;
    private ArrayList<Integer> idsList;
    private HashMap<Integer, String[]> singleItemData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_videos, container, false);
        mBaseView.setTag("RecyclerViewFragment");
        idsList = new ArrayList<>();
        singleItemData = new HashMap<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.video_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(1);
        mRecyclerView.setHasFixedSize(true);
        idsList.add(0);
        singleItemData.put(0, new String[] {"Interview Prep", "5:52",String.valueOf(
                Helpers.isUserLoggedIn("Interview Prep")) , AppGlobals.INTERVIEW_PREP, "interview_prep"});
        return mBaseView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCardsAdapter = new CardsAdapter(idsList, singleItemData);
        mRecyclerView.setAdapter(mCardsAdapter);
        mRecyclerView.addOnItemTouchListener(new CardsAdapter(idsList, singleItemData,
                getActivity()
                        .getApplicationContext(), new OnItemClickListener() {
            @Override
            public void onItem(Integer item) {
                String url = singleItemData.get(item)[3];
                int value = Helpers.isUserLoggedIn(singleItemData.get(item)[0]);
                Helpers.videoPlayer(singleItemData.get(item)[0], (value+1));
                if (!TextUtils.isEmpty(url)){
                    MD360PlayerActivity.startVideo(getActivity(), Uri.parse(url));
                } else {
                    Toast.makeText(getActivity(), "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    class CardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<Integer> videoIds;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        private HashMap<Integer, String[]> videoData;

        public CardsAdapter(ArrayList<Integer> videoIds, HashMap<Integer, String[]> videoData,
                            Context context,
                            OnItemClickListener listener) {
            this.mListener = listener;
            this.videoIds = videoIds;
            this.videoData = videoData;
            this.mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }
                    });
        }

        public CardsAdapter(ArrayList<Integer> videoIds, HashMap<Integer, String[]> videoData)   {
            this.videoIds = videoIds;
            this.videoData = videoData;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_delegate,
                    parent, false);
            mViewHolder = new CustomView(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            mViewHolder.hiddenId.setText(String.valueOf(videoIds.get(position)));
            mViewHolder.videoTitle.setText(videoData.get(videoIds.get(position))[0]);
            mViewHolder.videoTime.setText(videoData.get(videoIds.get(position))[1]);
            mViewHolder.watchTime.setText("Views " +videoData.get(videoIds.get(position))[2]);
            String drawableName = videoData.get(videoIds.get(position))[4];
            if (!drawableName.trim().isEmpty()) {
                int resId = getResources().getIdentifier(drawableName, "drawable",
                        getActivity().getPackageName());
                if (resId != 0) {
                    Drawable d = getActivity().getResources().getDrawable(resId);
                    mViewHolder.imageView.setImageDrawable(d);
                }
            }

        }

        @Override
        public int getItemCount() {
            return videoIds.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItem(videoIds.get(rv.getChildPosition(childView)));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    // custom class getting view videoIds by giving view in constructor.
    public static class CustomView extends RecyclerView.ViewHolder {

        public TextView hiddenId;
        public TextView videoTitle;
        public TextView watchTime;
        public TextView videoTime;
        public ImageView imageView;

        public CustomView(View itemView) {
            super(itemView);
            hiddenId = (TextView) itemView.findViewById(R.id.hidden_id);
            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            watchTime = (TextView) itemView.findViewById(R.id.watch_time);
            videoTime = (TextView) itemView.findViewById(R.id.video_time);
            imageView = (ImageView) itemView.findViewById(R.id.bitmap);
        }
    }

    public interface OnItemClickListener {
        void onItem(Integer item);
    }
}
