package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public List<Message> getAllMessage() throws SQLException {
        Connection con = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                messages.add(message);

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    // For Create Message if Message user exits
    public Message createMessage(Message message) {
        Connection con = ConnectionUtil.getConnection();
        try {
            // Check if posted_by foregain Key of Account_ID exits in the Message Dtabase
            String checkPosted_by = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement checkPostedBystmt = con.prepareStatement(checkPosted_by);
            checkPostedBystmt.setInt(1, message.posted_by);
            ResultSet rs = checkPostedBystmt.executeQuery();

            if (rs.next()) {
                String insertMessageQuery = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES(?,?,?)";

                PreparedStatement insertMessageStmt = con.prepareStatement(insertMessageQuery,
                        Statement.RETURN_GENERATED_KEYS);
                insertMessageStmt.setInt(1, message.posted_by);
                insertMessageStmt.setString(2, message.message_text);
                insertMessageStmt.setLong(3, message.time_posted_epoch);
                int affectedRow = insertMessageStmt.executeUpdate();
                if (affectedRow == 0) {
                    return null;
                }
                ResultSet generateKeys = insertMessageStmt.getGeneratedKeys();
                if (generateKeys.next()) {
                    int messageID = generateKeys.getInt(1);
                    return new Message(messageID, message.posted_by, message.message_text, message.time_posted_epoch);

                } else {
                    return null;
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;

        }

        return null;

    }

    // For delete Message by ID test!
    public Message deleteMessage(int messageId) {
        Connection con = ConnectionUtil.getConnection();
        try {
            String getMessageQuery = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement getMessageStmt = con.prepareStatement(getMessageQuery);
            getMessageStmt.setInt(1, messageId);
            ResultSet rs = getMessageStmt.executeQuery();
            if (rs.next()) {
                Message deletedMessage = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                String deleteMessageQuery = "DELETE FROM message WHERE message_id = ?";
                PreparedStatement deleteMessageStmt = con.prepareStatement(deleteMessageQuery);
                deleteMessageStmt.setInt(1, messageId);
                int affectedRows = deleteMessageStmt.executeUpdate();
                if (affectedRows > 0) {
                    return deletedMessage;
                } else {
                    return null; // message deletion failed
                }
            } else {
                return null; // message not found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // For user be able to update Message Test
    public Message updateMessageText(int message_id, String newMessageText) {
        Connection con = ConnectionUtil.getConnection();

        try {
            // check if message_id exists in the database
            String checkMessageId = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement checkMessageStatement = con.prepareStatement(checkMessageId);
            checkMessageStatement.setInt(1, message_id);
            ResultSet rs = checkMessageStatement.executeQuery();
            if (rs.next()) {
                // Update message_text in database
                String updateMessageQuery = "UPDATE message SET message_text = ?";
                PreparedStatement updateMessageStatement = con.prepareStatement(updateMessageQuery);
                updateMessageStatement.setString(1, newMessageText);
                int affectedRows = updateMessageStatement.executeUpdate();
                if (affectedRows == 0) {
                    return null;
                }
                return new Message(message_id, rs.getInt("posted_by"), newMessageText,
                        rs.getLong("time_posted_epoch"));
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public Message getMessageById(int message_id) {
        Connection con = ConnectionUtil.getConnection();
        Message message = null;

        try {
            // Retrieve the message with the given message_id
            String query = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, message_id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return message;
    }

    // Get Message by Account_ID
    public List<Message> getMessageByAccount(int account_id) {
        Connection con = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message WHERE posted_by=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, account_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), account_id, rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return messages;
    }

}
