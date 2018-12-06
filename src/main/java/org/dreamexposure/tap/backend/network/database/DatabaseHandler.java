package org.dreamexposure.tap.backend.network.database;

import org.dreamexposure.novautils.database.DatabaseInfo;
import org.dreamexposure.novautils.database.DatabaseManager;
import org.dreamexposure.novautils.database.DatabaseSettings;
import org.dreamexposure.tap.backend.conf.SiteSettings;
import org.dreamexposure.tap.backend.utils.Logger;
import org.dreamexposure.tap.core.enums.blog.BlogType;
import org.dreamexposure.tap.core.enums.post.PostType;
import org.dreamexposure.tap.core.objects.account.Account;
import org.dreamexposure.tap.core.objects.auth.AccountAuthentication;
import org.dreamexposure.tap.core.objects.confirmation.EmailConfirmation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NovaFox161
 * Date Created: 12/4/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings({"UnusedReturnValue", "SqlNoDataSourceInspection", "SqlResolve", "Duplicates"})
public class DatabaseHandler {
    private static DatabaseHandler instance;
    private DatabaseInfo databaseInfo;
    
    private DatabaseHandler() {
    } //Prevent initialization.
    
    /**
     * Gets the instance of the {@link DatabaseHandler}.
     *
     * @return The instance of the {@link DatabaseHandler}
     */
    public static DatabaseHandler getHandler() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }
    
    /**
     * Connects to the MySQL server specified.
     */
    public void connectToMySQL() {
        DatabaseSettings settings = new DatabaseSettings(SiteSettings.SQL_HOST.get(), SiteSettings.SQL_PORT.get(), SiteSettings.SQL_DB.get(), SiteSettings.SQL_USER.get(), SiteSettings.SQL_PASSWORD.get(), SiteSettings.SQL_PREFIX.get());
        
        databaseInfo = DatabaseManager.connectToMySQL(settings);
    }
    
    /**
     * Disconnects from the MySQL server if still connected.
     */
    public void disconnectFromMySQL() {
        if (databaseInfo != null) {
            DatabaseManager.disconnectFromMySQL(databaseInfo);
        }
    }
    
    /**
     * Creates all required tables in the database if they do not exist.
     */
    public void createTables() {
        try {
            Statement statement = databaseInfo.getConnection().createStatement();
            
            String accountsTableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
            String confirmationTableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
            String blogTableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());
            String postTableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
            String authTableName = String.format("%sauth", databaseInfo.getSettings().getPrefix());
            
            String createAccountsTable = "CREATE TABLE IF NOT EXISTS " + accountsTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " username VARCHAR(255) not NULL, " +
                    " email LONGTEXT not NULL, " +
                    " hash LONGTEXT not NULL, " +
                    " phone_number VARCHAR(255) not NULL, " +
                    " birthday VARCHAR(255) not NULL, " +
                    " safe_search BOOLEAN not NULL, " +
                    " verified BOOLEAN not NULL, " +
                    " email_confirmed BOOLEAN not NULL, " +
                    " admin BOOLEAN not NULL, " +
                    " PRIMARY KEY (id))";
            String createConfirmationTable = "CREATE TABLE IF NOT EXISTS " + confirmationTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " code VARCHAR(32) not NULL, " +
                    "PRIMARY KEY (id))";
            String createBlogTable = "CREATE TABLE IF NOT EXISTS " + blogTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " base_url LONGTEXT not NULL, " +
                    " complete_url LONGTEXT not NULL, " +
                    " blog_type VARCHAR(255) not NULL, " +
                    " name LONGTEXT not NULL, " +
                    " description LONGTEXT not NULL, " +
                    " icon_url LONGTEXT not NULL, " +
                    " background_color VARCHAR(255) not NULL, " +
                    " background_url LONGTEXT not NULL, " +
                    " allow_under_18 BOOLEAN not NULL, " +
                    " nsfw BOOLEAN not NULL, " +
                    " owners LONGTEXT NULL, " +
                    " owner VARCHAR(255) null, " +
                    " PRIMARY KEY (id))";
            String createPostTable = "CREATE TABLE IF NOT EXISTS " + postTableName +
                    "(id VARCHAR(255) not NULL, " +
                    " creator_id VARCHAR(255) not NULL, " +
                    " origin_blog_id VARCHAR(255) not NULL, " +
                    " permalink LONGTEXT not NULL, " +
                    " full_url LONGTEXT not NULL, " +
                    " post_type VARCHAR(255) not NULL, " +
                    " timestamp LONG not NULL, " +
                    " title LONGTEXT not NULL, " +
                    " body LONGTEXT not NULL, " +
                    " nsfw BOOLEAN not NULL, " +
                    " image_url LONGTEXT NULL, " +
                    " audio_url LONGTEXT NULL, " +
                    " video_url LONGTEXT NULL, " +
                    " PRIMARY KEY (id))";
            String createAuthTable = "CREATE TABLE IF NOT EXISTS " + authTableName +
                    "(id VARCHAR(255) NOT NULL, " +
                    " refresh_token VARCHAR(64) NOT NULL, " +
                    " access_token VARCHAR(64) NOT NULL, " +
                    " expire LONG NOT NULL, " +
                    " PRIMARY KEY (id))";
            
            statement.execute(createAccountsTable);
            statement.execute(createConfirmationTable);
            statement.execute(createBlogTable);
            statement.execute(createPostTable);
            statement.execute(createAuthTable);
            
            statement.close();
            System.out.println("Successfully created needed tables in MySQL database!");
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to create database tables", e, this.getClass());
        }
    }
    
    //Actual database methods that are very useful
    public void createAccount(String username, String email, String hash, String birthday) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getMySQL().getPrefix());
                String query = "INSERT INTO " + tableName + " (id, username, email, hash, phone_number, birthday, safe_search, verified, email_confirmed, admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, username);
                statement.setString(3, email);
                statement.setString(4, hash);
                statement.setString(5, "000.000.000");
                statement.setString(6, birthday);
                statement.setBoolean(7, false);
                statement.setBoolean(8, false);
                statement.setBoolean(9, false);
                statement.setBoolean(10, false);
                
                statement.execute();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to register new user", e, this.getClass());
        }
    }
    
    public Account getAccountFromUsername(String username) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE username = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, username);
                
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff) {
                    Account a = new Account();
                    a.setAccountId(UUID.fromString(res.getString("user_id")));
                    a.setUsername(username);
                    a.setEmail(res.getString("email"));
                    a.setPhoneNumber(res.getString("phone_number"));
                    a.setBirthday(res.getString("birthday"));
                    a.setSafeSearch(res.getBoolean("safe_search"));
                    a.setVerified(res.getBoolean("verified"));
                    a.setEmailConfirmed(res.getBoolean("email_confirmed"));
                    
                    statement.close();
                    return a;
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get user from database by username", e, this.getClass());
        }
        return null;
    }
    
    public Account getAccountFromEmail(String email) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE email = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, email);
                
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff) {
                    Account a = new Account();
                    a.setAccountId(UUID.fromString(res.getString("user_id")));
                    a.setUsername(res.getString("username"));
                    a.setEmail(email);
                    a.setPhoneNumber(res.getString("phone_number"));
                    a.setBirthday(res.getString("birthday"));
                    a.setSafeSearch(res.getBoolean("safe_search"));
                    a.setVerified(res.getBoolean("verified"));
                    a.setEmailConfirmed(res.getBoolean("email_confirmed"));
                    
                    statement.close();
                    return a;
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get user from database by email", e, this.getClass());
        }
        return null;
    }
    
    public Account getAccountFromId(UUID id) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE user_id = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, id.toString());
                
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff) {
                    Account a = new Account();
                    a.setAccountId(id);
                    a.setUsername(res.getString("username"));
                    a.setEmail(res.getString("email"));
                    a.setPhoneNumber(res.getString("phone_number"));
                    a.setBirthday(res.getString("birthday"));
                    a.setSafeSearch(res.getBoolean("safe_search"));
                    a.setVerified(res.getBoolean("verified"));
                    a.setEmailConfirmed(res.getBoolean("email_confirmed"));
                    return a;
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get user from database by id", e, this.getClass());
        }
        return null;
    }
    
    public boolean validLogin(String email, String password) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE email = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, email);
                
                ResultSet res = statement.executeQuery();
                
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (res.next() && encoder.matches(password, res.getString("hash"))) {
                    statement.close();
                    return true;
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to validate login", e, this.getClass());
        }
        return false;
    }
    
    public boolean usernameOrEmailTaken(String username, String email) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                
                //Try email first....
                String query = "SELECT * FROM " + tableName + " WHERE email = ? OR username = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, email);
                statement.setString(2, username);
                
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff) {
                    statement.close();
                    return true;
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to verify username/email taken", e, this.getClass());
        }
        return false;
    }
    
    public void addPendingConfirmation(Account account, String code) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "INSERT INTO " + tableName + " (user_id, code) VALUES (?, ?)";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.setString(1, account.getAccountId().toString());
                statement.setString(2, code);
                
                statement.execute();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to input confirmation code", e, this.getClass());
        }
    }
    
    public EmailConfirmation getConfirmationInfo(String code) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE code = '" + code + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff && res.getString("code") != null) {
                    EmailConfirmation con = new EmailConfirmation();
                    con.setUserId(UUID.fromString(res.getString("id")));
                    con.setCode(code);
                    
                    statement.close();
                    
                    return con;
                } else {
                    //Data not present.
                    statement.close();
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get confirmation data", e, this.getClass());
        }
        return null;
    }
    
    public void removeConfirmationInfo(String code) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE code = '" + code + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete confirmation data", e, this.getClass());
        }
    }
    
    public void saveAuth(AccountAuthentication auth) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sauth", databaseInfo.getMySQL().getPrefix());
                String query = "INSERT INTO " + tableName + " (id, refresh_token, access_token, expire) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.setString(1, auth.getAccountId().toString());
                statement.setString(2, auth.getRefreshToken());
                statement.setString(3, auth.getAccessToken());
                statement.setLong(4, auth.getExpire());
                
                statement.execute();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to add Authentication data.", e, this.getClass());
        }
    }
    
    public void updateAuth(AccountAuthentication auth) {
        removeAuthByRefreshToken(auth.getRefreshToken());
        saveAuth(auth);
    }
    
    public AccountAuthentication getAuthFromAccessToken(String accessToken) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE access_token = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, accessToken);
                
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff) {
                    AccountAuthentication auth = new AccountAuthentication();
                    auth.setAccountId(UUID.fromString(res.getString("id")));
                    auth.setRefeshToken(res.getString("refresh_token"));
                    auth.setAccessToken(res.getString("access_token"));
                    auth.setExpire(res.getLong("expire"));
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth data from database by access token", e, this.getClass());
        }
        return null;
    }
    
    public AccountAuthentication getAuthFromRefreshToken(String refreshToken) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE refresh_token = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, refreshToken);
                
                ResultSet res = statement.executeQuery();
                
                boolean hasStuff = res.next();
                
                if (hasStuff) {
                    AccountAuthentication auth = new AccountAuthentication();
                    auth.setAccountId(UUID.fromString(res.getString("id")));
                    auth.setRefeshToken(res.getString("refresh_token"));
                    auth.setAccessToken(res.getString("access_token"));
                    auth.setExpire(res.getLong("expire"));
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth data from database by refresh token", e, this.getClass());
        }
        return null;
    }
    
    public List<AccountAuthentication> getAllAuth(UUID accountId) {
        List<AccountAuthentication> all = new ArrayList<>();
        
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                String query = "SELECT * FROM " + tableName + " WHERE id = ?";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                statement.setString(1, accountId.toString());
                
                ResultSet res = statement.executeQuery();
                
                while (res.next()) {
                    if (res.getString("id") != null) {
                        AccountAuthentication auth = new AccountAuthentication();
                        auth.setAccountId(accountId);
                        auth.setRefeshToken(res.getString("refresh_token"));
                        auth.setAccessToken(res.getString("access_token"));
                        auth.setExpire(res.getLong("expire"));
                        
                        all.add(auth);
                    }
                }
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth data from database by refresh token", e, this.getClass());
        }
        
        return all;
    }
    
    public void removeAuth(UUID accountId) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sauth", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE id = '" + accountId.toString() + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete auth data.", e, this.getClass());
        }
    }
    
    public void removeAuthByAccessToken(String accessToken) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE access_token = '" + accessToken + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete auth data by access token.", e, this.getClass());
        }
    }
    
    public void removeAuthByRefreshToken(String refreshToken) {
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sconfirmation", databaseInfo.getSettings().getPrefix());
                String query = "DELETE FROM " + tableName + " WHERE refreshToken = '" + refreshToken + "';";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to delete auth data by refresh token.", e, this.getClass());
        }
    }
    
    //For handling counting...
    public int getAccountCount() {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%saccounts", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get account count", e, this.getClass());
        }
        return amount;
    }
    
    public int getAuthCount(UUID accountId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sauth", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE id = " + accountId.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get auth count for account.", e, this.getClass());
        }
        return amount;
    }
    
    public int getBlogCount() {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog count", e, this.getClass());
        }
        return amount;
    }
    
    public int getBlogCount(BlogType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE blog_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog count", e, this.getClass());
        }
        return amount;
    }
    
    public int getBlogCount(UUID accountId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%sblog", databaseInfo.getSettings().getPrefix());
                
                //Include personal AND group blogs
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE owner = " + accountId.toString() + " OR owners LIKE %" + accountId.toString() + "%;";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get blog count for account", e, this.getClass());
        }
        return amount;
    }
    
    public int getPostCount() {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count", e, this.getClass());
        }
        return amount;
    }
    
    public int getPostCount(PostType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE post_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count by type", e, this.getClass());
        }
        return amount;
    }
    
    public int getPostCountForBlog(UUID blogId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE origin_blog_id = " + blogId.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for blog", e, this.getClass());
        }
        return amount;
    }
    
    public int getPostCountForBlog(UUID blogId, PostType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE origin_blog_id = " + blogId.toString() + " AND post_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for blog", e, this.getClass());
        }
        return amount;
    }
    
    public int getPostcountForAccount(UUID accountId) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE creator_id = " + accountId.toString() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for account", e, this.getClass());
        }
        return amount;
    }
    
    public int getPostcountForAccount(UUID accountId, PostType type) {
        int amount = -1;
        try {
            if (databaseInfo.getMySQL().checkConnection()) {
                String tableName = String.format("%spost", databaseInfo.getSettings().getPrefix());
                
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE creator_id = " + accountId.toString() + " AND post_type = " + type.name() + ";";
                PreparedStatement statement = databaseInfo.getConnection().prepareStatement(query);
                ResultSet res = statement.executeQuery();
                
                if (res.next())
                    amount = res.getInt(1);
                else
                    amount = 0;
                
                
                res.close();
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger().exception("Failed to get post count for account", e, this.getClass());
        }
        return amount;
    }
}
