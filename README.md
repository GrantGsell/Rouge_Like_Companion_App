# Rougelike Companion App (Machine Learning Application)

Over the course of the last year streaming services have had an influx of new users and have grown drastically in popularity. Game streaming in particular saw a large growth with Twitch and YouTube gaming seeing a revenue increase of 33% and 21% respectively from 2020 to 2021.

One sub-genre of these games that caught my interest were roguelike games such as Hades and Enter the Gungeon. These games are characterized by being dungeon crawlers, with procedurally generated levels where you lose all of the objects and progress once your character loses all of their health. These types of games heavily reward player skill and have a degree of randomness about them so that no two runs are the same which adds an almost endless replayability factor.

While these games are incredibly fun to play or watch, the variance in each run allows for unique experiences but this variance can also be quite daunting. With a lot of these games there are hundreds of objects you can pick up and the stats and descriptions of these objects are left out or vague. If the player wants to know these stats or descriptions, they need to take the time to find the information online and read through it. In a game genre where fluidity, speed and quick runs are core aspects of these games, this information search process interrupts the flow of the game and takes away from the overall experience. Moreover, if a streaming is playing one of these games and needs to look up object information, the stream will be interrupted until the information is found.

After noticing this problem, I realized that with my programming background and machine learning knowledge I decided to remedy the problem. Therefore, I created an application that observes the user’s game, and whenever they pick up an object the app using a neural network based optical character recognition (OCR) algorithm to identify the name of the object. The app then obtains all the relevant stat information and displays it in a secondary screen in real time. As a proof of concept, I chose to apply this app to the game 'Enter the Gungeon', which is one of the highest rated roguelike games to come out in the past 5 years.


<img src="https://github.com/GrantGsell/Rouge_Like_Companion_App/blob/main/Companion_App_Preview_1.gif">
