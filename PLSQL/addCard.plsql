create or replace function addCard(customId in customer.id%type, acctno in account.accountnum%type, creditLimit in credit_card.creditlimit%type, txType in number) return integer
as

loanNo loan.loannum%type;
badAmount exception;
customerCheck number;
cardno card.cardnum%type;


begin
    select count(*) into customerCheck from customer where id = customId;
        if (customerCheck != 1)
        then raise badAmount;
        end if;

    select max(cardnum) into cardno from card;
    cardno := cardno + 1;

    if(txType = 1)
    then select count(*) into customerCheck from custodian where customerid = customId and accountnum = acctno;
      if(customerCheck != 1)
      then raise badAmount;
      end if;
    end if;

    insert into Card (cardNum) values (cardNo);
    insert into Card_Owner (cardNum, customerId) values (cardNo, customId);

    if(txType = 1)
    then
    insert into Debit_Card (cardNum, accountNum) values (cardNo, acctno);
    end if;

    if(txType = 2)
    then
    select max(loannum) into loanno from loan;
    if(loanno is null)
    then loanno := 0;
    end if;
    loanno := loanno + 1;
    insert into loan (principal, balance, interest, paymentdue, loannum) values (0, 0, 15, 0, loanNo);
    insert into owes (loannum, customerid) values (loanNo, customId);
    insert into credit_card (cardnum, loannum, creditlimit) values (cardNo, loanNo, creditlimit);
    end if;

    commit;
    return 1;


EXCEPTION
    when badAmount then dbms_output.put_line('Amount is bad'); rollback; return 0;
    when others then dbms_output.put_line('Fail safe'); rollback; return 0;

end;
