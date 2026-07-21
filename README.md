# Real Estate Manager

Real Estate Manager is an Android application designed for real estate agents to create, manage, search, and display property listings.

The application supports smartphones and tablets, works with local data, integrates Google Maps and Google Places services, and provides responsive layouts for portrait, landscape, and master-detail tablet configurations.

## Features

- Display all available real estate properties
- Create and edit property listings
- Add photos from the gallery or camera
- Delete property photos
- Mark a property as sold
- Display detailed property information
- Display properties on Google Maps
- Show a static map in the property detail screen
- Search and filter properties
- Filter by property type, price, surface, city, nearby points of interest, sold status, and minimum number of photos
- Convert prices between euros and dollars
- Address autocomplete with Google Places
- Automatic geocoding for manually entered addresses
- Offline support for local property data
- Cached static map images
- Responsive smartphone and tablet layouts
- Master-detail layout on tablets
- Camera position preservation after screen rotation
- Internet connectivity monitoring

## Architecture

The project follows an MVVM architecture with a separation between the data, domain, and presentation layers.

### Presentation layer

The presentation layer contains Activities, Fragments, ViewModels, view states, view actions, and RecyclerView adapters.

ViewModels expose UI states with `StateFlow` and one-time events with `SharedFlow`.
