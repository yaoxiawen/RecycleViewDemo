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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private List<> mDataList = new ArrayList<>();
    private LeftSnapHelper mSnapHelper;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int width = 100;
    private int num = 0;
    private int mCurBannerPosition = 0;
    private int mLastBannerPosition = 0;
    private static final float STAY_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.ll_rv);

        // TODO: 2019-12-17 mDataList的初始化

        num = mDataList.size();
        mSnapHelper = new LeftSnapHelper();
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mLinearLayoutManager);
        rv.setAdapter(mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                    int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(width, width);
                //params.setMargins(KluiUtils.dp2px(10), 0, KluiUtils.dp2px(10), 0);
                itemView.setLayoutParams(params);
                //ImageView imageView = (ImageView) itemView;
                TestData data = (TestData) mBaseItem;
                mImageView.setBackgroundResource(data.data);
                itemView.setPivotX(0);
                itemView.setPivotY(width / 2);
                itemView.setScaleX(STAY_SCALE);
                itemView.setScaleY(STAY_SCALE);
            }

            @Override
            public int getItemCount() {
                return Integer.MAX_VALUE;
            }
        });
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

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mCurBannerPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                //
                //View view = mLinearLayoutManager.findViewByPosition(mCurBannerPosition);
                //float d = mSnapHelper.calculateDistanceToFinalSnap(mLinearLayoutManager, view)[0];
                Log.v("yxw", "mCurBannerPosition:" + mCurBannerPosition % num);
                //if (mCurBannerPosition < 0) {
                //    return;
                //}
                //if (mCurBannerPosition <= 1) {
                //    if (d<-width){
                //        mLinearLayoutManager.scrollToPositionWithOffset(num + mCurBannerPosition+1, 0);
                //    }else{
                //        mLinearLayoutManager.scrollToPositionWithOffset(num + mCurBannerPosition, 0);
                //    }
                //    //if (d<-width){
                //    //    mLinearLayoutManager.scrollToPositionWithOffset(num + mCurBannerPosition+1, KluiUtils.dp2px(60));
                //    //}else{
                //    //    mLinearLayoutManager.scrollToPositionWithOffset(num + mCurBannerPosition, -KluiUtils.dp2px(60));
                //    //}
                //
                //} else if (3 * num - mCurBannerPosition <= 2) {
                //    if (d<-width){
                //        mLinearLayoutManager.scrollToPositionWithOffset(mCurBannerPosition - num+1, 0);
                //    }else{
                //        mLinearLayoutManager.scrollToPositionWithOffset(mCurBannerPosition - num, 0);
                //    }
                //
                //}
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

        Log.v("yxw", "width:" + width);
        //snapingView.setScaleX(1 + 0.3f * (1 + Math.abs((float) currentSnapingOffset / width)));

        //RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) snapingView.getLayoutParams();
        //Log.v("yxw","height:"+params.height);
        //params.height = (int) (params.height*offset);
        //snapingView.setLayoutParams(params);
        //Log.v("yxw","height:"+params.height);
        //snapingView.setScaleX(offset);
        //snapingView.setScaleY(offset);
        //if (snapingView != null) {
        //    if (currentSnapingOffset>0){
        //        snapingView.setScaleX(1+0.5f*(1-currentSnapingOffset/width));
        //        snapingView.setScaleY(1+0.5f*(1-currentSnapingOffset/width));
        //    }else{
        //        snapingView.setScaleX(1+0.5f*(1+currentSnapingOffset/width));
        //        snapingView.setScaleY(1+0.5f*(1+currentSnapingOffset/width));
        //    }
        //
        //}

        //if (rightSnapingView != null) {
        //    rightSnapingView.setScaleX(rightSnapingOffset/width);
        //    rightSnapingView.setScaleY(rightSnapingOffset/width);
        //}
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


