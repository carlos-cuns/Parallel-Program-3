# Running
To compile and run the code, use:
```
javac Program3.java
java Program3
```
## Problem 1
The way the servants were processing the gifts was not thread-safe! 
I decided that using a thread-safe data structure to represent the bag and then a concurrent linked list to represent the chain would solve the Minotaur's issues.
### Correctness
To determine the correctness of the program, I made sure that:
- each present added was unique
- the number of thank you letters is equal to the number of gifts received
- the presents are stored on the chain (concurrent linked list) after being removed from the bag

This was done by displaying smaller data sets to assure uniqueness and ordering and then using counters for larger data sets
### Efficiency
To have an efficient approach, I made sure that my thread-safe data structure had quick removal operations to simulate removing from the top of a bag.
I decided that a concurrent queue structure with a poll function was the best choice.
I also used a parallel integer stream to generate the 500K items in a quick manner.
### Progress
The progress guarantee here is related to the concurrent data structures used for the bag and chain, as well as the synchronized operations and volatile counters. 
The threads have a predetermined termination and all possible tasks have some sort of end condition.
The concurrent linked list uses a course-grained approach, which allows for guaranteed implementation of need functions. 
Though this has some bottlenecking, it guarantees that each function is thread-safe without complicated logic. (Fine-grained locks are an approach to try in the future)
## Problem 2
The Mars Rover uses its eight sensors and the atmospheric temperature module to create a report at the end of the hour containing:
- the greatest difference between temperatures in a 10-minute interval
- hottest temperatures
- coldest temperatures
### Correctness
To determine correctness, I used smaller data sets to first make sure that:
- the temperatures recorded were valid
- Each thread produced a temperature per minute
- The intervals for each range were calculated by a clear helper function (indices were made from the order placed into the queue)
### Efficiency
As mentioned in the correctness section, using a queue allowed me to keep track of the order placed (scanned) to get the intervals.
Then, it was a matter of using the sort algorithm from Java and taking the five first and five last elements. I believe this is the most straightforward approach to gathering the information required for the report.
### Progress
The LinkedBlockingQueue used in this problem assures that there is no deadlock related to the manipulation of the shared memory. 
Since all the threads run in a for loop (60 iterations to simulate minutes), they have a predetermined termination.
These features guarantee progress in the program.
# Experimentation
In both problems, the experimentation followed this procedure:
1. create a set of threads that will execute a runnable
2. create a runnable that has a termination condition
3. determine a thread-safe data structure that follows guidelines
4. create problem-specific functions that are thread-safe
5. display an output to confirm small data sets manually
6. create a display that can be used for large data sets
7. confirm correctness
