Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Practical 2
The accompanying README for prac 2
Last Updated: 23 August 2026

===== RUNNING THE PROGRAM ====

Conveniently, there is a Makefile to run the program, therefore we can easily run it by doing the following:
1. Navigate to terminal in the same directory as all the java files.
2. Type the following and enter, after which the program will run in terminal:
    make all
    make run
3. To clean up, type the following and enter

===== LOCKS IMPLEMENTED =====

===== FILTER LOCK =====
The threads squeeze through narrowing doorways;
at each door one thread volunteers to wait if someone shows up at the same time.
Exactly one thread gets stuck while the rest move on.
Mutual exclusion is achieved (only one thread reaches the last doorway), and it is
deadlock and starvation free. However, a thread can wait for a long time (it may be repeatedly
selected as the volunteer).

===== BAKERY LOCK =====
Works with a ticketing system where threads are served in order of their tickets.
If two threads take the same ticket, the one with the smaller thread ID goes first.
There is mutual exclusion, deadlock-freedom and starvation-freedom, since service order is followed.
Threads are serviced in a first-come-first-served order which can be considered as fair.
There are, however, caveats, such as a thread staying in the critical section for too long when
served, or the integer limit.