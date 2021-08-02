package dev.ragnarok.fenrir.fragment.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import dev.ragnarok.fenrir.Extra;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.adapter.OwnersAdapter;
import dev.ragnarok.fenrir.fragment.AbsOwnersListFragment;
import dev.ragnarok.fenrir.model.Owner;
import dev.ragnarok.fenrir.mvp.core.IPresenterFactory;
import dev.ragnarok.fenrir.mvp.presenter.FollowersPresenter;
import dev.ragnarok.fenrir.mvp.view.IFollowersView;
import dev.ragnarok.fenrir.place.PlaceFactory;
import dev.ragnarok.fenrir.util.Utils;

public class FollowersFragment extends AbsOwnersListFragment<FollowersPresenter, IFollowersView>
        implements IFollowersView {
    public static FollowersFragment newInstance(int accountId, int userId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.USER_ID, userId);
        FollowersFragment followersFragment = new FollowersFragment();
        followersFragment.setArguments(args);
        return followersFragment;
    }

    @NonNull
    @Override
    public IPresenterFactory<FollowersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new FollowersPresenter(getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.USER_ID),
                saveInstanceState);
    }

    @Override
    public void showNotFollowers(List<Owner> data, int accountId) {
        OwnersAdapter adapter = new OwnersAdapter(requireActivity(), data);
        adapter.setClickListener(owner -> PlaceFactory.getOwnerWallPlace(accountId, owner.getOwnerId(), null).tryOpenWith(requireContext()));
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(requireActivity().getString(R.string.not_follower))
                .setView(Utils.createAlertRecycleFrame(requireActivity(), adapter, null))
                .setPositiveButton("OK", null)
                .setCancelable(true)
                .show();
    }

    @Override
    public void showAddFollowers(List<Owner> add, List<Owner> remove, int accountId) {
        if (add.size() <= 0 && remove.size() > 0) {
            showNotFollowers(remove, accountId);
            return;
        }
        OwnersAdapter adapter = new OwnersAdapter(requireActivity(), add);
        adapter.setClickListener(owner -> PlaceFactory.getOwnerWallPlace(accountId, owner.getOwnerId(), null).tryOpenWith(requireContext()));
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(requireActivity().getString(R.string.new_follower))
                .setView(Utils.createAlertRecycleFrame(requireActivity(), adapter, null))
                .setPositiveButton("OK", (dialog, which) -> {
                    if (remove.size() > 0) {
                        showNotFollowers(remove, accountId);
                    }
                })
                .setCancelable(remove.size() <= 0)
                .show();
    }

    @Override
    protected boolean hasToolbar() {
        return false;
    }

    @Override
    protected boolean needShowCount() {
        return true;
    }
}