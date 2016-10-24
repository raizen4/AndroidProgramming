#MyMediaPlayer
-This application acts as both an mediaplayer and as a music downloader app as it usses 2 main apis for doing so(Youtube v3 API and https://market.mashape.com/kashyap32/youtubetomp3). The music downloaded is not intended to be used for commercial use, but for my use only.<br />
-As a music player you got the very general functionalities like, creating/deleting/editing playlist, adding and deleting songs from a playlist, playing songs from outside the app( via a custom made notification which acts as exit button also) , you got the normal main screen with the music playing but also when you are not in the main screen, you got a more compact media controller( custom made) which controls the music you are playing(btw, all music controllers are sync'ed, so no worries),shufle/repeat etc... <br />
-As a music downloader app, you can search any song which can be found on youtube and download(almost) any of them straightaway by pressing download button( you can also see the song's video if you want first, to make sure that is the one you were searching for by pressing the play button)<br />
-Music can play in the background without any problem<br />
-You can filter the songs if you search for something specific( working only for songs fragment(I'll put it for playlists as well) when i have some more time.<br />
# Note that there is a little bug when the app is first opened(i let it the last as it is just a line of code). In order the app to show you the songs, after you first give it permissions required, you have to restart the app.<br />

Components used in app development:<br />
-activities and fragments( and their lifecycles)<br />
-custom made and standard adapters<br />
-custom made notifications(using remote views)<br />
-Broadcast receivers<br />
-Services(bound services)<br />
-Viewpagers and custom made toolbars.<br />
-Picasso(image loading) and OkHttp for asynchronus requests<br />
-Standard Json parser<br />
-Sql database<br />
-content providers<br />
-I am really tired now, if i find something more i ll update this post :)<br />


A few photos:<br />

![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/MyMediaPlayer/Capture%2B_2016-10-20-01-23-30.png)

![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/MyMediaPlayer/Capture%2B_2016-10-20-01-23-39.png)

![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/MyMediaPlayer/Capture%2B_2016-10-20-01-23-56.png)

![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/MyMediaPlayer/Capture%2B_2016-10-20-01-24-59.png)

![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/MyMediaPlayer/Capture%2B_2016-10-20-11-10-33.png)

![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/MyMediaPlayer/Screenshot_2016-10-20-11-11-40.png)
