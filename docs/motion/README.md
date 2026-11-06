# How the animations work

This is a maintainer note. It isn't published on the site (it's excluded in `mkdocs.yml`).

Every animation on the site comes from one self-contained file, `docs/motion/index.html`: plain JavaScript and SVG, with no build step and no dependencies. Pages show it in an `<iframe>`, and query parameters choose what it shows.

## The data, top to bottom

Everything is data near the top of the `<script>`. The engine at the bottom (`Stage`, `selectPattern`, `setVariant` and so on) rarely needs to change.

| Name | What it holds |
|---|---|
| `CATS` | The families (`creational`, `microservices`, `anti`, …) with a display name and a blurb. Each family also needs colour tokens `--c-<cat>` (accent) and `--t-<cat>` (tint) in the light `:root` block **and** in both dark blocks. |
| `P` | One object per animation (see below). The FreshCart tour (`TOUR`) is pushed onto it later. |
| `HL` | `id → [[line, …], …]`: which code lines light up at each step. There's one entry per step of the main version, and line numbers start at 0. |
| `PAIRS` | `"a~b" → "one-sentence difference"`. These are the comparisons offered by `?compare=`. |
| `NAIVE` | `id → {nodes, edges, steps}`: the "Without it" version, the one that breaks the principle, or the anti-pattern itself. It's required for every animation except the tour. |
| `BREAKS` | `id → [{label, steps}]`: the "Break it" scenarios. They reuse the main version's nodes. |
| `PICK` | The picker's decision tree: `{q, o:[{t, h?, n:{q, o:[…]}} or {t, p:"id"}]}`. Every animation (except the tour) must be reachable from it. |

### One animation

```js
{id:"ap-retry-storm", name:"Retry Storms", cat:"anti", ex:[],          // ex: source files linked under the code
 intent:"…", when:"…", analogy:"…", tip:"…",                          // the text panels
 code:`…highlighted HTML…`,                                            // <span class="k"> keywords, <span class="c"> comments; escape < > &
 nodes:[N("app","App",100,200,{kind:"client",sub:"no retry"}), …],
 edges:[["app","gw"], ["gw","svc","i"], …],
 steps:[{say:"…", on:[…], send:[…], show:[…], hide:[…], tag:{…}}, …]}
```

- **`N(id, label, x, y, opts)`** places a box centred on `(x, y)` in an 800 × 400 canvas. The options are `sub` (the small second line), `kind` (`"client"` for actors, `"iface"` for interfaces and abstract types) and `hidden: 1` (the box appears later through `show`).
- **Edges** are `[from, to, kind]`. With no kind, the edge means "uses". `"i"` means "implements" (dashed, with a hollow triangle) and `"h"` means "holds" (with a diamond).
- **Steps** play one after another:
  - `say` is the caption.
  - `on` highlights nodes.
  - `send` is a list of messages that fly one after another: `[from, to, label]`. Wrap several in `{par:[…]}` to fly them at the same time.
  - `show` and `hide` reveal or remove nodes.
  - `tag` replaces a node's `sub` text from this step on.
  - `pats` (tour only) names the patterns at work in this step.
- Code from the repository is linked through `mod`/`pkg`/`ex`, or through an explicit `repoLinks: [[label, url], …]`.

The switch labels depend on the family. Most say "With the pattern / Without it". `principles` and `dprinciples` say "Follows the principle / Breaks it", `lld` says "This design / Naive design", `oop` says "Uses the pillar / Without it", and `anti` says "The fix / The anti-pattern". Anti-patterns open on the anti-pattern.

## Adding an animation

1. Add the object to `P`, next to the others in its family. A new family needs a `CATS` entry and colour tokens.
2. Add its `HL` line map, a `NAIVE` version and a `PICK` option. Add a `PAIRS` entry if it's commonly confused with another animation.
3. Run the checker (below). It catches boxes outside the canvas or overlapping, messages sent to hidden nodes, line maps that don't match, and animations the picker can't reach.
4. Embed it on its page.

## Embedding

```html
<iframe class="motion-embed" src="../motion/index.html?p=ap-retry-storm&amp;embed=1"
        title="Animated walkthrough of …" loading="lazy"></iframe>
```

The site uses `use_directory_urls`, so `docs/a/b.md` is served at `/a/b/`. The iframe path is relative to that URL: use `../motion/` from a top-level page and `../../motion/` from a page one folder down. (Markdown links stay file-relative as usual.) The checker verifies every embed's path.

| Parameter | Shows | CSS class |
|---|---|---|
| `p=<id>` | One animation | `motion-embed` (860px) |
| `set=<id>,<id>,…` | A player with chips for exactly these animations, in this order | `motion-family` (1050px) |
| `family=<cat>,<cat>` | A player with chips for whole families | `motion-family` |
| `compare=<a~b>\|<c~d>` or `compare=all` | Side-by-side comparisons from `PAIRS` | `motion-compare` (1100px) |
| `quiz=1` | The "name that pattern" quiz | `motion-quiz` (900px) |
| `pick=1` | The pattern picker | `motion-pick` (760px) |
| `embed=1` | Always add it to embeds. It hides the explorer's chrome, follows the site's light or dark theme, resizes the iframe to fit, and starts playing when the iframe scrolls into view. | |

Opened directly (not embedded), `index.html#<id>` opens the explorer on that animation.

## The checker

`tools/motion-check/check.js` drives the explorer in headless Chromium. It runs in CI (`.github/workflows/handbook.yml`) after the site build.

```bash
cd tools/motion-check
npm ci
npx playwright install chromium
node check.js
```

It plays every step of every version (main, without, and each break) and checks:

- every box stays inside the canvas, with no overlaps;
- every message and tag points at a node that is shown;
- `HL`, `NAIVE`, `BREAKS` and `PAIRS` refer to real ids, and line maps match the steps and the code;
- every animation is reachable from the picker;
- every `<iframe>` in `docs/**/*.md` has the right relative path, includes `embed=1`, and names real ids, families and pairs;
- the explorer, compare, quiz, picker, family and set views load with no JavaScript errors and no sideways scrolling at 1280px and 390px.

It exits non-zero and lists each problem, so a broken animation fails the build.
