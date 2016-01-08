# Assignment3
## Description
* This is the third assignment in the Udacity Android nanodegree. 
* Alexandria app is an application that scans books ISBN using a barcode scanner and fetches the book information using google API. 
* football app is an app that shows the results of football european leagues matches. It also has two different kinds of widgets that can be displayed on andorid devices.

## Features
* Alexandria has barcode scanning functionality.
* Alexandria does not crash while searching for a book without an internet connection.
* Football Scores can be displayed in a widget.
* Football Scores app has content descriptions for all buttons.
* Football Scores app supports layout mirroring.
* Alexandria’s barcode scanning functionality does not require the installation of a separate app on first use.
* Extra error cases are found, accounted for, and called out in code comments.
* Football Scores also supports a collection widget.
* Strings are all included in the strings.xml file and untranslatable strings have a translatable tag marked to false.

## The extra error cases found

### Alexandria:
1. fixed crash on BookDetail when shareActionProvider = null
2. dismissed keyboard when a book is chosen from the list.
3. set emptylistview to tell user there are not books to show.
4. fixed crash when no author.
5. removed back button

### Football:
1. fixed league names to show correct league names.
2. added emptyview to list when it no data to show or when there is not network connectivity.


