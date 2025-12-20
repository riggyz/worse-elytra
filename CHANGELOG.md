# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
-

### Fixed
-

### Changed
-

### Removed
-

## [0.1.1] - 2025-12-20
### Added
- Added two custom advancements

### Fixed
- Fixed an issue where the mixin definition was incorrectly named
- Fixed an issue where unbreaking was not being applied correctly

### Changed
- Rebalanced elytra thanks to player testing
- TODO: Flight changes applied to the vanilla item
- TODO: Magic numbers have been moved into a config file

### Removed
- TODO: Cleaned up the codebase

## [0.1.0] - 2025-12-15
### Added
- Elytra state counted outside of durability with the following being derived from state
  - Air resistance
  - Durability
  - Flight distance
  - Cooldown
- Cooldowns now trigger and kick you out of flight
- Particles when the elytra degrades or kicks you out of flight
- Hotbar cooldown sprites
- Non-programmer item sprites
- Overlay sprite that damages your wings based on state

### Fixed
- Cape still rendering with elytra equipped

[unreleased]: https://github.com/riggyz/worse-elytra/compare/v0.1.0...HEAD
[0.1.1]: https://github.com/riggyz/worse-elytra/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/riggyz/worse-elytra/releases/tag/v0.1.0