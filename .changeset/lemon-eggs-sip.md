---
"CreateColonies": patch
---

fix: The network part had a logical error in it, which meant some code that was only meant for clients got loaded on the server by accident. Fixes #27
