package com.baiyu.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author lpc
 * <p>
 * 分享列表界面
 */
public final class OkShareFragment extends DialogFragment implements OkOnItemClick<OkShareOption> {

    static final String ARG_SHARE_MESSAGE = "ARG_SHARE_MESSAGE";
    static final String ARG_SHARE_OPTIONS = "ARG_SHARE_OPTIONS";

    RecyclerView rvOptionsList;
    FragmentActivity activity;

    private OkShareMessage message;
    private List<OkShareOption> options;

    private OneKeyShareSdk shareSdk;

    public OkShareFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getArgShareMessage();
        getArgShareOptions();
//        setStyle(STYLE_NO_TITLE, R.style.ok_share_bottom_dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;

            window.setAttributes(params);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ok_share_fragment_option_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        shareSdk = new OneKeyShareSdk(activity);

        rvOptionsList = view.findViewById(R.id.rv_share_options);
        GridLayoutManager glm = new GridLayoutManager(activity, 4);
        rvOptionsList.setLayoutManager(glm);
        rvOptionsList.setHasFixedSize(true);
        rvOptionsList.setAdapter(new ShareOptionAdapter(options, this));
    }


    /**
     * 第三方分享回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void shareActivityResult(int requestCode, int resultCode, Intent data) {
        shareSdk.onActivityResult(requestCode, resultCode, data);
    }

    private void getArgShareMessage() {
        Bundle bundle = getArguments();
        if (bundle == null
                || bundle.getParcelable(ARG_SHARE_MESSAGE) == null) {
            throw new NullPointerException("The arg share message is null");
        }

        message = bundle.getParcelable(ARG_SHARE_MESSAGE);
    }

    private void getArgShareOptions() {
        Bundle bundle = getArguments();
        if (bundle == null
                || bundle.getParcelableArrayList(ARG_SHARE_OPTIONS) == null) {
            // 设置默认的数据
            options = new ArrayList<>();

        }

        if (bundle != null
                && bundle.getParcelableArrayList(ARG_SHARE_OPTIONS) != null) {
            options = bundle.getParcelableArrayList(ARG_SHARE_OPTIONS);
            Collections.sort(Objects.requireNonNull(options));
        }
    }

    @Override
    public void itemClick(View v, OkShareOption item) {
        if (item.getListener() != null) {
            item.getListener().click();
        }

        if (item.getType() == OkShareOption.SHARE_TYPE_CUSTOM) {
            dismissAllowingStateLoss();
            return;
        }

        shareSdk.share(item.getType(), message);
        dismissAllowingStateLoss();
    }


    private static class ShareOptionAdapter extends RecyclerView.Adapter<ShareOptionAdapter.ShareOptionViewHolder> {

        private List<OkShareOption> options;
        private OkOnItemClick<OkShareOption> onItemClick;

        ShareOptionAdapter(List<OkShareOption> options, OkOnItemClick<OkShareOption> onItemClick) {
            this.options = options;
            this.onItemClick = onItemClick;
        }

        @NonNull
        @Override
        public ShareOptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ok_share_option_item, viewGroup, false);
            return new ShareOptionViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ShareOptionViewHolder holder, int position) {
            if (onItemClick != null) {
                holder.itemView.setOnClickListener(v -> onItemClick.itemClick(holder.itemView, options.get(position)));
            }

            holder.tv.setText(options.get(position).getText());
            holder.iv.setImageResource(options.get(position).getDrawable());
        }

        @Override
        public int getItemCount() {
            return options == null ? 0 : options.size();
        }

        private static class ShareOptionViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            TextView tv;

            ShareOptionViewHolder(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv_share_option_item_icon);
                tv = itemView.findViewById(R.id.tv_share_option_item_text);
            }
        }
    }

}
