/* preview-readme.js · 拉取 README.md 并渲染；禁止 file:// 直开 */
const cfg = window.__PREVIEW_README__ || {};
const DEFAULT_PORT = cfg.port || 8090;
const README_URL = './README.md';
const STATUS = document.getElementById('status');
const META = document.getElementById('meta');
const TARGET = document.getElementById('readme');
const FOOTER = document.getElementById('footer-text');
if (FOOTER && cfg.footer) { FOOTER.textContent = cfg.footer; }
function setStatus(text, state) { STATUS.textContent = text; if (state) STATUS.dataset.state = state; }
function setMeta(text) { META.textContent = text; }
function renderError(message, detail) {
  TARGET.innerHTML = '';
  const wrap = document.createElement('div'); wrap.className = 'error-box';
  const h = document.createElement('h2'); h.textContent = '预览失败'; wrap.appendChild(h);
  const p = document.createElement('p'); p.textContent = message; wrap.appendChild(p);
  if (detail) { const pre = document.createElement('pre'); pre.textContent = detail; wrap.appendChild(pre); }
  const hint = document.createElement('p');
  hint.innerHTML = ['常见原因：','<ul>','<li>端口不是 ', DEFAULT_PORT, '：请在仓库根执行 <code>python -m http.server ', DEFAULT_PORT, '</code></li>','<li>直接 <code>file://</code> 打开会导致 fetch 失败</li>','</ul>'].join('');
  wrap.appendChild(hint); TARGET.appendChild(wrap);
}
function waitForMarked(timeoutMs) {
  const deadline = Date.now() + (timeoutMs || 8000);
  return new Promise((resolve, reject) => {
    (function poll() {
      if (window.marked && typeof window.marked.parse === 'function') { resolve(window.marked); return; }
      if (Date.now() >= deadline) { reject(new Error('marked.js 未加载')); return; }
      setTimeout(poll, 50);
    })();
  });
}
async function loadReadme() {
  setStatus('加载中…', 'loading'); setMeta('README.md · 加载中');
  try {
    const markedLib = await waitForMarked(8000);
    const resp = await fetch(README_URL, { cache: 'no-cache' });
    if (!resp.ok) throw new Error(`HTTP ${resp.status} ${resp.statusText}`);
    const md = await resp.text();
    TARGET.innerHTML = markedLib.parse(md, { mangle: false, headerIds: true });
    setStatus('已加载', 'ok');
    const ts = new Date();
    setMeta(`README.md · ${ts.toLocaleString()}`);
  } catch (err) {
    setStatus('加载失败', 'error');
    renderError(cfg.hint || '无法读取 README.md', String(err?.message ?? err));
  }
}
document.getElementById('reload-btn').addEventListener('click', loadReadme);
loadReadme();