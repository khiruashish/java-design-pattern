// Headless checks for the animation explorer (docs/motion/index.html) and the pages that embed it.
//   cd tools/motion-check && npm install && npx playwright install chromium && node check.js
// Exits non-zero if anything is wrong. See docs/motion/README.md for what each check means.
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');
const { pathToFileURL } = require('url');

const DOCS = path.resolve(__dirname, '../../docs');
const PAGE = pathToFileURL(path.join(DOCS, 'motion/index.html')).href;
const problems = [];
const fail = (msg) => problems.push(msg);

// Every Markdown page that embeds the explorer.
function markdownFiles(dir) {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap((e) => {
    const p = path.join(dir, e.name);
    return e.isDirectory() ? markdownFiles(p) : e.name.endsWith('.md') ? [p] : [];
  });
}
function embeds() {
  const out = [];
  for (const file of markdownFiles(DOCS)) {
    const rel = path.relative(DOCS, file).split(path.sep).join('/');
    // use_directory_urls: docs/a/b.md is served at /a/b/, docs/a/index.md at /a/
    const url = rel.endsWith('index.md') ? path.posix.dirname(rel) : rel.slice(0, -3);
    const depth = url === '.' ? 0 : url.split('/').length;
    const text = fs.readFileSync(file, 'utf8');
    if (rel === 'motion/README.md') continue; // maintainer note, not published
    for (const m of text.matchAll(/<iframe[^>]*\bsrc="([^"]*motion\/index\.html[^"]*)"/g)) {
      out.push({ rel, depth, src: m[1].replace(/&amp;/g, '&') });
    }
  }
  return out;
}

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1280, height: 1000 } });
  page.on('pageerror', (e) => fail('JS error: ' + e.message));
  await page.goto(PAGE + '#saga');
  await page.waitForTimeout(300);

  // 1. Every animation, every variant, every step: nodes inside the 800×400 canvas, no overlaps,
  //    messages and tags point at real nodes, code-line maps match the steps, picker covers everything.
  const data = await page.evaluate(() => {
    const out = [];
    let variants = 0;
    const checkSteps = (tag) => {
      const p = main.p;
      for (let i = 0; i < p.steps.length; i++) {
        go(i, false);
        const rs = [...document.querySelectorAll('#dia .node rect')].map((r) => ({ id: r.parentNode.dataset.id, b: r.getBBox() }));
        for (const r of rs) if (r.b.x < 0 || r.b.y < 0 || r.b.x + r.b.width > 800 || r.b.y + r.b.height > 400) out.push(`${tag} step ${i}: node "${r.id}" is outside the canvas`);
        for (let a = 0; a < rs.length; a++) for (let c = a + 1; c < rs.length; c++) {
          const A = rs[a].b, B = rs[c].b;
          if (A.x < B.x + B.width + 6 && B.x < A.x + A.width + 6 && A.y < B.y + B.height + 6 && B.y < A.y + A.height + 6) out.push(`${tag} step ${i}: nodes "${rs[a].id}" and "${rs[c].id}" overlap`);
        }
        const s = p.steps[i];
        for (const m of (s.send || []).flatMap((x) => x.par || [x])) for (const id of m.slice(0, 2)) if (!rs.find((r) => r.id === id)) out.push(`${tag} step ${i}: message to hidden or missing node "${id}"`);
        for (const id of [...(s.on || []), ...(s.show || []), ...(s.hide || []), ...Object.keys(s.tag || {})]) if (!p.nodes.find((n) => n.id === id)) out.push(`${tag} step ${i}: unknown node "${id}"`);
        for (const id of s.pats || []) if (!byId(id)) out.push(`${tag} step ${i}: unknown pattern "${id}"`);
      }
    };
    const seen = new Set();
    for (const p of P) {
      if (seen.has(p.id)) out.push(`duplicate id "${p.id}"`);
      seen.add(p.id);
      if (!CATS[p.cat]) out.push(`${p.id}: unknown category "${p.cat}"`);
      selectPattern(p, { auto: false, push: false });
      setVariant('with', 0, false); variants++; checkSteps(p.id);
      if (NAIVE[p.id]) { setVariant('naive', 0, false); variants++; checkSteps(p.id + ' (without)'); }
      else if (p.cat !== 'tour') out.push(`${p.id}: no NAIVE ("without it") version`);
      (BREAKS[p.id] || []).forEach((b, j) => { setVariant('break', j, false); variants++; checkSteps(`${p.id} (break ${j})`); });
      const hl = HL[p.id];
      if (hl) {
        const lines = p.code.split('\n').length;
        if (hl.length !== p.steps.length) out.push(`${p.id}: HL has ${hl.length} entries for ${p.steps.length} steps`);
        hl.flat().forEach((n) => { if (n < 0 || n >= lines) out.push(`${p.id}: HL line ${n} is past the ${lines}-line code`); });
      }
    }
    for (const k of Object.keys(HL)) if (!byId(k)) out.push(`HL entry for unknown id "${k}"`);
    for (const k of Object.keys(NAIVE)) if (!byId(k)) out.push(`NAIVE entry for unknown id "${k}"`);
    for (const k of Object.keys(BREAKS)) if (!byId(k)) out.push(`BREAKS entry for unknown id "${k}"`);
    for (const k of Object.keys(PAIRS)) for (const id of k.split('~')) if (!byId(id)) out.push(`PAIRS "${k}": unknown id "${id}"`);
    const picked = [];
    const walk = (n) => n.o.forEach((o) => (o.p ? picked.push(o.p) : walk(o.n)));
    walk(PICK);
    picked.filter((id) => !byId(id)).forEach((id) => out.push(`picker points at unknown id "${id}"`));
    P.filter((p) => p.cat !== 'tour' && !picked.includes(p.id)).forEach((p) => out.push(`${p.id}: not reachable from the picker`));
    return { out, count: P.length, variants, ids: P.map((p) => p.id), cats: Object.keys(CATS), pairs: Object.keys(PAIRS) };
  });
  data.out.forEach(fail);

  // 2. Every embed in the docs: right relative path for its page, and every id it names exists.
  const list = embeds();
  for (const e of list) {
    const where = `${e.rel}: ${e.src}`;
    const want = '../'.repeat(e.depth) + 'motion/index.html';
    if (!e.src.startsWith(want + '?') && e.src !== want) fail(`${where}: path should start with "${want}"`);
    const q = new URLSearchParams(e.src.split('?')[1] || '');
    if (q.get('embed') !== '1') fail(`${where}: missing embed=1`);
    const p = q.get('p');
    if (p && !data.ids.includes(p)) fail(`${where}: unknown animation "${p}"`);
    for (const id of (q.get('set') || '').split(',').filter(Boolean)) if (!data.ids.includes(id)) fail(`${where}: unknown animation "${id}" in set`);
    for (const c of (q.get('family') || '').split(',').filter(Boolean)) if (!data.cats.includes(c)) fail(`${where}: unknown family "${c}"`);
    const cmp = q.get('compare');
    if (cmp && cmp !== 'all') for (const k of cmp.split('|')) if (!data.pairs.includes(k)) fail(`${where}: unknown comparison "${k}"`);
  }

  // 3. The special views load cleanly, at desktop and phone widths.
  const views = ['#circuit-breaker', '?compare=all', '?quiz=1', '?pick=1', '?family=oop,principles&embed=1', '?set=ap-retry-storm,ap-dual-writes&embed=1'];
  for (const width of [1280, 390]) {
    const v = await browser.newPage({ viewport: { width, height: 900 } });
    v.on('pageerror', (e) => fail(`JS error at ${width}px: ${e.message}`));
    for (const u of views) {
      await v.goto(PAGE + u);
      await v.waitForTimeout(250);
      if (await v.evaluate(() => document.documentElement.scrollWidth > document.documentElement.clientWidth)) fail(`${u} scrolls sideways at ${width}px`);
    }
    await v.close();
  }
  await browser.close();

  console.log(`${data.count} animations, ${data.variants} variants, ${list.length} embeds checked.`);
  if (problems.length) {
    console.error(`\n${problems.length} problem(s):\n- ` + problems.join('\n- '));
    process.exit(1);
  }
  console.log('All animation checks passed.');
})().catch((e) => { console.error(e); process.exit(1); });
