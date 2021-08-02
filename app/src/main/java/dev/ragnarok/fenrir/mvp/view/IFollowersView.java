package dev.ragnarok.fenrir.mvp.view;

import java.util.List;

import dev.ragnarok.fenrir.model.Owner;


public interface IFollowersView extends ISimpleOwnersView {
    void showNotFollowers(List<Owner> data, int accountId);

    void showAddFollowers(List<Owner> add, List<Owner> remove, int accountId);
}
