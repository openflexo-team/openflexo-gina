# openflexo-gina — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `GINATA-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### GINATA-F-1 — The gina adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-gina#1](https://github.com/openflexo-team/openflexo-gina/issues/1)

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `ConfigureGINAFIBComponent`: no template component on the model slot, or an action not assigned to a `FIBComponentModelSlot`, is an `FMLResolutionError`; a template that cannot be loaded an `FMLTechnologyError` (a printed stack, then null after a warning).

**Known limits.**
- No test: gina-ta has no test module (gina-ta is deprecated).

**After review (batch review).** No template component (a missing or renamed `.fib`) keeps the former behaviour: the actor is created,
a warning is logged and the action gives null. This action is appended to every creation scheme of an FML-controlled FIB virtual model:
an error would fail every creation. A template that cannot be loaded is still a typed error.

**Verified (2026-11-16).** ECJ compiles gina-ta (no test module).

**Revisited with FML try/catch (`CORE-F-11` phase 7, decided with the user, 2026-11-16).** No template component, or an empty one, is an
`FMLTechnologyError` that FML can catch: the instance is no longer created without user interface (the symptom of a modeler that silently
shows nothing). No FML of the workspace calls the action; it is appended by the creation of an FML-controlled FIB virtual model.
