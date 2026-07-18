const s=document.querySelector('#status');async function load(){if(location.protocol==='file:'){s.textContent='请使用 HTTP 服务器打开';return}s.textContent='加载中…';try{const r=await fetch('./README.md?'+Date.now());document.querySelector('#content').innerHTML=marked.parse(await r.text());s.textContent='已加载'}catch(e){s.textContent='加载失败：'+e.message}}document.querySelector('#reload').onclick=load;load();

