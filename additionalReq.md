Requirements
Extend the system to support multiple space allocation strategies:
* Lowest numbered free space (default).
* Highest numbered free space.
* Random free space.

The strategy must be selectable at car park creation time.

You may assume:
* Given the same set of free spaces, a strategy must either return a valid free space or indicate that none are available.
* A strategy must not maintain its own copy of parking state.




Requirements
Extend the system so that:
* Multiple vehicles may attempt to enter and exit the car park concurrently.
* The system must guarantee:
    * A parking space is never assigned to more than one vehicle.
    * A parking space is never freed more than once.
    * Entry and exit operations are thread-safe.
    * The system remains correct under high contention.
* The act of checking for a free space and assigning it must be atomic.

You may assume:
* Parking requests originate from multiple threads.
* Entry and exit requests may interleave arbitrarily.
* Scenarios where many threads attempt to park when only one space remains.