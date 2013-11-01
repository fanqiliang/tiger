CFLAGS = -c -g

OBJS = linktable.o

HEADERS = linktable.h

main: students.o $(OBJS)
	gcc -o main students.o $(OBJS)

students.o: students.c $(HEADERS)
	gcc $(CFLAGS) students.c

linktable.o: linktable.c $(HEADERS)
	gcc $(CFLAGS) linktable.c

clean:
	-rm *.o main
