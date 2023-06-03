## Requirements
- [x] Data must be fetched from the open exchange rates service.
  - [x] You must use a free account - not a paid one.
- [x] Data must be persisted locally to permit the application to be used
offline after data has been fetched.
- [x] In order to limit bandwidth usage, the required data can be refreshed from the API no more frequently than once every 30 minutes.
- [x] The user must be able to select a currency from a list of currencies provided by open exchange rates.
- [x] The user must be able to enter the desired amount for the selected currency.
- [x] The user must then be shown a list showing the desired amount in the selected currency converted into amounts in each currency provided by open exchange rates.
  - [x] If exchange rates for the selected currency are not available via open exchange rates, perform the conversions on the app side.
- [X] The project must contain unit tests that ensure correct operation.

## About this project
- 100% kotlin
- Jetpack
- Android Architecture Components
  - ViewModel
  - LiveData
- Manual dependency injection
- DB
  - Room
- Network
  - Retrofit
- Mock
  - Mockk

## Project structure
- Use MVVM and clean architecture to build for
  - Separation of Concerns
    - UI logic resides in the View and ViewModel, while the business logic and data access are handled by Repository & Use Case. This separation makes the codebase more maintainable, testable, and easier to understand.
  - Testability
    - Easily tested by mocking dependencies and verifying its behavior.
  - Reusability
    - This modular approach allows for easier code reuse, as different components can be plugged into multiple projects without tightly coupling them to the specific implementation.
  - Scalability
    - The separation of layers and dependencies allows for easier addition or modification of features without affecting the entire codebase.

  ![image info](./ClassDiagram.png)

## How to build
- Use `./gradlew installDebug` to install App to connected device/emulator
- Use `./gradlew assembleDebug` to build debug apk file

## Test
### Unit test
Use `./gradlew test` to run unit tests, due to the time limitation, only focus on core business rules in following packages
- repository
  - ApiLayerRepositoryTest
- usecase
  - GetCurrenciesUseCaseTest
  - GetRatesUseCaseTest
- viewmodel
  - MainViewModelTest

### Instrumented test
Use `./gradlew connectedAndroidTest` to run unit tests, due to the time limitation, only test Room's write and read
- CurrencyDaoTest
- RateDaoTest

## TODOs
- Unit test for data source classes
- More instrumental tests
- Use DI tools, e.g., Hilt
