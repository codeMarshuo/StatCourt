if (!window.API_BASE_URL) {
  window.API_BASE_URL = 'http://localhost:8081';
}
if (typeof API_BASE_URL === 'undefined') {
  var API_BASE_URL = window.API_BASE_URL;
}

const NAV_ITEMS = [
  { href: './index.html', label: '首页' },
  { href: './data.html', label: '数据' },
  { href: './teams.html', label: '球队' },
  { href: './map.html', label: '球队地图' },
  { href: './predict.html', label: '预测' },
  { href: './visualization.html', label: '可视化' },
  { href: './three3d.html', label: '3D展示' },
  { href: './chat.html', label: 'AI助手' },
  { href: './advanced.html', label: '高阶数据' },
  { href: './compare.html', label: '对比' },
  { href: './profile.html', label: '个人中心' },
  { href: './admin.html', label: '管理后台' }
];

const PUBLIC_PAGES = ['login.html', 'index.html'];

function getToken() {
  return localStorage.getItem('token');
}

function getUserInfo() {
  const userInfo = localStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo) : null;
}

function setUserInfo(user) {
  localStorage.setItem('userInfo', JSON.stringify(user));
}

function clearAuth() {
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');
}

function isLoggedIn() {
  return !!getToken();
}

function getCurrentPage() {
  const path = window.location.pathname;
  const page = path.substring(path.lastIndexOf('/') + 1);
  return page || 'index.html';
}

function isPublicPage() {
  const page = getCurrentPage();
  return PUBLIC_PAGES.includes(page);
}

async function checkAuth() {
  const token = getToken();
  if (!token) {
    return false;
  }
  
  try {
    const response = await fetch(window.API_BASE_URL + '/api/user/info', {
      headers: {
        'Authorization': token
      }
    });
    const result = await response.json();
    if (result.code === 200 && result.data) {
      setUserInfo(result.data);
      return true;
    }
    clearAuth();
    return false;
  } catch (e) {
    console.error('验证登录状态失败:', e);
    return false;
  }
}

async function requireLogin() {
  const page = getCurrentPage();
  
  if (page === 'login.html') {
    return;
  }
  
  const valid = await checkAuth();
  
  if (!valid && !isPublicPage()) {
    alert('请先登录');
    window.location.href = './login.html';
  }
}

function logout() {
  clearAuth();
  window.location.href = './login.html';
}

function createIntroAnimation() {
  if (sessionStorage.getItem('introPlayed')) return;
  
  const intro = document.createElement('div');
  intro.id = 'intro-overlay';
  intro.innerHTML = `
    <div class="intro-content">
      <div class="intro-logo">StatCourt</div>
      <div class="intro-line"></div>
    </div>
  `;
  
  const style = document.createElement('style');
  style.textContent = `
    #intro-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: #000;
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 99999;
      animation: introFadeOut 0.6s ease 1.8s forwards;
    }
    .intro-content {
      text-align: center;
    }
    .intro-logo {
      font-family: 'Inter', -apple-system, sans-serif;
      font-size: clamp(2.5rem, 8vw, 5rem);
      font-weight: 800;
      letter-spacing: -0.02em;
      background: linear-gradient(135deg, #06b6d4 0%, #8b5cf6 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      animation: introScale 1s cubic-bezier(0.16, 1, 0.3, 1) forwards;
      opacity: 0;
    }
    .intro-line {
      width: 0;
      height: 2px;
      background: linear-gradient(90deg, transparent, #06b6d4, #8b5cf6, transparent);
      margin: 20px auto 0;
      animation: introLine 0.8s ease 0.6s forwards;
    }
    @keyframes introScale {
      0% { transform: scale(0.8); opacity: 0; }
      100% { transform: scale(1); opacity: 1; }
    }
    @keyframes introLine {
      0% { width: 0; }
      100% { width: 200px; }
    }
    @keyframes introFadeOut {
      to { opacity: 0; pointer-events: none; }
    }
  `;
  document.head.appendChild(style);
  document.body.appendChild(intro);
  
  setTimeout(() => {
    intro.remove();
    sessionStorage.setItem('introPlayed', 'true');
  }, 2400);
}

function createNavStyles() {
  const style = document.createElement('style');
  style.id = 'nav-styles';
  style.textContent = `
    .nav {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
      background: rgba(10, 15, 26, 0.92);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border-bottom: 1px solid rgba(148, 163, 184, 0.1);
    }
    .nav-inner {
      max-width: 1400px;
      margin: 0 auto;
      padding: 0 24px;
      height: 56px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .brand {
      font-size: 1.1rem;
      font-weight: 700;
      color: #f0f9ff;
      text-decoration: none;
      display: flex;
      align-items: center;
      gap: 2px;
      transition: all 0.3s ease;
    }
    .brand:hover {
      text-shadow: 0 0 20px rgba(6, 182, 212, 0.5);
    }
    .brand span {
      background: linear-gradient(135deg, #06b6d4 0%, #8b5cf6 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .nav-links {
      display: flex;
      list-style: none;
      gap: 4px;
      margin: 0;
      padding: 0;
      align-items: center;
    }
    .nav-links a {
      color: #94a3b8;
      text-decoration: none;
      font-size: 0.72rem;
      font-weight: 500;
      padding: 8px 14px;
      border-radius: 8px;
      transition: all 0.25s ease;
      white-space: nowrap;
    }
    .nav-links a:hover {
      color: #f0f9ff;
      background: rgba(6, 182, 212, 0.15);
    }
    .nav-links a.active {
      color: #06b6d4;
      background: rgba(6, 182, 212, 0.2);
    }
    .user-section {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #f0f9ff;
      font-size: 0.8rem;
    }
    .user-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: linear-gradient(135deg, #06b6d4, #8b5cf6);
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: 0.75rem;
      color: white;
    }
    .logout-btn {
      background: rgba(239, 68, 68, 0.1);
      border: 1px solid rgba(239, 68, 68, 0.3);
      color: #ef4444;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 0.75rem;
      cursor: pointer;
      transition: all 0.25s ease;
    }
    .logout-btn:hover {
      background: rgba(239, 68, 68, 0.2);
    }
    .login-btn {
      background: linear-gradient(135deg, #06b6d4, #8b5cf6);
      color: white;
      padding: 8px 16px;
      border-radius: 8px;
      font-size: 0.75rem;
      font-weight: 500;
      text-decoration: none;
      transition: all 0.25s ease;
    }
    .login-btn:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
    }
    .nav-menu-btn {
      display: none;
      background: rgba(6, 182, 212, 0.1);
      border: 1px solid rgba(6, 182, 212, 0.2);
      border-radius: 8px;
      padding: 8px 16px;
      color: #06b6d4;
      cursor: pointer;
      font-size: 0.75rem;
      font-weight: 500;
      transition: all 0.25s ease;
    }
    .nav-menu-btn:hover {
      background: rgba(6, 182, 212, 0.2);
    }
    .nav-dropdown {
      display: none;
      position: absolute;
      top: 56px;
      left: 0;
      right: 0;
      background: rgba(10, 15, 26, 0.98);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border-bottom: 1px solid rgba(148, 163, 184, 0.1);
      padding: 16px 24px;
      animation: dropIn 0.2s ease;
    }
    .nav-dropdown.show { display: block; }
    .nav-dropdown-grid {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 8px;
      max-width: 700px;
      margin: 0 auto;
    }
    .nav-dropdown a {
      display: block;
      color: #94a3b8;
      text-decoration: none;
      font-size: 0.75rem;
      padding: 10px 8px;
      border-radius: 8px;
      text-align: center;
      transition: all 0.25s ease;
    }
    .nav-dropdown a:hover {
      color: #f0f9ff;
      background: rgba(6, 182, 212, 0.15);
    }
    .nav-dropdown a.active {
      color: #06b6d4;
      background: rgba(6, 182, 212, 0.2);
    }
    @keyframes dropIn {
      from { opacity: 0; transform: translateY(-8px); }
      to { opacity: 1; transform: translateY(0); }
    }
    @media (max-width: 1100px) {
      .nav-links { display: none; }
      .nav-menu-btn { display: block; }
      .user-section { display: none; }
    }
    body { padding-top: 56px; }
  `;
  
  const existingStyle = document.getElementById('nav-styles');
  if (existingStyle) {
    existingStyle.remove();
  }
  document.head.appendChild(style);
}

function createUserSection() {
  const user = getUserInfo();
  const token = getToken();
  
  if (token && user) {
    const initial = (user.nickname || user.username || 'U').charAt(0).toUpperCase();
    return `
      <div class="user-section">
        <div class="user-info">
          <div class="user-avatar">${initial}</div>
          <span>${user.nickname || user.username}</span>
        </div>
        <button class="logout-btn" id="logoutBtn">退出</button>
      </div>
    `;
  } else {
    return `
      <div class="user-section">
        <a href="./login.html" class="login-btn">登录</a>
      </div>
    `;
  }
}

function createNav() {
  const existingNav = document.getElementById('mainNav');
  if (existingNav) {
    existingNav.remove();
  }
  
  createNavStyles();
  
  const currentPage = getCurrentPage();
  
  const nav = document.createElement('nav');
  nav.className = 'nav';
  nav.id = 'mainNav';
  
  const navLinksHtml = NAV_ITEMS.map(item => {
    const isActive = item.href === './' + currentPage ? ' active' : '';
    return `<a href="${item.href}" class="${isActive}">${item.label}</a>`;
  }).join('');
  
  const dropdownLinksHtml = NAV_ITEMS.map(item => {
    const isActive = item.href === './' + currentPage ? ' active' : '';
    return `<a href="${item.href}" class="${isActive}">${item.label}</a>`;
  }).join('');
  
  const userSection = createUserSection();
  
  nav.innerHTML = `
    <div class="nav-inner">
      <a href="./index.html" class="brand">Stat<span>Court</span></a>
      <div class="nav-links">${navLinksHtml}</div>
      ${userSection}
      <button class="nav-menu-btn" id="navMenuBtn">菜单</button>
    </div>
    <div class="nav-dropdown" id="navDropdown">
      <div class="nav-dropdown-grid">${dropdownLinksHtml}</div>
    </div>
  `;
  
  document.body.insertBefore(nav, document.body.firstChild);
  
  const menuBtn = document.getElementById('navMenuBtn');
  const dropdown = document.getElementById('navDropdown');
  
  if (menuBtn && dropdown) {
    menuBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      dropdown.classList.toggle('show');
      menuBtn.textContent = dropdown.classList.contains('show') ? '收起' : '菜单';
    });
    
    document.addEventListener('click', (e) => {
      if (!dropdown.contains(e.target) && !menuBtn.contains(e.target)) {
        dropdown.classList.remove('show');
        menuBtn.textContent = '菜单';
      }
    });
  }
  
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      if (confirm('确定要退出登录吗？')) {
        logout();
      }
    });
  }
}

async function init() {
  await requireLogin();
  createIntroAnimation();
  createNav();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init);
} else {
  init();
}

window.navUtils = {
  getToken,
  getUserInfo,
  isLoggedIn,
  logout,
  checkAuth
};
