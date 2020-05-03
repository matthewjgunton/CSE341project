create or replace function createBasicTransaction(customId in customer.id%type, accountNo in account.accountnum%type, amount in transactions.amount%type, txType in number, locId in location.locationid%type, cardId in card.cardnum%type) return integer
as

noCustomer EXCEPTION;
noAccount EXCEPTION;
badAmount EXCEPTION;
badLoc EXCEPTION;
badCard EXCEPTION;
customerCheck customer.id%type;
accountCheck loan.loannum%type;
cardCheck number;
checkingCheck number;
penal number;
hold number;
locCheck location.locationid%type;
acctBalance account.balance%type;
txNum transactions.txnum%type;
intermediate date;
chronos number;
newBal number;

/*1 is deposit, 2 is withdrawal*/
begin

    select count(*) into accountCheck from custodian where customerid = customId and accountNum = accountNo;
        if (customerCheck != 1)
        then raise noCustomer;
        end if;

    select count(*) into accountCheck from account where accountNum = accountNo;
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
        if(acctBalance < amount AND txType = 2)
        then raise badAmount;
        end if;
        if(amount > 99999.99)
        then raise badAmount;
        end if;
        if(acctBalance is null)
        then raise badAmount;
        end if;

    penal := 0;
    select count(*) into checkingCheck from checking where accountNum = accountNo;
    if(checkingCheck = 1)
        then select penalty into penal from checking where accountNum = accountno;
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

    if(txType = 1)
        then dbms_output.put_line('new ');
        insert into Transactions (txnum, amount) values (txNum, amount);
        insert into deposittx (txnum, accountNum) values (txNum, accountNo);
        insert into occured (txnum, locationid, utc_time) values (txNum, locId, chronos);
        hold := amount - penal;
        dbms_output.put_line('BAM '||acctBalance||' - '||hold);
        newBal := acctBalance + hold;
        if(hold < 0)
        then newBal := acctBalance;
        end if;
        dbms_output.put_line('OK >'||newBal);
        update Account set balance = newBal where accountNum = accountNo;
        if(checkingCheck = 1)
        then penal := penal - amount;
            if(penal < 0)
            then penal := 0;
            end if;
        update Checking set penalty = penal where accountnum = accountno;
        end if;
    end if;
    
    if(txType = 2)
        then newBal := acctBalance - amount;
        insert into Transactions (txnum, amount) values (txNum, amount);
        insert into withdrawaltx (txnum, accountnum) values (txNum, accountNo);
        insert into occured (txnum, locationid, utc_time) values (txNum, locId, chronos);
        update Account set balance = newBal where accountNum = accountNo;
        commit;
    end if;

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

