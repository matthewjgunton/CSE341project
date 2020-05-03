create or replace function newCustomer(first in customer.first%type, middle in customer.middle%type, last in customer.last%type, dayte in char) return integer
as

customerNo customer.id%type;
intermediate date;


begin

    select max(id) into customerNo from customer;
    customerNo := customerNo + 1;

    intermediate := TO_DATE(dayte, 'yyyymmdd');

    insert into customer (dob, first, last, middle, id) values (intermediate, first, last, middle, customerNo);
    commit;
    return 1;


EXCEPTION
    when others then dbms_output.put_line('Fail safe'); rollback; return 0;

end;
