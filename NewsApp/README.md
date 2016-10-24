# NewsAPP
<pr>-This application acts as an RSS feeder. In the first screen you are prompted to make a query so the application
will give you the most important feeds about what you searched(there are 10 results by default). The result will be shown each with name and photo(Attention!! the photos will lazy load,so until there is one available( I had to parse the html and find one as the default api doesn't give any thumbnail), a placeholder will be shown)</pr>
-The app usses Google Ajax API for the feeds.
-The app alows saving the articles or blogs or whatever the result is in a local database as well as it lets you share the result on other apps.
-you can read what you saved if you have internet.
-IT REQUIRES INTERNET!
-A few photos of it(excuse the poor design, I wanted to focus primarly on the functionalities)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-15-48.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-15-59.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-16-07.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-16-32.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-16-49.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-16-52.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-16-56.png)
![Problem loading](https://github.com/raizen4/AndroidProgramming/blob/master/NewsApp/Screenshot_2016-10-20-11-17-03.png)

What components were used to make this app:
-Activities
-Standard HTML parser
-Standard Json parser
-the connection with the api was made manually(i haven't used OkHttp)
-Picasso
-callbacks
-custom made and standard adapters
-SQL database
-Custom made alert dialogs
