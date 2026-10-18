import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

function App() {
  const [attempt, setAttempt] = useState(0);
  const [connection, setConnection] = useState({ state: 'loading' });

  useEffect(() => {
    const controller = new AbortController();
    let active = true;
    setConnection({ state: 'loading' });
    const timeout = setTimeout(() => controller.abort(), 8000);
    fetch('/api/system/status', { signal: controller.signal })
      .then(async response => {
        if (!response.ok) throw new Error('API không phản hồi thành công.');
        const data = await response.json();
        if (data.status !== 'UP' || !['demo', 'mysql'].includes(data.mode)) {
          throw new Error('Phản hồi API không đúng định dạng.');
        }
        if (active) setConnection({ state: 'connected', data });
      })
      .catch(() => {
        if (active) setConnection({ state: 'error' });
      })
      .finally(() => clearTimeout(timeout));
    return () => { active = false; clearTimeout(timeout); controller.abort(); };
  }, [attempt]);

  return <div className="shell">
    <header><a className="brand" href="/">GiaSư<span>KếtNối</span></a><span className="tag">Đồ án tốt nghiệp · Bản khởi tạo</span></header>
    <main>
      <section className="intro"><p className="eyebrow">HỌC TẬP CÙNG NGƯỜI PHÙ HỢP</p><h1>Kết nối hôm nay.<br/>Tiến bộ mỗi ngày.</h1><p>Nền tảng giúp phụ huynh tìm gia sư theo môn học, lịch học và ngân sách.</p></section>
      <section className="panel" aria-labelledby="status-heading"><p className="eyebrow">MÔI TRƯỜNG PHÁT TRIỂN</p><h2 id="status-heading">Kiểm tra kết nối hệ thống</h2><div aria-live="polite">
        {connection.state === 'loading' && <p>Đang kết nối backend…</p>}
        {connection.state === 'error' && <><p className="error">Chưa kết nối được backend.</p><p>Khởi động Spring Boot ở cổng 8080 rồi kiểm tra lại.</p></>}
        {connection.state === 'connected' && <><p className="success">React đã kết nối thành công với Spring Boot.</p><p>{connection.data.mode === 'demo' ? 'Chế độ demo: chưa kết nối MySQL, chưa lưu dữ liệu.' : 'Chế độ MySQL đã bật. Phép kiểm tra này chỉ xác nhận API đang hoạt động.'}</p></>}
      </div><button disabled={connection.state === 'loading'} onClick={() => setAttempt(x => x + 1)}>Kiểm tra lại</button></section>
      <section className="roadmap" aria-label="Luồng nghiệp vụ dự kiến"><h2>Luồng chính sẽ xây dựng</h2><ol><li><strong>Tìm gia sư</strong><span>Lọc môn học, khối lớp và học phí.</span></li><li><strong>Gửi yêu cầu</strong><span>Trao đổi nhu cầu và lịch mong muốn.</span></li><li><strong>Bắt đầu lớp học</strong><span>Gia sư chấp nhận và tạo lớp.</span></li></ol><p className="note">Các chức năng nghiệp vụ chưa được triển khai ở bước khởi tạo này.</p></section>
    </main><footer>React · JavaScript · Java Spring Boot · MySQL</footer>
  </div>;
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App/></React.StrictMode>);
