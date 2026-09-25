package com.northstar.crm.service;

import com.northstar.crm.account.Account;
import com.northstar.crm.account.AccountRepository;
import com.northstar.crm.account.TransactionLog;
import com.northstar.crm.account.TransactionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
  private final AccountRepository accountRepository;
  private final TransactionLogRepository transactionLogRepository;

  public TransferService(AccountRepository accountRepository,
                         TransactionLogRepository transactionLogRepository) {
    this.accountRepository = accountRepository;
    this.transactionLogRepository = transactionLogRepository;
  }

  // Finished prompt: debit, credit, and log commit or roll back together.
  @Transactional
  public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
    if (amount == null || amount.signum() <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (fromAccountId == null || toAccountId == null || fromAccountId.equals(toAccountId)) {
      throw new IllegalArgumentException("Source and destination must differ");
    }
    Account from = accountRepository.findById(fromAccountId)
        .orElseThrow(() -> new IllegalArgumentException("Unknown from account"));
    if (from.getBalance().compareTo(amount) < 0) {
      throw new IllegalArgumentException("Insufficient funds");
    }
    // Finished prompt: flush the debit before the synthetic failure to prove rollback.
    from.setBalance(from.getBalance().subtract(amount));
    accountRepository.saveAndFlush(from);
    if ("ACC-FORCE-FAIL".equals(toAccountId)) {
      throw new IllegalStateException("Forced transfer failure for rollback demo");
    }
    Account to = accountRepository.findById(toAccountId)
        .orElseThrow(() -> new IllegalArgumentException("Unknown to account"));

    // Finished prompt: credit the destination and write the log in this transaction.
    to.setBalance(to.getBalance().add(amount));
    accountRepository.save(to);
    TransactionLog log = new TransactionLog();
    log.setFromAccountId(fromAccountId);
    log.setToAccountId(toAccountId);
    log.setAmount(amount);
    transactionLogRepository.save(log);
  }
}
