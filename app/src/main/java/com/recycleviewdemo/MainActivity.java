package com.recycleviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private List<Integer> mDataList = new ArrayList<>();
    private LeftSnapHelper mSnapHelper;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int width = 500;
    private int num = 0;
    private int mCurBannerPosition = 0;
    private int mLastBannerPosition = 0;
    private static final float STAY_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.ll_rv);
        mDataList.add(R.drawable.image_practice_repast);
        mDataList.add(R.drawable.image_practice_repast0);
        mDataList.add(R.drawable.image_practice_repast1);
        mDataList.add(R.drawable.image_practice_repast2);
        mDataList.add(R.drawable.image_practice_repast_5);
        mDataList.add(R.drawable.image_practice_repast_6);
        num = mDataList.size();
        mSnapHelper = new LeftSnapHelper();
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mLinearLayoutManager);
        rv.setAdapter(mAdapter = new MyAdapter(mDataList));
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                    RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 10;
            }
        });
        mSnapHelper.attachToRecyclerView(rv);

        rv.addOnScrollListener(mOnScrollListener);
        mAdapter.notifyDataSetChanged();
        rv.post(new Runnable() {
            @Override
            public void run() {
                int mid = Integer.MAX_VALUE / 2;
                int startPosition = mid - mid % num;
                mLinearLayoutManager.scrollToPositionWithOffset(startPosition, 0);
                pageScrolled();
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<Integer> mDatas;

        public MyAdapter(List<Integer> data) {
            this.mDatas = data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.test, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(width, width);
            //params.setMargins(KluiUtils.dp2px(10), 0, KluiUtils.dp2px(10), 0);
            holder.itemView.setLayoutParams(params);
            holder.iv.setBackgroundResource(mDatas.get(position%num));
            holder.itemView.setPivotX(0);
            holder.itemView.setPivotY(width / 2);
            holder.itemView.setScaleX(STAY_SCALE);
            holder.itemView.setScaleY(STAY_SCALE);
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv);
            }
        }
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //super.onScrolled(recyclerView, dx, dy);
            pageScrolled();
        }
    };

    private void pageScrolled() {

        View snapingView = mSnapHelper.findSnapView(mLinearLayoutManager);
        int snapingViewPosition = rv.getChildAdapterPosition(snapingView);
        Log.v("yxw", "snapingViewPosition:" + snapingViewPosition);
        int currentSnapingOffset =
                mSnapHelper.calculateDistanceToFinalSnap(mLinearLayoutManager, snapingView)[0];
        int dis = Math.abs(currentSnapingOffset);
        float offset = 1 - (1 - STAY_SCALE) * ((float) dis / width);
        snapingView.setScaleX(offset);
        snapingView.setScaleY(offset);

        View leftSnapingView = mLinearLayoutManager.findViewByPosition(snapingViewPosition - 1);
        View rightSnapingView = mLinearLayoutManager.findViewByPosition(snapingViewPosition + 1);

        if (rightSnapingView != null) {
            int rightSnapingOffset = mSnapHelper.calculateDistanceToFinalSnap(mLinearLayoutManager,
                    rightSnapingView)[0];
            Log.v("yxw", "rightSnapingOffset:" + rightSnapingOffset);
            dis = Math.abs(rightSnapingOffset);
            if (dis > width) {
                offset = STAY_SCALE;
            } else {
                offset = 1 - (1 - STAY_SCALE) * ((float) dis / width);
            }
            rightSnapingView.setScaleX(offset);
            rightSnapingView.setScaleY(offset);
        }
        if (leftSnapingView != null) {
            int leftSnapingOffset = mSnapHelper.calculateDistanceToFinalSnap(mLinearLayoutManager,
                    leftSnapingView)[0];
            Log.v("yxw", "leftSnapingOffset:" + leftSnapingOffset);
            dis = Math.abs(leftSnapingOffset);
            if (dis > width) {
                offset = STAY_SCALE;
            } else {
                offset = 1 - (1 - STAY_SCALE) * ((float) dis / width);
            }
            leftSnapingView.setScaleX(offset);
            leftSnapingView.setScaleY(offset);
        }
    }

    private class LeftSnapHelper extends PagerSnapHelper {
        private OrientationHelper mHorizontalHelper;

        @Override
        public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                @NonNull View targetView) {
            //return super.calculateDistanceToFinalSnap(layoutManager, targetView);
            int[] out = new int[2];
            //判断支持水平滚动，修改水平方向的位置，是修改的out[0]的值
            if (layoutManager.canScrollHorizontally()) {
                out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
            } else {
                out[0] = 0;
            }
            return out;
        }

        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            //return super.findSnapView(layoutManager);
            return findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }

        private View findStartView(RecyclerView.LayoutManager layoutManager,
                OrientationHelper helper) {

            if (layoutManager instanceof LinearLayoutManager) {
                int firstChild =
                        ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                //int lastChild = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                if (firstChild == RecyclerView.NO_POSITION) {
                    return null;
                }

                View child = layoutManager.findViewByPosition(firstChild);
                //获取偏左显示的Item
                if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                        && helper.getDecoratedEnd(child) > 0) {
                    return child;
                } else {
                    return layoutManager.findViewByPosition(firstChild + 1);
                }
            }

            return super.findSnapView(layoutManager);
        }

        private int distanceToStart(View targetView, OrientationHelper helper) {
            return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
        }

        private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
            if (mHorizontalHelper == null) {
                mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
            }
            return mHorizontalHelper;
        }
    }
}


