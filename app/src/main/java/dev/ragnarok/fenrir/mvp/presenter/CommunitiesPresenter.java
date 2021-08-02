package dev.ragnarok.fenrir.mvp.presenter;

import static dev.ragnarok.fenrir.util.Utils.getCauseIfRuntime;
import static dev.ragnarok.fenrir.util.Utils.nonEmpty;
import static dev.ragnarok.fenrir.util.Utils.trimmedIsEmpty;
import static dev.ragnarok.fenrir.util.Utils.trimmedNonEmpty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.ragnarok.fenrir.domain.ICommunitiesInteractor;
import dev.ragnarok.fenrir.domain.InteractorFactory;
import dev.ragnarok.fenrir.model.Community;
import dev.ragnarok.fenrir.model.DataWrapper;
import dev.ragnarok.fenrir.mvp.presenter.base.AccountDependencyPresenter;
import dev.ragnarok.fenrir.mvp.view.ICommunitiesView;
import dev.ragnarok.fenrir.util.Objects;
import dev.ragnarok.fenrir.util.RxUtils;
import dev.ragnarok.fenrir.util.Translit;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class CommunitiesPresenter extends AccountDependencyPresenter<ICommunitiesView> {

    private final int userId;

    private final DataWrapper<Community> own;
    private final DataWrapper<Community> filtered;
    private final DataWrapper<Community> search;
    private final ICommunitiesInteractor communitiesInteractor;
    private final CompositeDisposable actualDisposable = new CompositeDisposable();
    private final CompositeDisposable cacheDisposable = new CompositeDisposable();
    private final CompositeDisposable netSeacrhDisposable = new CompositeDisposable();
    private final CompositeDisposable filterDisposable = new CompositeDisposable();
    private boolean actualEndOfContent;
    private boolean netSearchEndOfContent;
    private boolean actualLoadingNow;
    //private int actualLoadingOffset;
    private boolean cacheLoadingNow;
    private boolean netSeacrhNow;
    private String filter;

    public CommunitiesPresenter(int accountId, int userId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        this.userId = userId;
        communitiesInteractor = InteractorFactory.createCommunitiesInteractor();

        own = new DataWrapper<>(new ArrayList<>(), true);
        filtered = new DataWrapper<>(new ArrayList<>(0), false);
        search = new DataWrapper<>(new ArrayList<>(0), false);

        loadCachedData();
        requestActualData(0);
    }

    private static Single<List<Community>> filter(List<Community> orig, String filter) {
        return Single.create(emitter -> {
            List<Community> result = new ArrayList<>(5);

            for (Community community : orig) {
                if (emitter.isDisposed()) {
                    break;
                }

                if (isMatchFilter(community, filter)) {
                    result.add(community);
                }
            }

            emitter.onSuccess(result);
        });
    }

    private static boolean isMatchFilter(Community community, String filter) {
        if (trimmedIsEmpty(filter)) {
            return true;
        }

        String lower = filter.toLowerCase().trim();

        if (nonEmpty(community.getName())) {
            String lowername = community.getName().toLowerCase();
            if (lowername.contains(lower)) {
                return true;
            }

            try {
                if (lowername.contains(Translit.cyr2lat(lower))) {
                    return true;
                }
            } catch (Exception ignored) {
            }


            try {
                //Caused by java.lang.StringIndexOutOfBoundsException: length=3; index=3
                if (lowername.contains(Translit.lat2cyr(lower))) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        return nonEmpty(community.getScreenName()) && community.getScreenName().toLowerCase().contains(lower);
    }

    private static boolean exist(DataWrapper<Community> data, Community in) {
        if (data == null || in == null) {
            return false;
        }

        for (int i = 0; i < data.size(); i++) {
            if (data.get().get(i).getOwnerId() == in.getOwnerId()) {
                return true;
            }
        }

        return false;
    }

    private void requestActualData(int offset) {
        actualLoadingNow = true;
        //this.actualLoadingOffset = offset;

        int accountId = getAccountId();

        resolveRefreshing();
        actualDisposable.add(communitiesInteractor.getActual(accountId, userId, 1000, offset)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(communities -> onActualDataReceived(offset, communities), this::onActualDataGetError));
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshing();
    }
    //private int netSearchOffset;

    private void resolveRefreshing() {
        callResumedView(v -> v.displayRefreshing(actualLoadingNow || netSeacrhNow));
    }

    private void onActualDataGetError(Throwable t) {
        actualLoadingNow = false;

        resolveRefreshing();
        callView(v -> showError(v, t));
    }

    @Override
    public void onGuiCreated(@NonNull ICommunitiesView view) {
        super.onGuiCreated(view);
        view.displayData(own, filtered, search);
    }

    private void onActualDataReceived(int offset, List<Community> communities) {
        //reset cache loading
        cacheDisposable.clear();
        cacheLoadingNow = false;

        actualLoadingNow = false;
        actualEndOfContent = communities.isEmpty();

        if (offset == 0) {
            own.get().clear();
            own.get().addAll(communities);
            callView(ICommunitiesView::notifyDataSetChanged);
        } else {
            int startOwnSize = own.size();
            own.get().addAll(communities);
            callView(view -> view.notifyOwnDataAdded(startOwnSize, communities.size()));
        }

        resolveRefreshing();
    }

    private void loadCachedData() {
        cacheLoadingNow = true;

        int accountId = getAccountId();
        cacheDisposable.add(communitiesInteractor.getCachedData(accountId, userId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onCachedDataReceived));
    }

    private boolean isSearchNow() {
        return trimmedNonEmpty(filter);
    }

    private void onCachedDataReceived(List<Community> communities) {
        cacheLoadingNow = false;

        own.get().clear();
        own.get().addAll(communities);
        callView(ICommunitiesView::notifyDataSetChanged);
    }

    public void fireSearchQueryChanged(String query) {
        if (!Objects.safeEquals(filter, query)) {
            filter = query;
            onFilterChanged();
        }
    }

    private void onFilterChanged() {
        boolean searchNow = trimmedNonEmpty(filter);

        own.setEnabled(!searchNow);

        filtered.setEnabled(searchNow);
        filtered.clear();

        search.setEnabled(searchNow);
        search.clear();

        callView(ICommunitiesView::notifyDataSetChanged);

        filterDisposable.clear();
        netSeacrhDisposable.clear();
        //netSearchOffset = 0;
        netSeacrhNow = false;

        if (searchNow) {
            filterDisposable.add(filter(own.get(), filter)
                    .compose(RxUtils.applySingleComputationToMainSchedulers())
                    .subscribe(this::onFilteredDataReceived, RxUtils.ignore()));

            startNetSearch(0, true);
        } else {
            resolveRefreshing();
        }
    }

    private void startNetSearch(int offset, boolean withDelay) {
        int accountId = getAccountId();
        String filter = this.filter;

        Single<List<Community>> single;
        Single<List<Community>> searchSingle = communitiesInteractor.search(accountId, filter, null,
                null, null, null, 0, 100, offset);

        if (withDelay) {
            single = Completable.complete()
                    .delay(1, TimeUnit.SECONDS)
                    .andThen(searchSingle);
        } else {
            single = searchSingle;
        }

        netSeacrhNow = true;
        //this.netSearchOffset = offset;

        resolveRefreshing();
        netSeacrhDisposable.add(single
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(data -> onSearchDataReceived(offset, data), this::onSeacrhError));
    }

    private void onSeacrhError(Throwable t) {
        netSeacrhNow = false;
        resolveRefreshing();
        callView(v -> showError(v, getCauseIfRuntime(t)));
    }

    private void onSearchDataReceived(int offset, List<Community> communities) {
        netSeacrhNow = false;
        netSearchEndOfContent = communities.isEmpty();

        resolveRefreshing();

        if (offset == 0) {
            search.replace(communities);
            callView(ICommunitiesView::notifyDataSetChanged);
        } else {
            int sizeBefore = search.size();
            int count = communities.size();

            search.addAll(communities);
            callView(view -> view.notifySeacrhDataAdded(sizeBefore, count));
        }
    }

    private void onFilteredDataReceived(List<Community> filteredData) {
        filtered.replace(filteredData);
        callView(ICommunitiesView::notifyDataSetChanged);
    }

    public void fireCommunityClick(Community community) {
        callView(v -> v.showCommunityWall(getAccountId(), community));
    }

    public void fireUnsubscribe(Community community) {
        actualDisposable.add(communitiesInteractor.leave(getAccountId(), community.getId())
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(this::fireRefresh, this::onSeacrhError));
    }

    public boolean fireCommunityLongClick(Community community) {
        if ((exist(own, community) || exist(filtered, community)) && userId == getAccountId()) {
            callView(v -> v.showCommunityMenu(community));
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyed() {
        actualDisposable.dispose();
        cacheDisposable.dispose();
        filterDisposable.dispose();
        netSeacrhDisposable.dispose();
        super.onDestroyed();
    }

    public void fireRefresh() {
        if (isSearchNow()) {
            netSeacrhDisposable.clear();
            netSeacrhNow = false;

            startNetSearch(0, false);
        } else {
            cacheDisposable.clear();
            cacheLoadingNow = false;

            actualDisposable.clear();
            actualLoadingNow = false;
            //actualLoadingOffset = 0;

            requestActualData(0);
        }
    }

    public void fireScrollToEnd() {
        if (isSearchNow()) {
            if (!netSeacrhNow && !netSearchEndOfContent) {
                int offset = search.size();
                startNetSearch(offset, false);
            }
        } else {
            if (!actualLoadingNow && !cacheLoadingNow && !actualEndOfContent) {
                int offset = own.size();
                requestActualData(offset);
            }
        }
    }
}
