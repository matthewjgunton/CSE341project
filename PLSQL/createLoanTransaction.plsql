create or replace function createLoanTransaction(customId in customer.id%type, accountNo in account.accountnum%type, amount in transactions.amount%type, locId in location.locationid%type, rate in loan.interest%type, cardId in card.cardnum%type, assetno in asset.assetnum%type) return integer
as

noCustomer EXCEPTION;
noAccount EXCEPTION;
badAmount EXCEPTION;
badLoc EXCEPTION;
badCard EXCEPTION;
customerCheck customer.id%type;
accountCheck loan.loannum%type;
cardCheck number;
locCheck location.locationid%type;
acctBalance account.balance%type;
txNum transactions.txnum%type;
loanNum loan.loannum%type;
intermediate date;
paymentdue loan.paymentdue%type;
chronos number;
newBal number;


begin
    select count(*) into customerCheck from customer where id = customId;
        if (customerCheck != 1)
        then raise noCustomer;
        end if;

    if(assetno != -1)
    then select count(*) into customerCheck from owns where customerId = customId and assetnum = assetno;
      if(customerCheck != 1)
      then raise noCustomer;
      end if;
    end if;

    select count(*) into accountCheck from custodian where customerid = customId and accountNum = accountNo;
        if(accountCheck != 1)
        then raise noAccount;
        end if;

    select count(*) into locCheck from location where locationid = locId;
        if(locCheck != 1)
        then raise badLoc;
        end if;

    if(cardid != -1)
    then dbms_output.put_line('reached'||cardid);
    select count(*) into cardCheck from card_owner where cardnum = cardid and customerid = customId;
        if(cardCheck != 1)
        then dbms_output.put_line('issue with cardCheck'||cardCheck);
        raise badCard;
        end if;
    end if;

    select balance into acctBalance from account natural join custodian inner join customer on customer.id = customerid where customerId=customId and accountNum=accountNo;
        if(amount > 99999.99)
        then raise badAmount;
        end if;

    select max(txNum) into txNum from transactions;
    select max(loanNum) into loanNum from loan;

    if(txNum is null)
    then txNum := 0;
    end if;

    if(loanNum is null)
    then loanNum := 0;
    end if;

    txNum := txNum + 1;
    loanNum := loanNum + 1;

    if(rate > 1000)
    then raise badAmount;
    end if;

    paymentDue:= (amount * (rate / 1200));

    select systimestamp into intermediate from dual;
    dbms_output.put_line('BAM '||intermediate);
    chronos := round(( intermediate - date '1970-01-01' ) * 60 * 60 * 24);
    dbms_output.put_line('OOH '||chronos);


        newBal := acctBalance + amount;
        dbms_output.put_line('WIN'||newBal);
        insert into Transactions (txnum, amount) values (txNum, amount);
        dbms_output.put_line('WIN2'||newBal);
        insert into Loan (loanNum, balance, interest, paymentDue, principal) values (loanNum, amount, rate, paymentDue, amount);
        insert into Owes (customerId, loanNum) values (customId, loanNum);
        if(assetno != -1)
        then insert into is_secured (loannum, assetnum) values (loanNum, assetno);
        end if;
        insert into loantx (txnum, accountnum, loanNum) values (txNum, accountNo, loanNum);
        insert into occured (txnum, locationid, utc_time) values (txNum, locId, chronos);
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
