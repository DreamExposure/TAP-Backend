package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.enums.post.PostType;
import org.dreamexposure.tap.core.objects.post.*;
import org.dreamexposure.tap.core.utils.Logger;
import org.dreamexposure.tap.core.utils.PostUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/21/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings({"UnusedReturnValue", "SqlNoDataSourceInspection", "Duplicates"})
public class PostDataHandler {
    private static PostDataHandler instance;

    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

    private String tableName;

    private PostDataHandler() {
    }

    public static PostDataHandler get() {
        if (instance == null) instance = new PostDataHandler();

        return instance;
    }

    void init(DatabaseInfo _master, DatabaseInfo _slave) {
        masterInfo = _master;
        slaveInfo = _slave;
        tableName = String.format("%spost", masterInfo.getSettings().getPrefix());
    }

    //Getters
    public IPost getPost(UUID postId, UUID accountIdForBookmarks) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, postId.toString());

            ResultSet res = ps.executeQuery();

            boolean hasNext = res.next();

            if (hasNext && res.getString("id") != null) {
                //Data present, let's grab it.
                switch (PostType.valueOf(res.getString("post_type"))) {
                    case TEXT:
                        TextPost textPost = new TextPost();
                        textPost.setId(postId);
                        textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                        textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                        textPost.setPermaLink(res.getString("permalink"));
                        textPost.setFullUrl(res.getString("full_url"));
                        textPost.setTimestamp(res.getLong("timestamp"));
                        textPost.setTitle(res.getString("title"));
                        textPost.setBody(res.getString("body"));
                        textPost.setNsfw(res.getBoolean("nsfw"));
                        textPost.tagsFromString(res.getString("tags"));
                        try {
                            UUID parent = UUID.fromString(res.getString("parent"));
                            textPost.setParent(parent);
                        } catch (Exception ignore) {
                            //No parent.
                        }
                        textPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, postId) != null);

                        textPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(postId));
                        textPost.setTotalReblogs(DataCountHandling.get().getReblogCount(postId));


                        ps.close();
                        return textPost;
                    case IMAGE:
                        ImagePost imagePost = new ImagePost();
                        imagePost.setId(postId);
                        imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                        imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                        imagePost.setPermaLink(res.getString("permalink"));
                        imagePost.setFullUrl(res.getString("full_url"));
                        imagePost.setTimestamp(res.getLong("timestamp"));
                        imagePost.setTitle(res.getString("title"));
                        imagePost.setBody(res.getString("body"));
                        imagePost.setNsfw(res.getBoolean("nsfw"));
                        imagePost.tagsFromString(res.getString("tags"));
                        try {
                            UUID parent = UUID.fromString(res.getString("parent"));
                            imagePost.setParent(parent);
                        } catch (Exception ignore) {
                            //No parent.
                        }
                        imagePost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, postId) != null);

                        imagePost.setImage(FileDataHandler.get().getFileFromUrl(res.getString("image_url")));

                        imagePost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(postId));
                        imagePost.setTotalReblogs(DataCountHandling.get().getReblogCount(postId));

                        ps.close();
                        return imagePost;
                    case VIDEO:
                        VideoPost videoPost = new VideoPost();
                        videoPost.setId(postId);
                        videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                        videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                        videoPost.setPermaLink(res.getString("permalink"));
                        videoPost.setFullUrl(res.getString("full_url"));
                        videoPost.setTimestamp(res.getLong("timestamp"));
                        videoPost.setTitle(res.getString("title"));
                        videoPost.setBody(res.getString("body"));
                        videoPost.setNsfw(res.getBoolean("nsfw"));
                        videoPost.tagsFromString(res.getString("tags"));
                        try {
                            UUID parent = UUID.fromString(res.getString("parent"));
                            videoPost.setParent(parent);
                        } catch (Exception ignore) {
                            //No parent.
                        }
                        videoPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, postId) != null);

                        videoPost.setVideo(FileDataHandler.get().getFileFromUrl(res.getString("video_url")));

                        videoPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(postId));
                        videoPost.setTotalReblogs(DataCountHandling.get().getReblogCount(postId));

                        ps.close();
                        return videoPost;
                    case AUDIO:
                        AudioPost audioPost = new AudioPost();
                        audioPost.setId(postId);
                        audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                        audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                        audioPost.setPermaLink(res.getString("permalink"));
                        audioPost.setFullUrl(res.getString("full_url"));
                        audioPost.setTimestamp(res.getLong("timestamp"));
                        audioPost.setTitle(res.getString("title"));
                        audioPost.setBody(res.getString("body"));
                        audioPost.setNsfw(res.getBoolean("nsfw"));
                        audioPost.tagsFromString(res.getString("tags"));
                        try {
                            UUID parent = UUID.fromString(res.getString("parent"));
                            audioPost.setParent(parent);
                        } catch (Exception ignore) {
                            //No parent.
                        }
                        audioPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, postId) != null);

                        audioPost.setAudio(FileDataHandler.get().getFileFromUrl(res.getString("audio_url")));

                        audioPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(postId));
                        audioPost.setTotalReblogs(DataCountHandling.get().getReblogCount(postId));

                        ps.close();
                        return audioPost;
                }
            }
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, true, this.getClass());
        }
        return null;
    }

    public List<IPost> getPostsSearch(long before, int inclusiveLimit, List<String> filters, UUID accountIdForBookmarks) {
        List<IPost> posts = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            //Build query
            StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE timestamp < ?");
            if (filters.size() > 0) {
                for (String ignored : filters) {
                    query.append(" AND tags LIKE ?");
                }
            }
            query.append(" ORDER BY timestamp DESC"); //This should fix issues...
            PreparedStatement ps = connection.prepareStatement(query.toString());
            ps.setLong(1, before);

            //Add filters
            if (filters.size() > 0) {
                int index = 2;
                for (String s : filters) {
                    ps.setString(index, "%" + s + "%");
                    index++;
                }
            }

            //Process data
            ResultSet res = ps.executeQuery();

            while (res.next() && posts.size() <= inclusiveLimit) {
                if (res.getString("id") != null) {
                    //Data present, let's grab it.
                    switch (PostType.valueOf(res.getString("post_type"))) {
                        case TEXT:
                            TextPost textPost = new TextPost();
                            textPost.setId(UUID.fromString(res.getString("id")));
                            textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            textPost.setPermaLink(res.getString("permalink"));
                            textPost.setFullUrl(res.getString("full_url"));
                            textPost.setTimestamp(res.getLong("timestamp"));
                            textPost.setTitle(res.getString("title"));
                            textPost.setBody(res.getString("body"));
                            textPost.setNsfw(res.getBoolean("nsfw"));
                            textPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                textPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            textPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, textPost.getId()) != null);

                            textPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(textPost.getId()));
                            textPost.setTotalReblogs(DataCountHandling.get().getReblogCount(textPost.getId()));

                            posts.add(textPost);
                            break;
                        case IMAGE:
                            ImagePost imagePost = new ImagePost();
                            imagePost.setId(UUID.fromString(res.getString("id")));
                            imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            imagePost.setPermaLink(res.getString("permalink"));
                            imagePost.setFullUrl(res.getString("full_url"));
                            imagePost.setTimestamp(res.getLong("timestamp"));
                            imagePost.setTitle(res.getString("title"));
                            imagePost.setBody(res.getString("body"));
                            imagePost.setNsfw(res.getBoolean("nsfw"));
                            imagePost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                imagePost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            imagePost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, imagePost.getId()) != null);

                            imagePost.setImage(FileDataHandler.get().getFileFromUrl(res.getString("image_url")));

                            imagePost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(imagePost.getId()));
                            imagePost.setTotalReblogs(DataCountHandling.get().getReblogCount(imagePost.getId()));

                            posts.add(imagePost);
                            break;
                        case VIDEO:
                            VideoPost videoPost = new VideoPost();
                            videoPost.setId(UUID.fromString(res.getString("id")));
                            videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            videoPost.setPermaLink(res.getString("permalink"));
                            videoPost.setFullUrl(res.getString("full_url"));
                            videoPost.setTimestamp(res.getLong("timestamp"));
                            videoPost.setTitle(res.getString("title"));
                            videoPost.setBody(res.getString("body"));
                            videoPost.setNsfw(res.getBoolean("nsfw"));
                            videoPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                videoPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            videoPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, videoPost.getId()) != null);

                            videoPost.setVideo(FileDataHandler.get().getFileFromUrl(res.getString("video_url")));

                            videoPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(videoPost.getId()));
                            videoPost.setTotalReblogs(DataCountHandling.get().getReblogCount(videoPost.getId()));

                            posts.add(videoPost);
                            break;
                        case AUDIO:
                            AudioPost audioPost = new AudioPost();
                            audioPost.setId(UUID.fromString(res.getString("id")));
                            audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            audioPost.setPermaLink(res.getString("permalink"));
                            audioPost.setFullUrl(res.getString("full_url"));
                            audioPost.setTimestamp(res.getLong("timestamp"));
                            audioPost.setTitle(res.getString("title"));
                            audioPost.setBody(res.getString("body"));
                            audioPost.setNsfw(res.getBoolean("nsfw"));
                            audioPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                audioPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            audioPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, audioPost.getId()) != null);

                            audioPost.setAudio(FileDataHandler.get().getFileFromUrl(res.getString("audio_url")));

                            audioPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(audioPost.getId()));
                            audioPost.setTotalReblogs(DataCountHandling.get().getReblogCount(audioPost.getId()));

                            posts.add(audioPost);
                            break;
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, true, this.getClass());
        }

        return posts;
    }

    public List<IPost> getPostsByBlog(UUID blogId, UUID accountIdForBookmarks) {
        List<IPost> posts = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE origin_blog_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, blogId.toString());

            ResultSet res = ps.executeQuery();

            while (res.next()) {
                if (res.getString("id") != null) {
                    //Data present, let's grab it.
                    switch (PostType.valueOf(res.getString("post_type"))) {
                        case TEXT:
                            TextPost textPost = new TextPost();
                            textPost.setId(UUID.fromString(res.getString("id")));
                            textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            textPost.setPermaLink(res.getString("permalink"));
                            textPost.setFullUrl(res.getString("full_url"));
                            textPost.setTimestamp(res.getLong("timestamp"));
                            textPost.setTitle(res.getString("title"));
                            textPost.setBody(res.getString("body"));
                            textPost.setNsfw(res.getBoolean("nsfw"));
                            textPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                textPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            textPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, textPost.getId()) != null);

                            textPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(textPost.getId()));
                            textPost.setTotalReblogs(DataCountHandling.get().getReblogCount(textPost.getId()));

                            posts.add(textPost);
                            break;
                        case IMAGE:
                            ImagePost imagePost = new ImagePost();
                            imagePost.setId(UUID.fromString(res.getString("id")));
                            imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            imagePost.setPermaLink(res.getString("permalink"));
                            imagePost.setFullUrl(res.getString("full_url"));
                            imagePost.setTimestamp(res.getLong("timestamp"));
                            imagePost.setTitle(res.getString("title"));
                            imagePost.setBody(res.getString("body"));
                            imagePost.setNsfw(res.getBoolean("nsfw"));
                            imagePost.tagsFromString(res.getString("tags"));
                            imagePost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, imagePost.getId()) != null);

                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                imagePost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            imagePost.setImage(FileDataHandler.get().getFileFromUrl(res.getString("image_url")));

                            imagePost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(imagePost.getId()));
                            imagePost.setTotalReblogs(DataCountHandling.get().getReblogCount(imagePost.getId()));

                            posts.add(imagePost);
                            break;
                        case VIDEO:
                            VideoPost videoPost = new VideoPost();
                            videoPost.setId(UUID.fromString(res.getString("id")));
                            videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            videoPost.setPermaLink(res.getString("permalink"));
                            videoPost.setFullUrl(res.getString("full_url"));
                            videoPost.setTimestamp(res.getLong("timestamp"));
                            videoPost.setTitle(res.getString("title"));
                            videoPost.setBody(res.getString("body"));
                            videoPost.setNsfw(res.getBoolean("nsfw"));
                            videoPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                videoPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            videoPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, videoPost.getId()) != null);

                            videoPost.setVideo(FileDataHandler.get().getFileFromUrl(res.getString("video_url")));

                            videoPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(videoPost.getId()));
                            videoPost.setTotalReblogs(DataCountHandling.get().getReblogCount(videoPost.getId()));

                            posts.add(videoPost);
                            break;
                        case AUDIO:
                            AudioPost audioPost = new AudioPost();
                            audioPost.setId(UUID.fromString(res.getString("id")));
                            audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            audioPost.setPermaLink(res.getString("permalink"));
                            audioPost.setFullUrl(res.getString("full_url"));
                            audioPost.setTimestamp(res.getLong("timestamp"));
                            audioPost.setTitle(res.getString("title"));
                            audioPost.setBody(res.getString("body"));
                            audioPost.setNsfw(res.getBoolean("nsfw"));
                            audioPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                audioPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            audioPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, audioPost.getId()) != null);

                            audioPost.setAudio(FileDataHandler.get().getFileFromUrl(res.getString("audio_url")));

                            audioPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(audioPost.getId()));
                            audioPost.setTotalReblogs(DataCountHandling.get().getReblogCount(audioPost.getId()));

                            posts.add(audioPost);
                            break;
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, true, this.getClass());
        }

        return posts;
    }

    public List<IPost> getPostsByBlog(UUID blogId, long before, int inclusiveLimit, List<String> filters, UUID accountIdForBookmarks) {
        List<IPost> posts = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            //Build query
            StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE origin_blog_id = ? AND timestamp < ?");
            if (filters.size() > 0) {
                for (String ignored : filters) {
                    query.append(" AND tags LIKE ?");
                }
            }
            query.append(" ORDER BY timestamp DESC"); //This should fix issues...
            PreparedStatement ps = connection.prepareStatement(query.toString());
            ps.setString(1, blogId.toString());
            ps.setLong(2, before);

            //Add filters
            if (filters.size() > 0) {
                int index = 3;
                for (String s : filters) {
                    ps.setString(index, "%" + s + "%");
                    index++;
                }
            }

            //Process data
            ResultSet res = ps.executeQuery();

            while (res.next() && posts.size() <= inclusiveLimit) {
                if (res.getString("id") != null) {
                    //Data present, let's grab it.
                    switch (PostType.valueOf(res.getString("post_type"))) {
                        case TEXT:
                            TextPost textPost = new TextPost();
                            textPost.setId(UUID.fromString(res.getString("id")));
                            textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            textPost.setPermaLink(res.getString("permalink"));
                            textPost.setFullUrl(res.getString("full_url"));
                            textPost.setTimestamp(res.getLong("timestamp"));
                            textPost.setTitle(res.getString("title"));
                            textPost.setBody(res.getString("body"));
                            textPost.setNsfw(res.getBoolean("nsfw"));
                            textPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                textPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            textPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, textPost.getId()) != null);

                            textPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(textPost.getId()));
                            textPost.setTotalReblogs(DataCountHandling.get().getReblogCount(textPost.getId()));

                            posts.add(textPost);
                            break;
                        case IMAGE:
                            ImagePost imagePost = new ImagePost();
                            imagePost.setId(UUID.fromString(res.getString("id")));
                            imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            imagePost.setPermaLink(res.getString("permalink"));
                            imagePost.setFullUrl(res.getString("full_url"));
                            imagePost.setTimestamp(res.getLong("timestamp"));
                            imagePost.setTitle(res.getString("title"));
                            imagePost.setBody(res.getString("body"));
                            imagePost.setNsfw(res.getBoolean("nsfw"));
                            imagePost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                imagePost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            imagePost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, imagePost.getId()) != null);

                            imagePost.setImage(FileDataHandler.get().getFileFromUrl(res.getString("image_url")));

                            imagePost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(imagePost.getId()));
                            imagePost.setTotalReblogs(DataCountHandling.get().getReblogCount(imagePost.getId()));

                            posts.add(imagePost);
                            break;
                        case VIDEO:
                            VideoPost videoPost = new VideoPost();
                            videoPost.setId(UUID.fromString(res.getString("id")));
                            videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            videoPost.setPermaLink(res.getString("permalink"));
                            videoPost.setFullUrl(res.getString("full_url"));
                            videoPost.setTimestamp(res.getLong("timestamp"));
                            videoPost.setTitle(res.getString("title"));
                            videoPost.setBody(res.getString("body"));
                            videoPost.setNsfw(res.getBoolean("nsfw"));
                            videoPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                videoPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            videoPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, videoPost.getId()) != null);

                            videoPost.setVideo(FileDataHandler.get().getFileFromUrl(res.getString("video_url")));

                            videoPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(videoPost.getId()));
                            videoPost.setTotalReblogs(DataCountHandling.get().getReblogCount(videoPost.getId()));

                            posts.add(videoPost);
                            break;
                        case AUDIO:
                            AudioPost audioPost = new AudioPost();
                            audioPost.setId(UUID.fromString(res.getString("id")));
                            audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            audioPost.setPermaLink(res.getString("permalink"));
                            audioPost.setFullUrl(res.getString("full_url"));
                            audioPost.setTimestamp(res.getLong("timestamp"));
                            audioPost.setTitle(res.getString("title"));
                            audioPost.setBody(res.getString("body"));
                            audioPost.setNsfw(res.getBoolean("nsfw"));
                            audioPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                audioPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            audioPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, audioPost.getId()) != null);

                            audioPost.setAudio(FileDataHandler.get().getFileFromUrl(res.getString("audio_url")));

                            audioPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(audioPost.getId()));
                            audioPost.setTotalReblogs(DataCountHandling.get().getReblogCount(audioPost.getId()));

                            posts.add(audioPost);
                            break;
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, true, this.getClass());
        }

        return posts;
    }

    public List<IPost> getPostsByAccount(UUID accountId, UUID accountIdForBookmarks) {
        List<IPost> posts = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE creator_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountId.toString());

            ResultSet res = ps.executeQuery();

            while (res.next()) {
                if (res.getString("id") != null) {
                    //Data present, let's grab it.
                    switch (PostType.valueOf(res.getString("post_type"))) {
                        case TEXT:
                            TextPost textPost = new TextPost();
                            textPost.setId(UUID.fromString(res.getString("id")));
                            textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            textPost.setPermaLink(res.getString("permalink"));
                            textPost.setFullUrl(res.getString("full_url"));
                            textPost.setTimestamp(res.getLong("timestamp"));
                            textPost.setTitle(res.getString("title"));
                            textPost.setBody(res.getString("body"));
                            textPost.setNsfw(res.getBoolean("nsfw"));
                            textPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                textPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            textPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, textPost.getId()) != null);

                            textPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(textPost.getId()));
                            textPost.setTotalReblogs(DataCountHandling.get().getReblogCount(textPost.getId()));

                            posts.add(textPost);
                            break;
                        case IMAGE:
                            ImagePost imagePost = new ImagePost();
                            imagePost.setId(UUID.fromString(res.getString("id")));
                            imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            imagePost.setPermaLink(res.getString("permalink"));
                            imagePost.setFullUrl(res.getString("full_url"));
                            imagePost.setTimestamp(res.getLong("timestamp"));
                            imagePost.setTitle(res.getString("title"));
                            imagePost.setBody(res.getString("body"));
                            imagePost.setNsfw(res.getBoolean("nsfw"));
                            imagePost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                imagePost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            imagePost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, imagePost.getId()) != null);

                            imagePost.setImage(FileDataHandler.get().getFileFromUrl(res.getString("image_url")));

                            imagePost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(imagePost.getId()));
                            imagePost.setTotalReblogs(DataCountHandling.get().getReblogCount(imagePost.getId()));

                            posts.add(imagePost);
                            break;
                        case VIDEO:
                            VideoPost videoPost = new VideoPost();
                            videoPost.setId(UUID.fromString(res.getString("id")));
                            videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            videoPost.setPermaLink(res.getString("permalink"));
                            videoPost.setFullUrl(res.getString("full_url"));
                            videoPost.setTimestamp(res.getLong("timestamp"));
                            videoPost.setTitle(res.getString("title"));
                            videoPost.setBody(res.getString("body"));
                            videoPost.setNsfw(res.getBoolean("nsfw"));
                            videoPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                videoPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            videoPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, videoPost.getId()) != null);

                            videoPost.setVideo(FileDataHandler.get().getFileFromUrl(res.getString("video_url")));

                            videoPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(videoPost.getId()));
                            videoPost.setTotalReblogs(DataCountHandling.get().getReblogCount(videoPost.getId()));

                            posts.add(videoPost);
                            break;
                        case AUDIO:
                            AudioPost audioPost = new AudioPost();
                            audioPost.setId(UUID.fromString(res.getString("id")));
                            audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            audioPost.setPermaLink(res.getString("permalink"));
                            audioPost.setFullUrl(res.getString("full_url"));
                            audioPost.setTimestamp(res.getLong("timestamp"));
                            audioPost.setTitle(res.getString("title"));
                            audioPost.setBody(res.getString("body"));
                            audioPost.setNsfw(res.getBoolean("nsfw"));
                            audioPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                audioPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            audioPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, audioPost.getId()) != null);

                            audioPost.setAudio(FileDataHandler.get().getFileFromUrl(res.getString("audio_url")));

                            audioPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(audioPost.getId()));
                            audioPost.setTotalReblogs(DataCountHandling.get().getReblogCount(audioPost.getId()));

                            posts.add(audioPost);
                            break;
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, true, this.getClass());
        }
        return posts;
    }

    public List<IPost> getPostsByAccount(UUID accountId, long before, int inclusiveLimit, UUID accountIdForBookmarks) {
        List<IPost> posts = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE creator_id = ? AND timestamp < ? ORDER BY timestamp DESC";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountId.toString());
            ps.setLong(2, before);

            ResultSet res = ps.executeQuery();

            while (res.next() && posts.size() <= inclusiveLimit) {
                if (res.getString("id") != null) {
                    //Data present, let's grab it.
                    switch (PostType.valueOf(res.getString("post_type"))) {
                        case TEXT:
                            TextPost textPost = new TextPost();
                            textPost.setId(UUID.fromString(res.getString("id")));
                            textPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            textPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            textPost.setPermaLink(res.getString("permalink"));
                            textPost.setFullUrl(res.getString("full_url"));
                            textPost.setTimestamp(res.getLong("timestamp"));
                            textPost.setTitle(res.getString("title"));
                            textPost.setBody(res.getString("body"));
                            textPost.setNsfw(res.getBoolean("nsfw"));
                            textPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                textPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            textPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, textPost.getId()) != null);

                            textPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(textPost.getId()));
                            textPost.setTotalReblogs(DataCountHandling.get().getReblogCount(textPost.getId()));

                            posts.add(textPost);
                            break;
                        case IMAGE:
                            ImagePost imagePost = new ImagePost();
                            imagePost.setId(UUID.fromString(res.getString("id")));
                            imagePost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            imagePost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            imagePost.setPermaLink(res.getString("permalink"));
                            imagePost.setFullUrl(res.getString("full_url"));
                            imagePost.setTimestamp(res.getLong("timestamp"));
                            imagePost.setTitle(res.getString("title"));
                            imagePost.setBody(res.getString("body"));
                            imagePost.setNsfw(res.getBoolean("nsfw"));
                            imagePost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                imagePost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            imagePost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, imagePost.getId()) != null);

                            imagePost.setImage(FileDataHandler.get().getFileFromUrl(res.getString("image_url")));

                            imagePost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(imagePost.getId()));
                            imagePost.setTotalReblogs(DataCountHandling.get().getReblogCount(imagePost.getId()));

                            posts.add(imagePost);
                            break;
                        case VIDEO:
                            VideoPost videoPost = new VideoPost();
                            videoPost.setId(UUID.fromString(res.getString("id")));
                            videoPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            videoPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            videoPost.setPermaLink(res.getString("permalink"));
                            videoPost.setFullUrl(res.getString("full_url"));
                            videoPost.setTimestamp(res.getLong("timestamp"));
                            videoPost.setTitle(res.getString("title"));
                            videoPost.setBody(res.getString("body"));
                            videoPost.setNsfw(res.getBoolean("nsfw"));
                            videoPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                videoPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            videoPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, videoPost.getId()) != null);

                            videoPost.setVideo(FileDataHandler.get().getFileFromUrl(res.getString("video_url")));

                            videoPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(videoPost.getId()));
                            videoPost.setTotalReblogs(DataCountHandling.get().getReblogCount(videoPost.getId()));

                            posts.add(videoPost);
                            break;
                        case AUDIO:
                            AudioPost audioPost = new AudioPost();
                            audioPost.setId(UUID.fromString(res.getString("id")));
                            audioPost.setCreator(AccountDataHandler.get().getAccountFromId(UUID.fromString(res.getString("creator_id"))));
                            audioPost.setOriginBlog(BlogDataHandler.get().getBlog(UUID.fromString(res.getString("origin_blog_id"))));
                            audioPost.setPermaLink(res.getString("permalink"));
                            audioPost.setFullUrl(res.getString("full_url"));
                            audioPost.setTimestamp(res.getLong("timestamp"));
                            audioPost.setTitle(res.getString("title"));
                            audioPost.setBody(res.getString("body"));
                            audioPost.setNsfw(res.getBoolean("nsfw"));
                            audioPost.tagsFromString(res.getString("tags"));
                            try {
                                UUID parent = UUID.fromString(res.getString("parent"));
                                audioPost.setParent(parent);
                            } catch (Exception ignore) {
                                //No parent.
                            }
                            audioPost.setBookmarked(BookmarkDataHandler.get().getBookmark(accountIdForBookmarks, audioPost.getId()) != null);

                            audioPost.setAudio(FileDataHandler.get().getFileFromUrl(res.getString("audio_url")));

                            audioPost.setTotalBookmarks(DataCountHandling.get().getBookmarkCount(audioPost.getId()));
                            audioPost.setTotalReblogs(DataCountHandling.get().getReblogCount(audioPost.getId()));

                            posts.add(audioPost);
                            break;
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post", e, true, this.getClass());
        }
        return posts;
    }

    public List<IPost> getParentTree(IPost post, UUID accountIdForBookmarks) {
        List<IPost> parents = new ArrayList<>();

        if (post.getParent() != null) {
            IPost postOn = post;

            while (postOn.getParent() != null) {
                IPost pp = getPost(postOn.getParent(), accountIdForBookmarks);

                if (PostUtils.doesNotHavePost(parents, pp.getId()))
                    parents.add(pp);
                postOn = pp;
            }
        }

        return parents;
    }

    //Setters
    public boolean addPost(IPost post) {
        try (final Connection masterConnection = masterInfo.getSource().getConnection()) {
            String hasDataQuery = "SELECT * FROM " + tableName + " WHERE id = ?";
            Connection slaveConnection = slaveInfo.getSource().getConnection();
            PreparedStatement statement = slaveConnection.prepareStatement(hasDataQuery);
            statement.setString(1, post.getId().toString());

            ResultSet res = statement.executeQuery();

            boolean hasData = res.next();

            if (!hasData || res.getString("id") == null) {
                //No data present, add
                String insert = "INSERT INTO " + tableName +
                        " (id, creator_id, origin_blog_id, permalink, full_url, post_type, " +
                        " timestamp, title, body, nsfw, parent, tags, " +
                        " image_url, audio_url, video_url)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = masterConnection.prepareStatement(insert);

                ps.setString(1, post.getId().toString());
                ps.setString(2, post.getCreator().getAccountId().toString());
                ps.setString(3, post.getOriginBlog().getBlogId().toString());
                ps.setString(4, post.getPermaLink());
                ps.setString(5, post.getFullUrl());
                ps.setString(6, post.getPostType().name());
                ps.setLong(7, post.getTimestamp());
                ps.setString(8, post.getTitle());
                ps.setString(9, post.getBody());
                ps.setBoolean(10, post.isNsfw());
                if (post.getParent() != null)
                    ps.setString(11, post.getParent().toString());
                else
                    ps.setString(11, null);
                ps.setString(12, post.tagsToString());

                //Specific post type handling
                switch (post.getPostType()) {
                    case IMAGE:
                        ps.setString(13, ((ImagePost) post).getImage().getUrl());
                        ps.setString(14, null);
                        ps.setString(15, null);
                        break;
                    case AUDIO:
                        ps.setString(13, null);
                        ps.setString(14, ((AudioPost) post).getAudio().getUrl());
                        ps.setString(15, null);
                        break;
                    case VIDEO:
                        ps.setString(13, null);
                        ps.setString(14, null);
                        ps.setString(15, ((VideoPost) post).getVideo().getUrl());
                        break;
                    default:
                        ps.setString(13, null);
                        ps.setString(14, null);
                        ps.setString(15, null);
                        break;
                }

                ps.execute();
                ps.close();
                statement.close();
                slaveConnection.close();
                return true;
            } else {
                //Data present, update
                String update = "UPDATE " + tableName + " SET " +
                        "creator_id = ?, origin_blog_id = ?, permalink = ?, full_url = ?, post_type = ?, " +
                        "timestamp = ?, title = ?, body = ?, nsfw = ?, parent = ?, tags = ?, " +
                        " image_url = ?, audio_url = ?, video_url = ? " +
                        " WHERE id = ?";
                PreparedStatement ps = masterInfo.getSource().getConnection().prepareStatement(update);

                ps.setString(1, post.getCreator().getAccountId().toString());
                ps.setString(2, post.getOriginBlog().getBlogId().toString());
                ps.setString(3, post.getPermaLink());
                ps.setString(4, post.getFullUrl());
                ps.setString(5, post.getPostType().name());
                ps.setLong(6, post.getTimestamp());
                ps.setString(7, post.getTitle());
                ps.setString(8, post.getBody());
                ps.setBoolean(9, post.isNsfw());
                if (post.getParent() != null)
                    ps.setString(10, post.getParent().toString());
                else
                    ps.setString(10, null);
                ps.setString(11, post.tagsToString());

                //Specific post type handling
                switch (post.getPostType()) {
                    case IMAGE:
                        ps.setString(12, ((ImagePost) post).getImage().getUrl());
                        ps.setString(13, null);
                        ps.setString(14, null);
                        break;
                    case AUDIO:
                        ps.setString(12, null);
                        ps.setString(13, ((AudioPost) post).getAudio().getUrl());
                        ps.setString(14, null);
                        break;
                    case VIDEO:
                        ps.setString(12, null);
                        ps.setString(13, null);
                        ps.setString(14, ((VideoPost) post).getVideo().getUrl());
                        break;
                    default:
                        ps.setString(12, null);
                        ps.setString(13, null);
                        ps.setString(14, null);
                        break;
                }
                ps.setString(15, post.getId().toString());

                ps.execute();
                ps.close();
                statement.close();
                slaveConnection.close();
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to save post", e, true, this.getClass());
        }
        return false;
    }
}
