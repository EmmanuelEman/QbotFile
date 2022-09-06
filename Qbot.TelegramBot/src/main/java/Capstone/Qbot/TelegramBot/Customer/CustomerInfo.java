package Capstone.Qbot.TelegramBot.Customer;

import org.springframework.context.annotation.Configuration;


@Configuration
public class CustomerInfo {

    private String firstName;
    private String lastName;
    private String transactionType;
    private int phoneNumber;

    public CustomerInfo(String firstName, String lastName, String transactionType, int phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.transactionType = transactionType;
        this.phoneNumber = phoneNumber;
    }

    public CustomerInfo() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String setTransactionType(String transactionType) {
        this.transactionType = transactionType;
        return transactionType;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "CustomerInfo{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }
}
