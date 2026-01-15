name: Ron Kasiel. ID:325106573.
The Pacman has 4 states: pink,green,black,white. 
Here's an explanation about the 4 states.

1.pink: The pacman wants to eat pink points.
2.green:The pacman wants to eat green points.
3.black:The pacman wants to run away from ghosts.
4.white:The pacman wants to eat the ghosts.

Here's an explanation about the algorithm about each state:

1.pink: stage 1 : Gets A Map with all the distances from the pacman.
        stage 2 : Finds the closet point to pacman that's also pink.
        stage 3 : Gets the shortest path from pacman to point.
        stage 4 : returns the direction for pacman to go according to the 2nd pixel in the path.
        
2.green: stage 1 : Gets A Map with all the distances from the pacman.
         stage 2 : Finds the closet point to pacman that's also green.
         stage 3 : Gets the shortest path from pacman to point.
         stage 4 : returns the direction for pacman to go according to the 2nd pixel in the path.

3.black: stage 1 : Gets A Map with all the distances from the ghosts.
         stage 2 : Gets the distances from all the ghost and checks for the closest ghost.
         stage 3 : Gets All the neighbors from pacman and their distances from their closest ghosts.
         stage 4 : Get the neighbor which has the biggest distances from the closest ghost.
         stage 5 : returns the direction for pacman to go according to the neighbor from stage 3.
        




not done yet
4.white: stage 1: Gets A Map with all the distances from the ghosts.
         stage 2: Finds the closet point to pacman that's also ghost.
         stage 3: Gets the shortest path from pacman to ghost.
         stage 4: returns the direction for pacman to go according to the 2nd pixel in the path.
