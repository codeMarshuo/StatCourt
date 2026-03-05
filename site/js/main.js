// Typing effect
const phrases = ['赛后分析','实时热力图','战术回放'];
const typedEl = document.getElementById('typed');
let pi=0, ci=0, deleting=false;
function tick(){
  const full = phrases[pi];
  if(!deleting){
    typedEl.textContent = full.slice(0, ci+1);
    ci++;
    if(ci===full.length){deleting=true;setTimeout(tick,1200);return}
  } else {
    typedEl.textContent = full.slice(0, ci-1);
    ci--;
    if(ci===0){deleting=false;pi=(pi+1)%phrases.length}
  }
  setTimeout(tick, deleting?80:120);
}
setTimeout(tick,600);

// Smooth scroll for nav
document.querySelectorAll('.nav-links a, .hero-cta a').forEach(a=>{
  a.addEventListener('click', e=>{
    const href = a.getAttribute('href');
    if(href && href.startsWith('#')){
      e.preventDefault();
      document.querySelector(href)?.scrollIntoView({behavior:'smooth',block:'start'});
    }
  })
});

// Mobile nav toggle
const navToggle = document.querySelector('.nav-toggle');
const navLinks = document.querySelector('.nav-links');
navToggle?.addEventListener('click', ()=>{
  navLinks.style.display = navLinks.style.display === 'flex' ? 'none' : 'flex';
});

// Reveal on scroll & counters
const observer = new IntersectionObserver((entries)=>{
  entries.forEach(entry=>{
    if(entry.isIntersecting){
      entry.target.classList.add('active');
      observer.unobserve(entry.target);
      // if it has counters inside
      entry.target.querySelectorAll('.num').forEach(el=>animateCounter(el));
    }
  })
},{threshold:0.18});

document.querySelectorAll('.reveal').forEach(el=>observer.observe(el));

function animateCounter(el){
  const target = +el.dataset.target || 0;
  const duration = 1600;
  let start=0; const step = Math.ceil(target/(duration/30));
  const t = setInterval(()=>{
    start += step; if(start>=target){el.textContent = target;clearInterval(t)} else el.textContent = start;
  },30);
}

// Accessibility: allow keyboard nav toggle
navToggle?.addEventListener('keyup', (e)=>{ if(e.key==='Enter') navToggle.click(); });
