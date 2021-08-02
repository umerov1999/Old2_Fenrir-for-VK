package dev.ragnarok.fenrir.adapter;

import static dev.ragnarok.fenrir.util.Objects.isNull;
import static dev.ragnarok.fenrir.util.Objects.nonNull;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso3.Transformation;

import java.util.List;

import dev.ragnarok.fenrir.Constants;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.adapter.base.RecyclerBindableAdapter;
import dev.ragnarok.fenrir.link.internal.LinkActionAdapter;
import dev.ragnarok.fenrir.link.internal.OwnerLinkSpanFactory;
import dev.ragnarok.fenrir.link.internal.TopicLink;
import dev.ragnarok.fenrir.model.Comment;
import dev.ragnarok.fenrir.place.PlaceFactory;
import dev.ragnarok.fenrir.settings.CurrentTheme;
import dev.ragnarok.fenrir.settings.Settings;
import dev.ragnarok.fenrir.util.AppTextUtils;
import dev.ragnarok.fenrir.util.Utils;
import dev.ragnarok.fenrir.util.ViewUtils;
import dev.ragnarok.fenrir.view.WeakViewAnimatorAdapter;
import dev.ragnarok.fenrir.view.emoji.EmojiconTextView;

public class CommentsAdapter extends RecyclerBindableAdapter<Comment, RecyclerView.ViewHolder> {

    private static final int TYPE_DELETED = 0;
    private static final int TYPE_NORMAL = 1;
    private final Context context;
    private final AttachmentsViewBinder attachmentsViewBinder;
    private final Transformation transformation;
    private final int colorTextSecondary;
    private final int iconColorActive;
    private EmojiconTextView.OnHashTagClickListener onHashTagClickListener;
    private OnCommentActionListener listener;
    private RecyclerView recyclerView;

    public CommentsAdapter(Context context, List<Comment> items, AttachmentsViewBinder.OnAttachmentsActionCallback attachmentsActionCallback) {
        super(items);
        this.context = context;
        attachmentsViewBinder = new AttachmentsViewBinder(context, attachmentsActionCallback);
        transformation = CurrentTheme.createTransformationForAvatar();
        colorTextSecondary = CurrentTheme.getSecondaryTextColorCode(context);
        iconColorActive = CurrentTheme.getColorPrimary(context);
    }

    public void setOnHashTagClickListener(EmojiconTextView.OnHashTagClickListener onHashTagClickListener) {
        this.onHashTagClickListener = onHashTagClickListener;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position, int type) {
        switch (type) {
            case TYPE_NORMAL:
                bindNormalHolder((NormalCommentHolder) viewHolder, getItem(position));
                break;
            case TYPE_DELETED:
                bindDeletedComment((DeletedHolder) viewHolder, getItem(position));
                break;
        }
    }

    private void bindDeletedComment(DeletedHolder holder, Comment comment) {
        holder.buttonRestore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestoreComment(comment.getId());
            }
        });
    }

    private void bindNormalHolder(NormalCommentHolder holder, Comment comment) {
        holder.cancelSelectionAnimation();

        if (comment.isAnimationNow()) {
            holder.startSelectionAnimation();
            comment.setAnimationNow(false);
        }

        if (!comment.hasAttachments()) {
            holder.vAttachmentsRoot.setVisibility(View.GONE);
        } else {
            holder.vAttachmentsRoot.setVisibility(View.VISIBLE);
            attachmentsViewBinder.displayAttachments(comment.getAttachments(), holder.attachmentContainers, true, null);
        }

        holder.tvOwnerName.setText(comment.getFullAuthorName());

        Spannable text = OwnerLinkSpanFactory.withSpans(comment.getText(), true, true, new LinkActionAdapter() {
            @Override
            public void onTopicLinkClicked(TopicLink link) {
                onReplyClick(link.replyToOwner, link.replyToCommentId);
            }

            @Override
            public void onOwnerClick(int ownerId) {
                if (listener != null) {
                    listener.onAvatarClick(ownerId);
                }
            }
        });

        holder.item_comment_thread_counter.setVisibility(comment.getThreads() > 0 ? View.VISIBLE : View.GONE);
        if (comment.getThreads() > 0) {
            holder.itemView.setOnClickListener(v -> PlaceFactory.getCommentsThreadPlace(Settings.get().accounts().getCurrent(), comment.getCommented(), null, comment.getId()).tryOpenWith(context));
            holder.item_comment_thread_counter.setText(String.valueOf(comment.getThreads()));
        }
        holder.tvText.setText(text, TextView.BufferType.SPANNABLE);
        holder.tvText.setVisibility(TextUtils.isEmpty(comment.getText()) ? View.GONE : View.VISIBLE);
        holder.tvText.setMovementMethod(LinkMovementMethod.getInstance());

        holder.ivLike.setVisibility(comment.getLikesCount() > 0 ? View.VISIBLE : View.GONE);
        Utils.setColorFilter(holder.ivLike, comment.isUserLikes() ? iconColorActive : colorTextSecondary);
        holder.tvLikeCounter.setText(String.valueOf(comment.getLikesCount()));
        holder.tvLikeCounter.setVisibility(comment.getLikesCount() > 0 ? View.VISIBLE : View.GONE);
        holder.tvLikeCounter.setTextColor(comment.isUserLikes() ? iconColorActive : colorTextSecondary);

        holder.tvTime.setMovementMethod(LinkMovementMethod.getInstance());

        ViewUtils.displayAvatar(holder.ivOwnerAvatar, transformation, comment.getMaxAuthorAvaUrl(), Constants.PICASSO_TAG);

        holder.tvTime.setText(genTimeAndReplyText(comment), TextView.BufferType.SPANNABLE);
        holder.tvTime.setTextColor(colorTextSecondary);

        holder.ivLike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentLikeClick(comment, !comment.isUserLikes());
            }
        });

        holder.ivOwnerAvatar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAvatarClick(comment.getFromId());
            }
        });
    }

    private Spannable genTimeAndReplyText(Comment comment) {
        String time = AppTextUtils.getDateFromUnixTime(comment.getDate());
        if (comment.getReplyToUser() == 0) {
            return Spannable.Factory.getInstance().newSpannable(time);
        }

        String commentText = context.getString(R.string.comment).toLowerCase();
        String target = context.getString(R.string.in_response_to, time, commentText);

        int start = target.indexOf(commentText);

        Spannable spannable = Spannable.Factory.getInstance().newSpannable(target);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onReplyClick(comment.getReplyToUser(), comment.getReplyToComment());
            }
        };

        spannable.setSpan(span, start, target.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void onReplyClick(int ownerId, int commentId) {
        if (listener != null) {
            listener.onReplyToOwnerClick(ownerId, commentId);
        }
    }

    @Override
    protected RecyclerView.ViewHolder viewHolder(View view, int type) {
        switch (type) {
            case TYPE_NORMAL:
                return new NormalCommentHolder(view);
            case TYPE_DELETED:
                return new DeletedHolder(view);
            default:
                return null;
        }
    }

    @Override
    protected int layoutId(int type) {
        switch (type) {
            case TYPE_DELETED:
                return R.layout.item_comment_deleted;
            case TYPE_NORMAL:
                return R.layout.item_comment;
        }

        throw new IllegalArgumentException();
    }

    public void setListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    protected int getItemType(int position) {
        return getItem(position - getHeadersCount()).isDeleted() ? TYPE_DELETED : TYPE_NORMAL;
    }

    public interface OnCommentActionListener {
        void onReplyToOwnerClick(int ownerId, int commentId);

        void onRestoreComment(int commentId);

        void onAvatarClick(int ownerId);

        void onCommentLikeClick(Comment comment, boolean add);

        void populateCommentContextMenu(ContextMenu menu, Comment comment);
    }

    private static class DeletedHolder extends RecyclerView.ViewHolder {

        final MaterialButton buttonRestore;

        DeletedHolder(View itemView) {
            super(itemView);
            buttonRestore = itemView.findViewById(R.id.item_comment_deleted_restore);
        }
    }

    private class NormalCommentHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        final TextView tvOwnerName;
        final ImageView ivOwnerAvatar;
        final EmojiconTextView tvText;
        final TextView tvTime;
        final ImageView ivLike;
        final TextView tvLikeCounter;
        final View selectionView;
        final View vAttachmentsRoot;
        final TextView item_comment_thread_counter;

        final AttachmentsHolder attachmentContainers;
        final Animator.AnimatorListener animationAdapter;
        ObjectAnimator animator;

        NormalCommentHolder(View root) {
            super(root);
            ivOwnerAvatar = root.findViewById(R.id.item_comment_owner_avatar);
            tvOwnerName = root.findViewById(R.id.item_comment_owner_name);
            tvText = root.findViewById(R.id.item_comment_text);

            tvText.setOnHashTagClickListener(hashTag -> {
                if (nonNull(onHashTagClickListener)) {
                    onHashTagClickListener.onHashTagClicked(hashTag);
                }
            });

            item_comment_thread_counter = root.findViewById(R.id.item_comment_thread_counter);
            tvTime = root.findViewById(R.id.item_comment_time);
            ivLike = root.findViewById(R.id.item_comment_like);
            tvLikeCounter = root.findViewById(R.id.item_comment_like_counter);
            selectionView = root.findViewById(R.id.item_comment_selection);
            selectionView.setBackgroundColor(CurrentTheme.getColorPrimary(context));
            Utils.setColorFilter(ivLike, CurrentTheme.getSecondaryTextColorCode(context));
            vAttachmentsRoot = root.findViewById(R.id.item_comment_attachments_root);

            itemView.setOnCreateContextMenuListener(this);

            attachmentContainers = AttachmentsHolder.forComment((ViewGroup) vAttachmentsRoot);
            animationAdapter = new WeakViewAnimatorAdapter<View>(selectionView) {
                @Override
                public void onAnimationEnd(View view) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationStart(View view) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onAnimationCancel(View view) {
                    view.setVisibility(View.INVISIBLE);
                }
            };
        }

        void startSelectionAnimation() {
            selectionView.setAlpha(0.5f);

            animator = ObjectAnimator.ofFloat(selectionView, View.ALPHA, 0.0f);
            animator.setDuration(1500);
            animator.addListener(animationAdapter);
            animator.start();
        }

        void cancelSelectionAnimation() {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }

            selectionView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (isNull(recyclerView)) return;

            int position = recyclerView.getChildAdapterPosition(v) - getHeadersCount();
            if (listener != null) {
                listener.populateCommentContextMenu(menu, getItem(position));
            }
        }
    }
}
