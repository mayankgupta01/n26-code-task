# n26-code-task
statistics aggregator of previous x secs

Build,Test and Run

Project is built using gradle, please use the following commands - Go to parent directory n26-code-task and run -

./gradlew build

./gradlew test

./gradlew bootRun

Apis

POST /transaction

Time Complexity O(1)

Request : { "amount" : "88889.5", "timestamp" :1497346761213 }

Response : 201 (transaction created), 204 (transaction older than 60 secs)

GET /statistics

Time Complexity O(1)

Response: 200 OK

{ "sum": 1000, "avg": 100, "max": 200, "min": 50, "count": 10 }

Solution Approach

1. Incoming transactions are handed off to a queue to ensure O(1) response time
2. Two daemon threads are responsible to keep the statistics updated for O(1) get /statistics. Incoming thread adds to the thread safe DataStructure sorted by timestamp, and DataStructure sorted by Amount(To find min and max). Maintainer or GarbageCollector thread cleans up the expired transactions from the DSs to keep the stats updated.
