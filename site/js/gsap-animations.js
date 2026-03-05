const GSAP_TRANSITION_HTML = `
<div class="pt-overlay" style="
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 99999;
  pointer-events: none;
">
  <div class="pt-layer pt-layer-1" style="
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #0a0f1a 0%, #1a1f2e 100%);
    transform: scaleX(1);
    transform-origin: left;
  "></div>
  <div class="pt-layer pt-layer-2" style="
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
    transform: scaleX(1);
    transform-origin: left;
  "></div>
  <div class="pt-layer pt-layer-3" style="
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
    transform: scaleX(1);
    transform-origin: left;
  "></div>
  <div class="pt-logo" style="
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) scale(0);
    z-index: 10;
  ">
    <svg viewBox="0 0 100 100" width="80" height="80" style="animation: pt-spin 0.8s linear infinite;">
      <circle cx="50" cy="50" r="45" fill="none" stroke="#f97316" stroke-width="3"/>
      <line x1="50" y1="5" x2="50" y2="95" stroke="#f97316" stroke-width="2"/>
      <line x1="5" y1="50" x2="95" y2="50" stroke="#f97316" stroke-width="2"/>
      <path d="M 50 5 Q 80 50 50 95" fill="none" stroke="#f97316" stroke-width="2"/>
      <path d="M 50 5 Q 20 50 50 95" fill="none" stroke="#f97316" stroke-width="2"/>
    </svg>
  </div>
</div>
`;

const GSAP_STYLES = `
@keyframes pt-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.gsap-fade-in {
  opacity: 0;
  transform: translateY(30px);
}

.gsap-slide-up {
  opacity: 0;
  transform: translateY(50px);
}

.gsap-scale-in {
  opacity: 0;
  transform: scale(0.9);
}

.gsap-stagger > * {
  opacity: 0;
  transform: translateY(20px);
}

@media (prefers-reduced-motion: reduce) {
  .gsap-fade-in, .gsap-slide-up, .gsap-scale-in, .gsap-stagger > * {
    opacity: 1 !important;
    transform: none !important;
  }
}
`;

let transitionOverlay = null;
let isAnimating = false;

function createTransitionOverlay() {
  if (transitionOverlay) return transitionOverlay;
  
  const styleEl = document.createElement('style');
  styleEl.id = 'gsap-transition-styles';
  styleEl.textContent = GSAP_STYLES;
  document.head.appendChild(styleEl);
  
  const container = document.createElement('div');
  container.id = 'page-transition-container';
  container.innerHTML = GSAP_TRANSITION_HTML;
  document.body.insertBefore(container, document.body.firstChild);
  
  transitionOverlay = container.querySelector('.pt-overlay');
  return transitionOverlay;
}

function animatePageEnter() {
  if (typeof gsap === 'undefined') {
    console.warn('GSAP not loaded');
    return;
  }
  
  const overlay = createTransitionOverlay();
  if (!overlay) return;
  
  isAnimating = true;
  
  const layer1 = overlay.querySelector('.pt-layer-1');
  const layer2 = overlay.querySelector('.pt-layer-2');
  const layer3 = overlay.querySelector('.pt-layer-3');
  const logo = overlay.querySelector('.pt-logo');
  
  gsap.set([layer1, layer2, layer3], { scaleX: 1, transformOrigin: 'right' });
  gsap.set(logo, { scale: 0 });
  
  const tl = gsap.timeline({
    onComplete: () => {
      isAnimating = false;
      overlay.style.pointerEvents = 'none';
    }
  });
  
  tl.to(layer3, {
    scaleX: 0,
    duration: 0.5,
    ease: 'power3.inOut'
  })
  .to(layer2, {
    scaleX: 0,
    duration: 0.5,
    ease: 'power3.inOut'
  }, '-=0.3')
  .to(logo, {
    scale: 1,
    duration: 0.4,
    ease: 'back.out(1.7)'
  }, '-=0.3')
  .to(logo, {
    scale: 0,
    duration: 0.3,
    ease: 'power2.in'
  }, '+=0.1')
  .to(layer1, {
    scaleX: 0,
    duration: 0.5,
    ease: 'power3.inOut'
  }, '-=0.2');
  
  return tl;
}

function animatePageExit(callback) {
  if (typeof gsap === 'undefined') {
    if (callback) callback();
    return;
  }
  
  const overlay = createTransitionOverlay();
  if (!overlay) {
    if (callback) callback();
    return;
  }
  
  isAnimating = true;
  overlay.style.pointerEvents = 'all';
  
  const layer1 = overlay.querySelector('.pt-layer-1');
  const layer2 = overlay.querySelector('.pt-layer-2');
  const layer3 = overlay.querySelector('.pt-layer-3');
  const logo = overlay.querySelector('.pt-logo');
  
  gsap.set([layer1, layer2, layer3], { scaleX: 0, transformOrigin: 'left' });
  gsap.set(logo, { scale: 0 });
  
  const tl = gsap.timeline({
    onComplete: () => {
      isAnimating = false;
      if (callback) callback();
    }
  });
  
  tl.to(layer1, {
    scaleX: 1,
    duration: 0.4,
    ease: 'power3.inOut'
  })
  .to(layer2, {
    scaleX: 1,
    duration: 0.3,
    ease: 'power3.inOut'
  }, '-=0.2')
  .to(logo, {
    scale: 1,
    duration: 0.3,
    ease: 'back.out(1.7)'
  }, '-=0.1')
  .to(layer3, {
    scaleX: 1,
    duration: 0.3,
    ease: 'power3.inOut'
  }, '-=0.2')
  .to(logo, {
    scale: 0,
    duration: 0.2,
    ease: 'power2.in'
  }, '+=0.1');
  
  return tl;
}

function initContentAnimations() {
  if (typeof gsap === 'undefined' || typeof ScrollTrigger === 'undefined') return;
  
  gsap.registerPlugin(ScrollTrigger);
  
  gsap.utils.toArray('.gsap-fade-in').forEach((el) => {
    gsap.fromTo(el, 
      { opacity: 0, y: 30 },
      {
        opacity: 1,
        y: 0,
        duration: 0.8,
        ease: 'power2.out',
        scrollTrigger: {
          trigger: el,
          start: 'top 85%',
          toggleActions: 'play none none none'
        }
      }
    );
  });
  
  gsap.utils.toArray('.gsap-slide-up').forEach((el) => {
    gsap.fromTo(el,
      { opacity: 0, y: 50 },
      {
        opacity: 1,
        y: 0,
        duration: 1,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: el,
          start: 'top 85%',
          toggleActions: 'play none none none'
        }
      }
    );
  });
  
  gsap.utils.toArray('.gsap-stagger').forEach((container) => {
    const items = container.children;
    gsap.fromTo(items,
      { opacity: 0, y: 20 },
      {
        opacity: 1,
        y: 0,
        duration: 0.6,
        stagger: 0.08,
        ease: 'power2.out',
        scrollTrigger: {
          trigger: container,
          start: 'top 85%',
          toggleActions: 'play none none none'
        }
      }
    );
  });
  
  gsap.utils.toArray('.gsap-scale-in').forEach((el) => {
    gsap.fromTo(el,
      { opacity: 0, scale: 0.9 },
      {
        opacity: 1,
        scale: 1,
        duration: 0.8,
        ease: 'back.out(1.7)',
        scrollTrigger: {
          trigger: el,
          start: 'top 85%',
          toggleActions: 'play none none none'
        }
      }
    );
  });
}

function initHeroAnimation() {
  if (typeof gsap === 'undefined') return;
  
  const heroTitle = document.querySelector('.hero-title, .carousel-title, .page-title, .teams-title');
  const heroSubtitle = document.querySelector('.hero-subtitle, .carousel-subtitle, .page-subtitle');
  
  if (heroTitle) {
    gsap.fromTo(heroTitle, 
      { opacity: 0, y: 50, scale: 0.95 },
      { opacity: 1, y: 0, scale: 1, duration: 1, delay: 0.2, ease: 'power3.out' }
    );
  }
  
  if (heroSubtitle) {
    gsap.fromTo(heroSubtitle,
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.8, delay: 0.4, ease: 'power2.out' }
    );
  }
}

function initNavAnimation() {
  if (typeof gsap === 'undefined') return;
  
  const nav = document.querySelector('.nav');
  if (nav) {
    gsap.fromTo(nav,
      { y: -80, opacity: 0 },
      { y: 0, opacity: 1, duration: 0.6, delay: 0.1, ease: 'power3.out' }
    );
  }
}

function initNavLinks() {
  document.querySelectorAll('.nav-links a, .brand').forEach((link) => {
    const href = link.getAttribute('href');
    if (!href) return;
    if (href.startsWith('#') || href.startsWith('javascript:')) return;
    if (link.hostname !== window.location.hostname) return;
    
    link.addEventListener('click', (e) => {
      if (isAnimating) return;
      e.preventDefault();
      
      animatePageExit(() => {
        window.location.href = href;
      });
    });
  });
}

function initGSAP() {
  if (typeof gsap === 'undefined') {
    console.warn('GSAP library not loaded. Animations disabled.');
    return;
  }
  
  animatePageEnter();
  
  setTimeout(() => {
    initNavAnimation();
    initHeroAnimation();
    initContentAnimations();
    initNavLinks();
  }, 100);
}

function preloadTransition() {
  createTransitionOverlay();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initGSAP);
} else {
  initGSAP();
}

window.GSAPUtils = {
  animatePageEnter,
  animatePageExit,
  initContentAnimations,
  initNavLinks,
  initHeroAnimation,
  preloadTransition
};
