# VideosDB
First Homework OOP - VideosDB

Implementation:
•there is a specific class for every signle-entity object (actor, user, movie, series)

•there is a specific data base for every single one of this entities(example:
UsersDB for users, ActorDB for actor)

•the entire flow of the program can be seen in Main, where I check every command
that has to be made and print its result


Design:
•The only classes that inherit attributes from another one are Movie and Series,
 which are both Videos
 
•They each have specific methods, but rely eavily on the ones found in Video too


•Every single-entity class I implemented (Actor, User, Movie, Series) gets its
data from the input list, but adds its unique features, like: number of actions
for user, rating for movie and series, number of awards for actors.

•Every single "DB"(database) class contains an array of the specified elements

•The Database is used when performing actions that require the list of all elements
of the same type. (e.g: in USerDB I have a list containing the username and its 
number of valid actions, in ActorDB I have a list with the actor name and the
number of awards he has)


How it actually works:

•I create my own Stringbuilder message, which I'll use to write to the output file

•For every new command, I reset its length and then begin to append to it the result
of the wanted command

•For every instruction, I verify the object type and the sortCriteria if necessary

•Depending on the command and its requirements, I call a method specifically made for it
(e.g: I build a rating list in which I have the ratings of every movie and show and sort
them, if the command type is BestRatedUnseen )

•If an instruction expects a certain number of elements in the output(e.g: query), my output
will contain a number of elements equal to the minimum of that number and the size of the array
that contains the elements wanted.

•I always have my arrays sorted in ascending order, so I can easily reverse their order if
needed





