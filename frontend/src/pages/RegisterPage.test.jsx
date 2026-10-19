import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import RegisterPage from './RegisterPage.jsx';

const account = { id: 1, fullName: 'Nguyễn Linh', email: 'linh@example.com', role: 'PARENT', status: 'ACTIVE' };
const reply = (status, body) => ({ status, ok: status >= 200 && status < 300, json: async () => body });

async function fillForm(user, overrides = {}) {
  const values = { fullName: 'Nguyễn Linh', email: 'linh@example.com', password: 'MatKhau123!', confirmPassword: 'MatKhau123!', ...overrides };
  for (const [field, label] of Object.entries({ fullName: 'Họ và tên', email: 'Địa chỉ email', password: 'Mật khẩu', confirmPassword: 'Nhập lại mật khẩu' })) {
    if (values[field]) await user.type(screen.getByLabelText(label, { exact: true }), values[field]);
  }
}

describe('UC-02: Đăng ký tài khoản', () => {
  it('kiểm tra trường bắt buộc và không gửi dữ liệu không hợp lệ', async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();
    render(<RegisterPage />);
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(screen.getByLabelText('Họ và tên')).toHaveAttribute('aria-invalid', 'true');
    expect(screen.getByLabelText('Họ và tên')).toHaveFocus();
    expect(screen.getByLabelText('Địa chỉ email')).toHaveAccessibleDescription('Vui lòng nhập địa chỉ email hợp lệ.');
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('chặn mật khẩu xác nhận không khớp trước khi gọi API', async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();
    render(<RegisterPage />);
    await fillForm(user, { confirmPassword: 'MatKhauKhac!' });
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(screen.getByText('Mật khẩu nhập lại chưa khớp.')).toBeInTheDocument();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it.each(['PARENT', 'TUTOR'])('gửi vai trò %s, giữ nguyên mật khẩu và xóa biểu mẫu sau thành công', async role => {
    const fetchMock = vi.fn().mockResolvedValue(reply(201, { ...account, role }));
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();
    render(<RegisterPage />);
    if (role === 'TUTOR') await user.click(screen.getByRole('radio', { name: /Gia sư/ }));
    await fillForm(user, { fullName: '  Nguyễn Linh  ', password: '  MatKhau123!  ', confirmPassword: '  MatKhau123!  ' });
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(await screen.findByRole('heading', { name: 'Chào Nguyễn Linh!' })).toBeInTheDocument();
    const [url, options] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/auth/register');
    expect(options.method).toBe('POST');
    expect(JSON.parse(options.body)).toEqual({ fullName: 'Nguyễn Linh', email: 'linh@example.com', password: '  MatKhau123!  ', role });
    expect(screen.getByText(/Bạn chưa đăng nhập/)).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản khác' }));
    expect(screen.getByLabelText('Mật khẩu', { exact: true })).toHaveValue('');
    expect(screen.getByLabelText('Nhập lại mật khẩu')).toHaveValue('');
  });

  it('hiển thị lỗi 400 tại trường tương ứng và cho phép sửa', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(reply(400, { code: 'VALIDATION_ERROR', message: 'Thông tin chưa hợp lệ.', fieldErrors: { fullName: 'Tên không được để trống.' } })));
    const user = userEvent.setup();
    render(<RegisterPage />);
    await fillForm(user);
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Thông tin chưa hợp lệ.');
    expect(screen.getByLabelText('Họ và tên')).toHaveAccessibleDescription('Tên không được để trống.');
    await user.type(screen.getByLabelText('Họ và tên'), ' A');
    expect(screen.queryByText('Tên không được để trống.')).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Tạo tài khoản' })).toBeEnabled();
  });

  it('hiển thị lỗi email đã tồn tại khi API trả 409', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(reply(409, { code: 'EMAIL_ALREADY_EXISTS', message: 'Email đã được sử dụng.', fieldErrors: {} })));
    const user = userEvent.setup();
    render(<RegisterPage />);
    await fillForm(user);
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Email đã được sử dụng.');
    expect(screen.getByLabelText('Địa chỉ email')).toHaveAttribute('aria-invalid', 'true');
  });

  it('khóa biểu mẫu trong khi chờ và không gửi lặp yêu cầu', async () => {
    let complete;
    const fetchMock = vi.fn().mockImplementation(() => new Promise(resolve => { complete = resolve; }));
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();
    render(<RegisterPage />);
    await fillForm(user);
    await user.dblClick(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(screen.getByRole('button', { name: 'Đang tạo tài khoản…' })).toBeDisabled();
    expect(screen.getByLabelText('Địa chỉ email')).toBeDisabled();
    expect(fetchMock).toHaveBeenCalledTimes(1);
    await act(async () => complete(reply(201, account)));
    expect(await screen.findByRole('heading', { name: 'Chào Nguyễn Linh!' })).toBeInTheDocument();
  });

  it('không báo thành công hoặc tạo tài khoản giả trong chế độ demo 503', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(reply(503, { code: 'REGISTRATION_UNAVAILABLE', message: 'Cần kết nối PostgreSQL để đăng ký.', fieldErrors: {} })));
    const user = userEvent.setup();
    render(<RegisterPage />);
    await fillForm(user);
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Cần kết nối PostgreSQL để đăng ký.');
    expect(screen.queryByText('ĐÃ TẠO TÀI KHOẢN')).not.toBeInTheDocument();
  });

  it('báo lỗi mạng và cho phép thử lại', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')));
    const user = userEvent.setup();
    render(<RegisterPage />);
    await fillForm(user);
    await user.click(screen.getByRole('button', { name: 'Tạo tài khoản' }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Không nhận được phản hồi từ máy chủ.');
    await waitFor(() => expect(screen.getByRole('button', { name: 'Tạo tài khoản' })).toBeEnabled());
  });
});
