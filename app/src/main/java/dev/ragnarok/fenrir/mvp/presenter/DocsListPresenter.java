package dev.ragnarok.fenrir.mvp.presenter;

import static dev.ragnarok.fenrir.Injection.provideMainThreadScheduler;
import static dev.ragnarok.fenrir.util.Objects.isNull;
import static dev.ragnarok.fenrir.util.Utils.findIndexById;
import static dev.ragnarok.fenrir.util.Utils.getCauseIfRuntime;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.ragnarok.fenrir.Injection;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.domain.IDocsInteractor;
import dev.ragnarok.fenrir.domain.InteractorFactory;
import dev.ragnarok.fenrir.model.DocFilter;
import dev.ragnarok.fenrir.model.Document;
import dev.ragnarok.fenrir.model.LocalPhoto;
import dev.ragnarok.fenrir.mvp.presenter.base.AccountDependencyPresenter;
import dev.ragnarok.fenrir.mvp.reflect.OnGuiCreated;
import dev.ragnarok.fenrir.mvp.view.IDocListView;
import dev.ragnarok.fenrir.upload.IUploadManager;
import dev.ragnarok.fenrir.upload.Upload;
import dev.ragnarok.fenrir.upload.UploadDestination;
import dev.ragnarok.fenrir.upload.UploadIntent;
import dev.ragnarok.fenrir.upload.UploadResult;
import dev.ragnarok.fenrir.upload.UploadUtils;
import dev.ragnarok.fenrir.util.AppPerms;
import dev.ragnarok.fenrir.util.DisposableHolder;
import dev.ragnarok.fenrir.util.Pair;
import dev.ragnarok.fenrir.util.RxUtils;
import dev.ragnarok.fenrir.util.Utils;


public class DocsListPresenter extends AccountDependencyPresenter<IDocListView> {

    public static final String ACTION_SELECT = "dev.ragnarok.fenrir.select.docs";
    public static final String ACTION_SHOW = "dev.ragnarok.fenrir.show.docs";
    private static final String SAVE_FILTER = "save_filter";
    private final int mOwnerId;
    private final DisposableHolder<Integer> mLoader = new DisposableHolder<>();
    private final List<Document> mDocuments;
    private final String mAction;
    private final List<DocFilter> filters;
    private final IDocsInteractor docsInteractor;
    private final IUploadManager uploadManager;
    private final UploadDestination destination;
    private final List<Upload> uploadsData;
    private final DisposableHolder<Integer> requestHolder = new DisposableHolder<>();
    private boolean requestNow;
    private boolean cacheLoadingNow;

    public DocsListPresenter(int accountId, int ownerId, @Nullable String action, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        docsInteractor = InteractorFactory.createDocsInteractor();
        uploadManager = Injection.provideUploadManager();

        mOwnerId = ownerId;

        mDocuments = new ArrayList<>();
        uploadsData = new ArrayList<>(0);
        mAction = action;

        destination = UploadDestination.forDocuments(ownerId);

        appendDisposable(uploadManager.get(getAccountId(), destination)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onUploadsDataReceived));

        appendDisposable(uploadManager.observeAdding()
                .observeOn(provideMainThreadScheduler())
                .subscribe(this::onUploadsAdded));

        appendDisposable(uploadManager.observeDeleting(true)
                .observeOn(provideMainThreadScheduler())
                .subscribe(this::onUploadDeleted));

        appendDisposable(uploadManager.observeResults()
                .filter(pair -> destination.compareTo(pair.getFirst().getDestination()))
                .observeOn(provideMainThreadScheduler())
                .subscribe(this::onUploadResults));

        appendDisposable(uploadManager.obseveStatus()
                .observeOn(provideMainThreadScheduler())
                .subscribe(this::onUploadStatusUpdate));

        appendDisposable(uploadManager.observeProgress()
                .observeOn(provideMainThreadScheduler())
                .subscribe(this::onProgressUpdates));

        int filter = isNull(savedInstanceState) ? DocFilter.Type.ALL : savedInstanceState.getInt(SAVE_FILTER);
        filters = createFilters(filter);

        loadAll();
        requestAll();
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        super.saveState(outState);
        outState.putInt(SAVE_FILTER, getSelectedFilter());
    }

    private List<DocFilter> createFilters(int selectedType) {
        List<DocFilter> data = new ArrayList<>();
        data.add(new DocFilter(DocFilter.Type.ALL, R.string.doc_filter_all));
        data.add(new DocFilter(DocFilter.Type.TEXT, R.string.doc_filter_text));
        data.add(new DocFilter(DocFilter.Type.ARCHIVE, R.string.doc_filter_archive));
        data.add(new DocFilter(DocFilter.Type.GIF, R.string.doc_filter_gif));
        data.add(new DocFilter(DocFilter.Type.IMAGE, R.string.doc_filter_image));
        data.add(new DocFilter(DocFilter.Type.AUDIO, R.string.doc_filter_audio));
        data.add(new DocFilter(DocFilter.Type.VIDEO, R.string.doc_filter_video));
        data.add(new DocFilter(DocFilter.Type.BOOKS, R.string.doc_filter_books));
        data.add(new DocFilter(DocFilter.Type.OTHER, R.string.doc_filter_other));

        for (DocFilter filter : data) {
            filter.setActive(selectedType == filter.getType());
        }

        return data;
    }

    private void onUploadsDataReceived(List<Upload> data) {
        uploadsData.clear();
        uploadsData.addAll(data);

        callView(IDocListView::notifyDataSetChanged);
        resolveUploadDataVisiblity();
    }

    private void onUploadResults(Pair<Upload, UploadResult<?>> pair) {
        mDocuments.add(0, (Document) pair.getSecond().getResult());
        callView(IDocListView::notifyDataSetChanged);
    }

    private void onProgressUpdates(List<IUploadManager.IProgressUpdate> updates) {
        for (IUploadManager.IProgressUpdate update : updates) {
            int index = findIndexById(uploadsData, update.getId());
            if (index != -1) {
                callView(view -> view.notifyUploadProgressChanged(index, update.getProgress(), true));
            }
        }
    }

    private void onUploadStatusUpdate(Upload upload) {
        int index = findIndexById(uploadsData, upload.getId());
        if (index != -1) {
            callView(view -> view.notifyUploadItemChanged(index));
        }
    }

    private void onUploadsAdded(List<Upload> added) {
        for (Upload u : added) {
            if (destination.compareTo(u.getDestination())) {
                int index = uploadsData.size();
                uploadsData.add(u);
                callView(view -> view.notifyUploadItemsAdded(index, 1));
            }
        }

        resolveUploadDataVisiblity();
    }

    private void onUploadDeleted(int[] ids) {
        for (int id : ids) {
            int index = findIndexById(uploadsData, id);
            if (index != -1) {
                uploadsData.remove(index);
                callView(view -> view.notifyUploadItemRemoved(index));
            }
        }

        resolveUploadDataVisiblity();
    }

    @OnGuiCreated
    private void resolveUploadDataVisiblity() {
        callView(v -> v.setUploadDataVisible(!uploadsData.isEmpty()));
    }

    private void setCacheLoadingNow(boolean cacheLoadingNow) {
        this.cacheLoadingNow = cacheLoadingNow;
        resolveRefreshingView();
    }

    private void setRequestNow(boolean requestNow) {
        this.requestNow = requestNow;
        resolveRefreshingView();
    }

    private int getSelectedFilter() {
        for (DocFilter filter : filters) {
            if (filter.isActive()) {
                return filter.getType();
            }
        }

        return DocFilter.Type.ALL;
    }

    private void requestAll() {
        setRequestNow(true);

        int filter = getSelectedFilter();
        int accountId = getAccountId();

        requestHolder.append(docsInteractor.request(accountId, mOwnerId, filter)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onNetDataReceived, throwable -> onRequestError(getCauseIfRuntime(throwable))));
    }

    private void onRequestError(Throwable throwable) {
        setRequestNow(false);
        callView(v -> showError(v, throwable));
    }

    private void onCacheDataReceived(List<Document> data) {
        setCacheLoadingNow(false);

        mDocuments.clear();
        mDocuments.addAll(data);

        safelyNotifyDataSetChanged();
    }

    private void onNetDataReceived(List<Document> data) {
        // cancel db loading if active
        mLoader.dispose();

        cacheLoadingNow = false;
        requestNow = false;

        resolveRefreshingView();

        mDocuments.clear();
        mDocuments.addAll(data);

        safelyNotifyDataSetChanged();
    }

    @Override
    public void onGuiCreated(@NonNull IDocListView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.displayUploads(uploadsData);
        viewHost.displayFilterData(filters);
    }

    private void loadAll() {
        setCacheLoadingNow(true);

        int accountId = getAccountId();
        int filter = getSelectedFilter();

        mLoader.append(docsInteractor.getCacheData(accountId, mOwnerId, filter)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onCacheDataReceived, throwable -> onLoadError(getCauseIfRuntime(throwable))));
    }

    @OnGuiCreated
    private void resolveRefreshingView() {
        callView(v -> v.showRefreshing(isNowLoading()));
    }

    private boolean isNowLoading() {
        return cacheLoadingNow || requestNow;
    }

    private void safelyNotifyDataSetChanged() {
        resolveDocsListData();
    }

    @OnGuiCreated
    private void resolveDocsListData() {
        callView(v -> v.displayData(mDocuments, isImagesOnly()));
    }

    private boolean isImagesOnly() {
        return Utils.intValueIn(getSelectedFilter(), DocFilter.Type.IMAGE, DocFilter.Type.GIF);
    }

    private void onLoadError(Throwable throwable) {
        throwable.printStackTrace();
        setCacheLoadingNow(false);

        callView(v -> showError(v, throwable));

        resolveRefreshingView();
    }

    @Override
    public void onDestroyed() {
        mLoader.dispose();
        requestHolder.dispose();
        super.onDestroyed();
    }

    public void fireRefresh() {
        mLoader.dispose();
        cacheLoadingNow = false;

        requestAll();
    }

    public void fireButtonAddClick() {
        if (AppPerms.hasReadStoragePermission(getApplicationContext())) {
            callView(v -> v.startSelectUploadFileActivity(getAccountId()));
        } else {
            callView(IDocListView::requestReadExternalStoragePermission);
        }
    }

    public void fireDocClick(@NonNull Document doc) {
        if (ACTION_SELECT.equals(mAction)) {
            ArrayList<Document> selected = new ArrayList<>(1);
            selected.add(doc);

            callView(v -> v.returnSelection(selected));
        } else {
            if (doc.isGif() && doc.hasValidGifVideoLink()) {
                ArrayList<Document> gifs = new ArrayList<>();
                int selectedIndex = 0;
                for (int i = 0; i < mDocuments.size(); i++) {
                    Document d = mDocuments.get(i);

                    if (d.isGif() && d.hasValidGifVideoLink()) {
                        gifs.add(d);
                    }

                    if (d == doc) {
                        selectedIndex = gifs.size() - 1;
                    }
                }

                int finalSelectedIndex = selectedIndex;
                callView(v -> v.goToGifPlayer(getAccountId(), gifs, finalSelectedIndex));
            } else {
                callView(v -> v.openDocument(getAccountId(), doc));
            }
        }
    }

    public void fireReadPermissionResolved() {
        if (AppPerms.hasReadStoragePermission(getApplicationContext())) {
            callView(v -> v.startSelectUploadFileActivity(getAccountId()));
        }
    }

    public void fireFileForUploadSelected(String file) {
        UploadIntent intent = new UploadIntent(getAccountId(), destination)
                .setAutoCommit(true)
                .setFileUri(Uri.parse(file));

        uploadManager.enqueue(Collections.singletonList(intent));
    }

    public void fireRemoveClick(Upload upload) {
        uploadManager.cancel(upload.getId());
    }

    public void fireFilterClick(DocFilter entry) {
        for (DocFilter filter : filters) {
            filter.setActive(entry.getType() == filter.getType());
        }

        callView(IDocListView::notifyFiltersChanged);

        loadAll();
        requestAll();
    }

    public void pleaseNotifyViewAboutAdapterType() {
        callView(v -> v.setAdapterType(isImagesOnly()));
    }

    public void fireLocalPhotosForUploadSelected(ArrayList<LocalPhoto> photos) {
        List<UploadIntent> intents = UploadUtils.createIntents(getAccountId(), destination, photos, Upload.IMAGE_SIZE_FULL, true);
        uploadManager.enqueue(intents);
    }
}