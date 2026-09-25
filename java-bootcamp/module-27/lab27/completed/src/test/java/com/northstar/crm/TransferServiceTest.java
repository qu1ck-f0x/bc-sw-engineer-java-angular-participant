package com.northstar.crm;

import com.northstar.crm.account.Account;
import com.northstar.crm.account.AccountRepository;
import com.northstar.crm.account.TransactionLog;
import com.northstar.crm.account.TransactionLogRepository;
import com.northstar.crm.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransferServiceTest {
  @Autowired TransferService transferService;
  @Autowired AccountRepository accounts;
  @Autowired TransactionLogRepository logs;

  @BeforeEach
  void resetLedger() {
    logs.deleteAll();
    accounts.saveAndFlush(new Account("ACC-MAIN-1001", "CUS-1001", "MAIN", new BigDecimal("1000.00")));
    accounts.saveAndFlush(new Account("ACC-LOYALTY-1001", "CUS-1001", "LOYALTY", new BigDecimal("50.00")));
  }

  @Test
  void forceFailRollsBack() {
    // Finished prompt: check both balances and the log after a flushed debit rolls back.
    IllegalStateException error = assertThrows(IllegalStateException.class, () ->
        transferService.transfer("ACC-MAIN-1001", "ACC-FORCE-FAIL", new BigDecimal("10.00")));
    assertEquals("Forced transfer failure for rollback demo", error.getMessage());
    assertLedger("1000.00", "50.00", 0);
  }

  @Test
  void happyPathMovesFunds() {
    transferService.transfer("ACC-MAIN-1001", "ACC-LOYALTY-1001", new BigDecimal("5.00"));
    assertLedger("995.00", "55.00", 1);
    TransactionLog log = logs.findAll().get(0);
    assertEquals("ACC-MAIN-1001", log.getFromAccountId());
    assertEquals("ACC-LOYALTY-1001", log.getToAccountId());
    assertEquals(0, log.getAmount().compareTo(new BigDecimal("5.00")));
  }

  @Test
  void missingDestinationRollsBackDebit() {
    assertThrows(IllegalArgumentException.class, () ->
        transferService.transfer("ACC-MAIN-1001", "ACC-UNKNOWN", new BigDecimal("10.00")));
    assertLedger("1000.00", "50.00", 0);
  }

  @Test
  void insufficientFundsDoNotChangeLedger() {
    assertThrows(IllegalArgumentException.class, () ->
        transferService.transfer("ACC-MAIN-1001", "ACC-LOYALTY-1001", new BigDecimal("1001.00")));
    assertLedger("1000.00", "50.00", 0);
  }

  @Test
  void invalidAmountsDoNotChangeLedger() {
    for (String amount : new String[] {"0.00", "-1.00"}) {
      assertThrows(IllegalArgumentException.class, () ->
          transferService.transfer("ACC-MAIN-1001", "ACC-LOYALTY-1001", new BigDecimal(amount)));
    }
    assertLedger("1000.00", "50.00", 0);
  }

  private void assertLedger(String main, String loyalty, long logCount) {
    assertEquals(0, accounts.findById("ACC-MAIN-1001").orElseThrow().getBalance()
        .compareTo(new BigDecimal(main)));
    assertEquals(0, accounts.findById("ACC-LOYALTY-1001").orElseThrow().getBalance()
        .compareTo(new BigDecimal(loyalty)));
    assertEquals(logCount, logs.count());
  }
}
