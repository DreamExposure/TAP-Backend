package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.dreamexposure.tap.core.objects.blog.GroupBlog;
import org.dreamexposure.tap.core.objects.blog.IBlog;
import org.dreamexposure.tap.core.objects.blog.PersonalBlog;
import org.dreamexposure.tap.core.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/20/2018
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings({"UnusedReturnValue", "SqlNoDataSourceInspection", "Duplicates"})
public class BlogDataHandler {
    private static BlogDataHandler instance;

    private DatabaseInfo masterInfo;
    private DatabaseInfo slaveInfo;

    private BlogDataHandler() {
    }

    public static BlogDataHandler get() {
        if (instance == null) instance = new BlogDataHandler();

        return instance;
    }

    void init(DatabaseInfo _master, DatabaseInfo _slave) {
        masterInfo = _master;
        slaveInfo = _slave;
    }

    public boolean createOrUpdateBlog(IBlog blog) {
        try (final Connection masterConnection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());

            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            Connection slaveConnection = slaveInfo.getSource().getConnection();
            PreparedStatement statement = slaveConnection.prepareStatement(query);
            statement.setString(1, blog.getBlogId().toString());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (!hasStuff || res.getString("id") == null) {
                //Data not present, add to DB.
                String insertCommand = "INSERT INTO " + tableName +
                        "(id, base_url, complete_url, blog_type, name, description, " +
                        " icon_url, background_color, background_url, " +
                        " allow_under_18, nsfw, show_age, owners, owner)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement ps = masterConnection.prepareStatement(insertCommand);

                ps.setString(1, blog.getBlogId().toString());
                ps.setString(2, blog.getBaseUrl());
                ps.setString(3, blog.getCompleteUrl());
                ps.setString(4, blog.getType().name());
                ps.setString(5, blog.getName());
                ps.setString(6, blog.getDescription());
                ps.setString(7, blog.getIconImage().getUrl());
                ps.setString(8, blog.getBackgroundColor());
                ps.setString(9, blog.getBackgroundImage().getUrl());
                ps.setBoolean(10, blog.isAllowUnder18());
                ps.setBoolean(11, blog.isNsfw());
                if (blog instanceof GroupBlog) {
                    ps.setBoolean(12, false);
                    ps.setString(13, ((GroupBlog) blog).getOwners().toString());
                    ps.setString(14, null);
                } else {
                    ps.setBoolean(12, ((PersonalBlog) blog).isDisplayAge());
                    ps.setString(13, null);
                    ps.setString(14, ((PersonalBlog) blog).getOwnerId().toString());
                }

                ps.executeUpdate();
                ps.close();
                statement.close();
                slaveConnection.close();
                return true;
            } else {
                //Data present, update.
                String update = "UPDATE " + tableName +
                        " SET base_url = ?, complete_url = ?, blog_type = ?, name = ?, description = ?, " +
                        "icon_url = ?, background_color = ?, background_url = ?, " +
                        " allow_under_18 = ?, nsfw = ?, show_age = ?, owners = ?, owner = ? " +
                        " WHERE id = ?";
                PreparedStatement ps = masterConnection.prepareStatement(update);

                ps.setString(1, blog.getBaseUrl());
                ps.setString(2, blog.getCompleteUrl());
                ps.setString(3, blog.getType().name());
                ps.setString(4, blog.getName());
                ps.setString(5, blog.getDescription());
                ps.setString(6, blog.getIconImage().getUrl());
                ps.setString(7, blog.getBackgroundColor());
                ps.setString(8, blog.getBackgroundImage().getUrl());
                ps.setBoolean(9, blog.isAllowUnder18());
                ps.setBoolean(10, blog.isNsfw());
                if (blog instanceof GroupBlog) {
                    ps.setBoolean(11, false);
                    ps.setString(12, ((GroupBlog) blog).getOwners().toString());
                    ps.setString(13, null);
                } else {
                    ps.setBoolean(11, ((PersonalBlog) blog).isDisplayAge());
                    ps.setString(12, null);
                    ps.setString(13, ((PersonalBlog) blog).getOwnerId().toString());
                }
                ps.setString(14, blog.getBlogId().toString());

                ps.executeUpdate();
                ps.close();
                statement.close();
                slaveConnection.close();
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to create or update blog in database", e, true, this.getClass());
        }
        return false;
    }

    public IBlog getBlog(UUID blogId) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, blogId.toString());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                if (BlogType.valueOf(res.getString("blog_type")) == BlogType.GROUP) {
                    GroupBlog blog = new GroupBlog();
                    blog.setBlogId(blogId);

                    blog.setBaseUrl(res.getString("base_url"));
                    blog.setCompleteUrl(res.getString("complete_url"));
                    blog.setName(res.getString("name"));
                    blog.setDescription(res.getString("description"));
                    blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                    blog.setBackgroundColor(res.getString("background_color"));
                    blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                    blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                    blog.setNsfw(res.getBoolean("nsfw"));

                    @SuppressWarnings("RegExpRedundantEscape")
                    String ownersRaw = res.getString("owners").replaceAll("\\[", "").replaceAll("\\]", "");

                    for (String s : ownersRaw.split(",")) {
                        blog.getOwners().add(UUID.fromString(s));
                    }

                    blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));

                    statement.close();
                    return blog;
                } else {
                    PersonalBlog blog = new PersonalBlog();
                    blog.setBlogId(blogId);

                    blog.setBaseUrl(res.getString("base_url"));
                    blog.setCompleteUrl(res.getString("complete_url"));
                    blog.setName(res.getString("name"));
                    blog.setDescription(res.getString("description"));
                    blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                    blog.setBackgroundColor(res.getString("background_color"));
                    blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                    blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                    blog.setNsfw(res.getBoolean("nsfw"));

                    blog.setOwnerId(UUID.fromString(res.getString("owner")));
                    blog.setDisplayAge(res.getBoolean("show_age"));

                    blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));

                    statement.close();
                    return blog;
                }
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog from database", e, true, this.getClass());
        }
        return null;
    }

    public IBlog getBlog(String baseUrl) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE base_url = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, baseUrl);

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                if (BlogType.valueOf(res.getString("blog_type")) == BlogType.GROUP) {
                    GroupBlog blog = new GroupBlog();
                    blog.setBlogId(UUID.fromString(res.getString("id")));

                    blog.setBaseUrl(res.getString("base_url"));
                    blog.setCompleteUrl(res.getString("complete_url"));
                    blog.setName(res.getString("name"));
                    blog.setDescription(res.getString("description"));
                    blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                    blog.setBackgroundColor(res.getString("background_color"));
                    blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                    blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                    blog.setNsfw(res.getBoolean("nsfw"));

                    @SuppressWarnings("RegExpRedundantEscape")
                    String ownersRaw = res.getString("owners").replaceAll("\\[", "").replaceAll("\\]", "");

                    for (String s : ownersRaw.split(",")) {
                        blog.getOwners().add(UUID.fromString(s));
                    }

                    blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));

                    statement.close();
                    return blog;
                } else {
                    PersonalBlog blog = new PersonalBlog();
                    blog.setBlogId(UUID.fromString(res.getString("id")));

                    blog.setBaseUrl(res.getString("base_url"));
                    blog.setCompleteUrl(res.getString("complete_url"));
                    blog.setName(res.getString("name"));
                    blog.setDescription(res.getString("description"));
                    blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                    blog.setBackgroundColor(res.getString("background_color"));
                    blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                    blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                    blog.setNsfw(res.getBoolean("nsfw"));

                    blog.setOwnerId(UUID.fromString(res.getString("owner")));
                    blog.setDisplayAge(res.getBoolean("show_age"));

                    blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));

                    statement.close();
                    return blog;
                }
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog from database", e, true, this.getClass());
        }
        return null;
    }

    public GroupBlog getGroupBlog(UUID blogId) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE id = ? AND blog_type = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, blogId.toString());
            statement.setString(2, BlogType.GROUP.name());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                GroupBlog blog = new GroupBlog();
                blog.setBlogId(blogId);

                blog.setBaseUrl(res.getString("base_url"));
                blog.setCompleteUrl(res.getString("complete_url"));
                blog.setName(res.getString("name"));
                blog.setDescription(res.getString("description"));
                blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                blog.setBackgroundColor(res.getString("background_color"));
                blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                blog.setNsfw(res.getBoolean("nsfw"));

                @SuppressWarnings("RegExpRedundantEscape")
                String ownersRaw = res.getString("owners").replaceAll("\\[", "").replaceAll("\\]", "");

                for (String s : ownersRaw.split(",")) {
                    blog.getOwners().add(UUID.fromString(s));
                }

                blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));


                statement.close();
                return blog;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog from database", e, true, this.getClass());
        }
        return null;
    }

    public PersonalBlog getPersonalBlog(UUID blogId) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE id = ? AND blog_type = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, blogId.toString());
            statement.setString(2, BlogType.PERSONAL.name());

            ResultSet res = statement.executeQuery();

            boolean hasStuff = res.next();

            if (hasStuff) {
                PersonalBlog blog = new PersonalBlog();
                blog.setBlogId(blogId);

                blog.setBaseUrl(res.getString("base_url"));
                blog.setCompleteUrl(res.getString("complete_url"));
                blog.setName(res.getString("name"));
                blog.setDescription(res.getString("description"));
                blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                blog.setBackgroundColor(res.getString("background_color"));
                blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                blog.setNsfw(res.getBoolean("nsfw"));

                blog.setOwnerId(UUID.fromString(res.getString("owner")));
                blog.setDisplayAge(res.getBoolean("show_age"));

                blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));

                statement.close();
                return blog;
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog from database", e, true, this.getClass());
        }
        return null;
    }

    public List<IBlog> getBlogs(UUID ownerId) {
        List<IBlog> blogs = new ArrayList<>();
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE owner = ? OR owners LIKE ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ownerId.toString());
            statement.setString(2, ownerId.toString());

            ResultSet res = statement.executeQuery();

            while (res.next()) {
                if (BlogType.valueOf(res.getString("blog_type")) == BlogType.GROUP) {
                    GroupBlog blog = new GroupBlog();
                    blog.setBlogId(UUID.fromString(res.getString("id")));

                    blog.setBaseUrl(res.getString("base_url"));
                    blog.setCompleteUrl(res.getString("complete_url"));
                    blog.setName(res.getString("name"));
                    blog.setDescription(res.getString("description"));
                    blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                    blog.setBackgroundColor(res.getString("background_color"));
                    blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                    blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                    blog.setNsfw(res.getBoolean("nsfw"));

                    @SuppressWarnings("RegExpRedundantEscape")
                    String ownersRaw = res.getString("owners").replaceAll("\\[", "").replaceAll("\\]", "");

                    for (String s : ownersRaw.split(",")) {
                        blog.getOwners().add(UUID.fromString(s));
                    }

                    blogs.add(blog);
                } else {
                    PersonalBlog blog = new PersonalBlog();
                    blog.setBlogId(UUID.fromString(res.getString("id")));

                    blog.setBaseUrl(res.getString("base_url"));
                    blog.setCompleteUrl(res.getString("complete_url"));
                    blog.setName(res.getString("name"));
                    blog.setDescription(res.getString("description"));
                    blog.setIconImage(FileDataHandler.get().getFileFromUrl(res.getString("icon_url")));
                    blog.setBackgroundColor(res.getString("background_color"));
                    blog.setBackgroundImage(FileDataHandler.get().getFileFromUrl(res.getString("background_url")));
                    blog.setAllowUnder18(res.getBoolean("allow_under_18"));
                    blog.setNsfw(res.getBoolean("nsfw"));

                    blog.setOwnerId(UUID.fromString(res.getString("owner")));
                    blog.setDisplayAge(res.getBoolean("show_age"));

                    blog.getFollowers().addAll(FollowerDataHandler.get().getFollowersIdList(blog.getBlogId()));

                    blogs.add(blog);
                }
            }
            statement.close();
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get all blogs of user from database", e, true, this.getClass());
        }
        return blogs;
    }

    public boolean blogUrlTaken(String url) {
        try (final Connection connection = slaveInfo.getSource().getConnection()) {
            String tableName = String.format("%sblog", slaveInfo.getSettings().getPrefix());
            String query = "SELECT * FROM " + tableName + " WHERE base_url = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, url);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                statement.close();
                return true;
            }
            statement.close();
            return false;
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to check if URL is taken", e, true, this.getClass());
            //Assume its taken just to be on the safe side.
            return true;
        }
    }
}
