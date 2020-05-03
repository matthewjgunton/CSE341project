create or replace function readTime(chronos in number) return date
as


begin

  return to_date('1970-01-01', 'YYYY-MM-DD') + numtodsinterval(chronos,'SECOND');


EXCEPTION
    when others then dbms_output.put_line('Fail safe'); return to_date('1970-01-01', 'YYYY-MM-DD');

end;
