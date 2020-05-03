create or replace function purchaseTransaction(customId in customer.id%type, accountNo in account.accountnum%type, amount in transactions.amount%type, cardId in card.cardnum%type, vendorId in purchasetx.vendor%type, txType in number) return integer
as

noCustomer EXCEPTION;
noAccount EXCEPTION;
badAmount EXCEPTION;
badLoc EXCEPTION;
badCard EXCEPTION;
badCredit EXCEPTION;
customerCheck customer.id%type;
accountCheck loan.loannum%type;
cardCheck number;
creditSpent number;
creditLimit number;
locCheck location.locationid%type;
acctBalance account.balance%type;
txNum transactions.txnum%type;
intermediate date;
chronos number;
newBal number;
loanid number;
newPrin number;

/*1 is debit, 2 is credit*/
begin

    if(txType = 1)
    then select count(*) into accountCheck from custodian where customerid = customId and accountNum = accountNo;
        if (accountCheck != 1)
        then raise noCustomer;
        end if;

        select balance into acctBalance from account natural join custodian inner join customer on customer.id = customerid where customerId=customId and accountNum=accountNo;
            if(amount > 99999.99)
            then raise badAmount;
            end if;

        select count(*) into accountCheck from account where accountNum = accountNo and balance >= amount;
            if(accountCheck != 1)
            then raise noAccount;
            end if;

        select count(*) into cardCheck from debit_card natural join card_owner where cardnum = cardid and customerid = customId;
            if(cardCheck != 1)
            then dbms_output.put_line('issue with cardCheck'||cardCheck);
            raise badCard;
            end if;
    end if;

    if(txType = 2)
    then select count(*) into accountCheck from customer where id = customId;
      if(accountCheck != 1)
      then raise noCustomer;
      end if;

      select count(*) into cardCheck from credit_card natural join card_owner where cardnum = cardid and customerid = customId;
          if(cardCheck != 1)
          then dbms_output.put_line('issue with cardCheck'||cardCheck);
          raise badCard;
          end if;

      select creditLimit, balance into cardCheck, creditSpent from credit_card natural join loan where cardnum = cardid;
        if(creditLimit < (creditSpent + amount))
        then raise badCredit;
        end if;

      select balance, principal, loannum into acctBalance, newPrin, loanId from credit_card natural join loan where cardnum = cardid;
          if(amount > 99999.99)
          then raise badAmount;
          end if;
    dbms_output.put_line('SUCCESS');
    end if;

    select max(txNum) into txNum from transactions;

    if(txNum is null)
    then txNum := 0;
    end if;
    txNum := txNum + 1;


    select systimestamp into intermediate from dual;
    dbms_output.put_line('BAM '||intermediate);
    chronos := round(( intermediate - date '1970-01-01' ) * 60 * 60 * 24);
    dbms_output.put_line('OOH '||chronos);


    if(txType =1)
    then
        newBal := acctBalance - amount;
        insert into Transactions (txnum, amount) values (txNum, amount);
        update Account set balance = newBal where accountNum = accountNo;
        insert into purchasetx (txnum, txType, vendor) values (txNum, txType, vendorId);
    end if;

    if(txType =2)
    then
        newBal := acctBalance + amount;
        newPrin := newPrin + amount;
                dbms_output.put_line('wooh');
        insert into Transactions (txnum, amount) values (txNum, amount);

        update Loan set balance = newBal, principal = newPrin where loanNum = loanId;
        insert into purchasetx (txnum, txType, vendor) values (txNum, txType, vendorId);
    end if;

        insert into occured (txnum, locationId, utc_time) values (txNum, 0, chronos);
        insert into used_card (cardnum, txnum) values (cardid, txNum);
    commit;
    return 1;


EXCEPTION
    when noCustomer then dbms_output.put_line('No Customer For That ID'); rollback; return 0;
    when noAccount then dbms_output.put_line('No Accounts For That Customer'); rollback; return 0;
    when badAmount then dbms_output.put_line('Amount is bad'); rollback; return 0;
    when badCredit then dbms_output.put_line('Amount is bad'); rollback; return -3;
    when others then dbms_output.put_line('Fail safe'); rollback; return 0;

end;
