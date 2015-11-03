An android app, that lets you make little interactive gps-based adventures for your friends.

Language: Java for Android


===== Just quickly... =====

This is a little project I made a few months ago for purely private ends (a fun birthday scavenger hunt for someone). I've now put a few days more work in to it to make it minimally usable by others for arbitrary ends. With a little more work it could be made much better. But before I invest that time, I need to know that there's interest. 



===== What are GeoAdventures? =====

GeoAdventures are like the "Choose Your Own Adventure" books you may have read as a kid. Except that instead of reading a book, you read them on your android device, and instead of turning to specific pages, you make choices by actually going to physical locations.

For instance, the story might read:

"The bank robbers scatter before you. The leader heads towards the old cemetery on 5th street, while the others make a break for the central train station. After them!"

To unlock the next part of the story you (and your GPS-enabled android device) would need to actually go to one of those two locations. Of course, which option you chose would send the story spiralling in very different directions.

GeoAdventure is basically a simple android app that takes a spreadsheet of story elements and associated images and navigates through them based on your actual GPS location. Besides interactive fiction, GeoAdventure could be used to make interactive tours of cities, scavenger hunts and probably even some sort of fancy cutting-edge poetry.

===== How do I play a GeoAdventure? =====

First, you'll need to install the GeoAdventure android app on your GPS-equipped device. Above there's a link to the current geo_adventure.apk file. If you need to know how to install .apk files, just google. Chances are good that you can just tap the link above and your android device will take care of the rest.

Then you'll need to load in an actual advenure (encased in a *.GeoAdventure.zip file). Since GeoAdventures are all set in specific locations with specific GPS coordinates, the GeoAdventure app doesn't come pre-equipped with any generic ones. You'll need to find and install a GeoAdventure that's set somewhere near you. Eventually I'll keep a list of publicly released GeoAdventures that people have created, maybe linked to a google map that'll show you the area they take place in. If there isn't already a GeoAdventure set near you, why not make one?

To install a GeoAdventure file, you'll need to get it to your device somehow (email, web link, put it on an sdcard, etc.) and then click on it. Android should pass any files that end with .GeoAdventure.zip to the GeoAdventure app which will attempt to automatically import them. For some reason, Android's default email clients seem to struggle with this simple task, so if you're emailing yourself a .GeoAdventure file you'll possibly need to access your email through a web browser and click the attachment there.

===== How do I create a GeoAdventure? ===== 

Short version: download my sample //.GeoAdventure.zip example file// (linked above) and follow the same template.

Long version:

A .GeoAventure.zip file is just a zip archive where the extension has been changed from .zip to .GeoAdventure.zip so that Android knows to pass it to the GeoAdventure app. For instance, you might call it MyAwesomeAdventure.GeoAdventure.zip; whatever you do though, don't put any dots before the .GeoAdventure part, the android doesn't like that. 

The zip archive must contain the following two files:

^   Essential files    ^^
| //meta.csv//    | a spreadsheet containing the adventure meta-data |
| //adventure.csv// | a spreadsheet containing the logic of the actual adventure| 

Additionally it can (but needn't) contain a directory called "assets" which can contain any number of images. GeoAventure files cannot contain anything other than these elements, or the app will reject them.

The simplest way to make a GeoAdventure is to download my //.GeoAdventure.zip example file// (linked above) and build off of that. Just unzip the file, open the .csv files in a spreadsheet program. Edit them to make your GeoAdventure. Resave them as csv files (your spreadsheet should do this automatically, but if you're doing it by hand: use a comma delimiter and double-quotes around any fields with newlines in them). Put your images in the assets folder. Rezip the whole thing and make sure the file name ends in .GeoAdventure.zip.

Be careful, some operating systems (I'm looking at you Windows) hide known file extensions from you. So if you rename the file FunTimes.GeoAdventure.zip, the actual name on the file system will be .GeoAdventure.zip.zip and the GeoAdventure app won't recognise it. This is easy to solve, just google for it.

=====  How do I edit the meta.csv file? ===== 

The meta.csv file is used to define various meta-data about the adventure (like its title).

The meta.csv file has two columns. One is called "variable" and the other "value". It must contain at least three rows, which correspond to these variables:

^   Example meta.csv file   ^^
^  variable  ^  value  ^
| title    | The title of your GeoAdventure |
| author   | Mr. Generic Psydonymous |
| version: | 1.0  |

Eventually, I intend for this file to customise various aspects of how the GeoAdventure app looks and feels during your adventure.

===== How do I edit the adventure.csv file? ===== 

The adventure.csv file is where you write your GeoAdventure story. Each row corresponds to one segment of the story. 

The GeoAdventure app is pretty simple. It shows you all the messages (i.e., the "message" column of all the rows) that have been "triggered". A user triggers a message by selecting the "scanner" in the app. The scanner does this:

  * It selects all the messages that have not been triggered
  * Of these, it selects just those which meet their "required" and "prohibited" criteria (see below)
  * For each of these, 
    * if a message has no "location" criterion it gets triggered.
    * if a message has a location, the scanner compares the current GPS reading to this location and triggers the message if the difference is less than "proximity" meters.


Here's a column-by-column breakdown. Some columns can be left blank and will be replaced by a default value (listed below). Columns that cannot be left blank are indicated with a * in the default column.


^   Explanation of adventure.csv file   ^^^
^  Column  ^  Default  ^  Explanation  ^
| id    |  *  | Each row must have a unique integer as an ID number. |
| title   |   *  | The title of your message. It will appear in the list of messages the use sees. | 
| message |  *  | The actual content of your message. You're free to use line breaks here, as long as you follow standard csv quoting conventions (your spreadsheet should do this automatically). |  
| image |    | The filename of an image (standard formats like jpg, png, gif, bmp and so on should be fine) that  exists in your assets directory. This image will be shown with your message.  | 
| required |    | The id number of a single row that **must** be triggered for this row to be considered for triggering.  |
| prohibited |    | The id number of a single row that **cannot** be triggered for this row to be considered for triggering.  |
| location |    | A pair of GPS coordinates in decimal form, separated by a comma (latitude then longitude; e.g., //33.446545, -111.923551//). The gps reading must be within "proximity" of this point for this row to be triggered. (hint: google "get gps coordinates") |
| proximity |  100  | How close in meters the user must be to "location" before this row is triggered. I would recommend against setting this much lower than 100 since the GPS accuracy on many devices is kind of crappy, and your players might end up standing right next to their destination but not being able to trigger the message. |
| triggered |  0  | Either 0 (= false) or 1 (= true), indicating whether this row's message should be shown to the user in the messages window. This is 0 (i.e., false) by default, but you can set it to 1 if you want to pre-trigger the messages the user will see when they first start your GeoAdventure. | 


Have fun!
