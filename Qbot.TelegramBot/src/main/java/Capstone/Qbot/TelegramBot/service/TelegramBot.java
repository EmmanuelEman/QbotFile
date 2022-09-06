package Capstone.Qbot.TelegramBot.service;

import Capstone.Qbot.TelegramBot.Constant.ConstantVariable;
import Capstone.Qbot.TelegramBot.Customer.CustomerInfo;
import Capstone.Qbot.TelegramBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot{

    private BotConfig config;

    public CustomerInfo customerInfo;

    private ConstantVariable constant;


    public String botLastState;
    @Autowired
    public TelegramBot(BotConfig config, CustomerInfo customerInfo){
        this.config = config;
        this.customerInfo = customerInfo;
    }

    public TelegramBot(){

    }

    @Override
    public void onUpdateReceived(Update update) {
//       log.info(update.getMessage().getText());
        if(update.hasMessage() && update.getMessage().hasText()){

            Message message = update.getMessage();
            String text = message.getText();
            long chatID = update.getMessage().getChatId();

                if(text.equals(ConstantVariable.START)){
                    startCommandReceived(chatID, update.getMessage().getChat().getFirstName());
                }
            if(!text.equals(ConstantVariable.START)) {
                if(getBotLastState().equals(ConstantVariable.FIRST_NAME)){
                    String customerFirstName = message.getText();
                    customerInfo.setFirstName(customerFirstName);
                    log.info(ConstantVariable.FIRST_NAME +  customerFirstName);
                    setBotLastState(ConstantVariable.LAST_NAME);
                    addLastName(update.getMessage().getChatId());
                }else if(getBotLastState().equals(ConstantVariable.LAST_NAME)){
                    Message newMessage = update.getMessage();
                    String customerLastName = newMessage.getText();
                    customerInfo.setFirstName(customerLastName);
                    log.info(ConstantVariable.LAST_NAME +  customerLastName);
                    setBotLastState(ConstantVariable.CLEAR);
                }
            }

        }else if(update.hasCallbackQuery()){
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(ConstantVariable.GREETINGS+ update.getCallbackQuery().getData());
                String confirmation = update.getCallbackQuery().getData();
                sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
                execute(sendMessage);
                if(confirmation.equals(ConstantVariable.REGISTER)){
                    transaction(update.getCallbackQuery().getMessage().getChatId());
                }else if(update.hasCallbackQuery()){
                    setBotLastState(ConstantVariable.FIRST_NAME);
                    SendMessage returnMessage = new SendMessage();
                    returnMessage.setText(ConstantVariable.GREETINGS + update.getCallbackQuery().getData());
                    String transactionSet = update.getCallbackQuery().getData();
                    log.info("Customer transaction type " + transactionSet);
                    customerInfo.setTransactionType(transactionSet);
                    addFirstName(update.getCallbackQuery().getMessage().getChatId());

                }

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }
    public synchronized void addFirstName(long chatId) throws TelegramApiException {
        SendMessage sendFirstName = new SendMessage();
        sendFirstName.setText(ConstantVariable.FIRST_NAME);
        sendFirstName.enableMarkdown(true);
        sendFirstName.setChatId(chatId);
        execute(sendFirstName);
        log.info(ConstantVariable.CHAT_ID + chatId);
    }
    public synchronized void addLastName(long chatId){

        SendMessage sendLastName = new SendMessage();
        sendLastName.setText(ConstantVariable.LAST_NAME);
        sendLastName.enableMarkdown(true);
        sendLastName.setChatId(chatId);
        try {
            execute(sendLastName);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isStringFirstName(String firstName){
        char[] chars = firstName.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    private void startCommandReceived(long chatID, String name){
        String messageToUser = " Hi, " + name +", Good day! Welcome to QBot\nin order to proceed please click the register button";
        sendMessage(chatID,messageToUser);

    }
    public void transaction(Long chatId) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText("Show button here: ");

                sendMessage.setText("Choose your transaction");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText("Withdrawal");
                inlineKeyboardButton2.setText("Deposit");
                inlineKeyboardButton1.setCallbackData("Withdrawal");
                inlineKeyboardButton2.setCallbackData("Deposit");
                List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
                keyboardButtonsRow1.add(inlineKeyboardButton1);
                keyboardButtonsRow2.add(inlineKeyboardButton2);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardButtonsRow1);
                rowList.add(keyboardButtonsRow2);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                execute(sendMessage);


        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    private void sendMessage(long chatID, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatID));
        message.setText(textToSend);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(ConstantVariable.REGISTER);
        inlineKeyboardButton.setCallbackData(ConstantVariable.REGISTER);
        inlineKeyboardButtonList.add(inlineKeyboardButton);
        inlineButtons.add(inlineKeyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try{
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred " + e.getMessage());
        }

    }
    public String getBotLastState() {
        return botLastState;
    }

    public void setBotLastState(String botLastState) {
        this.botLastState = botLastState;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

}

