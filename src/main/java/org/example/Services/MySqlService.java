package org.example.Services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.example.Entities.GptChat;
import org.example.Entities.GptChatMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MySqlService {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3307/DiscordMain";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "20162016";

    public static void setBannedRoleId(String guildId, String roleId) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE Guilds SET banned_role_id = '" + roleId + "' WHERE guild_id = '" + guildId + "'");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static String getBannedRoleId(String guildId) {
        String bannedRoleId = null;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Guilds WHERE guild_id = '" + guildId + "'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bannedRoleId = rs.getString("banned_role_id");
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return bannedRoleId;
    }

    public static ArrayList<String> getGuildsId() {
        ArrayList<String> guildsId = new ArrayList<String>();
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Guilds");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                guildsId.add(rs.getString("guild_id"));
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return guildsId;
    }

    public static String getGuildName(String guildId) {
        String guildName = null;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Guilds WHERE guild_id = '" + guildId + "'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                guildName = rs.getString("guild_name");
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return guildName;
    }

    public static void setGuildName(String guildId, String guildName) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE Guilds SET guild_name = '" + guildName + "' WHERE guild_id = '" + guildId + "'");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static void removeGuild(String guildId) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Guilds WHERE guild_id = '" + guildId + "'");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static void setupGuilds(List<Guild> guilds) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            for (var guild : guilds) {
                stmt.executeUpdate("INSERT IGNORE INTO Guilds (guild_id, guild_name) VALUES ('" + guild.getId() + "', '" + guild.getName() + "')");
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static void setApiKey(String guildId, String apiKey) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE Guilds SET api_key = '" + apiKey + "' WHERE guild_id = '" + guildId + "'");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static String getApiKey(String guildId) {
        String apiKey = null;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Guilds WHERE guild_id = '" + guildId + "'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                apiKey = rs.getString("api_key");
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return apiKey;
    }

    public static void insertGptChatMessage(GptChatMessage message) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO GptChatMessages (channel_id, message_role, message_content) VALUES ('"
                    + message.getChannelId() + "', '"
                    + message.getMessageRole() + "', '"
                    + message.getMessageContent() + "')");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static ArrayList<GptChatMessage> getGptDialogMessages(String channelId) {
        ArrayList<GptChatMessage> dialog = new ArrayList<GptChatMessage>();
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM GptChatMessages WHERE channel_id = '" + channelId + "'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dialog.add(new GptChatMessage(rs.getString("channel_id"),
                        rs.getString("message_role"),
                        rs.getString("message_content"),
                        rs.getDate("message_time")));
            }
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return dialog;
    }

    public static void deleteGptDialogMessages(String channelId){
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM GptChatMessages WHERE channel_id = '"+channelId+"'");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static boolean isGptChatExist(GptChat chat){
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM GptChats WHERE user_id LIKE '%"+chat.getUserId()+"%' and guild_id LIKE '%"+chat.getGuildId()+"%' LIMIT 1) AS mycheck");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("mycheck") > 0;
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    public static void insertGptChat(GptChat chat){
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO GptChats (user_id, guild_id) VALUES ('"+chat.getUserId()+"', '"+chat.getGuildId()+"')");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static void deleteGptChat(GptChat chat){
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM GptChats WHERE user_id = '"+chat.getUserId()+"' and guild_id = '"+chat.getGuildId()+"'");
        } catch (SQLException e) {
            System.err.println("Message: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }
}
