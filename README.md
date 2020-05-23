# Moneytor

A budgeting Android application that uses Monzo's public Open Banking API to obtain user's transaction data. The user's chosen budgeting technique is then applied to that month's transactions to give the user a breakdown of their spending and a score that places them on a leader board with other users.

## Getting Started

Once you have unzipped the `Moneytor.zip` file, open the project in Android Studio and allow some time for Gradle build to finish. If an Android device is not available to run the application, an emulator must be set up (the emulator used for testing during development was `Pixel 3 API 26`). Once all that has been setup, click the `Run` button to run the application on the emulator/device.

## Prerequisites

To run the application, Android Studio and a device/emulator will be needed. To be able to use the application and bypass the authentication process, the device being used must have a Monzo bank account, the application and be logged in and when the user receives an authentication email, they must click the link on the email on the same device where Moneytor is being used.

### Budgeting

The currently available budgeting techniques are:
  - 50/30/20 \- The user's income is split into 50% on necessities like groceries and bills, 30% in discretionary transactions like eating out and 20% on savings/investment related transactions.
  - 80/20 \- Combines the necessities and discretionary transactions together and separates them from the savings budget.

### Scoring

The scoring system compares user's monthly transactions to their budget for the corresponding transaction category (budget is determined based on that month's income and the budgeting technique chosen) and computes a score for each category. The total score is then computed using different weightings for each category.

### Change timestamp for current month's transactions

Given the current Covid-19 crisis, it is expected that most people will not have any transactions made this month therefore in the Budgeting page, nothing will show up. To resolve this, you can change the code such that transactions since a different start date is considered. To do this, enter the epoch time of when you want transactions to be considered in place of the 'startOfMonth' variable in the if-statement in the 'FetchData' class. It is underneath the comment `// To manually change the time from which transactions are included, enter the epoch time value in place of 'startOfMonth' below`.
