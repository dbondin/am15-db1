do
$$
begin
	raise notice 'Прювет Волку !!!';
end;
$$;

create or replace function xfn1() returns integer as
$$
begin
	return 123;
end;
$$
language plpgsql;

create or replace function xfn2() returns void as
$$
declare
	v integer default 10;
begin
	raise notice '1] v=%', v;
	begin
		v := v + 5;
		raise notice '2] v=%', v;
	end;
	declare
		v integer default 20;
	begin
		raise notice '3] v=%', v;
	end;
	raise notice '4] v=%', v;
end;
$$
language plpgsql;

create or replace function xfn3() returns void as
$$
declare
	a integer;
	b varchar(32) default 'hello';
	c double precision := 3.14;
	d cat.id%type;
	e record;
	f cat%rowtype;
begin
	f.id=10;
	f.name='Васька';

	raise notice 'f=%', f;

	select * into e from dog where id=1;
	raise notice 'e=%', e;
end;
$$
language plpgsql;

create or replace function xfn4(integer, arg2 varchar) returns void as
$$
declare
	x integer := $1;
	arg1 alias for $1;
	y constant varchar := 'Hello';
begin
	raise notice '$1=% $2=% arg1=% x=% arg2=%', $1, $2, arg1, x, arg2;
	x := x + 1;
	arg1 := arg1 + 10;
	$2 := 'Ха-ха-ха';
	raise notice '$1=% $2=% arg1=% x=% arg2=%', $1, $2, arg1, x, arg2;
	-- NOTE: Нельзя менять константы
	-- y := 'xxx';
end;
$$
language plpgsql;

CREATE OR REPLACE FUNCTION xfn5(in x int, in y int,
       in out z int, OUT sum int, OUT prod int) AS
$$
BEGIN
	raise notice 'sum=% prod=%', sum, prod;
	sum := x + y;
	prod := x * y;
	z := z + x + y;
END;
$$ LANGUAGE plpgsql;

--do
--$$
--begin
--	raise notice '?=%', ('а' < 'ё' collate "CP1251");
--end
--$$

create or replace function xfn6(x bigint) returns bigint as
$$
begin
	if x < 2
	then
		return 1;
	end if;
	return x * xfn6(x-1);
end;
$$
language plpgsql;

create or replace function xfn6(x bigint) returns bigint as
$$
begin
	if x < 2
	then
		return 1;
	end if;
	return x * xfn6(x-1);
end;
$$
language plpgsql;

create or replace function xfn7() returns void as
$$
declare
	cc bigint;
begin
	select count(*) into cc from cat where id=100;
	if cc = 0
	then
		insert into cat(id,name,age,breed_id) values(100, 'Сотый кот', 100, null);
	end if;
end;
$$
language plpgsql;

create or replace function xfn8(integer) returns cat as
$$
declare
	c cat%rowtype;
begin
	select * into c from cat where id=$1;
	c.id=NULL;
	return c;
end;
$$
language plpgsql;

create or replace function xfn9(integer) returns cat as
$$
declare
	c record;
begin
	select * into c from cat where id=$1;
	if not found
	then
		return (null, null, 'Не существует !!!', null)::cat;
	end if;
	c.id=NULL;
	return c;
end;
$$
language plpgsql;

create or replace function xfn10(cat_id integer) returns cat as
$$
declare
	query text := 'select';
	c record;
begin
	query := query || ' * from cat where id=$1';
	execute query into c using cat_id;
	return c;
end;
$$
language plpgsql;

create or replace function xfn11(integer) returns setof cat as
$$
declare
	c record;
begin
	-- for c in select * from cat where age > $1
	-- loop
	--	return next c;
	-- end loop;
	-- return;
	return query select * from cat where age > $1;
end;
$$
language plpgsql;

do
$$
declare
	i integer := 0;
begin
	loop
		if i >= 5
		then
			exit;
		end if;
		if i % 2 = 0
		then
			i := i + 1;
			continue;
		end if;
		raise notice 'i=%', i;
		i := i + 1;
	end loop;
end;
$$;

do
$$
begin
	for i in 1..10 by 2
	loop
		raise notice 'for i=%', i;
	end loop;
end;
$$;

create or replace function xfn12(double precision, double precision)
       returns double precision as
$$
begin
	return $1 / $2;
exception
	when division_by_zero then
	     raise notice 'На ноль делить нельзя!';
	     return 0.0;
end;
$$
language plpgsql;

create or replace function xfn13(text)
       returns bigint as
$$
declare
	status boolean;
	r record;
	res bigint := 0;
	c1 cursor (_name text) for select * from cat where name ilike _name;
begin
	open c1($1);
	-- open c1 execute 'select * from cat where name ilike $1' using $1
	loop
		fetch c1 into r;
		if not found
		then
			exit;
		end if;
		res := res + r.age;
	end loop;
	close c1;
	return res;
end;
$$
language plpgsql;

create or replace function xfn15()
       returns refcursor as
$$
declare
	c1 cursor for select * from cat;
begin
	open c1;
	return c1;
end;
$$
language plpgsql;

create table if not exists
       chat_message(id serial primary key,
       		    sender text not null,
		    body text not null,
		    ctime timestamp not null,
		    mtime timestamp not null);

create table if not exists
       chat_message_log(id serial primary key,
                        chat_message_id integer,
       		    	sender text not null,
		        body text not null,
		        ts timestamp not null,
		        oper text not null);

create or replace function chat_message_insert_update_delete_trg_fn()
returns trigger
as
$$
begin
	if TG_OP = 'INSERT'
	then
		new.ctime = current_timestamp;
		new.mtime = current_timestamp;
		insert into chat_message_log(chat_message_id, sender, body, ts, oper)
		       values(new.id, new.sender, new.body, current_timestamp, TG_OP);
	elsif TG_OP = 'UPDATE'
	then
		new.ctime = old.ctime;
		new.mtime = current_timestamp;
		insert into chat_message_log(chat_message_id, sender, body, ts, oper)
		       values(new.id, new.sender, new.body, current_timestamp, TG_OP);
	else
		raise exception 'Operation % not allowed', TG_OP;
	end if;     
	return new;
end;
$$
language plpgsql;

create trigger chat_message_insert_update_delete_trg
before insert or update or delete
on chat_message
for each row
execute procedure chat_message_insert_update_delete_trg_fn();

