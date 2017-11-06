all: odbc-testing

%.o: %.c
	gcc -g -O0 -Wall -Werror -c $<

odbc-testing: main.o
	gcc -o $@ $^ -lodbc

clean:
	rm -rf *.o odbc-testing *~
