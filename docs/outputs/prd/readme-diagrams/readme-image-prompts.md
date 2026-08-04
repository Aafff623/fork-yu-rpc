# README Image Prompts · Threetwoa RPC

## §0 全局规范

| 项 | 值 |
|---|---|
| 定位 | 可扩展、可观察的 Java RPC 学习与实践框架 |
| 品牌 | Threetwoa RPC（勿写 Yu RPC 作主标题） |
| 色板 | deep navy `#0f172a` · cyan `#22d3ee` · emerald `#10b981` · slate `#64748b` · paper `#f8fafc` |
| 材质 | modular grid, soft glass, volumetric light, generous negative space |
| 禁止 | watermark, fake tiny text, neon glow, purple gradients, people portraits, fake logos |

**系统指令模板（可粘贴）：**

> Generate a clean developer-tool illustration for Threetwoa RPC. Palette: deep navy, luminous cyan, emerald accents. Precise modular grid, soft glass panels, high-end but calm. No watermark, no microscopic unreadable text, no people, no brand impersonation.

## banner.png

- method: existing / img2img optional
- Prompt: Premium 3:1 GitHub README hero for “Threetwoa RPC”, metaphor of modular Java RPC pipeline, deep navy, cyan+emerald accents, glass panels, generous space, crisp developer branding.

## features.png

- reference_image: `readme-polish/references/features/infographic-modules-pack.png`
- Labels: Dynamic Proxy · Registry · Vert.x TCP · SPI · Load Balancer · Retry/Tolerant · Spring Starter
- Prompt: Seven-module infographic for Java RPC capabilities, unified icon style, deep navy cards, cyan accents, clear labels, no clutter.

## architecture.png

- style_key: bytebytego-client-server
- Labels: Consumer → Proxy → Discovery → LB → Retry → TCP Protocol → Provider → Tolerant → Result
- Prompt: Client-server architecture diagram for Threetwoa RPC call pipeline, left-to-right flow, registry and SPI as side capabilities, deep navy/cyan, no spider-web.

## tech-stack.png

- Labels: Java 8+ · Vert.x · Hessian/Kryo · jetcd · Curator · guava-retrying · Spring Boot · SPI
- Prompt: Layered tech-stack wall for RPC framework, icon rows with clear English labels, navy/cyan/emerald, distinct from architecture flowchart.

## workflow.png

- style_key: stage-decision
- Labels: Invoke → Discover → Balance → Retry? → Encode/TCP → Reflect → Decode → Tolerant?
- Prompt: Stage flowchart of RPC request lifecycle with diamond decision for retry and tolerant strategies, calm developer aesthetic.

## structure.png

- method: prefer Markdown tree in README; image is module map
- Labels: yu-rpc-core · yu-rpc-easy · starter · example-*
- Prompt: Clean repository module map for multi-module Maven RPC project, hierarchical folders, navy/cyan.

## preview-shell.png

- method: omitted — 本仓无 Preview 资产站

## showcase-01.png · 入口 / Provider-Consumer

- method: generative conceptual (not fake SaaS UI)
- Prompt: Conceptual illustration of starting example-provider then example-consumer for Threetwoa RPC, two terminal-like glass panels on deep navy desk, cyan accent, labels “Provider” and “Consumer”, no fake dashboard UI, no watermark.

## showcase-02.png · 核心调用链

- method: generative conceptual
- Prompt: Conceptual mid-flow illustration of Threetwoa RPC: Dynamic Proxy → Service Discovery → Load Balancer → TCP encode, horizontal pipeline cards, deep navy cyan emerald, clean developer tool style, no fake product screenshots.

## showcase-03.png · 结果 / 容错

- method: generative conceptual
- Prompt: Conceptual illustration of RPC response decode and tolerant strategies fail-fast/safe/over/back as four calm option chips, success and explicit error paths, deep navy cyan emerald, no watermark, no fake UI chrome.
