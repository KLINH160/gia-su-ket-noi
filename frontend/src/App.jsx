import { useEffect, useState } from 'react';
import RegisterPage from './pages/RegisterPage.jsx';
import SystemStatusPage from './pages/SystemStatusPage.jsx';

export default function App() {
  const [route, setRoute] = useState(window.location.hash);
  useEffect(() => {
    const onRouteChange = () => {
      if (['#/dang-ky', '#/he-thong'].includes(window.location.hash)) setRoute(window.location.hash);
    };
    window.addEventListener('hashchange', onRouteChange);
    return () => window.removeEventListener('hashchange', onRouteChange);
  }, []);
  const isSystem = route === '#/he-thong';
  return <div className="shell">
    <a className="skip-link" href="#noi-dung">Đến nội dung chính</a>
    <header className="site-header">
      <a className="brand" href="#/dang-ky" aria-label="Gia Sư Kết Nối — trang đăng ký">
        <span className="brand-icon" aria-hidden="true">g<span>.</span></span>
        <span>GiaSư<span className="brand-accent">KếtNối</span><small>CÙNG NHAU TIẾN BỘ</small></span>
      </a>
      <span className="header-note"><span className="status-dot" /> Khởi đầu hành trình học tập</span>
    </header>
    <main id="noi-dung" tabIndex={-1}>{isSystem ? <SystemStatusPage /> : <RegisterPage />}</main>
    <footer className="site-footer"><span>Gia Sư Kết Nối · Đồ án tốt nghiệp</span><a href={isSystem ? '#/dang-ky' : '#/he-thong'}>{isSystem ? 'Về trang đăng ký' : 'Trạng thái hệ thống'}</a></footer>
  </div>;
}
