package dev.ragnarok.fenrir.mvp.presenter;

import static dev.ragnarok.fenrir.util.Utils.getCauseIfRuntime;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.ragnarok.fenrir.domain.IRelationshipInteractor;
import dev.ragnarok.fenrir.domain.InteractorFactory;
import dev.ragnarok.fenrir.model.Owner;
import dev.ragnarok.fenrir.model.User;
import dev.ragnarok.fenrir.mvp.view.IFollowersView;
import dev.ragnarok.fenrir.settings.Settings;
import dev.ragnarok.fenrir.util.RxUtils;
import dev.ragnarok.fenrir.util.Utils;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class FollowersPresenter extends SimpleOwnersPresenter<IFollowersView> {

    private final int userId;
    private final IRelationshipInteractor relationshipInteractor;
    private final CompositeDisposable actualDataDisposable = new CompositeDisposable();
    private final CompositeDisposable cacheDisposable = new CompositeDisposable();
    private boolean actualDataLoading;
    private boolean actualDataReceived;
    private boolean endOfContent;
    private boolean cacheLoadingNow;
    private boolean doLoadTabs;

    public FollowersPresenter(int accountId, int userId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        this.userId = userId;
        relationshipInteractor = InteractorFactory.createRelationshipInteractor();
    }

    private void requestActualData(int offset, boolean do_scan) {
        actualDataLoading = true;
        resolveRefreshingView();

        int accountId = getAccountId();
        actualDataDisposable.add(relationshipInteractor.getFollowers(accountId, userId, Settings.get().other().isNot_friend_show() ? 1000 : 200, offset)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(users -> onActualDataReceived(offset, users, do_scan), this::onActualDataGetError));
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshingView();
        if (doLoadTabs) {
            return;
        } else {
            doLoadTabs = true;
        }
        loadAllCacheData();
        if (!Settings.get().other().isNot_friend_show()) {
            requestActualData(0, false);
        }
    }

    private void resolveRefreshingView() {
        callView(v -> v.displayRefreshing(actualDataLoading));
    }

    private void onActualDataGetError(Throwable t) {
        actualDataLoading = false;
        callView(v -> showError(v, getCauseIfRuntime(t)));

        resolveRefreshingView();
    }

    private void onActualDataReceived(int offset, List<User> users, boolean do_scan) {
        if (do_scan && Settings.get().other().isNot_friend_show()) {
            List<Owner> not_friends = new ArrayList<>();
            for (Owner i : data) {
                if (Utils.indexOf(users, i.getOwnerId()) == -1) {
                    not_friends.add(i);
                }
            }
            if (userId == getAccountId()) {
                if (not_friends.size() > 0) {
                    callView(view -> view.showNotFollowers(not_friends, getAccountId()));
                }
            } else {
                List<Owner> add_friends = new ArrayList<>();
                for (User i : users) {
                    if (Utils.indexOfOwner(data, i.getId()) == -1) {
                        add_friends.add(i);
                    }
                }
                if (add_friends.size() > 0 || not_friends.size() > 0) {
                    callView(view -> view.showAddFollowers(add_friends, not_friends, getAccountId()));
                }
            }
        }
        actualDataLoading = false;
        cacheDisposable.clear();

        actualDataReceived = true;
        endOfContent = users.isEmpty();

        if (offset == 0) {
            data.clear();
            data.addAll(users);
            callView(IFollowersView::notifyDataSetChanged);
        } else {
            int startSzie = data.size();
            data.addAll(users);
            callView(view -> view.notifyDataAdded(startSzie, users.size()));
        }

        resolveRefreshingView();
    }

    @Override
    void onUserScrolledToEnd() {
        if (!endOfContent && !cacheLoadingNow && !actualDataLoading && actualDataReceived) {
            requestActualData(data.size(), false);
        }
    }

    @Override
    void onUserRefreshed() {
        cacheDisposable.clear();
        cacheLoadingNow = false;

        actualDataDisposable.clear();
        requestActualData(0, false);
    }

    private void loadAllCacheData() {
        cacheLoadingNow = true;

        int accountId = getAccountId();
        cacheDisposable.add(relationshipInteractor.getCachedFollowers(accountId, userId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onCachedDataReceived, this::onCacheDataGetError));
    }

    private void onCacheDataGetError(Throwable t) {
        cacheLoadingNow = false;
        callView(v -> showError(v, getCauseIfRuntime(t)));
    }

    private void onCachedDataReceived(List<User> users) {
        cacheLoadingNow = false;

        data.addAll(users);
        callView(IFollowersView::notifyDataSetChanged);
        if (Settings.get().other().isNot_friend_show()) {
            requestActualData(0, users.size() > 0);
        }
    }

    @Override
    public void onDestroyed() {
        cacheDisposable.dispose();
        actualDataDisposable.dispose();
        super.onDestroyed();
    }
}