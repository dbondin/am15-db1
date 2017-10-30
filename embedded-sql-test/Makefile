all: embedded-sql-test

%.c: %.ec
	ecpg $<

%.o: %.c
	gcc -g -O0 -Wall -Werror -c -I/usr/include/postgresql $<

embedded-sql-test: embedded-sql-test.o main.o
	gcc -o $@ $^ -lecpg

clean:
	rm -rf *.o embedded-sql-test *~ embedded-sql-test.c
