package Controller;

import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

import org.h2.util.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();

    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("/account", this::getAllUserAccountHandeler);
        app.post("/register", this::createAccountHandler);
        app.post("/login", this::loginHandler);
        app.get("/messages", this::getAllMessageHandler);
        app.post("/messages", this::createMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageTextHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountHandler);
        app.get("/messages/{message_id}", this::getAllMessagesByMessageId);

        return app;
    }

    private void createMessageHandler(Context ctx) {
        // Parse JSON request body into Message object
        Message message = ctx.bodyAsClass(Message.class);

        // Check if message_text is valid
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() >= 255) {
            ctx.status(400);
            return;
        }

        // Create new message and persist to database
        Message newMessage = messageService.createMessage(message);
        if (newMessage == null) {
            ctx.status(400);
            return;
        }

        // Return new message with message_id
        ctx.json(newMessage);

    }

    /**
     * This is an example handler for an example endpoint.
     * 
     * @param ctx The Javalin Context object manages information about both the
     *            HTTP request and response.
     */
    private void getAllUserAccountHandeler(Context ctx) {
        ctx.json(accountService.getAllUserAccount());
    }

    private void createAccountHandler(Context ctx) {
        // Parse JSON request body into Account object
        Account account = ctx.bodyAsClass(Account.class);

        // Check if username and password are valid
        if (account.getUsername().isEmpty() || account.getPassword().length() < 4) {
            ctx.status(400);
            return;
        }

        // Check if account with same username already exists
        else if (accountService.getAccountByUsername(account.getUsername()) != null) {
            ctx.status(400);
            return;
        }

        // Create new account and persist to database
        Account newAccount = accountService.createAccount(account);
        ctx.json(newAccount);
    }

    private void loginHandler(Context ctx) {
        // Parse JSON request body into Account object
        Account account = ctx.bodyAsClass(Account.class);
        Account existingAccount = accountService.getAccountByUsernameAndPassword(account.getUsername(),
                account.getPassword());

        // Check if username and password are provided
        if (account.getUsername().isEmpty() || account.getPassword().isEmpty()) {
            ctx.status(401);
            return;
        }

        // Check if account with given username and password exists

        else if (existingAccount == null) {
            ctx.status(401);
            return;
        }

        // Login successful, return account object
        else {
            ctx.json(existingAccount);
            // return;
        }
    }

    private void getAllMessageHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessage();
        ctx.status(200);
        ctx.json(messages);

    }

    private void deleteMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessage(messageId);
        if (deletedMessage == null) {
            ctx.status(200);
            ctx.result("");
            return;
        } else {
            ctx.status(200);
            ctx.json(deletedMessage);
            return;
        }
    }

    private void updateMessageTextHandler(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        String requestBody = ctx.body();
        try {
            // Parse the request body as a JSON object
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            String newMessageText = jsonNode.get("message_text").asText();
            if (newMessageText.trim().isEmpty()) {
                ctx.status(400);
                return;
            }
            if (newMessageText.length() > 250) {
                ctx.status(400);
                return;
            }

            // Update the message in the database
            Message updatedMessage = messageService.updateMessageText(message_id, newMessageText);
            if (updatedMessage == null) {
                ctx.status(400);
                return;
            } else {
                ctx.status(200);
                ctx.json(updatedMessage);
            }
        } catch (JsonProcessingException e) {
            ctx.status(400);
        }
    }

    private void getMessagesByAccountHandler(Context ctx) {
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        // Retrieve all messages posted by the user with the given account_id
        List<Message> messages = messageService.getMessagesByAccount(account_id);
        ctx.json(messages);

    }

    private void getAllMessagesByMessageId(Context ctx) {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        // Retrive the message with the given message_id
        Message messages = messageService.getMessagesByMessageId(message_id);

        if (messages == null) {
            ctx.status(200);
            return;
        } else {
            ctx.status();
            ctx.json(messages);
        }
    }

}