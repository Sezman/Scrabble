PROJECT TITLE: Scrabble 

PURPOSE OF PROJECT: To improve on the GUI based scrabble game for 1 to 4 players, as deliverable 4 for the SYSC3110 project.
                    This was done using the MVC design pattern and java GUI components outlined in the class lectures.

AUTHORS: Sebastian Ezman, Jeronimo Cumming, Simon Damato, Kaitlyn Conron

USER INSTRUCTIONS: When the program is run a pop up will appear prompting you for the number of players (1 - 4), then
                    for each players name and the game will begin. To place a tile, select it from your hand then select
                    the space on the board in which you'd like to place it. You can remove a previously placed tile (in the same turn)
                    by selecting it again from the board. For the first move it must be on the center (yellow) tile,
                    it doesn't have to begin there, as long as it passes through. When you are done placing your word,
                    click the 'Play Word' button to complete your turn. A player can skip their turn by clicking the 'Skip Turn' button.
                    The game can be restarted with the current players or a brand new game by using the 'Game' menu.
                    There is also a 'Help' button in the 'Game' menu that will display a pop up with the instructions for the game.

                    There are 3 board options: default, a heart shape, and one with many multipliers. if you would like to add
                    your own custom board follow these steps:
                        1. create a new .XML file in the 'boardLayouts' folder within 'src',
                        2. format the position of your special squares by replacing the value of 'row' and 'col' in the appropriate
                           multiplier field to the position you desire, remember the board is 0-based.
                        3. once complete, run the game, your custom board will appear under the 'Board Layouts' menu with the tile you've chosen.

                    You can save the current game as is by clicking the 'Save' option from the 'Game' menu. This will prompt you
                    for a name to save the game as. You can then reload any saved game by clicking the 'Load' menu and selecting the game you wish.
