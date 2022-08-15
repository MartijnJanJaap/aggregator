Design decisions:
- Spring boot for the rest api due to convenience and familiarity. 
Alternative which I considered was to use no framework. 
It could be that using no framework would make the solution more simple. This is something I will only know if I try it. 
- A separate thread will be used that is always running and processing all the requests. 
- A Queue will be used to communicate data between all the "endpoint threads" and the "tasks processing thread".
- DeferredResult is used to be able to respond to the client from a different thread. 
- DeferredResult along with the request will be passed over to the queue. Which will be read by the thread that is always running.

Future improvements:
- improve the while true loop. I'm sure it can be done smarter. 
- implement performance test