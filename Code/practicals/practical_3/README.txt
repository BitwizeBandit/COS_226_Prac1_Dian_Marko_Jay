COS 226 – Practical 3: Test-and-Set Locks and Contention

- Dian le Roux (25147065)
- Marko de Swardt (24658562)
- Jay Macaskill (25198387)

Files

- SimpleLock.java – common interface implemented by both locks.
- TASLock.java – Task 1: baseline Test-and-Set spin lock. Calls testAndSet() on every loop iteration, regardless of the lock's state.
- TTASLock.java – Task 2: optimised Test-and-Test-and-Set lock. Spins on a plain (non-atomic) locked.get() read while the lock is held, and only attempts the atomic testAndSet() once the lock looks free. This cuts down on unnecessary atomic RMW operations and cache-coherence traffic under contention, while still using testAndSet() to actually acquire the lock.
- Main.java – Task 3: runs the contention experiment (2, 4, 8, 16, 32 threads, 5 runs per configuration) for both lock implementations, printing averaged results and raw per-run data.
- COS_226_Practical_3_Data__Completed_.xlsx – recorded results (execution time and testAndSet() invocation counts) for both locks across all thread counts.

How to build and run

make        # compiles all .java files
make run    # compiles (if needed) and runs Main
make clean  # removes compiled .class files

Summary of results

Both locks maintain correctness (actual counter always matched the expected counter across all runs). As thread count increases, TTASLock consistently performs fewer testAndSet() invocations than TASLock, demonstrating the reduction in unnecessary atomic operations:

The gap between the two implementations widens as contention increases, showing that the "test before test-and-set" optimisation becomes more valuable as more threads compete for the lock. Full per-run raw data and timing results are in the spreadsheet.