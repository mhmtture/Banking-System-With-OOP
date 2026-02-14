public interface IOperations {
    void Transfer_funds(IOperations targetAccount, double amount) throws Exception;
    void Risk_evaluation();
    void transaction_history();
    double Interest_computation();
    void show_information();
    void Deposit_money(double amount, String senderId);
    String getAccountID();

}

