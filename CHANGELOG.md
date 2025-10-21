# CreateColonies

## 1.0.0

### Major Changes

- feat: Schematic Table, which can be used to convert schematics to blueprints and back ([#12](https://github.com/MotionlessTrain/CreateColonies/pull/12))

  Schematics are provided using the schematic items, destination names / paths are indicated with a scan tool.
  If the slot has no name set, the name is inferred automatically from the schematic name

  The blueprint to convert back is based on the open preview of the build tool.
  Open a preview somewhere (location does not matter at all), and insert the build tool in the table (with the arrow pointing left)
  Insert an empty schematic in the left, and you can convert from blueprint to schematic

### Patch Changes

- fix: Chain conveyors lost their connections when they got built by a builder. And they didn't require chains when they got built ([#14](https://github.com/MotionlessTrain/CreateColonies/pull/14))

## 0.1.4

### Patch Changes

- fix: when encased gears are present in a schematic, require the non-encased version of those gears instead (fixes [#8](https://github.com/MotionlessTrain/CreateColonies/issues/8)) ([#9](https://github.com/MotionlessTrain/CreateColonies/pull/9))

## 0.1.3

### Patch Changes

- chore: automate the upload to CurseForge ([`ede0cc6`](https://github.com/MotionlessTrain/CreateColonies/commit/ede0cc6d11649935982a61588f7c95f5d3e19070))

## 0.1.2

### Patch Changes

- chore: make an automatic mod release when a new release is made ([`1f2e587`](https://github.com/MotionlessTrain/CreateColonies/commit/1f2e5872fba2ca3a2dd40c91acec7dc5ed8bf739))

## 0.1.1

### Patch Changes

- chore: make an automatic mod release when a new release is made ([`c75895f`](https://github.com/MotionlessTrain/CreateColonies/commit/c75895f59b0fe13f9455555ef3ae373dee903a8a))

## 0.1.0

### Minor Changes

- feat: add integration to note requirements from Minecolonies' builder's hut onto Create's clipboard ([#3](https://github.com/MotionlessTrain/CreateColonies/pull/3))
