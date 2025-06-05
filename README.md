# CreateColonies
A compatibility project between Create and Structurize/Minecolonies

## Ideas for the future
- Compatibility between both request systems:
  - Some way to request items from Create via the postbox automatically? To let citizens craft things on Create's (stockkeeper's) demand?
  - Interoperability between the resource scroll and Create's clipboard?
- Some way to convert a Create schematic into a Structurize blueprint, or the other way around??? Is there a way that is not a command?
- Style

## Things this mod will *not* do
- Allow citizens to use trains.
  That may require programming a schedule app, to figure out the most efficient route to their destination,
  and some other sort of trickery to let citizens figure out the difference between a freight train and a passenger train
  Moreover, at this point, citizens won't wait for something during their pathfinding.
  They figure a route that is possible at this moment, not a route that would be possible if they wait a minute for a train
- Add a citizen who can craft using Create machines
  It doesn't make sense to have a citizen who only needs to move items from their hut block or rack to some sort of input chest, then move items out of an output into their hut block
  The postbox does the former of those, the stash the latter. I do have plans (if viable, that is), to automate the post box more, and let it be able to handle requests from Create's request system
  That may be enough to get a similar behaviour
- Get train signals to place nicely
  For some reason, when a train signal is being placed by the builder, even if it is after the rail has been placed, it is marking itself as invalid, and doesn't render the (>) on the rails
  I don't know what the cause is, and whether that is solvable easily
