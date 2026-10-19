import { useEffect, useState } from 'react';

export default function SystemStatusPage() {
  const [attempt, setAttempt] = useState(0);
  const [connection, setConnection] = useState({ state: 'loading' });
  useEffect(() => {
    const controller = new AbortController();
    let active = true;
    setConnection({ state: 'loading' });
    const timeout = setTimeout(() => controller.abort(), 8000);
    fetch('/api/system/status', { signal: controller.signal })
      .then(async response => {
        if (!response.ok) throw new Error();
        const data = await response.json();
        if (data.status !== 'UP' || !['demo', 'postgres'].includes(data.mode)) throw new Error();
        if (active) setConnection({ state: 'connected', data });
      })
      .catch(() => { if (active) setConnection({ state: 'error' }); })
      .finally(() => clearTimeout(timeout));
    return () => { active = false; clearTimeout(timeout); controller.abort(); };
  }, [attempt]);
  return <section className="system-panel" aria-labelledby="system-heading">
    <p className="eyebrow">MÔI TRƯỜNG PHÁT TRIỂN</p><h1 id="system-heading">Trạng thái hệ thống</h1>
    <div aria-live="polite">
      {connection.state === 'loading' && <p>Đang kết nối máy chủ…</p>}
      {connection.state === 'error' && <><p className="error-text">Chưa kết nối được máy chủ.</p><p>Khởi động Spring Boot ở cổng 8080 rồi kiểm tra lại.</p></>}
      {connection.state === 'connected' && <><p className="success-text">React đã kết nối thành công với Spring Boot.</p><p>{connection.data.mode === 'demo' ? 'Chế độ demo: chưa kết nối PostgreSQL. Đăng ký chưa sẵn sàng và không lưu dữ liệu.' : 'Chế độ PostgreSQL đã bật. Phép kiểm tra này chỉ xác nhận API đang hoạt động.'}</p></>}
    </div>
    <button type="button" className="primary-button" disabled={connection.state === 'loading'} onClick={() => setAttempt(value => value + 1)}>Kiểm tra lại</button>
  </section>;
}
