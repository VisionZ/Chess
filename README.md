# Chess
My newest iteration of my 3-year chess project. It comes with a AI Engine, a socket network (for LAN play), and AES Encryption. 
Note: Search Depths displayed on the small window are 1 less than they really are. They do not account for 
the initial depth, which is all the legal moves to choose from initially. 



AI Difficulty:

Novice - Searches to depth 2 (Actually depth 3).

Student - Searches to depth 3 (Actually depth 4).

Good – Searches to depth 4 (Actually depth 5). 

Strong - Attempts to search as deep as possible for a fixed time limit of 30 seconds per move

Master - Attempts to search as deep as possible for a fixed time limit of 45 seconds per move

Grand Master – Attempts to search as deep as possible for a fixed time limit of 60 seconds per move



Checking Algorithms (Those labeled with "Secure" or "NoStore") are not currently performing at their maximum speeds because there is
a bunch of "CHECK_MODE" if statements used for testing purposes. AlphaBetaBlack & AlphaBetaWhite ARE performing optimally. 



Powerful Optimizations:

1) DONE: Rather than generating new Grid objects for every 
search level in AlphaBeta/MinMax, we can just modify the Grid objects
in place. This improvement made my Chess Engine speed
increase from 20,000-30,000 positions per second to around
450,000-1,000,000 positions per second (Single Thread Search)! 
With parallel processing (Multithreaded Search) the speed is around 750,000 pps to 2,000,000 pps. 
Higher Upper Bound speeds are achieved when the move lists are small, where there
is less unnecessary read and write operations. The move lists are the only
remaining overhead (See 3rd Bullet point). These speeds were achieved on my computer, running a
dual core i7-7500U CPU.

2) DONE: With the newest version of the Evaluator, which is more powerful & accurate 
but slower, speeds are decreased somewhat. However, the search depth is often deeper
as more accuracy entails more node cutoffs. 

3) This will be messy to implement, but 
instead of allocating temporary lists of tiles
for moves, we generate the tiles (moves) at the same time
we score them in the AlphaBeta/MinMax,
which, if successful, would effectively double 
Chess Engine speed! This is true because
filling a list with tiles and then reading that list
is twice as slow as just looking at the tiles directly.

4) Using a table to store evaluation scores avoids
unnecessary calculations. However, table lookup
and saving cloned Grid objects to avoid
memory reference modification, is expensive and 
consumes a lot of memory.

5) To eliminate redundant tree searches, another table 
(more effective) could be used which stores a Grid & Search Depth as a Key
and a Score as a Value. This eliminates entire tree branches, which means 
the Engine may be able to search much farther!
