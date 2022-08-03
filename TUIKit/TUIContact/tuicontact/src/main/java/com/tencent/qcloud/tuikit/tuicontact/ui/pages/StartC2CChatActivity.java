package com.tencent.qcloud.tuikit.tuicontact.ui.pages;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;

import com.tencent.qcloud.tuicore.component.TitleBarLayout;
import com.tencent.qcloud.tuicore.component.activities.BaseLightActivity;
import com.tencent.qcloud.tuicore.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.util.ContactUtils;
import com.tencent.qcloud.tuikit.tuicontact.ui.view.ContactListView;

import java.util.ArrayList;
import java.util.List;

public class StartC2CChatActivity extends BaseLightActivity {

    private static final String TAG = StartC2CChatActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private ContactListView mContactListView;
    private ContactItemBean mSelectedItem;
    private List<ContactItemBean> mContacts = new ArrayList<>();

    private ContactPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_start_c2c_chat_activity);

        mTitleBar = findViewById(R.id.start_c2c_chat_title);
        mTitleBar.setTitle(getResources().getString(com.tencent.qcloud.tuicore.R.string.sure), ITitleBarLayout.Position.RIGHT);
        mTitleBar.getRightIcon().setVisibility(View.GONE);
        mTitleBar.setOnRightClickListener(v -> startConversation());
        mTitleBar.setOnLeftClickListener(v -> finish());

        mContactListView = findViewById(R.id.contact_list_view);
        mContactListView.setSingleSelectMode(true);

        presenter = new ContactPresenter();
        presenter.setFriendListListener();
        mContactListView.setPresenter(presenter);
        presenter.setContactListView(mContactListView);

        mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);
        mContactListView.setOnSelectChangeListener((contact, selected) -> {
            if (selected) {
                if (mSelectedItem == contact) {
                    // 相同的Item，忽略
                } else {
                    if (mSelectedItem != null) {
                        mSelectedItem.setSelected(false);
                    }
                    mSelectedItem = contact;
                }
            } else {
                if (mSelectedItem == contact) {
                    mSelectedItem.setSelected(false);
                }
            }
        });
    }

    public void startConversation() {
        if (mSelectedItem == null || !mSelectedItem.isSelected()) {
            ToastUtil.toastLongMessage(getString(R.string.select_chat));
            return;
        }
        String chatName = mSelectedItem.getId();
        if (!TextUtils.isEmpty(mSelectedItem.getRemark())) {
            chatName = mSelectedItem.getRemark();
        } else if (!TextUtils.isEmpty(mSelectedItem.getNickName())) {
            chatName = mSelectedItem.getNickName();
        }

        ContactUtils.startChatActivity(mSelectedItem.getId(), ContactItemBean.TYPE_C2C, chatName, "");
        finish();
    }
}
