create or replace function openAccount(customId in customer.id%type, acctType in number, initBalance in account.balance%type, rate in account.interestrate%type, locationId in occured.locationid%type) return integer
as

accountNo account.accountnum%type;
badAmount exception;
customerCheck number;
intermediate date;
chronos number;
txNum number;


begin
    select count(*) into customerCheck from customer where id = customId;
        if (customerCheck != 1)
        then raise badAmount;
        end if;

    select max(accountnum) into accountNo from account;

    if(initBalance < 1500 and acctType = 1)
    then raise badAmount;
    end if;

    if(initBalance < 0)
    then raise badAmount;
    end if;

    select systimestamp into intermediate from dual;
    dbms_output.put_line('BAM '||intermediate);
    chronos := round(( intermediate - date '1970-01-01' ) * 60 * 60 * 24);
    dbms_output.put_line('OOH '||chronos);

    select max(txNum) into txNum from transactions;
                dbms_output.put_line('MADE IT '||txnum);
    if(txNum is null)
    then txNum := 0;
    end if;
    txNum := txNum + 1;
    accountNo := accountNo + 1;

    insert into Account (accountNum, balance, interestRate) values (accountNo, initBalance, rate);
    insert into Custodian (customerId, accountNum) values (customId, accountNo);
    insert into Transactions (txnum, amount) values (txNum, initBalance);
                dbms_output.put_line('MADE IT '||chronos);
    insert into deposittx (txnum, accountnum) values (txNum, accountNo);
    insert into occured (txnum, locationId, utc_time) values (txNum, locationId, chronos);
    if(acctType = 1)
    then insert into Checking (accountNum, penalty, minimumBalance) values (accountNo, 0, 1500);
    end if;

    commit;
    return 1;


EXCEPTION
    when badAmount then dbms_output.put_line('Amount is bad'); rollback; return 0;
    when others then dbms_output.put_line('Fail safe'); rollback; return 0;

end;
