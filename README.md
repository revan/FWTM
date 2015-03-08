#First We Take Manhattan: Territory Capture
##Hack NY Spring 2015
This is a weird little game where two teams of players, equipped with an Android phone in hand and a unique QR tag taped to their backs, compete for control of a selection of New York City blocks.

![](https://raw.githubusercontent.com/revan/FWTM/master/1.png)
![](https://raw.githubusercontent.com/revan/FWTM/master/2.png)

The blocks are selected from the [NYC Open Data 2010 Census](https://data.cityofnewyork.us/City-Government/2010-Census-Blocks/v2h8-6mxf), which provides detailed boundaries of every block.

Territory passes from one team to the other as a function of how many players of each team is present in a territory, over time.

If a player scans an opposing player's QR tag, the scanee is eliminated from play for an hour, to add a hunting element.

##Requirements
The Python server requires `pip install flask fastkml shapely`, for the server, map parsing, and geometry calculations.

The Android application requires `Google Play Services` and `QR Droid` to be installed.

##FAQ
###What's to stop people from not wearing the QR tags?
An... outstanding moral compass?

###That's a terrible name.
Named after [an old song by Leonard Cohen](https://www.youtube.com/watch?v=JTTC_fD598A). I realize that probably doesn't help my case.
