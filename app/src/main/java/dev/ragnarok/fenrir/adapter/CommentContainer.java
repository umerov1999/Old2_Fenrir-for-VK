package dev.ragnarok.fenrir.adapter;

import static dev.ragnarok.fenrir.util.Objects.nonNull;
import static dev.ragnarok.fenrir.util.Utils.safeIsEmpty;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso3.Transformation;

import java.util.ArrayList;
import java.util.List;

import dev.ragnarok.fenrir.Constants;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.link.internal.LinkActionAdapter;
import dev.ragnarok.fenrir.link.internal.OwnerLinkSpanFactory;
import dev.ragnarok.fenrir.link.internal.TopicLink;
import dev.ragnarok.fenrir.model.Comment;
import dev.ragnarok.fenrir.settings.CurrentTheme;
import dev.ragnarok.fenrir.util.AppTextUtils;
import dev.ragnarok.fenrir.util.Utils;
import dev.ragnarok.fenrir.util.ViewUtils;
import dev.ragnarok.fenrir.view.emoji.EmojiconTextView;

public class CommentContainer extends LinearLayout {
    private Transformation transformation;
    private int colorTextSecondary;
    private int iconColorActive;

    public CommentContainer(Context context) {
        super(context);
        init();
    }

    public CommentContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CommentContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        transformation = CurrentTheme.createTransformationForAvatar();
        colorTextSecondary = CurrentTheme.getSecondaryTextColorCode(getContext());
        iconColorActive = CurrentTheme.getColorPrimary(getContext());
    }

    public void displayComments(List<Comment> commentsData, AttachmentsViewBinder binder, CommentsAdapter.OnCommentActionListener listener, EmojiconTextView.OnHashTagClickListener onHashTagClickListener) {
        List<Comment> comments = new ArrayList<>();
        if (!Utils.isEmpty(commentsData)) {
            for (Comment i : commentsData) {
                if (!i.isDeleted()) {
                    comments.add(i);
                }
            }
        }
        setVisibility(safeIsEmpty(comments) ? View.GONE : View.VISIBLE);
        if (safeIsEmpty(comments)) {
            return;
        }

        int i = comments.size() - getChildCount();
        for (int j = 0; j < i; j++) {
            addView(LayoutInflater.from(getContext()).inflate(R.layout.item_comment_container, this, false));
        }

        for (int g = 0; g < getChildCount(); g++) {
            ViewGroup root = (ViewGroup) getChildAt(g);
            if (g < comments.size()) {
                Comment comment = comments.get(g);
                CommentHolder check = (CommentHolder) root.getTag();
                if (check == null) {
                    check = new CommentHolder(root, onHashTagClickListener);
                    root.setTag(check);
                }
                CommentHolder holder = check;
                root.setOnLongClickListener(v -> {
                    if (listener != null) {
                        listener.populateCommentContextMenu(comment);
                    }
                    return true;
                });
                if (!comment.hasAttachments()) {
                    holder.vAttachmentsRoot.setVisibility(View.GONE);
                } else {
                    holder.vAttachmentsRoot.setVisibility(View.VISIBLE);
                    binder.displayAttachments(comment.getAttachments(), holder.attachmentContainers, true, null);
                }

                holder.tvOwnerName.setText(comment.getFullAuthorName());

                Spannable text = OwnerLinkSpanFactory.withSpans(comment.getText(), true, true, new LinkActionAdapter() {
                    @Override
                    public void onTopicLinkClicked(TopicLink link) {
                        if (listener != null) {
                            listener.onReplyToOwnerClick(link.replyToOwner, link.replyToCommentId);
                        }
                    }

                    @Override
                    public void onOwnerClick(int ownerId) {
                        if (listener != null) {
                            listener.onAvatarClick(ownerId);
                        }
                    }
                });

                if (Utils.isEmpty(text) && comment.getFromId() == 0) {
                    holder.tvText.setVisibility(View.VISIBLE);
                    holder.tvText.setText(R.string.deleted);
                } else {
                    holder.tvText.setText(text, TextView.BufferType.SPANNABLE);
                    holder.tvText.setVisibility(TextUtils.isEmpty(comment.getText()) ? View.GONE : View.VISIBLE);
                    holder.tvText.setMovementMethod(LinkMovementMethod.getInstance());
                }

                holder.ivLike.setVisibility(comment.getLikesCount() > 0 ? View.VISIBLE : View.GONE);
                Utils.setColorFilter(holder.ivLike, comment.isUserLikes() ? iconColorActive : colorTextSecondary);
                holder.tvLikeCounter.setText(AppTextUtils.getCounterWithK(comment.getLikesCount()));
                holder.tvLikeCounter.setVisibility(comment.getLikesCount() > 0 ? View.VISIBLE : View.GONE);
                holder.tvLikeCounter.setTextColor(comment.isUserLikes() ? iconColorActive : colorTextSecondary);

                holder.tvTime.setMovementMethod(LinkMovementMethod.getInstance());

                ViewUtils.displayAvatar(holder.ivOwnerAvatar, transformation, comment.getMaxAuthorAvaUrl(), Constants.PICASSO_TAG);

                holder.tvTime.setText(AppTextUtils.getDateFromUnixTime(comment.getDate()));
                holder.tvTime.setTextColor(colorTextSecondary);

                holder.ivLike.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCommentLikeClick(comment, !comment.isUserLikes());
                    }
                });

                holder.ivOwnerAvatar.setOnClickListener(v -> {
                    if (comment.getFromId() == 0) {
                        return;
                    }
                    if (listener != null) {
                        listener.onAvatarClick(comment.getFromId());
                    }
                });

                root.setVisibility(View.VISIBLE);
            } else {
                root.setVisibility(View.GONE);
            }
        }
    }

    private class CommentHolder {

        final TextView tvOwnerName;
        final ImageView ivOwnerAvatar;
        final EmojiconTextView tvText;
        final TextView tvTime;
        final ImageView ivLike;
        final TextView tvLikeCounter;
        final View vAttachmentsRoot;

        final AttachmentsHolder attachmentContainers;

        CommentHolder(View root, EmojiconTextView.OnHashTagClickListener onHashTagClickListener) {
            ivOwnerAvatar = root.findViewById(R.id.item_comment_owner_avatar);
            tvOwnerName = root.findViewById(R.id.item_comment_owner_name);
            tvText = root.findViewById(R.id.item_comment_text);

            tvText.setOnHashTagClickListener(hashTag -> {
                if (nonNull(onHashTagClickListener)) {
                    onHashTagClickListener.onHashTagClicked(hashTag);
                }
            });

            tvTime = root.findViewById(R.id.item_comment_time);
            ivLike = root.findViewById(R.id.item_comment_like);
            tvLikeCounter = root.findViewById(R.id.item_comment_like_counter);
            Utils.setColorFilter(ivLike, CurrentTheme.getSecondaryTextColorCode(getContext()));
            vAttachmentsRoot = root.findViewById(R.id.item_comment_attachments_root);

            attachmentContainers = AttachmentsHolder.forComment((ViewGroup) vAttachmentsRoot);
        }
    }
}
