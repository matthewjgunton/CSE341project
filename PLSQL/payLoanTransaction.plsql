create or replace function payLoanTransaction(customId in customer.id%type, accountNo in account.accountnum%type, amount in transactions.amount%type, locId in location.locationid%type, loanNo in loan.loannum%type, cardId in card.cardnum%type) return integer
as

noCustomer EXCEPTION;
noAccount EXCEPTION;
badAmount EXCEPTION;
badLoc EXCEPTION;
badCard EXCEPTION;
customerCheck customer.id%type;
loanCheck number;
amoont transactions.amount%type;
accountCheck loan.loannum%type;
cardCheck number;
locCheck location.locationid%type;
acctBalance account.balance%type;
txNum transactions.txnum%type;
intermediate date;
newLoanPaymentDue loan.paymentdue%type;
newLoanBalance loan.balance%type;
chronos number;
newBal number;


begin
    select count(*) into customerCheck from customer where id = customId;
        if (customerCheck != 1)
        then raise noCustomer;
        end if;

    select count(*) into accountCheck from custodian where customerid = customId and accountNum = accountNo;
        if(accountCheck != 1)
        then raise noAccount;
        end if;

    select count(*) into locCheck from location where locationid = locId;
        if(locCheck != 1)
        then raise badLoc;
        end if;

    select count(*) into loanCheck from loan where loannum = loanNo;
        if(loanCheck != 1)
        then raise badLoc;
        end if;

    if(cardid != -1)
    then dbms_output.put_line('reached'||cardid);
    select count(*) into cardCheck from card_owner natural join debit_card where cardnum = cardid and customerid = customId;
        if(cardCheck != 1)
        then dbms_output.put_line('issue with cardCheck'||cardCheck);
        raise badCard;
        end if;
    end if;

    select balance into acctBalance from account natural join custodian inner join customer on customer.id = customerid where customerId=customId and accountNum=accountNo;
        if(acctBalance < amount)
        then raise badAmount;
        end if;
        if(amount > 99999.99)
        then raise badAmount;
        end if;

    select max(txNum) into txNum from transactions;
    if(txNum is null)
    then txNum:= 0;
    end if;


    txNum := txNum + 1;

    amoont := amount;

    select paymentdue, balance into newLoanPaymentDue, newLoanBalance from loan where loannum = loanNo;
    if(newLoanBalance = 0)
    then raise badAmount;
    end if;

    newLoanPaymentDue := newLoanPaymentDue - amount;
    if(newLoanPaymentDue < 0)
    then newLoanPaymentDue := 0;
    end if;

    newLoanBalance := newLoanBalance - amount;
    if(newLoanBalance < 0)
    then amoont := amount - newLoanBalance;
    newLoanBalance := 0;
    end if;


    select systimestamp into intermediate from dual;
    dbms_output.put_line('BAM '||intermediate);
    chronos := round(( intermediate - date '1970-01-01' ) * 60 * 60 * 24);
    dbms_output.put_line('OOH '||chronos);


        newBal := acctBalance - amoont;
        dbms_output.put_line('WIN'||newBal);
        insert into Transactions (txnum, amount) values (txNum, amoont);
        insert into withdrawaltx (txNum, accountnum) values (txNum, accountNo);
        insert into occured (txnum, locationid, utc_time) values (txNum, locId, chronos);
        update Loan set balance = newLoanBalance, paymentdue = newLoanPaymentDue where loanNum = loanNo;
        update Account set balance = newBal where accountNum = accountNo;


    if(cardid != -1)
        then insert into used_card (cardnum, txnum) values (cardid, txNum);
    end if;
    commit;
    return 1;


EXCEPTION
    when noCustomer then dbms_output.put_line('No Customer For That ID'); rollback; return 0;
    when noAccount then dbms_output.put_line('No Accounts For That Customer'); rollback; return 0;
    when badAmount then dbms_output.put_line('Amount is bad'); rollback; return 0;
    when others then dbms_output.put_line('Fail safe'); rollback; return 0;

end;
