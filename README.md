# Safraa
Safraa is a product that provides travel bus tickets booking service. It’s on a mission to enable digital transformation in the Sudanese bus travel sector by making bus travel easy, safe, and affordable.

The product consists of two integrated applications, namely:
- **Safraa Passenger**, where travelers browse, book, and pay for bus tickets
- **Safraa Bus**, where bus company agents manage trips, review bookings, and confirm payments.

# Safraa Passenger
<img src="https://github.com/Ahmedgadein/Safraa-Passenger/blob/master/screenshots/1.jpg" width="200"> <img src="https://github.com/Ahmedgadein/Safraa-Passenger/blob/master/screenshots/2.jpg" width="200"> <img src="https://github.com/Ahmedgadein/Safraa-Passenger/blob/master/screenshots/3.jpg" width="200"> <img src="https://github.com/Ahmedgadein/Safraa-Passenger/blob/master/screenshots/4.jpg" width="200">

## App Features

- **Find Trips**: Browse and filter trips of different destinations and dates.
- **Book a Seat**: Book and reserve your preferable seat(s).
- **Get Tickets**: Pay to confirm reservation and receive your e-ticket.

## App architecture

The app adopts a MVVM (Model-View-Viewmodel) clean architecture with unidirectional data flow. Model layer consists of data repositories and use-cases when needed. 
Viewmodels are utlilized per app screen. Presentation (View) layer incorporates a single Activity scheme with multiple fragment management using navigational components.

## Components, Libraries, and Tools:
- Android Navigation.
- Andriod Workmanager.
- Room Database.
- Kotlin Coroutines.
- Hilt Dependency Injection.
- Firebase (Authentication, Firestore, Messaging).
- Facebook Shimmer.
- GSON.
- Timber.
- Mixpanel.
