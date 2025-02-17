# MicroProfile Fault Tolerance

The MicroProfile Fault Tolerance specification defines strategies to deal with errors inherent in distributed microservices.

The MicroProfile Fault Tolerance specification defines the following strategies to handle errors:

* Timeout
Define the amount of time within which an execution must finish. Defining a timeout prevents waiting for an execution indefinitely.

* Retry
Define the criteria for retrying a failed execution.

* Fallback
Provide an alternative in the case of a failed execution.

* CircuitBreaker
Define the number of failed execution attempts before temporarily stopping. You can define the length of the delay before resuming execution.

* Bulkhead
Isolate failures in part of the system so that the rest of the system can still function.

* Asynchronous
Execute client request in a separate thread