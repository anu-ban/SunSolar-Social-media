package Service;

import Model.Message;

import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;

public class MessageService {
    MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    public List<Message> getAllMessage() {
        try {
            return messageDAO.getAllMessage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Message createMessage(Message message) {
        // Call the createMessage method in the MessageDAO class
        // MessageDAO messageDAO = new MessageDAO();
        return messageDAO.createMessage(message);
    }

    // delete message service
    public Message deleteMessage(int messageId) {
        return messageDAO.deleteMessage(messageId);
    }

    // update message service
    public Message updateMessageText(int message_id, String newMessage) {
        Message updateResult = messageDAO.updateMessageText(message_id, newMessage);
        return updateResult;
    }

    public List<Message> getMessagesByAccount(int account_id) {
        List<Message> messages = messageDAO.getMessageByAccount(account_id);
        return messages;
    }

    public Message getMessagesByMessageId(int message_id) {
        Message messages = messageDAO.getMessageById(message_id);
        return messages;
    }

}
