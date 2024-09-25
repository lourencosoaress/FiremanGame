# Fireman - The Game

**Fireman - The Game** is a fire-fighting simulation game where you control a fireman to extinguish fires, prevent their spread, and save the environment. The game features different terrain types with unique fire spread probabilities, and various tools such as bulldozers, firetrucks, and planes to assist in firefighting.

## Features

- **Fireman Movement**: Control the fireman with the regular directional buttons (`UP`, `DOWN`, `LEFT`, `RIGHT`).
- **Plane**: Summon a plane by pressing `P`. The plane will fly over the column with the most fires and extinguish them two by two.
- **Fire Extinguishing**: Use the fireman's water gun, firetruck, or plane to extinguish fires.
- **Firetruck**: The firetruck automatically extinguishes fire when it reaches a burning tile and the surrounding tiles.
- **Bulldozer**: The bulldozer turns tiles into non-flammable land to prevent fire spread.
- **Fuel Barrels**: Highly flammable barrels that explode after 3 turns of burning, setting nearby tiles on fire.

## Terrain Types and Fire Spread Probabilities

Different terrains have unique probabilities of catching fire:
- **Grass**: 15% chance of burning.
- **Eucalyptus**: 10% chance of burning.
- **Pine**: 5% chance of burning.
- **Abies**: 5% chance of burning.
- **Fuel Barrel**: 90% chance of burning, explodes after 3 turns of fire.

## How to Play

1. **Movement**: Control the fireman using the arrow keys to move around the grid.
2. **Extinguish Fires**: Move to burning tiles and press the appropriate key to extinguish the fire.
3. **Bulldozer and Firetruck**: Enter a bulldozer or firetruck when standing on their tile by pressing `ENTER`.
4. **Plane**: Press `P` to call the plane, which extinguishes fires in the column with the most fires.
5. **Winning Condition**: Successfully extinguish all fires on the map to move to the next level.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/fireman-the-game.git
    ```

3. Compile and run the game:
    ```bash
    javac GameEngine.java
    java GameEngine
    ```

## Project Structure

- **`GameEngine.java`**: Main game engine, handles game logic and GUI updates.
- **`gui`**: Contains classes for rendering the game elements on the GUI.
- **`observer`**: Implements the observer pattern to track user input.
- **`utils`**: Contains utility classes such as `Direction` and `Point2D`.
- **`levels`**: Level files containing the layout of each map.

## Controls

- **Arrow Keys**: Move the fireman around the grid.
- **Enter**: Enter/Exit vehicles (bulldozer or firetruck).
- **P**: Summon the plane.
- **Esc**: Quit the game.

## Future Improvements

- Adding more levels with varying difficulty.
- Introducing new game elements, like water sources or different fire behaviors.
- Adding multiplayer mode with multiple firemen working together.

Enjoy playing **Fireman - The Game**! Save the forest, protect the environment, and fight the flames!
