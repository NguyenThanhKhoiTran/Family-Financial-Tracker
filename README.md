## Getting Started

Welcome to the family finance tracker (FFT). To run this program, the user must have Maven installed. To run this program, open up the terminal and run `mvn clean install` to ensure proper compiling. Next, to run the program, run the command `mvn javafx:run`.

## Folder Structure

The workspace contains two folders by default, where:

- `src/main/java`: the folder to maintain sources
- `lib`: the folder to maintain dependencies
- `target/classes`: the folder to maintain .class files

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

To keep files local, the user may choose to include their .xlsx or .csv files in the `resources` folder.

## Dependency Management

The dependencies of the FFT include:

- `JavaFX`: to run the GUI.
- `ApachePOI`: for file handling.
- `JUnit`: for unit testing.

