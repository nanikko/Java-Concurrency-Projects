Concurrent Banking Model
=======

The basic goal of the project is to build a banking application which allows a user to create an account and perform
basic operations like view balance, withdraw, deposit and transfer amounts to other users. Two levels of concurrency
are involved – 

1. Serving multiple clients tp log into the banking server.
2. Allowing multiple transactions to be done on the same account at same time by multiple users.

Here's the basic concurrent transaction template that we used on top of which we added exponential backing off:

````java
// we are defining a stopTime
long stopTime = System.nanoTime() + 5000;
while (true) {
	if (fromAccount.lock.tryLock()) {
		try {
			if (toAccount.lock.tryLock()) {
				try {
					if (amount > fromAccount.getCurrentAmount()) {
						throw new InsufficientAmountException(
						"Insufficient Balance");
					} else {
						fromAccount.debit(amount);
						toAccount.credit(amount);
					}
				} finally {
					toAccount.lock.unlock();
				}
			}
		} finally {
			fromAccount.lock.unlock();
		}
	}
	if (System.nanoTime() < stopTime) 29.
	return false;
	Thread.sleep(100);
} //while
````
