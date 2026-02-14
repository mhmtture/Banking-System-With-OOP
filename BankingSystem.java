import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


class BankingSystem {
    //collections where created bank accounts are kept
    public static Map<String, current_acount> currentAccounts = new HashMap<>();
    public static Map<String, Saving_Account> SavingAccounts = new HashMap<>();
    public static Map<String,Fixed_Deposit_Account> FixedDepositAccounts = new HashMap<>();



    public static ArrayList<String> readTxt(String input) {      // Function to read a text file and return its contents as a list of strings

        ArrayList<String> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static Map input_account(String input1) { //Function where accounts read from txt file are created and saved to map

        String[] input_col = input1.split(",");
        if(input_col[1].equals("Current")){
            String accountID = input_col[0];
            double balance = Double.parseDouble(input_col[2]);
            double overdraftLimit = Double.parseDouble(input_col[3]);
            current_acount c1 = new current_acount(accountID, balance, overdraftLimit);
            currentAccounts.put(accountID, new current_acount(accountID, balance, overdraftLimit));


        } else if (input_col[1].equals("Saving")) {
            String accountID = input_col[0];
            double balance = Double.parseDouble(input_col[2]);
            double interest_rate = Double.parseDouble(input_col[3]);
            double minbalance = Double.parseDouble(input_col[4]);
            Saving_Account acountID = new Saving_Account(accountID, balance, interest_rate, minbalance);
            SavingAccounts.put(accountID, new Saving_Account(accountID, balance, interest_rate,minbalance));


        } else if (input_col[1].equals("Deposit")) {
            String accountID = input_col[0];
            double balance = Double.parseDouble(input_col[2]);
            double interest_rate = Double.parseDouble(input_col[3]);
            int termInMonths = Integer.parseInt(input_col[4]);
            double penaltyRate = Double.parseDouble(input_col[5]);
            LocalDate startDate = LocalDate.parse(input_col[6]);
            Fixed_Deposit_Account acountID = new Fixed_Deposit_Account(accountID, balance, interest_rate, termInMonths, penaltyRate, startDate);
            FixedDepositAccounts.put(accountID, new Fixed_Deposit_Account(accountID, balance, interest_rate,termInMonths,penaltyRate,startDate));

        }
        return currentAccounts;
    }

    private static IOperations getAccountById(String accountID) { //Function that controls the accounts to which money will be transferred
        if (currentAccounts.containsKey(accountID)) {
            return currentAccounts.get(accountID);
        } else if (SavingAccounts.containsKey(accountID)) {
            return SavingAccounts.get(accountID);
        } else if (FixedDepositAccounts.containsKey(accountID)) {
            return FixedDepositAccounts.get(accountID);
        }
        return null;
    }
    private static void displayAccountSummaries() {
        for (current_acount account : currentAccounts.values()) {
            System.out.println("****************** Summary for Account " + account.getAccountID() + " ******************\n");
            for (String transaction : account.getTransactionHistory()) {
                System.out.println(transaction);
            }
            account.show_information();
            System.out.println("************************************************************************************************************");        }

        for (Saving_Account account : SavingAccounts.values()) {
            System.out.println("****************** Summary for Account " + account.getAccountID() + " ******************\n");

            for (String transaction : account.getTransactionHistory()) {
                System.out.println(transaction);
            }
            account.show_information();
            System.out.println("************************************************************************************************************");        }

        for (Fixed_Deposit_Account account : FixedDepositAccounts.values()) {
            System.out.println("****************** Summary for Account " + account.getAccountID() + " ******************\n");

            for (String transaction : account.getTransactionHistory()) {
                System.out.println(transaction);
            }
            account.show_information();
            System.out.println("************************************************************************************************************");
        }
    }




    public static void main(String[] args) {
        String filePath = args[0]; // accounts
        String transPath = args[1];// transfers

        // Read and parse account data from file
        ArrayList<String> fileData = readTxt(filePath);
        for (String data : fileData) {
            input_account(data);// Create account objects and store them
        }

        // Read and parse transaction data from file
        ArrayList<String> transData = readTxt(transPath);

        for (String transaction : transData) {
            String[] inputCol = transaction.split(",");
            String senderID = inputCol[0];// Sender account ID
            double amount = Double.parseDouble(inputCol[1]);// Transfer amount
            String receiverID = inputCol[2];// Receiver account ID

            // check sender and receiver account objects
            IOperations sender = getAccountById(senderID);
            IOperations receiver = getAccountById(receiverID);

            if (sender != null && receiver != null) {
                try {
                    sender.Transfer_funds(receiver, amount);//transfer money
                } catch (Exception e) {// Handle transfer errors
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Invalid account(s): Sender ID - " + senderID + ", Receiver ID - " + receiverID);
            }
        }

        // Display summaries for all account types
        displayAccountSummaries();



    }
}

// Current Account Class
class current_acount implements IOperations {
    private String accountID;
    private double balance;
    private double overdraft_limit;
    private List<String> transactionHistory;


    public current_acount(String accountID, double balance, double overdraft_limit) {
        this.accountID = accountID;
        this.balance = balance;
        this.overdraft_limit = overdraft_limit;
        this.transactionHistory = new ArrayList<>();
    }


    // Method to transfer funds from the current account to another account
    @Override
    public void Transfer_funds(IOperations targetAccount, double amount) throws Exception {

        if (amount > balance + overdraft_limit) {
            throw new Exception("Current Account: Amount exceeds overdraft limit. Amount: " + amount + " Balance: " + balance + " Limit: " + overdraft_limit);
          //  return; // Prevent transfer if the amount exceeds balance + overdraft limit
        }
        //,}
        balance -= amount; //Deduct the amount from sender's balance
        String sender_account = accountID;
        targetAccount.Deposit_money(amount,sender_account);  // Deposit money to receiver account
        String id = UUID.randomUUID().toString(); // Generate unique transaction ID

        //add to trancistion history the transaction details
        transactionHistory.add("------------------------------------");
        transactionHistory.add("Transaction UD: " + id);
        transactionHistory.add("Sender: " + accountID);
        transactionHistory.add("Reciever: " + targetAccount.getAccountID()
        );
        transactionHistory.add("Amount: -" + amount);
        transactionHistory.add("------------------------------------");
        System.out.println();
    }

    // Evaluate the account's risk
    @Override
    public void Risk_evaluation() {
        if (balance >= 0) {
            System.out.println("Current Acount-Low Risk: Account is stable.");
        } else {
            double usedOverdraft = Math.abs(balance);
            double usedPercentage = (usedOverdraft / overdraft_limit) * 100;
            if (usedPercentage >= 80) {
                System.out.println("Current Acount-High Risk: Overdraft usage close to the limit.");
            } else {
                System.out.println("Current Account-Medium Risk: Account is in overdraft.");
            }
        }
    }

    @Override
    public void transaction_history() {    // Display the transaction history of the account
        System.out.println("Transaction History for " + accountID + ":");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }

    }
   // interest function for current accounts
    @Override
    public double Interest_computation() {
        System.out.println("Current Account için faiz hesaplanmaz.");
        return 0;

    }


    // Display account information and risk evaluation
    @Override
    public void show_information() {
        System.out.println("Account info");
        System.out.println("Current Account - Account Number: " + accountID);
        System.out.println("Balance: $" + balance);
        System.out.println("Overdraft Limit: $" + overdraft_limit);
        System.out.println("\n" +
                "Account Risk Evaluation");
        Risk_evaluation();
    }

    // function for deposit money into the account
    @Override
    public void Deposit_money(double amount, String accountID_sender) {
        balance += amount;
        String id = UUID.randomUUID().toString();

        transactionHistory.add("------------------------------------");
        transactionHistory.add("Transaction UD: " + id);
        transactionHistory.add("Sender: " + accountID);
        transactionHistory.add("Reciever: " + accountID_sender);
        transactionHistory.add("Amount: " + amount);
        transactionHistory.add("------------------------------------");
    }

    // Evaluate the account's value
    public void evaluate_account_value() {
        if (balance > 5000 && overdraft_limit > 1000) {
            System.out.println("Account Value: High Value Account");
        } else {
            System.out.println("Account Value: Standard Account");
        }
    }

    public String getAccountID() {
        return accountID;
    }
    public List<String> getTransactionHistory() {
        return transactionHistory;
    }
    public double getBalance() {
        return balance;
    }
    public double getOverdraftLimit() {
        return overdraft_limit;
    }
}

// Saving Account Class
class Saving_Account implements IOperations {
    private String accountID;
    private double balance;
    private double interest_rate;
    private double minBalance;
    private List<String> transactionHistory;
    private int transactionCount;

    public Saving_Account(String accountID, double balance, double interest_rate, double minBalance) {
        this.accountID = accountID;
        this.balance = balance;
        this.interest_rate = interest_rate;
        this.minBalance = minBalance;
        this.transactionHistory = new ArrayList<>();
        this.transactionCount = 0;
    }


    // Method to transfer funds from the Saving account to another account
    @Override
    public void Transfer_funds(IOperations targetAccount, double amount) throws Exception {
        if (amount > balance + minBalance) {
            throw new Exception("Saving Account: Amount exceeds overdraft limit. Amount: " + amount + " Balance: " + balance + " Limit: " + minBalance);
        }

        double remainingBalance = balance - amount;

        //Apply penalties if not meeting minimum balance requirement
        if (remainingBalance < minBalance) {
            double penalty = (minBalance - remainingBalance) * 0.05; // %5 penalty
            balance = remainingBalance - penalty;
        } else {
            balance -= amount;
        }
        targetAccount.Deposit_money(amount,accountID); // We deposit money to the target account
        transactionCount++; //Increase number of transactions
        String id = UUID.randomUUID().toString();

        //add the transaction details

        transactionHistory.add("------------------------------------");
        transactionHistory.add("Transaction UD: " + id);
        transactionHistory.add("Sender: " + accountID);
        transactionHistory.add("Reciever: " + targetAccount.getAccountID()
        );
        transactionHistory.add("Amount: -" + amount);
        transactionHistory.add("------------------------------------");
        System.out.println();    }

    // Evaluate the account's risk
    @Override
    public void Risk_evaluation() {
        if (balance < 1.2 * minBalance) {
            System.out.println("Saving Account-High Risk: Overdraft usage close to the limit.");
        } else if (transactionCount > 10) {
            System.out.println("Saving Account-Medium Risk: Account is in overdraft.");
        } else {
            System.out.println("Saving Account-Low Risk: Account is stable.");
        }
    }

    // Display the transaction history
    @Override
    public void transaction_history() {
        System.out.println("Transaction History for Saving Account " + accountID + ":");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    // interest function
    @Override
    public double Interest_computation() {
        double interest = balance*interest_rate;
        return interest_rate;
    }

    // Display account information and risk evaluation
    @Override
    public void show_information() {
        System.out.println("Account Info");
        System.out.println("Saving Account - Account ID: " + accountID);
        System.out.println("Balance: $" + balance);
        System.out.println("Interest Rate: " + (Interest_computation() * 100) + "%");
        System.out.println("\nAccount Risk Evaluation");
        Risk_evaluation();        // Risk değerlendirmesi
    }

    // Method to deposit money into the account
    @Override
    public void Deposit_money(double amount, String senderId) {
        balance += amount;
        String id = UUID.randomUUID().toString();

        transactionHistory.add("------------------------------------");
        transactionHistory.add("Transaction UD: " + id);
        transactionHistory.add("Sender: " + accountID);
        transactionHistory.add("Reciever: " + senderId);
        transactionHistory.add("Amount: " + amount);
        transactionHistory.add("------------------------------------");    }

    // Evaluate the account's value
    public void evaluate_account_value() {
        if (balance > 10000) {
            System.out.println("Account Value: High Value Account ");
        } else {
            System.out.println("Account Value: Standard Account");
        }
    }

    public String getAccountID() {
        return accountID;
    }
    public List<String> getTransactionHistory(){
        return transactionHistory;
    }

    public double getBalance() {
        return balance;
    }

    public double getInterest_rate() {
        return interest_rate;
    }

}

// Fixed Deposit Account Class
class Fixed_Deposit_Account implements IOperations {
    private String accountID;
    private double balance;
    private double interest_rate;
    private int termInMonths;
    private LocalDate startDate;
    private double penaltyRate;
    private List<String> transactionHistory;
    private LocalDate maturityDate; // Vade tarihi


    public Fixed_Deposit_Account(String accountID, double balance, double interest_rate, int termInMonths, double penaltyRate, LocalDate startDate) {
        this.accountID = accountID;
        this.balance = balance;
        this.interest_rate = interest_rate;
        this.termInMonths = termInMonths;
        this.penaltyRate = penaltyRate;
        this.startDate = startDate;
        this.transactionHistory = new ArrayList<>();
        this.maturityDate = startDate.plusMonths(termInMonths);

    }


    @Override
    public void Transfer_funds(IOperations targetAccount, double amount) throws Exception {

        String id = UUID.randomUUID().toString();

        if (amount > balance) {
            throw new Exception("Fixed Deposit Account: Amount exceeds overdraft limit. Amount: " + amount + " Balance: " + balance + " Limit: " + balance);
        }

        long daysToMaturity = ChronoUnit.DAYS.between(LocalDate.now(), maturityDate);

        if (daysToMaturity > 0) { // Vade dolmamışsa
            double penalty = amount * penaltyRate;
            if (amount + penalty > balance) {
                throw new Exception("Fixed Deposit Account: Insufficient funds including penalty charges. Amount: " + amount + " Penalty: " + penalty + " Balance: " + balance);
            }
            balance -= (amount + penalty);
            targetAccount.Deposit_money(amount, accountID);

            //transaction details
            transactionHistory.add("------------------------------------");
            transactionHistory.add("Transaction UD: " + id);
            transactionHistory.add("Sender: " + accountID);
            transactionHistory.add("Reciever: " + targetAccount.getAccountID());
            transactionHistory.add("Amount: -" + amount);
            transactionHistory.add("------------------------------------");
            System.out.println();

        } else { // Vade dolmuşsa
            balance -= amount;
            targetAccount.Deposit_money(amount,accountID);
            transactionHistory.add("------------------------------------");
            transactionHistory.add("Transaction UD: " + id);
            transactionHistory.add("Sender: " + accountID);
            transactionHistory.add("Reciever: " + targetAccount.getAccountID());
            transactionHistory.add("Amount: -" + amount);
            transactionHistory.add("------------------------------------");
            System.out.println();
        }
    }
   /*
        String id = UUID.randomUUID().toString();
        LocalDate maturityDate = startDate.plusMonths(termInMonths);
        if (LocalDate.now().isBefore(maturityDate)) {
            //Apply penalty for account not being due
            double penalty = amount * penaltyRate;
            if (amount + penalty > balance) {
                throw new Exception("Fixed Deposit Account: Insufficient funds including penalty charges. Amount: " + amount + " Penalty: " + penalty + "Balance: " + balance);
            }
            balance -= (amount + penalty);// Deduct the amount from sender's balance
            targetAccount.Deposit_money(amount,accountID);

            //transaction details
            transactionHistory.add("------------------------------------");
            transactionHistory.add("Transaction UD: " + id);
            transactionHistory.add("Sender: " + accountID);
            transactionHistory.add("Reciever: " + targetAccount.getAccountID()
            );
            transactionHistory.add("Amount: -" + amount);
            transactionHistory.add("------------------------------------");
            System.out.println();        } else {
            if (amount > balance) {
                throw new Exception("Fixed Deposit Account: Amount exceeds overdraft limit. Amount: " + amount + " Balance: " + balance + " Limit: "+ balance);
            }
            balance -= amount;
            targetAccount.Deposit_money(amount,accountID);

            transactionHistory.add("------------------------------------");
            transactionHistory.add("Transaction UD: " + id);
            transactionHistory.add("Sender: " + accountID);
            transactionHistory.add("Reciever: " + targetAccount.getAccountID()
            );
            transactionHistory.add("Amount: -" + amount);
            transactionHistory.add("------------------------------------");
            System.out.println();
              }
    */




    // Evaluate the account's risk
    @Override
    public void Risk_evaluation() {
        LocalDate maturityDate = startDate.plusMonths(termInMonths);
        long daysToMaturity = ChronoUnit.DAYS.between(LocalDate.now(), maturityDate);

        if (daysToMaturity > 30) {
            System.out.println("Saving Account-Low Risk: Account is stable.");
        } else if (daysToMaturity > 0 && daysToMaturity <= 30) {
            System.out.println("Saving Account-Medium Risk: Account is in overdraft.");
        } else if (balance > 0) {
            System.out.println("Saving Account-High Risk: Overdraft usage close to the limit.");
        }
    }

    // Display the transaction history
    @Override
    public void transaction_history() {
        System.out.println("Transaction History for Account " + accountID + ":");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    // interest computation
    @Override
    public double Interest_computation() {
        LocalDate maturityDate = startDate.plusMonths(termInMonths);
        long daysToMaturity = ChronoUnit.DAYS.between(LocalDate.now(), maturityDate);

        if (daysToMaturity <= 0) {
            return balance * interest_rate * (termInMonths / 12.0);
        }
        return balance * interest_rate * (daysToMaturity / 365.0);
    }

    // Display account information and risk evaluation
    @Override
    public void show_information() {
        System.out.println("Account Info");
        System.out.println("Fixed Deposit Account - Account Number: " + accountID);
        System.out.println("Balance: $" + balance);
        System.out.println("Interest Rate: " + interest_rate * 100 + "%");
        System.out.println("Maturity Date: " + maturityDate);
        if (LocalDate.now().isAfter(maturityDate)) {
            System.out.println("Status: Matured");
        } else {
            System.out.println("Status: Active");
        }
        System.out.println("\n" +
                "Account Risk Evaluation");
        Risk_evaluation();
    }

    // Method to deposit money into the account
    @Override
    public void Deposit_money(double amount, String senderId) {
        balance += amount;
        String id = UUID.randomUUID().toString();

        transactionHistory.add("------------------------------------");
        transactionHistory.add("Transaction UD: " + id);
        transactionHistory.add("Sender: " + accountID);
        transactionHistory.add("Reciever: " + senderId);
        transactionHistory.add("Amount: " + amount);
        transactionHistory.add("------------------------------------");

    }
    public String getAccountID() {
        return accountID;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public double getInterest_rate() {
        return interest_rate;
    }

    public double getBalance() {
        return balance;
    }

    public int getTermInMonths() {
        return termInMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}


